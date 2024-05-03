package com.xionce.doctorvetServices;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Pet_pelage;
import com.xionce.doctorvetServices.data.Pet_race;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditPetPelageActivity extends EditBaseActivity {

    private static final String TAG = "EditMascotaPelajeActivi";
    private TextInputLayout txtName;
    private Pet_pelage petPelage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_pelage);
        toolbar_title.setText(R.string.nuevo_pelage);
        toolbar_subtitle.setText(R.string.ingresando_nuevo_pelage);
        txtName = findViewById(R.id.txt_name);
        DoctorVetApp.get().markRequired(txtName);
        hideToolbarImage();

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Pet_pelage petpelage = getObject();
        setObjectToUI(petpelage);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PET_PELAGE_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PET_PELAGE_OBJ.name());
        petPelage = MySqlGson.getGson().fromJson(objectInString, Pet_pelage.class);
    }

    @Override
    protected void save() {
        if (!validateName())
            return;

        showWaitDialog();

        final Pet_pelage petpelage_sugerido = getObjectFromUI();
        final String pelajeJsonObject = MySqlGson.getGson().toJson(petpelage_sugerido);
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
                return pelajeJsonObject.getBytes();
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
        return NetworkUtils.buildGetPetsPelajesUrl(null, null);
    }

    @Override
    protected Pet_pelage getObjectFromUI() {
        Pet_pelage petPelage = getObject();
        petPelage.setName(txtName.getEditText().getText().toString());
        return petPelage;
    }

    @Override
    protected Pet_pelage getObject() {
        if (petPelage == null)
            petPelage = new Pet_pelage();

        return petPelage;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_pelage petPelage = (Pet_pelage) object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petPelage, "txt_");
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
