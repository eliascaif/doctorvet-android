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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Pet_recipe;
import com.xionce.doctorvetServices.data.Pet_recipe_item;
import com.xionce.doctorvetServices.data.Pet_recipe_itemsAdapter;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import org.apache.commons.lang3.math.NumberUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditRecipeActivity_1_1 extends EditBaseActivity {

    private static final String TAG = "EditRecipeActivity_1_";
    private AutoCompleteTextView actvProducto;
    private TextInputLayout txtProduct;
    private TextInputLayout txtDosage;
    private TextInputLayout txtEveryXHours;
    private TextInputLayout txtTotalDays;

    private Pet_recipe petRecipe = null;
    private Pet_recipe_item petRecipeItem = null;
    private Pet_recipe_itemsAdapter recipeItemsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_recipe_1_1);
        toolbar_title.setText("Receta");
        toolbar_subtitle.setText("¿Que productos vas a incluir?");
        actvProducto = findViewById(R.id.actv_product);
        txtProduct = findViewById(R.id.txt_product);
        txtDosage = findViewById(R.id.txt_dosage);
        txtEveryXHours = findViewById(R.id.txt_every_hours);
        txtTotalDays = findViewById(R.id.txt_total_days);
        Button btn_add = findViewById(R.id.btn_add);
        Button btn_end = findViewById(R.id.btn_end);
        hideToolbarImage();
        hideFab();
        DoctorVetApp.get().markRequired(txtProduct);
        DoctorVetApp.get().markRequired(txtDosage);
        DoctorVetApp.get().markRequired(txtEveryXHours);

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

        Pet_recipe_item recipeItem = getObject();
        setObjectToUI(recipeItem);

        //setup recyclerview
        RecyclerView recycler_new_supply = findViewById(R.id.recycler_products);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditRecipeActivity_1_1.this);
        recycler_new_supply.setLayoutManager(layoutManager);
        recycler_new_supply.setHasFixedSize(true);
        recipeItemsAdapter = new Pet_recipe_itemsAdapter(getRecipe().getItems(), Pet_recipe_itemsAdapter.RecipeItemsAdapterTypes.FOR_RECIPE_INPUT);
        recycler_new_supply.setAdapter(recipeItemsAdapter);
        recipeItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
            }
        });

        ImageView productSearch = findViewById(R.id.img_search_product);
        productSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRecipeActivity_1_1.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productSearchBarcode = findViewById(R.id.img_search_barcode);
        productSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRecipeActivity_1_1.this, ScannerActivity.class);
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
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name(), MySqlGson.getGson().toJson(getRecipe()));
                setResult(RESULT_OK, dataBackIntent);
                finish();
            }
        });

        txtTotalDays.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        outState.putString("recipe_item", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name(), MySqlGson.getGson().toJson(getRecipe()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("recipe_item");
        petRecipeItem = MySqlGson.getGson().fromJson(objectInString, Pet_recipe_item.class);

        String recipeInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name());
        petRecipe = MySqlGson.getGson().fromJson(recipeInStr, Pet_recipe.class);

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

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                DoctorVetApp.get().showKeyboard();
                                txtDosage.requestFocus();
                            }
                        }, 100);

                        //add_product();
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditRecipeActivity_1_1.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
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
                txtDosage.requestFocus();
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
    protected Pet_recipe_item getObjectFromUI() {
        Pet_recipe_item recipeItem = getObject();

        recipeItem.setDosage(txtDosage.getEditText().getText().toString());

        recipeItem.setEvery_hours(null);
        if (NumberUtils.isCreatable(txtEveryXHours.getEditText().getText().toString()))
            recipeItem.setEvery_hours(Integer.valueOf(txtEveryXHours.getEditText().getText().toString()));

        recipeItem.setTotal_days(null);
        if (NumberUtils.isCreatable(txtTotalDays.getEditText().getText().toString()))
            recipeItem.setTotal_days(Integer.valueOf(txtTotalDays.getEditText().getText().toString()));

        return recipeItem;
    }

    @Override
    protected Pet_recipe_item getObject() {
        if (petRecipeItem == null)
            petRecipeItem = new Pet_recipe_item();

        return petRecipeItem;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_recipe_item petRecipeItem = (Pet_recipe_item) object;

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petRecipeItem, "txt_");

        Product product = petRecipeItem.getProduct();
        if (product != null) {
            actvProducto.setText(product.getName());
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
        actvProducto.setAdapter(productsAdapter.getAutocompleteAdapter(EditRecipeActivity_1_1.this));
        actvProducto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product)adapterView.getItemAtPosition(i);
                getObject().setProduct(product);
                txtDosage.requestFocus();
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
        DoctorVetApp.get().setAllWidthToDropDown(actvProducto, EditRecipeActivity_1_1.this);
    }

    private void add_product() {
        if (!validateProduct() || !validateDosage() || !validateEveryHours() || !validateTotalDays())
            return;

        //resetfields wipes out txtProduct.text and that set product to null. We need to clone
        Pet_recipe_item recipeItem = (Pet_recipe_item) getObjectFromUI().clone();

        recipeItemsAdapter.addItem(recipeItem);

        resetFields();
    }
    private Pet_recipe getRecipe() {
        if (petRecipe == null)
            petRecipe = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name()), Pet_recipe.class);

        return petRecipe;
    }
    private void resetFields() {
        actvProducto.setText("");
        txtDosage.getEditText().setText("");
        txtEveryXHours.getEditText().setText("");
        txtTotalDays.getEditText().setText("");
        actvProducto.requestFocus();
    }

    private boolean validateProduct() {
        return DoctorVetApp.get().validateExistence(txtProduct, getObject().getProduct(), "name", false);
    }
    private boolean validateDosage() {
        return HelperClass.validateEmpty(txtDosage);
    }
    private boolean validateEveryHours() {
        return HelperClass.validateNumber(txtEveryXHours, false);
    }
    private boolean validateTotalDays() {
        return HelperClass.validateNumber(txtTotalDays, true);
    }

}
