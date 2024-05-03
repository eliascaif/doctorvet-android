package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.Region_suggested;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;

import java.net.URL;

public class EditRegionActivity extends EditBaseActivity {

    private static final String TAG = "EditRegionActivity";
    private TextInputLayout txtCiudad;
    private TextInputLayout txtProvincia;
    private TextInputLayout txtPais;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_region);
        txtCiudad = findViewById(R.id.txt_ciudad);
        txtProvincia = findViewById(R.id.txt_provincia);
        txtPais = findViewById(R.id.txt_pais);
        DoctorVetApp.get().markRequired(txtCiudad);
        DoctorVetApp.get().markRequired(txtProvincia);
        DoctorVetApp.get().markRequired(txtPais);
        hideToolbarImage();

        final Region region = getObject();
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), region, "txt_");

        toolbar_title.setText(R.string.nueva_region);
        toolbar_subtitle.setText(R.string.ingresando_nueva_region);

        if (!isUpdate())
            txtCiudad.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Region region = getObjectFromUI();
        getIntent().putExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name(), MySqlGson.getGson().toJson(region));
    }

    private Region_suggested getRegion() {
        Intent intent = getIntent();
        Region_suggested region;
        if (intent.hasExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name())) {
            region = MySqlGson.getGson().fromJson(intent.getStringExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name()), Region_suggested.class);
        } else {
            region = new Region_suggested();
        }

        return region;
    }

    @Override
    protected void save() {
        if (!validateCiudad() || !validateProvincia() || !validatePais())
            return;

        showWaitDialog();

        final Region_suggested region_suggested = getObjectFromUI();
        final String regionJsonObject = MySqlGson.getGson().toJson(region_suggested);

        StringRequest stringRequest = new StringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String data = MySqlGson.getDataFromResponse(response).toString();
                //Response_insert response_insert = MySqlGson.getGson().fromJson(data, Response_insert.class);
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, /*EditRegionActivity.this,*/ TAG, true, response);
            } finally {
                hideWaitDialog();
                finish();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, /*EditRegionActivity.this,*/ TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return regionJsonObject.getBytes();
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
        return NetworkUtils.buildRegionUrl();
    }

    @Override
    protected Region_suggested getObjectFromUI() {
        //no utilizo helper por que region_sugerido extiende de region
        final Region_suggested region_suggested = getObject();
        region_suggested.setCity(txtCiudad.getEditText().getText().toString());
        region_suggested.setProvince(txtProvincia.getEditText().getText().toString());
        region_suggested.setCountry(txtPais.getEditText().getText().toString());
//        region_suggested.setid_user(DoctorVetApp.getInstance().getUser().getId());
//        region_suggested.setid_vet(DoctorVetApp.getInstance().getUser().get_id_vet_en_uso());
        return region_suggested;
    }

    @Override
    protected Region_suggested getObject() {
        return getRegion();
    }

    @Override
    protected void setObjectToUI(Object object) {

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

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {

    }

    private boolean validateCiudad() {
        return HelperClass.validateEmpty(txtCiudad);
    }
    private boolean validateProvincia() {
        return HelperClass.validateEmpty(txtProvincia);
    }
    private boolean validatePais() {
        return HelperClass.validateEmpty(txtPais);
    }

}
