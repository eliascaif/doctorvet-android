package com.xionce.doctorvetServices;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.OwnersAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainTabOwnersFragment extends FragmentBase
        implements HelperClass.AdapterOnClickHandler {

    private static final String TAG = "MainTabOwnersFragment";
    private RecyclerView recyclerLastMovements;
    private TextView txt_see_all;

    private final OwnersAdapter ownersAdapter = new OwnersAdapter(new ArrayList<>(), DoctorVetApp.get().preferences_get_owners_view_mode());
    private Socket socket;

    public MainTabOwnersFragment() {
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
        View root_view = inflater.inflate(R.layout.fragment_main_tab_owners, parent, false);

        NestedScrollView nestedScrollView = root_view.findViewById(R.id.nested_scroll);
        nestedScrollView.stopNestedScroll();

        txt_see_all = root_view.findViewById(R.id.txt_see_all);

        swipeRefreshLayout = root_view.findViewById(R.id.swipe_container);
        setSwipeRefreshLayout(root_view);

        //CardView cardviewLastMovements = root_view.findViewById(R.id.cardview_last_movements);
        recyclerLastMovements = root_view.findViewById(R.id.recycler_last_movements);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerLastMovements.setLayoutManager(layoutManager);
        recyclerLastMovements.setHasFixedSize(true);

        initializeOwnersAdapter();

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
    }

    @Override
    public void refreshView() {
        if (created) {
            showProgressBar();
            hideDisplayMessage();
            hideRecyclerView();

            DoctorVetApp.get().getOwnersRecentPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    try {
                        ArrayList<Owner> owners = ((Owner.Get_pagination_owners)pagination).getContent();
                        ownersAdapter.setList(owners);

                        if (ownersAdapter.getItemCount() == 0) {
                            showEmptyListMessage();
                        } else {
                            recyclerLastMovements.setAdapter(ownersAdapter);

                            showRecyclerView();
                            ownersAdapter.setOnClickHandler(MainTabOwnersFragment.this);
                            ownersAdapter.setOnAddEmailClickHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    DoctorVetApp.get().showAddEmailModal((Owner) data, MainTabOwnersFragment.this, new DoctorVetApp.VolleyCallbackOwner() {
                                        @Override
                                        public void onSuccess(Owner resultOwner) {
                                            if (resultOwner != null)
                                                refreshView();
                                        }
                                    });
                                }
                            });
                            ownersAdapter.setOnAddTelefonoClickHandler(new HelperClass.AdapterOnClickHandler() {
                                @Override
                                public void onClick(Object data, View view, int pos) {
                                    DoctorVetApp.get().showAddTelefonoModal((Owner) data, MainTabOwnersFragment.this, new DoctorVetApp.VolleyCallbackOwner() {
                                        @Override
                                        public void onSuccess(Owner resultOwner) {
                                            if (resultOwner != null)
                                                refreshView();
                                        }
                                    });
                                }
                            });
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

            DoctorVetApp.get().getOwnersRecentPagination(1, new DoctorVetApp.VolleyCallbackPagination() {
                @Override
                public void onSuccess(Get_pagination pagination) {
                    try {
                        ArrayList<Owner> owners = ((Owner.Get_pagination_owners)pagination).getContent();
                        ownersAdapter.setList(owners);
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                    } finally {
                        hideProgressBar();
                    }
                }
            });
        }
    }

    @Override
    public void onClick(Object data, View view, int pos) {
        Owner owner = (Owner) data;
        Intent activity = new Intent(getActivity(), ViewOwnerActivity.class);
        activity.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
        startActivity(activity);
    }

    private void initializeOwnersAdapter() {
        recyclerLastMovements.setAdapter(ownersAdapter);
        ownersAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                Owner owner = (Owner) data;
                Intent activity = new Intent(getActivity(), ViewOwnerActivity.class);
                activity.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
                startActivity(activity);
            }
        });

        txt_see_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ViewOwnersHistoricActivity.class);
                startActivity(intent);
            }
        });
    }
    public void changeAdapter(DoctorVetApp.Adapter_types type) {
        DoctorVetApp.get().preferences_set_owners_view_mode(type);
        recyclerLastMovements.setAdapter(null);
        ownersAdapter.setAdapterType(type);
        recyclerLastMovements.setAdapter(ownersAdapter);
        refreshView();
    }

    public void hideRecyclerView() {
        swipeRefreshLayout.setVisibility(View.INVISIBLE);
    }
    public void showRecyclerView() {
        coordinatorContainerLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
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

                        if (table_name.equalsIgnoreCase("owners_recent")) {
                            refreshLastMovementsFromSocket();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}