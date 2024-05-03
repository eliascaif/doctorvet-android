package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Vet_point;
import com.xionce.doctorvetServices.data.VetPointsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewVetPointsActivity extends ViewBaseActivity
        /*implements BottomSheetDialog.BottomSheetListener*/ {

    private static final String TAG = "ViewVetServicesSchedule";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_vet_sell_points);

        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar_title.setText("Emisión de comprobantes");
        toolbar_subtitle.setText("Configuración de puntos de emisión de comprobantes");

        hideToolbarImage();
        hideFab();

        Button create_sell_point = findViewById(R.id.btn_create_sell_point);
        create_sell_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadedFinished) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(ViewVetPointsActivity.this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ViewVetPointsActivity.this, EditVetPointActivity.class);
                startActivity(intent);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showSellPoints() {
        showProgressBar();

        DoctorVetApp.get().getVetPointsAdapter(new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                try {
                    showActivityContainer();
                    if (resultAdapter != null) {
                        setUI(resultAdapter);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewVetSellPointsActivity.this,*/ TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }
    @Override
    protected void setUI(Object object) {
        VetPointsAdapter vetsVetPointsAdapter = (VetPointsAdapter) object;

        vetsVetPointsAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                HelperClass.getOKCancelDialog(ViewVetPointsActivity.this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        Vet_point sellPoint = (Vet_point) data;
                        URL deleteVetSellPointsUrl = NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.DELETE_POINT, sellPoint.getId());
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, deleteVetSellPointsUrl.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String success = MySqlGson.getStatusFromResponse(response);
                                            if (success.equalsIgnoreCase("success")) {
                                                refreshView();
                                            } else {
                                                showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                            }
                                        } catch (Exception ex) {
                                            DoctorVetApp.get().handle_onResponse_error(ex, /*ViewVetSellPointsActivity.this,*/ TAG, true, response);
                                        } finally {
                                            hideWaitDialog();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        DoctorVetApp.get().handle_volley_error(error, /*ViewVetSellPointsActivity.this,*/ TAG, true);
                                        hideWaitDialog();
                                    }
                                }
                        );
                        DoctorVetApp.get().addToRequestQueque(stringRequest);
                    }
                });
            }
        });

        recyclerView.setAdapter(vetsVetPointsAdapter);
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showSellPoints();
    }

    @Override
    protected void go_update() {
    }

    private void go_insert_pet() {
    }

    @Override
    protected void go_delete() {
    }

    @Override
    protected void on_update_complete(Intent data) {
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

}
