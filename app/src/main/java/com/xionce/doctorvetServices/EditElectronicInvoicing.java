package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_READ_KEY_PEM;
import static com.xionce.doctorvetServices.utilities.NetworkUtils.DOCTOR_VET_BASE_URL;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.VolleyMultipartRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class EditElectronicInvoicing extends EditBaseActivity {

    private static final String TAG = "EditElectronicInvoicing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_electronic_invoicing);

        hideToolbarImage();
        hideFab();

        toolbar_title.setText("Facturación electrónica");
        toolbar_subtitle.setText("Selecciona archivos .key y .pem");

        TextView txtInstructions = findViewById(R.id.txt_instructions);
        txtInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(DOCTOR_VET_BASE_URL + "electronic-invoicing.html");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(browserIntent);
            }
        });

        Button btn_select_key = findViewById(R.id.btn_select_key);
        btn_select_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectKey();
            }
        });

        Button btn_select_pem = findViewById(R.id.btn_select_pem);
        btn_select_pem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPem();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_READ_KEY_PEM) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = data.getData();

            //String fileName = getFileName(uri);

            try {
                String outputFileName = HelperClass.createFile(getApplicationContext(), HelperClass.getExtensionFromUri(uri, contentResolver));
                InputStream in = getContentResolver().openInputStream(uri);
                OutputStream out = new FileOutputStream(new File(outputFileName));

                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                out.close();
                in.close();

                upload(outputFileName);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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

    private void selectKey() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_READ_KEY_PEM);
    }
    private void selectPem() {
        Intent intent = new Intent();
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_READ_KEY_PEM);
    }
    private void upload(String pathToFile) {
        showWaitDialog();

        File file = new File(pathToFile);
        byte[] file_byte_array = HelperClass.fileToByteArray(file);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, NetworkUtils.buildElectronicInvoicingArgUrl().toString(), new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                hideWaitDialog();
                String response_str = new String(response.data);
                String data = MySqlGson.getDataFromResponse(response_str).getAsString();
                if (data.equalsIgnoreCase("true"))
                    Snackbar.make(DoctorVetApp.getRootForSnack(EditElectronicInvoicing.this), "Archivo subido.", Snackbar.LENGTH_LONG).show();
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
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("file", new DataPart(pathToFile, file_byte_array));
                return params;
            }
        };
        DoctorVetApp.get().addToRequestQueque(volleyMultipartRequest);
    }
//    private String getFileName(Uri uri) {
//        String result = null;
//        if (uri.getScheme().equals("content")) {
//            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
//            try {
//                if (cursor != null && cursor.moveToFirst()) {
//                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//                    result = cursor.getString(nameIndex);
//                }
//            } finally {
//                cursor.close();
//            }
//        }
//        if (result == null) {
//            result = uri.getPath();
//            int cut = result.lastIndexOf('/');
//            if (cut != -1) {
//                result = result.substring(cut + 1);
//            }
//        }
//        return result;
//    }

}
