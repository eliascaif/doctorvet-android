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
import com.xionce.doctorvetServices.data.Spending;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewSpendingActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener, DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "ViewSpendingActivity";
    private TextView txt_date;
    private TextView txt_user;
    private TextView txt_total;
    private TextView txt_reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_spending);
        hideToolbarImage();
        hideSubtitle();

        txt_date = findViewById(R.id.txt_date);
        txt_user = findViewById(R.id.txt_user);
        txt_total = findViewById(R.id.txt_total);
        txt_reason = findViewById(R.id.txt_reason);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheet = new BottomSheetDialog();
                Bundle b = new Bundle();
                b.putString("spending_specific", "true");
                bottomSheet.setArguments(b);
                bottomSheet.show(getSupportFragmentManager(), "bottomSheetDialog");
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showSpending() {
        showProgressBar();
        Integer id_spending = getIdSpending();
        DoctorVetApp.get().getSpending(id_spending, new DoctorVetApp.VolleyCallbackObject() {
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
                    DoctorVetApp.get().handle_error(ex, /*ViewSpendingActivity.this,*/ TAG, true);
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
        Spending spending = (Spending) object;

        String title = "Gasto";
        if (spending.getDeleted() == 1)
            title += " ELIMINADO ";
        toolbar_title.setText(title);

        txt_date.setText("Fecha: " + HelperClass.getDateInLocale(spending.getDate(), getContext()));

        txt_user.setText(getText(R.string.user) + ": " + spending.getUser().getName());

        txt_total.setText("Total: " + HelperClass.formatCurrency(spending.getAmount()));

        txt_reason.setVisibility(View.GONE);
        if (spending.getReason() != null) {
            txt_reason.setText("Motivo: " + spending.getReason());
            txt_reason.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showSpending();
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
                final Integer idSpending = getIdSpending();
                URL deleteUrl = NetworkUtils.buildDeleteSpendingUrl(idSpending);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, deleteUrl.toString(),
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
                                DoctorVetApp.get().handle_onResponse_error(ex, /*ViewSpendingActivity.this,*/ TAG, true, response);
                            } finally {
                                hideWaitDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DoctorVetApp.get().handle_volley_error(error, /*ViewSpendingActivity.this,*/ TAG, true);
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

    private Integer getIdSpending() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.SPENDING_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.BottomSheetButtonClicked buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case SPENDING_CANCEL:
                go_delete();
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewSpendingActivity.this);
        }
    }

    @Override
    public Context getContext() {
        return ViewSpendingActivity.this;
    }

}
