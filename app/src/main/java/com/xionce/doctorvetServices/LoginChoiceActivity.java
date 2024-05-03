package com.xionce.doctorvetServices;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.data.VetsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginChoiceActivity extends AppCompatActivity {

    private static final String TAG = "LoginChoiceActivity";
    private ProgressBar mLoadingIndicator;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_choice);

        emailSocket = getEmailSocket();
        if (emailSocket != null) {
            emailSocket.on("email_accepted", serverMessage);
            Log.i(TAG, "Socket email_accepted listener on");
        }

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        swipeRefreshLayout = findViewById(R.id.swipe_container);

        //recyclerview
        recyclerView = findViewById(R.id.recycler_vets);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LoginChoiceActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        refreshAsociations();

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAsociations();
            }
        });

        Button btnCreateVet = findViewById(R.id.btn_crear_veterinaria);
        btnCreateVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginChoiceActivity.this, EditVetActivity.class);
                intent.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, DoctorVetApp.REQUEST_INIT_CREATE_VET);
                startActivityForResult(intent, DoctorVetApp.REQUEST_INIT_CREATE_VET);
            }
        });

        Button btnSelectVet = findViewById(R.id.btn_unirse_a_veterinaria);
        btnSelectVet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginChoiceActivity.this, SearchVetActivity.class);
                intent.putExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, DoctorVetApp.REQUEST_INIT_SELECT_VET);
                startActivityForResult(intent, DoctorVetApp.REQUEST_INIT_SELECT_VET);
            }
        });

        ImageView imgRefresh = findViewById(R.id.img_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshAsociations();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAsociations();
    }

    private void refreshAsociations() {
        showProgressBar();

        URL url = NetworkUtils.buildUsersVetsUrl(DoctorVetApp.get().preferences_getUserEmail());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String vetsArray = MySqlGson.getDataFromResponse(response).toString();
                    ArrayList<Vet> vets = MySqlGson.getGson().fromJson(vetsArray, new TypeToken<List<Vet>>(){}.getType());
                    VetsAdapter vetsAdapter = new VetsAdapter(vets, VetsAdapter.Adapter_types.SHOW);

                    vetsAdapter.setOnClickHandler(new HelperClass.AdapterOnClickHandler() {
                        @Override
                        public void onClick(Object data, View view, int pos) {
                            //prevent fast taps, double click
                            vetsAdapter.setClickeable(false);

                            Vet vet = (Vet) data;
                            DoctorVetApp.get().doFinalAuthPost(vet.getId(), LoginChoiceActivity.this, LoginActivity.class);
                        }
                    });
                    recyclerView.setAdapter(vetsAdapter);

                } catch (Exception e) {
                    DoctorVetApp.get().handle_onResponse_error(e, TAG, true, response);
                } finally {
                    hideProgressBar();
                    swipeRefreshLayout.setRefreshing(false);
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

    public void showProgressBar() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    public void hideProgressBar() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        emailSocket = getEmailSocket();
        if (emailSocket != null) {
            emailSocket.off("email_accepted", serverMessage);
            Log.i(TAG, "Socket email_accepted listener off");
        }
    }

    //socket
    private Socket emailSocket = null;
    public Socket getEmailSocket() {
        if (emailSocket == null) {
            try {
                emailSocket = IO.socket(NetworkUtils.DOCTORVET_SOCKET_URL);
                emailSocket.connect();
                String email = DoctorVetApp.get().preferences_getUserEmail();
                String init_email = email;
                emailSocket.emit("init_email", init_email);
            } catch (URISyntaxException e) {
                DoctorVetApp.get().handle_error(e, TAG, true);
            }
        }

        return emailSocket;
    }
    private Emitter.Listener serverMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, args[0].toString());

                    JSONObject incomingData = null;
                    try {
                        incomingData = new JSONObject(args[0].toString());
                        String email = incomingData.getString("email");
                        if (email.equalsIgnoreCase(DoctorVetApp.get().preferences_getUserEmail())) {
                            refreshAsociations();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}
