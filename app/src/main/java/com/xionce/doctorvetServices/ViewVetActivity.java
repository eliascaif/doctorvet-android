package com.xionce.doctorvetServices;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewVetActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener {

    private static final String TAG = "ViewVetActivity";
    private Vet vet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_vet);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle b = new Bundle();
                if (DoctorVetApp.get().userIsAdmin()) b.putString("veterinaria_especifico", "true");
                b.putString("communication_specific", "true");
                bottomSheet.setArguments(b);
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetDialog");
            }
        });

        TextView txt_subscription_exp = findViewById(R.id.txt_about_subscription_exp);
        txt_subscription_exp.setText("Si tu suscripción está vencida, no te preocupes, Doctor Vet funcionará normalmente y podrás acceder a todos los datos almacenados. La app no se bloquea. Con suscripción vencida no puedes ingresar información nueva, como nuevos " + DoctorVetApp.get().getPetNamingPlural().toLowerCase() + " o nuevos " + DoctorVetApp.get().getOwnerNamingPlural().toLowerCase() + ".");

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    @Override
    protected void refreshView() {
        showVet();
    }

    @Override
    protected void go_update() {
        Intent intent = new Intent(this, EditVetActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.VET_OBJ.name(), MySqlGson.getGson().toJson(vet));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    @Override
    protected void go_delete() {

    }

    @Override
    protected void on_update_complete(Intent data) {
        Intent act = new Intent(ViewVetActivity.this, LoginActivity.class);
        startActivity(act);
        finish();
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {

    }

    private void showVet() {
        showProgressBar();
        Integer id_vet = getIdVeterinaria();
        URL url = NetworkUtils.buildGetVetUrl(id_vet);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showActivityContainer();
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            vet = MySqlGson.getGson().fromJson(data, Vet.class);
                            setUI(vet);
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
        Vet vet = (Vet)object;

        toolbar_title.setText(vet.getName());
        toolbar_subtitle.setText(vet.getEmail());

        DoctorVetApp.ObjectToTextView(findViewById(R.id.linear_vet_data), vet, "txt_");
        TextView txt_subscription_until = findViewById(R.id.txt_subscription_until);
        txt_subscription_until.setText(HelperClass.getShortDate_inStr(vet.getSubscription_until(), ViewVetActivity.this));

        Glide.with(getApplicationContext()).load(R.drawable.ic_store_holo_dark).apply(RequestOptions.fitCenterTransform()).into(toolbar_image);
        super.setPhoto(vet.getThumb_url(), vet.getPhoto_url());

        findViewById(R.id.btn_extend_subscription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = NetworkUtils.buildGetExtendSuscriptionUrl(getIdVeterinaria(), vet.getRegion().getCountry());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linearVets = findViewById(R.id.linear_vet_data);
        DoctorVetApp.invisibilizeEmptyTextView(linearVets);
    }

    private Integer getIdVeterinaria() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.VET_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.BottomSheetButtonClicked buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case VET_UPDATE:
                go_update();
                break;
            case VET_EDIT_SCHEDULES:
                Intent intent = new Intent(this, ViewVetServicesSchedulesActivity.class);
                startActivity(intent);
                break;
            case VET_USERS:
                Intent intent_users = new Intent(this, ViewVetUsersActivity.class);
                startActivity(intent_users);
                break;
            case VET_CREATE_BRANCH:
                Intent intent_branch = new Intent(this, EditVetActivity.class);
                intent_branch.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, DoctorVetApp.REQUEST_CREATE_BRANCH_VET);
                startActivityForResult(intent_branch, DoctorVetApp.REQUEST_CREATE_BRANCH_VET);
                break;
            case VET_SELL_POINTS:
                Intent intent_sell_points = new Intent(this, ViewVetPointsActivity.class);
                startActivity(intent_sell_points);
                break;
            case VET_DEPOSITS:
                Intent intent_deposits = new Intent(this, ViewVetDepositsActivity.class);
                startActivity(intent_deposits);
                break;
            case COMUNICATION_CALL:
                sendCall(vet.getPhone());
                break;
            case COMUNICATION_WHATSAPP:
                sendWhatsAppMessage(vet.getPhone());
                break;
            case COMUNICATION_EMAIL:
                try {
                    HelperClass.sendEmail(ViewVetActivity.this, vet.getEmail());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                }
                break;
            case COMUNICATION_SMS:
                try {
                    HelperClass.sendSMS(ViewVetActivity.this, vet.getPhone());
                } catch (HelperClass.EnterPhoneException ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(this), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                }
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewVetActivity.this);
        }
    }

}