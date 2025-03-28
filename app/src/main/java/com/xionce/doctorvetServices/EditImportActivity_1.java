package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_CREATE;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.xionce.doctorvetServices.data.Upload_file;
import com.xionce.doctorvetServices.data.Xlsx_identifier;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class EditImportActivity_1 extends EditBaseActivity {

    private static final String TAG = "EditImportActivity_1";
    private Xlsx_identifier xlsx_identifier = null;
    private boolean uploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_import_1);

        hideToolbarImage();
        hideFab();

        //TextView txtSellTo = findViewById(R.id.txt_sell_to);
        toolbar_title.setText("Importar productos");
        toolbar_subtitle.setText("Selecciona archivo xlsx");

        Button btn_select_xlsx = findViewById(R.id.btn_select_xlsx);
        btn_select_xlsx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectXlxs();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH)
            finish();

        if (resultCode != RESULT_OK) return;

        if (requestCode == REQUEST_CREATE) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = data.getData();

            if (!HelperClass.getExtensionFromUri(uri, contentResolver).equals("xlsx")) {
                Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona xlsx", Snackbar.LENGTH_LONG).show();
                return;
            }

            if (HelperClass.getSizeInKBFromUri(uri, contentResolver) < 5000) {
                try {
                    String outputFileName = HelperClass.createFile(this, HelperClass.getExtensionFromUri(uri, contentResolver));
                    InputStream in = getContentResolver().openInputStream(uri);
                    OutputStream out = new FileOutputStream(new File(outputFileName));

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.close();
                    in.close();

                    File file = new File(outputFileName);
                    Upload_file upload_file = new Upload_file();
                    upload_file.setFile_base64(HelperClass.fileToString(file));

                    upload_xlsx(upload_file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.error_archivo_muy_grande, Snackbar.LENGTH_LONG).show();
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

    private void next() {
        if (uploading) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Please wait while uploading file", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (xlsx_identifier == null) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona archivo xlsx", Snackbar.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(EditImportActivity_1.this, EditImportActivity_2.class);
        intent.putExtra("xlsx_identifier", xlsx_identifier.getFile_identifier());
        startActivityForResult(intent, 1);
    }
    private void upload_xlsx(Upload_file xlsx) {
        if (xlsx == null) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Selecciona archivo xlsx", Snackbar.LENGTH_LONG).show();
            return;
        }

        try {
            showWaitDialog();
            uploading = true;
            String file_json_object = MySqlGson.getGson().toJson(xlsx);
            URL xlsxEndPoint = NetworkUtils.buildGetXlsx1Url();
            TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, xlsxEndPoint.toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        String data = MySqlGson.getDataFromResponse(response).toString();
                        xlsx_identifier = MySqlGson.getGson().fromJson(data, Xlsx_identifier.class);
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditImportActivity_1.this), "upload complete", Snackbar.LENGTH_LONG).show();
                        uploading = false;
                        next();
                    } catch (Exception ex) {
                        DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    } finally {
                        hideWaitDialog();
                        uploading = false;
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DoctorVetApp.get().handle_volley_error(error, TAG, true);
                    hideWaitDialog();
                    uploading = false;
                }
            })
            {
                @Override
                public byte[] getBody() {
                    return file_json_object.getBytes();
                }
            };
            DoctorVetApp.get().addToRequestQueque(stringRequest);
        } catch (Exception e) {
            DoctorVetApp.get().handle_error(e, TAG, true);
        }
    }
    private void selectXlxs() {
        Intent intent = new Intent();
        //intent.setType("application/excel");
        intent.setType("application/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CREATE);
    }

}
