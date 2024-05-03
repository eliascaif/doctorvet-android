package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;

import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.WaitingRoomsAdapter;
import com.xionce.doctorvetServices.data.Waiting_room;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.ArrayList;

public class ViewWaitingRoomsHistoricActivity extends RecyclerViewActivity implements HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "ViewWaitingRoomsHistori";
    private WaitingRoomsAdapter waitingRoomsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar_title.setText("Sala de espera");
        toolbar_subtitle.setVisibility(View.GONE);
    }

    @Override
    protected void refreshAdapter() {
        showProgressBar();
        hideRecyclerView();

        DoctorVetApp.get().getWaitingRoomsHistoricPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Waiting_room> waitingRooms = ((Waiting_room.Get_pagination_waiting_rooms)pagination).getContent();
                    waitingRoomsAdapter = new WaitingRoomsAdapter(waitingRooms, WaitingRoomsAdapter.WaitingRoomsAdapterType.ALL_USERS);

                    if (waitingRoomsAdapter.getItemCount() == 0) {
                        showEmptyListMessage();
                    } else {
                        recyclerView.setAdapter(waitingRoomsAdapter);

                        //pagination
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(ViewWaitingRoomsHistoricActivity.this);

                        showRecyclerView();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewWaitingRoomsHistoricActivity.this,*/ TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
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
        DoctorVetApp.get().getWaitingRoomsHistoricPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Waiting_room> waitingRooms = ((Waiting_room.Get_pagination_waiting_rooms)pagination).getContent();
                    waitingRoomsAdapter.addItems(waitingRooms);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("waiting_rooms", /*ViewWaitingRoomsHistoricActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }
}
