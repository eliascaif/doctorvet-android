package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewUserActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2 {

    private static final String TAG = "ViewUserActivity";
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_user);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean userViewingHimself = getIdUser().equals(DoctorVetApp.get().getUser().getId());
                if (userViewingHimself) {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewUserActivity.this, "ViewUserActivity");
                    bottomSheetDialog.show(getSupportFragmentManager(), null);
                } else {
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewUserActivity.this, "ViewUserActivityOnlyComm");
                    bottomSheetDialog.show(getSupportFragmentManager(), null);
                }
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH)
            finish();
    }

    @Override
    protected void refreshView() {
        showUser();
    }

    @Override
    protected void go_update() {
        Intent intent = new Intent(this, EditUserActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name(), MySqlGson.getGson().toJson(user));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    @Override
    protected void go_delete() {

    }

    @Override
    protected void on_update_complete(Intent data) {
        Intent act = new Intent(ViewUserActivity.this, LoginActivity.class);
        startActivity(act);
        finish();
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {

    }

    private void showUser() {
        showProgressBar();

        Integer id_user = getIdUser();
        URL get_userUrl = NetworkUtils.buildGetUserUrl(id_user);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, get_userUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showActivityContainer();
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            user = MySqlGson.getGson().fromJson(data, User.class);
                            setUI(user);
                            setLoadedFinished();
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                            showErrorMessage();
                        } finally {
                            hideProgressBar();
                            hideSwipeRefreshLayoutProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        hideProgressBar();
                        showErrorMessage();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    @Override
    protected void setUI(Object object) {
        User user = (User) object;

        toolbar_title.setText(user.getName());
        toolbar_subtitle.setText(user.getEmail());

        DoctorVetApp.ObjectToTextView(findViewById(R.id.linear_user_data), user, "txt_");

        Glide.with(getApplicationContext()).load(R.drawable.ic_account_circle_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);
        super.setPhoto(user.getThumb_url(), user.getPhoto_url());

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linearUsers = findViewById(R.id.linear_user_data);
        DoctorVetApp.setTextViewVisibility(linearUsers);
    }

    private Integer getIdUser() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.USER_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case USER_UPDATE:
                go_update();
                break;
            case USER_DELETE:
                go_delete();
                break;
            case USER_CHANGE_PASSWORD:
                HelperClass.getOKCancelDialog(ViewUserActivity.this, "¿Seguro deseas cambiar contraseña?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(ViewUserActivity.this, EditUserChangePassword.class);
                        startActivityForResult(intent, 1);
                    }
                });
                break;
            case USER_LOGOUT:
                HelperClass.getOKCancelDialog(this, "¿Seguro deseas cerrar sesión?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //check facebookaccount
//                        if (user.getLogin_type().equalsIgnoreCase("facebook"))
//                            LoginManager.getInstance().logOut();

                        logOut();
                    }
                });
                break;
            case COMUNICATION_CALL:
                sendCall(user.getPhone());
                break;
            case COMUNICATION_WHATSAPP:
                sendWhatsAppMessage(user.getPhone());
                break;
            case COMUNICATION_EMAIL:
                try {
                    HelperClass.sendEmail(ViewUserActivity.this, user.getEmail());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case COMUNICATION_SMS:
                try {
                    HelperClass.sendSMS(ViewUserActivity.this, user.getPhone());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewUserActivity.this);
        }

    }

    private void logOut() {
        DoctorVetApp.get().logout(new DoctorVetApp.VolleyCallback() {
            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    DoctorVetApp.get().deleteAllCacheFiles();
                    Intent intent = new Intent(ViewUserActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Snackbar.make(DoctorVetApp.getRootForSnack(ViewUserActivity.this), "No es posible realizar logout, intenta nuevamente", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logOutFromGoogleAccount() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient gsc = GoogleSignIn.getClient(ViewUserActivity.this, gso);
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                logOut();
            }
        });
    }
}
