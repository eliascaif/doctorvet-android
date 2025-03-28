package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import java.net.URL;

public class LoginCheckValid extends AppCompatActivity {

    private static final String TAG = "LoginCheckValid";
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_check_valid);

        TextView txt_info = findViewById(R.id.txt_info);
        coordinatorLayout = findViewById(R.id.root_cordinator_layout);
        final String info_cuenta = getString(R.string.check_login_valid) + " " + DoctorVetApp.get().preferences_getUserEmail() + " " + getString(R.string.check_login_valid_2);
        txt_info.setText(info_cuenta);
//        final String info_cuenta_snack = "Revisa " + DoctorVetApp.get().preferences_getUserEmail() + " para continuar.";

        final ProgressBar pb_loading_indicator = findViewById(R.id.pb_loading_indicator);

        Button btn_check_account = findViewById(R.id.btn_login);
        btn_check_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_loading_indicator.setVisibility(View.VISIBLE);
                final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginCheckValid.this);
                mOverlayDialog.show();

                User user = new User();
                user.setEmail(DoctorVetApp.get().preferences_getUserEmail());
                user.setPassword(DoctorVetApp.get().preferences_get_user_password());
                String userJson = MySqlGson.getGson().toJson(user);

                URL check_accountUrl = NetworkUtils.buildUsersCheckAccountUrl();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, check_accountUrl.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pb_loading_indicator.setVisibility(View.INVISIBLE);
                        mOverlayDialog.dismiss();
                        try {
                            Integer success = Integer.parseInt(MySqlGson.getDataFromResponse(response).toString());
                            if (success == 1) {
                                Intent act = new Intent(LoginCheckValid.this, LoginChoiceActivity.class);
                                startActivity(act);
                                finish();
                            } else {
//                                Snackbar.make(coordinatorLayout, info_cuenta_snack, Snackbar.LENGTH_LONG).show();
                                Snackbar.make(coordinatorLayout, info_cuenta, Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        pb_loading_indicator.setVisibility(View.INVISIBLE);
                        mOverlayDialog.dismiss();
                    }
                })
                {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return userJson.getBytes();
                    }
                };
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });

        Button btn_use_another_account = findViewById(R.id.btn_use_another_account);
        btn_use_another_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_loading_indicator.setVisibility(View.VISIBLE);
                final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginCheckValid.this);
                mOverlayDialog.show();

                //TODO: //borrar info cuenta local y web. volver a loginactivity
                URL delete_unverified_accountUrl = NetworkUtils.buildUsersDeleteUnverifiedAccountUrl(DoctorVetApp.get().preferences_getUserEmail());
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, delete_unverified_accountUrl.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        pb_loading_indicator.setVisibility(View.INVISIBLE);
                        mOverlayDialog.dismiss();
                        try {
                            Integer success = Integer.parseInt(MySqlGson.getDataFromResponse(response).toString());
                            if (success == 1) {
                                DoctorVetApp.get().preferences_deleteLoginInfo();
                                Intent act = new Intent(LoginCheckValid.this, LoginActivity.class);
                                startActivity(act);
                                finish();
                            } else {
                                Snackbar.make(coordinatorLayout, response, Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            Snackbar.make(coordinatorLayout, response, Snackbar.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        pb_loading_indicator.setVisibility(View.INVISIBLE);
                        mOverlayDialog.dismiss();
                    }
                });
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

}
