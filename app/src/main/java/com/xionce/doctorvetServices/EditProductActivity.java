package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.Product_category;
import com.xionce.doctorvetServices.data.Product_unit;
import com.xionce.doctorvetServices.data.Products_categoriesAdapter;
import com.xionce.doctorvetServices.data.Products_unitsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;

public class EditProductActivity extends EditBaseActivity {

    private static final String TAG = "EditProductActivity_2";
    private TextInputLayout txtName;
    private TextInputLayout txtCategories;
    private AutoCompleteTextView actvCategory;
    private TextInputLayout txtMeasureUnit;
    private AutoCompleteTextView actvMeasureUnit;
    private TextInputLayout txtComplexUnitQuantity;
    private TextInputLayout txtBarCode;
    private TextInputLayout txtQRCode;
    private Spinner spinner_p1;
    private Spinner spinner_p2;
    private Spinner spinner_p3;

    private Product product = null;
    private Products_categoriesAdapter selectedCategoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_product);
        txtName = findViewById(R.id.txt_name);
        txtCategories = findViewById(R.id.txt_categories);
        actvCategory = findViewById(R.id.actv_category);
        RecyclerView recyclerView_selected_categories = findViewById(R.id.recycler_categories);
        txtMeasureUnit = findViewById(R.id.txt_unit);
        actvMeasureUnit = findViewById(R.id.actv_unit);
        txtComplexUnitQuantity = findViewById(R.id.txt_complex_unit_quantity);
        txtBarCode = findViewById(R.id.txt_bar_code);
        txtQRCode = findViewById(R.id.txt_qr_code);
        spinner_p1 = findViewById(R.id.spinner_format_p1);
        spinner_p2 = findViewById(R.id.spinner_format_p2);
        spinner_p3 = findViewById(R.id.spinner_format_p3);
        DoctorVetApp.get().markRequired(txtName);
        DoctorVetApp.get().markRequired(txtCategories);
        DoctorVetApp.get().markRequired(txtMeasureUnit);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getProductsForInputObject(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Product.Products_for_input productsForInput = (Product.Products_for_input) resultObject;
                    setProductsCategoriesAdapter(productsForInput.getProducts_categories());
                    setProductsUnitsAdapter(productsForInput.getProducts_units());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        //spiners de calculo de precio
        ArrayAdapter<DoctorVetApp.products_prices> spinnerPreciosAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, DoctorVetApp.products_prices.values());
        spinnerPreciosAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinner_p1.setAdapter(spinnerPreciosAdapter);
        spinner_p2.setAdapter(spinnerPreciosAdapter);
        spinner_p3.setAdapter(spinnerPreciosAdapter);

        Product product = getObject();
        setObjectToUI(product);

        if (isProductWithTakePicture(product))
            implementTakePhoto(getResourceObject());

        //disable name edit for global products
        if (isUpdate() && product.getIs_global())
            disable_global_product_edit();

        selectedCategoriesAdapter = new Products_categoriesAdapter(getObject().getCategories(), DoctorVetApp.AdapterSelectTypes.REMOVE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(EditProductActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView_selected_categories.setLayoutManager(layoutManager);
        recyclerView_selected_categories.setHasFixedSize(true);
        recyclerView_selected_categories.setAdapter(selectedCategoriesAdapter);
        selectedCategoriesAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnCancelClickHandler() {
            @Override
            public void onCancelClick(Object data, View view, int pos) {
                selectedCategoriesAdapter.remove((Product_category) data);
            }
        });

        ImageView iconSearch_categoria = findViewById(R.id.img_search_categoria);
        iconSearch_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProductActivity.this, SearchProductCategoryActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_CATEGORY_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView iconSearch_measure_unit = findViewById(R.id.img_search_unit);
        iconSearch_measure_unit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProductActivity.this, SearchProductMeasureUnitActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView barcodeSearch = findViewById(R.id.img_search_bar_code);
        barcodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProductActivity.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });

        ImageView qrSearch = findViewById(R.id.img_search_qr);
        qrSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProductActivity.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_QR);
            }
        });

        TextInputLayout txtFixedP3 = findViewById(R.id.txt_fixed_p3);
        txtFixedP3.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        outState.putString(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
        product = MySqlGson.getGson().fromJson(objectInString, Product.class);

        String productsForInputInString = DoctorVetApp.get().readFromDisk("products_for_input");
        Product.Products_for_input productsForInput = MySqlGson.getGson().fromJson(productsForInputInString, Product.Products_for_input.class);
        setProductsCategoriesAdapter(productsForInput.getProducts_categories());
        setProductsUnitsAdapter(productsForInput.getProducts_units());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //barcodescanner
        if (requestCode == HelperClass.REQUEST_READ_BARCODE && data != null) {
            txtBarCode.getEditText().setText(data.getStringExtra(HelperClass.INTENT_EXTRA_BARCODE));
            txtBarCode.getEditText().requestFocus();
            return;
        }

        if (requestCode == HelperClass.REQUEST_READ_QR && data != null) {
            txtQRCode.getEditText().setText(data.getStringExtra(HelperClass.INTENT_EXTRA_BARCODE));
            txtQRCode.getEditText().requestFocus();
            return;
        }

        //busqueda
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PRODUCT_CATEGORY_OBJ.name())) {
                Product_category productcategory = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_CATEGORY_OBJ.name()), Product_category.class);
                selectedCategoriesAdapter.addCategory(productcategory);
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtMeasureUnit);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name())) {
                Product_unit productunit = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_UNIT_OBJ.name()), Product_unit.class);
                getObject().setUnit(productunit);
                txtMeasureUnit.getEditText().setText(productunit.getName());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtComplexUnitQuantity);
            }
        }
    }

    @Override
    protected void save() {
        if (!validateName() || !validateCategory() || !validateMeasureUnit())
            return;

        showWaitDialog();

        Product product = getObjectFromUI();
        final String producto_json_object = MySqlGson.postGson().toJson(product);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Product response_product = MySqlGson.getGson().fromJson(data, Product.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(product.getResources(), response_product.getResources());

                    if (!isUpdate()) {
                        Intent activity = new Intent(EditProductActivity.this, ViewProductVetActivity.class);
                        activity.putExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_ID.name(), response_product.getId());
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
                return producto_json_object.getBytes();
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
            return NetworkUtils.buildProductUrl(getObject().getId(), null, "RETURN_OBJECT", "product");

        return NetworkUtils.buildProductUrl(null, null, "RETURN_OBJECT", "product");
    }

    @Override
    protected Product getObjectFromUI() {
        Product product = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), product, true, "txt_", true, getApplicationContext());

        if (spinner_p1.getSelectedItem() != null) product.setFormat_p1(DoctorVetApp.products_prices.getEnumVal(spinner_p1.getSelectedItem().toString()));
        if (spinner_p2.getSelectedItem() != null) product.setFormat_p2(DoctorVetApp.products_prices.getEnumVal(spinner_p2.getSelectedItem().toString()));
        if (spinner_p3.getSelectedItem() != null) product.setFormat_p3(DoctorVetApp.products_prices.getEnumVal(spinner_p3.getSelectedItem().toString()));

        CheckBox chkExpires = findViewById(R.id.chk_expires);
        if (chkExpires.isChecked())
            product.setExpires(true);

        return product;
    }

    @Override
    protected Product getObject() {
        if (product == null) {
            if (isUpdate()) {
                product = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
            } else {
                product = new Product();
            }
        }

        return product;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Product product = (Product) object;
        loadThumb(product.getThumb_url());

        if (isUpdate()) {
            toolbar_title.setText(product.getName());
            toolbar_subtitle.setText(R.string.editar_product);
        } else {
            toolbar_title.setText(R.string.nuevo_product);
            toolbar_subtitle.setText(R.string.new_subtitle);
        }

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), product, "txt_");

        Product p = product;
        if (p.getFormat_p1() != null) spinner_p1.setSelection(p.getFormat_p1().ordinal());
        if (p.getFormat_p2() != null) spinner_p2.setSelection(p.getFormat_p2().ordinal());
        if (p.getFormat_p3() != null) spinner_p3.setSelection(p.getFormat_p3().ordinal());

        txtComplexUnitQuantity.setVisibility(View.GONE);
        if (p.getUnit() != null && p.getUnit().getIs_complex() == 1)
            txtComplexUnitQuantity.setVisibility(View.VISIBLE);
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return getObject();
    }

    @Override
    protected String getUploadTableName() {
        return "products";
    }

    private void setProductsCategoriesAdapter(ArrayList<Product_category> productCategories) {
        Products_categoriesAdapter productsCategoriesAdapter = new Products_categoriesAdapter(productCategories, DoctorVetApp.AdapterSelectTypes.NORMAL);
        actvCategory.setAdapter(productsCategoriesAdapter.getArrayAdapter(EditProductActivity.this));
        actvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategoriesAdapter.addCategory((Product_category) adapterView.getItemAtPosition(i));
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvCategory);
        DoctorVetApp.get().setAllWidthToDropDown(actvCategory, EditProductActivity.this);
    }
    private void setProductsUnitsAdapter(ArrayList<Product_unit> productUnits) {
        Products_unitsAdapter productsUnitsAdapter = new Products_unitsAdapter(productUnits);
        actvMeasureUnit.setAdapter(productsUnitsAdapter.getArrayAdapter(EditProductActivity.this));
        actvMeasureUnit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product_unit productUnit = (Product_unit) adapterView.getItemAtPosition(i);
                getObject().setUnit(productUnit);

                txtComplexUnitQuantity.setVisibility(View.GONE);
                if (productUnit.getIs_complex() == 1)
                    txtComplexUnitQuantity.setVisibility(View.VISIBLE);
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
        DoctorVetApp.get().setAllWidthToDropDown(actvMeasureUnit, EditProductActivity.this);
    }

    private void disable_global_product_edit() {
        findViewById(R.id.txt_atributos_globales).setVisibility(View.VISIBLE);
        findViewById(R.id.linear_atributos_globales).setVisibility(View.GONE);
    }
    private boolean isProductWithTakePicture(Product product) {
        if (product.getIs_global() != null && product.getIs_global())
            return false;

        return true;
    }

    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateCategory() {
        if (selectedCategoriesAdapter.getItemCount() == 0) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Ingresa categoría/s", Snackbar.LENGTH_LONG).show();
            txtCategories.requestFocus();
            return false;
        }

        return true;
    }
    private boolean validateMeasureUnit() {
        return DoctorVetApp.get().validateExistence(txtMeasureUnit, getObject().getUnit(), "name", false);
    }

}
