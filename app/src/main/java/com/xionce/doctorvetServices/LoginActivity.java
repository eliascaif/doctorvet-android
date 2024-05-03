package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.NetworkUtils.DOCTOR_VET_BASE_URL;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    TextInputLayout txtEmail;
    TextInputLayout txtPassword;
    ProgressBar pb_loading_indicator;
    private boolean humanVerify = BuildConfig.DEBUG; //false;

    //Google
    private SignInClient oneTapClient;
    private BeginSignInRequest signUpRequest;

    //Facebook
    //CallbackManager facebookCallbackManager;

    CoordinatorLayout rootCordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //if there is token then proceed to main
        String token = DoctorVetApp.get().preferences_getUserToken();
        if (!token.isEmpty()) {
            tokenSignIn(DoctorVetApp.get().preferences_getUserToken());
            return;
        }

        DoctorVetApp.login_types loginType = DoctorVetApp.get().preferences_getUserLoginType();
        if (loginType != null) {
            switch (loginType) {
                case EMAIL:
                    emailContrasenaSignIn(DoctorVetApp.get().preferences_getUserEmail(), DoctorVetApp.get().preferences_get_user_password());
                    break;
                case GOOGLE:
                case FACEBOOK:
                    googleFacebookSignIn();
                    break;
            }
            return;
        }

        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_login);
        rootCordinatorLayout = findViewById(R.id.root_cordinator_layout);
        setLoggingControls();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (facebookCallbackManager != null)
//            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setLoggingControls() {
        TextView txt_crear_cuenta = findViewById(R.id.txt_new_account);
        txt_crear_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginCreateAccount.class);
                startActivity(intent);
            }
        });

        Button btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });

        TextView txt_forgot_password = findViewById(R.id.txt_forgot_password);
        txt_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, LoginForgotPassword.class);
                startActivity(intent);
            }
        });

        pb_loading_indicator = findViewById(R.id.pb_loading_indicator);
        txtEmail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_password);
        txtPassword.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    doLogin();
                    return true;
                }
                return false;
            }
        });

        //google
        findViewById(R.id.google_login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!humanVerify) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(LoginActivity.this), "Valida que no eres robot", Snackbar.LENGTH_SHORT).show();
                    //Snackbar.make(rootCordinatorLayout, "Valida que no eres robot", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                initGoogleSignIn();
                googleSignIn();
            }
        });

        //facebook
//        findViewById(R.id.fb_login_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                initFacebookSignIn();
//                facebookSignIn();
//            }
//        });

        //privacy policy
        TextView txt_privacy_policy = findViewById(R.id.txt_privacy_policy);
        txt_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(DOCTOR_VET_BASE_URL + "privacy-policy.html");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        CheckBox chkHumanVerify = findViewById(R.id.chk_human_verify);
        chkHumanVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (humanVerify) return;

                pb_loading_indicator.setVisibility(View.VISIBLE);
                DoctorVetApp.get().doCaptcha(LoginActivity.this, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        humanVerify = result;
                        pb_loading_indicator.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
    private void doLogin() {
        if (!validateEmail() || !validatePassword())
            return;

        if (!humanVerify) {
            Snackbar.make( DoctorVetApp.getRootForSnack(LoginActivity.this), "Valida que no eres robot", Snackbar.LENGTH_SHORT).show();
            //Snackbar.make(rootCordinatorLayout, "Valida que no eres robot", Snackbar.LENGTH_SHORT).show();
            return;
        }

        String email = txtEmail.getEditText().getText().toString();
        String password = txtPassword.getEditText().getText().toString();

        emailContrasenaSignIn(email, password);
    }
    private void tokenSignIn(String token) {
        if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.VISIBLE);
        final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginActivity.this);
        mOverlayDialog.show();

        URL tokenUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.USER_AND_VET_BY_TOKEN, null, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, tokenUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    //User user = MySqlGson.getGson().fromJson(data, User.class);
                    //DoctorVetApp.get().setUser(user);
                    DoctorVetApp.get().preferences_set_user(data);
                    Intent activity = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(activity);
                    finish();
                } catch (Exception e) {
                    DoctorVetApp.get().handle_onResponse_error(e, TAG, true, response);
                } finally {
                    if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.INVISIBLE);
                    mOverlayDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.INVISIBLE);
                mOverlayDialog.dismiss();

                //handle invalidated token error
                if (error.networkResponse != null) {
                    String statusCode = String.valueOf(error.networkResponse.statusCode);
                    if (statusCode.equalsIgnoreCase("401")) {
                        DoctorVetApp.get().preferences_deleteLoginInfo();
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void emailContrasenaSignIn(String email, final String password) {
        if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.VISIBLE);
        final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginActivity.this);
        mOverlayDialog.show();

        User login_user = new User();
        login_user.setEmail(email);
        login_user.setPassword(password);
        final String json_user_object = MySqlGson.getPostJsonString(login_user);

        URL login_checkUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.EMAIL_PRE_AUTH, null, null, null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, login_checkUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String login_response_str = MySqlGson.getDataFromResponse(response).getAsString();
                    DoctorVetApp.login_response loginResponse = DoctorVetApp.login_response.valueOf(login_response_str);

                    DoctorVetApp.get().preferences_setLoginInfo(login_user.getEmail(), login_user.getPassword(), DoctorVetApp.login_types.EMAIL,null);

                    Intent activity;
                    switch (loginResponse){
                        case ACCOUNT_WAITING_FOR_EMAIL_CHECK:
                            activity = new Intent(LoginActivity.this, LoginCheckValid.class);
                            startActivity(activity);
                            finish();
                            break;
                        case ACCOUNT_WAITING_FOR_VET_ASSOCIATION:
                        case VALID:
                            activity = new Intent(LoginActivity.this, LoginChoiceActivity.class);
                            startActivity(activity);
                            finish();
                            break;
                    }
                } catch (Exception e) {
                    DoctorVetApp.get().handle_onResponse_error(e, TAG, true, response);
                } finally {
                    if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.INVISIBLE);
                    mOverlayDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.INVISIBLE);
                mOverlayDialog.dismiss();
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
    private void googleFacebookSignIn() {
        User loginUser = new User();
        loginUser.setEmail(DoctorVetApp.get().preferences_getUserEmail());
        loginUser.setLogin_type(DoctorVetApp.get().preferences_getUserLoginType().name());

        final String login_user_json_object = MySqlGson.getPostJsonString(loginUser);

        URL userFacebookGoogleLoginUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.FACEBOOK_GOOGLE_PRE_AUTH, null, null, null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, userFacebookGoogleLoginUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String login_response_str = MySqlGson.getDataFromResponse(response).getAsString();
                    DoctorVetApp.login_response loginResponse = DoctorVetApp.login_response.valueOf(login_response_str);

                    Intent activity;
                    switch (loginResponse){
                        case ACCOUNT_WAITING_FOR_VET_ASSOCIATION:
                        case VALID:
                            activity = new Intent(LoginActivity.this, LoginChoiceActivity.class);
                            startActivity(activity);
                            finish();
                            break;
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
            }
        })
        {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return login_user_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void googleFacebookCreateAccount(User user) {
        if (pb_loading_indicator != null) pb_loading_indicator.setVisibility(View.VISIBLE);
        final Dialog mOverlayDialog = HelperClass.getOverlayDialog(LoginActivity.this);
        mOverlayDialog.show();

        final String json_user_object = MySqlGson.postGson().toJson(user);

        URL create_accountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.CREATE_ACCOUNT, null, null, null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, create_accountUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    DoctorVetApp.login_types login_type = DoctorVetApp.login_types.valueOf(user.getLogin_type());
                    DoctorVetApp.get().preferences_setLoginInfo(user.getEmail(), null, login_type,null);

                    Intent activity = new Intent(LoginActivity.this, LoginChoiceActivity.class);
                    startActivity(activity);
                    finish();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                } finally {
                    pb_loading_indicator.setVisibility(View.INVISIBLE);
                    mOverlayDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    String responseError = new String(error.networkResponse.data);
                    JsonElement message = MySqlGson.getGson().fromJson(responseError, JsonObject.class).get("message");
                    String login_response_str = message.getAsString();
                    DoctorVetApp.login_response loginResponse = DoctorVetApp.login_response.valueOf(login_response_str);
                    if (loginResponse == DoctorVetApp.login_response.ACCOUNT_EXISTS) {
                        String existing_db_login_type = MySqlGson.getDataFromResponse(responseError).getAsJsonObject().get("login_type").getAsString();
                        DoctorVetApp.login_types login_type = DoctorVetApp.login_types.valueOf(user.getLogin_type());

                        if (existing_db_login_type.equalsIgnoreCase(login_type.name())) {
                            DoctorVetApp.get().preferences_setLoginInfo(user.getEmail(), null, login_type,null);
                            Intent act = new Intent(LoginActivity.this, LoginChoiceActivity.class);
                            startActivity(act);
                            finish();
                        } else {
                            Snackbar.make(rootCordinatorLayout, "Login type error. Account exists", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_volley_error(error, TAG, true);
                    DoctorVetApp.get().handle_error(ex, TAG, true);
                } finally {
                    pb_loading_indicator.setVisibility(View.INVISIBLE);
                    mOverlayDialog.dismiss();
                }
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

    private void initGoogleSignIn() {
        if (oneTapClient != null) return;

        oneTapClient = Identity.getSignInClient(this);
        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.web_client_id))
                        // Show all accounts on the device.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }
    private void googleSignIn() {
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        IntentSenderRequest intentSenderRequest = new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build();
                        activityResultLauncher.launch(intentSenderRequest);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        DoctorVetApp.get().handle_error(e, TAG, true);
                    }
                });
    }
    ActivityResultLauncher<IntentSenderRequest> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                try {
                    SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());

                    final User login_user = new User();
                    login_user.setName(credential.getDisplayName());
                    login_user.setEmail(credential.getId());
                    login_user.setLogin_type(DoctorVetApp.login_types.GOOGLE.name());
                    login_user.setExternal_thumb_url(credential.getProfilePictureUri().toString());

                    googleFacebookCreateAccount(login_user);
                } catch (ApiException e) {
                    DoctorVetApp.get().handle_error(e, TAG, true);
                }
            }
        }
    });

    /*
    private void initFacebookSignIn() {
        if (facebookCallbackManager != null) return;

        facebookCallbackManager = CallbackManager.Factory.create();

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null && !accessToken.isExpired()) {
            Intent activity = new Intent(LoginActivity.this, LoginChoiceActivity.class);
            startActivity(activity);
            finish();
            return;
        }

        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        facebookDoRequest(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        DoctorVetApp.get().handle_error(exception, TAG, true);
                    }
                });
    }
    private void facebookSignIn() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email","public_profile"));
    }
    private void facebookDoRequest(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    final User login_user = new User();
                    login_user.setName(object.getString("name"));
                    login_user.setEmail(object.getString("email"));
                    login_user.setLogin_type(DoctorVetApp.login_types.FACEBOOK.name());
                    login_user.setExternal_thumb_url(object.getJSONObject("picture").getJSONObject("data").getString("url"));

                    googleFacebookCreateAccount(login_user);
                } catch (JSONException ex) {
                    Snackbar.make(rootCordinatorLayout, ex.getMessage(), Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,cover,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
    */

    private boolean validateEmail() {
        return HelperClass.validateEmail(txtEmail, false);
    }
    private boolean validatePassword() {
        return HelperClass.validateEmpty(txtPassword);
    }

}


