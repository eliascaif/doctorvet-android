package com.xionce.doctorvetServices;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Pet_especies;
import com.xionce.doctorvetServices.data.Pet_race;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditPetRaceActivity extends EditBaseActivity {

    private static final String TAG = "EditMascotaRazaActivity";
    private TextInputLayout txtName;
    private TextInputLayout txtEspecie;
    private Pet_race petRace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_race);
        toolbar_title.setText(R.string.nueva_race);
        toolbar_subtitle.setText(R.string.ingresando_nueva_race);
        txtName = findViewById(R.id.txt_name);
        txtEspecie = findViewById(R.id.txt_especie);
        DoctorVetApp.get().markRequired(txtName);
        DoctorVetApp.get().markRequired(txtEspecie);
        hideToolbarImage();

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Pet_race petRace = getObject();
        setObjectToUI(petRace);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PET_RACE_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PET_RACE_OBJ.name());
        petRace = MySqlGson.getGson().fromJson(objectInString, Pet_race.class);
    }

    @Override
    protected void save() {
        if (!validateName() || !validateEspecies())
            return;

        showWaitDialog();

        final Pet_race petrace_sugerido = getObjectFromUI();
        final String razaJsonObject = MySqlGson.getGson().toJson(petrace_sugerido);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                if (success.equalsIgnoreCase("success"))
                    finish();
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, /*EditPetRaceActivity.this,*/ TAG, true, response);
            } finally {
                hideWaitDialog();
                finish();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, /*EditPetRaceActivity.this,*/ TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return razaJsonObject.getBytes();
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
        return NetworkUtils.buildGetPetsRazasUrl(null, null);
    }

    @Override
    protected Pet_race getObjectFromUI() {
        Pet_race petRace = getObject();
        petRace.setName(txtName.getEditText().getText().toString());
        petRace.getEspecies().setName(txtEspecie.getEditText().getText().toString());
        return petRace;
    }

    @Override
    protected Pet_race getObject() {
        if (petRace == null) {
            petRace = new Pet_race();
        }

        return petRace;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_race petRace = (Pet_race)object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petRace, "txt_");
        txtEspecie.getEditText().setText(petRace.getEspecies().getName());
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

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateEspecies() {
        return HelperClass.validateEmpty(txtEspecie);
    }

}
