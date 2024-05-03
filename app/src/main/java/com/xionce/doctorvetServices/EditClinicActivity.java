package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_RECORD_AUDIO;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_IMAGE;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_THUMB;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_VIDEO;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_clinic;
import com.xionce.doctorvetServices.data.ResourcesAdapter;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.Calendar;

public class EditClinicActivity extends EditBaseActivity {

    private static final String TAG = "EditClinicActivity";
    private TextInputLayout txtDate;
    private TextInputLayout txtHour;
    private TextInputLayout txtTemp;
    private TextInputLayout txtWeight;
    private TextInputLayout txtDescription;
    private TextView label_btn_add_audio;
    private ImageView btn_add_audio;

    private Pet_clinic pet_clinic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_clinic);
        txtDate = findViewById(R.id.txt_date);
        txtHour = findViewById(R.id.txt_hour);
        txtTemp = findViewById(R.id.txt_temp);
        txtTemp.setHint(getString(R.string.temperatura_en_default, DoctorVetApp.get().getVet().getTempUnit()));
        txtWeight = findViewById(R.id.txt_weight);
        txtWeight.setHint(getString(R.string.peso_en_default, DoctorVetApp.get().getVet().getWeightUnit()));
        txtDescription = findViewById(R.id.txt_description);
        btn_add_audio = findViewById(R.id.btn_add_audio);
        label_btn_add_audio = findViewById(R.id.label_btn_add_audio);
        DoctorVetApp.get().markRequired(txtDescription);

        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        if (isUpdate()) {
            toolbar_subtitle.setText(R.string.editando_clinica);
        } else {
            toolbar_subtitle.setText(R.string.nueva_clinica);
        }

        Pet_clinic clinic = getObject();
        setObjectToUI(clinic);

        //resources
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(EditClinicActivity.this, 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        resourcesAdapter = new ResourcesAdapter(getObject().getResources(), true, EditClinicActivity.this);
        recyclerView.setAdapter(resourcesAdapter);

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

        ImageButton btn_add_imagen = findViewById(R.id.btn_add_image);
        btn_add_imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context wrapper = new ContextThemeWrapper(EditClinicActivity.this, R.style.PopUpMenu);
                PopupMenu popupMenu = new PopupMenu(wrapper, v);
                popupMenu.getMenuInflater().inflate(R.menu.menu_take_image, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.action_take_photo) {
                            if (HelperClass.checkPermission(Manifest.permission.CAMERA, getApplicationContext())) {
                                DoctorVetApp.get().dispatchTakePictureIntent(EditClinicActivity.this);
                            } else {
                                HelperClass.requestPermission(EditClinicActivity.this, Manifest.permission.CAMERA, REQUEST_TAKE_IMAGE);
                            }
                        } else if (item.getItemId() == R.id.action_open_gallery) {
                            DoctorVetApp.get().dispatchOpenGalleryIntent(EditClinicActivity.this);
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
                    HelperClass.requestPermission(EditClinicActivity.this, Manifest.permission.CAMERA, REQUEST_TAKE_VIDEO);
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
                    HelperClass.requestPermission(EditClinicActivity.this, Manifest.permission.RECORD_AUDIO, REQUEST_RECORD_AUDIO);
                }
            }
        });

        ImageButton btn_add_archivo = findViewById(R.id.btn_add_archivo);
        btn_add_archivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DoctorVetApp.get().dispatchOpenFileIntent(EditClinicActivity.this);
            }
        });

        txtDescription.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    save();
                    return true;
                }
                return false;
            }
        });

        if (!isUpdate())
            txtDescription.requestFocus();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("clinic", MySqlGson.getGson().toJson(getObjectFromUI()));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("clinic");
        pet_clinic = MySqlGson.getGson().fromJson(objectInString, Pet_clinic.class);
    }

    @Override
    protected void save() {
        if (!validateDate() || !validateHour() || !validateDescripcion() || !validateTemp() || !validateWeight())
            return;

        showWaitDialog();

        final Pet_clinic clinica = (Pet_clinic) getObjectFromUI().clone();
        clinica.setResources(resourcesAdapter.getResources());
        final String clinica_json_object = MySqlGson.postGson().toJson(clinica);
        TokenStringRequest stringRequest = new TokenStringRequest(getMethod(), getUrl().toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_clinic clinica_from_response = MySqlGson.getGson().fromJson(data, Pet_clinic.class);
                    DoctorVetApp.get().linkTempAndFinalFiles(clinica.getResources(), clinica_from_response.getResources());

                    if (isUpdate()) {
                        Intent dataBackIntent = new Intent();
                        dataBackIntent.putExtra(DoctorVetApp.INTENT_VALUES.CLINIC_OBJ.name(), MySqlGson.getGson().toJson(clinica_from_response));
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
                return clinica_json_object.getBytes();
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
            return NetworkUtils.buildPetsClinicUrl(getObject().getId(), "RETURN_OBJECT");
        
        return NetworkUtils.buildPetsClinicUrl(null, "RETURN_OBJECT");
    }

    @Override
    protected Pet_clinic getObjectFromUI() {
        final Pet_clinic clinic = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), clinic, true, "txt_", true, getApplicationContext());

        //fecha
        String fecha_hora = txtDate.getEditText().getText().toString() + " " + txtHour.getEditText().getText().toString();
        clinic.setDate(HelperClass.getShortDateTime(fecha_hora, getBaseContext()));

        //polish
        Pet pet = clinic.getPet();
        Pet pet_polish = new Pet(pet.getId(), pet.getName(), pet.getThumb_url());
        clinic.setPet(pet_polish);

        return clinic;
    }

    @Override
    protected Pet_clinic getObject() {
        if (pet_clinic == null) {
            if (isUpdate()) {
                pet_clinic = MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.CLINIC_OBJ.name()), Pet_clinic.class);
            } else {
                pet_clinic = new Pet_clinic();
                pet_clinic.setDate(Calendar.getInstance().getTime());
            }

            pet_clinic.setPet(MySqlGson.getGson().fromJson(getIntent().getStringExtra(DoctorVetApp.INTENT_VALUES.PET_OBJ.name()), Pet.class));
        }

        return pet_clinic;
    }

    @Override
    protected void setObjectToUI(Object object) {
        Pet_clinic clinic = (Pet_clinic) object;
        toolbar_title.setText(clinic.getPet().getName());
        DoctorVetApp.get().setThumb(clinic.getPet().getThumb_url(), toolbar_image, R.drawable.ic_dog_holo_dark);
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), clinic, "txt_");

        //la hora no es un campo de la base de datos
        if (clinic.getDate() != null)
            txtHour.getEditText().setText(HelperClass.getTimeInLocale(clinic.getDate(), EditClinicActivity.this));
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return pet_clinic;
    }

    @Override
    protected String getUploadTableName() {
        return "pets_clinic";
    }

    @Override
    protected void record_video() {
        DoctorVetApp.get().dispathRecordVideoIntent(EditClinicActivity.this);
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
    private boolean validateTemp() {
        return HelperClass.validateNumber(txtTemp, true);
    }
    private boolean validateWeight() {
        return HelperClass.validateNumber(txtWeight, true);
    }
    private boolean validateDescripcion() {
        return HelperClass.validateEmpty(txtDescription);
    }

}
