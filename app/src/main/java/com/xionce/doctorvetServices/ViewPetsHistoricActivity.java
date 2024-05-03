package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;

import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.PetsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class ViewPetsHistoricActivity extends RecyclerViewActivity implements HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "ViewPetsHistoricActivit";
    private PetsAdapter petsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar_title.setText("Últimos movimientos");
        toolbar_subtitle.setVisibility(View.GONE);
    }

    @Override
    protected void refreshAdapter() {
        showProgressBar();
        hideRecyclerView();

        DoctorVetApp.get().getPetsRecentPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                    petsAdapter = new PetsAdapter(pets, DoctorVetApp.Adapter_types.COMPACT);

                    if (petsAdapter.getItemCount() == 0) {
                        showEmptyListMessage();
                    } else {
                        recyclerView.setAdapter(petsAdapter);

                        //pagination
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(ViewPetsHistoricActivity.this);

                        showRecyclerView();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewPetsHistoricActivity.this,*/ TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }

    @Override
    public void onPagination() {
        recyclerView.startLoading();
        showProgressBar();
        recyclerView.addPage();
        DoctorVetApp.get().getPetsRecentPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                    petsAdapter.addItems(pets);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("owners", /*ViewPetsHistoricActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }
}
