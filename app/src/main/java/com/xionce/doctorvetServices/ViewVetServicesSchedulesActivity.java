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
import com.xionce.doctorvetServices.data.Service_schedule;
import com.xionce.doctorvetServices.data.ServicesSchedulesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewVetServicesSchedulesActivity extends ViewBaseActivity {

    private static final String TAG = "ViewVetServicesSchedule";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_vet_schedules);
        toolbar_title.setText("Servicios y horarios");
        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        hideSubtitle();
        hideToolbarImage();
        hideFab();

        Button create_schedule = findViewById(R.id.btn_create_schedule);
        create_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadedFinished) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(ViewVetServicesSchedulesActivity.this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ViewVetServicesSchedulesActivity.this, EditVetScheduleActivity.class);
                startActivity(intent);
            }
        });

        Button edit_services = findViewById(R.id.btn_edit_services);
        edit_services.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadedFinished) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(ViewVetServicesSchedulesActivity.this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ViewVetServicesSchedulesActivity.this, EditVetServiceAssocActivity.class);
                startActivity(intent);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    @Override
    protected void setUI(Object object) {
        ServicesSchedulesAdapter servicesSchedulesAdapter = (ServicesSchedulesAdapter) object;
        servicesSchedulesAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                //go_delete();
                HelperClass.getOKCancelDialog(ViewVetServicesSchedulesActivity.this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        Service_schedule service_schedule = (Service_schedule) data;
                        URL deleteServicesSchedulesVetUrl = NetworkUtils.buildDeleteServicesSchedulesUrl(service_schedule.getId());
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, deleteServicesSchedulesVetUrl.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            int response_value = Integer.parseInt(data);
                                            if (response_value == 1) {
                                                refreshView();
                                            } else {
                                                showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                            }
                                        } catch (Exception ex) {
                                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                                        } finally {
                                            hideWaitDialog();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                                        hideWaitDialog();
                                    }
                                }
                        );
                        DoctorVetApp.get().addToRequestQueque(stringRequest);
                    }
                });
            }
        });
        recyclerView.setAdapter(servicesSchedulesAdapter);
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showVetServices();
    }

    @Override
    protected void go_update() {
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

    private void showVetServices() {
        showProgressBar();
        DoctorVetApp.get().getServicesSchedulesVet(new DoctorVetApp.VolleyCallbackAdapter() {
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
                    DoctorVetApp.get().handle_error(ex, TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }

}
