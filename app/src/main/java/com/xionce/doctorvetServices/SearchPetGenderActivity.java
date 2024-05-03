package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.Pet_gender;
import com.xionce.doctorvetServices.data.Pet_gendersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

public class SearchPetGenderActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    //los sexos son pocos, se cargan todos
    private static final String TAG = "SearchMascotaSexoAc";
    private Pet_gendersAdapter petgendersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSearch();
        search(true);
    }

    @Override
    public void search(boolean on_first_fill) {
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getPets_gendersAdapter(new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                hideProgressBar();
                //hideStartSearch();
                if (resultAdapter != null) {
                    showBottomBar();
                    setSugiereUnoListener(EditPetCharacterActivity.class);
                    petgendersAdapter = (Pet_gendersAdapter) resultAdapter;
                    petgendersAdapter.setOnClickHandler(SearchPetGenderActivity.this);
                    recyclerView.setAdapter(petgendersAdapter);
                    manageShowRecyclerView(petgendersAdapter, on_first_fill);
                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_genders", /*SearchPetGenderActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    protected void doPagination() {

    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Pet_gender petgender = (Pet_gender) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PET_GENDER_OBJ.name(), MySqlGson.getGson().toJson(petgender));
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
