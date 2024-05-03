package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.data.VetsAdapter;
import com.xionce.doctorvetServices.data.X_requests_users_vets;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

public class SearchVetActivity extends SearchActivityBase {

    private static final String TAG = "SearchVetActivity";
    private VetsAdapter vetsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextInputLayout txtBuscar = findViewById(R.id.txt_search);
        txtBuscar.setHint(getString(R.string.vet_search));
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
        DoctorVetApp.get().getVetsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                //hideStartSearch();
                if (pagination != null) {
                    showBottomBar();
                    setTotal_pages(pagination.getTotal_pages());

                    ArrayList<Vet> vets = ((Vet.Get_pagination_vets)pagination).getContent();
                    vetsAdapter = new VetsAdapter(vets, VetsAdapter.Adapter_types.SELECT_ITEM);
                    vetsAdapter.setOnOkClickHandler(new HelperClass.AdapterOnOkClickHandler() {
                        @Override
                        public void onOkClick(Object data, View view, int pos) {
                            Vet vet = (Vet) data;
                            AlertDialog.Builder builder = new AlertDialog.Builder(SearchVetActivity.this);

                            if (vet.getRequested() == 0) {
                                builder.setTitle(R.string.unirse_a_veterinaria_pregunta);
                                builder.setMessage(getString(R.string.unirse_a_veterinaria_mensaje, vet.getName()));
                                builder.setPositiveButton(R.string.unirse_a_veterinaria_unirse, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        sendSolicitudVeterinaria(vet.getId(), pos);
                                    }
                                });
                                builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                            } else {
                                builder.setTitle(R.string.cancelar_unirse_a_veterinaria_cancelar);
                                builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //cancelar peticion
                                        cancelSolicitudVeterinaria(vet.getId(), pos);
                                    }
                                });
                                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });
                            }

                            builder.create().show();
                        }
                    });

                    recyclerView.setAdapter(vetsAdapter);

                    manageShowRecyclerView(vetsAdapter, on_first_fill);

                    showSoftKeyboard();
                } else {
                    DoctorVetApp.get().handle_null_adapter("Vets", /*SearchVetActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    public void doPagination() {
        isLoading = true;
        showProgressBar();
        addPage();
        DoctorVetApp.get().getVetsPagination(getSearchText(), getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Vet> vets = ((Vet.Get_pagination_vets)pagination).getContent();
                    vetsAdapter.addItems(vets);
                    isLoading = false;
                } else {
                    DoctorVetApp.get().handle_null_adapter("Vets", /*SearchVetActivity.this,*/ TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

    private void sendSolicitudVeterinaria(Integer id_vet, final Integer pos) {
        showWaitDialog();

        X_requests_users_vets request = new X_requests_users_vets();
        request.setUser_email(DoctorVetApp.get().preferences_getUserEmail());
        request.setid_vet(id_vet);
        String json_request = MySqlGson.getGson().toJson(request);

        URL usersRequest = NetworkUtils.buildUsersRequestCreate();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, usersRequest.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String status = MySqlGson.getStatusFromResponse(response);
                    if (DoctorVetApp.Response_status.SUCCESS.name().equals(status)) {
                        vetsAdapter.setRequestSended(pos);
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, /*SearchVetActivity.this,*/ TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
                DoctorVetApp.get().handle_volley_error(error, /*SearchVetActivity.this,*/ TAG, true);
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json_request.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void cancelSolicitudVeterinaria(Integer id_vet, final Integer pos) {
        showWaitDialog();

        String user_email = DoctorVetApp.get().preferences_getUserEmail();
        URL usersRequest = NetworkUtils.buildUsersRequestDelete(user_email, id_vet);
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, usersRequest.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String status = MySqlGson.getStatusFromResponse(response);
                    if (DoctorVetApp.Response_status.SUCCESS.name().equals(status)) {
                        vetsAdapter.setRequestCanceled(pos);
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, /*SearchVetActivity.this,*/ TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
                DoctorVetApp.get().handle_volley_error(error, /*SearchVetActivity.this,*/ TAG, true);
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

}
