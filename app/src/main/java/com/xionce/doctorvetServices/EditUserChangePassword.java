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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditUserChangePassword extends AppCompatActivity {

    private static final String TAG = "EditUserChangePassword";
    private TextInputLayout txtPassword;
    private TextInputLayout txtPassword_actual;
    private TextInputLayout txtPassword_repeat;
    private CoordinatorLayout cordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_change_password);
        txtPassword = findViewById(R.id.txt_password);
        txtPassword_actual = findViewById(R.id.txt_password_actual);
        txtPassword_repeat = findViewById(R.id.txt_password_repeat);
        cordinatorLayout = findViewById(R.id.root_cordinator_layout);
        final ProgressBar pb_loading_indicator = findViewById(R.id.pb_loading_indicator);
        final Button btn_continue = findViewById(R.id.btn_continue);

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validateActualPassword() || !validatePassword() || !validatePasswordRepeat())
                    return;

                pb_loading_indicator.setVisibility(View.VISIBLE);
                final Dialog mOverlayDialog = HelperClass.getOverlayDialog(EditUserChangePassword.this);
                mOverlayDialog.show();

                String new_password = txtPassword.getEditText().getText().toString();
                String actual_password = txtPassword_actual.getEditText().getText().toString();

                JsonObject j = new JsonObject();
                j.addProperty("new_password", new_password);
                j.addProperty("actual_password", actual_password);
                j.toString();
//
//                Object toSend = new Object(){ String new_password = new_pass, String; };
//                User new_user = new User(null, email, password);
                final String json_user_object = j.toString(); //MySqlGson.postGson().toJson(new_user);

                URL changePasswordUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.PASSWORD_CHANGE, null, null, null);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, changePasswordUrl.toString(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                Snackbar.make(cordinatorLayout, "Contraseña restablecida", Snackbar.LENGTH_LONG).show();
                                DoctorVetApp.get().preferences_deleteLoginInfo();
                                DoctorVetApp.get().deleteAllCacheFiles();

                                setResult(HelperClass.REQUEST_FINISH);

                                Intent intent = new Intent(EditUserChangePassword.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Snackbar.make(cordinatorLayout, "No es posible resetear contraseña", Snackbar.LENGTH_LONG).show();
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_error(ex, /*EditUserChangePassword.this,*/ TAG, true);
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
                        DoctorVetApp.get().handle_volley_error(error, /*EditUserChangePassword.this,*/ TAG, true);
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
    private boolean validateActualPassword() {
        return HelperClass.validateEmpty(txtPassword_actual);
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
