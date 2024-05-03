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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.data.Pet_supplyAdapter;
import com.xionce.doctorvetServices.data.Pet_supply_POST_object;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditSupplyActivity_YesNo extends EditBaseActivity {

    private static final String TAG = "EditSupplyActivity_YesN";
    private TextInputLayout txtProduct;
    private AutoCompleteTextView actvProduct;
    private TextInputLayout txtDate;
    private TextView labelFecha;
    private SwitchCompat toggleButton;
    
    private Pet_supply pet_supply = null;
    private ArrayList<Pet_supply> pet_supplies = new ArrayList<>();
    private Pet_supplyAdapter pet_supplyAdapter = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_supply_yesno);
        txtProduct = findViewById(R.id.txt_name_product);
        actvProduct = findViewById(R.id.actv_product);
        txtDate = findViewById(R.id.txt_date_tentative);
        labelFecha = findViewById(R.id.label_date);
        toggleButton = findViewById(R.id.toggle_supply);
        Button btn_add = findViewById(R.id.btn_add);
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
        } else {
            restoreFromBundle(savedInstanceState);
        }

        Pet_supply supply = getObject();
        setObjectToUI(supply);

        //setup recyclerview
        RecyclerView recycler_new_supply = findViewById(R.id.recycler_new_supply);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditSupplyActivity_YesNo.this);
        recycler_new_supply.setLayoutManager(layoutManager);
        recycler_new_supply.setHasFixedSize(true);
        pet_supplyAdapter = new Pet_supplyAdapter(pet_supplies, Pet_supplyAdapter.Pet_supplyAdapter_types.NEW_SUPPLY);
        recycler_new_supply.setAdapter(pet_supplyAdapter);

        //en edicion solo puede modificar una dosis, no ingresar dosis futuras
//        if (isUpdate()) {
//            if (isInit()) {
//                recycler_new_supply1.setVisibility(View.INVISIBLE);
//                btn_add.setVisibility(View.INVISIBLE);
//                if (supply.getDate_supply() == null)
//                    toggleButton.setChecked(false);
//                else
//                    toggleButton.setChecked(true);
//            }
//        }

        ImageView productoSearch = findViewById(R.id.img_search_product);
        productoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSupplyActivity_YesNo.this, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        ImageView productoSearchBarcode = findViewById(R.id.img_search_barcode);
        productoSearchBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditSupplyActivity_YesNo.this, ScannerActivity.class);
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
                if (HelperClass.isValidDate(actual, EditSupplyActivity_YesNo.this)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditSupplyActivity_YesNo.this);
                    builder.setTitle(R.string.sumar_dias_a_fecha_actual);
                    builder.setMessage(R.string.cuantos_dias);
                    final EditText input = new EditText(EditSupplyActivity_YesNo.this);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    input.setSingleLine();
                    input.setFilters(new InputFilter[] { new InputFilter.LengthFilter(4) });
                    FrameLayout container = new FrameLayout(EditSupplyActivity_YesNo.this);
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
                                addDays(Integer.parseInt(numero), HelperClass.getShortDate(actual, EditSupplyActivity_YesNo.this));
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
                setLabelFechaLarga(editable.toString());
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_dosis();
            }
        });

        Button btn_end = findViewById(R.id.btn_end);
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
    }

    @Override
    protected void onAllRequestsCompleted() {
        if (isUpdate()) return;
        DoctorVetApp.get().showKeyboard();
        actvProduct.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pet_supply", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString("pet_supplies", MySqlGson.getGson().toJson(pet_supplies));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("pet_supply");
        pet_supply = MySqlGson.getGson().fromJson(objectInString, Pet_supply.class);
        String petSuppliesInString = savedInstanceState.getString("pet_supplies");
        pet_supplies = MySqlGson.getGson().fromJson(petSuppliesInString, new TypeToken<List<Product>>(){}.getType());
        
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
                        getObject().setProduct(product);
                        actvProduct.setText(product.getName());
                    } else {
                        Snackbar.make(DoctorVetApp.getRootForSnack(EditSupplyActivity_YesNo.this), "Producto no encontrado", Snackbar.LENGTH_SHORT).show();
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
    protected void save() {
        if (!validateSomethingToRegister())
            return;

        showWaitDialog();

        Pet_supply_POST_object post_object = new Pet_supply_POST_object();
        post_object.setPet(getObject().getPet());
        post_object.setSupply_array(pet_supplyAdapter.getArrayList());
        final String jsonObject = MySqlGson.postGson().toJson(post_object);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success"))
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
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected void update() {
//        if (!validateProducto() || !validateDate())
//            return;
//
//        showWaitDialog();
//
//        Pet_supply petsupply = getSupply();
//        petsupply.setid_pet(getIdPet());
//        Product product = selectedProductsAdapter.getProducto_by_name(actvProduct.getText().toString());
//        Integer id_product = product.getId();
//        petsupply.setId_product(id_product);
//
//        Date fecha = HelperClass.getShortDate(txtDate.getEditText().getText().toString(), this);
//        String fecha_hora = txtDate.getEditText().getText().toString();
//        Date fecha_hora_date = HelperClass.getShortDateTime(fecha_hora, getBaseContext());
//        if (toggleButton.isChecked()) {
//            petsupply.setDate_tentative(fecha);
//            petsupply.setDate_supply(fecha_hora_date);
//        } else {
//            petsupply.setDate_tentative(fecha);
//            petsupply.setDate_supply(null);
//        }
//        petsupply.getProduct().setThumb_url(product.getPhoto_url());
//        petsupply.getProduct().setName(product.getName());
//        petsupply.getProduct().setShort_name(product.getShort_name());
//
//        final String jsonObject = MySqlGson.getGson().toJson(petsupply);
//        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    String data = MySqlGson.getDataFromResponse(response).toString();
//                    Pet_supply suministro = MySqlGson.getGson().fromJson(data, Pet_supply.class);
//                    finish();
//                } catch (Exception ex) {
//                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
//                } finally {
//                    hideWaitDialog();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                DoctorVetApp.get().handle_volley_error(error, TAG, true);
//                hideWaitDialog();
//            }
//        })
//        {
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                return jsonObject.getBytes();
//            }
//        };
//        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }

    @Override
    protected URL getUrl() {
        if (isUpdate())
            return NetworkUtils.buildPetsSupplyYesNoUrl(getObject().getId(), null);

        return NetworkUtils.buildPetsSupplyYesNoUrl(null, NetworkUtils.petsSupplyEnum.SUPPLY);
    }

    @Override
    protected Pet_supply getObjectFromUI() {
        Pet_supply supply = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), supply, true, "txt_", false, getApplicationContext());

        //fecha
        String fecha_hora = txtDate.getEditText().getText().toString();
        supply.setDate_tentative(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        return supply;
    }

    @Override
    protected Pet_supply getObject() {
        if (pet_supply == null) {
            if (isUpdate()) {
                pet_supply = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.SUPPLY_OBJ.name()), Pet_supply.class);
            } else {
                pet_supply = new Pet_supply();
                pet_supply.setDate_tentative(Calendar.getInstance().getTime());
            }

            Pet pet = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class);
            Pet polish_pet = new Pet(pet.getId(), pet.getName(), pet.getThumb_url());
            pet_supply.setPet(polish_pet);
        }

        return pet_supply;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_supply pet_supply = (Pet_supply) object;

        toolbar_title.setText(pet_supply.getPet().getName());
        toolbar_subtitle.setText("¿Que vas a suministrar?");

        DoctorVetApp.get().setThumb(pet_supply.getPet().getThumb_url(), toolbar_image, R.drawable.ic_dog_holo_dark);

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), pet_supply, "txt_");
        setLabelFechaLarga(txtDate.getEditText().getText().toString());
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
        actvProduct.setAdapter(productsAdapter.getAutocompleteAdapter(EditSupplyActivity_YesNo.this));
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
        DoctorVetApp.get().setAllWidthToDropDown(actvProduct, EditSupplyActivity_YesNo.this);
    }
    
    private void insert_dosis() {
        if (!validateProducto() || !validateDate() || !validateIngresoDuplicadoParaMismaFecha())
            return;

        Pet_supply petsupply = (Pet_supply) getObjectFromUI().clone();

        Date fecha = HelperClass.getShortDate(txtDate.getEditText().getText().toString(), this);
        String hour = HelperClass.getTimeInLocale(Calendar.getInstance().getTime(), EditSupplyActivity_YesNo.this);
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + hour;

        if (toggleButton.isChecked()) {
            petsupply.setDate_tentative(fecha);
            petsupply.setDate_supply(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));
        } else {
            petsupply.setDate_tentative(fecha);
        }

        pet_supplyAdapter.addItem(petsupply);
    }
    private void addDays(int dias, Date dt_actual) {
        txtDate.getEditText().setText(HelperClass.getDateInLocaleShort(HelperClass.sumar_dias(dt_actual, dias)));
        toggleButton.setChecked(false);
    }
    private void setLabelFechaLarga(String input_text) {
        if (HelperClass.isValidDate(input_text, EditSupplyActivity_YesNo.this)) {
            Date fechaDate = HelperClass.getShortDate(input_text, EditSupplyActivity_YesNo.this);
            labelFecha.setText(DateFormat.getDateInstance(DateFormat.FULL).format(fechaDate));
        } else {
            labelFecha.setText("");
        }
    }
    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }

    private boolean validateProducto() {
        return DoctorVetApp.get().validateExistence(txtProduct, getObject().getProduct(), "name", false);
    }
    private boolean validateDate() {
        return HelperClass.validateDate(txtDate, false);
    }
    private boolean validateIngresoDuplicadoParaMismaFecha() {
        String producto = actvProduct.getText().toString().trim();
        Date fecha = HelperClass.getShortDate(txtDate.getEditText().getText().toString(), this);
        ArrayList<Pet_supply> petsupply = pet_supplyAdapter.getArrayList();
        for (Pet_supply m: petsupply) {
            if (producto.toLowerCase().equals(m.getProduct().getName().toLowerCase()) && fecha.equals(m.getDate_tentative())) {
                Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.suministro_duplicado, Snackbar.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }
    private boolean validateSomethingToRegister() {
        if (pet_supplyAdapter.getItemCount() == 0) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.nada_para_registrar, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}