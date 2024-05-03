package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.RegionsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchRegionActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static String TAG = "SearchRegionActivity";
    private RegionsAdapter regionsAdapter;
    private boolean suggest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtSearch.setHint(getString(R.string.region_search));
        search(true);
        setRecyclerView();

        suggest = getIntent().getBooleanExtra("suggest", false);
        if (suggest) {
            setSugiereUnoListener(EditRegionActivity.class);
        }
    }

    @Override
    public void search(boolean on_first_fill) {
        if (!on_first_fill && !validateEmptySearch())
            return;

        resetPage();
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getRegionsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Region> regiones = ((Region.Get_pagination_regiones)pagination).getContent();
                    regionsAdapter = new RegionsAdapter(regiones);
                    regionsAdapter.setOnClickHandler(SearchRegionActivity.this);
                    recyclerView.setAdapter(regionsAdapter);

                    manageShowRecyclerView(regionsAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Regiones", /*SearchRegionActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getRegionsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Region> regiones = ((Region.Get_pagination_regiones)pagination).getContent();
                    regionsAdapter.addItems(regiones);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Regiones", /*SearchRegionActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Region region = (Region) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name(), MySqlGson.getGson().toJson(region));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }

}
