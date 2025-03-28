package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Diagnostic;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_clinic2;
import com.xionce.doctorvetServices.data.Pet_recipe;
import com.xionce.doctorvetServices.data.Pet_recipe_itemsAdapter;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.Sell_itemAdapter;
import com.xionce.doctorvetServices.data.Symptom;
import com.xionce.doctorvetServices.data.Treatment;
import com.xionce.doctorvetServices.data.TreatmentsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class EditRecipeActivity_1 extends EditBaseActivity {

    private static final String TAG = "EditRecipeActivity_1";
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;
    private TextInputLayout txtTreatment;
    private AutoCompleteTextView actvTreatment;
    private TextInputLayout txtNotes;

    private Pet_recipe petRecipe = null;
    private Pet_recipe_itemsAdapter recipeItemsAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_recipe_1);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        txtTreatment = findViewById(R.id.txt_treatment);
        actvTreatment = findViewById(R.id.actv_treatment);
        txtNotes = findViewById(R.id.txt_notes);
        TextView txt_add_products_by_keyboard = findViewById(R.id.txt_add_products_by_keyboard);
        hideFab();

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getPetsRecipesForInput(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Pet_recipe.Pets_recipes_for_input petsRecipesForInput = (Pet_recipe.Pets_recipes_for_input) resultObject;
                    setTreatmentsAdapter(petsRecipesForInput.getTreatments());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()) {
            toolbar_subtitle.setText(R.string.recipe_edit);
        } else {
            toolbar_subtitle.setText(R.string.recipe_new);
        }

        Pet_recipe recipe = getObject();
        setObjectToUI(recipe);

        //setup recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditRecipeActivity_1.this);
        RecyclerView recyclerView = findViewById(R.id.recycler_products);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recipeItemsAdapter = new Pet_recipe_itemsAdapter(getObject().getItems(), Pet_recipe_itemsAdapter.RecipeItemsAdapterTypes.FOR_RECIPE_INPUT);
        recyclerView.setAdapter(recipeItemsAdapter);
        recipeItemsAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for products removal
            }
        });

        txt_add_products_by_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRecipeActivity_1.this, EditRecipeActivity_1_1.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name(), MySqlGson.postGson().toJson(getObjectFromUI()));
                startActivityForResult(intent, HelperClass.REQUEST_UPDATE);
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

        ImageView treatmentSearch = findViewById(R.id.img_search_treatment);
        treatmentSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditRecipeActivity_1.this, SearchTreatmentActivity.class);
                intent.putExtra("GET", DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_GET);
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
        actvTreatment.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("pets_recipes", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("pets_recipes");
        petRecipe = MySqlGson.getGson().fromJson(objectInString, Pet_recipe.class);

        String petsRecipesForInputInString = DoctorVetApp.get().readFromDisk("pets_recipes_for_input");
        Pet_recipe.Pets_recipes_for_input petsClinic2ForInput = MySqlGson.getGson().fromJson(petsRecipesForInputInString, Pet_recipe.Pets_recipes_for_input.class);
        setTreatmentsAdapter(petsClinic2ForInput.getTreatments());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //gets
        if (requestCode == HelperClass.REQUEST_GET && data != null) {
            String getMode = data.getStringExtra("GET_MODE");
            if (getMode.equalsIgnoreCase("CREATED")) {
                showProgressBar();
                DoctorVetApp.get().getPetsRecipesForInput(new DoctorVetApp.VolleyCallbackObject() {
                    @Override
                    public void onSuccess(Object resultObject) {
                        hideProgressBar();
                        Pet_recipe.Pets_recipes_for_input petsRecipesForInput = (Pet_recipe.Pets_recipes_for_input) resultObject;
                        setTreatmentsAdapter(petsRecipesForInput.getTreatments());
                    }
                });
            }

            Treatment treatment = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name()), Treatment.class);
            getObject().setTreatment(treatment);
            txtTreatment.getEditText().setText(treatment.getName());
            txtNotes.requestFocus();
        }

        //TODO: prevent hardwipeo
        if (data != null && data.hasExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name())) {
            String recipeInString = data.getStringExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name());
            Pet_recipe recipe = MySqlGson.getGson().fromJson(recipeInString, Pet_recipe.class);
            recipeItemsAdapter.updateList(recipe.getItems());
        }

    }

    @Override
    protected void save() {
        if (!validateDate() || !validateHour() || !validateTreatment()
            || !validateSomethingToRegister())
                return;

        showWaitDialog();

        final Pet_recipe recipe = getObjectFromUI();
        final String recipe_json_object = MySqlGson.getGson().toJson(recipe);

        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_recipe recipe_from_response = MySqlGson.getGson().fromJson(data, Pet_recipe.class);

                    if (isUpdate()) {
                        Intent dataBackIntent = new Intent();
                        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name(), MySqlGson.getGson().toJson(recipe_from_response));
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
            public byte[] getBody() {
                return recipe_json_object.getBytes();
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
            return NetworkUtils.buildPetsRecipesUrl(false, getObject().getId());

        return NetworkUtils.buildPetsRecipesUrl(false, null);
    }

    @Override
    protected Pet_recipe getObjectFromUI() {
        final Pet_recipe recipe = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), recipe, true, "txt_", true, getApplicationContext());

        //date
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        recipe.setDate(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

//        //starting date
//        String startingDate = txtStartingDate.getEditText().getText().toString() + " " + txtStartingHour.getEditText().getText().toString();
//        recipe.setStarting_date(HelperClass.getShortDateTime(startingDate, getBaseContext()));
//
//        //ending date
//        String endingDate = txtEndingDate.getEditText().getText().toString() + " " + txtEndingHour.getEditText().getText().toString();
//        recipe.setEnding_date(HelperClass.getShortDateTime(endingDate, getBaseContext()));

        //polish
        Pet pet = recipe.getPet();
        Pet pet_polish = new Pet(pet.getId(), pet.getName(), pet.getThumb_url());
        recipe.setPet(pet_polish);

        return recipe;
    }

    @Override
    protected Pet_recipe getObject() {
        if (petRecipe == null) {
            if (isUpdate()) {
                petRecipe = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.RECIPE_OBJ.name()), Pet_recipe.class);
            } else {
                petRecipe = new Pet_recipe();
                petRecipe.setDate(Calendar.getInstance().getTime());
                petRecipe.setItems(new ArrayList<>());
            }

            petRecipe.setPet(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class));
        }

        return petRecipe;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_recipe petRecipe = (Pet_recipe) object;

        DoctorVetApp.get().setThumb(petRecipe.getPet().getThumb_url(), toolbar_image, R.drawable.ic_pets_dark);
        toolbar_title.setText(petRecipe.getPet().getName());

        if (petRecipe.getTreatment() != null)
            actvTreatment.setText(petRecipe.getTreatment().getName());

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petRecipe, "txt_");

        //la hora no es un campo de la base de datos
        if (petRecipe.getDate() != null)
            txtHour.getEditText().setText(HelperClass.getTimeInLocale(petRecipe.getDate(), EditRecipeActivity_1.this));
//        if (petRecipe.getStarting_date() != null)
//            txtStartingHour.getEditText().setText(HelperClass.getTimeInLocale(petRecipe.getStarting_date(), EditRecipeActivity.this));
//        if (petRecipe.getEnding_date() != null)
//            txtEndingHour.getEditText().setText(HelperClass.getTimeInLocale(petRecipe.getEnding_date(), EditRecipeActivity.this));
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setTreatmentsAdapter(ArrayList<Treatment> treatments) {
        TreatmentsAdapter treatmentsAdapter = new TreatmentsAdapter(treatments);
        actvTreatment.setAdapter(treatmentsAdapter.getArrayAdapter(EditRecipeActivity_1.this));
        actvTreatment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setTreatment((Treatment) adapterView.getItemAtPosition(i));
                txtNotes.requestFocus();
            }
        });
        actvTreatment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setTreatment(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvTreatment);
        DoctorVetApp.get().setAllWidthToDropDown(actvTreatment, EditRecipeActivity_1.this);
    }

    private void searchTime() {
        DoctorVetApp.get().searchTimeSetInit(txtHour, getSupportFragmentManager());
    }
    private void searchDate() {
        DoctorVetApp.get().searchDateSetInit(txtDate, getSupportFragmentManager());
    }

    private boolean validateDate() {
        return HelperClass.validateDate(txtDate, false);
    }
    private boolean validateHour() {
        return HelperClass.validateHour(txtHour, false);
    }
    private boolean validateTreatment() {
        return DoctorVetApp.get().validateExistence(txtTreatment, getObject().getTreatment(), "name", true);
    }
    private boolean validateSomethingToRegister() {
        if (getObject().getItems().size() == 0) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.nada_para_registrar, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}
