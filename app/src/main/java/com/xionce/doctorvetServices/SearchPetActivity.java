package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.PetsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.util.ArrayList;

public class SearchPetActivity extends SearchActivityBase implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "SearchPetActivity";
    private PetsAdapter petsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        //txtBuscar.setHint(getString(R.string.pet_search));
        txtBuscar.setHint("Buscar " + DoctorVetApp.get().getPetNaming());
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
        DoctorVetApp.get().getPetsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                    petsAdapter = new PetsAdapter(pets, DoctorVetApp.Adapter_types.SEARCH);
                    petsAdapter.setOnClickHandler(SearchPetActivity.this);
                    recyclerView.setAdapter(petsAdapter);

                    manageShowRecyclerView(petsAdapter, on_first_fill);

                    manageShowCreateElement(petsAdapter, "crear " + DoctorVetApp.get().getPetNaming(), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(SearchPetActivity.this, EditPetActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets", /*SearchPetActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getPetsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                    petsAdapter.addItems(pets);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Pets", /*SearchPetActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Pet pet = (Pet) data;
        Intent intent = getIntent();

        if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_VIEW, false)) {
            Intent activity = new Intent(SearchPetActivity.this, ViewPetActivity.class);
            activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet.getId());
            activity.putExtra(DoctorVetApp.INTENT_UPDATE_LAST_VIEW, 1);
            startActivity(activity);
        } else if (intent.getBooleanExtra(DoctorVetApp.INTENT_SEARCH_RETURN, false)) {
            Intent dataBackIntent = getIntent();
            dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name(), MySqlGson.getGson().toJson(pet));
            this.setResult(RESULT_OK, dataBackIntent);
        }

        finish();
    }
}