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
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.Sell_item;
import com.xionce.doctorvetServices.data.Sell_itemAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditSellActivity_1_1 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_1_1";
    private AutoCompleteTextView actvProducto;
    private TextInputLayout txtQuantity;
    private TextInputLayout txtPrice;
    private TextInputLayout txtDiscount;
    private TextView txtSubtotal;
    private TextView txtTotal;
    private RadioButton opt_quantity_unit_1;
    private RadioButton opt_quantity_unit_2;

    private Sell sell = null;
    private Sell_item sellItem = null;
    private Sell_itemAdapter sellItemsAdapter = null;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sell_1_1);
        toolbar_title.setText("Venta");
        toolbar_subtitle.setText("¿Que productos y/o servicios vas a vender?");
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

        Sell_item sellItem = getObject();
        setObjectToUI(sellItem);

        //setup recyclerview
        /*RecyclerView*/ recyclerView = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditSellActivity_1_1.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        sellItemsAdapter = new Sell_itemAdapter(getSell().getItems(), Sell_itemAdapter.SellItemsAdapterTypes.FOR_SELL_INPUT);
        recyclerView.setAdapter(sellItemsAdapter);
        sellItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                calculateTotal();
            }
        });

        calculateSubtotal();
        calculateTotal();

        ImageView productSearch = findViewById(R.id.img_search_product);
        productSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_1_1.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productoSearchBarcode = findViewById(R.id.img_search_barcode);
        productoSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_1_1.this, ScannerActivity.class);
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
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.getGson().toJson(getSell()));
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

        if (DoctorVetApp.get().getVet().getSells_lock_price().equals(1)) {
            txtPrice.getEditText().setEnabled(false);
            txtPrice.getEditText().setFocusable(false);
            txtPrice.getEditText().setClickable(false);
        }
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
        outState.putString(DoctorVetApp.INTENT_VALUES.SELL_ITEM_OBJ.name(), MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.getGson().toJson(getSell()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.SELL_ITEM_OBJ.name());
        sellItem = MySqlGson.getGson().fromJson(objectInString, Sell_item.class);

        String sellInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name());
        sell = MySqlGson.getGson().fromJson(sellInStr, Sell.class);

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

                        if (product.getP1() == null) {
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
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditSellActivity_1_1.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
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
    protected Sell_item getObjectFromUI() {
        Sell_item sellItem = getObject();

        sellItem.setPrice(null);
        if (NumberUtils.isCreatable(txtPrice.getEditText().getText().toString()))
            sellItem.setPrice(new BigDecimal(txtPrice.getEditText().getText().toString()));

        sellItem.setQuantity(null);
        if (NumberUtils.isCreatable(txtQuantity.getEditText().getText().toString()))
            sellItem.setQuantity(new BigDecimal(txtQuantity.getEditText().getText().toString()));

        sellItem.setDiscount_surcharge(null);
        if (NumberUtils.isCreatable(txtDiscount.getEditText().getText().toString()))
            sellItem.setDiscount_surcharge(new BigDecimal(txtDiscount.getEditText().getText().toString()));

        return sellItem;
    }

    @Override
    protected Sell_item getObject() {
        if (sellItem == null)
            sellItem = new Sell_item();

        return sellItem;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Sell_item sellItem = (Sell_item) object;

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), sellItem, "txt_");

        Product product = sellItem.getProduct();
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
        actvProducto.setAdapter(productsAdapter.getAutocompleteAdapter(EditSellActivity_1_1.this));
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
        DoctorVetApp.get().setAllWidthToDropDown(actvProducto, EditSellActivity_1_1.this);
    }

    private void add_product() {
        if (!validateProduct() || !validateQuantity() || !validatePrice() || !validateDiscount())
            return;

        //resetfields wipes out txtProduct.text and that set product to null. We need to clone
        Sell_item sellItem = (Sell_item) getObjectFromUI().clone();

        if (opt_quantity_unit_1.isChecked())
            sellItem.setSelected_unit(sellItem.getProduct().getUnit().getFirst_unit_string());
        else
            sellItem.setSelected_unit(sellItem.getProduct().getUnit().getSecond_unit_string());

        sellItem.setTax(sellItem.getProduct().getTax());
        sellItem.setQuantity(new BigDecimal(txtQuantity.getEditText().getText().toString()));
        sellItem.setPrice(new BigDecimal(txtPrice.getEditText().getText().toString()));

        sellItem.setDiscount_surcharge(BigDecimal.ZERO);
        String discount = txtDiscount.getEditText().getText().toString();
        if (NumberUtils.isCreatable(discount))
            sellItem.setDiscount_surcharge(new BigDecimal(discount));

        sellItemsAdapter.addItem(sellItem);
        HelperClass.focusLastRow(recyclerView);

        resetFields();
        calculateTotal();
    }
    private Sell getSell() {
        if (sell == null)
            sell = MySqlGson.postGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name()), Sell.class);

        return sell;
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
        txtPrice.getEditText().setText(product.getRoundedPrice1());
        BigDecimal discount = new BigDecimal(0);
        txtDiscount.getEditText().setText(discount.setScale(2, RoundingMode.HALF_UP).toString());

        //prices opts
        RadioButton rp1 = findViewById(R.id.opt_price_1);
        RadioButton rp2 = findViewById(R.id.opt_price_2);
        RadioButton rp3 = findViewById(R.id.opt_price_3);
//        RadioButton rp4 = findViewById(R.id.opt_price_4);
        rp1.setVisibility(View.GONE);
        rp2.setVisibility(View.GONE);
        rp3.setVisibility(View.GONE);
//        rp4.setVisibility(View.GONE);
        if (product.getP1() != null) rp1.setVisibility(View.VISIBLE);
        if (product.getP2() != null) rp2.setVisibility(View.VISIBLE);
        if (product.getP3() != null) rp3.setVisibility(View.VISIBLE);
//        if (product.getP4() != null) rp4.setVisibility(View.VISIBLE);

        rp1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrice.getEditText().setText(product.getRoundedPrice1());
            }
        });
        rp2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrice.getEditText().setText(product.getRoundedPrice2());
            }
        });
        rp3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtPrice.getEditText().setText(product.getRoundedPrice3());
            }
        });
//        rp4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                txtPrice.getEditText().setText(product.getRoundedPrice4());
//            }
//        });

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

        //
        RadioButton rp2 = findViewById(R.id.opt_price_2);
        RadioButton rp3 = findViewById(R.id.opt_price_3);
        rp2.setVisibility(View.GONE);
        rp3.setVisibility(View.GONE);

        actvProducto.requestFocus();
    }
    private void calculateSubtotal() {
        BigDecimal quantity = new BigDecimal(0);
        BigDecimal price = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(0);
        BigDecimal subtotal = new BigDecimal(0);

        if (HelperClass.isDecimal(txtQuantity.getEditText().getText().toString()))
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
        txtTotal.setText("Total: " + sellItemsAdapter.getTotal().toString());
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
    private boolean validateProduct() {
        TextInputLayout txtProduct = findViewById(R.id.txt_name_product);
        return DoctorVetApp.get().validateExistence(txtProduct, getObject().getProduct(), "name", false);
    }

}
