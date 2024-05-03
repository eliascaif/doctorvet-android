package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.ManufacturersAdapter;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.Product_manufacturer;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;
import com.xionce.doctorvetServices.utilities.controls.CustomGridLayoutManager;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditProductAssocActivity extends EditBaseActivity
        implements HelperClass.AdapterOnClickHandler, HelperClass.AdapterOnSelectClickHandler {

    private static final String TAG = "EditProductActivity_1";
    private ManufacturersAdapter manufacturersAndProductsAdapter;
    private int productos_seleccionados = 0;
    RecyclerView recyclerView;
    private ArrayList<Product> productos_de_la_veterinaria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_product_assoc);
        toolbar_image.setVisibility(View.GONE);
        toolbar_title.setText(R.string.products);
        recyclerView = findViewById(R.id.recyclerview);

        initializeInitRequestNumber(1);
        URL productos_allUrl = NetworkUtils.buildProductsUrl(NetworkUtils.ProductsUrlEnum.FOR_ASSOC, null);
        TokenStringRequest productos_jsonRequest = new TokenStringRequest(Request.Method.GET, productos_allUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            setRequestCompleted();
                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                            String json = HelperClass.compressedBase64ToString(data);

                            ArrayList<Product_manufacturer> manufacturersAndProducts = MySqlGson.getGson().fromJson(json, new TypeToken<List<Product_manufacturer>>(){}.getType());
                            manufacturersAndProductsAdapter = new ManufacturersAdapter(manufacturersAndProducts, ManufacturersAdapter.Manufacturers_types.MANUFACTURERS_PRODUCTS2);
                            manufacturersAndProductsAdapter.setOnClickHandler(EditProductAssocActivity.this);
                            manufacturersAndProductsAdapter.setOnSelectClickHandler(EditProductAssocActivity.this::onSelectClick);
                            manufacturersAndProductsAdapter.setProductsAdapterOnClickHandler(EditProductAssocActivity.this);
                            manufacturersAndProductsAdapter.setProductsAdapterOnSelectClickHandler(EditProductAssocActivity.this::onSelectClick);

                            CustomGridLayoutManager linearLayoutManager = new CustomGridLayoutManager(EditProductAssocActivity.this);
                            recyclerView.setLayoutManager(linearLayoutManager);
                            recyclerView.setHasFixedSize(false);
                            recyclerView.setAdapter(manufacturersAndProductsAdapter);
                            manufacturersAndProductsAdapter.recyclerView = recyclerView;

                            toolbar_subtitle.setText(getString(R.string.productos_seleccionados, manufacturersAndProductsAdapter.getSelectedProductsQuantity()));
                        } catch (Exception e) {
                            DoctorVetApp.get().handle_onResponse_error(e, /*EditProductAssocActivity.this,*/ TAG, true, response);
                            hideWaitDialog();
                            showOnCreateLoadingErrorMessage();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, /*EditProductAssocActivity.this,*/ TAG, true);
                        hideWaitDialog();
                        showOnCreateLoadingErrorMessage();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(productos_jsonRequest);

        Button btn_crear_product = findViewById(R.id.btn_crear_product);
        btn_crear_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent activity = new Intent(EditProductAssocActivity.this, EditProductActivity.class);
                startActivity(activity);
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        if (data.getClass().equals(Product.class)) {
            Product product = (Product) data;
            Intent activity = new Intent(EditProductAssocActivity.this, ViewProductVetActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), product.getId());
            startActivity(activity);
        } else {
            Product_manufacturer productmanufacturer = (Product_manufacturer) data;
            Intent activity = new Intent(EditProductAssocActivity.this, ViewManufacturerActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_MANUFACTURER_ID.name(), productmanufacturer.getId());
            startActivity(activity);
        }
    }

    @Override
    public void onSelectClick(Object data, View view, int pos) {
        if (data.getClass().equals(Product_manufacturer.class)) {
            Product_manufacturer productmanufacturer = (Product_manufacturer) data;
            manufacturersAndProductsAdapter.setSelectedProduct_by_id_manufacturer(productmanufacturer.getId(), pos);
        } else {
            Product product = (Product) data;
            manufacturersAndProductsAdapter.setSelectProduct(product.getId(), pos);
        }

        toolbar_subtitle.setText(getString(R.string.productos_seleccionados, manufacturersAndProductsAdapter.getSelectedProductsQuantity()));
    }

    @Override
    protected void save() {
        if (!validateSomethingToRegister())
            return;

        showWaitDialog();

        HashMap hashMap = new HashMap();
        hashMap.put("products", manufacturersAndProductsAdapter.getSelectedProductsArray() );
        final String json_object = MySqlGson.getGson().toJson(hashMap);

        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, /*EditProductAssocActivity.this,*/ TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, /*EditProductAssocActivity.this,*/ TAG, true);
                hideWaitDialog();
            }
        })
        {
            @Override
            public byte[] getBody() {
                return json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {
        save();
    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildProductsUrl(NetworkUtils.ProductsUrlEnum.ASSOCIATES, null);
    }

    @Override
    protected Object getObjectFromUI() {
        return null;
    }

    @Override
    protected Object getObject() {
        return null;
    }

    @Override
    protected void setObjectToUI(Object object) {

    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }

    private boolean validateSomethingToRegister() {
        if (manufacturersAndProductsAdapter.getSelectedProductsQuantity() == 0) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.nada_para_registrar, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}




