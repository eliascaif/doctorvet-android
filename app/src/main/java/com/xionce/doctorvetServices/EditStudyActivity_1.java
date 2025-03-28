package com.xionce.doctorvetServices;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Pet_study_valuesAdapter;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_study;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.data.ResourcesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_IMAGE;

public class EditStudyActivity_1 extends EditBaseActivity {

    private static final String TAG = "EditStudyActivity_1";
    private TextInputLayout txtStudy;
    private AutoCompleteTextView actvStudy;
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;

    private Pet_study pet_study = null;
    private Pet_study_valuesAdapter studyValuesAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_study_1);
        txtStudy = findViewById(R.id.txt_product);
        actvStudy = findViewById(R.id.actv_product);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        TextInputLayout txtNotes = findViewById(R.id.txt_notes);
        DoctorVetApp.get().markRequired(txtStudy);
        TextView txt_add_products_by_keyboard = findViewById(R.id.txt_add_products_by_keyboard);
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getVetStudies(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setStudiesAdapter(resultList);
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()) {
            toolbar_subtitle.setText(R.string.editar_estudio);
        } else {
            toolbar_subtitle.setText(R.string.nuevo_estudio);
        }

        Pet_study study = getObject();
        setObjectToUI(study);

        //setup recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditStudyActivity_1.this);
        RecyclerView recyclerView_items = findViewById(R.id.recycler_values);
        recyclerView_items.setLayoutManager(layoutManager);
        recyclerView_items.setHasFixedSize(true);
        studyValuesAdapter = new Pet_study_valuesAdapter(getObject().getValues(), DoctorVetApp.AdapterSelectTypes.REMOVE);
        recyclerView_items.setAdapter(studyValuesAdapter);
        studyValuesAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
            }
        });

        txt_add_products_by_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateStudy())
                    return;

                Intent intent = new Intent(EditStudyActivity_1.this, EditStudyActivity_1_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
                startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
            }
        });

        //resources
        RecyclerView recyclerView_resources = findViewById(R.id.recyclerview_resources);
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(EditStudyActivity_1.this, 2);
        recyclerView_resources.setLayoutManager(layoutManager2);
        recyclerView_resources.setHasFixedSize(true);
        resourcesAdapter = new ResourcesAdapter(getObject().getResources(), true, EditStudyActivity_1.this);
        recyclerView_resources.setAdapter(resourcesAdapter);

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

        ImageView timeSearch = findViewById(R.id.img_search_time);
        timeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });
        txtHour.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTime();
            }
        });

        ImageView btn_add_imagen = findViewById(R.id.btn_add_image);
        btn_add_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(EditStudyActivity_1.this, R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_take_image, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_take_photo) {
                            if (HelperClass.checkPermission(Manifest.permission.CAMERA, getApplicationContext())) {
                                DoctorVetApp.get().dispatchTakePictureIntent(EditStudyActivity_1.this);
                            } else {
                                HelperClass.requestPermission(EditStudyActivity_1.this, Manifest.permission.CAMERA, REQUEST_TAKE_IMAGE);
                            }
                        } else if (item.getItemId() == R.id.action_open_gallery) {
                            DoctorVetApp.get().dispatchOpenGalleryIntent(EditStudyActivity_1.this);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        ImageView btn_add_archivo = findViewById(R.id.btn_add_archivo);
        btn_add_archivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorVetApp.get().dispatchOpenFileIntent(EditStudyActivity_1.this);
            }
        });

        ImageView estudioSearch = findViewById(R.id.img_search_estudio);
        estudioSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditStudyActivity_1.this, SearchProductStudyActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
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
        actvStudy.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("study", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("study");
        pet_study = MySqlGson.getGson().fromJson(objectInString, Pet_study.class);

        String vetStudiesInString = DoctorVetApp.get().readFromDisk("vet_studies");
        ArrayList<Product> vetStudies = MySqlGson.getGson().fromJson(vetStudiesInString, new TypeToken<List<Product>>(){}.getType());
        setStudiesAdapter(vetStudies);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            if (data.hasExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name())) {
                Product product = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
                txtStudy.getEditText().setText(product.getName());
                txtStudy.getEditText().clearFocus();
                getObject().setProduct(product);
            }
        }

        if (data != null && data.hasExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name())) {
            String studyInString = data.getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name());
            Pet_study study = MySqlGson.getGson().fromJson(studyInString, Pet_study.class);
            studyValuesAdapter.updateList(study.getValues());
        }

    }

    @Override
    protected void save() {
        if (!validateStudy() || !validateDate())
            return;
        //|| !validateSomethingToRegister()

        showWaitDialog();

        final Pet_study petstudy = (Pet_study) getObjectFromUI().clone();
        petstudy.setResources(resourcesAdapter.getResources());

        final String jsonObject = MySqlGson.postGson().toJson(petstudy);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_study estudio_from_response = MySqlGson.getGson().fromJson(data, Pet_study.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(petstudy.getResources(), estudio_from_response.getResources());

                    if (isUpdate()) {
                        Intent dataBackIntent = new Intent();
                        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name(), MySqlGson.getGson().toJson(estudio_from_response));
                        setResult(RESULT_OK, dataBackIntent);
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
            public byte[] getBody() throws AuthFailureError {
                return jsonObject.getBytes();
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
            return NetworkUtils.buildStudyUrl(getObject().getId(), "RETURN_OBJECT", null, null, false);

        return NetworkUtils.buildStudyUrl(null, "RETURN_OBJECT", null, null, false);
    }

    @Override
    protected Pet_study getObjectFromUI() {
        Pet_study study = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), study, true, "txt_", false, getApplicationContext());

        //fecha
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        study.setDate(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        //polish
        Pet pet = study.getPet();
        Pet pet_polish = new Pet(pet.getId(), pet.getName(), pet.getThumb_url());
        study.setPet(pet_polish);

        Product product = study.getProduct();
        if (product != null) {
            Product polish_product = new Product(product.getId(), product.getName());
            study.setProduct(polish_product);
        }

        return study;
    }

    @Override
    protected Pet_study getObject() {
        if (pet_study == null) {
            if (isUpdate()) {
                pet_study = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name()), Pet_study.class);
            } else {
                pet_study = new Pet_study();
                pet_study.setDate(Calendar.getInstance().getTime());
                pet_study.setValues(new ArrayList<>());
            }

            pet_study.setPet(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class));
        }

        return pet_study;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_study pet_study = (Pet_study) object;

        DoctorVetApp.get().setThumb(pet_study.getPet().getThumb_url(), toolbar_image, R.drawable.ic_pets_dark);
        toolbar_title.setText(pet_study.getPet().getName());

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), pet_study, "txt_");

        //la hora no es un campo de la base de datos
        if (pet_study.getDate() != null)
            txtHour.getEditText().setText(HelperClass.getTimeInLocale(pet_study.getDate(), EditStudyActivity_1.this));
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return pet_study;
    }

    @Override
    protected String getUploadTableName() {
        return "pets_studies";
    }

    private void setStudiesAdapter(ArrayList<Product> resultList) {
        ProductsAdapter productsAdapter = new ProductsAdapter(resultList, ProductsAdapter.Products_types.NORMAL);
        actvStudy.setAdapter(productsAdapter.getAutocompleteAdapter(EditStudyActivity_1.this));
        actvStudy.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product)adapterView.getItemAtPosition(i);
                getObject().setProduct(product);
            }
        });
        actvStudy.addTextChangedListener(new TextWatcher() {
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
        DoctorVetApp.get().setOnTouchToShowDropDown(actvStudy);
        DoctorVetApp.get().setAllWidthToDropDown(actvStudy, EditStudyActivity_1.this);
    }
    private void searchTime() {
        DoctorVetApp.get().searchTimeSetInit(txtHour, getSupportFragmentManager());
    }
    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }

    private boolean validateStudy() {
        return DoctorVetApp.get().validateExistence(txtStudy, getObject().getProduct(), "name", false);
    }
    private boolean validateDate() {
        return HelperClass.validateDate(txtDate, false);
    }
    private boolean validateSomethingToRegister() {
        if (getObject().getValues().size() == 0) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.nada_para_registrar, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
