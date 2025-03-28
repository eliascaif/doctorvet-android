package com.xionce.doctorvetServices;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.ProductsAdapter;
import com.xionce.doctorvetServices.data.Service_schedule;
import com.xionce.doctorvetServices.data.ServicesSchedulesAdapter;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditVetScheduleActivity extends EditBaseActivity {

    private static final String TAG = "EditServiceScheduleActi";
    public enum weekdays {
        /*NONE("Ninguno"),*/
        SUNDAY("Domingo"),
        MONDAY("Lunes"),
        TUESDAY("Martes"),
        WEDNESDAY("Miércoles"),
        THURSDAY("Jueves"),
        FRIDAY("Viernes"),
        SATURDAY("Sábado");

        private final String friendlyName;

        weekdays(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public static weekdays getEnumVal(String friendlyName) {
            if (friendlyName.equals("Domingo")) {
                return SUNDAY;
            } else if (friendlyName.equals("Lunes")) {
                return MONDAY;
            } else if (friendlyName.equals("Martes")) {
                return TUESDAY;
            } else if (friendlyName.equals("Miércoles")) {
                return WEDNESDAY;
            } else if (friendlyName.equals("Jueves")) {
                return THURSDAY;
            } else if (friendlyName.equals("Viernes")) {
                return FRIDAY;
            } else if (friendlyName.equals("Sábado")) {
                return SATURDAY;
            }
//            else if (friendlyName.equals("Ninguno")) {
//                return NONE;
//            }

            return SUNDAY;
        }
    }
    private AppCompatSpinner spinnerDays;
    private AutoCompleteTextView actvService;
    private TextInputLayout txtService;
    private TextInputLayout txtStartingHour;
    private TextInputLayout txtEndingHour;
    private Button btn_add;
    private View list_item_selected_user;

    private Service_schedule serviceSchedule = null;
    private ArrayList<Service_schedule> servicesSchedules = new ArrayList<>();
    private ServicesSchedulesAdapter selectedServicesAdapter = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_edit_vet_schedule);
        toolbar_title.setText("Nuevo horario");
        toolbar_subtitle.setText("Completa los datos");
        spinnerDays = findViewById(R.id.spinner_days);
        txtService = findViewById(R.id.txt_service);
        actvService = findViewById(R.id.actv_service);
        txtStartingHour = findViewById(R.id.txt_starting_hour);
        txtEndingHour = findViewById(R.id.txt_ending_hour);
        list_item_selected_user = findViewById(R.id.list_item_selected_user);
        btn_add = findViewById(R.id.btn_add);
        hideToolbarImage();
        hideFab();
        
        initializeInitRequestNumber(1);
        DoctorVetApp.get().getVetServicesAssoc(new DoctorVetApp.VolleyCallbackArrayList() {
            @Override
            public void onSuccess(ArrayList resultArrayList) {
                setRequestCompleted();
                setServicesAdapter(resultArrayList);
            }
        });
        
        if (savedInstanceState != null)
            restoreFromBundle(savedInstanceState);

        Service_schedule serviceSchedule = getObject();
        setObjectToUI(serviceSchedule);
        
        //setup recyclerview
        RecyclerView recyclerServiceSchedules = findViewById(R.id.recycler_services_schedules);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(EditVetScheduleActivity.this);
        recyclerServiceSchedules.setLayoutManager(layoutManager);
        recyclerServiceSchedules.setHasFixedSize(true);
        selectedServicesAdapter = new ServicesSchedulesAdapter(servicesSchedules);
        recyclerServiceSchedules.setAdapter(selectedServicesAdapter);
        selectedServicesAdapter.setOnRemoveClickHandler(new HelperClass.AdapterOnClickHandler() {
            @Override
            public void onClick(Object data, View view, int pos) {
                selectedServicesAdapter.removeItem((Service_schedule) data);
            }
        });

        ArrayAdapter<weekdays> weekdaysArrayAdapter = new ArrayAdapter<>(EditVetScheduleActivity.this, android.R.layout.simple_spinner_dropdown_item, weekdays.values());
        spinnerDays = findViewById(R.id.spinner_days);
        weekdaysArrayAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinnerDays.setAdapter(weekdaysArrayAdapter);

        ImageView serviceSearch = findViewById(R.id.img_search_service);
        serviceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditVetScheduleActivity.this, SearchServiceActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });

        ImageView startingTimeSearch = findViewById(R.id.img_search_starting_hour);
        startingTimeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStartingTime();
            }
        });
        txtStartingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStartingTime();
            }
        });
        txtStartingHour.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchStartingTime();
            }
        });

        ImageView endingTimeSearch = findViewById(R.id.img_search_ending_hour);
        endingTimeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEndingTime();
            }
        });
        txtEndingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEndingTime();
            }
        });
        txtEndingHour.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEndingTime();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert_schedule();
            }
        });

        list_item_selected_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditVetScheduleActivity.this, SearchUserActivity.class);
                intent.putExtra(DoctorVetApp.REQUEST_SEARCH_FOR, DoctorVetApp.INTENT_VALUES.USER_OBJ.name());
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_RETURN, true);
                startActivityForResult(intent, HelperClass.REQUEST_SEARCH);
            }
        });
        list_item_selected_user.findViewById(R.id.img_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeUserInView();
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("schedule", MySqlGson.getGson().toJson(getObjectFromUI()));
        outState.putString("schedules", MySqlGson.getGson().toJson(servicesSchedules));
    }

    @Override
    protected void restoreFromBundle(Bundle savedInstanceState) {
        String objectInString = savedInstanceState.getString("schedule");
        serviceSchedule = MySqlGson.getGson().fromJson(objectInString, Service_schedule.class);

        String petSuppliesInString = savedInstanceState.getString("schedules");
        servicesSchedules = MySqlGson.getGson().fromJson(petSuppliesInString, new TypeToken<List<Service_schedule>>(){}.getType());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        //busquedas
        if (requestCode == HelperClass.REQUEST_SEARCH && data != null) {
            String searchFor = data.getExtras().getString(DoctorVetApp.REQUEST_SEARCH_FOR, "");
            if (searchFor.equals(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name())) {
                Product product = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.PRODUCT_OBJ.name()), Product.class);
                getObject().setService(product);
                actvService.setText(product.getName());
                actvService.clearFocus();
            } else if (searchFor.equals(DoctorVetApp.INTENT_VALUES.USER_OBJ.name())) {
                User user = MySqlGson.getGson().fromJson(data.getStringExtra(DoctorVetApp.INTENT_VALUES.USER_OBJ.name()), User.class);
                getObject().setUser(user);
                setUserInView(user);
            }
        }
    }

    @Override
    protected void save() {
        if (!validateSomethingToRegister())
            return;

        showWaitDialog();

        final String jsonObject = MySqlGson.postGson().toJson(selectedServicesAdapter.getArrayList());
        Log.d(TAG, jsonObject);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, getUrl().toString(), new Response.Listener<String>() {
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
        save();
    }

    @Override
    protected URL getUrl() {
        return NetworkUtils.buildServicesSchedulesVetUrl("create_schedule");
    }

    @Override
    protected Service_schedule getObjectFromUI() {
        Service_schedule serviceSchedule = getObject();
        DoctorVetApp.TextInputLayoutToObject(findViewById(R.id.lista), serviceSchedule, true, "txt_", false, getApplicationContext());
        return serviceSchedule;
    }

    @Override
    protected Service_schedule getObject() {
        if (serviceSchedule == null)
            serviceSchedule = new Service_schedule();

        return serviceSchedule;
    }

    @Override
    protected void setObjectToUI(Object object) {
        DoctorVetApp.ObjectToTextInputLayout(findViewById(R.id.lista), serviceSchedule, "txt_");
    }

    @Override
    protected DoctorVetApp.IResourceObject getResourceObject() {
        return null;
    }

    @Override
    protected String getUploadTableName() {
        return null;
    }

    private void setServicesAdapter(ArrayList<Product> services) {
        btn_add.setEnabled(true);
        ProductsAdapter servicesAdapter = new ProductsAdapter(services, ProductsAdapter.Products_types.NORMAL);
        actvService.setAdapter(servicesAdapter.getAutocompleteAdapter(EditVetScheduleActivity.this));
        actvService.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                getObject().setService((Product) adapterView.getItemAtPosition(i));
                DoctorVetApp.get().closeKeyboard();
            }
        });
        actvService.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().isEmpty())
                    getObject().setService(null);
            }
        });
        DoctorVetApp.get().setOnTouchToShowDropDown(actvService);
        DoctorVetApp.get().setAllWidthToDropDown(actvService, EditVetScheduleActivity.this);
    }

    private void insert_schedule() {
        if (!validateStartingHour() || !validateEndingHour())
            return;

        if (!actvService.getText().toString().isEmpty() && !validateService())
            return;

        if (getObject().getService() != null && getObject().getUser() == null) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Establece servicio y usuario", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (getObject().getUser() != null && getObject().getService() == null) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Establece servicio y usuario", Snackbar.LENGTH_SHORT).show();
            return;
        }

        Service_schedule serviceSchedule = (Service_schedule) getObjectFromUI().clone();

        Date startingHourDate = HelperClass.getShortTime(txtStartingHour.getEditText().getText().toString(), EditVetScheduleActivity.this);
        Date endingHourDate = HelperClass.getShortTime(txtEndingHour.getEditText().getText().toString(), EditVetScheduleActivity.this);
        if (endingHourDate.before(startingHourDate)) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), "Hora fin debe ser mayor a hora inicio", Snackbar.LENGTH_LONG).show();
            return;
        }

        String startingHourStr = HelperClass.getTimeInLocale(startingHourDate, EditVetScheduleActivity.this);
        serviceSchedule.setStarting_hour_local(startingHourStr);
        serviceSchedule.setStarting_hour(HelperClass.getTimeForMySQL(startingHourDate));
        String endingHourStr = HelperClass.getTimeInLocale(endingHourDate, EditVetScheduleActivity.this);
        serviceSchedule.setEnding_hour_local(endingHourStr);
        serviceSchedule.setEnding_hour(HelperClass.getTimeForMySQL(endingHourDate));

        serviceSchedule.setWeekday(weekdays.getEnumVal(spinnerDays.getSelectedItem().toString()));

        selectedServicesAdapter.addItem(serviceSchedule);
    }

    private void searchStartingTime() {
        DoctorVetApp.get().searchTimeSetInit(txtStartingHour, getSupportFragmentManager());
    }
    private void searchEndingTime() {
        DoctorVetApp.get().searchTimeSetInit(txtEndingHour, getSupportFragmentManager());
    }

    private void setUserInView(User user) {
        ImageView img_thumb = list_item_selected_user.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_user.findViewById(R.id.txt_item_name);
        DoctorVetApp.get().setThumb(user.getThumb_url(), img_thumb, R.drawable.ic_account_circle_light);
        txt_name.setText(user.getName());
    }
    private void removeUserInView() {
        //getObject().setTable_id(null);
        getObject().setUser(null);
        ImageView img_thumb = list_item_selected_user.findViewById(R.id.img_thumb);
        TextView txt_name = list_item_selected_user.findViewById(R.id.txt_item_name);
        Context ctx = img_thumb.getContext();
        Glide.with(ctx).load(R.drawable.ic_account_circle_light).apply(RequestOptions.fitCenterTransform()).into(img_thumb);
        txt_name.setText("SELECCIONAR");
    }

    private boolean validateService() {
        return DoctorVetApp.get().validateExistence(txtService, getObject().getService(), "name", false);
    }
    private boolean validateStartingHour() {
        return HelperClass.validateHour(txtStartingHour, false);
    }
    private boolean validateEndingHour() {
        return HelperClass.validateHour(txtEndingHour, false);
    }
    private boolean validateSomethingToRegister() {
        if (selectedServicesAdapter.getItemCount() == 0) {
            Snackbar.make(DoctorVetApp.getRootForSnack(this), R.string.nada_para_registrar, Snackbar.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

}