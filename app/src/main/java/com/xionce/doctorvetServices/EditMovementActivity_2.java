package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.Movement_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditMovementActivity_2 extends EditBaseActivity {

    private static final String TAG = "EditMovementActivity_2";
    private Movement movement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_movement_2);
        toolbar_title.setText("Nuevo movimiento");
        toolbar_subtitle.setText("Revisa que este todo bien");
        TextView txtDepositOrigin = findViewById(R.id.txt_deposit_origin);
        TextView txtDepositDestination = findViewById(R.id.txt_deposit_destination);
        hideToolbarImage();
        hideFab();

        Movement movement = getObject();
        txtDepositOrigin.setText("Origen: " + movement.getOrigin_deposit().getCompleteName());
        txtDepositDestination.setText("Destino: " + movement.getDestination_deposit().getCompleteName());

        RecyclerView recyclerView_movement_products = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(EditMovementActivity_2.this);
        recyclerView_movement_products.setLayoutManager(layoutManager2);
        recyclerView_movement_products.setHasFixedSize(true);
        recyclerView_movement_products.setAdapter(new Movement_itemAdapter(movement.getItems(), Movement_itemAdapter.MovementAdapter_types.FOR_MOVEMENT_INPUT, false));

        Button btn_end = findViewById(R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_sell_request();
            }
        });
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }

    @Override
    protected void save() {

    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return null;
    }

    @Override
    protected Object getObjectFromUI() {
        return null;
    }

    @Override
    protected Movement getObject() {
        if (movement == null)
            movement = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name()), Movement.class);

        return movement;
    }

    @Override
    protected void setObjectToUI(Object object) {

    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void send_sell_request() {
        try {
            showWaitDialog();

            Movement movement = getObject();
            final String movement_json_object = MySqlGson.postGson().toJson(movement);
            URL url = NetworkUtils.buildMovementsUrl(null, null, null);
            TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, url.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String data = MySqlGson.getDataFromResponse(response).toString();
                        Movement movement_response = MySqlGson.getGson().fromJson(data, Movement.class);
                        setResult(HelperClass.REQUEST_FINISH);

                        Intent intent = new Intent(EditMovementActivity_2.this, EditMovementActivity_3.class);
                        intent.putExtra("id_movement", movement_response.getId());
                        startActivity(intent);

                        finish();
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
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
            })
            {
                @Override
                public byte[] getBody() {
                    return movement_json_object.getBytes();
                }
            };
            DoctorVetApp.get().addToRequestQueque(stringRequest);
        } catch (Exception e) {
            DoctorVetApp.get().handle_error(e, TAG, true);
        }
    }

}
