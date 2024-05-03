package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet_study_item;
import com.xionce.doctorvetServices.data.Pet_study_itemsAdapter;
import com.xionce.doctorvetServices.data.Symptom;
import com.xionce.doctorvetServices.data.SymptomsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchStudyItemActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchSymptomActivity";
    private Pet_study_itemsAdapter petStudyItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint("Buscar ítem");
        search(true);
        setRecyclerView();

        Intent intent = new Intent(SearchStudyItemActivity.this, EditStudyItemActivity.class);
        intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name());
        setCreateListener(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == HelperClass.REQUEST_CREATE && data != null) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name(), data.getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name()));
            dataBackIntent.putExtra("GET_MODE", "CREATED");
            this.setResult(RESULT_OK, dataBackIntent);
            finish();
        }
    }

    @Override
    public void search(boolean on_first_fill) {
        if (!on_first_fill && !validateEmptySearch())
            return;

        resetPage();
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getStudyItemsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Pet_study_item> studyItems = ((Pet_study_item.Get_pagination_study_items)pagination).getContent();
                    petStudyItemsAdapter = new Pet_study_itemsAdapter(studyItems);
                    petStudyItemsAdapter.setOnClickHandler(SearchStudyItemActivity.this);
                    recyclerView.setAdapter(petStudyItemsAdapter);

                    manageShowRecyclerView(petStudyItemsAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_studies_items", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getStudyItemsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Pet_study_item> studyItems = ((Pet_study_item.Get_pagination_study_items)pagination).getContent();
                    petStudyItemsAdapter.addItems(studyItems);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets_studies_items", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Pet_study_item studyItem = (Pet_study_item) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name(), MySqlGson.getGson().toJson(studyItem));
        dataBackIntent.putExtra("GET_MODE", "SEARCHED");
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }

}
