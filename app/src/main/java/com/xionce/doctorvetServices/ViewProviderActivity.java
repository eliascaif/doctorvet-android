package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.math.BigDecimal;
import java.net.URL;

public class ViewProviderActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2 {

    private static final String TAG = "ViewProviderActivity";
    private Product_provider productProvider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_provider);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewProviderActivity.this, "ViewProviderActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }


    @Override
    protected void refreshView() {
        showProvider();
    }

    @Override
    protected void go_update() {
        Intent intent = new Intent(this, EditProviderActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), MySqlGson.getGson().toJson(productProvider));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    @Override
    protected void go_delete() {
        HelperClass.getOKCancelDialog(this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer id_provider = getIdProvider();
                URL url = NetworkUtils.buildProviderUrl(id_provider, null);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, url.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String success = MySqlGson.getStatusFromResponse(response);
                                    if (success.equalsIgnoreCase("success")) {
                                        finish();
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

    private void showProvider() {
        showProgressBar();
        Integer idProvider = getIdProvider();
        URL providersUrl = NetworkUtils.buildGetProviderUrl(idProvider);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, providersUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showActivityContainer();
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            productProvider = MySqlGson.getGson().fromJson(data, Product_provider.class);
                            setUI(productProvider);
                            setLoadedFinished();
                        } catch (Exception ex) {
                            showErrorMessage();
                        } finally {
                            hideProgressBar();
                            hideSwipeRefreshLayoutProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideProgressBar();
                        showErrorMessage();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    @Override
    protected void setUI(Object object) {
        Product_provider productProvider = (Product_provider) object;

        Glide.with(getApplicationContext()).load(R.drawable.ic_provider_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);

        toolbar_title.setText(productProvider.getName());
        String email = productProvider.getEmail();
        if (email != null)
            toolbar_subtitle.setText(email);

        DoctorVetApp.ObjectToTextView(findViewById(R.id.lista_datos_provider), productProvider, "txt_");

        super.setPhoto(productProvider.getThumb_url(), productProvider.getPhoto_url());

        setPhone(productProvider.getPhone());

        //owner info/states
        final LinearLayout linear_provider_info = findViewById(R.id.linear_provider_info);
        linear_provider_info.removeAllViews();

        //debt
        if (productProvider.getBalance() != null && !productProvider.getBalance().equals(BigDecimal.ZERO)) {
            View debt_view = getLayoutInflater().inflate(R.layout.list_item_owner_info_debt, null);
            TextView debt = debt_view.findViewById(R.id.txt_debt_amount);
            TextView details = debt_view.findViewById(R.id.txt_debt_details);
            TextView pay = debt_view.findViewById(R.id.txt_debt_pay);

            debt.setText("Deuda: " + HelperClass.formatCurrency(productProvider.getBalance()));

            details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewProviderActivity.this, ViewProviderDebtsActivity.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), productProvider.getId());
                    intent.putExtra("provider_name", productProvider.getName());
                    startActivity(intent);
                }
            });

            pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ViewProviderActivity.this, EditPurchasePaymentActivity.class);
                    intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), MySqlGson.getPostJsonString(productProvider));
                    intent.putExtra("debt_amount", productProvider.getBalance());
                    startActivity(intent);
                }
            });

            linear_provider_info.addView(debt_view);
        }

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linearProviders = findViewById(R.id.lista_datos_provider);
        DoctorVetApp.setTextViewVisibility(linearProviders);

    }

    private Integer getIdProvider() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case PROVIDER_UPDATE:
                go_update();
                break;
            case PROVIDER_DELETE:
                go_delete();
                break;
            case PROVIDER_BUY:
                go_insert_purchase();
                break;
            case COMUNICATION_CALL:
                sendCall(productProvider.getPhone());
                break;
            case COMUNICATION_WHATSAPP:
                sendWhatsAppMessage(productProvider.getPhone());
                break;
            case COMUNICATION_EMAIL:
                try {
                    HelperClass.sendEmail(ViewProviderActivity.this, productProvider.getEmail());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case COMUNICATION_SMS:
                try {
                    HelperClass.sendSMS(ViewProviderActivity.this, productProvider.getPhone());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this),getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewProviderActivity.this);
        }
    }

    private void go_insert_purchase() {
        Intent intent = new Intent(ViewProviderActivity.this, EditPurchaseActivity_1.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), MySqlGson.getGson().toJson(productProvider));
        startActivity(intent);
    }

}
