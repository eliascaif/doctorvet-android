package com.xionce.doctorvetServices;

import android.content.Intent;

import com.android.volley.Request;
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
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.xionce.doctorvetServices.data.FinanceTypesFiscalAdapter;
import com.xionce.doctorvetServices.data.Finance_types_fiscal;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.RegionsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;

public class EditOwnerActivity extends EditBaseActivity {

    private static final String TAG = "EditOwnerActivity";
    private TextInputLayout txtName;
    private TextInputLayout txtRegion;
    private AutoCompleteTextView actv_region;
    private TextInputLayout txtEmail;
    private TextInputLayout txtPhone;
    private AutoCompleteTextView actvFiscalType;

    private Owner owner = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_owner);
        txtName = findViewById(R.id.txt_name);
        TextInputLayout txtAddress = findViewById(R.id.txt_address);
        txtRegion = findViewById(R.id.txt_region);
        actv_region = findViewById(R.id.actv_region);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        actvFiscalType = findViewById(R.id.actv_fiscal_type);
        TextInputLayout txtNotes = findViewById(R.id.txt_notes);
        DoctorVetApp.get().markRequired(txtName);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getOwnersForInput(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Owner.Owners_for_input ownersForInput = (Owner.Owners_for_input)resultObject;
                    setRegionsAdapter(ownersForInput.getRegions());
                    setFiscalTypesAdapter(ownersForInput.getFinance_types_fiscal());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()) {
            toolbar_title.setText("Editar " + DoctorVetApp.get().getOwnerNaming().toLowerCase());
            toolbar_subtitle.setText(R.string.edit_subtitle);
        } else {
            toolbar_title.setText("Nuevo " + DoctorVetApp.get().getOwnerNaming().toLowerCase());
            toolbar_subtitle.setText(R.string.new_subtitle);
        }

        Owner owner = getObject();
        setObjectToUI(owner);
        implementTakePhoto(getResourceObject());

//        ImageView iconSearch_region = findViewById(R.id.img_search_region);
//        iconSearch_region.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditOwnerActivity.this, SearchRegionActivity.class);
//                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
//            }
//        });

        txtAddress.getEditText().setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtAddress.getEditText().setRawInputType(InputType.TYPE_CLASS_TEXT);
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
        outState.putString("owner", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("owner");
        owner = MySqlGson.getGson().fromJson(objectInString, Owner.class);
        String ownersForInputInString = DoctorVetApp.get().readFromDisk("owners_for_input");
        Owner.Owners_for_input ownersForInput = MySqlGson.getGson().fromJson(ownersForInputInString, Owner.Owners_for_input.class);
        setRegionsAdapter(ownersForInput.getRegions());
        setFiscalTypesAdapter(ownersForInput.getFinance_types_fiscal());
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) return;
//
//        //busqueda regiones
//        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
//            Region region = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.REGION_OBJ.name()), Region.class);
//            getObject().setRegion(region);
//            actv_region.setText(region.getFriendly_name());
//            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtPhone);
//        }
//    }

    @Override
    protected void save() {
        if (!validateName() || !validateRegion() || !validateEmail())
            return;

        showWaitDialog();

        final Owner owner = getObjectFromUI();
        final String owner_json_object = MySqlGson.postGson().toJson(owner);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Owner response_owner = MySqlGson.getGson().fromJson(data, Owner.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(owner.getResources(), response_owner.getResources());

                    if (getIntent().hasExtra(DoctorVetApp.REQUEST_CREATE_OBJECT)) {
                        Intent dataBackIntent = new Intent();
                        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getGson().toJson(response_owner));
                        setResult(RESULT_OK, dataBackIntent);
                    } else if (!isUpdate()) {
                        Intent activity = new Intent(EditOwnerActivity.this, ViewOwnerActivity.class);
                        activity.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), response_owner.getId());
                        startActivity(activity);
                    }
                    finish();
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
        })
        {
            @Override
            public byte[] getBody() {
                return owner_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {
        save();
    }

    @Override
    protected URL getUrl() {
        if (getMethod() == Request.Method.POST)
            return NetworkUtils.buildOwnerUrl(null, null, "RETURN_OBJECT");

        return NetworkUtils.buildOwnerUrl(getObject().getId(), NetworkUtils.OwnersUpdateType.UPDATE, "RETURN_OBJECT");
    }

    @Override
    protected Owner getObjectFromUI() {
        Owner owner = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), owner, true, "txt_", true, getApplicationContext());
        return owner;
    }

    @Override
    protected Owner getObject() {
        if (owner == null) {
            if (isNew())
                owner = new Owner();
            else
                owner = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
        }

        return owner;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Owner owner = (Owner) object;
        loadThumb(owner.getThumb_url());
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), owner, "txt_");

        if (owner.getRegion() != null)
            actv_region.setText(owner.getRegion().getFriendly_name());
    }

    @Override
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
        super.remove_thumb(getResourceObject());
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return getObject();
    }

    @Override
    protected String getUploadTableName() {
        return "owners";
    }

    private void setRegionsAdapter(ArrayList<Region> regions) {
        RegionsAdapter regionsAdapter = new RegionsAdapter(regions);
        actv_region.setAdapter(regionsAdapter.getArrayAdapter(EditOwnerActivity.this));
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
        DoctorVetApp.get().setAllWidthToDropDown(actv_region, EditOwnerActivity.this);
    }
    private void setFiscalTypesAdapter(ArrayList<Finance_types_fiscal> fiscalTypes) {
        FinanceTypesFiscalAdapter fiscalTypesAdapter = new FinanceTypesFiscalAdapter(fiscalTypes);
        actvFiscalType.setAdapter(fiscalTypesAdapter.getArrayAdapter(EditOwnerActivity.this));
        actvFiscalType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setFiscal_type((Finance_types_fiscal) adapterView.getItemAtPosition(i));
            }
        });
        actvFiscalType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setFiscal_type(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvFiscalType);
        DoctorVetApp.get().setAllWidthToDropDown(actvFiscalType, EditOwnerActivity.this);
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateEmail() {
        return HelperClass.validateEmail(txtEmail, true);
    }
    private boolean validateRegion() {
        return DoctorVetApp.get().validateExistence(txtRegion, getObject().getRegion(), "friendly_name", true);
    }

}
