package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.ManufacturersAdapter;
import com.xionce.doctorvetServices.data.Product_manufacturer;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchManufacturerActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchProviderActivity";
    private ManufacturersAdapter manufacturersAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.search_manufacturer));
        search(true);
        setRecyclerView();
    }

    @Override
    public void search(boolean on_first_fill) {
        if (!on_first_fill && !validateEmptySearch())
            return;

        resetPage();
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getProductsManufacturersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Product_manufacturer> productManufacturers = ((Product_manufacturer.Get_pagination_manufacturers)pagination).getContent();
                    manufacturersAdapter = new ManufacturersAdapter(productManufacturers);
                    manufacturersAdapter.setOnClickHandler(SearchManufacturerActivity.this);
                    recyclerView.setAdapter(manufacturersAdapter);

                    manageShowRecyclerView(manufacturersAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Products_manufacturers", /*SearchManufacturerActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getProductsManufacturersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Product_manufacturer> productManufacturers = ((Product_manufacturer.Get_pagination_manufacturers)pagination).getContent();
                    manufacturersAdapter.addItems(productManufacturers);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Products_manufacturers", /*SearchManufacturerActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Product_manufacturer productManufacturer = (Product_manufacturer) data;
        Intent intent = getIntent();

        if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_VIEW, false)) {
            Intent activity = new Intent(SearchManufacturerActivity.this, ViewManufacturerActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_MANUFACTURER_ID.name(), productManufacturer.getId());
            startActivity(activity);
        } else if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false)) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_MANUFACTURER_OBJ.name(), MySqlGson.getGson().toJson(productManufacturer));
            this.setResult(RESULT_OK, dataBackIntent);
        }

        finish();
    }
}
