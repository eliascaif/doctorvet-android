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
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.Purchase_item;
import com.xionce.doctorvetServices.data.Purchase_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditPurchaseActivity_1_1 extends EditBaseActivity {

    private static final String TAG = "EditPurchaseActivity_1_";
    private AutoCompleteTextView actvProducto;
    private TextInputLayout txtQuantity;
    private TextInputLayout txtPrice;
    private TextInputLayout txtDiscount;
    private TextView txtSubtotal;
    private TextView txtTotal;
    private RadioButton opt_quantity_unit_1;
    private RadioButton opt_quantity_unit_2;

    private Purchase purchase = null;
    private Purchase_item purchaseItem = null;
    private Purchase_itemAdapter purchaseItemsAdapter = null;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_purchase_1_1);
        toolbar_title.setText("Compra");
        toolbar_subtitle.setText("¿Que productos y/o servicios vas a comprar?");
        actvProducto = findViewById(R.id.actv_product);
        txtQuantity = findViewById(R.id.txt_quantity);
        txtPrice = findViewById(R.id.txt_price);
        txtDiscount = findViewById(R.id.txt_discount_surcharge);
        txtSubtotal = findViewById(R.id.txt_subtotal);
        txtTotal = findViewById(R.id.txt_total);
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

        Purchase_item purchaseItem = getObject();
        setObjectToUI(purchaseItem);

        //setup recyclerview
        /*RecyclerView*/ recyclerView = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditPurchaseActivity_1_1.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        purchaseItemsAdapter = new Purchase_itemAdapter(getPurchase().getItems(), Purchase_itemAdapter.PurchaseDetailAdapter_types.FOR_PURCHASE_INPUT);
        recyclerView.setAdapter(purchaseItemsAdapter);
        purchaseItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                calculateTotal();
            }
        });

        calculateSubtotal();
        calculateTotal();

        ImageView productoSearch = findViewById(R.id.img_search_product);
        productoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPurchaseActivity_1_1.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productoSearchBarcode = findViewById(R.id.img_search_barcode);
        productoSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPurchaseActivity_1_1.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });

        txtQuantity.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateSubtotal();
            }
        });
        txtPrice.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateSubtotal();
            }
        });
        txtDiscount.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculateSubtotal();
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
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(getPurchase()));
                setResult(RESULT_OK, dataBackIntent);
                finish();
            }
        });

        txtDiscount.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        outState.putString("purchase_item", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name(), MySqlGson.getGson().toJson(getPurchase()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("purchase_item");
        purchaseItem = MySqlGson.getGson().fromJson(objectInString, Purchase_item.class);

        String purchaseInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name());
        purchase = MySqlGson.getGson().fromJson(purchaseInStr, Purchase.class);

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
                        setPrices(product);
                        txtQuantity.requestFocus();

                        if (product.getCost() == null) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    DoctorVetApp.get().showKeyboard();
                                    txtPrice.requestFocus();
                                }
                            }, 100);
                        }

                        //add_product();
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditPurchaseActivity_1_1.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
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
                setPrices(product);
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
    protected Purchase_item getObjectFromUI() {
        Purchase_item purchaseItem = getObject();

        purchaseItem.setPrice(null);
        if (NumberUtils.isCreatable(txtPrice.getEditText().getText().toString()))
            purchaseItem.setPrice(new BigDecimal(txtPrice.getEditText().getText().toString()));

        purchaseItem.setQuantity(null);
        if (NumberUtils.isCreatable(txtQuantity.getEditText().getText().toString()))
            purchaseItem.setQuantity(new BigDecimal(txtQuantity.getEditText().getText().toString()));

        purchaseItem.setDiscount_surcharge(null);
        if (NumberUtils.isCreatable(txtDiscount.getEditText().getText().toString()))
            purchaseItem.setDiscount_surcharge(new BigDecimal(txtDiscount.getEditText().getText().toString()));

        return purchaseItem;
    }

    @Override
    protected Purchase_item getObject() {
        if (purchaseItem == null)
            purchaseItem = new Purchase_item();

        return purchaseItem;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Purchase_item purchaseItem = (Purchase_item) object;

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), purchaseItem, "txt_");

        Product product = purchaseItem.getProduct();
        if (product != null) {
            actvProducto.setText(product.getName());
            setUnits(product);
            //setPrices(product);
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
        actvProducto.setAdapter(productsAdapter.getAutocompleteAdapter(EditPurchaseActivity_1_1.this));
        actvProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product)adapterView.getItemAtPosition(i);
                getObject().setProduct(product);
                setUnits(product);
                setPrices(product);
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
        DoctorVetApp.get().setAllWidthToDropDown(actvProducto, EditPurchaseActivity_1_1.this);
    }

    private void add_product() {
        if (!validateProducto() || !validateQuantity() || !validatePrice() || !validateDiscount())
            return;

        //resetfields wipes out txtProduct.text and that set product to null. We need to clone
        Purchase_item purchaseItem = (Purchase_item) getObjectFromUI().clone();

        if (opt_quantity_unit_1.isChecked())
            purchaseItem.setSelected_unit(purchaseItem.getProduct().getUnit().getFirst_unit_string());
        else
            purchaseItem.setSelected_unit(purchaseItem.getProduct().getUnit().getSecond_unit_string());

        purchaseItem.setTax(purchaseItem.getProduct().getTax());
        purchaseItem.setQuantity(new BigDecimal(txtQuantity.getEditText().getText().toString()));
        purchaseItem.setPrice(new BigDecimal(txtPrice.getEditText().getText().toString()));

        purchaseItem.setDiscount_surcharge(BigDecimal.ZERO);
        String discount = txtDiscount.getEditText().getText().toString();
        if (NumberUtils.isCreatable(discount))
            purchaseItem.setDiscount_surcharge(new BigDecimal(discount));

        purchaseItemsAdapter.addItem(purchaseItem);
        HelperClass.focusLastRow(recyclerView);

        resetFields();
        calculateTotal();
    }
    private Purchase getPurchase() {
        if (purchase == null)
            purchase = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PURCHASE_OBJ.name()), Purchase.class);

        return purchase;
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
    private void setPrices(Product product) {
        BigDecimal one = new BigDecimal(1);
        txtQuantity.getEditText().setText(one.setScale(2, RoundingMode.HALF_UP).toString());
        txtPrice.getEditText().setText(product.getRoundedCost());
        BigDecimal discount = new BigDecimal(0);
        txtDiscount.getEditText().setText(discount.setScale(2, RoundingMode.HALF_UP).toString());

        calculateSubtotal();
    }
    private void resetFields() {
        actvProducto.setText("");
        txtQuantity.getEditText().setText("");
        txtPrice.getEditText().setText("");
        txtDiscount.getEditText().setText("");
        txtSubtotal.setText("Subtotal: " + BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP).toString());
        opt_quantity_unit_1.setText("u");
        opt_quantity_unit_2.setVisibility(View.GONE);
        actvProducto.requestFocus();
    }
    private void calculateSubtotal() {
        BigDecimal quantity = new BigDecimal(0);
        BigDecimal price = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(0);
        BigDecimal subtotal = new BigDecimal(0);

        if (HelperClass.isDecimal(txtQuantity.getEditText().getText().toString())) //validateQuantity())
            quantity = new BigDecimal(txtQuantity.getEditText().getText().toString());

        if (HelperClass.isDecimal(txtPrice.getEditText().getText().toString()))
            price = new BigDecimal(txtPrice.getEditText().getText().toString());

        if (HelperClass.isDecimal(txtDiscount.getEditText().getText().toString()))
            discount = new BigDecimal(txtDiscount.getEditText().getText().toString());

        if (!discount.equals(BigDecimal.valueOf(0))) {
            price = price.subtract(price.multiply(discount).divide(BigDecimal.valueOf(100))); //price * discount / 100
        }

        subtotal = quantity.multiply(price);

        txtSubtotal.setText("Subtotal: " + subtotal.setScale(2, RoundingMode.HALF_UP).toString());
    }
    private void calculateTotal() {
        txtTotal.setText("Total: " + purchaseItemsAdapter.getTotal().toString());
    }

    private boolean validatePrice() {
        return HelperClass.validateNumber(txtPrice, false);
    }
    private boolean validateQuantity() {
        return HelperClass.validateNumberNotZero(txtQuantity, false);
    }
    private boolean validateDiscount() {
        return HelperClass.validateNumber(txtDiscount, true);
    }
    private boolean validateProducto() {
        TextInputLayout txtProduct = findViewById(R.id.txt_name_product);
        return DoctorVetApp.get().validateExistence(txtProduct, getObject().getProduct(), "name", false);
    }

}
