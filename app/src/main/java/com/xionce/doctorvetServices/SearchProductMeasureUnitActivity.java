package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Product_unit;
import com.xionce.doctorvetServices.data.Products_unitsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchProductMeasureUnitActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchProductoMeasureUnit";
    private Products_unitsAdapter productsunitsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.search_measure_unit));
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
        DoctorVetApp.get().getProductos_unitsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Product_unit> producto_unites = ((Product_unit.Get_pagination_unites)pagination).getContent();
                    productsunitsAdapter = new Products_unitsAdapter(producto_unites);
                    productsunitsAdapter.setOnClickHandler(SearchProductMeasureUnitActivity.this);
                    recyclerView.setAdapter(productsunitsAdapter);

                    manageShowRecyclerView(productsunitsAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Producto_categorias", /*SearchProductMeasureUnitActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    protected void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getProductos_unitsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Product_unit> productos_unites = ((Product_unit.Get_pagination_unites)pagination).getContent();
                    productsunitsAdapter.addItems(productos_unites);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Producto_unites", /*SearchProductMeasureUnitActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Product_unit productunit = (Product_unit) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name(), MySqlGson.getGson().toJson(productunit));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }

}
