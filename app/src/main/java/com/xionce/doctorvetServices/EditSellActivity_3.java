package com.xionce.doctorvetServices;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.data.Pet_supplyAdapter;
import com.xionce.doctorvetServices.data.PetsAdapter;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;

import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditSellActivity_3 extends EditBaseActivity {

    private static final String TAG = "EditSellActivity_3";
    private AutoCompleteTextView actvProduct;
    private TextInputLayout txtProduct;
    private TextInputLayout txtDate;
    private TextView labelDate;
    private Spinner spinner_pets;
    private TextView label_pet;

    private Sell sell = null;
    private Pet_supply pet_supply = null;
    private ArrayList<Pet_supply> pet_supplies = new ArrayList<>();
    private Pet_supplyAdapter pet_supplyAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_sell_3);
        toolbar_title.setText("Venta");
        toolbar_subtitle.setText("¿Que vas a suministrar en la próxima visita?");
        actvProduct = findViewById(R.id.actv_product);
        txtProduct = findViewById(R.id.txt_name_product);
        txtDate = findViewById(R.id.txt_date_tentative);
        labelDate = findViewById(R.id.label_date);
        spinner_pets = findViewById(R.id.spinner_pets);
        label_pet = findViewById(R.id.label_pet);
        label_pet.setText(DoctorVetApp.get().getPetNaming());

        hideToolbarImage();
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(2);
            DoctorVetApp.get().getSellPlanning(getSell(), new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setExistingPlanningRecyclerview(resultList);
                    getSell().setUpcoming_supplies(resultList);
                }
            });
            //if suggested products are in, then the user could never go to insert products
            //therefor products in disk may not exists
            //setProductsAdapter(DoctorVetApp.get().getProductsVetFromDisk());
            DoctorVetApp.get().getProductsVet(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setProductsAdapter(resultList);
                }
            });

            setPetsSpinner(getSell().getOwner().getPets());
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Pet_supply supply = getObject();
        setObjectToUI(supply);

        ImageView productSearch = findViewById(R.id.img_search_product);
        productSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_3.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productSearchBarcode = findViewById(R.id.img_search_barcode);
        productSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSellActivity_3.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });

        ImageView dateSearch = findViewById(R.id.img_search_date);
        dateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });
        txtDate.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDate();
            }
        });

        ImageView add_days = findViewById(R.id.img_add_days);
        add_days.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String actual = txtDate.getEditText().getText().toString();
                if (HelperClass.isValidDate(actual, EditSellActivity_3.this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditSellActivity_3.this);
                    builder.setTitle(R.string.sumar_dias_a_fecha_actual);
                    builder.setMessage(R.string.cuantos_dias);
                    final EditText input = new EditText(EditSellActivity_3.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setSingleLine();
                    input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                    FrameLayout container = new FrameLayout(EditSellActivity_3.this);
                    FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                    params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                    input.setLayoutParams(params);
                    container.addView(input);
                    builder.setView(container);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String numero = input.getText().toString();
                            if (HelperClass.isLong(numero)) {
                                addDays(Integer.parseInt(numero), HelperClass.getShortDate(actual, EditSellActivity_3.this));
                                //txt_dias.setText(numero);
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            DoctorVetApp.get().closeKeyboard();
                        }
                    });
                    builder.show();
                    input.requestFocus();
                    //El foco probablemente este en un cuadro con teclado mostrado, por defecto
                    //el modal lo cierra y no funciona para abrir, por eso el delay
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DoctorVetApp.get().showKeyboard();
                        }
                    }, 100);
                }
            }
        });

        txtDate.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setLabelLongDate(editable.toString());
            }
        });

        ImageView insertProduct = findViewById(R.id.img_insert);
        insertProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_dosis();
            }
        });

        Button btn_continue = findViewById(R.id.btn_continue);
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });


    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pet_supply", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString("pet_supplies", MySqlGson.getGson().toJson(pet_supplies));
//        outState.putString("pets", MySqlGson.getGson().toJson(pets));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("pet_supply");
        pet_supply = MySqlGson.getGson().fromJson(objectInString, Pet_supply.class);

        String petSuppliesInString = savedInstanceState.getString("pet_supplies");
        pet_supplies = MySqlGson.getGson().fromJson(petSuppliesInString, new TypeToken<List<Pet_supply>>(){}.getType());
        setExistingPlanningRecyclerview(pet_supplies);

//        String petsInString = savedInstanceState.getString("pets");
//        pets = MySqlGson.getGson().fromJson(petsInString, new TypeToken<List<Pet>>(){}.getType());
//        setPetsSpinner(pets);

        setProductsAdapter(DoctorVetApp.get().getProductsVetFromDisk());
        setPetsSpinner(getSell().getOwner().getPets());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == HelperClass.REQUEST_FINISH) {
            setResult(HelperClass.REQUEST_FINISH);
            finish();
        }

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
                        getObject().setProduct(product);
                        actvProduct.setText(product.getName());
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditSellActivity_3.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
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
                getObject().setProduct(product);
                actvProduct.setText(product.getName());
                actvProduct.clearFocus();
            }
        }
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_supply pet_supply = (Pet_supply) object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), pet_supply, "txt_");
        setLabelLongDate(txtDate.getEditText().getText().toString());

//        Integer petsNumber = getSell().getOwner().getPets().size();
//        findViewById(R.id.label_pet).setVisibility(View.GONE);
//        spinner_pets.setVisibility(View.GONE);
//        if (petsNumber > 1) {
//            findViewById(R.id.label_pet).setVisibility(View.VISIBLE);
//            spinner_pets.setVisibility(View.VISIBLE);
//        }
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
    protected Pet_supply getObjectFromUI() {
        Pet_supply supply = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), supply, true, "txt_", false, getApplicationContext());

        Date date = HelperClass.getShortDate(txtDate.getEditText().getText().toString(), this);
        supply.setDate_tentative(date);

        return supply;
    }

    @Override
    protected Pet_supply getObject() {
        if (pet_supply == null) {
            pet_supply = new Pet_supply();
            pet_supply.setDate_tentative(Calendar.getInstance().getTime());
        }

        return pet_supply;
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void next() {
        Sell sell = getSell();
        Intent intent = new Intent(EditSellActivity_3.this, EditSellActivity_4.class);
        intent.putExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name(), MySqlGson.getGson().toJson(sell));
        startActivityForResult(intent, 1);
    }

    private void setProductsAdapter(ArrayList resultList) {
        ProductsAdapter productsAdapter = new ProductsAdapter(resultList, ProductsAdapter.Products_types.NORMAL);
        actvProduct.setAdapter(productsAdapter.getAutocompleteAdapter(EditSellActivity_3.this));
        actvProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product) adapterView.getItemAtPosition(i);
                Product polish_product = new Product(product.getId(), product.getName(), product.getThumb_url());
                getObject().setProduct(polish_product);
                DoctorVetApp.get().closeKeyboard();
            }
        });
        actvProduct.addTextChangedListener(new TextWatcher() {
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
        DoctorVetApp.get().setOnTouchToShowDropDown(actvProduct);
        DoctorVetApp.get().setAllWidthToDropDown(actvProduct, EditSellActivity_3.this);
    }
    private void setExistingPlanningRecyclerview(ArrayList<Pet_supply> resultList) {
        RecyclerView recycler_existing_supply = findViewById(R.id.recycler_supply);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditSellActivity_3.this);
        recycler_existing_supply.setLayoutManager(layoutManager);
        recycler_existing_supply.setHasFixedSize(true);
        pet_supplies = resultList;
        pet_supplyAdapter = new Pet_supplyAdapter(pet_supplies, Pet_supplyAdapter.Pet_supplyAdapter_types.PLANNING_ACTIVITY);
        recycler_existing_supply.setAdapter(pet_supplyAdapter);
    }
    private void setPetsSpinner(ArrayList<Pet> pets) {
        PetsAdapter petsAdapter = new PetsAdapter(pets, DoctorVetApp.Adapter_types.NORMAL);
        spinner_pets.setAdapter(petsAdapter.getArrayAdapter(EditSellActivity_3.this));
        spinner_pets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Pet pet = (Pet) adapterView.getItemAtPosition(i);
                getObject().setPet(pet);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Pet selected_pet = pets.get(0);
        if (getSell().getPet() != null)
            selected_pet = getSell().getPet();

        getObject().setPet(selected_pet);

        int petsSelectedIndex = petsAdapter.getArrayAdapter(EditSellActivity_3.this).getPosition(selected_pet);
        spinner_pets.setSelection(petsSelectedIndex);
    }

    private Sell getSell() {
        if (sell == null)
            sell = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.SELL_OBJ.name()), Sell.class);

        return sell;
    }

    private void setLabelLongDate(String input_text) {
        if (HelperClass.isValidDate(input_text, EditSellActivity_3.this)) {
            labelDate.setVisibility(View.VISIBLE);
            Date fechaDate = HelperClass.getShortDate(input_text, EditSellActivity_3.this);
            labelDate.setText(DateFormat.getDateInstance(DateFormat.FULL).format(fechaDate));
        } else {
            labelDate.setVisibility(View.GONE);
            labelDate.setText("");
        }
    }

    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }
    private void addDays(int dias, Date dt_actual) {
        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(HelperClass.sumar_dias(dt_actual, dias)));
    }

    private void insert_dosis() {
        if (!validateProduct() || !validateDate() || !validateIngresoDuplicadoParaMismaFechaAndSamePet())
            return;

        Pet_supply petsupply = (Pet_supply) getObjectFromUI().clone();
        petsupply.setPlanning_activity_new(1);

        pet_supplyAdapter.addItem(petsupply);
    }

    private boolean validateProduct() {
        return DoctorVetApp.get().validateExistence(txtProduct, getObject().getProduct(), "name", false);
    }
    private boolean validateDate() {
        return HelperClass.validateDate(txtDate, false);
    }
    private boolean validateIngresoDuplicadoParaMismaFechaAndSamePet() {
        String producto = actvProduct.getText().toString().trim();
        String pet_name = getObject().getPet().getName();
        Date fecha = HelperClass.getShortDate(txtDate.getEditText().getText().toString(), this);
        ArrayList<Pet_supply> petsupply = pet_supplyAdapter.getArrayList();
        for (Pet_supply m: petsupply) {
            if (producto.toLowerCase().equals(m.getProduct().getName().toLowerCase()) && fecha.equals(m.getDate_tentative()) && pet_name.equalsIgnoreCase(m.getPet().getName())) {
                Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.suministro_duplicado, Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

}
