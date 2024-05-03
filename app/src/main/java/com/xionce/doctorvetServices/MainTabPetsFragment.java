package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.data.AgendaAdapter;
import com.xionce.doctorvetServices.data.Dashboard;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.PetsAdapter;
import com.xionce.doctorvetServices.data.WaitingRoomsAdapter;
import com.xionce.doctorvetServices.data.Waiting_room;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainTabPetsFragment extends FragmentBase {

    private static final String TAG = "MainTabPetsFragment";
    private CardView cardviewWaitingRoom;
    private RecyclerView recyclerViewWaitingRoom;
    private final WaitingRoomsAdapter waitingRoomsAdapter = new WaitingRoomsAdapter(new ArrayList<>(), WaitingRoomsAdapter.WaitingRoomsAdapterType.ALL_USERS);
    private RecyclerView.LayoutManager waitingRoomLayoutManager;
    private CardView cardviewInAgenda;
    private RecyclerView recyclerInAgenda;
    private final AgendaAdapter inAgendaAdapter = new AgendaAdapter(new ArrayList<>(), AgendaAdapter.AgendaAdapterTypes.DASHBOARD);
    private RecyclerView.LayoutManager inAgendaLayoutManager;
    private CardView cardviewLastMovements;
    private RecyclerView recyclerLastMovements;
    private final PetsAdapter petsAdapter = new PetsAdapter(new ArrayList<>(), DoctorVetApp.Adapter_types.EXTENDED);
    private SwipeRefreshLayout swipeRefreshLayout;
    private Socket socket;
    private TextView txtAllUsersSwitch;
    private TextView txtAgendaAllUsersSwitch;
    private TextView txt_see_all;

    public MainTabPetsFragment() {
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
        View root_view = inflater.inflate(R.layout.fragment_main_tab_pets, parent, false);

        txt_see_all = root_view.findViewById(R.id.txt_see_all);

        swipeRefreshLayout = root_view.findViewById(R.id.swipe_container);
        setSwipeRefreshLayout(root_view);

        cardviewWaitingRoom = root_view.findViewById(R.id.cardview_waiting_room);
        txtAllUsersSwitch = root_view.findViewById(R.id.txt_all_users_switch);
        txtAgendaAllUsersSwitch = root_view.findViewById(R.id.txt_agenda_all_users_switch);

        LinearLayoutCompat waitingRoomLinearLayout = root_view.findViewById(R.id.linear_waiting_rooms);

        recyclerViewWaitingRoom = root_view.findViewById(R.id.recycler_waiting_room);
        waitingRoomLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewWaitingRoom.setLayoutManager(waitingRoomLayoutManager);
        recyclerViewWaitingRoom.setHasFixedSize(true);
        initializeInWaitingRoomAdapter();

        cardviewInAgenda = root_view.findViewById(R.id.cardview_in_agenda);
        recyclerInAgenda = root_view.findViewById(R.id.recycler_in_agenda);
        inAgendaLayoutManager = new LinearLayoutManager(getActivity());
        recyclerInAgenda.setLayoutManager(inAgendaLayoutManager);
        recyclerInAgenda.setHasFixedSize(true);
        initializeInAgendaAdapter();

        cardviewLastMovements = root_view.findViewById(R.id.cardview_last_movements);
        recyclerLastMovements = root_view.findViewById(R.id.recycler_last_movements);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(getActivity());
        recyclerLastMovements.setLayoutManager(layoutManager3);
        recyclerLastMovements.setHasFixedSize(true);
        initializePetsAdapter();

        return root_view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
//        Log.i(TAG, "onResume executed - view refreshed");
    }

    @Override
    public void refreshView() {
        if (created) {
            showProgressBar();
            hideDisplayMessage();
            hideRecyclerView();

            DoctorVetApp.get().getPetsDashboardAdapter(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    try {
                        Dashboard dashboard = (Dashboard)resultObject;

                        cardviewWaitingRoom.setVisibility(View.GONE);
                        if (dashboard.getIn_waiting_rooms().size() > 0) {
                            waitingRoomsAdapter.setList(dashboard.getIn_waiting_rooms());
                            cardviewWaitingRoom.setVisibility(View.VISIBLE);
                        }

                        cardviewInAgenda.setVisibility(View.GONE);
                        if (dashboard.getIn_agenda().size() > 0) {
                            inAgendaAdapter.setList(dashboard.getIn_agenda());
                            cardviewInAgenda.setVisibility(View.VISIBLE);
                        }

                        cardviewLastMovements.setVisibility(View.GONE);
                        if (dashboard.getLast_movements().size() > 0) {
                            petsAdapter.setList(dashboard.getLast_movements());
                            cardviewLastMovements.setVisibility(View.VISIBLE);
                        }

                        if (dashboard.getIn_waiting_rooms().size() == 0
                                & dashboard.getIn_agenda().size() == 0
                                & dashboard.getLast_movements().size() == 0) {

                            showEmptyListMessage();
                        } else {
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
    }
    public void refreshLastMovementsFromSocket() {
        if (created) {
            showProgressBar();

            DoctorVetApp.get().getPetsRecentPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    try {
                        ArrayList<Pet> pets = ((Pet.Get_pagination_pets)pagination).getContent();
                        petsAdapter.setList(pets);
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                    } finally {
                        hideProgressBar();
                    }
                }
            });
        }
    }

    private void initializeInWaitingRoomAdapter() {
        recyclerViewWaitingRoom.setAdapter(waitingRoomsAdapter);
        waitingRoomsAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Waiting_room waitingRoom = (Waiting_room) data;
                Intent activity = new Intent(getActivity(), ViewPetActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), waitingRoom.getPet().getId());
                startActivity(activity);
            }
        });
        waitingRoomsAdapter.setOnCheckClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                checkWaitingRooms((Waiting_room) data);
            }
        });
        waitingRoomsAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnCancelClickHandler() {
            @Override
            public void onCancelClick(Object data, View view, int pos) {
                deleteWaitingRooms((Waiting_room)data);
            }
        });
        txtAllUsersSwitch.setTag(WaitingRoomsAdapter.WaitingRoomsAdapterType.ALL_USERS);
        txtAllUsersSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitingRoomsAdapter.WaitingRoomsAdapterType adapterType = (WaitingRoomsAdapter.WaitingRoomsAdapterType) txtAllUsersSwitch.getTag();
                if (adapterType == WaitingRoomsAdapter.WaitingRoomsAdapterType.USER) {
                    waitingRoomsAdapter.setAdapterType(WaitingRoomsAdapter.WaitingRoomsAdapterType.ALL_USERS);
                    txtAllUsersSwitch.setTag(WaitingRoomsAdapter.WaitingRoomsAdapterType.ALL_USERS);
                    txtAllUsersSwitch.setText("Personales");
                } else {
                    waitingRoomsAdapter.setAdapterType(WaitingRoomsAdapter.WaitingRoomsAdapterType.USER);
                    txtAllUsersSwitch.setTag(WaitingRoomsAdapter.WaitingRoomsAdapterType.USER);
                    txtAllUsersSwitch.setText("Todos");
                }

                recyclerViewWaitingRoom.setLayoutManager(null);
                recyclerViewWaitingRoom.setLayoutManager(waitingRoomLayoutManager);
            }
        });
    }
    private void initializeInAgendaAdapter() {
        recyclerInAgenda.setAdapter(inAgendaAdapter);
        inAgendaAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Pet pet = (Pet) data;
                Intent activity = new Intent(getActivity(), ViewPetActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet.getId());
                startActivity(activity);
            }
        });
        inAgendaAdapter.setOnCheckClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                checkAgenda((Agenda) data);
            }
        });
        txtAgendaAllUsersSwitch.setTag(AgendaAdapter.AgendaUsersType.ALL_USERS);
        txtAgendaAllUsersSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AgendaAdapter.AgendaUsersType adapterType = (AgendaAdapter.AgendaUsersType) txtAgendaAllUsersSwitch.getTag();
                if (adapterType == AgendaAdapter.AgendaUsersType.USER) {
                    inAgendaAdapter.setAdapterType(AgendaAdapter.AgendaUsersType.ALL_USERS);
                    txtAgendaAllUsersSwitch.setTag(AgendaAdapter.AgendaUsersType.ALL_USERS);
                    txtAgendaAllUsersSwitch.setText("Personales");
                } else {
                    inAgendaAdapter.setAdapterType(AgendaAdapter.AgendaUsersType.USER);
                    txtAgendaAllUsersSwitch.setTag(AgendaAdapter.AgendaUsersType.USER);
                    txtAgendaAllUsersSwitch.setText("Todos");
                }

                recyclerInAgenda.setLayoutManager(null);
                recyclerInAgenda.setLayoutManager(inAgendaLayoutManager);
            }
        });
    }
    private void initializePetsAdapter() {
        recyclerLastMovements.setAdapter(petsAdapter);
        petsAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Pet pet = (Pet) data;
                Intent activity = new Intent(getActivity(), ViewPetActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), pet.getId());
                startActivity(activity);
            }
        });

        txt_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewPetsHistoricActivity.class);
                startActivity(intent);
            }
        });
    }

    private void insertInWaitingRoomFromSocket(Waiting_room data) {
        Log.i(TAG, "insertInWaitingRoom called");

        if (waitingRoomsAdapter.getItemCount() == 0) {
            showRecyclerView();
            cardviewWaitingRoom.setVisibility(View.VISIBLE);
        }

        waitingRoomsAdapter.addWaitingRoom(data);
    }
    private void deleteInWaitingRoomFromSocket(Waiting_room data) {
        Log.i(TAG, "deleteInWaitingRoom called");

        waitingRoomsAdapter.removeWaitingRoom(data.getId());

        if (waitingRoomsAdapter.getItemCount() == 0) {
            cardviewWaitingRoom.setVisibility(View.GONE);
        }
    }
    private void insertInAgendaFromSocket(Agenda data) {
        Log.i(TAG, "insertInAgenda called");

        if (inAgendaAdapter.getItemCount() == 0) {
            showRecyclerView();
            cardviewInAgenda.setVisibility(View.VISIBLE);
        }

        inAgendaAdapter.addItem(data);
    }
    private void deleteInAgendaFromSocket(Agenda data) {
        //deleted or marked as done
        Log.i(TAG, "deleteInAgenda called");

        inAgendaAdapter.removeById(data.getId());

        if (inAgendaAdapter.getItemCount() == 0) {
            cardviewInAgenda.setVisibility(View.GONE);
        }
    }
    private void insertInRecentFromSocket(Pet data) {
        //in recent we can have two same pets with two different movements
        Log.i(TAG, "insertInRecent called");

        if (petsAdapter.getItemCount() == 0) {
            hideDisplayMessage();
            showRecyclerView();
            cardviewLastMovements.setVisibility(View.VISIBLE);
        }

        petsAdapter.replaceRecent(data);
        //petsAdapter.notifyItemInserted(0); //not working
        petsAdapter.notifyDataSetChanged();
    }

    public void hideRecyclerView() {
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }
    public void showRecyclerView() {
        coordinatorContainerLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
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
                            deleteInAgendaFromSocket(agenda_event);
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void checkWaitingRooms(Waiting_room waitingRoom) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_check_agenda), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog();
                DoctorVetApp.get().checkWaitingRooms(waitingRoom.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            deleteInWaitingRoomFromSocket(waitingRoom);
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
    private void deleteWaitingRooms(Waiting_room waitingRoom) {
        HelperClass.getOKCancelDialog(getContext(), getString(R.string.action_delete_pregunta), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showWaitDialog();
                DoctorVetApp.get().deleteWaitingRooms(waitingRoom.getId(), new DoctorVetApp.VolleyCallback() {
                    @Override
                    public void onSuccess(Boolean result) {
                        hideWaitDialog();
                        if (result) {
                            deleteInWaitingRoomFromSocket(waitingRoom);
                        } else {
                            Snackbar.make(DoctorVetApp.getRootForSnack(getActivity()), getString(R.string.err_action), Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
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

                        if (table_name.equalsIgnoreCase("waiting_rooms")) {
                            Waiting_room waitingRoom = MySqlGson.getGson().fromJson(incomingData.getString("data"), Waiting_room.class);
                            if (operation.equalsIgnoreCase("created")) {
                                insertInWaitingRoomFromSocket(waitingRoom);
                            } else if (operation.equalsIgnoreCase("deleted")) {
                                deleteInWaitingRoomFromSocket(waitingRoom);
                            }
                        } else if (table_name.equalsIgnoreCase("agenda")) {
                            Agenda agenda = MySqlGson.getGson().fromJson(incomingData.getString("data"), Agenda.class);
                            if (operation.equalsIgnoreCase("created")) {
                                insertInAgendaFromSocket(agenda);
                            } else if (operation.equalsIgnoreCase("deleted")) {
                                deleteInAgendaFromSocket(agenda);
                            }
                        } else if (table_name.equalsIgnoreCase("pets_recent")) {
                            refreshLastMovementsFromSocket();
//                            Pet pet = MySqlGson.getGson().fromJson(incomingData.getJSONObject("data").getString("pet"), Pet.class);
//                            if (operation.equalsIgnoreCase("created"))
//                                insertInRecentFromSocket(pet);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
