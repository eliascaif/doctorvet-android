package com.xionce.doctorvetServices;

import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Pet_recipe;
import com.xionce.doctorvetServices.data.Pet_study;
import com.xionce.doctorvetServices.data.Pet_study_item;
import com.xionce.doctorvetServices.data.Pet_study_itemsAdapter;
import com.xionce.doctorvetServices.data.Pet_study_value;
import com.xionce.doctorvetServices.data.Pet_study_valuesAdapter;
import com.xionce.doctorvetServices.data.Treatment;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EditStudyActivity_1_1 extends EditBaseActivity {

    private static final String TAG = "EditStudyActivity_1_";
    private AutoCompleteTextView actvItem;
    private TextInputLayout txtItem;
    private TextInputLayout txtValue;

    private Pet_study petStudy = null;
    private Pet_study_value petStudyValue = null;
    private Pet_study_valuesAdapter studyValuesAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_study_1_1);
        toolbar_title.setText("Estudio");
        toolbar_subtitle.setText("¿Que valores vas a incluir?");
        actvItem = findViewById(R.id.actv_item);
        txtItem = findViewById(R.id.txt_item);
        txtValue = findViewById(R.id.txt_value);
        Button btn_add = findViewById(R.id.btn_add);
        Button btn_end = findViewById(R.id.btn_end);
        hideToolbarImage();
        hideFab();
        DoctorVetApp.get().markRequired(txtItem);
        DoctorVetApp.get().markRequired(txtValue);

        getStudyItemsRequest();

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Pet_study_value studyItem = getObject();
        setObjectToUI(studyItem);

        //setup recyclerview
        RecyclerView recycler_items = findViewById(R.id.recycler);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditStudyActivity_1_1.this);
        recycler_items.setLayoutManager(layoutManager);
        recycler_items.setHasFixedSize(true);
        studyValuesAdapter = new Pet_study_valuesAdapter(getStudy().getValues(), DoctorVetApp.AdapterSelectTypes.REMOVE);
        recycler_items.setAdapter(studyValuesAdapter);
        studyValuesAdapter.setOnRemoveItemHandler(new HelperClass.AdapterOnRemoveItemHandler() {
            @Override
            public void onRemoveItem(Object data, View view, int pos) {
                //for items removal
            }
        });

        ImageView itemSearch = findViewById(R.id.img_search_item);
        itemSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditStudyActivity_1_1.this, SearchStudyItemActivity.class);
                intent.putExtra("GET", DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_GET);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_item();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dataBackIntent = new Intent();
                dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name(), MySqlGson.getGson().toJson(getStudy()));
                setResult(RESULT_OK, dataBackIntent);
                finish();
            }
        });

        txtValue.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    add_item();
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
        actvItem.requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("study_item", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name(), MySqlGson.getGson().toJson(getStudy()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("study_item");
        petStudyValue = MySqlGson.getGson().fromJson(objectInString, Pet_study_value.class);

        String studyInStr = savedInstanceState.getString(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name());
        petStudy = MySqlGson.getGson().fromJson(studyInStr, Pet_study.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        //gets
        if (requestCode == HelperClass.REQUEST_GET && data != null) {
            String getMode = data.getStringExtra("GET_MODE");
            if (getMode.equalsIgnoreCase("CREATED")) {
                getStudyItemsRequest();
//                showProgressBar();
//                DoctorVetApp.get().getPetsRecipesForInput(new DoctorVetApp.VolleyCallbackObject() {
//                    @Override
//                    public void onSuccess(Object resultObject) {
//                        hideProgressBar();
//                        Pet_recipe.Pets_recipes_for_input petsRecipesForInput = (Pet_recipe.Pets_recipes_for_input) resultObject;
//                        setTreatmentsAdapter(petsRecipesForInput.getTreatments());
//                    }
//                });
            }

            Pet_study_item studyItem = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_ITEM_OBJ.name()), Pet_study_item.class);
            getObject().setItem(studyItem);
            txtItem.getEditText().setText(studyItem.getName());
            txtValue.requestFocus();
//            DoctorVetApp.get().showKeyboard();
//            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtValue);
        }

        //TODO: prevent hardwipeo
        if (data != null && data.hasExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name())) {
            String studyInString = data.getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name());
            Pet_study study = MySqlGson.getGson().fromJson(studyInString, Pet_study.class);
            studyValuesAdapter.updateList(study.getValues());
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
    protected Pet_study_value getObjectFromUI() {
        Pet_study_value studyItem = getObject();
        studyItem.setValue(txtValue.getEditText().getText().toString());
        return studyItem;
    }

    @Override
    protected Pet_study_value getObject() {
        if (petStudyValue == null)
            petStudyValue = new Pet_study_value();

        return petStudyValue;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_study_value petStudyItem = (Pet_study_value) object;
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), petStudyItem, "txt_");
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void getStudyItemsRequest() {
        initializeInitRequestNumber(1);
        URL url = NetworkUtils.buildGetStudy_itemsUrl(null, true);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Pet_study_item> study_items = MySqlGson.getGson().fromJson(data, new TypeToken<List<Pet_study_item>>(){}.getType());
                            setItemsAdapter(study_items);
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        } finally {
                            setRequestCompleted();
                            hideProgressBar();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        setRequestCompleted();
                        hideProgressBar();
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    private void setItemsAdapter(ArrayList resultList) {
        Pet_study_itemsAdapter itemsAdapter = new Pet_study_itemsAdapter(resultList);
        actvItem.setAdapter(itemsAdapter.getArrayAdapter(EditStudyActivity_1_1.this));
        actvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Pet_study_item item = (Pet_study_item) adapterView.getItemAtPosition(i);
                getObject().setItem(item);
                txtValue.requestFocus();
            }
        });
        actvItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setItem(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvItem);
        DoctorVetApp.get().setAllWidthToDropDown(actvItem, EditStudyActivity_1_1.this);
    }
    private void add_item() {
        if (!validateItem() || !validateValue())
            return;

        //resetfields wipes out txtProduct.text and that set product to null. We need to clone
        Pet_study_value studyItem = (Pet_study_value) getObjectFromUI().clone();
        studyValuesAdapter.addItem(studyItem);

        resetFields();
    }
    private Pet_study getStudy() {
        if (petStudy == null)
            petStudy = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.STUDY_OBJ.name()), Pet_study.class);

        return petStudy;
    }
    private void resetFields() {
        actvItem.setText("");
        txtValue.getEditText().setText("");
        actvItem.requestFocus();
    }

    private boolean validateItem() {
        return DoctorVetApp.get().validateExistence(txtItem, getObject().getItem(), "name", false);
    }
    private boolean validateValue() {
        return HelperClass.validateEmpty(txtValue);
    }

}
