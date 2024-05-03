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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Pet_study_item;
import com.xionce.doctorvetServices.data.Product_category;
import com.xionce.doctorvetServices.data.Product_unit;
import com.xionce.doctorvetServices.data.Products_unitsAdapter;
import com.xionce.doctorvetServices.data.SDT;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditStudyItemActivity extends EditBaseActivity {

    private static final String TAG = "EditPetStudyItemActivit";
    private TextInputLayout txtName;
    private TextInputLayout txtMeasureUnit;
    private AutoCompleteTextView actvMeasureUnit;
    private TextInputLayout txtMin;

    private Pet_study_item item = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_study_item);
        txtName = findViewById(R.id.txt_name);
        txtMeasureUnit = findViewById(R.id.txt_unit);
        actvMeasureUnit = findViewById(R.id.actv_unit);
        txtMin = findViewById(R.id.txt_min);
        DoctorVetApp.get().markRequired(txtName);
        hideToolbarImage();

        toolbar_title.setText(R.string.nuevo);
        toolbar_subtitle.setText("Nuevo ítem");

        initializeInitRequestNumber(1);
        URL url = NetworkUtils.buildGetProductsUnitsMinUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                            String json = HelperClass.compressedBase64ToString(data);
                            ArrayList<Product_unit> productUnits = MySqlGson.getGson().fromJson(json, new TypeToken<List<Product_unit>>(){}.getType());
                            setProductsUnitsAdapter(productUnits);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        } finally {
                            setRequestCompleted();
                            hideProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        setRequestCompleted();
                        hideProgressBar();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Pet_study_item petStudyItem = getObject();
        setObjectToUI(petStudyItem);

        ImageView iconSearch_measure_unit = findViewById(R.id.img_search_unit);
        iconSearch_measure_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditStudyItemActivity.this, SearchProductMeasureUnitActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
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
        outState.putString("pet_study_item", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("pet_study_item");
        item = MySqlGson.getGson().fromJson(objectInString, Pet_study_item.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //busqueda
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name())) {
                Product_unit productunit = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name()), Product_unit.class);
                getObject().setUnit(productunit);
                txtMeasureUnit.getEditText().setText(productunit.getName());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtMin);
            }
        }
    }

    @Override
    protected void save() {
        if (!validateName())
            return;

        showWaitDialog();

        final Pet_study_item item = getObjectFromUI();
        final String itemJson = MySqlGson.getGson().toJson(item);

        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), response -> {
            try {
                String success = MySqlGson.getStatusFromResponse(response);
                Integer id = Integer.parseInt(MySqlGson.getDataFromResponse(response).getAsJsonObject().get("id").toString());
                item.setId(id);

                if (getIntent().hasExtra(DoctorVetApp.REQUEST_CREATE_OBJECT)) {
                    Intent dataBackIntent = new Intent();
                    dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name(), MySqlGson.getGson().toJson(item));
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
                return itemJson.getBytes();
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
        return NetworkUtils.buildStudyUrl(null, null, null, null, true);
    }

    @Override
    protected Pet_study_item getObjectFromUI() {
        Pet_study_item item1 = getObject();
        item1.setName(txtName.getEditText().getText().toString());
        return item1;
    }

    @Override
    protected Pet_study_item getObject() {
        if (item == null) {
            if (isUpdate()) {
                item = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name()), Pet_study_item.class);
            } else {
                item = new Pet_study_item();
            }
        }

        return item;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_study_item petStudyItem = (Pet_study_item) object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petStudyItem, "txt_");
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
        return "pets_studies";
    }

    private void setProductsUnitsAdapter(ArrayList<Product_unit> productUnits) {
        Products_unitsAdapter productsUnitsAdapter = new Products_unitsAdapter(productUnits);
        actvMeasureUnit.setAdapter(productsUnitsAdapter.getArrayAdapter(EditStudyItemActivity.this));
        actvMeasureUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product_unit productUnit = (Product_unit) adapterView.getItemAtPosition(i);
                getObject().setUnit(productUnit);
            }
        });
        actvMeasureUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setUnit(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvMeasureUnit);
        DoctorVetApp.get().setAllWidthToDropDown(actvMeasureUnit, EditStudyItemActivity.this);
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }

}
