package com.xionce.doctorvetServices;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Cash_movement;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewCashMovementActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2, DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "ViewCashMovementActivit";
    private TextView txt_date;
    private TextView txt_user;
    private TextView txt_total;
    private TextView txt_type;
    private TextView txt_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_cash_movement);
        hideToolbarImage();
        hideSubtitle();

        txt_date = findViewById(R.id.txt_date);
        txt_user = findViewById(R.id.txt_user);
        txt_total = findViewById(R.id.txt_total);
        txt_type = findViewById(R.id.txt_type);
        txt_reason = findViewById(R.id.txt_reason);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewCashMovementActivity.this, "ViewCashMovementActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showCashMovement() {
        showProgressBar();
        Integer id_cash_movement = getIdCashMovement();
        DoctorVetApp.get().getCashMovement(id_cash_movement, new DoctorVetApp.VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultCashMovement) {
                try {
                    showActivityContainer();
                    if (resultCashMovement != null) {
                        setUI(resultCashMovement);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewCashMovementActivity.this,*/ TAG, true);
                    showErrorMessage();
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }
    @Override
    protected void setUI(Object object) {
        Cash_movement cashMovement = (Cash_movement) object;

        String title = "Movimiento manual";
        if (cashMovement.getDeleted() == 1)
            title += " ELIMINADO ";
        toolbar_title.setText(title);

        txt_date.setText("Fecha: " + HelperClass.getDateInLocale(cashMovement.getDate(), getContext()));

        txt_user.setText(getText(R.string.user) + ": " + cashMovement.getUser().getName());

        txt_total.setText("Total: " + HelperClass.formatCurrency(cashMovement.getAmount()));

        String type = "Tipo: ";
        if (cashMovement.getType() == Cash_movement.manual_cash_type.MANUAL_CASH_OUT)
            type += "Egreso";
        else if (cashMovement.getType() == Cash_movement.manual_cash_type.MANUAL_CASH_IN)
            type += "Ingreso";

        txt_type.setText(type);

        txt_reason.setVisibility(View.GONE);
        if (cashMovement.getReason() != null) {
            txt_reason.setText("Motivo: " + cashMovement.getReason());
            txt_reason.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showCashMovement();
    }

    @Override
    protected void go_update() {
    }

    @Override
    protected void go_delete() {
        HelperClass.getOKCancelDialog(this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer idCashMovement = getIdCashMovement();
                URL url = NetworkUtils.buildDeleteCashMovementUrl(idCashMovement);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, url.toString(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                String success = MySqlGson.getStatusFromResponse(response);
                                if (success.equalsIgnoreCase("success")) {
                                    refreshView();
                                } else {
                                    showErrorToast(getString(R.string.error_borrando_registro), TAG);
                                }
                            } catch (Exception ex) {
                                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                            } finally {
                                hideWaitDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DoctorVetApp.get().handle_volley_error(error, TAG, true);
                            hideWaitDialog();
                        }
                    }
                );
                DoctorVetApp.get().addToRequestQueque(stringRequest);
            }
        });
    }

    @Override
    protected void on_update_complete(Intent data) {
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

    private Integer getIdCashMovement() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.CASH_MOVEMENT_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case CASH_MOVEMENT_CANCEL:
                go_delete();
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewCashMovementActivity.this);
        }
    }

    @Override
    public Context getContext() {
        return ViewCashMovementActivity.this;
    }

}
