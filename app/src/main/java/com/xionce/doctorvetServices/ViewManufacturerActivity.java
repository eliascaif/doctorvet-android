package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Product_manufacturer;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewManufacturerActivity extends ViewBaseActivity implements BottomSheetDialog.BottomSheetListener {

    private static final String TAG = "ViewManufacturerActivit";
    private Product_manufacturer productmanufacturer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_manufacturer);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle b = new Bundle();
                b.putString("manufacturer_specific", "true");
                bottomSheet.setArguments(b);
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetDialog");
            }
        });

        toolbar_subtitle.setVisibility(View.GONE);
        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    @Override
    protected void refreshView() {
        showManufacturer();
    }

    @Override
    protected void go_update() {
    }

    @Override
    protected void go_delete() {
    }

    @Override
    protected void on_update_complete(Intent data) {

    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {

    }

    private void showManufacturer() {
        showProgressBar();
        Integer idManufacturer = getIdManufacturer();
        URL get_product_fabricanteUrl = NetworkUtils.buildGetManufacturerUrl(idManufacturer);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, get_product_fabricanteUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showActivityContainer();
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            productmanufacturer = MySqlGson.getGson().fromJson(data, Product_manufacturer.class);
                            setUI(productmanufacturer);
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
                        DoctorVetApp.get().handle_volley_error(error, /*ViewManufacturerActivity.this,*/ TAG, true);
                        hideProgressBar();
                        showErrorMessage();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void setUI(Object object) {
        Product_manufacturer product_manufacturer = (Product_manufacturer) object;
        toolbar_title.setText(product_manufacturer.getName());
        DoctorVetApp.ObjectToTextView(findViewById(R.id.lista_datos_fabricante), product_manufacturer, "txt_");

        String thumb_url = product_manufacturer.getThumb_url();
        if (thumb_url != null && !thumb_url.isEmpty()) {
            //circular queda muy mal, alguna imagenes se reescalaban
            Glide.with(getApplicationContext()).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);
//            Glide.with(getApplicationContext()).load(thumb_url).into(toolbar_image);

            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent activity = new Intent(ViewManufacturerActivity.this, ViewFullScreenPhoto.class);
                    activity.putExtra(HelperClass.INTENT_IMAGE_URL, thumb_url);
                    startActivity(activity);
                }
            });
        } else {
            Glide.with(getApplicationContext()).load(R.drawable.ic_store_holo_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);
        }

        setPhone(product_manufacturer.getPhone());

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linear = findViewById(R.id.lista_datos_fabricante);
        DoctorVetApp.invisibilizeEmptyTextView(linear);
    }

    private Integer getIdManufacturer() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_MANUFACTURER_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.BottomSheetButtonClicked buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case COMUNICATION_CALL:
                sendCall(productmanufacturer.getPhone());
                break;
            case COMUNICATION_WHATSAPP:
                sendWhatsAppMessage(productmanufacturer.getPhone());
                break;
            case COMUNICATION_EMAIL:
                try {
                    HelperClass.sendEmail(ViewManufacturerActivity.this, productmanufacturer.getEmail());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case COMUNICATION_SMS:
                try {
                    HelperClass.sendSMS(ViewManufacturerActivity.this, productmanufacturer.getPhone());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewManufacturerActivity.this);
        }
    }

}
