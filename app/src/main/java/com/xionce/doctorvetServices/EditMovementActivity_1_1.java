package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.Movement_item;
import com.xionce.doctorvetServices.data.Movement_itemAdapter;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditMovementActivity_1_1 extends EditBaseActivity {

    private static final String TAG = "EditMovementActivity_1_";
    private AutoCompleteTextView actvProducto;
    private TextInputLayout txtProduct;
    private TextInputLayout txtQuantity;
    private RadioButton opt_quantity_unit_1;
    private RadioButton opt_quantity_unit_2;

    private Movement movement = null;
    private Movement_item movement_item = null;
    private Movement_itemAdapter movementItemsAdapter = null;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_movement_1_1);
        toolbar_title.setText("Nuevo movimiento");
        toolbar_subtitle.setText("¿Que productos vas a mover?");
        actvProducto = findViewById(R.id.actv_product);
        txtProduct = findViewById(R.id.txt_name_product);
        txtQuantity = findViewById(R.id.txt_quantity);
        opt_quantity_unit_1 = findViewById(R.id.opt_quantity_unit_1);
        opt_quantity_unit_2 = findViewById(R.id.opt_quantity_unit_2);
        Button btn_add = findViewById(R.id.btn_add);
        Button btn_end = findViewById(R.id.btn_end);
        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getProductsVet(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setProductsAdapter(resultList);
                }
            });
            resetFields();
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Movement_item movement_item = getObject();
        setObjectToUI(movement_item);

        //setup recyclerview
        /*RecyclerView*/ recyclerView = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditMovementActivity_1_1.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        movementItemsAdapter = new Movement_itemAdapter(getMovement().getItems(), Movement_itemAdapter.MovementAdapter_types.FOR_MOVEMENT_INPUT);
        recyclerView.setAdapter(movementItemsAdapter);
        movementItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
            }
        });

        ImageView productSearch = findViewById(R.id.img_search_product);
        productSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMovementActivity_1_1.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productSearchBarcode = findViewById(R.id.img_search_barcode);
        productSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditMovementActivity_1_1.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_product();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataBackIntent = new Intent();
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(getMovement()));
                setResult(RESULT_OK, dataBackIntent);
                finish();
            }
        });

        txtQuantity.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    add_product();
                    return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onAllRequestsCompleted() {
        if (!isInit()) return;

        DoctorVetApp.get().showKeyboard();
        actvProducto.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("movement_item", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name(), MySqlGson.getGson().toJson(getMovement()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("movement_item");
        movement_item = MySqlGson.getGson().fromJson(objectInString, Movement_item.class);

        String movementInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name());
        movement = MySqlGson.getGson().fromJson(movementInStr, Movement.class);

        String productsInString = DoctorVetApp.get().readFromDisk("products");
        ArrayList<Product> products = MySqlGson.getGson().fromJson(productsInString, new TypeToken<List<Product>>(){}.getType());
        setProductsAdapter(products);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        //barcodescanner
        if (requestCode == HelperClass.REQUEST_READ_BARCODE && data != null) {
            String barcode = data.getStringExtra(HelperClass.INTENT_EXTRA_BARCODE);
            showWaitDialog();
            DoctorVetApp.get().getProductByBarcode(barcode, new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    if (resultObject != null) {
                        Product product = (Product)resultObject;
                        actvProducto.setText(product.getName());
                        getObject().setProduct(product);
                        setUnits(product);

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                DoctorVetApp.get().showKeyboard();
                                txtQuantity.requestFocus();
                            }
                        }, 100);

                        //add_product();
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditMovementActivity_1_1.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
                    }
                    hideWaitDialog();
                }
            });
            return;
        }

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            if (data.hasExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name())) {
                Product product = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
                actvProducto.setText(product.getName());
                getObject().setProduct(product);
                setUnits(product);
                txtQuantity.requestFocus();
            }
        }

    }

    @Override
    protected void save() {

    }

    @Override
    protected void update() {

    }

    @Override
    protected URL getUrl() {
        return null;
    }

    @Override
    protected Movement_item getObjectFromUI() {
        Movement_item movementItem = getObject();

        movementItem.setQuantity(null);
        if (NumberUtils.isCreatable(txtQuantity.getEditText().getText().toString()))
            movementItem.setQuantity(new BigDecimal(txtQuantity.getEditText().getText().toString()));

        return movementItem;
    }

    @Override
    protected Movement_item getObject() {
        if (movement_item == null)
            movement_item = new Movement_item();

        return movement_item;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Movement_item movement_item = (Movement_item) object;

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), movement_item, "txt_");

        Product product = movement_item.getProduct();
        if (product != null) {
            actvProducto.setText(product.getName());
            setUnits(product);
        }
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setProductsAdapter(ArrayList resultList) {
        ProductsAdapter productsAdapter = new ProductsAdapter(resultList, ProductsAdapter.Products_types.NORMAL);
        actvProducto.setAdapter(productsAdapter.getAutocompleteAdapter(EditMovementActivity_1_1.this));
        actvProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product)adapterView.getItemAtPosition(i);
                getObject().setProduct(product);
                setUnits(product);
                txtQuantity.requestFocus();
            }
        });
        actvProducto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setProduct(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvProducto);
        DoctorVetApp.get().setAllWidthToDropDown(actvProducto, EditMovementActivity_1_1.this);
    }

    private void add_product() {
        if (!validateProduct() || !validateQuantity())
            return;

        //resetfields wipes out txtProduct.text and that set product to null. We need to clone
        Movement_item movementItem = (Movement_item) getObjectFromUI().clone();

        if (opt_quantity_unit_1.isChecked())
            movementItem.setSelected_unit(movementItem.getProduct().getUnit().getFirst_unit_string());
        else
            movementItem.setSelected_unit(movementItem.getProduct().getUnit().getSecond_unit_string());

        movementItem.setQuantity(new BigDecimal(txtQuantity.getEditText().getText().toString()));

        movementItemsAdapter.addItem(movementItem);
        HelperClass.focusLastRow(recyclerView);

        resetFields();
    }
    private Movement getMovement() {
        if (movement == null)
            movement = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.MOVEMENT_OBJ.name()), Movement.class);

        return movement;
    }
    private void setUnits(Product product) {
        opt_quantity_unit_1.setChecked(true);
        opt_quantity_unit_1.setText(product.getUnit().getFirst_unit_string());
        opt_quantity_unit_2.setVisibility(View.GONE);
        if (product.getUnit().getIs_complex() == 1) {
            opt_quantity_unit_2.setText(product.getUnit().getSecond_unit_string());
            opt_quantity_unit_2.setVisibility(View.VISIBLE);
        }
    }
    private void resetFields() {
        actvProducto.setText("");
        txtQuantity.getEditText().setText("");
        opt_quantity_unit_1.setText("u");
        opt_quantity_unit_2.setVisibility(View.GONE);
        actvProducto.requestFocus();
    }

    private boolean validateProduct() {
        return DoctorVetApp.get().validateExistence(txtProduct, getObject().getProduct(), "name", false);
    }
    private boolean validateQuantity() {
        return HelperClass.validateNumberNotZero(txtQuantity, false);
    }

}
