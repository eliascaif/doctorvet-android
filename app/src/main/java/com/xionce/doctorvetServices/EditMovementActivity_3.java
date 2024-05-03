package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditMovementActivity_3 extends EditBaseActivity {

    private static final String TAG = "EditMovementActivity_3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_movement_3);
        toolbar_title.setText("Nuevo movimiento");
        hideToolbarImage();
        hideFab();

        Button btn_show_receipt = findViewById(R.id.btn_show_receipt);
        btn_show_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_pdf_movement_request(getIntent().getIntExtra("id_movement", 0));
            }
        });

        Button btn_close = findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private void send_pdf_movement_request(Integer id_movement) {
        showWaitDialog();

        URL pdfMovementEndPoint = NetworkUtils.buildMovementsUrl(null, id_movement, true);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, pdfMovementEndPoint.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    HelperClass.writeToFile(response, EditMovementActivity_3.this, "movement.pdf");
                    boolean success = HelperClass.viewPdf("movement.pdf", EditMovementActivity_3.this);
                    if (!success)
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditMovementActivity_3.this), getString(R.string.err_pdf_viewer), Snackbar.LENGTH_SHORT).show();
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
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

}
