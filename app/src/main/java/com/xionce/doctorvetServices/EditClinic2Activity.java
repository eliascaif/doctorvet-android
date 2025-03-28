package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_RECORD_AUDIO;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_IMAGE;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_VIDEO;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Diagnostic;
import com.xionce.doctorvetServices.data.DiagnosticsAdapter;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_clinic2;
import com.xionce.doctorvetServices.data.Pet_recipe;
import com.xionce.doctorvetServices.data.ResourcesAdapter;
import com.xionce.doctorvetServices.data.Symptom;
import com.xionce.doctorvetServices.data.SymptomsAdapter;
import com.xionce.doctorvetServices.data.Treatment;
import com.xionce.doctorvetServices.data.TreatmentsAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

public class EditClinic2Activity extends EditBaseActivity {

    private static final String TAG = "EditClinicActivity";
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;
    private TextInputLayout txtAnamnesis;
    private TextInputLayout txtTemp;
    private TextInputLayout txtWeight;
    private TextView label_btn_add_audio;
    private AutoCompleteTextView actvSymptoms;
    private TextInputLayout txtSymptom;
    private TextInputLayout txtDiagnostic;
    private AutoCompleteTextView actvDiagnostics;
    private TextInputLayout txtTreatments;
    private AutoCompleteTextView actvTreatments;
    private TextView txtPossibleDiagnostics;

    private Pet_clinic2 pet_clinic2 = null;
    private SymptomsAdapter selectedSymptomsAdapter = null;
    private ImageView btn_add_audio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_clinic_2);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        txtAnamnesis = findViewById(R.id.txt_anamnesis);
        txtTemp = findViewById(R.id.txt_temp);
        txtTemp.setHint(getString(R.string.temperatura_en_default, DoctorVetApp.get().getVet().getTempUnit()));
        txtWeight = findViewById(R.id.txt_weight);
        txtWeight.setHint(getString(R.string.peso_en_default, DoctorVetApp.get().getVet().getWeightUnit()));
        txtSymptom = findViewById(R.id.txt_symptom);
        actvSymptoms = findViewById(R.id.actv_symptoms);
        actvDiagnostics = findViewById(R.id.actv_diagnostic);
        actvTreatments = findViewById(R.id.actv_treatment);
        txtDiagnostic = findViewById(R.id.txt_diagnostic);
        txtTreatments = findViewById(R.id.txt_treatment);
        txtPossibleDiagnostics = findViewById(R.id.txt_possible_diagnostics);
//        txtHelperMethods = findViewById(R.id.txt_helper_methods);
        btn_add_audio = findViewById(R.id.btn_add_audio);
        label_btn_add_audio = findViewById(R.id.label_btn_add_audio);
        DoctorVetApp.get().markRequired(txtAnamnesis);

        if (savedInstanceState == null) {
            initializeInitRequestNumber(1);
            DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
                @Override
                public void onSuccess(Object resultObject) {
                    setRequestCompleted();
                    Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
                    setSymptomsAdapter(clinic2ForInput.getSymptoms());
                    setDiagnosticsAdapter(clinic2ForInput.getDiagnostics());
                    setTreatments(clinic2ForInput.getTreatments());
                }
            });
        } else {
            restoreFromBundle(savedInstanceState);
        }

        if (isUpdate()) {
            toolbar_subtitle.setText(R.string.edit_clinic_ext);
        } else {
            toolbar_subtitle.setText(R.string.new_clinic_ext);
        }

        Pet_clinic2 clinic2 = getObject();
        setObjectToUI(clinic2);

        LinearLayoutManager layoutManager = new LinearLayoutManager(EditClinic2Activity.this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        RecyclerView recyclerView = findViewById(R.id.recycler_symptoms);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        selectedSymptomsAdapter = new SymptomsAdapter(getObject().getSymptoms(), DoctorVetApp.AdapterSelectTypes.REMOVE);
        recyclerView.setAdapter(selectedSymptomsAdapter);
        selectedSymptomsAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnCancelClickHandler() {
            @Override
            public void onCancelClick(Object data, View view, int pos) {
                Symptom symptom = (Symptom) data;
                removeSymptom(symptom);
            }
        });

        //resources
        RecyclerView recyclerView_res = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager2 = new GridLayoutManager(EditClinic2Activity.this, 2);
        recyclerView_res.setLayoutManager(layoutManager2);
        recyclerView_res.setHasFixedSize(true);
        resourcesAdapter = new ResourcesAdapter(getObject().getResources(), true, EditClinic2Activity.this);
        recyclerView_res.setAdapter(resourcesAdapter);

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

        ImageView iconSearchSymptom = findViewById(R.id.img_search_symptom);
        iconSearchSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditClinic2Activity.this, SearchSymptomActivity.class);
                intent.putExtra("GET", DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_GET);
            }
        });
//        ImageView createSymptom = findViewById(R.id.img_create_symptom);
//        createSymptom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditClinic2Activity.this, EditSDTActivity.class);
//                intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name());
//                startActivityForResult(intent, HelperClass.REQUEST_CREATE);
//            }
//        });

        ImageView iconSearchDiagnostic = findViewById(R.id.img_search_diagnostic);
        iconSearchDiagnostic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditClinic2Activity.this, SearchDiagnosticActivity.class);
                intent.putExtra("GET", DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_GET);
            }
        });
//        ImageView createDiagnostic = findViewById(R.id.img_create_diagnostic);
//        createDiagnostic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditClinic2Activity.this, EditSDTActivity.class);
//                intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name());
//                startActivityForResult(intent, HelperClass.REQUEST_CREATE);
//            }
//        });

        ImageView iconSearchTreatment = findViewById(R.id.img_search_treatment);
        iconSearchTreatment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditClinic2Activity.this, SearchTreatmentActivity.class);
                intent.putExtra("GET", DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name());
                startActivityForResult(intent, HelperClass.REQUEST_GET);
            }
        });
//        ImageView createTreatment = findViewById(R.id.img_create_treatment);
//        createTreatment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(EditClinic2Activity.this, EditSDTActivity.class);
//                intent.putExtra(DoctorVetApp.REQUEST_CREATE_OBJECT, DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name());
//                startActivityForResult(intent, HelperClass.REQUEST_CREATE);
//            }
//        });

        ImageButton btn_add_imagen = findViewById(R.id.btn_add_image);
        btn_add_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(EditClinic2Activity.this, R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_take_image, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_take_photo) {
                            if (HelperClass.checkPermission(Manifest.permission.CAMERA, getApplicationContext())) {
                                DoctorVetApp.get().dispatchTakePictureIntent(EditClinic2Activity.this);
                            } else {
                                HelperClass.requestPermission(EditClinic2Activity.this, Manifest.permission.CAMERA, REQUEST_TAKE_IMAGE);
                            }
                        } else if (item.getItemId() == R.id.action_open_gallery) {
                            DoctorVetApp.get().dispatchOpenGalleryIntent(EditClinic2Activity.this);
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        ImageButton btn_add_video = findViewById(R.id.btn_add_video);
        btn_add_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (HelperClass.checkPermission(Manifest.permission.CAMERA, getApplicationContext())) {
                    record_video();
                } else {
                    HelperClass.requestPermission(EditClinic2Activity.this, Manifest.permission.CAMERA, REQUEST_TAKE_VIDEO);
                }
            }
        });

        btn_add_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) return;
                mLastClickTime = now;

                if (HelperClass.checkPermission(Manifest.permission.RECORD_AUDIO, getApplicationContext())) {
                    record_audio();
                } else {
                    HelperClass.requestPermission(EditClinic2Activity.this, Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO);
                }
            }
        });

        ImageButton btn_add_archivo = findViewById(R.id.btn_add_archivo);
        btn_add_archivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorVetApp.get().dispatchOpenFileIntent(EditClinic2Activity.this);
            }
        });

    }

    @Override
    protected void onAllRequestsCompleted() {
        if (isUpdate()) return;
        DoctorVetApp.get().showKeyboard();
        findViewById(R.id.txt_visit_reason).requestFocus();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("clinic2", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("clinic2");
        pet_clinic2 = MySqlGson.getGson().fromJson(objectInString, Pet_clinic2.class);

        String petsForInputInString = DoctorVetApp.get().readFromDisk("pets_clinic_2_for_input");
        Pet_clinic2.Clinic2ForInput petsClinic2ForInput = MySqlGson.getGson().fromJson(petsForInputInString, Pet_clinic2.Clinic2ForInput.class);
        setSymptomsAdapter(petsClinic2ForInput.getSymptoms());
        setDiagnosticsAdapter(petsClinic2ForInput.getDiagnostics());
        setTreatments(petsClinic2ForInput.getTreatments());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //gets
        if (requestCode == HelperClass.REQUEST_GET && data != null) {
            String getFor = data.getExtras().getString("GET", "");
            String getMode = data.getStringExtra("GET_MODE");

            if (getFor.equals(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name())) {
                if (getMode.equalsIgnoreCase("CREATED")) {
                    showProgressBar();
                    DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
                        @Override
                        public void onSuccess(Object resultObject) {
                            hideProgressBar();
                            Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
                            setSymptomsAdapter(clinic2ForInput.getSymptoms());
                        }
                    });
                }

                Symptom symptom = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name()), Symptom.class);
                addSymptom(symptom);
            } else if (getFor.equals(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name())) {
                if (getMode.equalsIgnoreCase("CREATED")) {
                    showProgressBar();
                    DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
                        @Override
                        public void onSuccess(Object resultObject) {
                            hideProgressBar();
                            Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
                            setDiagnosticsAdapter(clinic2ForInput.getDiagnostics());
                        }
                    });
                }

                Diagnostic diagnostic = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name()), Diagnostic.class);
                getObject().setDiagnostic(diagnostic);
                txtDiagnostic.getEditText().setText(diagnostic.getName());
                txtTreatments.requestFocus();
            } else if (getFor.equals(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name())) {
                if (getMode.equalsIgnoreCase("CREATED")) {
                    showProgressBar();
                    DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
                        @Override
                        public void onSuccess(Object resultObject) {
                            hideProgressBar();
                            Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
                            setTreatments(clinic2ForInput.getTreatments());
                        }
                    });
                }

                Treatment treatment = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name()), Treatment.class);
                getObject().setTreatment(treatment);
                txtTreatments.getEditText().setText(treatment.getName());
            }
        }

        //busqueda
//        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
//            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
//            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name())) {
//                Symptom symptom = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name()), Symptom.class);
//                addSymptom(symptom);
//                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtSymptom);
//            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name())) {
//                Diagnostic diagnostic = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name()), Diagnostic.class);
//                getObject().setDiagnostic(diagnostic);
//                actvDiagnostics.setText(diagnostic.getName());
//                DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtTreatments);
//            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name())) {
//                Treatment treatment = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name()), Treatment.class);
//                getObject().setTreatment(treatment);
//                actvTreatments.setText(treatment.getName());
//            }
//        }
//
//        //symptoms creation
//        if (requestCode == HelperClass.REQUEST_CREATE && data != null) {
//            String createSDT = data.getExtras().getString(DoctorVetApp.REQUEST_CREATE_OBJECT, "");
//
//            if (createSDT.equalsIgnoreCase(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name())) {
//                showProgressBar();
//                DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
//                    @Override
//                    public void onSuccess(Object resultObject) {
//                        hideProgressBar();
//                        Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
//                        setSymptomsAdapter(clinic2ForInput.getSymptoms());
//                    }
//                });
//
//                Symptom symptom = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.SYMPTOM_OBJ.name()), Symptom.class);
//                addSymptom(symptom);
//            } else if (createSDT.equalsIgnoreCase(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name())) {
//                showProgressBar();
//                DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
//                    @Override
//                    public void onSuccess(Object resultObject) {
//                        hideProgressBar();
//                        Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
//                        setDiagnosticsAdapter(clinic2ForInput.getDiagnostics());
//                    }
//                });
//
//                Diagnostic diagnostic = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.DIAGNOSTIC_OBJ.name()), Diagnostic.class);
//                getObject().setDiagnostic(diagnostic);
//                txtDiagnostic.getEditText().setText(diagnostic.getName());
//                txtTreatments.requestFocus();
//            }  else if (createSDT.equalsIgnoreCase(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name())) {
//                showProgressBar();
//                DoctorVetApp.get().getClinic2ForInputObject(new DoctorVetApp.VolleyCallbackObject() {
//                    @Override
//                    public void onSuccess(Object resultObject) {
//                        hideProgressBar();
//                        Pet_clinic2.Clinic2ForInput clinic2ForInput = (Pet_clinic2.Clinic2ForInput) resultObject;
//                        setTreatments(clinic2ForInput.getTreatments());
//                    }
//                });
//
//                Treatment treatment = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.TREATMENT_OBJ.name()), Treatment.class);
//                getObject().setTreatment(treatment);
//                txtTreatments.getEditText().setText(treatment.getName());
//            }
//
////            DoctorVetApp.get().requestFocusAndShowKeyboard_noModal(txtHelperMethods);
//        }

    }

    @Override
    protected void save() {
        if (!validateDate() || !validateHour() || !validateAnamnesis() || !validateDiagnostic() || !validateTreatment() || !validateTemp() || !validateWeight())
            return;

        showWaitDialog();

        final Pet_clinic2 clinic2 = (Pet_clinic2) getObjectFromUI().clone();
        clinic2.setResources(resourcesAdapter.getResources());
        final String clinica_2_json_object = MySqlGson.postGson().toJson(clinic2);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_clinic2 clinica2_from_response = MySqlGson.getGson().fromJson(data, Pet_clinic2.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(clinic2.getResources(), clinica2_from_response.getResources());
                    
                    if (isUpdate()) {
                        Intent dataBackIntent = new Intent();
                        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.CLINIC2_OBJ.name(), MySqlGson.getGson().toJson(clinica2_from_response));
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
        }) {
            @Override
            public byte[] getBody() {
                return clinica_2_json_object.getBytes();
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
            return NetworkUtils.buildPetsClinic2Url(getObject().getId(), "RETURN_OBJECT");

        return NetworkUtils.buildPetsClinic2Url(null, "RETURN_OBJECT");
    }

    @Override
    protected Pet_clinic2 getObjectFromUI() {
        final Pet_clinic2 clinic2 = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), clinic2, true, "txt_", true, getApplicationContext());

        //fecha
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        clinic2.setDate(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        //polish
        Pet pet = clinic2.getPet();
        Pet pet_polish = new Pet(pet.getId(), pet.getName(), pet.getThumb_url());
        clinic2.setPet(pet_polish);

        return clinic2;
    }

    @Override
    protected Pet_clinic2 getObject() {
        if (pet_clinic2 == null) {
            if (isUpdate()) {
                pet_clinic2 = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.CLINIC2_OBJ.name()), Pet_clinic2.class);
            } else {
                pet_clinic2 = new Pet_clinic2();
                pet_clinic2.setDate(Calendar.getInstance().getTime());
            }

            pet_clinic2.setPet(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class));
        }

        return pet_clinic2;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_clinic2 clinic2 = (Pet_clinic2) object;
        DoctorVetApp.get().setThumb(clinic2.getPet().getThumb_url(), toolbar_image, R.drawable.ic_pets_dark);
        toolbar_title.setText(clinic2.getPet().getName());
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), clinic2, "txt_");

        //la hora no es un campo de la base de datos
        if (clinic2.getDate() != null)
            txtHour.getEditText().setText(HelperClass.getTimeInLocale(clinic2.getDate(), EditClinic2Activity.this));

        if (getObject().getDiagnostic() != null)
            actvDiagnostics.setText(getObject().getDiagnostic().getName());

        if (getObject().getTreatment() != null)
            actvTreatments.setText(getObject().getTreatment().getName());

    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return pet_clinic2;
    }

    @Override
    protected String getUploadTableName() {
        return "pets_clinic_2";
    }

    @Override
    protected void record_video() {
        DoctorVetApp.get().dispathRecordVideoIntent(EditClinic2Activity.this);
    }

    @Override
    protected void record_audio() {
        if (mStartRecording) {
            last_AudioFilePath = HelperClass.createAudioFile(this);
            label_btn_add_audio.setText("Stop");
            btn_add_audio.setBackgroundResource(R.drawable.shape_circle_red);
        } else {
            label_btn_add_audio.setText("Start");
            btn_add_audio.setBackgroundResource(R.drawable.shape_circle);
        }
        onRecord(mStartRecording, last_AudioFilePath);
        mStartRecording = !mStartRecording;
    }

    private void setSymptomsAdapter(ArrayList<Symptom> symptoms) {
        SymptomsAdapter symptomsAdapter = new SymptomsAdapter(symptoms, DoctorVetApp.AdapterSelectTypes.NORMAL);
        actvSymptoms.setAdapter(symptomsAdapter.getArrayAdapter(EditClinic2Activity.this));
        actvSymptoms.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addSymptom((Symptom) adapterView.getItemAtPosition(i));
                actvSymptoms.setText("");
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvSymptoms);
        DoctorVetApp.get().setAllWidthToDropDown(actvSymptoms, EditClinic2Activity.this);
    }
    private void setDiagnosticsAdapter(ArrayList<Diagnostic> diagnostics) {
        DiagnosticsAdapter diagnosticAdapter = new DiagnosticsAdapter(diagnostics);
        actvDiagnostics.setAdapter(diagnosticAdapter.getArrayAdapter(EditClinic2Activity.this));
        actvDiagnostics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setDiagnostic((Diagnostic) adapterView.getItemAtPosition(i));
                txtTreatments.requestFocus();
            }
        });
        actvDiagnostics.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setDiagnostic(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvDiagnostics);
        DoctorVetApp.get().setAllWidthToDropDown(actvDiagnostics, EditClinic2Activity.this);
    }
    private void setTreatments(ArrayList<Treatment> treatments) {
        TreatmentsAdapter treatmentAdapter = new TreatmentsAdapter(treatments);
        actvTreatments.setAdapter(treatmentAdapter.getArrayAdapter(EditClinic2Activity.this));
        actvTreatments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setTreatment((Treatment) adapterView.getItemAtPosition(i));
            }
        });
        actvTreatments.addTextChangedListener(new TextWatcher() {
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
        DoctorVetApp.get().setOnTouchToShowDropDown(actvTreatments);
        DoctorVetApp.get().setAllWidthToDropDown(actvTreatments, EditClinic2Activity.this);
    }

    private void addSymptom(Symptom symptom) {
        selectedSymptomsAdapter.addSymptom(symptom);
        DoctorVetApp.get().getPossibleDiagnosticsAdapter(selectedSymptomsAdapter.getArrayList(), new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                if (resultAdapter != null) {
                    DiagnosticsAdapter possibleDiagnostics = (DiagnosticsAdapter) resultAdapter;
                    String possibleDiagnosticsStr = android.text.TextUtils.join(", ", possibleDiagnostics.getArrayList());
                    txtPossibleDiagnostics.setText(possibleDiagnosticsStr);
                }
            }
        });
    }
    private void removeSymptom(Symptom symptom) {
        selectedSymptomsAdapter.remove(symptom);

        DoctorVetApp.get().getPossibleDiagnosticsAdapter(selectedSymptomsAdapter.getArrayList(), new DoctorVetApp.VolleyCallbackAdapter() {
            @Override
            public void onSuccess(RecyclerView.Adapter resultAdapter) {
                if (resultAdapter != null) {
                    DiagnosticsAdapter possibleDiagnostics = (DiagnosticsAdapter) resultAdapter;
                    txtPossibleDiagnostics.setText("");
                    if (possibleDiagnostics.getArrayList() != null) {
                        String possibleDiagnosticsStr = android.text.TextUtils.join(", ", possibleDiagnostics.getArrayList());
                        txtPossibleDiagnostics.setText(possibleDiagnosticsStr);
                    }
                }
            }
        });
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
    private boolean validateAnamnesis() {
        return HelperClass.validateEmpty(txtAnamnesis);
    }
    private boolean validateDiagnostic() {
        return DoctorVetApp.get().validateExistence(txtDiagnostic, getObject().getDiagnostic(), "name", true);
    }
    private boolean validateTreatment() {
        return DoctorVetApp.get().validateExistence(txtTreatments, getObject().getTreatment(), "name", true);
    }
    private boolean validateTemp() {
        return HelperClass.validateNumber(txtTemp, true);
    }
    private boolean validateWeight() {
        return HelperClass.validateNumber(txtWeight, true);
    }

}
