package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Diagnostic;
import com.xionce.doctorvetServices.data.DiagnosticsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchDiagnosticActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchDiagnosticActivity";
    private DiagnosticsAdapter diagnosticsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.diagnostic_search));
        search(true);
        setRecyclerView();

        Intent intent = new Intent(SearchDiagnosticActivity.this, EditSDTActivity.class);
        intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name());
        setCreateListener(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == HelperClass.REQUEST_CREATE && data != null) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name(), data.getStringExtra(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name()));
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
        DoctorVetApp.get().getDiagnosticsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();

                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Diagnostic> diagnostics = ((Diagnostic.Get_pagination_diagnostics)pagination).getContent();
                    diagnosticsAdapter = new DiagnosticsAdapter(diagnostics);
                    diagnosticsAdapter.setOnClickHandler(SearchDiagnosticActivity.this);
                    recyclerView.setAdapter(diagnosticsAdapter);

                    manageShowRecyclerView(diagnosticsAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("diagnostics", /*SearchDiagnosticActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getDiagnosticsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Diagnostic> diagnostics = ((Diagnostic.Get_pagination_diagnostics)pagination).getContent();
                    diagnosticsAdapter.addItems(diagnostics);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("diagnostics", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Diagnostic diagnostic = (Diagnostic) data;
        Intent dataBackIntent = getIntent();
        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name(), MySqlGson.getGson().toJson(diagnostic));
        dataBackIntent.putExtra("GET_MODE", "SEARCHED");
        this.setResult(RESULT_OK, dataBackIntent);
        finish();
    }
}
