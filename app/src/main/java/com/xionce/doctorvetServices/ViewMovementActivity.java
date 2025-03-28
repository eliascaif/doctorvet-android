package com.xionce.doctorvetServices;

import static android.view.View.GONE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.Movement_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewMovementActivity extends ViewBaseActivity
        implements BottomSheetDialog.BottomSheetListener2, DoctorVetApp.IProgressBarActivity {

    private static final String TAG = "ViewMovementsActivity";
    private TextView txt_date;
    private TextView txt_deposit_origin;
    private TextView txt_deposit_destination;
    private TextView txt_type;
    private TextView txt_receipt;
    private RecyclerView recycler_products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_movements);
        hideToolbarImage();
        hideSubtitle();

        txt_date = findViewById(R.id.txt_date);
        txt_deposit_origin = findViewById(R.id.txt_deposit_origin);
        txt_deposit_destination = findViewById(R.id.txt_deposit_destination);
        txt_type = findViewById(R.id.txt_type);
        txt_receipt = findViewById(R.id.txt_receipt);

        recycler_products = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ViewMovementActivity.this);
        recycler_products.setLayoutManager(layoutManager);
        recycler_products.setHasFixedSize(true);
        recycler_products.setVisibility(GONE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewMovementActivity.this, "ViewMovementActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });

        hideActivityContainer();
        setSwipeRefreshLayout();
    }

    private void showMovement() {
        showProgressBar();
        Integer idMovement = getIdMovement();
        DoctorVetApp.get().getMovement(idMovement, new DoctorVetApp.VolleyCallbackMovement() {
            @Override
            public void onSuccess(Movement resultMovement) {
                try {
                    showActivityContainer();
                    if (resultMovement != null) {
                        setUI(resultMovement);
                        setLoadedFinished();
                    } else {
                        showErrorMessage();
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, /*ViewMovementActivity.this,*/ TAG, true);
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
        Movement movement = (Movement) object;

        String title = "Remito";
        if (movement.getDeleted() == 1)
            title += " ELIMINADO ";
        toolbar_title.setText(title);

        txt_date.setText("Fecha: " + HelperClass.getDateInLocale(movement.getDate(), getContext()));
        txt_deposit_origin.setText("Origen: " + movement.getOrigin_deposit().getCompleteName());
        txt_deposit_destination.setText("Destino: " + movement.getDestination_deposit().getCompleteName());

        Movement_itemAdapter items_adapter = new Movement_itemAdapter(movement.getItems(), Movement_itemAdapter.MovementAdapter_types.SHOW);
        recycler_products.setAdapter(items_adapter);
        if (items_adapter.getItemCount() > 0)
            recycler_products.setVisibility(View.VISIBLE);

        String type = "Procesado";
        if (movement.getType().equalsIgnoreCase("IN_TRANSIT")) {
            type = "En tránsito";
        }
        type = "Estado: " + type;
        txt_type.setText(type);
        txt_receipt.setText("Comprobante: " + movement.getReceipt());
    }

    @Override
    protected void invisibilizeEmptyViews() {

    }

    @Override
    protected void refreshView() {
        showMovement();
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
                final Integer idMovement = getIdMovement();
                URL deleteMovementUrl = NetworkUtils.buildMovementsUrl(null, idMovement, null);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, deleteMovementUrl.toString(),
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
                                DoctorVetApp.get().handle_onResponse_error(ex, /*ViewMovementActivity.this,*/ TAG, true, response);
                            } finally {
                                hideWaitDialog();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DoctorVetApp.get().handle_volley_error(error, /*ViewMovementActivity.this,*/ TAG, true);
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

    private Integer getIdMovement() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (!loadedFinished) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case MOVEMENT_CANCEL:
                go_delete();
                break;
            case MOVEMENT_ACCEPT:
                accept_movement();
                break;
            case MOVEMENT_PDF:
                get_movement_pdf();
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewMovementActivity.this);
        }
    }

    private void accept_movement() {
        Integer idMovement = getIdMovement();
        showWaitDialog();
        DoctorVetApp.get().accept_movement(idMovement, new DoctorVetApp.VolleyCallback() {
            @Override
            public void onSuccess(Boolean result) {
                hideWaitDialog();
                if (result) {
                    showErrorToast("Remito procesado y aceptado", TAG);
                    refreshView();
                } else {
                    showErrorToast("El remito no se pudo procesar", TAG);
                }
            }
        });
    }

    private void get_movement_pdf() {
        Integer idMovement = getIdMovement();

        showWaitDialog();

        URL pdfMovementEndPoint = NetworkUtils.buildMovementsUrl(null, idMovement, true);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, pdfMovementEndPoint.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    HelperClass.writeToFile(response, ViewMovementActivity.this, "movement.pdf");
                    boolean success = HelperClass.viewPdf("movement.pdf", ViewMovementActivity.this);
                    if (!success)
                        Snackbar.make(DoctorVetApp.getRootForSnack(ViewMovementActivity.this), getString(R.string.err_pdf_viewer), Snackbar.LENGTH_SHORT).show();
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, /*ViewMovementActivity.this,*/ TAG, true, response);
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, /*ViewMovementActivity.this,*/ TAG, true);
                hideWaitDialog();
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    public Context getContext() {
        return ViewMovementActivity.this;
    }

}
