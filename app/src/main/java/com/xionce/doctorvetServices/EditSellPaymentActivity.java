package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Finance_payment_method;
import com.xionce.doctorvetServices.data.Finance_payment_methodsAdapter;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Sell_payment;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditSellPaymentActivity extends EditBaseActivity {

    private static final String TAG = "EditSellPaymentActivity";
    private TextInputLayout txt_payment_amount;
    private Spinner cmb_payment_methods;

    private Finance_payment_methodsAdapter finance_payment_methodsAdapter;
    private Sell_payment sellPayment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sell_payment);
        toolbar_title.setText("Pago");
        toolbar_subtitle.setText("Ingresa el monto");
        cmb_payment_methods = findViewById(R.id.cmb_payment_methods);
        txt_payment_amount = findViewById(R.id.txt_payment_amount);
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

        Sell_payment sellPayment = getObject();
        setObjectToUI(sellPayment);
        
        Button btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });

    }

    @Override
    protected void onAllRequestsCompleted() {
        if (isUpdate()) return;
        DoctorVetApp.get().showKeyboard();
        txt_payment_amount.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.SELL_PAYMENT_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.SELL_PAYMENT_OBJ.name());
        sellPayment = MySqlGson.getGson().fromJson(objectInString, Sell_payment.class);

        String financeInString = DoctorVetApp.get().readFromDisk("finance_types_payments");
        ArrayList<Finance_payment_method> payment_methods = MySqlGson.getGson().fromJson(financeInString, new TypeToken<List<Finance_payment_method>>() {}.getType());
        setFinanceTypesPaymentsAdapter(payment_methods);
    }

    @Override
    protected void save() {
        if (!validateAmount())
            return;

        showWaitDialog();

        Sell_payment sell_payment = getObjectFromUI();
        String sell_payment_json_object = MySqlGson.getGson().toJson(sell_payment);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success")) {
                        finish();
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditSellPaymentActivity.this), "No se puede registrar el pago", Snackbar.LENGTH_LONG).show();
                    }
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

    private boolean validateAmount() {
        return HelperClass.validateNumberNotZero(txt_payment_amount, false);
    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildSellsPaymentsUrl();
    }

    @Override
    protected Sell_payment getObjectFromUI() {
        Sell_payment sellPayment = getObject();

        if (!txt_payment_amount.getEditText().getText().toString().isEmpty()) {
            BigDecimal total = new BigDecimal(txt_payment_amount.getEditText().getText().toString());
            sellPayment.setAmount(total);
        }

        sellPayment.setFinance_payment_method((Finance_payment_method)cmb_payment_methods.getSelectedItem());

        return sellPayment;
    }

    @Override
    protected Sell_payment getObject() {
        if (sellPayment == null) {
            sellPayment = new Sell_payment();
            sellPayment.setDate(Calendar.getInstance().getTime());
            sellPayment.setFinance_payment_method(new Finance_payment_method(1, "Efectivo"));
            sellPayment.setOwner(getOwner());
        }

        return sellPayment;
    }

    @Override
    protected void setObjectToUI(Object object) {
        txt_payment_amount.getEditText().setText(getDebtAmount().setScale(2).toString());
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
        cmb_payment_methods.setAdapter(finance_payment_methodsAdapter.getArrayAdapter(EditSellPaymentActivity.this));
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

    private Owner getOwner() {
        return MySqlGson.postGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
    }
    private BigDecimal getDebtAmount() {
        BigDecimal debt_amount = (BigDecimal) getIntent().getSerializableExtra("debt_amount");
        return debt_amount;
    }

}
