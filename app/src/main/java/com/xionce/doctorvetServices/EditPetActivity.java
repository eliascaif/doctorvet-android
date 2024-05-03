package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.OwnersAdapter;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_character;
import com.xionce.doctorvetServices.data.Pet_charactersAdapter;
import com.xionce.doctorvetServices.data.Pet_pelage;
import com.xionce.doctorvetServices.data.Pet_pelagesAdapter;
import com.xionce.doctorvetServices.data.Pet_race;
import com.xionce.doctorvetServices.data.Pet_racesAdapter;
import com.xionce.doctorvetServices.data.Pet_gender;
import com.xionce.doctorvetServices.data.Pet_gendersAdapter;
import com.xionce.doctorvetServices.utilities.DatePickerFragment;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditPetActivity extends EditBaseActivity {

    private static final String TAG = "EditPetActivity";
    private TextInputLayout txtOwners;
    private AutoCompleteTextView actv_owners;
    private TextInputLayout txtName;
    private TextInputLayout txtRace;
    private AutoCompleteTextView actv_race;
    private TextInputLayout txtPelage;
    private AutoCompleteTextView actv_pelage;
    private TextInputLayout txtGender;
    private AutoCompleteTextView actv_gender;
    private TextInputLayout txtCharacter;
    private AutoCompleteTextView actv_character;
    private TextInputLayout txtBirthday;
    private TextInputLayout txtChip;
    private TextInputLayout txtNotes;
    private TextInputLayout txtWeight;
    private CheckBox chkDeath;

    private Pet pet = null;
    private OwnersAdapter selectedOwnersAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_pet);
        txtOwners = findViewById(R.id.txt_owners);
        actv_owners = findViewById(R.id.actv_owners);
        txtName = findViewById(R.id.txt_name);
        txtRace = findViewById(R.id.txt_race);
        actv_race = findViewById(R.id.actv_race);
        txtPelage = findViewById(R.id.txt_pelage);
        actv_pelage = findViewById(R.id.actv_pelage);
        txtGender = findViewById(R.id.txt_gender);
        actv_gender = findViewById(R.id.actv_gender);
        txtCharacter = findViewById(R.id.txt_character);
        actv_character = findViewById(R.id.actv_character);
        txtBirthday = findViewById(R.id.txt_birthday);
        txtChip = findViewById(R.id.txt_chip);
        txtNotes = findViewById(R.id.txt_notes);
        txtWeight = findViewById(R.id.txt_weight);
        txtWeight.setHint(getString(R.string.peso_en_default, DoctorVetApp.get().getVet().getWeightUnit()));
        chkDeath = findViewById(R.id.chk_death);
        DoctorVetApp.get().markRequired(txtOwners);
        DoctorVetApp.get().markRequired(txtName);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(2);
            DoctorVetApp.get().getOwners(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    setRequestCompleted();
                    setOwnersAdapter(resultList);
                }
            });
            DoctorVetApp.get().getPetsForInputObject(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Pet.Pets_for_input petsForInput = (Pet.Pets_for_input)resultObject;
                    setPetsRaces(petsForInput.getPets_races());
                    setPetsPelages(petsForInput.getPets_pelages());
                    setPetsGenders(petsForInput.getPets_genders());
                    setPetsCharacters(petsForInput.getPets_characters());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()){
            toolbar_title.setText("Editar " + DoctorVetApp.get().getPetNaming().toLowerCase());
            toolbar_subtitle.setText(R.string.edit_subtitle);
        } else {
            toolbar_title.setText(DoctorVetApp.get().getPetNamingNew());
            toolbar_subtitle.setText(R.string.new_subtitle);
        }

        Pet pet = getObject();
        setObjectToUI(pet);
        implementTakePhoto(getResourceObject());

        LinearLayoutManager layoutManager = new LinearLayoutManager(EditPetActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView recyclerView = findViewById(R.id.recycler_owners);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        selectedOwnersAdapter = new OwnersAdapter(pet.getOwners(), DoctorVetApp.Adapter_types.REMOVE);
        recyclerView.setAdapter(selectedOwnersAdapter);

        ImageView searchOwner = findViewById(R.id.img_search_owner);
        searchOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, SearchOwnerActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView createOwner = findViewById(R.id.img_create_owner);
        createOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, EditOwnerActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_CREATE);
            }
        });

        ImageView searchRaza = findViewById(R.id.img_search_race);
        searchRaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, SearchPetRaceActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PET_RACE_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView searchPelaje = findViewById(R.id.img_search_pelage);
        searchPelaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, SearchPetPelageActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PET_PELAGE_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView searchCaracter = findViewById(R.id.img_search_character);
        searchCaracter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, SearchPetCharacterActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PET_CHARACTER_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView searchSex = findViewById(R.id.img_search_gender);
        searchSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, SearchPetGenderActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PET_GENDER_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView dateSearch = findViewById(R.id.img_search_date);
        dateSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerFragment().showDatePickerDialog(getSupportFragmentManager(), txtBirthday);
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtWeight);
            }
        });

        ImageView barcodeSearch = findViewById(R.id.img_search_barcode);
        barcodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPetActivity.this, ScannerActivity.class);
                startActivityForResult(intent, HelperClass.REQUEST_READ_BARCODE);
            }
        });

        txtNotes.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
        outState.putString("pet", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("pet");
        pet = MySqlGson.getGson().fromJson(objectInString, Pet.class);

        String ownersInString = DoctorVetApp.get().readFromDisk("owners");
        ArrayList<Owner> owners = MySqlGson.getGson().fromJson(ownersInString, new TypeToken<List<Owner>>(){}.getType());
        setOwnersAdapter(owners);

        String petsForInputInString = DoctorVetApp.get().readFromDisk("pets_for_input");
        Pet.Pets_for_input petsForInput = MySqlGson.getGson().fromJson(petsForInputInString, Pet.Pets_for_input.class);
        setPetsRaces(petsForInput.getPets_races());
        setPetsPelages(petsForInput.getPets_pelages());
        setPetsGenders(petsForInput.getPets_genders());
        setPetsCharacters(petsForInput.getPets_characters());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //barcodescanner
        if (requestCode == HelperClass.REQUEST_READ_BARCODE && data != null) {
            txtChip.getEditText().setText(data.getStringExtra(HelperClass.INTENT_EXTRA_BARCODE));
            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtNotes);
            return;
        }

        //busqueda
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name())) {
                Owner owner = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
                selectedOwnersAdapter.add(owner.getPolish());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtName);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_RACE_OBJ.name())) {
                Pet_race petrace = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_RACE_OBJ.name()), Pet_race.class);
                getObject().setRace(petrace);
                txtRace.getEditText().setText(petrace.getName());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtPelage);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_PELAGE_OBJ.name())) {
                Pet_pelage petpelage = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_PELAGE_OBJ.name()), Pet_pelage.class);
                getObject().setPelage(petpelage);
                txtPelage.getEditText().setText(petpelage.getName());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtGender);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_CHARACTER_OBJ.name())) {
                Pet_character petcharacter = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_CHARACTER_OBJ.name()), Pet_character.class);
                getObject().setCharacter(petcharacter);
                txtCharacter.getEditText().setText(petcharacter.getName());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtBirthday);
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PET_GENDER_OBJ.name())) {
                Pet_gender petgender = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PET_GENDER_OBJ.name()), Pet_gender.class);
                getObject().setGender(petgender);
                txtGender.getEditText().setText(petgender.getName());
                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtCharacter);
            }
        }

        //creacion de propietarios
        if (requestCode == HelperClass.REQUEST_CREATE && data != null) {
            showProgressBar();
            DoctorVetApp.get().getOwners(new DoctorVetApp.VolleyCallbackArrayList() {
                @Override
                public void onSuccess(ArrayList resultList) {
                    hideProgressBar();
                    setOwnersAdapter(resultList);
                }
            });

            Owner owner = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class);
            selectedOwnersAdapter.add(owner.getPolish());
            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtName);
        }
    }

    @Override
    protected void save() {
        if (!validateNullOwner() || !validateName() || !validateRace() ||
                !validatePelage() || !validateGender() || !validateCharacter() || !validateBirthday())
            return;

        showWaitDialog();

        Pet pet = getObjectFromUI();
        final String pet_json_object = MySqlGson.postGson().toJson(pet);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet response_pet = MySqlGson.getGson().fromJson(data, Pet.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(pet.getResources(), response_pet.getResources());

                    if (!isUpdate()) {
                        Intent activity = new Intent(EditPetActivity.this, ViewPetActivity.class);
                        activity.putExtra(DoctorVetApp.INTENT_VALUES.PET_ID.name(), response_pet.getId());
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
                return pet_json_object.getBytes();
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
        if (getMethod() == Request.Method.POST)
            return NetworkUtils.buildPetUrl(null, "RETURN_OBJECT");

        return NetworkUtils.buildPetUrl(getObject().getId(), "RETURN_OBJECT");
    }

    @Override
    protected Pet getObjectFromUI() {
        Pet pet = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), pet, true, "txt_", true, getApplicationContext());

        pet.setDeath(0);
        if (chkDeath.isChecked())
            pet.setDeath(1);

        return pet;
    }

    @Override
    protected Pet getObject() {
        if (pet == null) {
            if (isUpdate()) {
                pet = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class);
            } else {
                pet = new Pet();
                if (getIntent().hasExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()))
                    pet.getOwners().add(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.OWNER_OBJ.name()), Owner.class));
            }
        }

        return pet;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet pet = (Pet) object;

        loadThumb(pet.getThumb_url());

        txtOwners.setHint(DoctorVetApp.get().getOwnerNaming() + "/s");
        txtName.setHint("Nombre " + DoctorVetApp.get().getPetNaming().toLowerCase());

        if (pet.getRace() != null)
            actv_race.setText(pet.getRace().getName());

        if (pet.getPelage() != null)
            actv_pelage.setText(pet.getPelage().getName());

        if (pet.getGender() != null)
            actv_gender.setText(pet.getGender().getName());

        if (pet.getCharacter() != null)
            actv_character.setText(pet.getCharacter().getName());

        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), pet, "txt_");

        if (pet.getDeath() != null && pet.getDeath() == 1)
            chkDeath.setChecked(true);
    }

    @Override
    protected void remove_thumb(DoctorVetApp.IResourceObject photoObject) {
        super.remove_thumb(getResourceObject());
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return getObject();
    }

    @Override
    protected String getUploadTableName() {
        return "pets";
    }

    private void setOwnersAdapter(ArrayList<Owner> owners) {
        OwnersAdapter ownersAdapter = new OwnersAdapter(owners, DoctorVetApp.Adapter_types.COMPACT);
        actv_owners.setAdapter(ownersAdapter.getAutocompleteAdapter(EditPetActivity.this));
        actv_owners.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedOwnersAdapter.add((Owner)adapterView.getItemAtPosition(i));
                actv_owners.setText("");
                txtName.requestFocus();
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_owners);
        DoctorVetApp.get().setAllWidthToDropDown(actv_owners, EditPetActivity.this);
    }
    private void setPetsRaces(ArrayList<Pet_race> pets_races) {
        Pet_racesAdapter racesAdapter = new Pet_racesAdapter(pets_races);
        actv_race.setAdapter(racesAdapter.getAutocompleteAdapter(EditPetActivity.this));
        actv_race.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setRace((Pet_race) adapterView.getItemAtPosition(i));
                txtPelage.requestFocus();
            }
        });
        actv_race.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setRace(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_race);
        DoctorVetApp.get().setAllWidthToDropDown(actv_race, EditPetActivity.this);
    }
    private void setPetsPelages(ArrayList<Pet_pelage> pets_pelages) {
        Pet_pelagesAdapter pelagesAdapter = new Pet_pelagesAdapter(pets_pelages);
        actv_pelage.setAdapter(pelagesAdapter.getArrayAdapter(EditPetActivity.this));
        actv_pelage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setPelage((Pet_pelage) adapterView.getItemAtPosition(i));
                txtGender.requestFocus();
            }
        });
        actv_pelage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setPelage(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_pelage);
        DoctorVetApp.get().setAllWidthToDropDown(actv_pelage, EditPetActivity.this);
    }
    private void setPetsGenders(ArrayList<Pet_gender> pets_genders) {
        Pet_gendersAdapter gendersAdapter = new Pet_gendersAdapter(pets_genders);
        actv_gender.setAdapter(gendersAdapter.getArrayAdapter(EditPetActivity.this));
        actv_gender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setGender((Pet_gender) adapterView.getItemAtPosition(i));
                txtCharacter.requestFocus();
            }
        });
        actv_gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setGender(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_gender);
        DoctorVetApp.get().setAllWidthToDropDown(actv_gender, EditPetActivity.this);
    }
    private void setPetsCharacters(ArrayList<Pet_character> pets_characters) {
        Pet_charactersAdapter charactersAdapter = new Pet_charactersAdapter(pets_characters);
        actv_character.setAdapter(charactersAdapter.getArrayAdapter(EditPetActivity.this));
        actv_character.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setCharacter((Pet_character) adapterView.getItemAtPosition(i));
                txtBirthday.requestFocus();
            }
        });
        actv_character.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setCharacter(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actv_character);
        DoctorVetApp.get().setAllWidthToDropDown(actv_character, EditPetActivity.this);
    }

    private boolean validateNullOwner() {
        if (getObject().getOwners().size() != 0)
            return true;

        Snackbar.make(DoctorVetApp.getRootForSnack(this), "Ingresa " + DoctorVetApp.get().getOwnerNaming() + "/s", Snackbar.LENGTH_LONG).show();
        txtOwners.requestFocus();
        return false;
    }
    private boolean validateName() {
        return HelperClass.validateEmpty(txtName);
    }
    private boolean validateRace() {
        return DoctorVetApp.get().validateExistence(txtRace, getObject().getRace(), "name", true);
    }
    private boolean validatePelage() {
        return DoctorVetApp.get().validateExistence(txtPelage, getObject().getPelage(), "name", true);
    }
    private boolean validateGender() {
        return DoctorVetApp.get().validateExistence(txtGender, getObject().getGender(), "name", true);
    }
    private boolean validateCharacter() {
        return DoctorVetApp.get().validateExistence(txtCharacter, getObject().getCharacter(), "name", true);
    }
    private boolean validateBirthday() {
        return HelperClass.validateDate(txtBirthday,  true);
    }

}
