package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Finance_payment_method;
import com.xionce.doctorvetServices.data.Finance_payment_methodsAdapter;
import com.xionce.doctorvetServices.data.Spending;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditSpendingActivity extends EditBaseActivity {

    private static final String TAG = "EditSellPaymentActivity";
    private TextInputLayout txtAmount;
    private Spinner cmb_payment_methods;
    private TextInputLayout txt_reason;
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;

    private Finance_payment_methodsAdapter finance_payment_methodsAdapter;
    private Spending spending = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_spending);
        toolbar_title.setText("Gasto");
        toolbar_subtitle.setText("Ingresa el monto");
        cmb_payment_methods = findViewById(R.id.cmb_payment_methods);
        txtAmount = findViewById(R.id.txt_payment_amount);
        txt_reason = findViewById(R.id.txt_reason);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getFinanceTypesPayments(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setFinanceTypesPaymentsAdapter(resultList);
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Spending spending = getObject();
        setObjectToUI(spending);

        ImageView dateSearch = findViewById(R.id.img_search_date);
        dateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });

        ImageView timeSearch = findViewById(R.id.img_search_time);
        timeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtHour.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });

        Button btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

        txt_reason.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onAllRequestsCompleted() {
        if (isUpdate()) return;
        DoctorVetApp.get().showKeyboard();
        txtAmount.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.SPENDING_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.SPENDING_OBJ.name());
        spending = MySqlGson.getGson().fromJson(objectInString, Spending.class);

        String financeInString = DoctorVetApp.get().readFromDisk("finance_types_payments");
        ArrayList<Finance_payment_method> payment_methods = MySqlGson.getGson().fromJson(financeInString, new TypeToken<List<Finance_payment_method>>() {}.getType());
        setFinanceTypesPaymentsAdapter(payment_methods);
    }

    @Override
    protected void save() {
        if (!validateAmount())
            return;

        showWaitDialog();

        Spending spending = getObjectFromUI();
        String sell_payment_json_object = MySqlGson.getGson().toJson(spending);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success"))
                        finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                hideWaitDialog();
            }
        })
        {
            @Override
            public byte[] getBody() {
                return sell_payment_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildSpendingsUrl();
    }

    @Override
    protected Spending getObjectFromUI() {
        Spending spending = getObject();

        if (!txtAmount.getEditText().getText().toString().isEmpty()) {
            BigDecimal total = new BigDecimal(txtAmount.getEditText().getText().toString());
            spending.setAmount(total);
        }

        spending.setReason(txt_reason.getEditText().getText().toString());

        spending.setFinance_payment_method((Finance_payment_method)cmb_payment_methods.getSelectedItem());

        //date
        String datetime = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        spending.setDate(HelperClass.getShortDateTime(datetime, getBaseContext()));

        return spending;
    }

    @Override
    protected Spending getObject() {
        if (spending == null) {
            spending = new Spending();
            spending.setDate(Calendar.getInstance().getTime());
            spending.setFinance_payment_method(new Finance_payment_method(1, "Efectivo"));
        }

        return spending;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Spending spending = (Spending) object;

        if (spending.getAmount() != null)
            txtAmount.getEditText().setText(spending.getAmount().toString());

        txt_reason.getEditText().setText(spending.getReason());

        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(spending.getDate()));
        txtHour.getEditText().setText(HelperClass.getTimeInLocale(spending.getDate(), getBaseContext()));

    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setFinanceTypesPaymentsAdapter(ArrayList resultList) {
        finance_payment_methodsAdapter = new Finance_payment_methodsAdapter(resultList, Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.NONE);
        cmb_payment_methods.setAdapter(finance_payment_methodsAdapter.getArrayAdapter(EditSpendingActivity.this));
        cmb_payment_methods.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                getObject().setFinance_payment_method((Finance_payment_method) adapterView.getItemAtPosition(pos));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                getObject().setFinance_payment_method(null);
            }
        });
        cmb_payment_methods.setSelection(finance_payment_methodsAdapter.getItemIndex(getObject().getFinance_payment_method()));
    }

    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }
    private void searchTime() {
        DoctorVetApp.get().searchTimeSetInit(txtHour, getSupportFragmentManager());
    }
    private boolean validateAmount() {
        return HelperClass.validateNumberNotZero(txtAmount, false);
    }

}
