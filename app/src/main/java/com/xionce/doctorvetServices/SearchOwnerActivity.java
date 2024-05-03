package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.OwnersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchOwnerActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchOwnerActivity";
    private OwnersAdapter ownersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtSearch = findViewById(R.id.txt_search);
        txtSearch.setHint("Buscar " + DoctorVetApp.get().getOwnerNaming());
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
        DoctorVetApp.get().getOwnersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Owner> owners = ((Owner.Get_pagination_owners)pagination).getContent();
                    ownersAdapter = new OwnersAdapter(owners, DoctorVetApp.Adapter_types.SEARCH);
                    ownersAdapter.setOnClickHandler(SearchOwnerActivity.this);
                    recyclerView.setAdapter(ownersAdapter);

                    manageShowRecyclerView(ownersAdapter, on_first_fill);

                    manageShowCreateElement(ownersAdapter, "crear " + DoctorVetApp.get().getOwnerNaming(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchOwnerActivity.this, EditOwnerActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Owners", /*SearchOwnerActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getOwnersPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Owner> owners = ((Owner.Get_pagination_owners)pagination).getContent();
                    ownersAdapter.addItems(owners);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Owners", /*SearchOwnerActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Owner owner = (Owner) data;
        Intent intent = getIntent();

        if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_VIEW, false)) {
            Intent activity = new Intent(SearchOwnerActivity.this, ViewOwnerActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
            activity.putExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 1);
            startActivity(activity);
        } else if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false)) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(owner));
            this.setResult(RESULT_OK, dataBackIntent);
        }

        finish();
    }

}