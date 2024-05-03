package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Product_category;
import com.xionce.doctorvetServices.data.Products_categoriesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchProductCategoryActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchProductCategoryAc";
    private Products_categoriesAdapter productscategoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.category_search));
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
        DoctorVetApp.get().getProductsCategoriesPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setSugiereUnoListener(EditProductCategoryActivity.class);
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Product_category> productcategories = ((Product_category.Get_pagination_categorias)pagination).getContent();
                    productscategoriesAdapter = new Products_categoriesAdapter(productcategories, DoctorVetApp.AdapterSelectTypes.NORMAL);
                    productscategoriesAdapter.setOnClickHandler(SearchProductCategoryActivity.this);
                    recyclerView.setAdapter(productscategoriesAdapter);

                    manageShowRecyclerView(productscategoriesAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Producto_categorias", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getProductsCategoriesPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Product_category> productos_categorias = ((Product_category.Get_pagination_categorias)pagination).getContent();
                    productscategoriesAdapter.addItems(productos_categorias);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Producto_categorias", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Product_category productcategory = (Product_category) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_CATEGORY_OBJ.name(), MySqlGson.getGson().toJson(productcategory));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
