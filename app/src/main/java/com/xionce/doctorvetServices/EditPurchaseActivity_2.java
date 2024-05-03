package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Finance_payment_method;
import com.xionce.doctorvetServices.data.Finance_payment_methodsAdapter;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EditPurchaseActivity_2 extends EditBaseActivity {

    private static final String TAG = "EditPurchaseActivity_2";
    private TextView txt_total;
    private TextView txt_return;
    private TextView txt_balance;
    private TextInputEditText txt_payment_amount;
    private Spinner cmb_payment_methods;

    private Purchase purchase = null;
    private Finance_payment_method paymentMethod = null;
    private Finance_payment_methodsAdapter paymentsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_purchase_2);
        toolbar_title.setText("Compra");
        toolbar_subtitle.setText("¿Cómo será el pago?");
        txt_total = findViewById(R.id.txt_total);
        txt_balance = findViewById(R.id.txt_balance);
        txt_return = findViewById(R.id.txt_return);
        cmb_payment_methods = findViewById(R.id.cmb_payment_methods);
        txt_payment_amount = findViewById(R.id.txt_payment_amount);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Finance_payment_method paymentMethod = getObject();
        setObjectToUI(paymentMethod);

        Purchase purchase = getPurchase();
        BigDecimal totalToPay = purchase.calculateTotal().setScale(2, RoundingMode.HALF_UP);
        txt_payment_amount.setText(totalToPay.toString());

        String paymentsInJson = getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PAYMENTS_TYPES_LIST.name());
        ArrayList<Finance_payment_method> payment_methods = MySqlGson.getGson().fromJson(paymentsInJson, new TypeToken<List<Finance_payment_method>>(){}.getType());
        Finance_payment_methodsAdapter financePaymentMethodsAdapter = new Finance_payment_methodsAdapter(payment_methods, Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.NONE);
        cmb_payment_methods.setAdapter(financePaymentMethodsAdapter.getArrayAdapter(EditPurchaseActivity_2.this));
        cmb_payment_methods.setSelection(financePaymentMethodsAdapter.getItemIndex(getObject()));

        RecyclerView recyclerView = findViewById(R.id.recycler_payments);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditPurchaseActivity_2.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        paymentsAdapter = new Finance_payment_methodsAdapter(getPurchase().getPayments(), Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.REMOVE);
        paymentsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                paymentsAdapter.removeItem(pos);
            }
        });
        recyclerView.setAdapter(paymentsAdapter);

        setTotalView();
        setBalanceInViews();

        txt_payment_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setBalanceInViews();
            }
        });

        TextView txt_multiple_payments = findViewById(R.id.txt_multiple_payments);
        txt_multiple_payments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPaymentIsZero()) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(EditPurchaseActivity_2.this), "Ingresa el monto del pago.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                Finance_payment_method payment = getCurrentPayment();
                if (!paymentsAdapter.isPossiblePayment(payment, getPurchase().calculateTotal())) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(EditPurchaseActivity_2.this), "El pago excede el total a pagar.", Snackbar.LENGTH_LONG).show();
                    return;
                }

                paymentsAdapter.addItemFirst(payment);
                setBalanceInViews();
            }
        });

        Button btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });

        txt_payment_amount.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PAYMENT_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PAYMENT_OBJ.name());
        paymentMethod = MySqlGson.getGson().fromJson(objectInString, Finance_payment_method.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH) {
            setResult(HelperClass.REQUEST_FINISH);
            finish();
        }
    }

    @Override
    protected void setObjectToUI(Object object) {
        Finance_payment_method financePaymentMethod = (Finance_payment_method) object;

        if (financePaymentMethod.getAmount() != null)
            txt_payment_amount.setText(financePaymentMethod.getAmount().toString());
    }

    @Override
    protected void save() {

    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return null;
    }

    @Override
    protected Object getObjectFromUI() {
        Finance_payment_method paymentMethod = getObject();
        paymentMethod.setName(((Finance_payment_method)cmb_payment_methods.getSelectedItem()).getName());

        if (NumberUtils.isCreatable(txt_payment_amount.getText().toString()))
            paymentMethod.setAmount(new BigDecimal(txt_payment_amount.getText().toString()));

        return paymentMethod;
    }

    @Override
    protected Finance_payment_method getObject() {
        if (paymentMethod == null)
            paymentMethod = new Finance_payment_method(3, "Efectivo");

        return paymentMethod;
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void next() {
        Purchase purchase = getPurchase();

        if (!validateNullProviderAndTotalPay(purchase)) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona distribuidor para registrar el saldo de deuda.", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (hasNoPayments() && !currentPaymentIsZero()) {
            if (currentPaymentIsGreaterThanTotalToPay())
                paymentsAdapter.addItem(getCurrentPaymentMax());
            else
                paymentsAdapter.addItem(getCurrentPayment());
        }

        Intent intent = new Intent(EditPurchaseActivity_2.this, EditPurchaseActivity_3.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(purchase));
        startActivityForResult(intent, 1);
    }

    protected Purchase getPurchase() {
        if (purchase == null)
            purchase = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name()), Purchase.class);

        return purchase;
    }
    private void setTotalView() {
        Purchase purchase = getPurchase();
        txt_total.setText("Total: " + HelperClass.formatCurrency(purchase.calculateTotal()));
    }
    private void setBalanceInViews() {
        BigDecimal current_payment = getCurrentPaymentAmount();
        BigDecimal sum_of_payments = getSumOfPayments();

        if (hasNoPayments())
            sum_of_payments = sum_of_payments.add(current_payment);

        BigDecimal total = getPurchase().calculateTotal();
        BigDecimal balance = sum_of_payments.subtract(total);
        BigDecimal zero = BigDecimal.ZERO;
        String currencyZero = HelperClass.formatCurrency(zero);

        if (balance.compareTo(zero) == 0) {
            txt_balance.setText(currencyZero);
            txt_return.setText(currencyZero);
            txt_balance.setVisibility(View.GONE);
            txt_return.setVisibility(View.GONE);
        } else if (balance.compareTo(BigDecimal.ZERO) < 0) {
            //incomplete payment
            txt_balance.setText("Deuda: " + HelperClass.formatCurrency(balance.abs()));
            txt_balance.setVisibility(View.VISIBLE);
            txt_return.setText("Cambio: " + currencyZero);
            txt_return.setVisibility(View.GONE);
        } else if (balance.compareTo(BigDecimal.ZERO) >= 0) {
            txt_balance.setText("Deuda: " + currencyZero);
            txt_balance.setVisibility(View.GONE);
            txt_return.setText("Cambio: " + HelperClass.formatCurrency(balance));
            txt_return.setVisibility(View.VISIBLE);
        }
    }
    private BigDecimal getSumOfPayments() {
        return Sell.calculateSumOfPayments(getPurchase().getPayments());
    }
    private BigDecimal getCurrentPaymentAmount() {
        BigDecimal payment_amount = new BigDecimal(0);
        String input = txt_payment_amount.getText().toString();
        if (NumberUtils.isCreatable(input)) {
            payment_amount = new BigDecimal(input);
        }

        return payment_amount;
    }
    private Finance_payment_method getCurrentPaymentMethod() {
        return (Finance_payment_method) cmb_payment_methods.getSelectedItem();
    }
    private Finance_payment_method getCurrentPayment() {
        Finance_payment_method finance_payment_method = new Finance_payment_method();
        finance_payment_method.setAmount(getCurrentPaymentAmount());
        finance_payment_method.setId(getCurrentPaymentMethod().getId());
        finance_payment_method.setName(getCurrentPaymentMethod().getName());
        finance_payment_method.setDate(Calendar.getInstance().getTime());
        return finance_payment_method;
    }
    private Finance_payment_method getCurrentPaymentMax() {
        Finance_payment_method payment = getCurrentPayment();
        payment.setAmount(getPurchase().calculateTotal());
        return payment;
    }
    private boolean currentPaymentIsGreaterThanTotalToPay() {
        return getCurrentPaymentAmount().compareTo(getPurchase().calculateTotal()) > 0;
    }
    private boolean currentPaymentIsZero() {
        return getCurrentPaymentAmount().compareTo(BigDecimal.ZERO) == 0;
    }
    private boolean hasNoPayments() {
        return getPurchase().getPayments().size() == 0;
    }
    private boolean validateNullProviderAndTotalPay(Purchase purchase) {
        if (is_null_provider() && hasDebtBalance(purchase))
            return false;

        return true;
    }
    private boolean hasDebtBalance(Purchase purchase) {
        BigDecimal sumOfPayments = purchase.calculateSumOfPayments();
        if (hasNoPayments())
            sumOfPayments = sumOfPayments.add(getCurrentPaymentAmount());

        return sumOfPayments.compareTo(purchase.calculateTotal()) < 0;
    }
    private boolean is_null_provider() {
        return getPurchase().getProvider() == null;
    }

}
