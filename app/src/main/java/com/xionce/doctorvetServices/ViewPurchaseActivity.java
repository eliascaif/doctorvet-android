package com.xionce.doctorvetServices;

import static android.view.View.GONE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Finance_payment_method;
import com.xionce.doctorvetServices.data.Finance_payment_methodsAdapter;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.Purchase_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;

public class ViewPurchaseActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2, DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "ViewPurchaseActivity";
    private TextView txt_date;
    private TextView txt_receipt;
    private TextView txt_user;
    private TextView txt_provider;
    private TextView txt_total;
    private TextView txt_balance;
    private RecyclerView recycler_products;
    private RecyclerView recycler_payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_purchase);
        hideToolbarImage();
        hideSubtitle();

        txt_date = findViewById(R.id.txt_date);
        txt_receipt = findViewById(R.id.txt_receipt);
        txt_user = findViewById(R.id.txt_user);
        txt_provider = findViewById(R.id.txt_provider);
        txt_total = findViewById(R.id.txt_total);
        txt_balance = findViewById(R.id.txt_balance);

        recycler_products = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewPurchaseActivity.this);
        recycler_products.setLayoutManager(layoutManager);
        recycler_products.setHasFixedSize(true);
        recycler_products.setVisibility(GONE);

        recycler_payments = findViewById(R.id.recycler_payments);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(ViewPurchaseActivity.this);
        recycler_payments.setLayoutManager(layoutManager2);
        recycler_payments.setHasFixedSize(true);
        recycler_payments.setVisibility(GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewPurchaseActivity.this, "ViewPurchaseActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showPurchase() {
        showProgressBar();
        Integer id_purchase = getIdPurchase();
        DoctorVetApp.get().getPurchase(id_purchase, new DoctorVetApp.VolleyCallbackPurchase() {
            @Override
            public void onSuccess(Purchase resultPurchase) {
                try {
                    showActivityContainer();
                    if (resultPurchase != null) {
                        setUI(resultPurchase);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewPurchaseActivity.this,*/ TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }
    @Override
    protected void setUI(Object object) {
        Purchase purchase = (Purchase) object;

        String title = "Compra";
        if (purchase.getDeleted() == 1)
            title += " ELIMINADO ";
        toolbar_title.setText(title);

        txt_date.setText("Fecha: " + HelperClass.getDateInLocale(purchase.getDate(), getContext()));

        txt_receipt.setVisibility(GONE);
        if (purchase.getReceipt() != null) {
            txt_receipt.setVisibility(View.VISIBLE);
            txt_receipt.setText("Comprobante: " + purchase.getReceipt());
        }

        txt_provider.setVisibility(GONE);
        if (purchase.getProvider() != null) {
            txt_provider.setVisibility(View.VISIBLE);
            txt_provider.setText(getText(R.string.provider) + ": " + purchase.getProvider().getName());
        }

        txt_user.setText(getText(R.string.user) + ": " + purchase.getUser().getName());

        txt_total.setText("Total: " + HelperClass.formatCurrency(purchase.getTotal()));

        txt_balance.setVisibility(GONE);
        if (purchase.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            txt_balance.setVisibility(View.VISIBLE);
            txt_balance.setText("Saldo deudor: " + HelperClass.formatCurrency(purchase.getBalance()));
        }

        Purchase_itemAdapter items_adapter = new Purchase_itemAdapter(purchase.getItems(), Purchase_itemAdapter.PurchaseDetailAdapter_types.SHOW);
        recycler_products.setAdapter(items_adapter);
        if (items_adapter.getItemCount() > 0)
            recycler_products.setVisibility(View.VISIBLE);

        //Hide payments delete for sell/purcahses without customer/provider. Check permission.
        Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types adapterType = Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.NONE;
        if (purchase.getProvider() != null && DoctorVetApp.get().getUser().getPermissions().actions_permissions.purchases_payments.delete == 1)
            adapterType = Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.REMOVE;

        Finance_payment_methodsAdapter payments_adapter = new Finance_payment_methodsAdapter(purchase.getPayments(), adapterType);
        recycler_payments.setAdapter(payments_adapter);
        payments_adapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        Finance_payment_method purchase_payment = (Finance_payment_method)data;
                        final Integer idPurchasePayment = purchase_payment.getId();
                        URL delete_ownerUrl = NetworkUtils.buildDeletePurchasePaymentUrl(idPurchasePayment);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_ownerUrl.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String success = MySqlGson.getStatusFromResponse(response);
                                            if (success.equalsIgnoreCase("success")) {
                                                refreshView();
                                            } else {
                                                showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                            }
                                        } catch (Exception ex) {
                                            DoctorVetApp.get().handle_onResponse_error(ex, /*ViewPurchaseActivity.this,*/ TAG, true, response);
                                        } finally {
                                            hideWaitDialog();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        DoctorVetApp.get().handle_volley_error(error, /*ViewPurchaseActivity.this,*/ TAG, true);
                                        hideWaitDialog();
                                    }
                                }
                        );
                        DoctorVetApp.get().addToRequestQueque(stringRequest);
                    }
                });
            }
        });
        if (payments_adapter.getItemCount() > 0)
            recycler_payments.setVisibility(View.VISIBLE);
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showPurchase();
    }

    @Override
    protected void go_update() {
    }

    @Override
    protected void go_delete() {
        HelperClass.getOKCancelDialog(this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer idPurchase = getIdPurchase();
                URL delete_purchaseUrl = NetworkUtils.buildDeletePurchaseUrl(idPurchase);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_purchaseUrl.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String success = MySqlGson.getStatusFromResponse(response);
                                if (success.equalsIgnoreCase("success")) {
                                    on_delete_complete(idPurchase);
                                    finish();
                                } else {
                                    showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                }
                            } catch (Exception ex) {
                                DoctorVetApp.get().handle_onResponse_error(ex, /*ViewPurchaseActivity.this,*/ TAG, true, response);
                            } finally {
                                hideWaitDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DoctorVetApp.get().handle_volley_error(error, /*ViewPurchaseActivity.this,*/ TAG, true);
                            hideWaitDialog();
                        }
                    }
                );
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

    @Override
    protected void on_update_complete(Intent data) {
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

    private Integer getIdPurchase() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case PURCHASE_CANCEL:
                go_delete();
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewPurchaseActivity.this);
        }
    }

    @Override
    public Context getContext() {
        return ViewPurchaseActivity.this;
    }

}
