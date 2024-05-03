package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.data.VetsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ChangeVetActivity extends RecyclerViewActivity {

    private static final String TAG = "ChangeVetActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        toolbar_image.setVisibility(View.GONE);
        toolbar_subtitle.setVisibility(View.GONE);
        toolbar_title.setText(R.string.vets);
    }

    private void refreshAsociations() {
        showProgressBar();

        URL userVetsUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.USER_VETS_BY_TOKEN, null, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, userVetsUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String vetsArray = MySqlGson.getDataFromResponse(response).toString();
                    ArrayList<Vet> vets = MySqlGson.getGson().fromJson(vetsArray, new TypeToken<List<Vet>>(){}.getType());
                    VetsAdapter vetsAdapter = new VetsAdapter(vets, VetsAdapter.Adapter_types.SHOW);

                    if (vetsAdapter.getItemCount() == 0) {
                        return;
                    }

                    vetsAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
                        @Override
                        public void onClick(Object data, View view, int pos) {
                            Vet vet = (Vet) data;
                            changeVet(vet.getId(), pos);
                        }
                    });
                    recyclerView.setAdapter(vetsAdapter);
                } catch (Exception e) {
                    DoctorVetApp.get().handle_onResponse_error(e, /*ChangeVetActivity.this,*/ TAG, true, response);
                } finally {
                    hideProgressBar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, /*ChangeVetActivity.this,*/ TAG, true);
                hideProgressBar();
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void refreshAdapter() {
        refreshAsociations();
    }

    private void changeVet(Integer id_vet, final Integer pos) {
        showWaitDialog();

        Vet changeVet = new Vet();
        changeVet.setId(id_vet);
        String change_vet_json_object = MySqlGson.postGson().toJson(changeVet);

        URL peticionUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.CHANGE_VET, null, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, peticionUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).getAsJsonObject().get("user").toString();
                    User responseUser = MySqlGson.getGson().fromJson(data, User.class);
                    DoctorVetApp.get().preferences_deleteLoginInfo();
                    DoctorVetApp.get().preferences_setLoginInfo(null, null, null, responseUser.getAccess_token());
                    //DoctorVetApp.get().setUserNullForVetChange();
                    Intent intent = new Intent(ChangeVetActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, /*ChangeVetActivity.this,*/ TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideWaitDialog();
                showVolleyError(error, TAG);
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return change_vet_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

}
