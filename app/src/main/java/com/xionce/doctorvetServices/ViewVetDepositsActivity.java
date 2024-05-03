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
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.data.VetsDepositsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewVetDepositsActivity extends ViewBaseActivity
        /*implements BottomSheetDialog.BottomSheetListener*/ {

    private static final String TAG = "ViewVetServicesSchedule";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_vet_deposits);

        recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        toolbar_title.setText("Almacenes");
        toolbar_subtitle.setText("Configuración de almacenes");
        //hideSubtitle();
        hideToolbarImage();
        hideFab();

        Button create_deposit = findViewById(R.id.btn_create_deposit);
        create_deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loadedFinished) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(ViewVetDepositsActivity.this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(ViewVetDepositsActivity.this, EditVetDepositActivity.class);
                startActivity(intent);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showDeposits() {
        showProgressBar();

        DoctorVetApp.get().getVetDepositsAdapter(new DoctorVetApp.VolleyCallbackAdapter() {
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
                    DoctorVetApp.get().handle_error(ex, /*ViewVetDepositsActivity.this,*/ TAG, true);
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
        VetsDepositsAdapter vetsDepositsAdapter = (VetsDepositsAdapter) object;

        vetsDepositsAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                HelperClass.getOKCancelDialog(ViewVetDepositsActivity.this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showWaitDialog();
                        Vet_deposit vet_deposit = (Vet_deposit) data;
                        URL deleteVetDepositsUrl = NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.DELETE_DEPOSIT, vet_deposit.getId());
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, deleteVetDepositsUrl.toString(),
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
                                            DoctorVetApp.get().handle_onResponse_error(ex, /*ViewVetDepositsActivity.this,*/ TAG, true, response);
                                        } finally {
                                            hideWaitDialog();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        DoctorVetApp.get().handle_volley_error(error, /*ViewVetDepositsActivity.this,*/ TAG, true);
                                        hideWaitDialog();
                                    }
                                }
                        );
                        DoctorVetApp.get().addToRequestQueque(stringRequest);
                    }
                });
            }
        });

        vetsDepositsAdapter.setOnEditClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Vet_deposit vetDeposit = (Vet_deposit)data;
                Intent intent = new Intent(ViewVetDepositsActivity.this, EditVetDepositActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.DEPOSIT_OBJ.name(), MySqlGson.getPostJsonString(vetDeposit));
                startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
            }
        });

        recyclerView.setAdapter(vetsDepositsAdapter);
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showDeposits();
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
