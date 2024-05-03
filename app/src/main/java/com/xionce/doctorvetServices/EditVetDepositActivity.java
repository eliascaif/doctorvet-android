package com.xionce.doctorvetServices;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditVetDepositActivity extends EditBaseActivity {

    private static final String TAG = "EditVetDepositActivity";
    private TextInputLayout txtName;
    private Vet_deposit vetDeposit = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_vet_deposit);
        txtName = findViewById(R.id.txt_name);
        DoctorVetApp.get().markRequired(txtName);
        hideToolbarImage();

        if (isUpdate()) {
            toolbar_title.setText(R.string.edit_deposit);
            toolbar_subtitle.setText(R.string.editing_deposit);
        } else {
            toolbar_title.setText(R.string.new_deposit);
            toolbar_subtitle.setText(R.string.adding_new_deposit);
        }

        Vet_deposit vetDeposit = getObject();
        setObjectToUI(vetDeposit);

        txtName.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("vet_deposit", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("vet_deposit");
        vetDeposit = MySqlGson.getGson().fromJson(objectInString, Vet_deposit.class);
    }

    @Override
    protected void save() {
        if (!validateName())
            return;

        showWaitDialog();

        Vet_deposit vetDeposit = getObjectFromUI();
        String vetDepositJsonObject = MySqlGson.postGson().toJson(vetDeposit);
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
                return vetDepositJsonObject.getBytes();
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
        if (isUpdate())
            return NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.UPDATE_DEPOSIT, getObject().getId());

        return NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.CREATE_DEPOSIT, null);
    }

    @Override
    protected Vet_deposit getObjectFromUI() {
        final Vet_deposit vetDeposit = getObject();
        vetDeposit.setName(txtName.getEditText().getText().toString());
        return vetDeposit;
    }

    @Override
    protected Vet_deposit getObject() {
        if (vetDeposit == null) {
            if (isUpdate()) {
                vetDeposit = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.DEPOSIT_OBJ.name()), Vet_deposit.class);
            } else {
                vetDeposit = new Vet_deposit();
            }
        }

        return vetDeposit;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Vet_deposit vetDeposit = (Vet_deposit) object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), vetDeposit, "txt_");
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
