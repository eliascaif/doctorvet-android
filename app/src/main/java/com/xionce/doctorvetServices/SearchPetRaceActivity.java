package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet_race;
import com.xionce.doctorvetServices.data.Pet_racesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchPetRaceActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchPetRaceActivity";
    private Pet_racesAdapter petracesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.race_search));
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
        DoctorVetApp.get().getPets_racesPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Pet_race> mascotaRazas = ((Pet_race.Get_pagination_races)pagination).getContent();
                    petracesAdapter = new Pet_racesAdapter(mascotaRazas);
                    petracesAdapter.setOnClickHandler(SearchPetRaceActivity.this);
                    recyclerView.setAdapter(petracesAdapter);

                    if (mascotaRazas.size() == 0)
                        setSugiereUnoListener(EditPetRaceActivity.class);

                    manageShowRecyclerView(petracesAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_races", /*SearchPetRaceActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getPets_racesPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Pet_race> razas = ((Pet_race.Get_pagination_races)pagination).getContent();
                    petracesAdapter.addItems(razas);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_races", /*SearchPetRaceActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Pet_race petrace = (Pet_race) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PET_RACE_OBJ.name(), MySqlGson.getGson().toJson(petrace));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
