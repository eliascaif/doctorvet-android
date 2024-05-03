package com.xionce.doctorvetServices;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Pet_character;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditPetCharacterActivity extends EditBaseActivity {

    private static final String TAG = "EditPetCharacterActivit";
    private TextInputLayout txtName;

    private Pet_character petCharacter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_character);
        toolbar_title.setText(R.string.nuevo_character);
        toolbar_subtitle.setText(R.string.ingresando_nuevo_character);
        txtName = findViewById(R.id.txt_name);
        DoctorVetApp.get().markRequired(txtName);
        hideToolbarImage();

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Pet_character petcharacter = getObject();
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petcharacter, "txt_");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PET_CHARACTER_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PET_CHARACTER_OBJ.name());
        petCharacter = MySqlGson.getGson().fromJson(objectInString, Pet_character.class);
    }

    @Override
    protected void save() {
        if (!validateName())
            return;

        showWaitDialog();

        final Pet_character petcharacter_sugerido = getObjectFromUI();
        final String caractererJsonObject = MySqlGson.getGson().toJson(petcharacter_sugerido);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                if (success.equalsIgnoreCase("success"))
                    finish();
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
                return caractererJsonObject.getBytes();
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
        return NetworkUtils.buildInsertCaracterUrl();
    }

    @Override
    protected Pet_character getObjectFromUI() {
        Pet_character petCharacter = getObject();
        petCharacter.setName(txtName.getEditText().getText().toString());
        return petCharacter;
    }

    @Override
    protected Pet_character getObject() {
        if (petCharacter == null)
            petCharacter = new Pet_character();

        return petCharacter;
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

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }

}
