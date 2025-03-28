package com.xionce.doctorvetServices;

import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_OPEN_GALLERY;
import static com.xionce.doctorvetServices.utilities.HelperClass.REQUEST_TAKE_VIDEO;
import static com.xionce.doctorvetServices.utilities.HelperClass.handleScrollView;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.text.InputFilter;
import android.text.InputType;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.xionce.doctorvetServices.data.Agenda;
import com.xionce.doctorvetServices.data.DailyCashDetails;
import com.xionce.doctorvetServices.data.Daily_cash;
import com.xionce.doctorvetServices.data.Dashboard;
import com.xionce.doctorvetServices.data.Diagnostic;
import com.xionce.doctorvetServices.data.DiagnosticsAdapter;
import com.xionce.doctorvetServices.data.FinanceTypesFiscalAdapter;
import com.xionce.doctorvetServices.data.FinanceTypesReceiptsAdapter;
import com.xionce.doctorvetServices.data.Finance_payment_method;
import com.xionce.doctorvetServices.data.Finance_types_fiscal;
import com.xionce.doctorvetServices.data.Finance_types_receipts;
import com.xionce.doctorvetServices.data.Get_pagination;
import com.xionce.doctorvetServices.data.Cash_movement;
import com.xionce.doctorvetServices.data.Movement;
import com.xionce.doctorvetServices.data.Owner;
import com.xionce.doctorvetServices.data.OwnerNotification;
import com.xionce.doctorvetServices.data.Pet;
import com.xionce.doctorvetServices.data.Pet_character;
import com.xionce.doctorvetServices.data.Pet_charactersAdapter;
import com.xionce.doctorvetServices.data.Pet_clinic2;
import com.xionce.doctorvetServices.data.Pet_clinic_root;
import com.xionce.doctorvetServices.data.Pet_pelage;
import com.xionce.doctorvetServices.data.Pet_gender;
import com.xionce.doctorvetServices.data.Pet_recipe;
import com.xionce.doctorvetServices.data.Pet_study_item;
import com.xionce.doctorvetServices.data.Pet_supply;
import com.xionce.doctorvetServices.data.Product;
import com.xionce.doctorvetServices.data.Product_category;
import com.xionce.doctorvetServices.data.Product_manufacturer;
import com.xionce.doctorvetServices.data.Product_provider;
import com.xionce.doctorvetServices.data.Product_unit;
import com.xionce.doctorvetServices.data.Pet_race;
import com.xionce.doctorvetServices.data.Purchase;
import com.xionce.doctorvetServices.data.PurchasesAdapter;
import com.xionce.doctorvetServices.data.Region;
import com.xionce.doctorvetServices.data.Pet_gendersAdapter;
import com.xionce.doctorvetServices.data.Resource;
import com.xionce.doctorvetServices.data.Sell;
import com.xionce.doctorvetServices.data.Sell_item;
import com.xionce.doctorvetServices.data.SellsAdapter;
import com.xionce.doctorvetServices.data.Service_schedule;
import com.xionce.doctorvetServices.data.ServicesSchedulesAdapter;
import com.xionce.doctorvetServices.data.Spending;
import com.xionce.doctorvetServices.data.Symptom;
import com.xionce.doctorvetServices.data.Treatment;
import com.xionce.doctorvetServices.data.Update_table;
import com.xionce.doctorvetServices.data.User;
import com.xionce.doctorvetServices.data.Users_permissions;
import com.xionce.doctorvetServices.data.Vet_point;
import com.xionce.doctorvetServices.data.Vet;
import com.xionce.doctorvetServices.data.Vet_deposit;
import com.xionce.doctorvetServices.data.Vet_structure;
import com.xionce.doctorvetServices.data.VetsDepositsAdapter;
import com.xionce.doctorvetServices.data.VetPointsAdapter;
import com.xionce.doctorvetServices.data.VetsStructuresAdapter;
import com.xionce.doctorvetServices.data.Waiting_room;
import com.xionce.doctorvetServices.utilities.CryptoManager;
import com.xionce.doctorvetServices.utilities.DatePickerFragment;
import com.xionce.doctorvetServices.utilities.HelperClass;
import com.xionce.doctorvetServices.utilities.MySqlGson;
import com.xionce.doctorvetServices.utilities.NetworkUtils;
import com.xionce.doctorvetServices.utilities.TimePickerFragment;
import com.xionce.doctorvetServices.utilities.TokenStringRequest;
import com.xionce.doctorvetServices.utilities.VolleyMultipartRequest;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Elias on 4/8/2017.
 */
public class DoctorVetApp extends Application {

    private static final String TAG = "DoctorVetApp";
    public static final boolean SHOW_ERROR_MESSAGE = true;

    private static DoctorVetApp singleton;
    public static synchronized DoctorVetApp get() {
        return singleton;
    }

    public SharedPreferences sharedPreferences;
    public static int views_abiertos = 0; //remove

    public enum login_types {EMAIL, GOOGLE, FACEBOOK}
    public enum login_response {
        INVALID_ACCOUNT_PASSWORD,
        ACCOUNT_WAITING_FOR_EMAIL_CHECK,
        VALID,
        ACCOUNT_WAITING_FOR_VET_ASSOCIATION,
        ACCOUNT_EXISTS
    }
    public enum unit_system {METRIC, ENGLISH}
    public enum hour_format {F24_HS, AM_PM}
    public enum get_format {EXTENDED, SEARCH, MIN}
    public enum Adapter_types {EXTENDED, COMPACT, SEARCH, OWNERS_DEBTORS, PROVIDERS_CREDITORS, NORMAL, REMOVE}
    public enum reports {
        SELECT(get().getApplicationContext().getString(R.string.reports_select)),

        CREDITORS(get().getApplicationContext().getString(R.string.reports_providers_creditors)),
        CREDITORS_TOLERANCE(get().getApplicationContext().getString(R.string.reports_providers_creditors_tolerance)),

        AGENDA("Agenda"),
        TOMORROW_AGENDA_VET("Agenda de mañana"),
        TOMORROW_AGENDA_USER("Agenda de mañana (usr)"),
        PENDING_AGENDA_VET(get().getApplicationContext().getString(R.string.reports_pending_appointments_tasks_vet)),
        PENDING_AGENDA_USER(get().getApplicationContext().getString(R.string.reports_pending_appointments_tasks_user)),
        EXPIRED_AGENDA_VET(get().getApplicationContext().getString(R.string.reports_expired_appointments_tasks_vet)),
        EXPIRED_AGENDA_USER(get().getApplicationContext().getString(R.string.reports_expired_appointments_tasks_user)),
        EXPIRED_AGENDA_ALL_VET(get().getApplicationContext().getString(R.string.reports_expired_appointments_tasks_all_vet)),

        LOW_FOOD("Alimento / Pienso bajo"),

        PURCHASES("Compras"),

        BIRTHDAY_PETS(get().getApplicationContext().getString(R.string.reports_birthdays)),

        DEBTORS(get().getApplicationContext().getString(R.string.reports_owners_debtors)),
        DEBTORS_TOLERANCE(get().getApplicationContext().getString(R.string.reports_owners_debtors_tolerance)),

        LIFE_EXPECTANCY("Espectativa de vida superada"),

        SPENDINGS("Gastos"),

        LOGS("Logs"),

        CASH_MOVEMENTS("Movimientos manuales"),

        OWNERS_NOTIFICATIONS(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_owners_notifications)),

        NEW_VET_USERS("Nuevos usuarios"),

        PRODUCTS("Productos"),
//        PRODUCTS_EXCEL("Productos Excel"),
        PRODUCTS_BELOW_MINIMUN("Productos por debajo del mínimo"),
        PRODUCTS_TRACEABILITY("Productos trazabilidad"),

        MOVEMENTS("Remitos"),
        IN_TRANSIT_MOVEMENTS("Remitos en tránsito"),

        WAITING_ROOM(DoctorVetApp.get().getApplicationContext().getString(R.string.waiting_room)),
        WAITING_ROOM_AUTO_DELETED(DoctorVetApp.get().getApplicationContext().getString(R.string.waiting_room_auto_deleted)),

//        SERVICES_BARCODES("Servicios códigos de barras"),

        USER_JOIN_REQUEST("Solicitudes de unión"),

        SUPPLY("Suministro"),
        TOMORROW_SUPPLY_VET("Suministro de mañana"),
        TOMORROW_SUPPLY_USER("Suministro de mañana (usr)"),
        DOMICILIARY_PENDING_SUPPLY_VET(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_domiciliary_pending_supply_vet)),
        DOMICILIARY_PENDING_SUPPLY_USER(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_domiciliary_pending_supply_user)),
        DOMICILIARY_EXPIRED_SUPPLY_VET(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_domiciliary_expired_supply_vet)),
        PENDING_SUPPLY_VET(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_pending_supply_vet)),
        PENDING_SUPPLY_USER(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_pending_supply_user)),
        EXPIRED_SUPPLY_VET(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_supply_vet)),
        EXPIRED_SUPPLY_USER(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_supply_user)),
        EXPIRED_SUPPLY_ALL_VET(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_supply_all_vet)),

        SELLS("Ventas"),
        SELLS_BY_OWNER("Ventas por " + DoctorVetApp.get().getOwnerNaming().toLowerCase()),
        SELLS_BY_PRODUCT("Ventas por producto");

        private final String friendlyName;

        private reports(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public static reports getEnumVal(String friendlyName) {
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_pending_appointments_tasks_vet))) return PENDING_AGENDA_VET;
            if (friendlyName.equals("Agenda de mañana")) return TOMORROW_AGENDA_VET;
            if (friendlyName.equals("Agenda de mañana (usr)")) return TOMORROW_AGENDA_USER;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_pending_appointments_tasks_user))) return PENDING_AGENDA_USER;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_appointments_tasks_vet))) return EXPIRED_AGENDA_VET;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_appointments_tasks_user))) return EXPIRED_AGENDA_USER;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_appointments_tasks_all_vet))) return EXPIRED_AGENDA_ALL_VET;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_birthdays))) return BIRTHDAY_PETS;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_pending_supply_vet))) return PENDING_SUPPLY_VET;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_pending_supply_user))) return PENDING_SUPPLY_USER;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_supply_vet))) return EXPIRED_SUPPLY_VET;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_supply_user))) return EXPIRED_SUPPLY_USER;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_domiciliary_pending_supply_vet))) return DOMICILIARY_PENDING_SUPPLY_VET;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_expired_supply_all_vet))) return EXPIRED_SUPPLY_ALL_VET;
            if (friendlyName.equals("Suministro de mañana")) return TOMORROW_SUPPLY_VET;
            if (friendlyName.equals("Suministro de mañana (usr)")) return TOMORROW_SUPPLY_USER;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_owners_notifications))) return OWNERS_NOTIFICATIONS;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_owners_debtors))) return DEBTORS;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_owners_debtors_tolerance))) return DEBTORS_TOLERANCE;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_providers_creditors))) return CREDITORS;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.reports_providers_creditors_tolerance))) return CREDITORS_TOLERANCE;
            if (friendlyName.equals("Remitos en tránsito")) return IN_TRANSIT_MOVEMENTS;
            if (friendlyName.equals("Productos por debajo del mínimo")) return PRODUCTS_BELOW_MINIMUN;
            if (friendlyName.equals("Espectativa de vida superada")) return LIFE_EXPECTANCY;
            if (friendlyName.equals("Nuevos usuarios")) return NEW_VET_USERS;
            if (friendlyName.equals("Solicitudes de unión")) return USER_JOIN_REQUEST;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.waiting_room))) return WAITING_ROOM;
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.waiting_room_auto_deleted))) return WAITING_ROOM_AUTO_DELETED;
            if (friendlyName.equals("Ventas")) return SELLS;
            if (friendlyName.equals("Ventas por producto")) return SELLS_BY_PRODUCT;
            if (friendlyName.equals("Ventas por " + DoctorVetApp.get().getOwnerNaming().toLowerCase())) return SELLS_BY_OWNER;
            if (friendlyName.equals("Compras")) return PURCHASES;
            if (friendlyName.equals("Gastos")) return SPENDINGS;
            if (friendlyName.equals("Movimientos manuales")) return CASH_MOVEMENTS;
            if (friendlyName.equals("Remitos")) return MOVEMENTS;
            if (friendlyName.equals("Suministro")) return SUPPLY;
            if (friendlyName.equals("Agenda")) return AGENDA;
            if (friendlyName.equals("Productos trazabilidad")) return PRODUCTS_TRACEABILITY;
            if (friendlyName.equals("Productos")) return PRODUCTS;
            if (friendlyName.equals("Logs")) return LOGS;
            if (friendlyName.equals("Alimento / Pienso bajo")) return LOW_FOOD;

            return SELECT;
        }
    }
    public enum products_prices {
        EMPTY("Ninguno"),
        FIXED(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_fijo)),
        COST_MARGIN(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_costo_margen)),
        COST_MARGIN_TAX(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_costo_margen_impuesto)),
        COST_DIV_MARGIN_TAX(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_costo_div_margen_impuesto));

        private final String friendlyName;

        private products_prices(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public static products_prices getEnumVal(String friendlyName) {
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_fijo))) {
                return FIXED;
            } else if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_costo_margen))) {
                return COST_MARGIN;
            } else if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_costo_margen_impuesto))) {
                return COST_MARGIN_TAX;
            } else if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.precio_costo_div_margen_impuesto))) {
                return COST_DIV_MARGIN_TAX;
            } else if (friendlyName.equals("Ninguno")) {
                return null;
            }
            return null;
        }
    }
    public enum products_columns_xlsx {
        NA, A, B, C, D, E, F, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, V, W, X, Y, Z
    }
    public enum Sdt_type { SYMPTOM, DIAGNOSTIC, TREATMENT }
    public enum AdapterSelectTypes { NORMAL, REMOVE }
    public enum Vet_points_types {
        PAPER(DoctorVetApp.get().getApplicationContext().getString(R.string.paper)),
        ELECTRONIC(DoctorVetApp.get().getApplicationContext().getString(R.string.electronic));

        private final String friendlyName;

        private Vet_points_types(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public static Vet_points_types getEnumVal(String friendlyName) {
            if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.paper))) {
                return PAPER;
            } else if (friendlyName.equals(DoctorVetApp.get().getApplicationContext().getString(R.string.electronic))) {
                return ELECTRONIC;
            }

            return null;
        }
    }
    public enum Owners_naming {
        CLIENT("Cliente"),
        LANDLORD("Dueño"),
        OWNER("Propietario"),
        TUTOR("Tutor");

        private final String friendlyName;

        private Owners_naming(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public static Owners_naming getEnumVal(String friendlyName) {
            if (friendlyName.equals("Propietario")) {
                return OWNER;
            } else if (friendlyName.equals("Dueño")) {
                return LANDLORD;
            } else if (friendlyName.equals("Cliente")) {
                return CLIENT;
            } else if (friendlyName.equals("Tutor")) {
                return TUTOR;
            }

            return null;
        }
    }
    public enum Pets_naming {
        ANIMAL("Animal"),
        PET("Mascota"),
        PATIENT("Paciente");

        private final String friendlyName;

        private Pets_naming(String friendlyName) {
            this.friendlyName = friendlyName;
        }

        @Override
        public String toString() {
            return friendlyName;
        }

        public static Pets_naming getEnumVal(String friendlyName) {
            if (friendlyName.equals("Mascota")) {
                return PET;
            } else if (friendlyName.equals("Paciente")) {
                return PATIENT;
            } else if (friendlyName.equals("Animal")) {
                return ANIMAL;
            }

            return null;
        }
    }
    public enum INTENT_VALUES {
        OWNER_OBJ, OWNER_ID, OWNER_NOTIFICATION_ID,
        PET_OBJ, PET_ID,
        PET_RACE_OBJ, PET_PELAGE_OBJ, PET_GENDER_OBJ, PET_CHARACTER_OBJ,
        CLINIC_OBJ, CLINIC_ID,
        CLINIC2_OBJ, CLINIC2_ID,
        RECIPE_OBJ, RECIPE_ID,
        SUPPLY_OBJ, SUPPLY_ID,
        PRODUCT_OBJ, PRODUCT_LIST, PRODUCT_ID,
        PRODUCT_CATEGORY_OBJ, PRODUCT_CATEGORY_ID,
        PRODUCT_UNIT_OBJ, PRODUCT_UNIT_ID,
        PRODUCT_PROVIDER_OBJ, PRODUCT_PROVIDER_ID,
        PRODUCT_MANUFACTURER_OBJ, PRODUCT_MANUFACTURER_ID,
        STUDY_OBJ, STUDY_ID, STUDY_ITEM_OBJ,
        VET_OBJ, VET_ID,
        USER_OBJ, USER_ID,
        AGENDA_OBJ, AGENDA_ID,
        REGION_OBJ,
        PAYMENTS_TYPES_LIST, PAYMENT_OBJ,
        DEPOSIT_ID, DEPOSIT_OBJ,
        SELL_POINT_ID, SELL_POINT_OBJ,
        MOVEMENT_ID, MOVEMENT_OBJ, MOVEMENT_DETAIL_OBJ,
        CASH_MOVEMENT_ID,
        SPENDING_ID, SPENDING_OBJ,
        SELL_ID, SELL_OBJ, SELL_ITEM_OBJ, SELL_PAYMENT_OBJ,
        PURCHASE_OBJ, PURCHASE_ID, PURCHASE_DETAIL_OBJ, PURCHASE_PAYMENT_OBJ,
        SERVICE_SCHEDULE_OBJ,
        SYMPTOM_OBJ, DIAGNOSTIC_OBJ, TREATMENT_OBJ, SDT_OBJ,
        VET_POINT_OBJ
    }
    public enum Response_status { SUCCESS, NOT_SUCCESS, ERROR }

    public static final String INTENT_AGENDA_SELECTED_DATE = "agendaSelectedDate";
    public static final String INTENT_DATE = "date";
    public static final String INTENT_SEARCH_VIEW = "searchView";
    public static final String INTENT_SEARCH_RETURN = "searchReturn";
    public static final String INTENT_UPDATE_LAST_VIEW = "intentUpdateLastView";
    public static final int REQUEST_INIT_CREATE_VET = 106;
    public static final int REQUEST_INIT_SELECT_VET = 107;
    public static final int REQUEST_CREATE_BRANCH_VET = 108;
    public static final String REQUEST_SEARCH_FOR = "searchFor";
    public static final String REQUEST_CREATE_OBJECT = "createObject";

    //Volley
    private RequestQueue requestQueue;

    private User user;
    public Vet getVet() {
        return getUser().getVet();
    }
    public User getUser() {
        if (user == null)
            user = MySqlGson.getGson().fromJson(preferences_get_user(), User.class);

        return user;
    }

    private Socket socket = null;
    public Socket getSocket() {
        if (socket == null) {
            try {
                socket = IO.socket(NetworkUtils.DOCTORVET_SOCKET_URL);
                socket.connect();
                String id_vet = getVet().getId().toString();
                String id_user = getUser().getId().toString();
                String init_message = String.format("{\"id_vet\":%s,\"id_user\":%s}", id_vet, id_user);
                socket.emit("init", init_message);
            } catch (URISyntaxException e) {
                handle_error(e, TAG, true);
            }
        }

        return socket;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
        sharedPreferences = this.getSharedPreferences(getString(R.string.preference_private_file), Context.MODE_PRIVATE);

        //First run, set guid and default preferences
        if (sharedPreferences.getString(getString(R.string.preference_unique_id), "").equals("")) {
            //GUID
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.preference_unique_id), HelperClass.generateGUID());

            //temp and weight
            editor.putString(getString(R.string.preference_settings_temp_unit), "°C");
            editor.putString(getString(R.string.preference_settings_weight_unit), "kg");

            //owners view mode
            editor.putString(getString(R.string.preference_owners_view_mode), "COMPACT");

            //sys_last_update
            editor.putString("owners_last_update", "2022-06-10 12:00:00");
            editor.putString("regions_last_update", "2022-06-10 12:00:00");
            editor.putString("products_last_update", "2022-06-10 12:00:00");
            editor.putString("finance_types_payments_last_update", "2022-06-10 12:00:00");

            editor.putString("vet_studies_last_update", "2022-06-10 12:00:00");

            editor.putString("pets_for_input_last_update", "2022-06-10 12:00:00");
            editor.putString("pets_clinic_2_for_input_last_update", "2022-06-10 12:00:00");
            editor.putString("pets_recipes_for_input_last_update", "2022-06-10 12:00:00");

            editor.putString("products_for_input_last_update", "2022-06-10 12:00:00");

            editor.putString("movements_for_input_last_update", "2022-06-10 12:00:00");
            editor.putString("purchases_for_input_last_update", "2022-06-10 12:00:00");
            editor.putString("sells_for_input_last_update", "2022-06-10 12:00:00");

            editor.putString("services_for_input_last_update", "2022-06-10 12:00:00");

            editor.apply();
        }

        //version db 30
        if (!sharedPreferences.contains("owners_for_input_last_update")) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("owners_for_input_last_update", "2022-06-10 12:00:00");
            editor.apply();
        }

    }

    public String getOwnerNaming() {
        DoctorVetApp.Owners_naming ownersNamingEnum = get().getVet().getOwner_naming();
        return ownersNamingEnum.toString();
    }
    public String getOwnerNamingPlural() {
        DoctorVetApp.Owners_naming ownersNamingEnum = get().getVet().getOwner_naming();

        if (ownersNamingEnum == Owners_naming.TUTOR)
            return "Tutores";

        return ownersNamingEnum.toString() + "s";
    }
    public String getPetNaming() {
        DoctorVetApp.Pets_naming petsNamingEnum = get().getVet().getPet_naming();
        return petsNamingEnum.toString();
    }
    public String getPetNamingPlural() {
        DoctorVetApp.Pets_naming petsNamingEnum = get().getVet().getPet_naming();

        if (petsNamingEnum == Pets_naming.ANIMAL)
            return "Animales";

        return petsNamingEnum.toString() + "s";
    }
    public String getPetNamingNew() {
        DoctorVetApp.Pets_naming petsNamingEnum = get().getVet().getPet_naming();

        if (petsNamingEnum == Pets_naming.PET)
            return "Nueva mascota";

        return "Nuevo " + getPetNaming().toLowerCase();
    }

    public View getOwnerDebtUI(Owner owner, LayoutInflater layoutInflater, Context ctx) {
        View debt_view = layoutInflater.inflate(R.layout.list_item_owner_info_debt, null);
        TextView debt = debt_view.findViewById(R.id.txt_debt_amount);
        TextView details = debt_view.findViewById(R.id.txt_debt_details);
        TextView pay = debt_view.findViewById(R.id.txt_debt_pay);

        debt.setText("Deuda: " + HelperClass.formatCurrency(owner.getBalance()));

        details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, ViewOwnerDebtsActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_VALUES.OWNER_ID.name(), owner.getId());
                intent.putExtra("owner_name", owner.getName());
                ctx.startActivity(intent);
            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx, EditSellPaymentActivity.class);
                intent.putExtra(INTENT_VALUES.OWNER_OBJ.name(), MySqlGson.getPostJsonString(owner));
                intent.putExtra("debt_amount", owner.getBalance());
                ctx.startActivity(intent);
            }
        });

        return debt_view;
    }
    public void markRequired(TextInputLayout textInputLayout) {
        textInputLayout.setHint(textInputLayout.getHint() + " *");
    }
//    public String reasonToString(String reason) {
//        if (reason == null) return "";
//        if (reason.equalsIgnoreCase("created")) return "Creación";
//        if (reason.equalsIgnoreCase("updated")) return "Edición";
//        if (reason.equalsIgnoreCase("requested")) return "Búsqueda";
//        if (reason.equalsIgnoreCase("sell")) return "Venta";
//        if (reason.equalsIgnoreCase("debt")) return "Deuda";
//        if (reason.equalsIgnoreCase("clinic")) return "Clínica";
//        if (reason.equalsIgnoreCase("clinic2")) return "Clínica";
//        if (reason.equalsIgnoreCase("supply")) return "Suministro";
//        if (reason.equalsIgnoreCase("study")) return "Estudio";
//        if (reason.equalsIgnoreCase("recipe")) return "Receta";
//        return "";
//    }

    public void doFinalAuthPost(Integer id_vet, Activity from, Class to) {
        if (DoctorVetApp.get().preferences_getUserLoginType() == DoctorVetApp.login_types.EMAIL) {
            DoctorVetApp.get().emailPostFinalAuth(
                    DoctorVetApp.get().preferences_getUserEmail(),
                    DoctorVetApp.get().preferences_get_user_password(),
                    id_vet,
                    new DoctorVetApp.VolleyCallback() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (result) {
                                Intent intent = new Intent(from, to);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                from.finish();
                            }
                        }
                    }
            );
        } else {
            DoctorVetApp.get().googleFacebookPostFinalAuth(
                    DoctorVetApp.get().preferences_getUserEmail(),
                    id_vet,
                    new DoctorVetApp.VolleyCallback() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if (result) {
                                Intent intent = new Intent(from, to);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                from.finish();
                            }
                        }
                    }
            );
        }
    }
    public void emailPostFinalAuth(String email, String password, Integer idVet, VolleyCallback callback) {
        User authUser = new User();
        authUser.setEmail(email);
        authUser.setPassword(password);
        authUser.setVet(new Vet(idVet));

        String jsonUser = MySqlGson.getPostJsonString(authUser);

        URL auth_accountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.EMAIL_AUTH, null, null, null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, auth_accountUrl.toString(), response -> {
            try {
                String data = MySqlGson.getDataFromResponse(response).getAsJsonObject().get("user").toString();
                User responseUser = MySqlGson.getGson().fromJson(data, User.class);
                DoctorVetApp.get().preferences_deleteLoginInfo();
                DoctorVetApp.get().preferences_setLoginInfo(null, null, null, responseUser.getAccess_token());
                callback.onSuccess(true);
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                callback.onSuccess(false);
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            callback.onSuccess(false);
        })
        {
            @Override
            public byte[] getBody() {
                return jsonUser.getBytes();
            }
        };
        addToRequestQueque(stringRequest);
    }
    public void googleFacebookPostFinalAuth(String email, Integer idVet, VolleyCallback callback) {
        User authUser = new User();
        authUser.setEmail(email);

        //authUser.setId_vet(idVet);
        authUser.setVet(new Vet(idVet));

        String jsonUser = MySqlGson.getPostJsonString(authUser);

        URL auth_accountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.FACEBOOK_GOOGLE_AUTH, null, null, null);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, auth_accountUrl.toString(), response -> {
            try {
                String data = MySqlGson.getDataFromResponse(response).getAsJsonObject().get("user").toString();
                User responseUser = MySqlGson.getGson().fromJson(data, User.class);
                DoctorVetApp.get().preferences_deleteLoginInfo();
                DoctorVetApp.get().preferences_setLoginInfo(null, null, null, responseUser.getAccess_token());
                callback.onSuccess(true);
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                callback.onSuccess(false);
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            callback.onSuccess(false);
        })
        {
            @Override
            public byte[] getBody() {
                return jsonUser.getBytes();
            }
        };
        addToRequestQueque(stringRequest);
    }
    public void logout(VolleyCallback callback) {
        URL auth_accountUrl = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.LOGOUT_ACCOUNT, null, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, auth_accountUrl.toString(), response -> {
            try {
                DoctorVetApp.get().preferences_deleteLoginInfo();
                callback.onSuccess(true);
            } catch (Exception ex) {
                DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                callback.onSuccess(false);
            }
        }, error -> {
            DoctorVetApp.get().handle_volley_error(error, TAG, true);
            callback.onSuccess(false);
        });
        addToRequestQueque(stringRequest);
    }

    public void getRegions(final VolleyCallbackArrayList volleyCallbackArrayList) {
        getLastUpdate("regions", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("regions_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("regions");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildGetRegionesMinUrl();
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("regions", json);
                                            setPreference("regions_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            ArrayList<Region> regions = MySqlGson.getGson().fromJson(json, new TypeToken<List<Region>>(){}.getType());
                                            volleyCallbackArrayList.onSuccess(regions);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackArrayList.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackArrayList.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String regionsData = DoctorVetApp.get().readFromDisk("regions");
                        ArrayList<Region> regions = MySqlGson.getGson().fromJson(regionsData, new TypeToken<List<Region>>(){}.getType());
                        volleyCallbackArrayList.onSuccess(regions);
                    }
                }
            }
        });
    }
    public void getRegionsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL regionesUrl = NetworkUtils.buildGetRegionesUrl(search, page);
        StringRequest tokenStringRequest = new StringRequest(Request.Method.GET, regionesUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Region.Get_pagination_regiones response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Region.Get_pagination_regiones>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }

    public void getOwners(final VolleyCallbackArrayList volleyCallbackArrayList) {
        getLastUpdate("owners", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("owners_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("owners");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildGetOwnersMinUrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("owners", json);
                                            setPreference("owners_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            ArrayList<Owner> owners = MySqlGson.getGson().fromJson(json, new TypeToken<List<Owner>>(){}.getType());
                                            volleyCallbackArrayList.onSuccess(owners);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackArrayList.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackArrayList.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String ownersData = DoctorVetApp.get().readFromDisk("owners");
                        ArrayList<Owner> owners = MySqlGson.getGson().fromJson(ownersData, new TypeToken<List<Owner>>(){}.getType());
                        volleyCallbackArrayList.onSuccess(owners);
                    }
                }
            }
        });
    }
    public void getPetsForInputObject(final VolleyCallbackObject volleyCallbackObject) {
        getLastUpdateObj("pets_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("pets_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("pets_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL petsForInput = NetworkUtils.buildGetPetsForInputUrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, petsForInput.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("pets_for_input", json);
                                            setPreference("pets_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Pet.Pets_for_input pets_for_input = MySqlGson.getGson().fromJson(json, Pet.Pets_for_input.class);
                                            volleyCallbackObject.onSuccess(pets_for_input);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String petsForInputData = DoctorVetApp.get().readFromDisk("pets_for_input");
                        Pet.Pets_for_input pets_for_input = MySqlGson.getGson().fromJson(petsForInputData, Pet.Pets_for_input.class);
                        volleyCallbackObject.onSuccess(pets_for_input);
                    }
                }
            }
        });
    }
    public void getOwnersForInput(final VolleyCallbackObject volleyCallbackObject) {
        getLastUpdateObj("owners_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("owners_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("owners_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildGetOwnersForInputUrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("owners_for_input", json);
                                            setPreference("owners_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Owner.Owners_for_input owners_for_input = MySqlGson.getGson().fromJson(json, Owner.Owners_for_input.class);
                                            volleyCallbackObject.onSuccess(owners_for_input);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String ownersForInputData = DoctorVetApp.get().readFromDisk("owners_for_input");
                        Owner.Owners_for_input owners_for_input = MySqlGson.getGson().fromJson(ownersForInputData, Owner.Owners_for_input.class);
                        volleyCallbackObject.onSuccess(owners_for_input);
                    }
                }
            }
        });
    }

    public void getProductsVet(final VolleyCallbackArrayList volleyCallbackArrayList) {
        getLastUpdate("products", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("products_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("products");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buidGetProductsVetMinUrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("products", json);
                                            setPreference("products_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            ArrayList<Product> products = MySqlGson.getGson().fromJson(json, new TypeToken<List<Product>>(){}.getType());
                                            volleyCallbackArrayList.onSuccess(products);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackArrayList.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackArrayList.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String productsData = DoctorVetApp.get().readFromDisk("products");
                        ArrayList<Product> products = MySqlGson.getGson().fromJson(productsData, new TypeToken<List<Product>>(){}.getType());
                        volleyCallbackArrayList.onSuccess(products);
                    }
                }
            }
        });
    }
    public ArrayList<Product> getProductsVetFromDisk() {
        String productsData = DoctorVetApp.get().readFromDisk("products");
        ArrayList<Product> products = MySqlGson.getGson().fromJson(productsData, new TypeToken<List<Product>>(){}.getType());
        return products;
    }
    public void getProductsVetPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL productosUrl = NetworkUtils.buidGetProductsVetUrl(search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product.Get_pagination_products response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product.Get_pagination_products>() {
                            }.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getProductsForInputObject(final VolleyCallbackObject volleyCallbackObject) {
        getLastUpdateObj("products_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("products_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("products_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL productsForInput = NetworkUtils.buildProductsUrl(NetworkUtils.ProductsUrlEnum.FOR_INPUT, null);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productsForInput.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("products_for_input", json);
                                            setPreference("products_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Product.Products_for_input products_for_input = MySqlGson.getGson().fromJson(json, Product.Products_for_input.class);
                                            volleyCallbackObject.onSuccess(products_for_input);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String productsForInputData = DoctorVetApp.get().readFromDisk("products_for_input");
                        Product.Products_for_input productsForInput = MySqlGson.getGson().fromJson(productsForInputData, Product.Products_for_input.class);
                        volleyCallbackObject.onSuccess(productsForInput);
                    }
                }
            }
        });
    }
    public void getServicesForInputObject(final VolleyCallbackObject volleyCallbackObject) {
        getLastUpdateObj("services_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("services_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("services_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL servicesForInput = NetworkUtils.buildProductsUrl(NetworkUtils.ProductsUrlEnum.SERVICES_FOR_INPUT, null);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, servicesForInput.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("services_for_input", json);
                                            setPreference("services_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Product.Services_for_input services_for_input = MySqlGson.getGson().fromJson(json, Product.Services_for_input.class);
                                            volleyCallbackObject.onSuccess(services_for_input);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String servicesForInputData = DoctorVetApp.get().readFromDisk("services_for_input");
                        Product.Services_for_input servicesForInput = MySqlGson.getGson().fromJson(servicesForInputData, Product.Services_for_input.class);
                        volleyCallbackObject.onSuccess(servicesForInput);
                    }
                }
            }
        });
    }
    public void getProductByBarcode(String barcode, final VolleyCallbackObject volleyCallbackObject) {
        URL productByBarcode = NetworkUtils.buildProductUrl(null, barcode, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productByBarcode.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product product = MySqlGson.getGson().fromJson(data, Product.class);
                            volleyCallbackObject.onSuccess(product);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, true, response);
                            volleyCallbackObject.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, false);
                        volleyCallbackObject.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }

    public void getPetsRecipesForInput(final VolleyCallbackObject callbackObject) {
        getLastUpdateObj("pets_recipes_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("pets_recipes_for_input_last_update"));
                    Boolean existsCacheFile = existsCacheFile("pets_recipes_for_input");
                    Boolean mustDoRequest = !existsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL petsForInput = NetworkUtils.buildPetsRecipesUrl(true, null);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, petsForInput.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("pets_recipes_for_input", json);
                                            setPreference("pets_recipes_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Pet_recipe.Pets_recipes_for_input petsRecipesForInput = MySqlGson.getGson().fromJson(json, Pet_recipe.Pets_recipes_for_input.class);
                                            callbackObject.onSuccess(petsRecipesForInput);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            callbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        callbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String pets_recipes_for_input_data = DoctorVetApp.get().readFromDisk("pets_recipes_for_input");
                        Pet_recipe.Pets_recipes_for_input petsRecipesForInput = MySqlGson.getGson().fromJson(pets_recipes_for_input_data, Pet_recipe.Pets_recipes_for_input.class);
                        callbackObject.onSuccess(petsRecipesForInput);
                    }
                }
            }
        });
    }
    public void getOwnersPagination(String search, Integer page, final VolleyCallbackPagination callbackAdapter) {
        URL propietariosUrl = NetworkUtils.buildGetOwnersUrl(get_format.SEARCH, search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, propietariosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Owner.Get_pagination_owners response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Owner.Get_pagination_owners>() {
                            }.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getOwner(Integer id_owner, Integer update_last_view, final VolleyCallbackOwner callbackPropietario) {
        URL propietarioUrl = NetworkUtils.buildGetOwnerUrl(id_owner, update_last_view);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, propietarioUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Owner owner = MySqlGson.getGson().fromJson(data, Owner.class);
                            callbackPropietario.onSuccess(owner);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPropietario.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPropietario.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getOwnersRecentPagination(Integer page, final VolleyCallbackPagination callbackAdapter) {
        URL url = NetworkUtils.buildGetOwnersRecientesUrl(page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Owner.Get_pagination_owners response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Owner.Get_pagination_owners>() {
                            }.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void getOwnerNotification(Integer notification_id, final VolleyCallbackObject callbackObject) {
        URL url = NetworkUtils.buildOwnerNotificationUrl(notification_id);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            OwnerNotification ownerNotification = MySqlGson.getGson().fromJson(data, new TypeToken<OwnerNotification>(){}.getType());
                            callbackObject.onSuccess(ownerNotification);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackObject.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackObject.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getOwnerDebts(Integer id_owner, final VolleyCallbackAdapter callbackAdapter) {
        URL ownerDebtsURL = NetworkUtils.buildOwnerDebtsUrl(id_owner);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, ownerDebtsURL.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Sell> sells_with_debt = MySqlGson.getGson().fromJson(data, new TypeToken<List<Sell>>(){}.getType());
                            SellsAdapter sellsAdapter = new SellsAdapter(sells_with_debt);
                            callbackAdapter.onSuccess(sellsAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getProviderDebts(Integer id_provider, final VolleyCallbackAdapter callbackAdapter) {
        URL providerDebtsUrl = NetworkUtils.buildProviderDebtsUrl(id_provider);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, providerDebtsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Purchase> purchases_with_debt = MySqlGson.getGson().fromJson(data, new TypeToken<List<Purchase>>(){}.getType());
                            PurchasesAdapter purchasesAdapter = new PurchasesAdapter(purchases_with_debt);
                            callbackAdapter.onSuccess(purchasesAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getSellsForInput(final VolleyCallbackObject callbackObject) {
        getLastUpdateObj("sells_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("sells_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("sells_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildSellsUrl(NetworkUtils.SellsUrlEnum.FOR_INPUT, null);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).toString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("sells_for_input", json);
                                            setPreference("sells_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Sell.SellsForInput sellsForInput = MySqlGson.getGson().fromJson(json, Sell.SellsForInput.class);
                                            callbackObject.onSuccess(sellsForInput);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            callbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        callbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String sellsForInputData = DoctorVetApp.get().readFromDisk("sells_for_input");
                        Sell.SellsForInput sellsForInput = MySqlGson.getGson().fromJson(sellsForInputData, Sell.SellsForInput.class);
                        callbackObject.onSuccess(sellsForInput);
                    }
                }
            }
        });
    }
    public void getPurchasesForInput(final VolleyCallbackObject callbackObject) {
        getLastUpdateObj("purchases_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("purchases_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("purchases_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildPurchasesUrl(NetworkUtils.PurchasesUrlEnum.FOR_INPUT);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).toString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("purchases_for_input", json);
                                            setPreference("purchases_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Purchase.PurchaseForInput purchaseForInput = MySqlGson.getGson().fromJson(json, Purchase.PurchaseForInput.class);
                                            callbackObject.onSuccess(purchaseForInput);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            callbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        callbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String purchasesForInputData = DoctorVetApp.get().readFromDisk("purchases_for_input");
                        Purchase.PurchaseForInput purchaseForInput = MySqlGson.getGson().fromJson(purchasesForInputData, Purchase.PurchaseForInput.class);
                        callbackObject.onSuccess(purchaseForInput);
                    }
                }
            }
        });
    }
    public void getSellSuggestedProducts(Integer id_owner, final VolleyCallbackArrayList volleyCallbackArrayList) {
        URL url = NetworkUtils.buildSellsUrl(NetworkUtils.SellsUrlEnum.SUGGESTED_PRODUCTS, id_owner);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Sell_item> sellItems = MySqlGson.getGson().fromJson(data, new TypeToken<List<Sell_item>>(){}.getType());
                            volleyCallbackArrayList.onSuccess(sellItems);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            volleyCallbackArrayList.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        volleyCallbackArrayList.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getSellPlanning(Sell sell, final VolleyCallbackArrayList volleyCallbackArrayList) {
        URL url = NetworkUtils.buildSellsUrl(NetworkUtils.SellsUrlEnum.PLANNING_ACTIVITY, null);
        final String sell_json_object = MySqlGson.postGson().toJson(sell);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    ArrayList<Pet_supply> petSupplies = MySqlGson.getGson().fromJson(data, new TypeToken<List<Pet_supply>>(){}.getType());
                    volleyCallbackArrayList.onSuccess(petSupplies);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    volleyCallbackArrayList.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                volleyCallbackArrayList.onSuccess(null);
            }
        })
        {
            @Override
            public byte[] getBody() {
                return sell_json_object.getBytes();
            }
        };
        addToRequestQueque(stringRequest);








//        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            String data = MySqlGson.getDataFromResponse(response).toString();
//                            ArrayList<Pet_supply> petSupplies = MySqlGson.getGson().fromJson(data, new TypeToken<List<Pet_supply>>(){}.getType());
//                            volleyCallbackArrayList.onSuccess(petSupplies);
//                        } catch (Exception ex) {
//                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
//                            volleyCallbackArrayList.onSuccess(null);
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
//                        volleyCallbackArrayList.onSuccess(null);
//                    }
//                })
//                {
//                    @Override
//                    public byte[] getBody() throws AuthFailureError {
//                        return MySqlGson.postGson().toJson(sell).getBytes();
//                    }
//                };
//
//        addToRequestQueque(stringRequest);
    }

    public void getMovementsForInput(final VolleyCallbackObject callbackObject) {
        getLastUpdateObj("movements_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("movements_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("movements_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildMovementsUrl(NetworkUtils.MovementsUrlEnum.FOR_INPUT, null, null);
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("movements_for_input", json);
                                            setPreference("movements_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Movement.MovementsForInput movementsForInput = MySqlGson.getGson().fromJson(json, Movement.MovementsForInput.class);
                                            callbackObject.onSuccess(movementsForInput);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            callbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        callbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String movementsForInputData = DoctorVetApp.get().readFromDisk("movements_for_input");
                        Movement.MovementsForInput movementsForInput = MySqlGson.getGson().fromJson(movementsForInputData, Movement.MovementsForInput.class);
                        callbackObject.onSuccess(movementsForInput);
                    }
                }
            }
        });
    }
    public void getVetDepositsAdapter(final VolleyCallbackAdapter callbackAdapter) {
        URL vetDepositsUrl = NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.DEPOSITS, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, vetDepositsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Vet_deposit> vetdeposits = MySqlGson.getGson().fromJson(data, new TypeToken<List<Vet_deposit>>() {
                            }.getType());
                            VetsDepositsAdapter vetsDepositsAdapter = new VetsDepositsAdapter(vetdeposits);
                            callbackAdapter.onSuccess(vetsDepositsAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getVetPointsAdapter(final VolleyCallbackAdapter callbackAdapter) {
        URL url = NetworkUtils.buildVetUrl(NetworkUtils.vetsUrl.ALL_POINTS, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Vet_point> vetPoints = MySqlGson.getGson().fromJson(data, new TypeToken<List<Vet_point>>() {
                            }.getType());
                            VetPointsAdapter vetPointsAdapter = new VetPointsAdapter(vetPoints);
                            callbackAdapter.onSuccess(vetPointsAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getServicesSchedulesVet(final VolleyCallbackAdapter callbackAdapter) {
        URL url = NetworkUtils.buildServicesSchedulesVetUrl("schedules");
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            android.util.Log.d("Tag", data);
                            ArrayList<Service_schedule> serviceSchedules = MySqlGson.getGson().fromJson(data, new TypeToken<List<Service_schedule>>(){}.getType());
                            ServicesSchedulesAdapter servicesSchedulesAdapter = new ServicesSchedulesAdapter(serviceSchedules);
                            callbackAdapter.onSuccess(servicesSchedulesAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }

    public void getFinanceTypesReceipts(final VolleyCallbackAdapter callbackAdapter) {
        URL url = NetworkUtils.buildReceiptTypesUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Finance_types_receipts> finance_types_receipts = MySqlGson.getGson().fromJson(data, new TypeToken<List<Finance_types_receipts>>(){}.getType());
                            FinanceTypesReceiptsAdapter financeTypesReceiptsAdapter = new FinanceTypesReceiptsAdapter(finance_types_receipts);
                            callbackAdapter.onSuccess(financeTypesReceiptsAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getFinanceTypesFiscal(String country, final VolleyCallbackAdapter callbackAdapter) {
        URL url = NetworkUtils.buildFiscalTypesUrl(country);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            ArrayList<Finance_types_fiscal> finance_types_fiscal = MySqlGson.getGson().fromJson(data, new TypeToken<List<Finance_types_fiscal>>(){}.getType());
                            FinanceTypesFiscalAdapter financeTypesFiscalAdapter = new FinanceTypesFiscalAdapter(finance_types_fiscal);
                            callbackAdapter.onSuccess(financeTypesFiscalAdapter);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getWaitingRoomsHistoricPagination(Integer page, final VolleyCallbackPagination callbackAdapter) {
        URL waitingRoomsHistoricUrl = NetworkUtils.buildgetWaitingRoomsHistoricUrl(page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, waitingRoomsHistoricUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Waiting_room.Get_pagination_waiting_rooms response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Waiting_room.Get_pagination_waiting_rooms>() {
                            }.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void getPetsPagination(String search, Integer page, final VolleyCallbackPagination callbackAdapter) {
//        Integer id_vet = DoctorVetApp.get().getVet().getId();
        URL mascotasUrl = NetworkUtils.buildGetPetsUrl(search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, mascotasUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet.Get_pagination_pets response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet.Get_pagination_pets>(){}.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getPet(Integer id_pet, Integer update_last_view, final VolleyCallbackMascota callbackMascota) {
        URL mascotaUrl = NetworkUtils.buildGetPetUrl(id_pet, update_last_view);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, mascotaUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet pet = MySqlGson.getGson().fromJson(data, Pet.class);
                            callbackMascota.onSuccess(pet);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackMascota.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackMascota.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getPetsRecentPagination(Integer page, final VolleyCallbackPagination callbackAdapter) {
//        Integer id_vet_en_uso = DoctorVetApp.get().getVet().getId();
        URL mascotasUrl = NetworkUtils.buildGetPetsRecientesUrl(page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, mascotasUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Pet.Get_pagination_pets response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet.Get_pagination_pets>() {
                            }.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void getPets_racesPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL razasUrl = NetworkUtils.buildGetPetsRazasUrl(search/*, get_format.SEARCH*/, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, razasUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_race.Get_pagination_races response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_race.Get_pagination_races>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getPets_pelagesPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL pelajesUrl = NetworkUtils.buildGetPetsPelajesUrl(search/*, get_format.SEARCH*/, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, pelajesUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_pelage.Get_pagination_pelages response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_pelage.Get_pagination_pelages>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getPets_gendersAdapter(final VolleyCallbackAdapter callbackAdapter) {
        final URL sexosUrl = NetworkUtils.buildGetPetsgendersUrl();
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, sexosUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    ArrayList<Pet_gender> sexos = MySqlGson.getGson().fromJson(data, new TypeToken<List<Pet_gender>>() {
                    }.getType());
                    Pet_gendersAdapter petgendersAdapter = new Pet_gendersAdapter(sexos);
                    callbackAdapter.onSuccess(petgendersAdapter);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackAdapter.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackAdapter.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getPets_charactersAdapter(final VolleyCallbackAdapter callbackAdapter) {
        final URL mascotas_characteresUrl = NetworkUtils.buildGetPetsCaracteresUrl();
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, mascotas_characteresUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    ArrayList<Pet_character> mascotas_characteres = MySqlGson.getGson().fromJson(data, new TypeToken<List<Pet_character>>() {
                    }.getType());
                    Pet_charactersAdapter mascotas_characteresAdapter = new Pet_charactersAdapter(mascotas_characteres);
                    callbackAdapter.onSuccess(mascotas_characteresAdapter);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackAdapter.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackAdapter.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }

    public void getProductsProvidersPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL productosUrl = NetworkUtils.buidGetProvidersUrl(get_format.SEARCH, search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product_provider.Get_pagination_providers response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_provider.Get_pagination_providers>() {}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getProductsManufacturersPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL productosUrl = NetworkUtils.buildGetProductsManufacturersUrl(get_format.SEARCH, search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, productosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product_manufacturer.Get_pagination_manufacturers response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_manufacturer.Get_pagination_manufacturers>() {}.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getProductsVetIncludeGlobalsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL url = NetworkUtils.buidGetProductsVetIncludeGlobalsUrl(search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product.Get_pagination_products response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product.Get_pagination_products>() {
                            }.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getVetServicesAssoc(VolleyCallbackArrayList callbackAdapter) {
        URL url = NetworkUtils.buidGetVetServicesAssocUrl();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            //String json = HelperClass.compressedBase64ToString(response);
                            ArrayList<Product> products = MySqlGson.getGson().fromJson(data, new TypeToken<List<Product>>() {
                            }.getType());
                            //ProductsAdapter productsAdapter = new ProductsAdapter(products, ProductsAdapter.Products_types.NORMAL);
                            callbackAdapter.onSuccess(products);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }

    public void getVetServicesPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL serviciosUrl = NetworkUtils.buildGetVetServicesUrl(search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, serviciosUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Product.Get_pagination_products response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product.Get_pagination_products>() {
                            }.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getProductos_unitsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL productos_unitesUrl = NetworkUtils.buildGetProductsUnitsUrl(search, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, productos_unitesUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Product_unit.Get_pagination_unites response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_unit.Get_pagination_unites>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getProductsCategoriesPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL productos_categoriasUrl = NetworkUtils.buildGetProductCategoriesUrl(search, get_format.SEARCH, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, productos_categoriasUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Product_category.Get_pagination_categorias response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_category.Get_pagination_categorias>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getServicesCategoriesPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL url = NetworkUtils.buildGetServicesCategoriesUrl(search, get_format.SEARCH, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Product_category.Get_pagination_categorias response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product_category.Get_pagination_categorias>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getVetStudies(final VolleyCallbackArrayList volleyCallbackArrayList) {
        getLastUpdateObj("vet_studies", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("vet_studies_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("vet_studies");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buildGetVetStudiesMinUrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).toString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("vet_studies", json);
                                            setPreference("vet_studies_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            ArrayList<Product> products = MySqlGson.getGson().fromJson(json, new TypeToken<List<Product>>(){}.getType());
                                            volleyCallbackArrayList.onSuccess(products);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackArrayList.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackArrayList.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String vetStudiesData = DoctorVetApp.get().readFromDisk("vet_studies");
                        ArrayList<Product> products = MySqlGson.getGson().fromJson(vetStudiesData, new TypeToken<List<Product>>(){}.getType());
                        volleyCallbackArrayList.onSuccess(products);
                    }
                }
            }
        });
    }
    public void getVetStudiesPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL productos_estudiosUrl = NetworkUtils.buildGetPetsStudiesPaginationUrl(search, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, productos_estudiosUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Product.Get_pagination_products response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Product.Get_pagination_products>() {
                    }.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getClinicsPagination(Integer id_pet, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL clinicaUrl = NetworkUtils.buildGetClinicUrl(id_pet, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, clinicaUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_clinic_root.Get_pagination_clinics response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_clinic_root.Get_pagination_clinics>(){}.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getSupplyPagination(Integer id_pet, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL supplyUrl = NetworkUtils.buildGetPetsSupplyUrl(id_pet, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, supplyUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_supply.Get_pagination_supply response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_supply.Get_pagination_supply>(){}.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getDailyCashPagination(Date date, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL dailyCashUrl = NetworkUtils.buidGetDailyCashUrl(date, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, dailyCashUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Daily_cash.Get_pagination_daily_cash response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Daily_cash.Get_pagination_daily_cash>() {
                            }.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getSell(Integer id_sell, final VolleyCallbackSell callbackSell) {
        URL sellUrl = NetworkUtils.buildGetSellUrl(id_sell);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, sellUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Sell sell = MySqlGson.getGson().fromJson(data, Sell.class);
                            callbackSell.onSuccess(sell);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackSell.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackSell.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getPurchase(Integer id_purchase, final VolleyCallbackPurchase callbackPurchase) {
        URL sellUrl = NetworkUtils.buildGetPurchaseUrl(id_purchase);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, sellUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Purchase purchase = MySqlGson.getGson().fromJson(data, Purchase.class);
                            callbackPurchase.onSuccess(purchase);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPurchase.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPurchase.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getMovement(Integer id_movement, final VolleyCallbackMovement callbackMovement) {
        URL movementUrl = NetworkUtils.buildMovementsUrl(null, id_movement, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, movementUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Movement movement = MySqlGson.getGson().fromJson(data, Movement.class);
                            callbackMovement.onSuccess(movement);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackMovement.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackMovement.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getCashMovement(Integer id_cash_movement, final VolleyCallbackObject callback) {
        URL cashMovementUrl = NetworkUtils.buildGetCashMovementUrl(id_cash_movement);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, cashMovementUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Cash_movement cashMovement = MySqlGson.getGson().fromJson(data, Cash_movement.class);
                            callback.onSuccess(cashMovement);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callback.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callback.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getSpending(Integer id_spending, final VolleyCallbackObject callback) {
        URL url = NetworkUtils.buildGetSpendingUrl(id_spending);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Spending spending = MySqlGson.getGson().fromJson(data, Spending.class);
                            callback.onSuccess(spending);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callback.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callback.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getDailyCashDetails(String date, final VolleyCallbackDailyCashDetails callbackDailyCashDetails) {
        URL dailyCashDetailsUrl = NetworkUtils.buildGetDailyCashDetailsUrl(date);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, dailyCashDetailsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            DailyCashDetails dailyCashDetails = MySqlGson.getGson().fromJson(data, DailyCashDetails.class);
                            callbackDailyCashDetails.onSuccess(dailyCashDetails);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackDailyCashDetails.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackDailyCashDetails.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getFinanceTypesPayments(final VolleyCallbackArrayList volleyCallbackArrayList) {
        getLastUpdate("finance_types_payments", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("finance_types_payments_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("finance_types_payments");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL url = NetworkUtils.buidGetFinanceTypesPaymemntsUrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).toString();
                                            ArrayList<Finance_payment_method> payment_methods = MySqlGson.getGson().fromJson(data, new TypeToken<List<Finance_payment_method>>() {}.getType());
                                            DoctorVetApp.get().writeToDisk("finance_types_payments", data);
                                            setPreference("finance_types_payments_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            volleyCallbackArrayList.onSuccess(payment_methods);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackArrayList.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackArrayList.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String paymentsData = DoctorVetApp.get().readFromDisk("finance_types_payments");
                        ArrayList<Finance_payment_method> payment_methods = MySqlGson.getGson().fromJson(paymentsData, new TypeToken<List<Finance_payment_method>>(){}.getType());
                        volleyCallbackArrayList.onSuccess(payment_methods);
                    }
                }
            }
        });
    }
    public void getUserPermissions(Integer id_user, final VolleyCallbackObject callbackObject) {
        URL url = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.PERMISSIONS, id_user, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Users_permissions usersPermissions = MySqlGson.getGson().fromJson(data, Users_permissions.class);
                            callbackObject.onSuccess(usersPermissions);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackObject.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackObject.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }

    public void showAddTelefonoModal(Owner owner, IProgressBarActivity activity, VolleyCallbackOwner callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getContext());
        //builder.setTitle(R.string.agregar_movil);
        builder.setMessage(R.string.Agregar_telefono_modal);
        final EditText input = new EditText(activity.getContext());
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setSingleLine();
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        FrameLayout container = new FrameLayout(activity.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String telefono = input.getText().toString();
                if (PhoneNumberUtils.isGlobalPhoneNumber(telefono)) {
                    owner.setPhone(telefono);
//                    owner.setid_vet(DoctorVetApp.get().getVet().getId());
                    activity.showProgressBar();
                    updateOwner(owner, NetworkUtils.OwnersUpdateType.UPDATE_PHONE, new VolleyCallbackOwner() {
                        @Override
                        public void onSuccess(Owner resultOwner) {
                            activity.hideProgressBar();
                            if (resultOwner != null) {
                                //activity.refreshContent();
                                callback.onSuccess(resultOwner);
                            } else {
                                callback.onSuccess(null);
                            }
                        }
                    });
                } else {
                    Toast.makeText(activity.getContext(), R.string.telefono_invalido, Toast.LENGTH_SHORT).show();
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
                closeKeyboard();
            }
        });
        builder.show();
        input.requestFocus();
        showKeyboard();
    }
    public void showAddEmailModal(Owner owner, IProgressBarActivity activity, VolleyCallbackOwner callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getContext());
        builder.setMessage(R.string.Agregar_email_modal);
        final EditText input = new EditText(activity.getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        input.setSingleLine();
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        FrameLayout container = new FrameLayout(activity.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        input.setLayoutParams(params);
        container.addView(input);
        builder.setView(container);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String email = input.getText().toString();
                if (HelperClass.validateEmail(email)) {
                    owner.setEmail(email);
//                    owner.setid_vet(DoctorVetApp.get().getVet().getId());
                    activity.showProgressBar();
                    updateOwner(owner, NetworkUtils.OwnersUpdateType.UPDATE_EMAIL, new VolleyCallbackOwner() {
                        @Override
                        public void onSuccess(Owner resultOwner) {
                            activity.hideProgressBar();
                            if (resultOwner != null) {
                                //activity.refreshContent();
                                callback.onSuccess(resultOwner);
                            } else {
                                callback.onSuccess(null);
                            }
                        }
                    });
                } else {
                    Toast.makeText(activity.getContext(), R.string.email_invalido, Toast.LENGTH_SHORT).show();
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
                closeKeyboard();
            }
        });
        builder.show();
        input.requestFocus();
        showKeyboard();
    }
    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) get().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    public boolean isSoftKeyboardVisible(View view) {
        if (view == null)
            return false;

        androidx.core.view.WindowInsetsCompat w = ViewCompat.getRootWindowInsets(view);
        if (w == null)
            return false;

        return w.isVisible(WindowInsetsCompat.Type.ime());
    }
    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) get().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
    public void updateOwner(Owner owner, NetworkUtils.OwnersUpdateType updateType, VolleyCallbackOwner callback) {

        final String owner_json_object = MySqlGson.postGson().toJson(owner);

        URL url = NetworkUtils.buildOwnerUrl(owner.getId(), updateType, null);

        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Owner response_owner = MySqlGson.getGson().fromJson(data, Owner.class);
                    callback.onSuccess(response_owner);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callback.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(null);
            }
        }) {
            @Override
            public byte[] getBody() {
                return owner_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public Integer get_autocomplete_selected_value_id(String editTextName, String editTextValue, IGetIdByValueAdapter iGetIdByValueAdapter) {
        Integer id = null;
        id = iGetIdByValueAdapter.getIdByValue(editTextValue);
        return id;
    }
    public void deletePet_supply(Integer id_pet_supply, VolleyCallback callback) {
        URL delete_suministroUrl = NetworkUtils.buildDeleteSupplyUrl(id_pet_supply);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_suministroUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callback.onSuccess(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callback.onSuccess(false);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void checkSupply(Integer id_pet_suministro, VolleyCallback callback) {
        String mascota_suministro_json_object = MySqlGson.getGson().toJson(new Pet_supply(id_pet_suministro));
        URL checkSuministroUrl = NetworkUtils.buildCheckSupplyUrl(id_pet_suministro);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, checkSuministroUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callback.onSuccess(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(false);
            }
        }) {
            @Override
            public byte[] getBody() {
                return mascota_suministro_json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void accept_movement(Integer idMovement, VolleyCallback callback) {
        URL pdfMovementAccept = NetworkUtils.buildMovementsUrl(NetworkUtils.MovementsUrlEnum.ACCEPT_MOVEMENT, idMovement, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, pdfMovementAccept.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success")) {
                        callback.onSuccess(true);
                    } else {
                        callback.onSuccess(false);
                    }
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callback.onSuccess(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(false);
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void checkAgenda(Integer id_agenda, VolleyCallback callback) {
        String url = NetworkUtils.buildAgendaCheckUrl(id_agenda).toString();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success")) {
                        callback.onSuccess(true);
                    } else {
                        callback.onSuccess(false);
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    callback.onSuccess(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                callback.onSuccess(false);
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void deleteAgenda(Integer id_agenda, VolleyCallback callback) {
        URL delete_agendaUrl = NetworkUtils.buildDeleteAgendaUrl(id_agenda);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, delete_agendaUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getStatusFromResponse(response);
                            if (data.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                            callback.onSuccess(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        callback.onSuccess(false);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void rescheduleAgenda(Integer id_agenda, String begin_time, VolleyCallback callback) {
        String url = NetworkUtils.buildAgendaRescheduleUrl(id_agenda, begin_time).toString();
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String success = MySqlGson.getStatusFromResponse(response);
                    if (success.equalsIgnoreCase("success")) {
                        callback.onSuccess(true);
                    } else {
                        callback.onSuccess(false);
                    }
                } catch (Exception ex) {
                    DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                    callback.onSuccess(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DoctorVetApp.get().handle_volley_error(error, TAG, true);
                callback.onSuccess(false);
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void requestFocusAndShowKeyboard_noModal(TextInputLayout input) {
        input.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
    }
    public void getSymptomsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL symptomsUrl = NetworkUtils.buildGetSymptomsUrl(search, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, symptomsUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Symptom.Get_pagination_symptoms response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Symptom.Get_pagination_symptoms>(){}.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getClinic2ForInputObject(final VolleyCallbackObject volleyCallbackObject) {
        getLastUpdateObj("pets_clinic_2_for_input", new VolleyCallbackObject() {
            @Override
            public void onSuccess(Object resultObject) {
                if (resultObject != null) {
                    Update_table update_table = (Update_table) resultObject;
                    Date lastTableDateServer = update_table.getUpdate_time();
                    Date lastTableDateLocal = HelperClass.getDatetime(getPreference("pets_clinic_2_for_input_last_update"));
                    Boolean existsRegionsCacheFile = existsCacheFile("pets_clinic_2_for_input");
                    Boolean mustDoRequest = !existsRegionsCacheFile || (lastTableDateServer.after(lastTableDateLocal));

                    if (mustDoRequest) {
                        URL petsClinic2ForInput = NetworkUtils.buildGetClinic2ForInputrl();
                        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, petsClinic2ForInput.toString(),
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            String data = MySqlGson.getDataFromResponse(response).getAsString();
                                            String json = HelperClass.compressedBase64ToString(data);
                                            DoctorVetApp.get().writeToDisk("pets_clinic_2_for_input", json);
                                            setPreference("pets_clinic_2_for_input_last_update", HelperClass.getDateTimeForMySQL(update_table.getUpdate_time()));
                                            Pet_clinic2.Clinic2ForInput clinic2_for_input = MySqlGson.getGson().fromJson(json, Pet_clinic2.Clinic2ForInput.class);
                                            volleyCallbackObject.onSuccess(clinic2_for_input);
                                        } catch (Exception ex) {
                                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                                            volleyCallbackObject.onSuccess(null);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                                        volleyCallbackObject.onSuccess(null);
                                    }
                                }
                        );
                        addToRequestQueque(stringRequest);
                    } else {
                        String petsClinic2ForInputData = DoctorVetApp.get().readFromDisk("pets_clinic_2_for_input");
                        Pet_clinic2.Clinic2ForInput clinic2_for_input = MySqlGson.getGson().fromJson(petsClinic2ForInputData, Pet_clinic2.Clinic2ForInput.class);
                        volleyCallbackObject.onSuccess(clinic2_for_input);
                    }
                }
            }
        });
    }
    public void getDiagnosticsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL diagnosticsUrl = NetworkUtils.buildGetDiagnosticsUrl(search, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, diagnosticsUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Diagnostic.Get_pagination_diagnostics response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Diagnostic.Get_pagination_diagnostics>(){}.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getTreatmentsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL treatmentsUrl = NetworkUtils.buildGetTreatmentsUrl(search, page);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, treatmentsUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Treatment.Get_pagination_treatments response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Treatment.Get_pagination_treatments>(){}.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getPossibleDiagnosticsAdapter(ArrayList<Symptom> symptoms, final VolleyCallbackAdapter callbackAdapter) {
        final URL possibleDiagnosticsUrl = NetworkUtils.buildGetPossibleDiagnosticsUrl(symptoms);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, possibleDiagnosticsUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    ArrayList<Diagnostic> diagnostics = MySqlGson.getGson().fromJson(data, new TypeToken<List<Diagnostic>>(){}.getType());
                    DiagnosticsAdapter diagnosticsAdapter = new DiagnosticsAdapter(diagnostics);
                    callbackAdapter.onSuccess(diagnosticsAdapter);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackAdapter.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackAdapter.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getVetStructuresMinAdapter(final VolleyCallbackAdapter callbackAdapter) {
        final URL vetStructureMinUrl = NetworkUtils.buildGetVetStructureMinUrl();
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, vetStructureMinUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    //String json = HelperClass.compressedBase64ToString(response);
                    ArrayList<Vet_structure> vetStructures = MySqlGson.getGson().fromJson(data, new TypeToken<List<Vet_structure>>(){}.getType());
                    VetsStructuresAdapter vetsStructuresAdapter = new VetsStructuresAdapter(vetStructures);
                    callbackAdapter.onSuccess(vetsStructuresAdapter);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackAdapter.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackAdapter.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getUsersPagination(String search, Integer page, final VolleyCallbackPagination callbackAdapter) {
        URL usersUrl = NetworkUtils.buildGetUsersUrl(search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, usersUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            User.Get_pagination_users response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<User.Get_pagination_users>() {
                            }.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getPetsDashboardAdapter(final VolleyCallbackObject callbackObject) {
        final URL petsDashboardUrl = NetworkUtils.buildGetPetsDashboardUrl();
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, petsDashboardUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Dashboard dashboard = MySqlGson.getGson().fromJson(data, new TypeToken<Dashboard>(){}.getType());
                    callbackObject.onSuccess(dashboard);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackObject.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackObject.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getVetsPagination(String search, Integer page, final VolleyCallbackPagination callbackAdapter) {
        URL vetsUrl = NetworkUtils.buildGetVetsForRequestUrl(search, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, vetsUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Vet.Get_pagination_vets response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Vet.Get_pagination_vets>() {
                            }.getType());
                            callbackAdapter.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackAdapter.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackAdapter.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getAgendaPagination(Date selectedDate, Integer page, final VolleyCallbackPagination callbackPagination) {
        URL agendaUrl = NetworkUtils.buildGetAgendaUrl(selectedDate, page);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, agendaUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getDataFromResponse(response).toString();
                            Agenda.Get_pagination_agendas response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Agenda.Get_pagination_agendas>() {
                            }.getType());
                            callbackPagination.onSuccess(response_obj);
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callbackPagination.onSuccess(null);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                        callbackPagination.onSuccess(null);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }
    public void getStudyItemsPagination(String search, Integer page, final VolleyCallbackPagination callbackPagination) {
        final URL symptomsUrl = NetworkUtils.buildStudyUrl(null, null, search, page, false);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, symptomsUrl.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    Pet_study_item.Get_pagination_study_items response_obj = MySqlGson.getGson().fromJson(data, new TypeToken<Pet_study_item.Get_pagination_study_items>(){}.getType());
                    callbackPagination.onSuccess(response_obj);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callbackPagination.onSuccess(null);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callbackPagination.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }

    public void postUserNotificationToken(String token, VolleyCallback callback) {
        String json_object = "{ \"token\": \"" + token + "\" }";
        URL url = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.SET_NOTIFICATION_TOKEN, null, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.POST, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                            callback.onSuccess(false);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(false);
            }
        }) {
            @Override
            public byte[] getBody() {
                return json_object.getBytes();
            }
        };
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void deleteUserNotificationToken(String token, VolleyCallback callback) {
        URL url = NetworkUtils.buildUsersDeleteNotificationUrl(token);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String data = MySqlGson.getStatusFromResponse(response);
                            if (data.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            handle_onResponse_error(ex, TAG, true, response);
                            callback.onSuccess(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        handle_volley_error(error, TAG, true);
                        callback.onSuccess(false);
                    }
                }
        );
        addToRequestQueque(stringRequest);
    }


    public String preferences_getUserEmail() {
        return sharedPreferences.getString(getString(R.string.preference_user_email), "");
    }
    public login_types preferences_getUserLoginType() {
        String login_type = sharedPreferences.getString(getString(R.string.preference_user_login_type), "");
        if (login_type.isEmpty())
            return null;

        return login_types.valueOf(login_type);
    }
    public String preferences_get_user_password() {
        String password = "";
        String passwordBase64Encoded = sharedPreferences.getString(getString(R.string.preference_user_password), "");
        if (!passwordBase64Encoded.isEmpty()) {
            try {
                password = new CryptoManager().decrypt(passwordBase64Encoded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return password;
    }
    public String preferences_getUserToken() {
        String token = "";
        String tokenBase64Encoded = sharedPreferences.getString(getString(R.string.preference_user_token), "");
        if (!tokenBase64Encoded.isEmpty()) {
            try {
                token = new CryptoManager().decrypt(tokenBase64Encoded);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return token;
    }
    public void preferences_setLoginInfo(@Nullable String email, @Nullable String password, @Nullable login_types loginType, @Nullable String token) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (email != null)
            editor.putString(getApplicationContext().getString(R.string.preference_user_email), email);

        if (loginType != null)
            editor.putString(getApplicationContext().getString(R.string.preference_user_login_type), loginType.name());

        //in google or facebook sigin, password equals null
        if (password != null && !password.isEmpty()) {
            try {
                String passwordEncrypted = new CryptoManager().encrypt(password);
                editor.putString(getApplicationContext().getString(R.string.preference_user_password), passwordEncrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (token != null && !token.isEmpty()) {
            try {
                String tokenEncrypted = new CryptoManager().encrypt(token);
                editor.putString(getApplicationContext().getString(R.string.preference_user_token), tokenEncrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        editor.apply();
    }
    public void preferences_deleteLoginInfo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getApplicationContext().getString(R.string.preference_user_email), "");
        editor.putString(getApplicationContext().getString(R.string.preference_user_password), "");
        editor.putString(getApplicationContext().getString(R.string.preference_user_login_type), "");
        editor.putString(getApplicationContext().getString(R.string.preference_user_token), "");

        //fcm token
        editor.putString("user_notification_token", "");
        FirebaseMessaging.getInstance().deleteToken();

        editor.apply();
    }
    public void preferences_set_owners_view_mode(DoctorVetApp.Adapter_types type) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.preference_owners_view_mode), type.toString().toUpperCase());
        editor.apply();
    }
    public DoctorVetApp.Adapter_types preferences_get_owners_view_mode() {
        String adapterType = DoctorVetApp.Adapter_types.EXTENDED.toString();
        adapterType = sharedPreferences.getString(getString(R.string.preference_owners_view_mode), adapterType);
        return DoctorVetApp.Adapter_types.valueOf(adapterType);
    }
    public void preferences_set_user(String data) {
        //for vet changes / update of vets or users data
        this.user = null;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("user", data);
        editor.apply();
    }
    public String preferences_get_user() {
        return sharedPreferences.getString("user", "");
    }
    public String getPreference(String name) {
        return sharedPreferences.getString(name, "");
    }
    public void setPreference(String name, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.apply();
    }
    public boolean existsLocalUserNotificationToken() {
        return !sharedPreferences.getString("user_notification_token", "").isEmpty();
    }
    public String getLocalUserNotificationToken() {
        return getPreference("user_notification_token");
    }
    public void setLocalUserNotificationToken(String token) {
        setPreference("user_notification_token", token);
    }
    public void firebaseGetNotificationToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            if (task.getException() != null)
                                handle_error(task.getException(), "firebaseGetNotificationToken", SHOW_ERROR_MESSAGE);
                        } else {
                            String token = task.getResult();
                            DoctorVetApp.get().postUserNotificationToken(token, new DoctorVetApp.VolleyCallback() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    DoctorVetApp.get().setLocalUserNotificationToken(token);
                                }
                            });
                        }
                    }
                });
    }

    //Volley
    public RequestQueue getRequestQueque() {
        if (this.requestQueue == null)
            this.requestQueue = Volley.newRequestQueue(getApplicationContext());

        return requestQueue;
    }
    public <T> void addToRequestQueque(Request<T> request) {
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getRequestQueque().add(request);
    }
    public void uploadFiles(ArrayList<Resource> resources, DoctorVetApp.VolleyCallback volleyCallback) {
        Integer upload_count = resources.size();
        final Integer[] upload_count_real = {0};

        for (Resource resource : resources) {
            android.util.Log.d(TAG, MySqlGson.postGson().toJson(resource));

            TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.POST, NetworkUtils.buildS3UploadPrepareUrl().toString(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    String data = MySqlGson.getDataFromResponse(response).getAsString();
                    if (data.equalsIgnoreCase(resource.getUnique_id())) {
                        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, NetworkUtils.buildS3UploadUrl().toString(), new Response.Listener<NetworkResponse>() {
                            @Override
                            public void onResponse(NetworkResponse response) {
                                String response_str = new String(response.data);
                                String data = MySqlGson.getDataFromResponse(response_str).getAsString();
                                if (data.equalsIgnoreCase("1")) {
                                    upload_count_real[0]++;
                                    if (upload_count == upload_count_real[0])
                                        volleyCallback.onSuccess(true);
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                handle_volley_error(error, TAG, true);
                                volleyCallback.onSuccess(false);
                            }
                        })
                        {
                            @Override
                            protected Map<String, DataPart> getByteData() {
                                Map<String, DataPart> params = new HashMap<>();
                                params.put("file", new DataPart(resource.getFile_name(), resource.getByteArray()));
                                return params;
                            }

                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<>();
                                params.put("context", MySqlGson.postGson().toJson(resource));
                                return params;
                            }
                        };
                        addToRequestQueque(volleyMultipartRequest);
                    } else {
                        handle_error(new Exception("File upload guid fail"), TAG, true);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    handle_volley_error(error, TAG, true);
                    volleyCallback.onSuccess(false);
                }
            })
            {
                @Override
                public byte[] getBody() {
                    String context_json_object = MySqlGson.postGson().toJson(resource);
                    return context_json_object.getBytes();
                }
            };
            addToRequestQueque(tokenStringRequest);
        }
    }

    public void handleGeneralBottomSheetClick(BottomSheetDialog.Buttons buttonClicked, Context ctx) {
        Intent intent;
        switch (buttonClicked) {
            case PETS_SEARCH:
                intent = new Intent(ctx, SearchPetActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                ctx.startActivity(intent);
                break;
            case PETS_NEW:
                intent = new Intent(ctx, EditPetActivity.class);
                ctx.startActivity(intent);
                break;
            case OWNERS_SEARCH:
                intent = new Intent(ctx, SearchOwnerActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                ctx.startActivity(intent);
                break;
            case OWNERS_NEW:
                intent = new Intent(ctx, EditOwnerActivity.class);
                ctx.startActivity(intent);
                break;
            case PRODUCTS_SEARCH:
                intent = new Intent(ctx, SearchProductActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                ctx.startActivity(intent);
                break;
            case PRODUCTS_NEW:
                intent = new Intent(ctx, EditProductActivity.class);
                ctx.startActivity(intent);
                break;
            case PRODUCTS_NEW_SERVICE:
                intent = new Intent(ctx, EditServiceActivity.class);
                ctx.startActivity(intent);
                break;
            case PRODUCTS_ASSOC:
                intent = new Intent(ctx, EditProductAssocActivity.class); //EditProductActivity_1.class);
                ctx.startActivity(intent);
                break;
            case SELLS_NEW:
                intent = new Intent(ctx, EditSellActivity_1.class);
                ctx.startActivity(intent);
                break;
            case PROVIDER_NEW:
                intent = new Intent(ctx, EditProviderActivity.class);
                ctx.startActivity(intent);
                break;
            case SPENDING_NEW:
                intent = new Intent(ctx, EditSpendingActivity.class);
                ctx.startActivity(intent);
                break;
            case MANUAL_IN_OUT:
                intent = new Intent(ctx, EditManualCashActivity.class);
                ctx.startActivity(intent);
                break;
            case PURCHASES_NEW:
                intent = new Intent(ctx, EditPurchaseActivity_1.class);
                ctx.startActivity(intent);
                break;
            case IMPORT_NEW:
                intent = new Intent(ctx, EditImportActivity_1.class);
                ctx.startActivity(intent);
                break;
            case AGENDA_NEW:
                intent = new Intent(ctx, EditAgendaActivity.class);
                ctx.startActivity(intent);
                break;
            case PROVIDERS_SEARCH:
                intent = new Intent(ctx, SearchProviderActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                ctx.startActivity(intent);
                break;
            case MANUFACTURERS_SEARCH:
                intent = new Intent(ctx, SearchManufacturerActivity.class);
                intent.putExtra(DoctorVetApp.INTENT_SEARCH_VIEW, true);
                ctx.startActivity(intent);
                break;
            case MOVEMENTS_NEW:
                intent = new Intent(ctx, EditMovementActivity_1.class);
                ctx.startActivity(intent);
                break;
        }
    }
    public void handle_volley_error(VolleyError error, String tag, boolean showToast) {
        String responseError = "";
        String statusCode = "";
        if (error instanceof TimeoutError) {
            responseError = getApplicationContext().getString(R.string.error_conexion_servidor);
        } else {
            if (error.networkResponse != null) {
                //error, codigo + response message
                statusCode = String.valueOf(error.networkResponse.statusCode);
                responseError = new String(error.networkResponse.data);
                //try to get mesage from server, remember that it could be an error outside server messages
                if (isValidJson(responseError)) {
                    responseError = new Gson().fromJson(responseError, JsonObject.class).get("message").getAsString();
                }
            } else {
                responseError = error.getMessage();
                if (responseError == null)
                    responseError = error.toString();
            }
        }
        Log.e(tag, statusCode + " " + responseError);
        if (showToast)
            Toast.makeText(getApplicationContext(), responseError, Toast.LENGTH_LONG).show();
    }
    public void handle_onResponse_error(Exception error, String tag, boolean showToast, String response) {
        String responseError = error.toString();
        if (responseError == null)
            responseError = error.getMessage();
        if (response != null)
            responseError += ". Response: " + response;
        Log.e(tag, responseError, error);
        if (showToast)
            Toast.makeText(getApplicationContext(), responseError, Toast.LENGTH_LONG).show();
    }
    public void handle_error(Exception error, String tag, boolean showToast) {
        String responseError = error.toString();
        if (responseError == null)
            responseError = error.getMessage();
        Log.e(tag, responseError, error);
        if (showToast)
            Toast.makeText(getApplicationContext(),responseError, Toast.LENGTH_LONG).show();
    }
    public void handle_null_adapter(String db_table, String tag, boolean showToast) {
        String error_toast = "Se perdió la sincronización con el servidor";
        Log.e(tag, error_toast + "_" + db_table + "_" + tag);
        if (showToast)
            Toast.makeText(getApplicationContext(),error_toast, Toast.LENGTH_LONG).show();
    }
    private boolean isValidJson(String json) {
        try {
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void setLastVisit(Date lastVisit, TextView txt) {
        //for today dates show only time. Others shows with short date
        if (lastVisit != null) {
            if (DateUtils.isToday(lastVisit.getTime())) {
                txt.setText(HelperClass.getShortTime_inStr(lastVisit, txt.getContext()));
            } else {
                txt.setText(HelperClass.getShortDate_inStr(lastVisit, txt.getContext()));
            }
        }
    }

    public void setOnTouchToShowDropDown(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoCompleteTextView.showDropDown();
            }
        });
    }
    public void setAllWidthToDropDown(AutoCompleteTextView autoCompleteTextView, Activity activity) {
        Point pointSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(pointSize);
        autoCompleteTextView.setDropDownWidth(pointSize.x);
    }

    public static void TextInputLayoutToObject(View view, Object o, boolean emptyToNull, String textInputLayoutPrefixName, boolean forSql, Context ctx) {
        if (o == null) return;

        String editTextName;
        String editTextValue;
        TextInputLayout editText;
        ViewGroup vgAux;

        if (view instanceof TextInputLayout) {
            //variables
            Field field = null;
            editText = (TextInputLayout) view;
            editTextName = editText.getResources().getResourceName(editText.getId());
            int index = editTextName.indexOf(":id/") + 4;
            editTextName = editTextName.substring(index + textInputLayoutPrefixName.length());
            editTextValue = editText.getEditText().getText().toString().trim();
            if (emptyToNull && editTextValue.isEmpty())
                editTextValue = null;

            if (editText.getEditText() instanceof AutoCompleteTextView) {
                //recordar que listas con names duplicados como mascotas o propietarios no pueden funcionar de este modo
                Object posibleAdapter = editText.getEditText().getTag();
                if (posibleAdapter != null && posibleAdapter instanceof DoctorVetApp.IGetIdByValueAdapter) {
                    try {
                        field = o.getClass().getDeclaredField(editTextName);
                        field.setAccessible(true);
                        DoctorVetApp.IGetSelectedObject adapter = (DoctorVetApp.IGetSelectedObject) ((AutoCompleteTextView) editText.getEditText()).getTag();
                        field.set(o, adapter.getObjectByName(editTextValue));
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                //get object field
                try {
                    field = o.getClass().getDeclaredField(editTextName);
                    field.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                    return;
                }

                //assign value
                try {
                    if (editTextValue == null) {
                        field.set(o, null);
                    } else {
                        if (field.getType().getSimpleName().toLowerCase().equals("date")) {
                            //field.set(o, HelperClass.getShortDate(editTextValue, ctx));
                            String time = HelperClass.getTimeInLocale(Calendar.getInstance().getTime(), ctx);
                            field.set(o, HelperClass.getShortDateTime(editTextValue + " " + time, ctx));
                        } else if (field.getType().getSimpleName().toLowerCase().equals("integer")) {
                            field.set(o, Integer.parseInt(editTextValue));
                        } else if (field.getType().getSimpleName().toLowerCase().equals("bigdecimal")) {
                            field.set(o, BigDecimal.valueOf(Double.parseDouble(editTextValue)));
                        } else {
                            field.set(o, editTextValue);
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else if (view instanceof AppCompatSpinner) {
            Spinner spinner = (Spinner)view;
            editTextName = spinner.getResources().getResourceName(spinner.getId());

            if (!editTextName.startsWith(textInputLayoutPrefixName))
                return;

            int index = editTextName.indexOf(":id/") + 4;
            editTextName = editTextName.substring(index + textInputLayoutPrefixName.length());
            editTextValue = spinner.getSelectedItem().toString(); //String.valueOf(spinner.getSelectedItemId());
            if (emptyToNull && editTextValue.isEmpty()) return;
            Field field = null;
            try {
                field = o.getClass().getDeclaredField(editTextName);
                field.setAccessible(true);

                if (field.getType().getSimpleName().toLowerCase().equals("integer")) {
                    DoctorVetApp.IGetIdByValueAdapter adapter =  (DoctorVetApp.IGetIdByValueAdapter)spinner.getTag();
                    field.set(o, DoctorVetApp.get().get_autocomplete_selected_value_id(editTextName, editTextValue, adapter));//Integer.parseInt(editTextValue));
                } else {
                    field.set(o, editTextValue);
                }

                //intento asignnar a name
                //cambio id_ por name_
                editTextName = editTextName.replace("id_", "name_");
                editTextValue = spinner.getSelectedItem().toString();
                field = o.getClass().getDeclaredField(editTextName);
                field.setAccessible(true);
                field.set(o, editTextValue);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (view instanceof ViewGroup) {
            vgAux = (ViewGroup) view;
            for (int i = 0; i < vgAux.getChildCount(); i++) {
                View v = vgAux.getChildAt(i);
                if (v instanceof ViewGroup) {
                    TextInputLayoutToObject((ViewGroup) v, o, emptyToNull, textInputLayoutPrefixName, forSql, ctx);
                }
            }
        }
    }
    public static void ObjectToTextInputLayout(View view, Object o, String textInputLayoutPrefixName) {
        if (o == null) return;

        String editTextName;
        String editTextValue;
        TextInputLayout editText;
        ViewGroup vgAux;

        if (view instanceof TextInputLayout) {
            editText = (TextInputLayout) view;
            editTextName = editText.getResources().getResourceName(editText.getId());
            int index = editTextName.indexOf(":id/") + 4;
            editTextName = editTextName.substring(index + textInputLayoutPrefixName.length());

            if (HelperClass.doesObjectContainField(o, editTextName)) {
                Field field = null;
                try {
                    field = o.getClass().getDeclaredField(editTextName);
                    field.setAccessible(true);
                    Object objAux = field.get(o);
                    if (objAux != null) {
                        if (field.getType().getSimpleName().toLowerCase().equals("date")) {
                            editText.getEditText().setText(HelperClass.getDateInLocale((Date)objAux, view.getContext())); /*HelperClass.getDateInLocaleShort((Date) objAux)*/
                        } else {
                            editText.getEditText().setText(objAux.toString());
                        }
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else if (view instanceof ViewGroup) {
            vgAux = (ViewGroup) view;
            for (int i = 0; i < vgAux.getChildCount(); i++) {
                View v = vgAux.getChildAt(i);
                if (v instanceof ViewGroup) {
                    ObjectToTextInputLayout((ViewGroup) v, o, textInputLayoutPrefixName);
                }
            }
        }
    }
    public static void ObjectToTextView(View view, Object o, String textViewPrefixName, boolean dateInShortFormat) {
        if (o == null) return;

        String textViewName;
        String textViewValue;
        TextView textView;
        ViewGroup vgAux;

        //checkbox primero porque los checks son instancias de textbox tambien
        if (view instanceof CheckBox) {
            CheckBox chk = (CheckBox) view;
            if (chk.getId() == -1) return;
            String chkViewName = chk.getResources().getResourceName(chk.getId());
            int index = chkViewName.indexOf(":id/") + 4;
            chkViewName = chkViewName.substring(index + textViewPrefixName.length());

            if (HelperClass.doesObjectContainField(o, chkViewName)) {
                Field field = null;
                try {
                    field = o.getClass().getDeclaredField(chkViewName);
                    field.setAccessible(true);
                    Object objAux = field.get(o);
                    if (objAux != null) {
                        if (objAux.toString().equals("1"))
                            chk.setChecked(true);
                        else
                            chk.setChecked(false);
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else if (view instanceof TextView) {
            textView = (TextView) view;
            if (view.getId() == -1) return;
            textViewName = textView.getResources().getResourceName(textView.getId());
            int index = textViewName.indexOf(":id/") + 4;
            textViewName = textViewName.substring(index + textViewPrefixName.length());

            if (HelperClass.doesObjectContainField(o, textViewName)) {
                Field field = null;
                try {
                    field = o.getClass().getDeclaredField(textViewName);
                    field.setAccessible(true);
                    Object objAux = field.get(o);
                    if (objAux != null) {
                        if (field.getType().getSimpleName().toLowerCase().equals("date")) {
                            if (dateInShortFormat)
                                textView.setText(HelperClass.getDateInLocaleShort((Date) objAux));
                            else
                                textView.setText(HelperClass.getDateTimeInLocaleShort((Date) objAux));
                        } else {
                            textView.setText(objAux.toString());
                        }
                    } else {
                        textView.setText("");
                    }
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else if (view instanceof ViewGroup) {
            vgAux = (ViewGroup) view;
            for (int i = 0; i < vgAux.getChildCount(); i++) {
                View v = vgAux.getChildAt(i);
                ObjectToTextView(v, o, textViewPrefixName, dateInShortFormat);
            }
        }

    }
    public static void ObjectToTextView(View view, Object o, String textViewPrefixName) {
        ObjectToTextView(view, o, textViewPrefixName, false);
    }

//    public static void invisibilizeEmptyTextView(LinearLayout linearLayoutCompat) {
//        int count = linearLayoutCompat.getChildCount();
//        int count2 = linearLayoutCompat.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View possibleTextview = linearLayoutCompat.getChildAt(i);
//            if (possibleTextview instanceof TextView) {
//                if (possibleTextview.getId() == -1) continue;
//                String possibleTextviewName = possibleTextview.getResources().getResourceName(possibleTextview.getId());
//                int index = possibleTextviewName.indexOf(":id/") + 4;
//                possibleTextviewName = possibleTextviewName.substring(index);
//
//                if (possibleTextviewName.startsWith("txt_") && ((TextView)possibleTextview).getText().toString().isEmpty()) {
//                    String textViewName = possibleTextviewName.substring(4);
//                    for (int j = 0; j < count; j++) {
//                        View possibleTextviewLabel = linearLayoutCompat.getChildAt(j);
//                        if (possibleTextviewLabel instanceof TextView) {
//                            if (possibleTextviewLabel.getId() == -1) continue;
//                            String possibleTextviewLabelName = possibleTextviewLabel.getResources().getResourceName(possibleTextviewLabel.getId());
//                            int index2 = possibleTextviewLabelName.indexOf(":id/") + 4;
//                            possibleTextviewLabelName = possibleTextviewLabelName.substring(index2);
//
//                            if (possibleTextviewLabelName.startsWith("label_")) {
//                                String labelName = possibleTextviewLabelName.substring(6);
//                                if (textViewName.equalsIgnoreCase(labelName)) {
//                                    possibleTextview.setVisibility(View.GONE);
//                                    possibleTextviewLabel.setVisibility(View.GONE);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//    public static void visibilizeNonEmptyTextView(LinearLayout linearLayoutCompat) {
//        int count = linearLayoutCompat.getChildCount();
//        int count2 = linearLayoutCompat.getChildCount();
//        for (int i = 0; i < count; i++) {
//            View possibleTextview = linearLayoutCompat.getChildAt(i);
//            if (possibleTextview instanceof TextView) {
//                if (possibleTextview.getId() == -1) continue;
//                String possibleTextviewName = possibleTextview.getResources().getResourceName(possibleTextview.getId());
//                int index = possibleTextviewName.indexOf(":id/") + 4;
//                possibleTextviewName = possibleTextviewName.substring(index);
//
//                if (possibleTextviewName.startsWith("txt_") && !(((TextView)possibleTextview).getText().toString().isEmpty())) {
//                    String textViewName = possibleTextviewName.substring(4);
//                    for (int j = 0; j < count; j++) {
//                        View possibleTextviewLabel = linearLayoutCompat.getChildAt(j);
//                        if (possibleTextviewLabel instanceof TextView) {
//                            if (possibleTextviewLabel.getId() == -1) continue;
//                            String possibleTextviewLabelName = possibleTextviewLabel.getResources().getResourceName(possibleTextviewLabel.getId());
//                            int index2 = possibleTextviewLabelName.indexOf(":id/") + 4;
//                            possibleTextviewLabelName = possibleTextviewLabelName.substring(index2);
//
//                            if (possibleTextviewLabelName.startsWith("label_")) {
//                                String labelName = possibleTextviewLabelName.substring(6);
//                                if (textViewName.equalsIgnoreCase(labelName)) {
//                                    possibleTextview.setVisibility(View.VISIBLE);
//                                    possibleTextviewLabel.setVisibility(View.VISIBLE);
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
    public static void setTextViewVisibility(LinearLayout linearLayoutCompat/*, int visibleType, boolean forEmptyTextviews*/) {
        int count = linearLayoutCompat.getChildCount();
        int count2 = linearLayoutCompat.getChildCount();
        for (int i = 0; i < count; i++) {
            View possibleTextview = linearLayoutCompat.getChildAt(i);
            if (possibleTextview instanceof TextView) {
                if (possibleTextview.getId() == -1) continue;
                String possibleTextviewName = possibleTextview.getResources().getResourceName(possibleTextview.getId());
                int index = possibleTextviewName.indexOf(":id/") + 4;
                possibleTextviewName = possibleTextviewName.substring(index);

                if (possibleTextviewName.startsWith("txt_") /*&& ((TextView)possibleTextview).getText().toString().isEmpty() == forEmptyTextviews*/) {
                    String textViewName = possibleTextviewName.substring(4);

                    if (((TextView)possibleTextview).getText().toString().isEmpty()) {
                        possibleTextview.setVisibility(View.GONE);
                    } else {
                        possibleTextview.setVisibility(View.VISIBLE);
                    }

                    for (int j = 0; j < count; j++) {
                        View possibleTextviewLabel = linearLayoutCompat.getChildAt(j);
                        if (possibleTextviewLabel instanceof TextView) {
                            if (possibleTextviewLabel.getId() == -1) continue;
                            String possibleTextviewLabelName = possibleTextviewLabel.getResources().getResourceName(possibleTextviewLabel.getId());
                            int index2 = possibleTextviewLabelName.indexOf(":id/") + 4;
                            possibleTextviewLabelName = possibleTextviewLabelName.substring(index2);

                            if (possibleTextviewLabelName.startsWith("label_")) {
                                String labelName = possibleTextviewLabelName.substring(6);
                                if (textViewName.equalsIgnoreCase(labelName)) {
                                    if (((TextView)possibleTextview).getText().toString().isEmpty()) {
                                        possibleTextviewLabel.setVisibility(View.GONE);
                                    } else {
                                        possibleTextviewLabel.setVisibility(View.VISIBLE);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //TODO: Siempre recordar que en servidor se debe establecer set global information_schema_stats_expiry=0
    // para que puedan verse los cambios en las tablas
    public interface VolleyCallback {
        void onSuccess(Boolean result);
    }
    public interface VolleyCallbackDailyCashDetails {
        void onSuccess(DailyCashDetails dailyCashDetails);
    }
    public interface VolleyCallbackAdapter {
        void onSuccess(RecyclerView.Adapter resultAdapter);
    }
    public interface VolleyCallbackPagination {
        void onSuccess(Get_pagination pagination);
    }
    public interface VolleyCallbackArrayList {
        void onSuccess(ArrayList resultList);
    }
    public interface VolleyCallbackObject {
        void onSuccess(Object resultObject);
    }
    public interface VolleyCallbackOwner {
        void onSuccess(Owner resultOwner);
    }
    public interface VolleyCallbackMascota {
        void onSuccess(Pet resultPet);
    }
    public interface VolleyCallbackSell {
        void onSuccess(Sell resultSell);
    }
    public interface VolleyCallbackPurchase {
        void onSuccess(Purchase resultPurchase);
    }
    public interface VolleyCallbackMovement {
        void onSuccess(Movement resultMovement);
    }
    public interface IResourceObject {
        void setThumb_url(String thumb_url);
        void setPhoto_url(String photo_url);
        String getPhoto_url();
        void setThumb_deleted(Integer value);
        ArrayList<Resource> getResources();
    }
    public interface IGetIdByValueAdapter {
        public Integer getIdByValue(String value);
    }
    public interface IGetSelectedObject {
        public Object getObjectByName(String name);
    }
    public interface IProgressBarActivity {
        public void showProgressBar();

        public void hideProgressBar();

        public Context getContext();
        //public void refreshContent();
    }
    public interface PlayableContent {
        void killMediaPlayer();
        MediaPlayer getMediaPlayer();
    }

    public void getLastUpdate(final String tableName, final VolleyCallbackObject callback) {
        URL url = NetworkUtils.buildGetSysLastUpdateUrl(tableName, null);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String data = MySqlGson.getDataFromResponse(response).toString();
                Update_table update_table = MySqlGson.getGson().fromJson(data, Update_table.class);
                callback.onSuccess(update_table);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void getLastUpdateObj(final String objectName, final VolleyCallbackObject callback) {
        URL url = NetworkUtils.buildGetSysLastUpdateUrl(null, objectName);
        TokenStringRequest tokenStringRequest = new TokenStringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String data = MySqlGson.getDataFromResponse(response).toString();
                Update_table update_table = MySqlGson.getGson().fromJson(data, Update_table.class);
                callback.onSuccess(update_table);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(null);
            }
        });
        addToRequestQueque(tokenStringRequest);
    }
    public void searchDateSetInit(TextInputLayout txtDate, FragmentManager fm) {
        Date init = Calendar.getInstance().getTime();
        if (HelperClass.isValidDate(txtDate.getEditText().getText().toString(), getBaseContext())) {
            init = HelperClass.getShortDate(txtDate.getEditText().getText().toString(), getBaseContext());
        }
        new DatePickerFragment(init).showDatePickerDialog(fm, txtDate);
    }
    public void searchTimeSetInit(TextInputLayout txtHour, FragmentManager fm) {
        Date init = Calendar.getInstance().getTime();
        if (HelperClass.isValidHour(txtHour.getEditText().getText().toString(), getBaseContext())) {
            init = HelperClass.getShortTime(txtHour.getEditText().getText().toString(), getBaseContext());
        }
        new TimePickerFragment(init).showTimePickerDialog(fm, txtHour);
    }
    public String toCurrency(BigDecimal amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setCurrency(Currency.getInstance(Locale.getDefault()));
        String numberFormattedToCurrency = formatter.format(amount);
        return numberFormattedToCurrency;
    }
    public static View getRootForSnack(Activity activity) {
        return activity.findViewById(android.R.id.content);
    }
    public void checkWaitingRooms(Integer id_waiting_rooms, VolleyCallback callback) {
        URL callAttendPetUrl = NetworkUtils.buildWaitingRoomsUrl(id_waiting_rooms);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.PUT, callAttendPetUrl.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                            callback.onSuccess(false);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        callback.onSuccess(false);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void deleteWaitingRooms(Integer id_waiting_rooms, VolleyCallback callback){
        URL url = NetworkUtils.buildWaitingRoomsUrl(id_waiting_rooms);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.DELETE, url.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            String success = MySqlGson.getStatusFromResponse(response);
                            if (success.equalsIgnoreCase("success")) {
                                callback.onSuccess(true);
                            } else {
                                //DoctorVetApp.get().handle_error(new Exception(getString(R.string.error_borrando_registro)), TAG, true);
                                callback.onSuccess(false);
                            }
                        } catch (Exception ex) {
                            DoctorVetApp.get().handle_onResponse_error(ex, TAG, true, response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        DoctorVetApp.get().handle_volley_error(error, TAG, true);
                        callback.onSuccess(false);
                    }
                }
        );
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public void dispatchTakeThumbIntent(EditBaseActivity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        /*"com.xionce.doctorvet.fileprovider"*/
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.getThumbCameraLauncher().launch(takePictureIntent);
            }
        }
    }
    public void dispatchOpenGalleryThumbIntent(EditBaseActivity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.getThumbOpenGalleryLauncher().launch(intent);
    }
    public void dispatchTakePictureIntent(EditBaseActivity activity) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                        /*"com.xionce.doctorvet.fileprovider"*/
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                activity.getCameraLauncher().launch(takePictureIntent);
            }
        }
    }
    public File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,".jpg", storageDir);
        DoctorVetApp.get().setPathToFile(image.getAbsolutePath());
        return image;
    }
    private File createVideoFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MP4_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File videoFile = File.createTempFile(imageFileName,".mp4", storageDir);
        DoctorVetApp.get().setPathToFile(videoFile.getAbsolutePath());
        return videoFile;
    }
    public void dispatchOpenGalleryIntent(EditBaseActivity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.getOpenGalleryLauncher().launch(intent);
    }
    public void dispatchOpenFileIntent(EditBaseActivity activity) {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.getOpenFileLauncher().launch(intent);
    }
    public void dispathRecordVideoIntent(EditBaseActivity activity) {
        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);

        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            File videoFile = null;
            try {
                videoFile = createVideoFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (videoFile != null) {
                Uri videoURI = FileProvider.getUriForFile(getApplicationContext(),
                        BuildConfig.APPLICATION_ID + ".provider",
                        videoFile);

                takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoURI);
                activity.startActivityForResult(takeVideoIntent, REQUEST_TAKE_VIDEO);
            }
        }
    }
    public void dispatchOpenGalleryIntentAnyApp(EditBaseActivity activity) {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});

        activity.startActivityForResult(chooserIntent, REQUEST_OPEN_GALLERY);
    }
    public Bitmap getThumb(Bitmap image) {
        Bitmap image_thumb = HelperClass.cropToSquare(image);
        Bitmap image_thumb_2 = Bitmap.createScaledBitmap(image_thumb, 128, 128, false);
        return image_thumb_2;
    }
    public String getPathToFile() {
        //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_private_file), Context.MODE_PRIVATE);
        return sharedPreferences.getString("pathToFile", "");
    }
    public void setPathToFile(String pathToFile) {
        //SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.preference_private_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pathToFile", pathToFile);
        editor.apply();
    }
    public void writeToDisk(String name, String data) {
        try {
            FileOutputStream fileout = openFileOutput(name, MODE_PRIVATE);
            OutputStreamWriter outputWriter = new OutputStreamWriter(fileout);
            outputWriter.write(data);
            outputWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readFromDisk(String name) {
        String data = "";

        try {
            FileInputStream fileIn = openFileInput(name);
            InputStreamReader inputRead = new InputStreamReader(fileIn);

            char[] inputBuffer = new char[fileIn.available()];
            int charRead;

            while ((charRead = inputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring = String.copyValueOf(inputBuffer, 0, charRead);
                data += readstring;
            }
            inputRead.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }
    private Boolean existsCacheFile(String name) {
        File file = new File(getApplicationContext().getFilesDir() + "/" + name);
        if (file.exists())
            return true;

        return false;
    }
    public void deleteAllCacheFiles() {
        File file = new File(getApplicationContext().getFilesDir() + "/regions");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/owners");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/pets_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/products");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/products_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/pets_recipes_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/sells_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/purchases_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/movements_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/vet_studies");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/regions");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/finance_types_payments");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/pets_clinic_2_for_input");
        file.delete();
    }
    public void deleteDepositRelatedForInputCacheFiles() {
        File file = new File(getApplicationContext().getFilesDir() + "/sells_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/purchases_for_input");
        file.delete();

        file = new File(getApplicationContext().getFilesDir() + "/movements_for_input");
        file.delete();
    }

    public void linkTempAndFinalFiles(ArrayList<Resource> pre_sync_resources, ArrayList<Resource> post_sync_resources) {
        //link temp files and final files
        for (Resource pre_sync_resource:pre_sync_resources) {
            Resource post_sync_resource = getResourceFromUniqueId(pre_sync_resource.getUnique_id(), post_sync_resources);
            if (post_sync_resource == null) continue;

            String old_path = pre_sync_resource.getLocal_path();
            String new_path = post_sync_resource.getLocal_path();
            File old_file = new File(old_path);
            File new_file = new File(new_path);
            old_file.renameTo(new_file);
        }
    }
    private Resource getResourceFromUniqueId(String unique_id, ArrayList<Resource> resources) {
        for (Resource resource:resources) {
            if (resource.getUnique_id().equalsIgnoreCase(unique_id))
                return resource;
        }

        return null;
    }
    public String getLocalFromUrl(String url) {
        String str_file_name = url.substring(url.lastIndexOf("/"));
        str_file_name = getExternalFilesDir(HelperClass.getAndroidDirType(str_file_name)).getAbsolutePath() + str_file_name;
        return str_file_name;
    }
    public void setThumb(String thumb_url, ImageView imageView, Integer notFoundDrawable) {
        if (thumb_url != null) {
            //on device search first. thumb
            String str_thumb_file_name = DoctorVetApp.get().getLocalFromUrl(thumb_url);
            if (HelperClass.fileExists(str_thumb_file_name))
                thumb_url = str_thumb_file_name;

            Glide.with(imageView.getContext()).load(thumb_url).apply(RequestOptions.fitCenterTransform()).apply(RequestOptions.circleCropTransform()).into(imageView);
        } else {
            Glide.with(imageView.getContext()).load(notFoundDrawable).apply(RequestOptions.fitCenterTransform()).into(imageView);
        }
    }
    public boolean validateExistence(TextInputLayout txtInput, Object obj, String propertyName, boolean null_empty_pass) {
        String input = txtInput.getEditText().getText().toString();
        if (input.isEmpty() && null_empty_pass) {
            txtInput.setError(null);
            return true;
        }

        if (obj == null) {
            txtInput.setError(getString(R.string.error_corrige_campo_no_existe));
            txtInput.requestFocus();
            handleScrollView(txtInput);
            return false;
        }

        try {
            Field field = obj.getClass().getDeclaredField(propertyName);
            field.setAccessible(true);
            Object value = field.get(obj);

            if (value == null || !input.equalsIgnoreCase(value.toString())) {
                txtInput.setError(getString(R.string.error_corrige_campo_no_existe));
                txtInput.requestFocus();
                handleScrollView(txtInput);
                return false;
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        txtInput.setError(null);
        return true;
    }
    public boolean userIsAdmin() {
        return !getUser().getRol().equalsIgnoreCase("USER");
    }

    //reCaptcha
    public void doCaptcha(Activity activity, final VolleyCallback callback) {
        SafetyNet.getClient(activity).verifyWithRecaptcha(BuildConfig.CAPTCHA_API_KEY) //no need to proxy
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse recaptchaTokenResponse) {
                        // Indicates communication with reCAPTCHA service was successful.
                        String userResponseToken = recaptchaTokenResponse.getTokenResult();
                        if (!userResponseToken.isEmpty()) {
                            URL url = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.VERIFY_CAPTCHA, null, null, null);

                            StringRequest request = new StringRequest(Request.Method.POST, url.toString(),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonObject = new JSONObject(response);
                                                if (jsonObject.getBoolean("success"))
                                                    callback.onSuccess(true);
                                            } catch (Exception ex) {
                                                DoctorVetApp.get().handle_error(ex, TAG, true);
                                                callback.onSuccess(false);
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            DoctorVetApp.get().handle_volley_error(error, TAG, true);
                                            callback.onSuccess(false);
                                        }
                                    })
                            {
                                @Override
                                public byte[] getBody() {
                                    JsonObject jsonObject = new JsonObject();
                                    jsonObject.addProperty("response", userResponseToken);
                                    String jsonObjectString = MySqlGson.getPostJsonString(jsonObject);
                                    return jsonObjectString.getBytes();
                                }
                            };

                            request.setRetryPolicy(new DefaultRetryPolicy(
                                    50000,
                                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

                            getRequestQueque().add(request);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        DoctorVetApp.get().handle_error(e, TAG, true);
                        callback.onSuccess(false);
                    }
                });
    }

    //users permissions
    public void setUsersPermissionsDueToVer16(final VolleyCallback callback) {
        URL url = NetworkUtils.buildUsersUrl(NetworkUtils.UsersUrlEnum.USER_AND_VET_BY_TOKEN, null, null, null);
        TokenStringRequest stringRequest = new TokenStringRequest(Request.Method.GET, url.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String data = MySqlGson.getDataFromResponse(response).toString();
                    DoctorVetApp.get().preferences_set_user(data);
                    callback.onSuccess(true);
                } catch (Exception ex) {
                    handle_onResponse_error(ex, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE, response);
                    callback.onSuccess(false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                handle_volley_error(error, TAG, DoctorVetApp.SHOW_ERROR_MESSAGE);
                callback.onSuccess(false);
            }
        });
        DoctorVetApp.get().addToRequestQueque(stringRequest);
    }
    public String getAgendaTitle() {
        if (getUser().getRol().equalsIgnoreCase("user"))
            return "Agenda personal";

        return "Agenda";
    }
    public String getDailyCashTitle() {
        if (getUser().getRol().equalsIgnoreCase("user"))
            return "Caja diaria personal";

        return "Caja diaria";
    }

    public View findViewByName(ViewGroup parent, String name) {
        int childCount = parent.getChildCount();
        View foundView = null;

        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            Integer viewId = childView.getId();
            if (viewId != -1)
            {
                String viewName = getResources().getResourceEntryName(viewId);

                if (viewName.equals(name)) {
                    foundView = childView;
                    break;
                }
            }

            // Si es un contenedor, busca también dentro de él recursivamente
            if (childView instanceof ViewGroup) {
                foundView = findViewByName((ViewGroup) childView, name);
                if (foundView != null) {
                    break;
                }
            }
        }

        return foundView;
    }

}
