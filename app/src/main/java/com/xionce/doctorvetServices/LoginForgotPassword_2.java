package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import java.net.URL;

public class LoginForgotPassword_2 extends AppCompatActivity {

    private static final String TAG = "LoginForgotPassword_2";
    private CoordinatorLayout rootCoodinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forgot_password_2);
        TextView txt_info = findViewById(R.id.txt_info);
        rootCoodinatorLayout = findViewById(R.id.root_cordinator_layout);
        final ProgressBar pb_loading_indicator = findViewById(R.id.pb_loading_indicator);
        final Button btn_continue = findViewById(R.id.btn_continue);

        String email = getIntent().getStringExtra("FORGOT_ACCOUNT_EMAIL");
        final String info_cuenta = getString(R.string.check_login_valid) + " " + email + ". " + getString(R.string.check_login_valid_2);
        txt_info.setText(info_cuenta);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_loading_indicator.setVisibility(View.VISIBLE);
                final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginForgotPassword_2.this);
                mOverlayDialog.show();

                URL forgotAccountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.IS_IN_PASSWORD_RESTORE, null, email, null);
                StringRequest stringRequest = new StringRequest(Request.Method.GET, forgotAccountUrl.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Integer success = Integer.parseInt(MySqlGson.getDataFromResponse(response).toString());
                            if (success == 1) {
                                Intent intent = new Intent(LoginForgotPassword_2.this, LoginForgotPassword_3.class);
                                intent.putExtra("FORGOT_ACCOUNT_EMAIL", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar.make(rootCoodinatorLayout, info_cuenta, Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_error(ex, /*LoginForgotPassword_2.this,*/ TAG, true);
                        } finally {
                            pb_loading_indicator.setVisibility(View.INVISIBLE);
                            mOverlayDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pb_loading_indicator.setVisibility(View.INVISIBLE);
                        mOverlayDialog.dismiss();
                        DoctorVetApp.get().handle_volley_error(error, /*LoginForgotPassword_2.this,*/ "LoginForgotPassword", true);
                    }
                });
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }
}
