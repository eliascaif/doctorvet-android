package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import java.net.URL;

public class LoginForgotPassword_3 extends AppCompatActivity {

    private static final String TAG = "LoginForgotPassword_3";
    private TextInputLayout txtPassword;
    private TextInputLayout txtPassword_repeat;
    private CoordinatorLayout coordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_forgot_password_3);
        txtPassword = findViewById(R.id.txt_password);
        txtPassword_repeat = findViewById(R.id.txt_password_repeat);
        coordinatorLayout = findViewById(R.id.root_cordinator_layout);
        final ProgressBar pb_loading_indicator = findViewById(R.id.pb_loading_indicator);
        final Button btn_continue = findViewById(R.id.btn_continue);

        String email = getIntent().getStringExtra("FORGOT_ACCOUNT_EMAIL");

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validatePassword() || !validatePasswordRepeat())
                    return;

                pb_loading_indicator.setVisibility(View.VISIBLE);
                final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginForgotPassword_3.this);
                mOverlayDialog.show();

                String password = txtPassword.getEditText().getText().toString();
                User new_user = new User(email, password);
                final String json_user_object = MySqlGson.postGson().toJson(new_user);

                URL create_accountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.FORGOT_ACCOUNT_3, null, null, null);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, create_accountUrl.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                Snackbar.make(coordinatorLayout, "Contraseña restablecida", Snackbar.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginForgotPassword_3.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar.make(coordinatorLayout, "No es posible resetear contraseña", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_error(ex, /*LoginForgotPassword_3.this,*/ TAG, true);
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
                        DoctorVetApp.get().handle_volley_error(error, /*LoginForgotPassword_3.this,*/ TAG, true);
                    }
                })
                {
                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return json_user_object.getBytes();
                    }
                };
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

    private boolean validatePassword() {
        return HelperClass.validateEmpty(txtPassword);
    }
    private boolean validatePasswordRepeat() {
        String pass1 = txtPassword.getEditText().getText().toString();
        String pass2 = txtPassword_repeat.getEditText().getText().toString();
        if (!pass2.equals(pass1)) {
            txtPassword_repeat.setError(txtPassword_repeat.getContext().getString(R.string.error_contrasenas_no_coinciden));
            txtPassword_repeat.getEditText().requestFocus();
            return false;
        } else {
            txtPassword_repeat.setError(null);
            return true;
        }
    }

}
