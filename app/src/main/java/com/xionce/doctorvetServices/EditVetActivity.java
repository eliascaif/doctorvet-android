package com.xionce.doctorvetServices;

import android.content.Intent;

import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputLayout;

import android.os.Bundle;

import androidx.annotation.NonNull;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.RegionsAdapter;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditVetActivity extends EditBaseActivity {

    private static final String TAG = "EditVetActivity";
    private TextInputLayout txtName;
    private TextInputLayout txtRegion;
    private AutoCompleteTextView actv_region;
    private TextInputLayout txtEmail;
    private TextInputLayout txtPhone;
    private RadioButton radioMetricSystem;
    private RadioButton radioEnglishSystem;
    private RadioButton radioHourFormat24;
    private RadioButton radioHourFormatAmPm;
    private RadioButton radioMobileServicesYes;
    private RadioButton radioMobileServicesNo;
    private Spinner spinner_owner_naming;
    private Spinner spinner_pet_naming;
    private RadioButton radioEmailMessagingYes;
    private RadioButton radioEmailMessagingNo;

    private Vet vet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_vet);
        txtName = findViewById(R.id.txt_name);
        TextInputLayout txtAddress = findViewById(R.id.txt_address);
        txtRegion = findViewById(R.id.txt_region);
        actv_region = findViewById(R.id.actv_region);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        TextInputLayout txtNotes = findViewById(R.id.txt_notes);
        radioMetricSystem = findViewById(R.id.radio_metric_system);
        radioEnglishSystem = findViewById(R.id.radio_english_system);
        radioHourFormat24 = findViewById(R.id.radio_24_hs);
        radioHourFormatAmPm = findViewById(R.id.radio_am_pm);
        radioMobileServicesYes = findViewById(R.id.radio_mobile_services_yes);
        radioMobileServicesNo = findViewById(R.id.radio_mobile_services_no);
        spinner_owner_naming = findViewById(R.id.spinner_owner_naming);
        spinner_pet_naming = findViewById(R.id.spinner_pet_naming);
        radioEmailMessagingYes = findViewById(R.id.radio_email_messaging_yes);
        radioEmailMessagingNo = findViewById(R.id.radio_email_messaging_no);
        DoctorVetApp.get().markRequired(txtName);
        DoctorVetApp.get().markRequired(txtRegion);
        DoctorVetApp.get().markRequired(txtEmail);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getRegions(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultArrayList) {
                    setRequestCompleted();
                    setRegionsAdapter(resultArrayList);
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isInitCreate()) {
            hideToolbarImage();
        }

        if (isUpdate()) {
            toolbar_title.setText(R.string.edit_vet);
            toolbar_subtitle.setText(R.string.edit_subtitle);
            //no puede modificarse el email
            txtEmail.setVisibility(View.GONE);
        } else {
            toolbar_title.setText(R.string.new_vet);
            toolbar_subtitle.setText(R.string.new_subtitle);
        }
        
        Vet vet = getObject();
        setObjectToUI(vet);
        implementTakePhoto(vet);

        ArrayAdapter<DoctorVetApp.Owners_naming> ownersNamingArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DoctorVetApp.Owners_naming.values());
        ownersNamingArrayAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinner_owner_naming.setAdapter(ownersNamingArrayAdapter);
        int ownerNamingSelectedIndex = ownersNamingArrayAdapter.getPosition(getObject().getOwner_naming());
        spinner_owner_naming.setSelection(ownerNamingSelectedIndex);

        ArrayAdapter<DoctorVetApp.Pets_naming> petsNamingArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DoctorVetApp.Pets_naming.values());
        petsNamingArrayAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinner_pet_naming.setAdapter(petsNamingArrayAdapter);
        int petNamingSelectedIndex = petsNamingArrayAdapter.getPosition(getObject().getPet_naming());
        spinner_pet_naming.setSelection(petNamingSelectedIndex);

        ImageView iconSearch_region = findViewById(R.id.img_search_region);
        iconSearch_region.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditVetActivity.this, SearchRegionActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        //
        txtAddress.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtAddress.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);

        txtNotes.getEditText().setImeOptions(EditorInfo.IME_ACTION_DONE);
        txtNotes.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
        txtNotes.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save();
                    return true;
                }
                return false;
            }
        });

        if (!isUpdate())
            txtName.requestFocus();

    }

    @Override
    protected void onAllRequestsCompleted() {
        if (isUpdate()) return;
        DoctorVetApp.get().showKeyboard();
        txtName.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("vet", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("vet");
        vet = MySqlGson.getGson().fromJson(objectInString, Vet.class);

        String regionsInString = DoctorVetApp.get().readFromDisk("regions");
        ArrayList<Region> regions = MySqlGson.getGson().fromJson(regionsInString, new TypeToken<List<Region>>(){}.getType());
        setRegionsAdapter(regions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //busqueda regiones
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            Region region = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name()), Region.class);
            getObject().setRegion(region);
            txtRegion.getEditText().setText(region.getFriendly_name());
            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtPhone);
        }
    }

    @Override
    protected void save() {
        if (!validateName() || !validateRegion() || !validateEmail())
            return;

        if (is_branch_create()) {
            save_branch();
            return;
        }

        showWaitDialog();

        final Vet vet = getObjectFromUI();
        final String vetJsonObject = MySqlGson.getPostJsonString(vet);

        //not tokenized request
        StringRequest stringRequest = new StringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String data = MySqlGson.getDataFromResponse(response).toString();
                Vet response_vet = MySqlGson.getGson().fromJson(data, Vet.class);
                if (isInitCreate()) {
                    //call final auth
                    DoctorVetApp.get().doFinalAuthPost(response_vet.getId(), EditVetActivity.this, LoginActivity.class);
                }
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
            } finally {
                hideWaitDialog();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return vetJsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void save_branch() {
        showWaitDialog();

        final Vet vet = getObjectFromUI();
        final String vetJsonObject = MySqlGson.getPostJsonString(vet);

        //tokenized request
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                Intent intent = new Intent(EditVetActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
            } finally {
                hideWaitDialog();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return vetJsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {
        if (!validateName() || !validateRegion() || !validateEmail())
            return;

        showWaitDialog();

        final Vet vet = getObjectFromUI();
        final String veterinariaJsonObject = MySqlGson.getPostJsonString(vet);

        //tokenized request
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                Intent intent = new Intent(EditVetActivity.this, LoginActivity.class);
                startActivity(intent);
                finishAndRemoveTask();
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
            } finally {
                hideWaitDialog();
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            hideWaitDialog();
        })
        {
            @Override
            public byte[] getBody() {
                return veterinariaJsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected URL getUrl() {
        if (isUpdate())
            return NetworkUtils.buildVetUrl(null, getObject().getId());

        if (isInitCreate())
            return NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.CREATE_VET, null);

        return NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.CREATE_BRANCH, null);
    }

    @Override
    protected Vet getObjectFromUI() {
        Vet vet = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), vet, true, "txt_", true, getApplicationContext());
        vet.setUnit_system(radioMetricSystem.isChecked() ? DoctorVetApp.unit_system.METRIC.name() : DoctorVetApp.unit_system.ENGLISH.name());
        vet.setUser_email(DoctorVetApp.get().preferences_getUserEmail());
        vet.setHour_format(radioHourFormat24.isChecked() ? "24_HS" : DoctorVetApp.hour_format.AM_PM.name()); //enumeration members cant start with a number
        vet.setMobile_services(radioMobileServicesYes.isChecked() ? 1 : 0);
        vet.setOwner_naming(DoctorVetApp.Owners_naming.getEnumVal(spinner_owner_naming.getSelectedItem().toString()));
        vet.setPet_naming(DoctorVetApp.Pets_naming.getEnumVal(spinner_pet_naming.getSelectedItem().toString()));
        vet.setEmail_messaging(radioEmailMessagingYes.isChecked() ? 1 : 0);
        return vet;
    }

    @Override
    protected Vet getObject() {
        if (vet == null) {
            if (isNew())
                vet = new Vet().Init();
            else
                vet = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.VET_OBJ.name()), Vet.class);
        }

        return vet;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Vet vet = (Vet) object;
        loadThumb(vet.getThumb_url());
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), vet, "txt_");

        if (vet.getRegion() != null)
            txtRegion.getEditText().setText(vet.getRegion().getFriendly_name());

        if (vet.getUnit_system() != null) {
            if (vet.getUnit_system().equals(DoctorVetApp.unit_system.METRIC.name()))
                radioMetricSystem.setChecked(true);
            else
                radioEnglishSystem.setChecked(true);
        }

        if (vet.getHour_format() != null) {
            if (vet.getHour_format().equals(DoctorVetApp.hour_format.AM_PM.name()))
                radioHourFormatAmPm.setChecked(true);
            else
                radioHourFormat24.setChecked(true);
        }

        if (vet.getMobile_services() != null) {
            if (vet.getMobile_services().equals(1))
                radioMobileServicesYes.setChecked(true);
            else
                radioMobileServicesNo.setChecked(true);
        }

        if (vet.getEmail_messaging() != null) {
            if (vet.getEmail_messaging().equals(1))
                radioEmailMessagingYes.setChecked(true);
            else
                radioEmailMessagingNo.setChecked(true);
        }

    }

    @Override
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
        super.remove_thumb(getResourceObject());
        //Glide.with(getApplicationContext()).load(R.drawable.ic_store_holo_dark).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(toolbar_image);
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return getObject();
    }

    @Override
    protected String getUploadTableName() {
        return "vets";
    }

    private void setRegionsAdapter(ArrayList<Region> regions) {
        RegionsAdapter regionsAdapter = new RegionsAdapter(regions);
        actv_region.setAdapter(regionsAdapter.getArrayAdapter(EditVetActivity.this));
        actv_region.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setRegion((Region)adapterView.getItemAtPosition(i));
                txtPhone.requestFocus();
            }
        });
        actv_region.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setRegion(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_region);
        DoctorVetApp.get().setAllWidthToDropDown(actv_region, EditVetActivity.this);
    }

    private boolean isInitCreate() {
        Intent intent = getIntent();
        if (intent.hasExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE))
            if (intent.getIntExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, 0) == DoctorVetApp.REQUEST_INIT_CREATE_VET)
                return true;

        return  false;
    }
    private boolean is_branch_create() {
        Intent intent = getIntent();
        return intent.hasExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE)
                && (intent.getIntExtra(HelperClass.INTENT_EXTRA_REQUEST_CODE, 0) == DoctorVetApp.REQUEST_CREATE_BRANCH_VET);
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateEmail() {
        return HelperClass.validateEmpty(txtEmail) && HelperClass.validateEmail(txtEmail, false);
    }
    private boolean validateRegion() {
        return DoctorVetApp.get().validateExistence(txtRegion, getObject().getRegion(), "friendly_name",false);
    }

}
