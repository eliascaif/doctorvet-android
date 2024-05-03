package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet_clinic2;
import com.xionce.doctorvetServices.data.Symptom;
import com.xionce.doctorvetServices.data.Treatment;
import com.xionce.doctorvetServices.data.TreatmentsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchTreatmentActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchTreatmentActivity";
    private TreatmentsAdapter treatmentsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.treatment_search));
        search(true);
        setRecyclerView();

        Intent intent = new Intent(SearchTreatmentActivity.this, EditSDTActivity.class);
        intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name());
        setCreateListener(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == HelperClass.REQUEST_CREATE && data != null) {
//            String createSDT = data.getExtras().getString(DoctorVetApp.REQUEST_CREATE_OBJECT, "");
//
//            if (createSDT.equalsIgnoreCase(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name())) {
                //Treatment treatment = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name()), Treatment.class);
                Intent dataBackIntent = getIntent();
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name(), data.getStringExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name()));
                dataBackIntent.putExtra("GET_MODE", "CREATED");
                this.setResult(RESULT_OK, dataBackIntent);
                finish();
//            }

        }

    }

    @Override
    public void search(boolean on_first_fill) {
        if (!on_first_fill && !validateEmptySearch())
            return;

        resetPage();
        showProgressBar();
        hideFailSearch();
        DoctorVetApp.get().getTreatmentsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Treatment> treatments = ((Treatment.Get_pagination_treatments)pagination).getContent();
                    treatmentsAdapter = new TreatmentsAdapter(treatments);
                    treatmentsAdapter.setOnClickHandler(SearchTreatmentActivity.this);
                    recyclerView.setAdapter(treatmentsAdapter);

//                    if (treatments.size() == 0)
//                        setSugiereUnoListener(EditSDT_suggestedActivity.class);

                    manageShowRecyclerView(treatmentsAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("treatments", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getTreatmentsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Treatment> treatments = ((Treatment.Get_pagination_treatments)pagination).getContent();
                    treatmentsAdapter.addItems(treatments);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("treatments", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Treatment treatment = (Treatment) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name(), MySqlGson.getGson().toJson(treatment));
        dataBackIntent.putExtra("GET_MODE", "SEARCHED");
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
