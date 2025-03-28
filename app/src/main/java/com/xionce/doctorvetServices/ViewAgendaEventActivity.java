package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class ViewAgendaEventActivity extends ViewBaseActivity implements BottomSheetDialog.BottomSheetListener2 {

    private static final String TAG = "ViewAgendaEventActivity";
    private Agenda agenda_event = null;
    private TextView txt_estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view_agenda_event);
        txt_estado = findViewById(R.id.txt_estado);
        hideToolbarImage();
        toolbar_subtitle.setVisibility(View.GONE);
        hideActivityContainer();
        setSwipeRefreshLayout();

        fab = findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ViewAgendaEventActivity.this, "ViewAgendaEventActivity");
                bottomSheetDialog.show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    protected void refreshView() {
        showAgendaEvent();
    }

    @Override
    protected void go_update() {
        if (agenda_event.getExecuted() == 1) {
            Snackbar.make(DoctorVetApp.getRootForSnack(ViewAgendaEventActivity.this), "Agenda realizada, no es posible modificar", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, EditAgendaActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.AGENDA_OBJ.name(), MySqlGson.getGson().toJson(agenda_event));
        startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
    }

    @Override
    protected void go_delete() {
        HelperClass.getOKCancelDialog(this, getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();

                final Integer id_agenda = getIdAgendaEvent();
                DoctorVetApp.get().deleteAgenda(id_agenda, new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            on_delete_complete(id_agenda);
                            finish();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(ViewAgendaEventActivity.this), R.string.error_borrando_registro, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void on_update_complete(Intent data) {
    }

    @Override
    protected void on_delete_complete(Integer deleted_id) {
    }

    private void showAgendaEvent() {
        showProgressBar();
        Integer id_agenda_event = getIdAgendaEvent();
        URL get_agendaEventUrl = NetworkUtils.buildGetAgendaEventUrl(id_agenda_event);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, get_agendaEventUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            showActivityContainer();
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            agenda_event = MySqlGson.getGson().fromJson(data, Agenda.class);
                            setUI(agenda_event);
                            setLoadedFinished();
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, /*ViewAgendaEventActivity.this,*/ TAG, true, response);
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
                        DoctorVetApp.get().handle_volley_error(error, /*ViewAgendaEventActivity.this,*/ TAG, true);
                        hideProgressBar();
                        showErrorMessage();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void setUI(Object object) {
        Agenda agenda = (Agenda) object;

        toolbar_title.setText(agenda.getEvent_name());

        DoctorVetApp.ObjectToTextView(findViewById(R.id.lista_datos_agenda), agenda, "txt_");

        CheckBox chk_private_task = findViewById(R.id.chk_private_task);
        if (agenda.getPrivate_task() != null && agenda.getPrivate_task() == 1)
            chk_private_task.setChecked(true);
        else
            chk_private_task.setVisibility(View.GONE);

        if (agenda.getExecuted() == 1)
            txt_estado.setText(R.string.estado_realizado);
        else
            txt_estado.setText(R.string.estado_no_realizado);

        TextView txtUserName = findViewById(R.id.txt_name_user);
        txtUserName.setText(agenda.getUser().getName());

        invisibilizeEmptyViews();
    }

    @Override
    protected void invisibilizeEmptyViews() {
        LinearLayout linearOwners = findViewById(R.id.lista_datos_agenda);
        DoctorVetApp.setTextViewVisibility(linearOwners);
    }

    private Integer getIdAgendaEvent() {
        return getIntent().getIntExtra(DoctorVetApp.INTENT_VALUES.AGENDA_ID.name(), 0);
    }

    @Override
    public void onButtonClicked(BottomSheetDialog.Buttons buttonClicked) {
        if (agenda_event == null) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_cargando_registro, Snackbar.LENGTH_SHORT).show();
            return;
        }

        switch (buttonClicked) {
            case AGENDA_UPDATE:
                go_update();
                break;
            case AGENDA_DELETE:
                go_delete();
                break;
            case AGENDA_CHECK:
                check_agenda();
                break;
            default:
                DoctorVetApp.get().handleGeneralBottomSheetClick(buttonClicked, ViewAgendaEventActivity.this);
        }
    }

    private void check_agenda() {
        if (agenda_event.getExecuted() == 1) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Agenda ya realizada", Snackbar.LENGTH_SHORT).show();
            return;
        }

        HelperClass.getOKCancelDialog(this, getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                DoctorVetApp.get().checkAgenda(agenda_event.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            agenda_event.setExecuted(1);
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(ViewAgendaEventActivity.this), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

}