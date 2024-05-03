package com.xionce.doctorvetServices;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.data.AgendaAdapter;
import com.xionce.doctorvetServices.data.CashMovementsAdapter;
import com.xionce.doctorvetServices.data.Cash_movement;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Log;
import com.xionce.doctorvetServices.data.LogsAdapter;
import com.xionce.doctorvetServices.data.LowFood;
import com.xionce.doctorvetServices.data.LowFoodAdapter;
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.MovementsAdapter;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.OwnersAdapter;
import com.xionce.doctorvetServices.data.OwnersNotificationsAdapter;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.data.Pet_supplyAdapter;
import com.xionce.doctorvetServices.data.PetsAdapter;
import com.xionce.doctorvetServices.data.OwnerNotification;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.Product_providersAdapter;
import com.xionce.doctorvetServices.data.Product_traceability;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.data.ProductsTraceabilityAdapter;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.PurchasesAdapter;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.SellsAdapter;
import com.xionce.doctorvetServices.data.Spending;
import com.xionce.doctorvetServices.data.SpendingsAdapter;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.UsersAdapter;
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.data.VetsDepositsAdapter;
import com.xionce.doctorvetServices.data.WaitingRoomsAdapter;
import com.xionce.doctorvetServices.data.Waiting_room;
import com.xionce.doctorvetServices.data.X_requests_users_vets;
import com.xionce.doctorvetServices.data.X_requests_users_vets_adminAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public class MainTabReportsFragment extends FragmentBase
        implements AdapterView.OnItemSelectedListener, HelperClass.RecyclerOnPaginationHandler {

    private static final String TAG = "MainTabReportsFragment";
    private Spinner spinner;
    private TextView txtInfo;

    //extensions
    private ConstraintLayout extensions_container;
    private ConstraintLayout from_to;
    private ConstraintLayout traceability;
    private TextInputLayout txt_from;
    private TextInputLayout txt_to;
    private LinearLayout linear_totals;
    private TextView txt_total;
    private TextView txt_balance;
    private AutoCompleteTextView actvProducto;
    private Spinner spinner_deposits;

    private ImageView img_go;
    private ArrayAdapter<DoctorVetApp.reports> spinnerAdapter;
    private DoctorVetApp.reports selectedReport;

    private PetsAdapter petsBirthdaysAdapter;
    private AgendaAdapter agendaAdapter;
    private Pet_supplyAdapter supplyAdapter;

    private OwnersNotificationsAdapter ownersNotificationsAdapter;

    private OwnersAdapter ownersDebtorsAdapter;
    private OwnersAdapter ownersDebtorsToleranceAdapter;
    private Product_providersAdapter providersCreditorsAdapter;
    private Product_providersAdapter providersCreditorsToleranceAdapter;

    private MovementsAdapter inTransitMovementsAdapter;

    private ProductsAdapter productsBelowMinimunAdapter;

    private PetsAdapter petsLifeExpectancyAdapter;

    private UsersAdapter newVetUsersAdapter;

    private X_requests_users_vets_adminAdapter usersRequestsAdapter;

    private WaitingRoomsAdapter waitingRoomsAdapter;

    private WaitingRoomsAdapter waitingRoomsAutoDeletedAdapter;

    private SellsAdapter sellsAdapter;

    private PurchasesAdapter purchasesAdapter;

    private SpendingsAdapter spendingsAdapter;

    private CashMovementsAdapter cashMovementsAdapter;

    private MovementsAdapter movementsAdapter;

    private ProductsTraceabilityAdapter traceabilityAdapter;

    private LogsAdapter logsAdapter;

    private LowFoodAdapter lowFoodAdapter;

    public MainTabReportsFragment() {

    }

    @Override
    public View provideYourFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_main_reports, parent, false);

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_container);
        setSwipeRefreshLayout(rootView);

        recyclerView = rootView.findViewById(R.id.recyclerview_reportes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        spinner = rootView.findViewById(R.id.spinner_reportes);
        spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, DoctorVetApp.reports.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1); // .simple_expandable_list_item_1);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        txtInfo = rootView.findViewById(R.id.txt_info);

        img_go = rootView.findViewById(R.id.img_go);
        img_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showReport();
            }
        });

        //extensions
        extensions_container = rootView.findViewById(R.id.container_constraint);
        from_to = rootView.findViewById(R.id.constraint_from_to);
        setFromTo(rootView);
        traceability = rootView.findViewById(R.id.constraint_traceability);
        setTraceability(rootView);

        return rootView;
    }

    @Override
    public void onViewStateRestored(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            if (txt_from != null && txt_from.getEditText() != null)
                txt_from.getEditText().setText(savedInstanceState.getString("from"));

            if (txt_to != null && txt_to.getEditText() != null)
                txt_to.getEditText().setText(savedInstanceState.getString("to"));
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if (txt_from != null && txt_from.getEditText() != null)
            outState.putString("from", txt_from.getEditText().getText().toString());

        if (txt_to != null && txt_to.getEditText() != null)
            outState.putString("to", txt_to.getEditText().getText().toString());
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    @Override
    public void refreshView() {
        if (created) {

            String selectedReportStr = DoctorVetApp.get().getPreference("selectedReport");
            DoctorVetApp.reports auxSelectedReport = DoctorVetApp.reports.getEnumVal(selectedReportStr);
            if (
                auxSelectedReport == DoctorVetApp.reports.SERVICES_BARCODES
                || auxSelectedReport == DoctorVetApp.reports.PRODUCTS_EXCEL
                )
                    return;

            hideRecyclerView();
            showReport();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        String report = adapterView.getItemAtPosition(pos).toString();
        selectedReport = DoctorVetApp.reports.getEnumVal(report);
        DoctorVetApp.get().setPreference("selectedReport", report);

        recyclerView.setVisibility(View.INVISIBLE);
        txtInfo.setVisibility(View.GONE);
        txtInfo.setText("");
        extensions_container.setVisibility(View.GONE);

        showReport();
    }
    public void setReport(DoctorVetApp.reports report) {
        hideRecyclerView();
        int spinnerPosition = spinnerAdapter.getPosition(report);
        spinner.setSelection(spinnerPosition);
    }

    private void showReport() {
        if (selectedReport == null)
            return;

        clearRecyclerView();

        switch (selectedReport) {
            case PENDING_AGENDA_VET:
            case PENDING_AGENDA_USER:
            case EXPIRED_AGENDA_VET:
            case EXPIRED_AGENDA_USER:
            case EXPIRED_AGENDA_ALL_VET:
            case TOMORROW_AGENDA_VET:
            case TOMORROW_AGENDA_USER:
                showAgendaReport();
                break;

            case BIRTHDAY_PETS:
                showBirthdaysReport();
                break;

            case PENDING_SUPPLY_VET:
            case PENDING_SUPPLY_USER:
            case EXPIRED_SUPPLY_VET:
            case EXPIRED_SUPPLY_USER:
            case DOMICILIARY_PENDING_SUPPLY_VET:
            case DOMICILIARY_EXPIRED_SUPPLY_VET:
            case EXPIRED_SUPPLY_ALL_VET:
            case TOMORROW_SUPPLY_VET:
            case TOMORROW_SUPPLY_USER:
                showPendingExpiredSupplyReport();
                break;

            case OWNERS_NOTIFICATIONS:
                showOwnersNotificationsReport();
                break;

            case DEBTORS:
                showOwnersDebtorsReport();
                break;
            case DEBTORS_TOLERANCE:
                showOwnersDebtorsToleranceReport();
                break;

            case CREDITORS:
                showProvidersCreditorsReport();
                break;
            case CREDITORS_TOLERANCE:
                showProvidersCreditorsToleranceReport();
                break;

            case IN_TRANSIT_MOVEMENTS:
                showInTransitMovementsReport();
                break;

            case PRODUCTS_BELOW_MINIMUN:
                showProductsBelowMinimunReport();
                break;

            case PRODUCTS_TRACEABILITY:
                showProductsTraceabilityReport();
                break;

            case LIFE_EXPECTANCY:
                showLifeExpectancyReport();
                break;

            case NEW_VET_USERS:
                showNewVetUsersReport();
                break;

            case USER_JOIN_REQUEST:
                showUsersJoinRequestReport();
                break;

            case WAITING_ROOM:
                showWaitingRoomsReport();
                break;

            case WAITING_ROOM_AUTO_DELETED:
                showWaitingRoomsAutoDeletedReport();
                break;

            case SELLS:
                showSellsReport();
                break;

            case PURCHASES:
                showPurchasesReport();
                break;

            case SPENDINGS:
                showSpendingsReport();
                break;

            case CASH_MOVEMENTS:
                showCashMovementsReport();
                break;

            case MOVEMENTS:
                showMovementsReport();
                break;

            case SUPPLY:
                showSupplyReport();
                break;

            case AGENDA:
                showAgendaReport2();
                break;

            case PRODUCTS_EXCEL:
                showProductsExcel();
                break;

            case SERVICES_BARCODES:
                showServicesBarcodes();
                break;

            case LOGS:
                showLogsReport();
                break;

            case LOW_FOOD:
                showLowFoodReport();
                break;
        }
    }
    private void showInfo(DoctorVetApp.reports selectedReport) {
        txtInfo.setVisibility(View.VISIBLE);
        switch (selectedReport) {
            case PENDING_AGENDA_VET:
                txtInfo.setText("Citas y/o tareas de agenda de hoy. De todos los usuarios.");
                break;
            case PENDING_AGENDA_USER:
                txtInfo.setText("Citas y/o tareas de agenda de hoy. Personales");
                break;
            case EXPIRED_AGENDA_VET:
                txtInfo.setText("Citas y/o tareas de agenda de ayer que no fueron marcadas como 'realizadas'. De todos los usuarios.");
                break;
            case EXPIRED_AGENDA_USER:
                txtInfo.setText("Citas y/o tareas de agenda de ayer que no fueron marcadas como 'realizadas'. Personales");
                break;
            case EXPIRED_AGENDA_ALL_VET:
                txtInfo.setText("Citas y/o tareas de agenda que no fueron marcadas como 'realizadas'. De todos los usuarios. Revísalas intentando reagendar/eliminar. (No se incluyen los vencimientos de ayer que tienen su propio reporte)");
                break;
            case TOMORROW_AGENDA_VET:
                txtInfo.setText("Citas y/o tareas agendadas para mañana. De todos los usuarios.");
                break;
            case TOMORROW_AGENDA_USER:
                txtInfo.setText("Citas y/o tareas agendadas para mañana. Personales.");
                break;

            case PENDING_SUPPLY_VET:
                txtInfo.setText("Productos y/o servicios a suministrar hoy. De todos los usuarios.");
                break;
            case PENDING_SUPPLY_USER:
                txtInfo.setText("Productos y/o servicios a suministrar hoy. Personales.");
                break;
            case EXPIRED_SUPPLY_VET:
                txtInfo.setText("Productos y/o servicios a suminstrar ayer (que no fueron suminstrados). De todos los usuarios.");
                break;
            case EXPIRED_SUPPLY_USER:
                txtInfo.setText("Productos y/o servicios a suminstrar ayer (que no fueron suminstrados). Personales.");
                break;
            case DOMICILIARY_PENDING_SUPPLY_VET:
                txtInfo.setText("Productos y/o servicios domiciliarios a suministrar hoy. De todos los usuarios.");
                break;
            case DOMICILIARY_EXPIRED_SUPPLY_VET:
                txtInfo.setText("Productos y/o servicios domiciliarios a suministrar ayer (que no fueron suminstrados). De todos los usuarios.");
                break;
            case EXPIRED_SUPPLY_ALL_VET:
                txtInfo.setText("Productos y/o servicios que no fueron suministrados. De todos los usuarios. Revísalos intentando suminstrar/reagendar/eliminar. (No se incluyen los vencimientos de ayer que tienen su propio reporte)");
                break;
            case TOMORROW_SUPPLY_VET:
                txtInfo.setText("Productos y/o servicios a suministrar mañana. De todos los usuarios.");
                break;
            case TOMORROW_SUPPLY_USER:
                txtInfo.setText("Productos y/o servicios a suministrar mañana. Personales.");
                break;

            case LIFE_EXPECTANCY:
                txtInfo.setText(DoctorVetApp.get().getPetNamingPlural()  + " que han superado el máximo de espectativa de vida de su raza. Puedes marcarlos con 'deceso' a fin de evitar que se les envíen notificaciones de cualquier tipo.");
                break;

            case NEW_VET_USERS:
                txtInfo.setText("Usuarios nuevos que se unen hoy.");
                break;

            case USER_JOIN_REQUEST:
                txtInfo.setText("Usuarios que quieren unirse al establecimiento. Acepta solicitudes de usuarios que conozcas y que trabajen contigo. Rechaza solicitudes de usuarios desconocidos.");
                break;

            case WAITING_ROOM:
                txtInfo.setText(DoctorVetApp.get().getPetNamingPlural() + " que están en sala de espera.");
                break;
            case WAITING_ROOM_AUTO_DELETED:
                txtInfo.setText(DoctorVetApp.get().getPetNamingPlural() + " que ingresaron a sala de espera ayer y que no fueron marcados como atendidos.");
                break;

            case PRODUCTS_EXCEL:
                txtInfo.setText("Exportación de productos a formato Excel");
                break;

            case SERVICES_BARCODES:
                txtInfo.setText("PDF para imprimir códigos de barras para servicios");
                break;

            case OWNERS_NOTIFICATIONS:
                txtInfo.setText("Notificaciones a " + DoctorVetApp.get().getOwnerNamingPlural() + ". Se envían cada día a las 9 hs.");
                break;

            case LOW_FOOD:
                txtInfo.setText(DoctorVetApp.get().getPetNamingPlural() + " con niveles bajos de alimento / pienso. Se computan ventas de los últimos 60 días para " + DoctorVetApp.get().getPetNamingPlural() + " que tienen registrado peso y productos de categorias de alimentos / pienso que utilizan unidades complejas (u/kg por ejemplo)." );
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void clearRecyclerView() {
        recyclerView.setAdapter(null);
    }

    public void onFromSearchClick(View view) {
        clearRecyclerView();
        clearTotalAndBalance();
        DoctorVetApp.get().searchDateSetInit(txt_from, getActivity().getSupportFragmentManager());
    }
    public void onToSearchClick(View view) {
        clearRecyclerView();
        clearTotalAndBalance();
        DoctorVetApp.get().searchDateSetInit(txt_to, getActivity().getSupportFragmentManager());
    }
    private void clearTotalAndBalance() {
        txt_total.setText("");
        txt_balance.setText("");
    }

    private void showAgendaReport() {
        showProgressBar();
        showInfo(selectedReport);

        getAgendaPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Agenda> agenda = ((Agenda.Get_pagination_agendas)pagination).getContent();
                    agendaAdapter = new AgendaAdapter(agenda, AgendaAdapter.AgendaAdapterTypes.NORMAL);

                    if (agendaAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(agendaAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        agendaAdapter.setOnClickHandler(MainTabReportsFragment.this::onAgendaClick);
                        agendaAdapter.setOnCheckClickHandler(MainTabReportsFragment.this::onAgendaCheckClick);
//                        agendaAdapter.setOnRemoveClickHandler(MainTabReportsFragment.this::onAgendaRemoveClick);

                        showRecyclerView();
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
    private void getAgendaPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL productosUrl = NetworkUtils.buildGetReportsUrl(selectedReport, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Agenda.Get_pagination_agendas response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Agenda.Get_pagination_agendas>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onAgendaClick(Object data, View view, int pos) {
        Agenda agenda_event = (Agenda) data;
        Intent intent = new Intent(getContext(), ViewAgendaEventActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.AGENDA_ID.name(), agenda_event.getId());
        startActivity(intent);
    }
    public void onAgendaCheckClick(Object data, View view, int pos) {
        Agenda agendaObj = (Agenda) data;
        checkAgenda(agendaObj);
    }
    private void checkAgenda(Agenda agenda_event) {
        if (agenda_event.getExecuted() == 1) {
            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "La cita/tarea ya está realizada", Snackbar.LENGTH_SHORT).show();
            return;
        }

        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog();

                DoctorVetApp.get().checkAgenda(agenda_event.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showBirthdaysReport() {
        showProgressBar();

        getBirthdaysPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                    petsBirthdaysAdapter = new PetsAdapter(pets, DoctorVetApp.Adapter_types.COMPACT);

                    if (petsBirthdaysAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(petsBirthdaysAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        petsBirthdaysAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                Pet pet = (Pet) data;
                                Intent activity = new Intent(getActivity(), ViewPetActivity.class);
                                activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet.getId());
                                startActivity(activity);
                            }
                        });

                        showRecyclerView();
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
    private void getBirthdaysPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL productosUrl = NetworkUtils.buildGetReportsUrl(selectedReport, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet.Get_pagination_pets response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet.Get_pagination_pets>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showPendingExpiredSupplyReport() {
        showProgressBar();
        showInfo(selectedReport);

        getPendingExpiredSupplyPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Pet_supply> petsSupply = ((Pet_supply.Get_pagination_supply)pagination).getContent();
                    supplyAdapter = new Pet_supplyAdapter(petsSupply, Pet_supplyAdapter.Pet_supplyAdapter_types.PENDING_COMM);

                    if (supplyAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(supplyAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        supplyAdapter.setOnClickHandler(MainTabReportsFragment.this::onSupplyClick);
                        supplyAdapter.setOnCheckItemHandler(MainTabReportsFragment.this::onSupplyCheck);
                        supplyAdapter.setOnRemoveItemHandler(MainTabReportsFragment.this::onSupplyRemove);
                        HelperClass.AdapterOnClickHandler callClick = new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                try {
                                    HelperClass.makePhoneCall(getActivity(), ((Pet_supply)data).getPet().getFirstPrincipalOwner().getPhone());
                                } catch (Exception ex) {
                                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        };
                        HelperClass.AdapterOnClickHandler whatsappClick = new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                try {
                                    HelperClass.sendWhatsAppMessage(getActivity(), ((Pet_supply)data).getPet().getFirstPrincipalOwner().getPhone(), "");
                                } catch (HelperClass.NoWhatsAppException ex) {
                                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_falta_whatsapp), Snackbar.LENGTH_SHORT).show();
                                } catch (Exception ex) {
                                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        };
                        HelperClass.AdapterOnClickHandler emailClick = new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                try {
                                    HelperClass.sendEmail(getActivity(), ((Pet_supply)data).getPet().getFirstPrincipalOwner().getEmail());
                                } catch (Exception ex) {
                                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_falta_email), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        };
                        HelperClass.AdapterOnClickHandler smsClick = new HelperClass.AdapterOnClickHandler() {
                            @Override
                            public void onClick(Object data, View view, int pos) {
                                try {
                                    HelperClass.sendSMS(getActivity(), ((Pet_supply)data).getPet().getFirstPrincipalOwner().getPhone());
                                } catch (Exception ex) {
                                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_falta_telefono), Snackbar.LENGTH_SHORT).show();
                                }
                            }
                        };
                        supplyAdapter.setComHandlers(callClick, whatsappClick, emailClick, smsClick);

                        showRecyclerView();
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
    private void getPendingExpiredSupplyPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL supplyUrl = NetworkUtils.buildGetReportsUrl(selectedReport, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, supplyUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet_supply.Get_pagination_supply response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_supply.Get_pagination_supply>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void onSupplyClick(Object data, View view, int pos) {
        Pet_supply pet_supply = (Pet_supply) data;
        Intent activity = new Intent(getActivity(), ViewPetActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet_supply.getPet().getId());
        startActivity(activity);
    }
    private void onSupplyCheck(Object data, View view, int pos) {
        Pet_supply pet_supply = (Pet_supply) data;
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressBar();
                DoctorVetApp.get().checkSupply(pet_supply.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideProgressBar();
                        if (result) {
                            supplyAdapter.deleteItem(pos);
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "El registro no pudo ser marcado como suministrado", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private boolean onSupplyRemove(Object data, View view, int pos) {
        final boolean[] success = {false};
        Pet_supply pet_supply = (Pet_supply) data;
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressBar();
                DoctorVetApp.get().deletePet_supply(pet_supply.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        success[0] = result;
                        hideProgressBar();
                        if (result) {
                            supplyAdapter.deleteItem(pos);
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "El registro no pudo ser eliminado", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return success[0];
    }
    private void showSupplyReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(false);

        getSupplyPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Pet_supply> supplies = ((Pet_supply.Get_pagination_supply)pagination).getContent();
                    supplyAdapter = new Pet_supplyAdapter(supplies, Pet_supplyAdapter.Pet_supplyAdapter_types.EDIT_SUPPLY);

                    if (supplyAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(supplyAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        supplyAdapter.setOnClickHandler(MainTabReportsFragment.this::onSupplyClick);
                        supplyAdapter.setOnCheckItemHandler(MainTabReportsFragment.this::onSupplyCheck);
                        supplyAdapter.setOnRemoveItemHandler(MainTabReportsFragment.this::onSupplyRemove);

                        showRecyclerView();
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
    private void getSupplyPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.SUPPLY, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet_supply.Get_pagination_supply response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_supply.Get_pagination_supply>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showOwnersNotificationsReport() {
        showProgressBar();
        showInfo(selectedReport);

        getOwnersNotificationsPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<OwnerNotification> ownerNotifications = ((OwnerNotification.Get_pagination_owner_notif)pagination).getContent();
                    ownersNotificationsAdapter = new OwnersNotificationsAdapter(ownerNotifications);

                    if (ownersNotificationsAdapter.getItemCount() != 0) {
                        ownersNotificationsAdapter.setOnClickHandler(MainTabReportsFragment.this::onNotificationsClick);

                        recyclerView.setAdapter(ownersNotificationsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getOwnersNotificationsPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL ownersNotifications = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.OWNERS_NOTIFICATIONS, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, ownersNotifications.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            OwnerNotification.Get_pagination_owner_notif response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<OwnerNotification.Get_pagination_owner_notif>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void onNotificationsClick(Object data, View view, int pos) {
        OwnerNotification ownerNotification = (OwnerNotification) data;
        Intent activity = new Intent(getActivity(), ViewOwnerNotificationActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_NOTIFICATION_ID.name(), ownerNotification.getId());
        startActivity(activity);
    }

    private void showOwnersDebtorsReport() {
        showProgressBar();

        getOwnersDebtorsPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Owner> ownersDebtors = ((Owner.Get_pagination_owners)pagination).getContent();
                    ownersDebtorsAdapter = new OwnersAdapter(ownersDebtors, DoctorVetApp.Adapter_types.OWNERS_DEBTORS);

                    if (ownersDebtorsAdapter.getItemCount() != 0) {
                        ownersDebtorsAdapter.setOnClickHandler(MainTabReportsFragment.this::onDebtorsClick);
                        ownersDebtorsAdapter.setOnDebtDetailsClickHandler(MainTabReportsFragment.this::onDebtorsDatailsClick);
                        ownersDebtorsAdapter.setOnDebtPayClickHandler(MainTabReportsFragment.this::onDebtorsPayClick);

                        recyclerView.setAdapter(ownersDebtorsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getOwnersDebtorsPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL ownersDebtors = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.DEBTORS, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, ownersDebtors.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Owner.Get_pagination_owners response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Owner.Get_pagination_owners>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void showOwnersDebtorsToleranceReport() {
        showProgressBar();

        getOwnersDebtorsTolerancePagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Owner> ownersDebtorsTolerance = ((Owner.Get_pagination_owners)pagination).getContent();
                    ownersDebtorsToleranceAdapter = new OwnersAdapter(ownersDebtorsTolerance, DoctorVetApp.Adapter_types.OWNERS_DEBTORS);

                    if (ownersDebtorsToleranceAdapter.getItemCount() != 0) {
                        ownersDebtorsToleranceAdapter.setOnClickHandler(MainTabReportsFragment.this::onDebtorsClick);
                        ownersDebtorsToleranceAdapter.setOnDebtDetailsClickHandler(MainTabReportsFragment.this::onDebtorsDatailsClick);
                        ownersDebtorsToleranceAdapter.setOnDebtPayClickHandler(MainTabReportsFragment.this::onDebtorsPayClick);

                        recyclerView.setAdapter(ownersDebtorsToleranceAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getOwnersDebtorsTolerancePagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL ownersDebtors = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.DEBTORS_TOLERANCE, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, ownersDebtors.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Owner.Get_pagination_owners response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Owner.Get_pagination_owners>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onDebtorsClick(Object data, View view, int pos) {
        Owner owner = (Owner) data;
        Intent activity = new Intent(getActivity(), ViewOwnerActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
        startActivity(activity);
    }
    public void onDebtorsDatailsClick(Object data, View view, int pos) {
        Owner owner = (Owner)data;
        Intent intent = new Intent(getContext(), ViewOwnerDebtsActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
        intent.putExtra("owner_name", owner.getName());
        startActivity(intent);
    }
    public void onDebtorsPayClick(Object data, View view, int pos) {
        Owner owner = (Owner)data;
        Intent intent = new Intent(getContext(), EditSellPaymentActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getPostJsonString(owner));
        intent.putExtra("debt_amount", owner.getBalance());
        startActivity(intent);
    }

    private void showProvidersCreditorsReport() {
        showProgressBar();

        getProvidersCreditorsPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Product_provider> providersCreditors = ((Product_provider.Get_pagination_providers)pagination).getContent();
                    providersCreditorsAdapter = new Product_providersAdapter(providersCreditors, DoctorVetApp.Adapter_types.PROVIDERS_CREDITORS);

                    if (providersCreditorsAdapter.getItemCount() != 0) {
                        providersCreditorsAdapter.setOnClickHandler(MainTabReportsFragment.this::onCreditorsClick);
                        providersCreditorsAdapter.setOnDebtDetailsClickHandler(MainTabReportsFragment.this::onCreditorsDatailsClick);
                        providersCreditorsAdapter.setOnDebtPayClickHandler(MainTabReportsFragment.this::onCreditorsPayClick);

                        recyclerView.setAdapter(providersCreditorsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getProvidersCreditorsPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL ownersDebtors = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.CREDITORS, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, ownersDebtors.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product_provider.Get_pagination_providers response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_provider.Get_pagination_providers>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void showProvidersCreditorsToleranceReport() {
        showProgressBar();

        getProvidersCreditorsTolernacePagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Product_provider> providersCreditors = ((Product_provider.Get_pagination_providers)pagination).getContent();
                    providersCreditorsToleranceAdapter = new Product_providersAdapter(providersCreditors, DoctorVetApp.Adapter_types.PROVIDERS_CREDITORS);

                    if (providersCreditorsToleranceAdapter.getItemCount() != 0) {
                        providersCreditorsToleranceAdapter.setOnClickHandler(MainTabReportsFragment.this::onCreditorsClick);
                        providersCreditorsToleranceAdapter.setOnDebtDetailsClickHandler(MainTabReportsFragment.this::onCreditorsDatailsClick);
                        providersCreditorsToleranceAdapter.setOnDebtPayClickHandler(MainTabReportsFragment.this::onCreditorsPayClick);

                        recyclerView.setAdapter(providersCreditorsToleranceAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getProvidersCreditorsTolernacePagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL ownersDebtors = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.CREDITORS_TOLERANCE, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, ownersDebtors.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product_provider.Get_pagination_providers response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_provider.Get_pagination_providers>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onCreditorsClick(Object data, View view, int pos) {
        Product_provider provider = (Product_provider)data;
        Intent activity = new Intent(getContext(), ViewProviderActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), provider.getId());
        startActivity(activity);
    }
    public void onCreditorsDatailsClick(Object data, View view, int pos) {
        Product_provider provider = (Product_provider)data;
        Intent intent = new Intent(getContext(), ViewProviderDebtsActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), provider.getId());
        intent.putExtra("provider_name", provider.getName());
        startActivity(intent);

    }
    public void onCreditorsPayClick(Object data, View view, int pos) {
        Product_provider provider = (Product_provider)data;
        Intent intent = new Intent(getContext(), EditPurchasePaymentActivity.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), MySqlGson.getPostJsonString(provider));
        intent.putExtra("debt_amount", provider.getBalance());
        startActivity(intent);

    }

    private void showInTransitMovementsReport() {
        showProgressBar();

        getInTransitMovementsPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Movement> inTransitMovements = ((Movement.Get_pagination_movements)pagination).getContent();
                    inTransitMovementsAdapter = new MovementsAdapter(inTransitMovements);

                    if (inTransitMovementsAdapter.getItemCount() != 0) {
                        inTransitMovementsAdapter.setOnClickHandler(MainTabReportsFragment.this::onInTransitMovementClick);
                        inTransitMovementsAdapter.setOnCheckClickHandler(MainTabReportsFragment.this::onInTransitMovementCheckClick);

                        recyclerView.setAdapter(inTransitMovementsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getInTransitMovementsPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL inTransitMovements = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.IN_TRANSIT_MOVEMENTS, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, inTransitMovements.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Movement.Get_pagination_movements response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Movement.Get_pagination_movements>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onInTransitMovementClick(Object data, View view, int pos) {
        Movement movement = (Movement) data;
        Intent activity = new Intent(getActivity(), ViewMovementActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_ID.name(), movement.getId());
        startActivity(activity);
    }
    public void onInTransitMovementCheckClick(Object data, View view, int pos) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_in_transit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showProgressBar();
                Movement movement = (Movement) data;
                DoctorVetApp.get().accept_movement(movement.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if (result) {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "Remito procesado y aceptado", Snackbar.LENGTH_SHORT).show();
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "El remito no se pudo procesar", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showProductsBelowMinimunReport() {
        showProgressBar();

        getProductsBelowMinimunPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Product> productsBelowMinimun = ((Product.Get_pagination_products)pagination).getContent();
                    productsBelowMinimunAdapter = new ProductsAdapter(productsBelowMinimun, ProductsAdapter.Products_types.NORMAL);

                    if (productsBelowMinimunAdapter.getItemCount() != 0) {
                        productsBelowMinimunAdapter.setOnClickHandler(MainTabReportsFragment.this::onProductsBelowMinimunClick);

                        recyclerView.setAdapter(productsBelowMinimunAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getProductsBelowMinimunPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL inTransitMovements = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.PRODUCTS_BELOW_MINIMUN, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, inTransitMovements.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product.Get_pagination_products response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product.Get_pagination_products>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onProductsBelowMinimunClick(Object data, View view, int pos) {
        Product product = (Product) data;
        Intent activity = new Intent(getActivity(), ViewProductVetActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), product.getId());
        startActivity(activity);
    }

    private void showLifeExpectancyReport() {
        showProgressBar();
        showInfo(selectedReport);

        getLifeExpectancyPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Pet> petsLifeExpectancy = ((Pet.Get_pagination_pets)pagination).getContent();
                    petsLifeExpectancyAdapter = new PetsAdapter(petsLifeExpectancy, DoctorVetApp.Adapter_types.COMPACT);

                    if (petsLifeExpectancyAdapter.getItemCount() != 0) {
                        petsLifeExpectancyAdapter.setOnClickHandler(MainTabReportsFragment.this::onLifeExpectancyClick);

                        recyclerView.setAdapter(petsLifeExpectancyAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getLifeExpectancyPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL inTransitMovements = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.LIFE_EXPECTANCY, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, inTransitMovements.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet.Get_pagination_pets response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet.Get_pagination_pets>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onLifeExpectancyClick(Object data, View view, int pos) {
        Pet pet = (Pet) data;
        Intent activity = new Intent(getActivity(), ViewPetActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet.getId());
        startActivity(activity);
    }

    private void showNewVetUsersReport() {
        showProgressBar();
        showInfo(selectedReport);

        getNewVetUsersPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<User> newVetUsers = ((User.Get_pagination_users)pagination).getContent();
                    newVetUsersAdapter = new UsersAdapter(newVetUsers, UsersAdapter.Adapter_types.SHOW);

                    if (newVetUsersAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(newVetUsersAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getNewVetUsersPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL newVetUsersUrl = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.NEW_VET_USERS, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, newVetUsersUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            User.Get_pagination_users response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<User.Get_pagination_users>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showUsersJoinRequestReport() {
        showProgressBar();
        showInfo(selectedReport);

        getUsersJoinRequestPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<X_requests_users_vets> requests_users_vets = ((X_requests_users_vets.Get_pagination_requests)pagination).getContent();
                    usersRequestsAdapter = new X_requests_users_vets_adminAdapter(requests_users_vets, MainTabReportsFragment.this::onUsersJoinRequestClick, MainTabReportsFragment.this::onUsersJoinRequestAcceptClick, MainTabReportsFragment.this::onUsersJoinRequestCancelClick);

                    if (usersRequestsAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(usersRequestsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getUsersJoinRequestPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL usersRequestsUrl = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.USER_JOIN_REQUEST, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, usersRequestsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            X_requests_users_vets.Get_pagination_requests response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<X_requests_users_vets.Get_pagination_requests>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onUsersJoinRequestClick(Object data, View view, int pos) {
        Integer id_user = ((X_requests_users_vets)data).getUser().getId();
        Intent activity = new Intent(getContext(), ViewUserActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.USER_ID.name(), id_user);
        startActivity(activity);
    }
    public void onUsersJoinRequestAcceptClick(Object data, View view, int pos) {
        X_requests_users_vets x_requestsusersvets = (X_requests_users_vets) data;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.aceptar_solucitud_pregunta);
        builder.setMessage(getString(R.string.aceptar_solucitud_mensaje, x_requestsusersvets.getUser().getName()));
        builder.setPositiveButton(R.string.aceptar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                aceptarSolicitudUsuario(x_requestsusersvets, pos);
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
    public void onUsersJoinRequestCancelClick(Object data, View view, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.cancelar_unirse_a_veterinaria_cancelar);
        builder.setPositiveButton(R.string.si, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                cancelarSolicitudUsuario( ((X_requests_users_vets)data).getId(), pos);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        builder.create().show();
    }
    private void aceptarSolicitudUsuario(final X_requests_users_vets request, final Integer pos) {
        showWaitDialog();

        final String json_request = MySqlGson.postGson().toJson(request);

        URL url = NetworkUtils.buildUsersRequestAccept();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String status = MySqlGson.getStatusFromResponse(response);
                    if (DoctorVetApp.Response_status.SUCCESS.name().equals(status)) {
                        usersRequestsAdapter.remove(pos);
                        usersRequestsAdapter.notifyItemRemoved(pos);
                        Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), R.string.solicitud_aceptada, Snackbar.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_conexion_servidor), Snackbar.LENGTH_SHORT).show();
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                hideWaitDialog();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                return json_request.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void cancelarSolicitudUsuario(final Integer id_peticion, final Integer pos) {
        showWaitDialog();

        URL url = NetworkUtils.buildUsersRequestDeleteById(id_peticion);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String status = MySqlGson.getStatusFromResponse(response);
                    if (DoctorVetApp.Response_status.SUCCESS.name().equals(status)) {
                        usersRequestsAdapter.remove(pos);
                        usersRequestsAdapter.notifyItemRemoved(pos);
                    }
                } catch (Exception ex) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.error_conexion_servidor), Snackbar.LENGTH_SHORT).show();
                } finally {
                    hideWaitDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                hideWaitDialog();
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showWaitingRoomsReport() {
        showProgressBar();
        showInfo(selectedReport);

        getWaitingRoomsPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Waiting_room> waiting_rooms = ((Waiting_room.Get_pagination_waiting_rooms)pagination).getContent();
                    waitingRoomsAdapter = new WaitingRoomsAdapter(waiting_rooms, WaitingRoomsAdapter.WaitingRoomsAdapterType.ALL_USERS);

                    if (waitingRoomsAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(waitingRoomsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        waitingRoomsAdapter.setOnClickHandler(MainTabReportsFragment.this::onWaitingRoomsClick);
                        waitingRoomsAdapter.setOnRemoveClickHandler(MainTabReportsFragment.this::onWaitingRoomsRemoveClick);
                        waitingRoomsAdapter.setOnCheckClickHandler(MainTabReportsFragment.this::onWaitingRoomsCheckClick);

                        showRecyclerView();
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
    private void getWaitingRoomsPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL waitingRoomsUrl = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.WAITING_ROOM, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, waitingRoomsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Waiting_room.Get_pagination_waiting_rooms response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Waiting_room.Get_pagination_waiting_rooms>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onWaitingRoomsClick(Object data, View view, int pos) {
        Waiting_room waitingRoom = (Waiting_room) data;
        Intent activity = new Intent(getActivity(), ViewPetActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), waitingRoom.getPet().getId());
        startActivity(activity);
    }
    public void onWaitingRoomsRemoveClick(Object data, View view, int pos) {
        Waiting_room waitingRoom = (Waiting_room)data;
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showWaitDialog();
                DoctorVetApp.get().deleteWaitingRooms(waitingRoom.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void onWaitingRoomsCheckClick(Object data, View view, int pos) {
        Waiting_room waitingRoom = (Waiting_room)data;
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog();
                DoctorVetApp.get().checkWaitingRooms(waitingRoom.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            refreshView();
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void showWaitingRoomsAutoDeletedReport() {
        showProgressBar();
        showInfo(selectedReport);

        getWaitingRoomsAutoDeletedPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Waiting_room> waiting_rooms = ((Waiting_room.Get_pagination_waiting_rooms)pagination).getContent();
                    waitingRoomsAutoDeletedAdapter = new WaitingRoomsAdapter(waiting_rooms, WaitingRoomsAdapter.WaitingRoomsAdapterType.REPORT);

                    if (waitingRoomsAutoDeletedAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(waitingRoomsAutoDeletedAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getWaitingRoomsAutoDeletedPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL waitingRoomsUrl = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.WAITING_ROOM_AUTO_DELETED, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, waitingRoomsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Waiting_room.Get_pagination_waiting_rooms response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Waiting_room.Get_pagination_waiting_rooms>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showSellsReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(true);

        getSellsPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    Sell.Get_pagination_sells pagination_sells = (Sell.Get_pagination_sells)pagination;
                    ArrayList<Sell> sells = pagination_sells.getContent();
                    sellsAdapter = new SellsAdapter(sells, MainTabReportsFragment.this::onSellClickHandler);

                    if (sellsAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(sellsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();

                        txt_total.setText("Total: " + HelperClass.formatCurrency(pagination_sells.getTotal()));
                        txt_balance.setText("Balance: " + HelperClass.formatCurrency(pagination_sells.getBalance()));
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
    private void getSellsPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL waitingRoomsUrl = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.SELLS, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, waitingRoomsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Sell.Get_pagination_sells response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Sell.Get_pagination_sells>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onSellClickHandler(Object data, View view, int pos) {
        Sell sell = (Sell)data;
        Intent activity = new Intent(getContext(), ViewSellActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.SELL_ID.name(), sell.getId());
        startActivity(activity);
    }
    private String getFrom() {
        return HelperClass.getDateForMySQL(HelperClass.getShortDate(txt_from.getEditText().getText().toString(), getContext()));
    }
    private String getTo() {
        return HelperClass.getDateForMySQL(HelperClass.getShortDate(txt_to.getEditText().getText().toString(), getContext()));
    }

    private void showPurchasesReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(true);

        getPurchasesPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    Purchase.Get_pagination_purchases pagination_purchases = (Purchase.Get_pagination_purchases)pagination;
                    ArrayList<Purchase> purchases = pagination_purchases.getContent();
                    purchasesAdapter = new PurchasesAdapter(purchases, MainTabReportsFragment.this::onPurchaseClickHandler);

                    if (purchasesAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(purchasesAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();

                        txt_total.setText("Total: " + HelperClass.formatCurrency(pagination_purchases.getTotal()));
                        txt_balance.setText("Balance: " + HelperClass.formatCurrency(pagination_purchases.getBalance()));
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
    private void getPurchasesPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL waitingRoomsUrl = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.PURCHASES, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, waitingRoomsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Purchase.Get_pagination_purchases response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Purchase.Get_pagination_purchases>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onPurchaseClickHandler(Object data, View view, int pos) {
        Purchase purchase = (Purchase) data;
        Intent activity = new Intent(getContext(), ViewPurchaseActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_ID.name(), purchase.getId());
        startActivity(activity);
    }

    private void showSpendingsReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(false);

        getSpendingsPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Spending> spendings = ((Spending.Get_pagination_spendings)pagination).getContent();
                    spendingsAdapter = new SpendingsAdapter(spendings, MainTabReportsFragment.this::onSpendingClickHandler);

                    if (spendingsAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(spendingsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getSpendingsPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL waitingRoomsUrl = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.SPENDINGS, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, waitingRoomsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Spending.Get_pagination_spendings response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Spending.Get_pagination_spendings>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onSpendingClickHandler(Object data, View view, int pos) {
        Spending spending = (Spending) data;
        Intent activity = new Intent(getContext(), ViewSpendingActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.SPENDING_ID.name(), spending.getId());
        startActivity(activity);
    }

    private void showCashMovementsReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(false);

        getCashMovementsPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Cash_movement> cash_movements = ((Cash_movement.Get_pagination_cash_movements)pagination).getContent();
                    cashMovementsAdapter = new CashMovementsAdapter(cash_movements);

                    if (cashMovementsAdapter.getItemCount() != 0) {
                        cashMovementsAdapter.setOnClickHandler(MainTabReportsFragment.this::onCashMovementClickHandler);

                        recyclerView.setAdapter(cashMovementsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getCashMovementsPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.CASH_MOVEMENTS, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Cash_movement.Get_pagination_cash_movements response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Cash_movement.Get_pagination_cash_movements>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onCashMovementClickHandler(Object data, View view, int pos) {
        Cash_movement cash_movement = (Cash_movement) data;
        Intent activity = new Intent(getContext(), ViewCashMovementActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.CASH_MOVEMENT_ID.name(), cash_movement.getId());
        startActivity(activity);
    }

    private void showMovementsReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(false);

        getMovementsPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Movement> movements = ((Movement.Get_pagination_movements)pagination).getContent();
                    movementsAdapter = new MovementsAdapter(movements, MainTabReportsFragment.this::onMovementClickHandler, MovementsAdapter.AdapterTypes.SHOW);

                    if (movementsAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(movementsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getMovementsPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.MOVEMENTS, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Movement.Get_pagination_movements response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Movement.Get_pagination_movements>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void onMovementClickHandler(Object data, View view, int pos) {
        Movement movement = (Movement) data;
        Intent activity = new Intent(getContext(), ViewMovementActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_ID.name(), movement.getId());
        startActivity(activity);
    }

    private void showAgendaReport2() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(false);

        getAgendaPagination2(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Agenda> agenda = ((Agenda.Get_pagination_agendas)pagination).getContent();
                    agendaAdapter = new AgendaAdapter(agenda, AgendaAdapter.AgendaAdapterTypes.NORMAL);

                    if (agendaAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(agendaAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        agendaAdapter.setOnClickHandler(MainTabReportsFragment.this::onAgendaClick);
                        agendaAdapter.setOnCheckClickHandler(MainTabReportsFragment.this::onAgendaCheckClick);
//                        agendaAdapter.setOnRemoveClickHandler(MainTabReportsFragment.this::onAgendaRemoveClick);

                        showRecyclerView();
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
    private void getAgendaPagination2(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.AGENDA, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Agenda.Get_pagination_agendas response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Agenda.Get_pagination_agendas>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showProductsExcel() {
        showProgressBar();
        showInfo(selectedReport);

        URL url = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.PRODUCTS_EXCEL, 1);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    String path = HelperClass.writeToFile(data, getContext(), "products_excel.xlsx");
                    txtInfo.setText(txtInfo.getText() + ". Guardado en: " + path);
                    //HelperClass.viewFile("products_excel.xlsx", getContext());
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                } finally {
                    hideProgressBar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                hideProgressBar();
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showServicesBarcodes() {
        showProgressBar();
        showInfo(selectedReport);

        URL url = NetworkUtils.buildGetReportsUrl(DoctorVetApp.reports.SERVICES_BARCODES, 1);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    String path = HelperClass.writeToFile(data, getContext(), "services_barcodes.pdf");
                    txtInfo.setText(txtInfo.getText() + ". Guardado en: " + path);
                    HelperClass.viewPdf("services_barcodes.pdf", getContext());
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                } finally {
                    hideProgressBar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                hideProgressBar();
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showProductsTraceabilityReport() {

        if (spinner_deposits.getAdapter() == null) {
            showTraceability();
            return;
        }

        if (traceabilityProduct == null || deposit == null) {
            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "Establece producto y almacen", Snackbar.LENGTH_SHORT).show();
            hideProgressBar();
            hideSwipeRefreshLayoutProgressBar();
            return;
        }

        showProgressBar();

        getProductsTraceabilityPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    ArrayList<Product_traceability> productTraceabilities = ((Product_traceability.Get_pagination_products)pagination).getContent();
                    traceabilityAdapter = new ProductsTraceabilityAdapter(productTraceabilities);

                    if (traceabilityAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(traceabilityAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getProductsTraceabilityPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetTraceabilityReportUrl(page, traceabilityProduct.getId(), deposit.getId());
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product_traceability.Get_pagination_products response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_traceability.Get_pagination_products>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showLogsReport() {
        showProgressBar();
        showInfo(selectedReport);

        showFromTo(false);

        getLogsPagination(1, getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    Log.Get_pagination_logs pagination_logs = (Log.Get_pagination_logs)pagination;
                    ArrayList<Log> logs = pagination_logs.getContent();
                    logsAdapter = new LogsAdapter(logs);

                    if (logsAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(logsAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        showRecyclerView();
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
    private void getLogsPagination(Integer page, String from, String to, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.LOGS, page, from, to);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Log.Get_pagination_logs response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Log.Get_pagination_logs>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    private void showLowFoodReport() {
        showProgressBar();
        showInfo(selectedReport);

        getLowFoodPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
            @Override
            public void onSuccess(Get_pagination pagination) {
                try {
                    LowFood.Get_pagination_low_food pagination_low_food = (LowFood.Get_pagination_low_food)pagination;
                    ArrayList<LowFood> lowFoods = pagination_low_food.getContent();
                    lowFoodAdapter = new LowFoodAdapter(lowFoods);

                    if (lowFoodAdapter.getItemCount() != 0) {
                        recyclerView.setAdapter(lowFoodAdapter);
                        recyclerView.resetPage();
                        recyclerView.setOnPaginationHandler(MainTabReportsFragment.this);

                        lowFoodAdapter.setOnClickHandler(MainTabReportsFragment.this::onLowFoodClick);

                        showRecyclerView();
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
    private void getLowFoodPagination(Integer page, final DoctorVetApp.VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buildGetReportsFromToUrl(DoctorVetApp.reports.LOW_FOOD, page, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            LowFood.Get_pagination_low_food response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<LowFood.Get_pagination_low_food>(){}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void onLowFoodClick(Object data, View view, int pos) {
        LowFood lowFood = (LowFood) data;
        Intent activity = new Intent(getActivity(), ViewPetActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), lowFood.getPet().getId());
        startActivity(activity);
    }

    @Override
    public void hideRecyclerView () {
        recyclerView.setAdapter(null);
        recyclerView.setVisibility(View.INVISIBLE);
    }
    @Override
    public void showRecyclerView () {
        recyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPagination() {
        recyclerView.startLoading();
        showProgressBar();
        recyclerView.addPage();

        switch (selectedReport) {
            case PENDING_AGENDA_VET:
            case EXPIRED_AGENDA_VET:
            case PENDING_AGENDA_USER:
            case EXPIRED_AGENDA_USER:
                getAgendaPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Agenda> agenda = ((Agenda.Get_pagination_agendas)pagination).getContent();
                            agendaAdapter.addItems(agenda);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case BIRTHDAY_PETS:
                getBirthdaysPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                            petsBirthdaysAdapter.addItems(pets);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case PENDING_SUPPLY_VET:
            case PENDING_SUPPLY_USER:
            case EXPIRED_SUPPLY_VET:
            case EXPIRED_SUPPLY_USER:
                getPendingExpiredSupplyPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Pet_supply> petSupply = ((Pet_supply.Get_pagination_supply)pagination).getContent();
                            supplyAdapter.addItems(petSupply);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case OWNERS_NOTIFICATIONS:
                getOwnersNotificationsPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<OwnerNotification> ownerNotifications = ((OwnerNotification.Get_pagination_owner_notif)pagination).getContent();
                            ownersNotificationsAdapter.addItems(ownerNotifications);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case DEBTORS:
                getOwnersDebtorsPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Owner> ownersDebtors = ((Owner.Get_pagination_owners)pagination).getContent();
                            ownersDebtorsAdapter.addItems(ownersDebtors);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;
            case DEBTORS_TOLERANCE:
                getOwnersDebtorsTolerancePagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Owner> ownersDebtors = ((Owner.Get_pagination_owners)pagination).getContent();
                            ownersDebtorsToleranceAdapter.addItems(ownersDebtors);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case CREDITORS:
                getProvidersCreditorsPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Product_provider> providersCreditors = ((Product_provider.Get_pagination_providers)pagination).getContent();
                            providersCreditorsAdapter.addItems(providersCreditors);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;
            case CREDITORS_TOLERANCE:
                getProvidersCreditorsTolernacePagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Product_provider> providersCreditors = ((Product_provider.Get_pagination_providers)pagination).getContent();
                            providersCreditorsToleranceAdapter.addItems(providersCreditors);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case IN_TRANSIT_MOVEMENTS:
                getInTransitMovementsPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Movement> inTransitMovements = ((Movement.Get_pagination_movements)pagination).getContent();
                            inTransitMovementsAdapter.addItems(inTransitMovements);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case PRODUCTS_BELOW_MINIMUN:
                getProductsBelowMinimunPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Product> productsBelowMinimun = ((Product.Get_pagination_products)pagination).getContent();
                            productsBelowMinimunAdapter.addItems(productsBelowMinimun);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;
            case PRODUCTS_TRACEABILITY:
                if (traceabilityProduct == null || deposit == null) {
                    Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "Establece producto y almacen", Snackbar.LENGTH_SHORT).show();
                    hideProgressBar();
                    recyclerView.finishLoading();
                    return;
                }

                getProductsTraceabilityPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Product_traceability> productTraceabilities = ((Product_traceability.Get_pagination_products)pagination).getContent();
                            traceabilityAdapter.addItems(productTraceabilities);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case LIFE_EXPECTANCY:
                getLifeExpectancyPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Pet> petsLifeExpectancy = ((Pet.Get_pagination_pets)pagination).getContent();
                            petsLifeExpectancyAdapter.addItems(petsLifeExpectancy);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case NEW_VET_USERS:
                getNewVetUsersPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<User> newVetUsers = ((User.Get_pagination_users)pagination).getContent();
                            newVetUsersAdapter.addItems(newVetUsers);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case USER_JOIN_REQUEST:
                getUsersJoinRequestPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<X_requests_users_vets> joinRequestUsers = ((X_requests_users_vets.Get_pagination_requests)pagination).getContent();
                            usersRequestsAdapter.addItems(joinRequestUsers);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case WAITING_ROOM:
                getWaitingRoomsPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Waiting_room> waitingRooms = ((Waiting_room.Get_pagination_waiting_rooms)pagination).getContent();
                            waitingRoomsAdapter.addItems(waitingRooms);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case WAITING_ROOM_AUTO_DELETED:
                getWaitingRoomsAutoDeletedPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Waiting_room> waitingRooms = ((Waiting_room.Get_pagination_waiting_rooms)pagination).getContent();
                            waitingRoomsAutoDeletedAdapter.addItems(waitingRooms);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case SELLS:
                getSellsPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Sell> sells = ((Sell.Get_pagination_sells)pagination).getContent();
                            sellsAdapter.addItems(sells);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case PURCHASES:
                getPurchasesPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Purchase> purchases = ((Purchase.Get_pagination_purchases)pagination).getContent();
                            purchasesAdapter.addItems(purchases);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case SPENDINGS:
                getSpendingsPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Spending> spendings = ((Spending.Get_pagination_spendings)pagination).getContent();
                            spendingsAdapter.addItems(spendings);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case CASH_MOVEMENTS:
                getCashMovementsPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Cash_movement> cashMovements = ((Cash_movement.Get_pagination_cash_movements)pagination).getContent();
                            cashMovementsAdapter.addItems(cashMovements);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case MOVEMENTS:
                getMovementsPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Movement> movements = ((Movement.Get_pagination_movements)pagination).getContent();
                            movementsAdapter.addItems(movements);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case SUPPLY:
                getSupplyPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Pet_supply> supplies = ((Pet_supply.Get_pagination_supply)pagination).getContent();
                            supplyAdapter.addItems(supplies);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case AGENDA:
                getAgendaPagination2(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Agenda> agenda = ((Agenda.Get_pagination_agendas)pagination).getContent();
                            agendaAdapter.addItems(agenda);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case LOGS:
                getLogsPagination(recyclerView.getPage(), getFrom(), getTo(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<Log> logs = ((Log.Get_pagination_logs)pagination).getContent();
                            logsAdapter.addItems(logs);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;

            case LOW_FOOD:
                getLowFoodPagination(recyclerView.getPage(), new DoctorVetApp.VolleyCallbackPagination() {
                    @Override
                    public void onSuccess(Get_pagination pagination) {
                        hideProgressBar();
                        if (pagination != null) {
                            ArrayList<LowFood> lowFoods = ((LowFood.Get_pagination_low_food)pagination).getContent();
                            lowFoodAdapter.addItems(lowFoods);
                            recyclerView.finishLoading();
                        } else {
                            DoctorVetApp.get().handle_null_adapter("reports", TAG, true);
                            showErrorMessage();
                        }
                    }
                });
                break;
        }
    }

    private void setFromTo(View rootView) {
        //linear_from_to = rootView.findViewById(R.id.linear_from_to);
        txt_from = rootView.findViewById(R.id.txt_from);
        ImageView img_search_from = rootView.findViewById(R.id.img_search_from);
        txt_from.getEditText().setOnClickListener(this::onFromSearchClick);
        img_search_from.setOnClickListener(this::onFromSearchClick);

        txt_to = rootView.findViewById(R.id.txt_to);
        ImageView img_search_to = rootView.findViewById(R.id.img_search_to);
        txt_to.getEditText().setOnClickListener(this::onToSearchClick);
        img_search_to.setOnClickListener(this::onToSearchClick);

        txt_from.getEditText().setText(HelperClass.getDateInLocale(new Date(), getContext()));
        txt_to.getEditText().setText(HelperClass.getDateInLocale(new Date(), getContext()));

        linear_totals = rootView.findViewById(R.id.linear_totals);
        txt_total = rootView.findViewById(R.id.txt_total);
        txt_balance = rootView.findViewById(R.id.txt_balance);
    }
    private void showFromTo(boolean includeTotals) {
        traceability.setVisibility(View.GONE);
        extensions_container.setVisibility(View.VISIBLE);
        from_to.setVisibility(View.VISIBLE);
        linear_totals.setVisibility(View.GONE);

        if (includeTotals)
            linear_totals.setVisibility(View.VISIBLE);
    }

    //traceability
    private Product traceabilityProduct;
    private Vet_deposit deposit;
    private void setTraceability(View rootView) {
        actvProducto = rootView.findViewById(R.id.actv_product);
        spinner_deposits = rootView.findViewById(R.id.spinner_deposit);

        ImageView productSearch = rootView.findViewById(R.id.img_search_product);
        productSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productoSearchBarcode = rootView.findViewById(R.id.img_search_barcode);
        productoSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });
    }
    private void showTraceability() {
        traceability.setVisibility(View.VISIBLE);
        extensions_container.setVisibility(View.VISIBLE);
        from_to.setVisibility(View.GONE);
        linear_totals.setVisibility(View.GONE);

        showWaitDialog();

        DoctorVetApp.get().getProductsVet(new DoctorVetApp.VolleyCallbackArrayList() {
            @Override
            public void onSuccess(ArrayList resultList) {
                setProductsAdapter(resultList);

                DoctorVetApp.get().getSellsForInput(new DoctorVetApp.VolleyCallbackObject() {
                    @Override
                    public void onSuccess(Object resultObject) {
                        Sell.SellsForInput sellsForInput = (Sell.SellsForInput) resultObject;
                        setDepositAdapter(sellsForInput);
                        deposit = sellsForInput.getDeposits().get(0);

                        hideWaitDialog();
                    }
                });
            }
        });
    }
    private void setProductsAdapter(ArrayList resultList) {
        ProductsAdapter productsAdapter = new ProductsAdapter(resultList, ProductsAdapter.Products_types.NORMAL);
        actvProducto.setAdapter(productsAdapter.getAutocompleteAdapter(getContext()));
        actvProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product)adapterView.getItemAtPosition(i);
                traceabilityProduct = product;
            }
        });
        actvProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    traceabilityProduct = null;
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvProducto);
        DoctorVetApp.get().setAllWidthToDropDown(actvProducto, getActivity());
    }
    private void setDepositAdapter(Sell.SellsForInput sellsForInput) {
        VetsDepositsAdapter vetsDepositsAdapter = new VetsDepositsAdapter(sellsForInput.getDeposits());
        spinner_deposits.setAdapter(vetsDepositsAdapter.getArrayAdapter(getContext()));
        spinner_deposits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Vet_deposit vetDeposit = (Vet_deposit) adapterView.getItemAtPosition(i);
                deposit = vetDeposit;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                deposit = null;
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        //barcodescanner
        if (requestCode == HelperClass.REQUEST_READ_BARCODE && data != null) {
            String barcode = data.getStringExtra(HelperClass.INTENT_EXTRA_BARCODE);
            showWaitDialog();
            DoctorVetApp.get().getProductByBarcode(barcode, new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    if (resultObject != null) {
                        traceabilityProduct = (Product)resultObject;
                        actvProducto.setText(traceabilityProduct.getName());

                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
                    }
                    hideWaitDialog();
                }
            });
            return;
        }

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            if (data.hasExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name())) {
                traceabilityProduct = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
                actvProducto.setText(traceabilityProduct.getName());
            }
        }

    }

}


