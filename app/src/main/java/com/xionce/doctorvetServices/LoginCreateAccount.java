package com.xionce.doctorvetServices;

import android.app.Dialog;
import android.content.Intent;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

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

public class LoginCreateAccount extends AppCompatActivity {

    private static final String TAG = "LoginCreateAccount";
    TextInputLayout txtName;
    TextInputLayout txtEmail;
    TextInputLayout txtPassword;
    TextInputLayout txtPassword_repeat;
    ProgressBar pb_loading_indicator;
    private boolean humanVerify = BuildConfig.DEBUG; //false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_create_account);
        txtName = findViewById(R.id.txt_name);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        txtPassword_repeat = findViewById(R.id.txt_password_repeat);
        pb_loading_indicator = findViewById(R.id.pb_loading_indicator);

        Button btn_create_account = findViewById(R.id.btn_create_account);
        btn_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

        txtPassword_repeat.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    createAccount();
                    return true;
                }
                return false;
            }
        });

        CheckBox chkHumanVerify = findViewById(R.id.chk_human_verify);
        chkHumanVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (humanVerify) return;

                pb_loading_indicator.setVisibility(View.VISIBLE);
                DoctorVetApp.get().doCaptcha(LoginCreateAccount.this, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        humanVerify = result;
                        pb_loading_indicator.setVisibility(View.GONE);
                    }
                });
            }
        });

//        DoctorVetApp.get().showKeyboard();
//        txtName.requestFocus();
    }

    private void createAccount() {
        if (!validateName() || !validateEmail() || !validatePassword() || !validatePasswordRepeat())
            return;

        if (!humanVerify) {
            Snackbar.make( DoctorVetApp.getRootForSnack(LoginCreateAccount.this), "Valida que no eres robot", Snackbar.LENGTH_SHORT).show();
            return;
        }

        pb_loading_indicator.setVisibility(View.VISIBLE);
        final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginCreateAccount.this);
        mOverlayDialog.show();

        String name = txtName.getEditText().getText().toString();
        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();
        User new_user = new User(name, email, password); //, HelperClass.generateGUID(), HelperClass.getDeviceName());
        new_user.setLogin_type(DoctorVetApp.login_types.EMAIL.name());
        final String json_user_object = MySqlGson.getGson().toJson(new_user);

        URL create_accountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.CREATE_ACCOUNT, null, null, null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, create_accountUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pb_loading_indicator.setVisibility(View.INVISIBLE);
                mOverlayDialog.dismiss();
                try {
                    DoctorVetApp.get().preferences_setLoginInfo(email, password, DoctorVetApp.login_types.EMAIL,null);
                    Intent activity = new Intent(LoginCreateAccount.this, LoginCheckValid.class);
                    startActivity(activity);
                    finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pb_loading_indicator.setVisibility(View.INVISIBLE);
                mOverlayDialog.dismiss();
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
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

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateEmail() {
        return HelperClass.validateEmail(txtEmail, false);
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
