package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.RegionsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditProviderActivity extends EditBaseActivity {

    private static final String TAG = "EditProviderActivity";
    private TextInputLayout txtName;
    private AutoCompleteTextView actv_region;
    private TextInputLayout txtRegion;
    private TextInputLayout txtEmail;
    private TextInputLayout txtPhone;
    
    private Product_provider provider = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_provider);
        txtName = findViewById(R.id.txt_name);
        actv_region = findViewById(R.id.actv_region);
        txtRegion = findViewById(R.id.txt_region);
        txtEmail = findViewById(R.id.txt_email);
        txtPhone = findViewById(R.id.txt_phone);
        DoctorVetApp.get().markRequired(txtName);
        DoctorVetApp.get().markRequired(txtRegion);
        DoctorVetApp.get().markRequired(txtEmail);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getRegions(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultArrayList) {
                    if (resultArrayList != null) {
                        setRequestCompleted();
                        setRegionsAdapter(resultArrayList);
                    }
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()) {
            toolbar_title.setText(R.string.edit_provider);
            toolbar_subtitle.setText(R.string.creating_provider);
        } else {
            toolbar_title.setText(R.string.new_provider);
            toolbar_subtitle.setText(R.string.creating_provider);
        }

        Product_provider provider = getObject();
        setObjectToUI(provider);
        implementTakePhoto(getResourceObject());

//        ImageView iconSearch_region = findViewById(R.id.img_search_region);
//        iconSearch_region.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditProviderActivity.this, SearchRegionActivity.class);
//                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
//            }
//        });
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
        outState.putString(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name());
        provider = MySqlGson.getGson().fromJson(objectInString, Product_provider.class);

        String regionsInString = DoctorVetApp.get().readFromDisk("regions");
        ArrayList<Region> regions = MySqlGson.getGson().fromJson(regionsInString, new TypeToken<List<Region>>(){}.getType());
        setRegionsAdapter(regions);
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
//            txtRegion.getEditText().setText(region.getFriendly_name());
//            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtPhone);
//        }
//    }

    @Override
    protected void save() {
        if (!validateName() || !validateRegion() || !validateEmail())
            return;

        showWaitDialog();

        final Product_provider provider = getObjectFromUI();
        final String provider_json_object = MySqlGson.getGson().toJson(provider);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Product_provider response_provider = MySqlGson.getGson().fromJson(data, Product_provider.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(provider.getResources(), response_provider.getResources());

                    if (getIntent().hasExtra(DoctorVetApp.REQUEST_CREATE_OBJECT)) {
                        Intent dataBackIntent = new Intent();
                        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name(), response);
                        setResult(RESULT_OK, dataBackIntent);
                    } else if (!isUpdate()) {
                        Intent activity = new Intent(EditProviderActivity.this, ViewProviderActivity.class);
                        activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_ID.name(), response_provider.getId());
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
                return provider_json_object.getBytes();
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
        if (isUpdate())
            return NetworkUtils.buildProviderUrl(getObject().getId(), "RETURN_OBJECT");

        return NetworkUtils.buildProviderUrl(null, "RETURN_OBJECT");
    }

    @Override
    protected Product_provider getObjectFromUI() {
        final Product_provider productProvider = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), productProvider, true, "txt_", true, getApplicationContext());
        return productProvider;
    }

    @Override
    protected Product_provider getObject() {
        if (provider == null) {
            if (isNew())
                provider = new Product_provider();
            else
                provider = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_PROVIDER_OBJ.name()), Product_provider.class);
        }

        return provider;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Product_provider productProvider = (Product_provider) object;
        loadThumb(provider.getThumb_url());
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), productProvider, "txt_");

        if (provider.getRegion() != null)
            actv_region.setText(productProvider.getRegion().getFriendly_name());
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
        return "products_providers";
    }

    private void setRegionsAdapter(ArrayList<Region> regions) {
        RegionsAdapter regionsAdapter = new RegionsAdapter(regions);
        actv_region.setAdapter(regionsAdapter.getArrayAdapter(EditProviderActivity.this));
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
        DoctorVetApp.get().setAllWidthToDropDown(actv_region, EditProviderActivity.this);
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateEmail() {
        return HelperClass.validateEmail(txtEmail, false);
    }
    private boolean validateRegion() {
        return DoctorVetApp.get().validateExistence(txtRegion, getObject().getRegion(), "friendly_name",false);
    }

}
