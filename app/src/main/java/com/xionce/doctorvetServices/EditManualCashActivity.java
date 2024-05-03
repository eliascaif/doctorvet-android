package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.xionce.doctorvetServices.data.Cash_movement;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditManualCashActivity extends EditBaseActivity {

    private static final String TAG = "EditManualCashActivity";
    private TextInputLayout txtAmount;
    private Spinner cmb_movement_type;
    private Spinner cmb_payment_methods;
    private TextInputLayout txt_reason;
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;

    private Finance_payment_methodsAdapter finance_payment_methodsAdapter;
    private Cash_movement cash_movement = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_manual_cash);
        toolbar_title.setText("Ingreso / egreso manual");
        toolbar_subtitle.setText("Ingresa el monto");
        cmb_movement_type = findViewById(R.id.cmb_movement_type);
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

        ArrayList<String> items = new ArrayList<>();
        items.add("Egreso");
        items.add("Ingreso");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(EditManualCashActivity.this, android.R.layout.simple_dropdown_item_1line, items);
        cmb_movement_type.setAdapter(arrayAdapter);

        Cash_movement cash_movement = getObject();
        setObjectToUI(cash_movement);

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
        outState.putString("cash_movement", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("cash_movement");
        cash_movement = MySqlGson.getGson().fromJson(objectInString, Cash_movement.class);

        String financeInString = DoctorVetApp.get().readFromDisk("finance_types_payments");
        ArrayList<Finance_payment_method> payment_methods = MySqlGson.getGson().fromJson(financeInString, new TypeToken<List<Finance_payment_method>>() {}.getType());
        setFinanceTypesPaymentsAdapter(payment_methods);
    }

    @Override
    protected void save() {
        if (!validateAmount())
            return;

        showWaitDialog();

        Cash_movement manualCash = getObjectFromUI();
        String manual_cash_json_object = MySqlGson.postGson().toJson(manualCash);
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
                return manual_cash_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildManualCashUrl();
    }

    @Override
    protected Cash_movement getObjectFromUI() {
        Cash_movement manualCash = getObject();

        if (!txtAmount.getEditText().getText().toString().isEmpty()) {
            BigDecimal total = new BigDecimal(txtAmount.getEditText().getText().toString());
            manualCash.setAmount(total);
        }

        if (cmb_movement_type.getSelectedItem().toString().equalsIgnoreCase("ingreso"))
            manualCash.setType(Cash_movement.manual_cash_type.MANUAL_CASH_IN);
        else
            manualCash.setType(Cash_movement.manual_cash_type.MANUAL_CASH_OUT);

        manualCash.setReason(txt_reason.getEditText().getText().toString());

        manualCash.setFinance_payment_method((Finance_payment_method)cmb_payment_methods.getSelectedItem());

        //date
        String datetime = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        manualCash.setDate(HelperClass.getShortDateTime(datetime, getBaseContext()));

        return manualCash;
    }

    @Override
    protected Cash_movement getObject() {
        if (cash_movement == null) {
            cash_movement = new Cash_movement();
            cash_movement.setDate(Calendar.getInstance().getTime());
            cash_movement.setFinance_payment_method(new Finance_payment_method(1, "Efectivo"));
        }

        return cash_movement;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Cash_movement cash_movement = (Cash_movement) object;

        if (cash_movement.getAmount() != null)
            txtAmount.getEditText().setText(cash_movement.getAmount().toString());

        txt_reason.getEditText().setText(cash_movement.getReason());

        cmb_movement_type.setSelection(1);
        if (cash_movement.getType() == Cash_movement.manual_cash_type.MANUAL_CASH_OUT)
            cmb_movement_type.setSelection(0);

        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(cash_movement.getDate()));
        txtHour.getEditText().setText(HelperClass.getTimeInLocale(cash_movement.getDate(), getBaseContext()));
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
        cmb_payment_methods.setAdapter(finance_payment_methodsAdapter.getArrayAdapter(EditManualCashActivity.this));
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
