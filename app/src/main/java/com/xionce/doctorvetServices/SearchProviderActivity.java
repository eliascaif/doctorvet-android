package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.Product_providersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchProviderActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchProviderActivity";
    private Product_providersAdapter product_providersAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.search_provider));
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
        DoctorVetApp.get().getProductsProvidersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Product_provider> productProviders = ((Product_provider.Get_pagination_providers)pagination).getContent();
                    product_providersAdapter = new Product_providersAdapter(productProviders, DoctorVetApp.Adapter_types.SEARCH);
                    product_providersAdapter.setOnClickHandler(SearchProviderActivity.this);
                    recyclerView.setAdapter(product_providersAdapter);

                    manageShowRecyclerView(product_providersAdapter, on_first_fill);

                    manageShowCreateElement(product_providersAdapter, "crear distribuidor", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchProviderActivity.this, EditProviderActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Products_providers", /*SearchProviderActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getProductsProvidersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Product_provider> productProviders = ((Product_provider.Get_pagination_providers)pagination).getContent();
                    product_providersAdapter.addItems(productProviders);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Products_provider", /*SearchProviderActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Product_provider product_provider = (Product_provider) data;
        Intent intent = getIntent();

        if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_VIEW, false)) {
            Intent activity = new Intent(SearchProviderActivity.this, ViewProviderActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), product_provider.getId());
            startActivity(activity);
        } else if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false)) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), MySqlGson.getGson().toJson(product_provider));
            this.setResult(RESULT_OK, dataBackIntent);
        }

        finish();
    }
}
