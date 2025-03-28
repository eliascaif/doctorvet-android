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
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.Sell_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;

public class ViewSellActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2, DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "ViewSellActivity";
    private TextView txt_date;
    private TextView txt_user;
    private TextView txt_receipt;
    private TextView txt_pet;
    private TextView txt_owner;
    private TextView txt_total;
    private TextView txt_balance;
    private RecyclerView recycler_products;
    private RecyclerView recycler_payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_sell);
        hideToolbarImage();
        hideSubtitle();

        txt_date = findViewById(R.id.txt_date);
        txt_user = findViewById(R.id.txt_user);
        txt_receipt = findViewById(R.id.txt_receipt);
        txt_pet = findViewById(R.id.txt_pet);
        txt_owner = findViewById(R.id.txt_owners);
        txt_total = findViewById(R.id.txt_total);
        txt_balance = findViewById(R.id.txt_balance);

        recycler_products = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewSellActivity.this);
        recycler_products.setLayoutManager(layoutManager);
        recycler_products.setHasFixedSize(true);
        recycler_products.setVisibility(GONE);

        recycler_payments = findViewById(R.id.recycler_payments);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(ViewSellActivity.this);
        recycler_payments.setLayoutManager(layoutManager2);
        recycler_payments.setHasFixedSize(true);
        recycler_payments.setVisibility(GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewSellActivity.this, "ViewSellActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showSell() {
        showProgressBar();
        Integer id_sell = getIdSell();
        DoctorVetApp.get().getSell(id_sell, new DoctorVetApp.VolleyCallbackSell() {
            @Override
            public void onSuccess(Sell resultSell) {
                try {
                    showActivityContainer();
                    if (resultSell != null) {
                        setUI(resultSell);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, TAG, true);
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
        Sell sell = (Sell) object;

        String title = "Venta";
        if (sell.getDeleted() == 1)
            title += " ELIMINADO ";
        toolbar_title.setText(title);

        txt_date.setText("Fecha: " + HelperClass.getDateInLocale(sell.getDate(), getContext()));
        txt_receipt.setText("Comprobante: " + sell.getReceipt());

        txt_pet.setVisibility(GONE);
        if (sell.getPet() != null) {
            txt_pet.setVisibility(View.VISIBLE);
            txt_pet.setText(DoctorVetApp.get().getPetNaming() + ": " + sell.getPet().getName());
        }

        txt_owner.setVisibility(GONE);
        if (sell.getOwner() != null) {
            txt_owner.setVisibility(View.VISIBLE);
            txt_owner.setText(DoctorVetApp.get().getOwnerNaming() + ": " + sell.getOwner().getName());
        }

        txt_user.setText(getText(R.string.user) + ": " + sell.getUser().getName());

        txt_total.setText("Total: " + HelperClass.formatCurrency(sell.getTotal()));

        txt_balance.setVisibility(GONE);
        if (sell.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            txt_balance.setVisibility(View.VISIBLE);
            txt_balance.setText("Saldo deudor: " + HelperClass.formatCurrency(sell.getBalance()));
        }

        Sell_itemAdapter items_adapter = new Sell_itemAdapter(sell.getItems(), Sell_itemAdapter.SellItemsAdapterTypes.SHOW);
        recycler_products.setAdapter(items_adapter);
        if (items_adapter.getItemCount() > 0)
            recycler_products.setVisibility(View.VISIBLE);

        //Hide payments delete for sell/purcahses without customer/provider. Check permission
        Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types adapterType = Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.NONE;
        if (sell.getOwner() != null && DoctorVetApp.get().getUser().getPermissions().actions_permissions.sells_payments.delete == 1)
            adapterType = Finance_payment_methodsAdapter.Finance_payment_methodsAdapter_types.REMOVE;

        Finance_payment_methodsAdapter payments_adapter = new Finance_payment_methodsAdapter(sell.getPayments(), adapterType);
        recycler_payments.setAdapter(payments_adapter);
        payments_adapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        Finance_payment_method sell_payment = (Finance_payment_method)data;
                        final Integer idSellPayment = sell_payment.getId();
                        URL delete_ownerUrl = NetworkUtils.buildDeleteSellPaymentUrl(idSellPayment);
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
                                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                                        } finally {
                                            hideWaitDialog();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
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
        showSell();
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
                final Integer idSell = getIdSell();
                URL delete_ownerUrl = NetworkUtils.buildDeleteSellUrl(idSell);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_ownerUrl.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String success = MySqlGson.getStatusFromResponse(response);
                                if (success.equalsIgnoreCase("success")) {
                                    refreshView();
//                                    on_delete_complete(idSell);
//                                    finish();
                                } else {
                                    showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                }
                            } catch (Exception ex) {
                                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                            } finally {
                                hideWaitDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DoctorVetApp.get().handle_volley_error(error, TAG, true);
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

    private Integer getIdSell() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.SELL_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case SELL_CANCEL:
                go_delete();
                break;
            case SELL_PDF:
                get_sell_pdf();
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewSellActivity.this);
        }
    }

    private void get_sell_pdf() {
        Integer id_sell = getIdSell();

        showWaitDialog();

        URL pdfSellEndPoint = NetworkUtils.buildGetSellsPdfUrl(id_sell);

        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, pdfSellEndPoint.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    HelperClass.writeToFile(response, ViewSellActivity.this, "sell.pdf");
                    boolean success = HelperClass.viewPdf("sell.pdf", ViewSellActivity.this);
                    if (!success)
                        Snackbar.make(DoctorVetApp.getRootForSnack(ViewSellActivity.this), getString(R.string.err_pdf_viewer), Snackbar.LENGTH_SHORT).show();
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
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);

    }

    @Override
    public Context getContext() {
        return ViewSellActivity.this;
    }

}
