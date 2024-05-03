package com.xionce.doctorvetServices.utilities;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

import com.xionce.doctorvetServices.BuildConfig;
import com.xionce.doctorvetServices.DoctorVetApp;
import com.xionce.doctorvetServices.data.Symptom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

public final class NetworkUtils {

    private static final String TAG = "NetworkUtils";

    public static String DOCTOR_VET_BASE_URL = BuildConfig.DOCTOR_VET_BASE_URL;
    public static String DOCTORVET_SOCKET_URL = BuildConfig.DOCTORVET_SOCKET_URL;

    private static final String DOCTORVET_ROOT_URL = DOCTOR_VET_BASE_URL + "api/v1/";
    private static final String OWNERS_URL = DOCTORVET_ROOT_URL + "owners";
    private static final String PETS_URL = DOCTORVET_ROOT_URL + "pets";
    private static final String PETS_RACES_URL = DOCTORVET_ROOT_URL + "pets_races";
    private static final String PETS_PELAGES_URL = DOCTORVET_ROOT_URL + "pets_pelages";
    private static final String PETS_GENDERS_URL = DOCTORVET_ROOT_URL + "pets_genders";
    private static final String PETS_CHARACTERS_URL = DOCTORVET_ROOT_URL + "pets_characters";
    private static final String PRODUCTS_URL = DOCTORVET_ROOT_URL + "products";
    private static final String PRODUCTS_MANUFACTURERS_URL = DOCTORVET_ROOT_URL + "products_manufacturers";
    private static final String PRODUCTS_CATEGORIES_URL = DOCTORVET_ROOT_URL + "products_categories";
    private static final String PRODUCTS_PROVIDERS_URL = DOCTORVET_ROOT_URL + "products_providers";
    private static final String VETS_URL = DOCTORVET_ROOT_URL + "vets";
    private static final String AGENDA_URL = DOCTORVET_ROOT_URL + "agenda";
    private static final String USERS_URL = DOCTORVET_ROOT_URL + "users";
    private static final String USERS_REQUESTS_URL = DOCTORVET_ROOT_URL + "users_requests";
    private static final String PETS_CLINICS_URL = DOCTORVET_ROOT_URL + "pets_clinic";
    private static final String PETS_CLINICS_2_URL = DOCTORVET_ROOT_URL + "pets_clinic_2";
    private static final String PETS_STUDIES_URL = DOCTORVET_ROOT_URL + "pets_studies";
    private static final String PETS_RECIPES_URL = DOCTORVET_ROOT_URL + "pets_recipes";
    private static final String REGIONS_URL = DOCTORVET_ROOT_URL + "regions";
    private static final String USERS_HOME_URL = DOCTORVET_ROOT_URL + "users_home";
    private static final String PETS_SUPPLY_URL = DOCTORVET_ROOT_URL + "pets_supply";
    private static final String REPORTS_URL = DOCTORVET_ROOT_URL + "reports";
    private static final String SELLS_URL = DOCTORVET_ROOT_URL + "sells";
    private static final String PURCHASES_URL = DOCTORVET_ROOT_URL + "purchases";
    private static final String MOVEMENTS_URL = DOCTORVET_ROOT_URL + "internal_movements";
    private static final String SYS_LAST_UPDATE_URL = DOCTORVET_ROOT_URL + "sys_last_update";
    private static final String FINANCE_URL = DOCTORVET_ROOT_URL + "finance";
    private static final String SELLS_PAYMENTS_URL = DOCTORVET_ROOT_URL + "sells_payments";
    private static final String SPENDINGS_URL = DOCTORVET_ROOT_URL + "spendings";
    private static final String CASH_MOVEMENTS_URL = DOCTORVET_ROOT_URL + "cash_movements";
    private static final String PURCHASES_PAYMENTS_URL = DOCTORVET_ROOT_URL + "purchases_payments";
    private static final String XLSX_1_URL = DOCTORVET_ROOT_URL + "import_from_xlsx_1";
    private static final String XLSX_2_URL = DOCTORVET_ROOT_URL + "import_from_xlsx_2";
    private static final String S3_UPLOAD_URL = DOCTORVET_ROOT_URL + "s3_upload";
    private static final String S3_UPLOAD_PREPARE_URL = DOCTORVET_ROOT_URL + "s3_upload_prepare";
    private static final String SYMPTOMS_URL = DOCTORVET_ROOT_URL + "symptoms";
    private static final String DIAGNOSTICS_URL = DOCTORVET_ROOT_URL + "diagnostics";
    private static final String TREATMENTS_URL = DOCTORVET_ROOT_URL + "treatments";
    private static final String SDT_SUGGESTED_URL = DOCTORVET_ROOT_URL + "sdt_suggested";
    private static final String WAITING_ROOMS_URL = DOCTORVET_ROOT_URL + "waiting_rooms";

    public static URL buildUsersRequestCreate() {
        Uri.Builder uriBuilder = Uri.parse(USERS_REQUESTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("create_request", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildUsersRequestAccept() {
        Uri.Builder uriBuilder = Uri.parse(USERS_REQUESTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("accept_request", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildUsersRequestDelete(String user_email, Integer id_vet) {
        Uri.Builder uriBuilder = Uri.parse(USERS_REQUESTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("user_email", user_email);
        uriBuilder.appendQueryParameter("id_vet", id_vet.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildUsersRequestDeleteById(Integer id) {
        Uri.Builder uriBuilder = Uri.parse(USERS_REQUESTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static Uri buildGetExtendSuscriptionUrl(Integer idVet, String country) {
        return Uri.parse(DOCTOR_VET_BASE_URL + "payments.php?id_vet="+idVet+"&region="+country);
    }

    public enum ProductsUrlEnum {
        FOR_INPUT, FOR_ASSOC, ASSOCIATES, RESTORE, SERVICES_FOR_INPUT
    }

    public enum SellsUrlEnum {
        VET_DEFAULTS, FOR_INPUT
    }

    public static URL buildSellsUrl(SellsUrlEnum type) {
        Uri.Builder uriBuilder = Uri.parse(SELLS_URL).buildUpon();
        uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildMovementsUrl(MovementsUrlEnum type, Integer id, Boolean pdfReceipt) {
        Uri.Builder uriBuilder = Uri.parse(MOVEMENTS_URL).buildUpon();

        if (type != null)
            uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        if (pdfReceipt != null && pdfReceipt)
            uriBuilder.appendQueryParameter("pdf_receipt", "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public enum MovementsUrlEnum {
        VET_DEFAULTS, FOR_INPUT, ACCEPT_MOVEMENT
    }

    public enum PurchasesUrlEnum {
        VET_DEFAULTS, FOR_INPUT
    }
    public static URL buildPurchasesUrl(PurchasesUrlEnum type) {
        Uri.Builder uriBuilder = Uri.parse(PURCHASES_URL).buildUpon();
        uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetUsersUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        appendForPagination(uriBuilder, search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public enum UsersUrlEnum {
        CREATE_ACCOUNT, EMAIL_PRE_AUTH, EMAIL_AUTH, FACEBOOK_GOOGLE_PRE_AUTH, FACEBOOK_GOOGLE_AUTH,
        CHECK_FOR_VALID_ACCOUNT, VALIDATE_ACCOUNT, FORGOT_ACCOUNT, DELETE_UNVERIFIED_ACCOUNT,
        USER_VETS, USER_AND_VET_BY_TOKEN, LOGOUT_ACCOUNT, CHANGE_VET, USER_VETS_BY_TOKEN, PROMOTE,
        REMOVE_FROM_VET, UPDATE, IS_IN_PASSWORD_RESTORE, FORGOT_ACCOUNT_3, PASSWORD_CHANGE, VERIFY_CAPTCHA
    }
    public static URL buildUsersUrl(UsersUrlEnum type, @Nullable Integer id_user, @Nullable String email, @Nullable String return_option) {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");

        if (id_user != null)
            uriBuilder.appendQueryParameter("id", id_user.toString());

        if (email != null)
            uriBuilder.appendQueryParameter("email", email.toString());

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildUsersMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        appendMinOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }


    public static URL buildUsersCheckAccountUrl() {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        uriBuilder.appendQueryParameter(UsersUrlEnum.CHECK_FOR_VALID_ACCOUNT.name().toLowerCase(), "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildUsersVetsUrl(String email) {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        uriBuilder.appendQueryParameter(UsersUrlEnum.USER_VETS.name().toLowerCase(), "");
        uriBuilder.appendQueryParameter("email", email);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    //for login order must provide id_user
    public static URL buildUsersDeleteUnverifiedAccountUrl(String email) {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        uriBuilder.appendQueryParameter(UsersUrlEnum.DELETE_UNVERIFIED_ACCOUNT.name().toLowerCase(), "");
        uriBuilder.appendQueryParameter("email", email);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    // users requests
    public enum UsersRequestsUrlEnum { CREATE_REQUEST, ACCEPT_REQUEST, CHECK_FOR_ACCEPTED_REQUEST }

    public enum petsSupplyEnum { SUPPLY, PLAN, SUPPLY_PLAN, CHECK }

    public static URL buildPetsSupplyYesNoUrl(@Nullable Integer id_supply, @Nullable petsSupplyEnum type) {
        Uri.Builder uriBuilder = Uri.parse(PETS_SUPPLY_URL).buildUpon();

        if (id_supply != null)
            uriBuilder.appendQueryParameter("id", id_supply.toString());

        if (type != null)
            uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetReportsUrl(DoctorVetApp.reports reporte_type, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(REPORTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("type", reporte_type.name());
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetTraceabilityReportUrl(Integer page, Integer product_id, Integer deposit_id) {
        Uri.Builder uriBuilder = Uri.parse(REPORTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("type", DoctorVetApp.reports.PRODUCTS_TRACEABILITY.name());
        uriBuilder.appendQueryParameter("page", page.toString());
        uriBuilder.appendQueryParameter("id_product", product_id.toString());
        uriBuilder.appendQueryParameter("id_deposit", deposit_id.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetReportsFromToUrl(DoctorVetApp.reports reporte_type, Integer page, String from, String to) {
        Uri.Builder uriBuilder = Uri.parse(REPORTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("type", reporte_type.name());
        uriBuilder.appendQueryParameter("page", page.toString());
        uriBuilder.appendQueryParameter("from", from);
        uriBuilder.appendQueryParameter("to", to);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildDeleteAgendaUrl(Integer id_agenda) {
        Uri.Builder uriBuilder = Uri.parse(AGENDA_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_agenda.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetAgendaEventUrl(Integer id_agenda) {
        Uri.Builder uriBuilder = Uri.parse(AGENDA_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_agenda.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetOwnersMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildProductsUrl(@Nullable ProductsUrlEnum type, @Nullable Integer idProduct) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();

        if (idProduct != null)
            uriBuilder.appendQueryParameter("id", idProduct.toString());

        if (type != null)
            uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetPetsForInputUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("for_input", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetOwnersUrl(DoctorVetApp.get_format format, String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        appendForPagination(uriBuilder,/* format,*/ search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetOwnersRecientesUrl(Integer page) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("recent", "");
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetOwnersRecientesMinUrl(/*Integer id_vet*/) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("recent", "");
        appendFormatOption(uriBuilder, DoctorVetApp.get_format.MIN);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetOwnerUrl(Integer id_owner, /*, Integer id_vet,*/ Integer update_last_view) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_owner.toString());
        if (update_last_view != 0)
            uriBuilder.appendQueryParameter("update_last_view", "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public enum OwnersUpdateType { UPDATE, UPDATE_EMAIL, UPDATE_PHONE }
    public static URL buildOwnerUrl(@Nullable Integer id, @Nullable OwnersUpdateType updateType, @Nullable String return_option) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        if (updateType != null)
            uriBuilder.appendQueryParameter(updateType.name().toLowerCase(), "");

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildOwnerNotificationUrl(Integer notification_id) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("notification_id", notification_id.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildOwnerDebtsUrl(Integer id_owner) {
        Uri.Builder uriBuilder = Uri.parse(FINANCE_URL).buildUpon();
        
        uriBuilder.appendQueryParameter("id_owner", id_owner.toString());
        uriBuilder.appendQueryParameter("owner_debt_details", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildProviderDebtsUrl(Integer id_provider) {
        Uri.Builder uriBuilder = Uri.parse(FINANCE_URL).buildUpon();
        
        uriBuilder.appendQueryParameter("id_provider", id_provider.toString());
        uriBuilder.appendQueryParameter("provider_debt_details", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildReceiptTypesUrl() {
        Uri.Builder uriBuilder = Uri.parse(FINANCE_URL).buildUpon();
        uriBuilder.appendQueryParameter("receipt_types", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildDeletePropietarioUrl(Integer id_owner) {
        Uri.Builder uriBuilder = Uri.parse(OWNERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_owner.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildDeleteSellUrl(Integer id_sell) {
        Uri.Builder uriBuilder = Uri.parse(SELLS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_sell.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteSellPaymentUrl(Integer id_sell_payment) {
        Uri.Builder uriBuilder = Uri.parse(SELLS_PAYMENTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_sell_payment.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteCashMovementUrl(Integer id_cash_movement) {
        Uri.Builder uriBuilder = Uri.parse(CASH_MOVEMENTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_cash_movement.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteSpendingUrl(Integer id_spending) {
        Uri.Builder uriBuilder = Uri.parse(SPENDINGS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_spending.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildDeletePurchaseUrl(Integer id_purchase) {
        Uri.Builder uriBuilder = Uri.parse(PURCHASES_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_purchase.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeletePurchasePaymentUrl(Integer id_purchase_payment) {
        Uri.Builder uriBuilder = Uri.parse(PURCHASES_PAYMENTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_purchase_payment.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildServicesSchedulesVetUrl(String type) {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();

        //uriBuilder.appendQueryParameter("vet_services", "");
        if (type != null)
            uriBuilder.appendQueryParameter(type, "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteServicesSchedulesUrl(Integer id_schedule) {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("delete_schedule", "");
        uriBuilder.appendQueryParameter("id", id_schedule.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
//    public static URL buildServicesSchedulesUserUrl() {
//        Uri.Builder uriBuilder = Uri.parse(SERVICES_SCHEDULES_URL).buildUpon();
//        uriBuilder.appendQueryParameter("user_services", "");
//        Uri builtUri = uriBuilder.build();
//        return buildUri(builtUri);
//    }

    public static URL buildGetPetsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsUrl(/*Integer id_vet, DoctorVetApp.get_format format,*/ String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("page", page.toString());
        uriBuilder.appendQueryParameter("search", search);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsRecientesUrl(/*Integer id_vet,*/ Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("recent", "");
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsRecientesMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("recent", "1");
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetUrl(Integer id_pet, Integer update_last_view) {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_pet.toString());
        if (update_last_view != 0)
            uriBuilder.appendQueryParameter("update_last_view", "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildPetUrl(@Nullable Integer id, @Nullable String return_option) {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());
        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteMascotaUrl(Integer id_pet/*, Integer id_vet*/) {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_pet.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsRazasMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_RACES_URL).buildUpon();
        appendFormatOption(uriBuilder, DoctorVetApp.get_format.MIN);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetSellsUrl() {
        Uri.Builder uriBuilder = Uri.parse(SELLS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetXlsx1Url() {
        Uri.Builder uriBuilder = Uri.parse(XLSX_1_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetXlsx2Url() {
        Uri.Builder uriBuilder = Uri.parse(XLSX_2_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetPurchasesUrl() {
        Uri.Builder uriBuilder = Uri.parse(PURCHASES_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetSellUrl(Integer id_sell) {
        Uri.Builder uriBuilder = Uri.parse(SELLS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_sell.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetCashMovementUrl(Integer id_cash_movement) {
        Uri.Builder uriBuilder = Uri.parse(CASH_MOVEMENTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_cash_movement.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetSpendingUrl(Integer id_cash_movement) {
        Uri.Builder uriBuilder = Uri.parse(SPENDINGS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_cash_movement.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetSellsPdfUrl(Integer id_sell) {
        Uri.Builder uriBuilder = Uri.parse(SELLS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_sell.toString());
        uriBuilder.appendQueryParameter("pdf_receipt", "1");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPurchaseUrl(Integer id_purchase) {
        Uri.Builder uriBuilder = Uri.parse(PURCHASES_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_purchase.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    private static void appendCompressOption(Uri.Builder builder) {
        builder.appendQueryParameter("compress", "");
    }
    private static void appendMinOption(Uri.Builder builder) {
        builder.appendQueryParameter("min", "");
    }
    private static void appendFormatOption(Uri.Builder builder, DoctorVetApp.get_format format) {
        builder.appendQueryParameter("format", format.name());
    }
    private static void appendPage(Uri.Builder builder, Integer page) {
        builder.appendQueryParameter("page", page.toString());
    }

    public static URL buildGetPetsRazasUrl(String search, /*DoctorVetApp.get_format format,*/ Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_RACES_URL).buildUpon();

        if (search != null)
            uriBuilder.appendQueryParameter("search", search);

        //uriBuilder.appendQueryParameter("format", format.name());
        if (page != null)
            uriBuilder.appendQueryParameter("page", page.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetPetsPelajesMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_PELAGES_URL).buildUpon();
        appendFormatOption(uriBuilder, DoctorVetApp.get_format.MIN);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsPelajesUrl(String search/*, DoctorVetApp.get_format format*/, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_PELAGES_URL).buildUpon();

        if (search != null)
            uriBuilder.appendQueryParameter("search", search);
        //uriBuilder.appendQueryParameter("format", format.name());
        if (page != null)
            uriBuilder.appendQueryParameter("page", page.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsgendersUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_GENDERS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsCaracteresUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_CHARACTERS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProductsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("globals_include", "");
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProductsUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("page", page.toString());
        uriBuilder.appendQueryParameter("search", search);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetProductsVetMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetProductsVetIncludeGlobalsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("globals_include", "1");
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetProductsVetIncludeGlobalsUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("globals_include", "");
        uriBuilder.appendQueryParameter("page", page.toString());
        uriBuilder.appendQueryParameter("search", search);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buidGetProductsVetUrl(/*DoctorVetApp.get_format format,*/ String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("page", page.toString());
        uriBuilder.appendQueryParameter("search", search);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    private static void appendForPagination(Uri.Builder builder, /*DoctorVetApp.get_format format,*/ String search, Integer page) {
//        builder.appendQueryParameter("format", format.name());
        builder.appendQueryParameter("search", search);
        builder.appendQueryParameter("page", page.toString());
    }
    public static URL buidGetProvidersUrl(DoctorVetApp.get_format format, String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_PROVIDERS_URL).buildUpon();
        
        appendForPagination(uriBuilder, search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProviderUrl(Integer id_provider) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_PROVIDERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_provider.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildProviderUrl(@Nullable Integer id, @Nullable String return_option) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_PROVIDERS_URL).buildUpon();

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetProductVetUrl(Integer id_product) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_product.toString());
        
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteProductVetUrl(Integer id_product) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_product.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetVetServicesAssocUrl() {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("services", "");
        appendMinOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetVetSchedulesUrl() {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("schedules", "");
        appendMinOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetVetServicesUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("services", "");
        uriBuilder.appendQueryParameter("search", search);
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProductsManufacturersUrl(DoctorVetApp.get_format format, String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_MANUFACTURERS_URL).buildUpon();
        appendForPagination(uriBuilder, search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetManufacturerUrl(Integer id_product_manufacturer) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_MANUFACTURERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_product_manufacturer.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProductsUnitsUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("units", "");
        uriBuilder.appendQueryParameter("search", search);
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProductsUnitsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();
        uriBuilder.appendQueryParameter("units", "");
        uriBuilder.appendQueryParameter("min", "");
        uriBuilder.appendQueryParameter("compress", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetProductCategoriesUrl(String search, DoctorVetApp.get_format format, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_CATEGORIES_URL).buildUpon();
        uriBuilder.appendQueryParameter("search", search);
        uriBuilder.appendQueryParameter("only_products", "");
        uriBuilder.appendQueryParameter("format", format.name());
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetServicesCategoriesUrl(String search, DoctorVetApp.get_format format, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_CATEGORIES_URL).buildUpon();
        uriBuilder.appendQueryParameter("search", search);
        uriBuilder.appendQueryParameter("only_services", "");
        uriBuilder.appendQueryParameter("format", format.name());
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildProductUrl(@Nullable Integer id, @Nullable String barCode, @Nullable String return_option, @Nullable String productOrService) {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_URL).buildUpon();

        if (productOrService != null)
            uriBuilder.appendQueryParameter(productOrService, "");

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        if (barCode != null)
            uriBuilder.appendQueryParameter("bar_code", barCode);

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetVetUrl(Integer id_vet) {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_vet.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public enum vetsUrl {
        CREATE_VET, CREATE_BRANCH, CREATE_DEPOSIT, CREATE_POINT, DEPOSITS,
        SELL_POINTS, UPDATE_DEPOSIT, DELETE_DEPOSIT, DELETE_POINT, MOVEMENT_POINTS,
        ALL_POINTS
    }
    public static URL buildVetUrl(@Nullable vetsUrl type, @Nullable Integer vet_id) {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();

        if (type != null)
            uriBuilder.appendQueryParameter(type.name().toLowerCase(), "");

        if (vet_id != null)
            uriBuilder.appendQueryParameter("id", vet_id.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetAgendaUrl(Date selectedDate, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(AGENDA_URL).buildUpon();
        uriBuilder.appendQueryParameter("selected_date", android.text.format.DateFormat.format("yyyy-MM-dd", selectedDate).toString());
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildAgendaUrl(@Nullable Integer id) {
        Uri.Builder uriBuilder = Uri.parse(AGENDA_URL).buildUpon();

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildPetsClinicUrl(@Nullable Integer id_clinic, @Nullable String return_option) {
        Uri.Builder uriBuilder = Uri.parse(PETS_CLINICS_URL).buildUpon();

        if (id_clinic != null)
            uriBuilder.appendQueryParameter("id", id_clinic.toString());

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteClinicaUrl(Integer id_clinica) {
        Uri.Builder uriBuilder = Uri.parse(PETS_CLINICS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_clinica.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildPetsClinic2Url(@Nullable Integer id_clinic_2, @Nullable String return_option) {
        Uri.Builder uriBuilder = Uri.parse(PETS_CLINICS_2_URL).buildUpon();

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);

        if (id_clinic_2 != null)
            uriBuilder.appendQueryParameter("id", id_clinic_2.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteClinica2Url(Integer id_clinica2) {
        Uri.Builder uriBuilder = Uri.parse(PETS_CLINICS_2_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_clinica2.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildDeleteEstudioUrl(Integer id_estudio) {
        Uri.Builder uriBuilder = Uri.parse(PETS_STUDIES_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_estudio.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteRecipeUrl(Integer id_recipe) {
        Uri.Builder uriBuilder = Uri.parse(PETS_RECIPES_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_recipe.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildPetsRecipesUrl(@Nullable Boolean for_input, @Nullable Integer id) {
        Uri.Builder uriBuilder = Uri.parse(PETS_RECIPES_URL).buildUpon();

        if (for_input != null && for_input)
            uriBuilder.appendQueryParameter("for_input", "");

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetUserUrl(Integer id_user) {
        Uri.Builder uriBuilder = Uri.parse(USERS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_user.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsSupplyUrl(Integer id_pet, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_SUPPLY_URL).buildUpon();
        uriBuilder.appendQueryParameter("id_pet", id_pet.toString());
        appendPage(uriBuilder, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetStudy_itemsUrl(@Nullable Integer idStudy, @Nullable Boolean all_items) {
        Uri.Builder uriBuilder = Uri.parse(PETS_STUDIES_URL).buildUpon();

        if (idStudy != null)
            uriBuilder.appendQueryParameter("id", idStudy.toString());

        if (all_items != null)
            uriBuilder.appendQueryParameter("all_items", "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetVetStudiesMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_STUDIES_URL).buildUpon();
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPetsStudiesPaginationUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_STUDIES_URL).buildUpon();
        uriBuilder.appendQueryParameter("search", search);
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetRegionesUrl(String search, /*DoctorVetApp.get_format format,*/ Integer page) {
        Uri.Builder uriBuilder = Uri.parse(REGIONS_URL).buildUpon();
        uriBuilder.appendQueryParameter("search", search);
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetRegionesMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(REGIONS_URL).buildUpon();
        appendMinOption(uriBuilder);
        appendCompressOption(uriBuilder);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildRegionUrl() {
        Uri.Builder uriBuilder = Uri.parse(REGIONS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildInsertCaracterUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_CHARACTERS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildProductoCategoriaUrl() {
        Uri.Builder uriBuilder = Uri.parse(PRODUCTS_CATEGORIES_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetNotificationsUrl() {
        Uri.Builder uriBuilder = Uri.parse(USERS_HOME_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildStudyUrl(@Nullable Integer id_study, @Nullable String return_option, @Nullable String search, @Nullable Integer page, @Nullable Boolean study_item) {
        Uri.Builder uriBuilder = Uri.parse(PETS_STUDIES_URL).buildUpon();

        if (return_option != null)
            uriBuilder.appendQueryParameter("return_option", return_option);

        if (id_study != null)
            uriBuilder.appendQueryParameter("id", id_study.toString());

        if (search != null && page != null) {
            appendForPagination(uriBuilder, search, page);
            uriBuilder.appendQueryParameter("items", "");
        }

        if (study_item != null && study_item)
            uriBuilder.appendQueryParameter("study_item", "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetClinicUrl(Integer id_pet, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(PETS_CLINICS_URL).buildUpon();
        uriBuilder.appendQueryParameter("id_pet", id_pet.toString());
        appendPage(uriBuilder, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteSupplyBatchUrl(){
        Uri.Builder uriBuilder = Uri.parse(PETS_SUPPLY_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDeleteSupplyUrl(Integer id_supply){
        Uri.Builder uriBuilder = Uri.parse(PETS_SUPPLY_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_supply.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildCheckSupplyUrl(Integer id_supply) {
        Uri.Builder uriBuilder = Uri.parse(PETS_SUPPLY_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_supply.toString());
        uriBuilder.appendQueryParameter("check", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    private static URL buildUri(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built URI " + url);
        return url;
    }

    public static URL buildGetSysLastUpdateUrl(String table_name, String object_name) {
        Uri.Builder uriBuilder = Uri.parse(SYS_LAST_UPDATE_URL).buildUpon();

        if (table_name != null)
            uriBuilder.appendQueryParameter("table_name", table_name);

        if (object_name != null)
            uriBuilder.appendQueryParameter("object_name", object_name);

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetFinanceTypesPaymemntsUrl() {
        Uri.Builder uriBuilder = Uri.parse(FINANCE_URL).buildUpon();
        uriBuilder.appendQueryParameter("payment_types", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buidGetDailyCashUrl(Date date, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(FINANCE_URL).buildUpon();
        uriBuilder.appendQueryParameter("daily_cash", "1");
        uriBuilder.appendQueryParameter("date", HelperClass.getDateForMySQL(date));
        appendPage(uriBuilder, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetDailyCashDetailsUrl(String date) {
        Uri.Builder uriBuilder = Uri.parse(FINANCE_URL).buildUpon();
        uriBuilder.appendQueryParameter("daily_cash_details", "");
        uriBuilder.appendQueryParameter("date", date);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildSellsPaymentsUrl() {
        Uri.Builder uriBuilder = Uri.parse(SELLS_PAYMENTS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildPurchasePaymentsUrl() {
        Uri.Builder uriBuilder = Uri.parse(PURCHASES_PAYMENTS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildSpendingsUrl() {
        Uri.Builder uriBuilder = Uri.parse(SPENDINGS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildManualCashUrl() {
        Uri.Builder uriBuilder = Uri.parse(CASH_MOVEMENTS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildS3UploadUrl() {
        Uri.Builder uriBuilder = Uri.parse(S3_UPLOAD_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildS3UploadPrepareUrl() {
        Uri.Builder uriBuilder = Uri.parse(S3_UPLOAD_PREPARE_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetSymptomsUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(SYMPTOMS_URL).buildUpon();
        appendForPagination(uriBuilder, search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetSymptomsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(SYMPTOMS_URL).buildUpon();
        appendFormatOption(uriBuilder, DoctorVetApp.get_format.MIN);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetDiagnosticsUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(DIAGNOSTICS_URL).buildUpon();
        appendForPagination(uriBuilder, search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetDiagnosticsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(DIAGNOSTICS_URL).buildUpon();
        appendFormatOption(uriBuilder, DoctorVetApp.get_format.MIN);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetClinic2ForInputrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_CLINICS_2_URL).buildUpon();
        uriBuilder.appendQueryParameter("for_input", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildSymptomsUrl() {
        Uri.Builder uriBuilder = Uri.parse(SYMPTOMS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildDiagnosticUrl() {
        Uri.Builder uriBuilder = Uri.parse(DIAGNOSTICS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildTreatmentsUrl() {
        Uri.Builder uriBuilder = Uri.parse(TREATMENTS_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetTreatmentsUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(TREATMENTS_URL).buildUpon();
        appendForPagination(uriBuilder, /*DoctorVetApp.get_format.SEARCH,*/ search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetTreatmentsMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(TREATMENTS_URL).buildUpon();
        appendFormatOption(uriBuilder, DoctorVetApp.get_format.MIN);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetPossibleDiagnosticsUrl(ArrayList<Symptom> symptoms) {
        String symptoms_str = "";
        if (symptoms.size() > 0) {
            for (Symptom s:symptoms)
                symptoms_str += s.getId().toString() + ",";

            symptoms_str = symptoms_str.substring(0, symptoms_str.length()-1);
        }

        Uri.Builder uriBuilder = Uri.parse(DIAGNOSTICS_URL).buildUpon();
        uriBuilder.appendQueryParameter("symptoms", symptoms_str);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildSDTSuggestedUrl() {
        Uri.Builder uriBuilder = Uri.parse(SDT_SUGGESTED_URL).buildUpon();
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildGetVetStructureMinUrl() {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("structure", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }
    public static URL buildWaitingRoomsUrl(@Nullable Integer id) {
        Uri.Builder uriBuilder = Uri.parse(WAITING_ROOMS_URL).buildUpon();

        if (id != null)
            uriBuilder.appendQueryParameter("id", id.toString());

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetPetsDashboardUrl() {
        Uri.Builder uriBuilder = Uri.parse(PETS_URL).buildUpon();
        
        uriBuilder.appendQueryParameter("dashboard", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }


    public static URL buildCheckAgendaUrl(Integer idAgenda/*, Integer id_vet*/) {
        Uri.Builder uriBuilder = Uri.parse(AGENDA_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", idAgenda.toString());
        uriBuilder.appendQueryParameter("set_executed", "");
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildgetWaitingRoomsHistoricUrl(Integer page) {
        Uri.Builder uriBuilder = Uri.parse(WAITING_ROOMS_URL).buildUpon();
        //uriBuilder.appendQueryParameter("historic", "1");
        
        uriBuilder.appendQueryParameter("page", page.toString());
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildAgendaCheckUrl(Integer id_agenda) {
        Uri.Builder uriBuilder = Uri.parse(AGENDA_URL).buildUpon();
        uriBuilder.appendQueryParameter("id", id_agenda.toString());
        uriBuilder.appendQueryParameter("set_executed", "");

        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

    public static URL buildGetVetsForRequestUrl(String search, Integer page) {
        Uri.Builder uriBuilder = Uri.parse(VETS_URL).buildUpon();
        uriBuilder.appendQueryParameter("user_email", DoctorVetApp.get().preferences_getUserEmail());
        uriBuilder.appendQueryParameter("for_request", "");
        appendForPagination(uriBuilder, search, page);
        Uri builtUri = uriBuilder.build();
        return buildUri(builtUri);
    }

}