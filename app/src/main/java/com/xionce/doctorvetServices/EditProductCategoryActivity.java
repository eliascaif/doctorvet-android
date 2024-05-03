package com.xionce.doctorvetServices;

import android.os.Bundle;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Product_category;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;

public class EditProductCategoryActivity extends EditBaseActivity {

    private static final String TAG = "EditProductCategoryActi";
    private TextInputLayout txtName;
    private CheckBox chkAlimentos;
    private CheckBox chkServicios;
    
    private Product_category category = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_product_category);
        toolbar_title.setText(R.string.nueva_categoria);
        toolbar_subtitle.setText(R.string.ingresando_nueva_categoria);
        txtName = findViewById(R.id.txt_name);
        chkAlimentos = findViewById(R.id.chk_es_categoria_alimentos);
        chkServicios = findViewById(R.id.chk_es_categoria_servicios);
        DoctorVetApp.get().markRequired(txtName);
        hideToolbarImage();

        Product_category productcategory = getObject();
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), productcategory, "txt_");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(DoctorVetApp.INTENT_VALUES.PRODUCT_CATEGORY_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PRODUCT_CATEGORY_OBJ.name());
        category = MySqlGson.getGson().fromJson(objectInString, Product_category.class);
    }

    @Override
    protected void save() {
        if (!validateName())
            return;

        showWaitDialog();

        final Product_category category = getObjectFromUI();
        final String categoriaJsonObject = MySqlGson.getGson().toJson(category);
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
                return categoriaJsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update(){
    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildProductoCategoriaUrl();
    }

    @Override
    protected Product_category getObjectFromUI() {
        final Product_category category = getObject();
        category.setName(txtName.getEditText().getText().toString());

        category.setIs_food_category(0);
        if (chkAlimentos.isChecked())
            category.setIs_food_category(1);

        category.setIs_service_category(0);
        if (chkServicios.isChecked())
            category.setIs_service_category(1);

        return category;
    }

    @Override
    protected Product_category getObject() {
        if (category == null)
            category = new Product_category();

        return category;
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
