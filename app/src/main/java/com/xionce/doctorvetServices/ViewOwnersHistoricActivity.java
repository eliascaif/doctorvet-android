package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;

import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.OwnersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class ViewOwnersHistoricActivity extends RecyclerViewActivity implements HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "ViewOwnersHistoricActiv";
    private OwnersAdapter ownersAdapter;

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

        DoctorVetApp.get().getOwnersRecentPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Owner> owners = ((Owner.Get_pagination_owners)pagination).getContent();
                    ownersAdapter = new OwnersAdapter(owners, DoctorVetApp.get().preferences_get_owners_view_mode());

                    if (ownersAdapter.getItemCount() == 0) {
                        showEmptyListMessage();
                    } else {
                        recyclerView.setAdapter(ownersAdapter);

                        //pagination
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(ViewOwnersHistoricActivity.this);

                        showRecyclerView();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewOwnersHistoricActivity.this,*/ TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
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
        DoctorVetApp.get().getOwnersRecentPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Owner> owners = ((Owner.Get_pagination_owners)pagination).getContent();
                    ownersAdapter.addItems(owners);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("owners", /*ViewOwnersHistoricActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }
}
