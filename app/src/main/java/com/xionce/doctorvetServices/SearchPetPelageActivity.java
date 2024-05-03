package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet_pelage;
import com.xionce.doctorvetServices.data.Pet_pelagesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchPetPelageActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchPetPelageActivity";
    private Pet_pelagesAdapter petpelagesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.pelage_search));
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
        DoctorVetApp.get().getPets_pelagesPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Pet_pelage> petpelages = ((Pet_pelage.Get_pagination_pelages)pagination).getContent();
                    petpelagesAdapter = new Pet_pelagesAdapter(petpelages);
                    petpelagesAdapter.setOnClickHandler(SearchPetPelageActivity.this);
                    recyclerView.setAdapter(petpelagesAdapter);

                    if (petpelages.size() == 0)
                        setSugiereUnoListener(EditPetPelageActivity.class);

                    manageShowRecyclerView(petpelagesAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_pelages", /*SearchPetPelageActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getPets_pelagesPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Pet_pelage> pelajes = ((Pet_pelage.Get_pagination_pelages)pagination).getContent();
                    petpelagesAdapter.addItems(pelajes);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_pelages", /*SearchPetPelageActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Pet_pelage petpelage = (Pet_pelage) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PET_PELAGE_OBJ.name(), MySqlGson.getGson().toJson(petpelage));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
