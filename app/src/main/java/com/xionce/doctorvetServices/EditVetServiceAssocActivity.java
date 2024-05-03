package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditVetServiceAssocActivity extends EditBaseActivity {

    private static final String TAG = "EditVetServiceAssocActi";
    private Button btn_end;

    private ArrayList<Product> services_assoc = null;
    private ProductsAdapter productsAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_vet_service_assoc);
        toolbar_title.setText("Servicios asociados");
        toolbar_subtitle.setText("Marca / desmarca");
        btn_end = findViewById(R.id.btn_add);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getVetServicesAssoc(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList arrayList) {
                    setRequestCompleted();
                    services_assoc = arrayList;
                    productsAdapter = new ProductsAdapter(services_assoc, ProductsAdapter.Products_types.SERVICES_ASSOC);
                    setUpRecycler();
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
            productsAdapter = new ProductsAdapter(services_assoc, ProductsAdapter.Products_types.SERVICES_ASSOC);
            setUpRecycler();
        }

        Button btn_end = findViewById(R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    private void setUpRecycler() {
        RecyclerView recyclerView = findViewById(R.id.recycler_services_schedules);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditVetServiceAssocActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productsAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("services_assoc", MySqlGson.getGson().toJson(services_assoc));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("services_assoc");
        services_assoc = MySqlGson.getGson().fromJson(objectInString, new TypeToken<List<Product>>(){}.getType());
    }

    @Override
    protected void save() {
        showWaitDialog();

        ArrayList<Product> polish_products = new ArrayList<>();
        for (Product product:productsAdapter.getArrayList()) {
            if (product.getService_assoc() == 1) {
                polish_products.add(product.getPolish());
            }
        }
        final String jsonObject = MySqlGson.postGson().toJson(polish_products);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success"))
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
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {
        save();
    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildServicesSchedulesVetUrl("create_service_assoc");
    }

    @Override
    protected Object getObjectFromUI() {
        return null;
    }

    @Override
    protected Object getObject() {
        return null;
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

}