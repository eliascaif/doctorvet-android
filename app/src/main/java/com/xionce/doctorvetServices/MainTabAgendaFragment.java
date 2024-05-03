package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.data.AgendaAdapter;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.utilities.DatePickerFragment;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainTabAgendaFragment extends FragmentBase
        implements HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "MainTabAgendaFragment";
    private AgendaAdapter agendaAdapter;
    private Button btnDate;
    private Socket socket;
    private TextView txtEmptyAgenda;
    //private PaginationRecyclerView recyclerView;
    //private SwipeRefreshLayout swipeRefreshLayout;

    public MainTabAgendaFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        socket = DoctorVetApp.get().getSocket();
        if (socket != null) {
            socket.on("server_message", serverMessage);
            Log.i(TAG, "Socket server_message listener on");
        }
    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_main_agenda, parent, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        setSwipeRefreshLayout(rootView);

        btnDate = rootView.findViewById(R.id.btn_date);

        txtEmptyAgenda = rootView.findViewById(R.id.txt_empty_agenda);

        recyclerView = rootView.findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment datePickerFragment = new DatePickerFragment(getSelectedDate());
                datePickerFragment.showDatePickerDialog(getActivity().getSupportFragmentManager(),
                        arg -> {
                            showAgenda(arg);
                            setSelectedDate(arg);
                        });
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        setSelectedDate(getSelectedDate());

        socket = DoctorVetApp.get().getSocket();
        if (socket != null) {
            socket.off("server_message", serverMessage);
            Log.i(TAG, "Socket server_message listener off");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void refreshView() {
        if (created) {
            showAgenda(getSelectedDate());
        }
    }

    private Date getSelectedDate() {
        Intent intent = getActivity().getIntent();
        Date selectedDate = new Date();
        if (intent.hasExtra(DoctorVetApp.INTENT_AGENDA_SELECTED_DATE)) {
            String selectedDate_in_string = intent.getStringExtra(DoctorVetApp.INTENT_AGENDA_SELECTED_DATE);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try {
                selectedDate = format.parse(selectedDate_in_string);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return selectedDate;
    }
    private void setSelectedDate(Date selectedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String selectedDate_in_string = dateFormat.format(selectedDate);
        getActivity().getIntent().putExtra(DoctorVetApp.INTENT_AGENDA_SELECTED_DATE, selectedDate_in_string);
    }

    private void showAgenda(Date selectedDate) {
        showProgressBar();
        recyclerView.setVisibility(View.INVISIBLE);

        btnDate.setText(HelperClass.getDateInLocaleLong(selectedDate));

        DoctorVetApp.get().getAgendaPagination(selectedDate, 1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Agenda> agenda = ((Agenda.Get_pagination_agendas) pagination).getContent();
                    agendaAdapter = new AgendaAdapter(agenda, AgendaAdapter.AgendaAdapterTypes.NORMAL);

                    if (agendaAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(agendaAdapter);
                        recyclerView.setVisibility(View.VISIBLE);
                        txtEmptyAgenda.setVisibility(View.GONE);

                        agendaAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                Agenda agenda_event = (Agenda) data;
                                Intent intent = new Intent(getContext(), ViewAgendaEventActivity.class);
                                intent.putExtra(DoctorVetApp.INTENT_VALUES.AGENDA_ID.name(), agenda_event.getId());
                                startActivity(intent);
                            }
                        });
                        agendaAdapter.setOnCheckClickHandler(new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                checkAgenda((Agenda)data, pos);
                            }
                        });

                        //pagination
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabAgendaFragment.this);
                    } else {
                        txtEmptyAgenda.setVisibility(View.VISIBLE);
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                } finally {
                    hideProgressBar();
                    hideSwipeRefreshLayoutProgressBar();
                }
            }
        });
    }

    private void checkAgenda(Agenda agenda, Integer pos) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer idAgenda = agenda.getId();
                URL delete_ownerUrl = NetworkUtils.buildCheckAgendaUrl(idAgenda);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, delete_ownerUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String success = MySqlGson.getStatusFromResponse(response);
                                    if (success.equalsIgnoreCase("success")) {
                                        refreshView();
                                    } else {
                                        DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_update)), TAG, true);
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
    private void deleteAgenda(Agenda agenda, Integer pos) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                final Integer idAgenda = agenda.getId();
                URL delete_ownerUrl = NetworkUtils.buildDeleteAgendaUrl(idAgenda);
                TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_ownerUrl.toString(),
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    String success = MySqlGson.getStatusFromResponse(response);
                                    if (success.equalsIgnoreCase("success")) {
                                        refreshView();
                                    } else {
                                        DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_update)), TAG, true);
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

    private Emitter.Listener serverMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!(isAdded() && isVisible() && getUserVisibleHint()))
                        return;

                    Log.i(TAG, args[0].toString());

                    JSONObject incomingData = null;
                    try {
                        incomingData = new JSONObject(args[0].toString());

                        String table_name = incomingData.getString("table_name");
                        String operation = incomingData.getString("operation");

                        if (table_name.equalsIgnoreCase("agenda")) {
                            refreshView();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

    @Override
    public void onPagination() {
        recyclerView.startLoading();
        showProgressBar();
        recyclerView.addPage();
        DoctorVetApp.get().getAgendaPagination(getSelectedDate(), recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                hideProgressBar();
                if (pagination != null) {
                    ArrayList<Agenda> agenda = ((Agenda.Get_pagination_agendas)pagination).getContent();
                    agendaAdapter.addItems(agenda);
                    recyclerView.finishLoading();
                } else {
                    DoctorVetApp.get().handle_null_adapter("agenda", TAG, true);
                    showErrorMessage();
                }
            }
        });
    }

}
