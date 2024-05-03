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

public class EditSellActivity_4 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sell_4);

        hideToolbarImage();
        hideFab();

        toolbar_title.setText("Venta");

        Button btn_show_receipt = findViewById(R.id.btn_show_receipt);
        btn_show_receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                send_pdf_sell_request(getIntent().getIntExtra("id_sell", 0));
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

    private void send_pdf_sell_request(Integer id_sell) {
        showWaitDialog();

        URL pdfSellEndPoint = NetworkUtils.buildGetSellsPdfUrl(id_sell);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, pdfSellEndPoint.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    HelperClass.writeToFile(response, EditSellActivity_4.this, "sell.pdf");
                    boolean success = HelperClass.viewPdf("sell.pdf", EditSellActivity_4.this);
                    if (!success)
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditSellActivity_4.this), getString(R.string.err_pdf_viewer), Snackbar.LENGTH_SHORT).show();
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

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }
}
