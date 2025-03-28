package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.SDT;
import com.xionce.doctorvetServices.data.Symptom;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditSDTActivity extends EditBaseActivity {

    private static final String TAG = "EditSDTActivity";
    private TextInputLayout txtName;
    DoctorVetApp.Sdt_type typeSDT;

    private SDT sdt = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sdt);
        txtName = findViewById(R.id.txt_name);
        DoctorVetApp.get().markRequired(txtName);
        hideToolbarImage();

        //sysmtom/diagnostic/treatments are base equals objects
        setTypeSDT();
        toolbar_title.setText(R.string.nuevo);
        toolbar_subtitle.setText(getSubtitleSDT());

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        SDT sdt = getObject();
        setObjectToUI(sdt);

        txtName.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("sdt", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("sdt");
        sdt = MySqlGson.getGson().fromJson(objectInString, SDT.class);
    }

    @Override
    protected void save() {
        if (!validateName())
            return;

        showWaitDialog();

        final SDT sdt = getObjectFromUI();
        final String sdtJson = MySqlGson.getGson().toJson(sdt);

        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                Integer id = Integer.parseInt(MySqlGson.getDataFromResponse(response).getAsJsonObject().get("id").toString());

                if (getIntent().hasExtra(DoctorVetApp.REQUEST_CREATE_OBJECT)) {
                    Intent dataBackIntent = new Intent();
                    dataBackIntent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, getIntentValue());
                    dataBackIntent.putExtra(getIntentValue(), MySqlGson.getGson().toJson(new SDT(id, sdt.getName())));
                    setResult(RESULT_OK, dataBackIntent);
                }
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
            } finally {
                hideWaitDialog();
                finish();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return sdtJson.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update(){
        save();
    }

    @Override
    protected URL getUrl() {
        if (typeSDT == DoctorVetApp.Sdt_type.SYMPTOM)
            return NetworkUtils.buildSymptomsUrl();

        if (typeSDT == DoctorVetApp.Sdt_type.DIAGNOSTIC)
            return NetworkUtils.buildDiagnosticUrl();

        return NetworkUtils.buildTreatmentsUrl();
    }

    @Override
    protected SDT getObjectFromUI() {
        SDT sdt = getObject();
        sdt.setName(txtName.getEditText().getText().toString());
        return sdt;
    }

    @Override
    protected SDT getObject() {
        if (sdt == null) {
            if (isUpdate()) {
                sdt = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.SDT_OBJ.name()), SDT.class);
            } else {
                sdt = new SDT();
            }
        }

        return sdt;
    }

    @Override
    protected void setObjectToUI(Object object) {
        SDT sdt = (SDT)object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), sdt, "txt_");
    }

    @Override
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
    }
    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setTypeSDT() {
        String type = getIntent().getStringExtra(DoctorVetApp.REQUEST_CREATE_OBJECT);
        if (type.equalsIgnoreCase(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name()))
            typeSDT = DoctorVetApp.Sdt_type.SYMPTOM;
        else if (type.equalsIgnoreCase(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name()))
            typeSDT = DoctorVetApp.Sdt_type.DIAGNOSTIC;
        else
            typeSDT = DoctorVetApp.Sdt_type.TREATMENT;
    }

    private String getSubtitleSDT() {
        if (typeSDT == DoctorVetApp.Sdt_type.SYMPTOM)
            return "Nuevo síntoma";

        if (typeSDT == DoctorVetApp.Sdt_type.DIAGNOSTIC)
            return "Nuevo diagnóstico";

        return "Nuevo tratamiento";
    }

    private String getIntentValue() {
        return getIntent().getStringExtra(DoctorVetApp.REQUEST_CREATE_OBJECT);
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }

}
