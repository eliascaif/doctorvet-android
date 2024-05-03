package com.xionce.doctorvetServices;

import android.app.Dialog;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import java.net.URL;

public class LoginForgotPassword extends AppCompatActivity {

    private static final String TAG = "LoginForgotPassword";
    private TextInputLayout txtEmail;
    CoordinatorLayout rootCoodinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forgot_password);
        txtEmail = findViewById(R.id.txt_email);
        final ProgressBar pb_loading_indicator = findViewById(R.id.pb_loading_indicator);
        final Button btn_create_account = findViewById(R.id.btn_send_email);
        rootCoodinatorLayout = findViewById(R.id.root_cordinator_layout);

        DoctorVetApp.get().markRequired(txtEmail);

        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateEmail())
                    return;

                pb_loading_indicator.setVisibility(View.VISIBLE);
                final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginForgotPassword.this);
                mOverlayDialog.show();

                String email = txtEmail.getEditText().getText().toString();
                User user = new User(email);
                final String json = MySqlGson.postGson().toJson(user);
                URL forgotAccountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.FORGOT_ACCOUNT, null, null, null);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, forgotAccountUrl.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                Intent intent = new Intent(LoginForgotPassword.this, LoginForgotPassword_2.class);
                                intent.putExtra("FORGOT_ACCOUNT_EMAIL", email);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar.make(rootCoodinatorLayout, "No es posible resetear contraseña", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_error(ex, /*LoginForgotPassword.this,*/ TAG, true);
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
                        DoctorVetApp.get().handle_volley_error(error, /*LoginForgotPassword.this,*/ "LoginForgotPassword", true);
                    }
                })
                {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return json.getBytes();
                    }
                };
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

    private boolean validateEmail() {
        return HelperClass.validateEmpty(txtEmail) && HelperClass.validateEmail(txtEmail, false);
    }

}
