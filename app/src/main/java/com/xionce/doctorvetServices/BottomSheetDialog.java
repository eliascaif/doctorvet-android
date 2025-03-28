package com.xionce.doctorvetServices;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.xionce.doctorvetServices.data.Users_permissions;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private Context mContext;
    private LinearLayout root_linear;
    private String view_to_show;

    public BottomSheetDialog(@NonNull Context context, String view_to_show) {
        this.mContext = context;
        this.view_to_show = view_to_show;
    }

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);
        root_linear = v.findViewById(R.id.bottom_sheet_linear_root);
        createBottomSheet();
        return v;
    }

    private void createBottomSheet() {
        if (view_to_show.equalsIgnoreCase("MainActivity")) createMain();
        if (view_to_show.equalsIgnoreCase("ViewAgendaEventActivity")) createAgendaEvent();
        if (view_to_show.equalsIgnoreCase("ViewCashMovementActivity")) createCashMovement();
        if (view_to_show.equalsIgnoreCase("ViewManufacturerActivity")) createViewManufacturer();
        if (view_to_show.equalsIgnoreCase("ViewMovementActivity")) createViewMovement();
        if (view_to_show.equalsIgnoreCase("ViewOwnerActivity")) createViewOwner();
        if (view_to_show.equalsIgnoreCase("ViewPetActivity")) createViewPet();
        if (view_to_show.equalsIgnoreCase("ViewProductVetActivity")) createViewProductVet();
        if (view_to_show.equalsIgnoreCase("ViewProviderActivity")) createViewProvider();
        if (view_to_show.equalsIgnoreCase("ViewPurchaseActivity")) createViewPurchase();
        if (view_to_show.equalsIgnoreCase("ViewSellActivity")) createViewSell();
        if (view_to_show.equalsIgnoreCase("ViewSpendingActivity")) createViewSpending();
        if (view_to_show.equalsIgnoreCase("ViewUserActivity")) createViewUser();
        if (view_to_show.equalsIgnoreCase("ViewUserActivityOnlyComm")) createViewUserOnlyComm();
        if (view_to_show.equalsIgnoreCase("ViewVetActivity")) createViewVet();
    }

    private void createMain() {
        //most used
        GridLayout gridMostUsed = getGrid();
        Users_permissions permissions = DoctorVetApp.get().getUser().getPermissions();

        //row1
        if (permissions.actions_permissions.pets.create == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.PETS_NEW, R.drawable.ic_add_pet_2, "Nuevo", DoctorVetApp.get().getPetNaming()));
        if (permissions.actions_permissions.pets.read == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.PETS_SEARCH, R.drawable.ic_search_pet_2, "Buscar", DoctorVetApp.get().getPetNaming()));
        if (permissions.actions_permissions.owners.create == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.OWNERS_NEW, R.drawable.ic_add_person_2, "Nuevo", DoctorVetApp.get().getOwnerNaming()));
        if (permissions.actions_permissions.owners.read == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.OWNERS_SEARCH, R.drawable.ic_search_owner_2, "Buscar", DoctorVetApp.get().getOwnerNaming()));

        //row2
        if (permissions.actions_permissions.products.read == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.PRODUCTS_SEARCH, R.drawable.ic_search_product, "Buscar", "producto"));
        if (permissions.actions_permissions.sells.create == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.SELLS_NEW, R.drawable.ic_add_sell, "Venta", null));
        if (permissions.actions_permissions.spendings.create == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.SPENDING_NEW, R.drawable.ic_add_spending_2, "Gasto", null));
        if (permissions.actions_permissions.agenda.create == 1) gridMostUsed.addView(getButton(gridMostUsed, Buttons.AGENDA_NEW, R.drawable.ic_add_task, "Agenda", null));

        AddToRoot(gridMostUsed, "Más usados");

        //others
        GridLayout gridOthers = getGrid();

        //row1
        if (permissions.actions_permissions.purchases.create == 1) gridOthers.addView(getButton(gridOthers, Buttons.PURCHASES_NEW, R.drawable.ic_add_buy_2, "Compra", null));
        if (permissions.actions_permissions.cash_movements.create == 1) gridOthers.addView(getButton(gridOthers, Buttons.MANUAL_IN_OUT, R.drawable.ic_add_in_out_2, "Ingreso / egreso", "manual"));
        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("OWNER")) gridOthers.addView(getButton(gridOthers, Buttons.IMPORT_NEW, R.drawable.ic_add_import_2, "Importar", "productos"));
        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("OWNER")) gridOthers.addView(getButton(gridOthers, Buttons.PRODUCTS_ASSOC, R.drawable.ic_assoc_products, "Asociar", "productos"));

        //row2
        if (permissions.actions_permissions.products.create == 1) gridOthers.addView(getButton(gridOthers, Buttons.PRODUCTS_NEW, R.drawable.ic_add_product_2, "Nuevo", "producto"));
        if (permissions.actions_permissions.products.create == 1) gridOthers.addView(getButton(gridOthers, Buttons.PRODUCTS_NEW_SERVICE, R.drawable.ic_add_service_2, "Nuevo", "servicio"));
        if (permissions.actions_permissions.products_providers.create == 1) gridOthers.addView(getButton(gridOthers, Buttons.PROVIDER_NEW, R.drawable.ic_add_provider_2, "Nuevo", "distribuidor"));
        if (permissions.actions_permissions.products_providers.read == 1) gridOthers.addView(getButton(gridOthers, Buttons.PROVIDERS_SEARCH, R.drawable.ic_search_provider, "Buscar", "distribuidor"));

        //row3
        if (permissions.actions_permissions.products_manufacturers.read == 1) gridOthers.addView(getButton(gridOthers, Buttons.MANUFACTURERS_SEARCH, R.drawable.ic_search_manufacturer, "Buscar", "fabricante"));
        if (permissions.actions_permissions.internal_movements.create == 1) gridOthers.addView(getButton(gridOthers, Buttons.MOVEMENTS_NEW, R.drawable.ic_arrow_right_alt_48px, "Remito", null));

        AddToRoot(gridOthers, "Otros");
    }
    private void createViewVet() {
        if (DoctorVetApp.get().getUser().getRol().equalsIgnoreCase("OWNER")) {
            GridLayout gridVet = getGrid();
            gridVet.addView(getButton(gridVet, Buttons.VET_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
            gridVet.addView(getButton(gridVet, Buttons.VET_EDIT_SCHEDULES, R.drawable.ic_design_services, "Servicios", "y horarios"));
            gridVet.addView(getButton(gridVet, Buttons.VET_USERS, R.drawable.ic_users, "Usuarios", null));
            gridVet.addView(getButton(gridVet, Buttons.VET_CREATE_BRANCH, R.drawable.ic_vet_create_branch, "Crear", "sucursal"));
            gridVet.addView(getButton(gridVet, Buttons.VET_SELL_POINTS, R.drawable.ic_add_sell, "Comprobantes", null));
            gridVet.addView(getButton(gridVet, Buttons.VET_DEPOSITS, R.drawable.ic_warehouse, "Almacenes", null));
            gridVet.addView(getButton(gridVet, Buttons.VET_ELECTRONIC_INVOICING, R.drawable.ic_creditors_tolerance, "Facturación", "electrónica"));

            AddToRoot(gridVet, "Veterinaria");
        }

        AddToRoot(getCommGrid(), "Comunicación");
    }
    private void createViewUser() {
        GridLayout grid = getGrid();
        grid.addView(getButton(grid, Buttons.USER_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
        grid.addView(getButton(grid, Buttons.USER_CHANGE_PASSWORD, R.drawable.ic_change_password, "Cambiar", "contraseña"));
        grid.addView(getButton(grid, Buttons.USER_LOGOUT, R.drawable.ic_close_session, "Cerrar", "sesión"));
        AddToRoot(grid, "Usuario");
    }
    private void createViewUserOnlyComm() {
        AddToRoot(getCommGrid(), "Comunicación");
    }
    private void createViewSpending() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.spendings.delete == 1) grid.addView(getButton(grid, Buttons.SPENDING_CANCEL, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(grid, "Gasto");
    }
    private void createViewSell() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.sells.delete == 1) grid.addView(getButton(grid, Buttons.SELL_CANCEL, R.drawable.ic_action_delete, "Eliminar", null));
        grid.addView(getButton(grid, Buttons.SELL_PDF, R.drawable.ic_receipt, "PDF", null));
        AddToRoot(grid, "Venta");
   }
    private void createViewPurchase() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.purchases.delete == 1) grid.addView(getButton(grid, Buttons.PURCHASE_CANCEL, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(grid, "Compra");
    }
    private void createViewProvider() {
        GridLayout gridActions = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.purchases.create == 1) gridActions.addView(getButton(gridActions, Buttons.PROVIDER_BUY, R.drawable.ic_add_buy_2, "Compra", null));
        AddToRoot(gridActions, "Acciones");

        GridLayout gridProvider = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.purchases.create == 1) gridProvider.addView(getButton(gridProvider, Buttons.PROVIDER_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.purchases.create == 1) gridProvider.addView(getButton(gridProvider, Buttons.PROVIDER_DELETE, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(gridProvider, "Distribuidor");
    }
    private void createViewProductVet() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.products.update == 1) grid.addView(getButton(grid, Buttons.PRODUCT_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.products.delete == 1) grid.addView(getButton(grid, Buttons.PRODUCT_DELETE, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(grid, "Producto");
    }
    private void createViewPet() {
        GridLayout gridActions = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.pets_clinic.create == 1) {
            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_CLINIC, R.drawable.ic_clinic, "Clínica", null));
            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_CLINIC_2, R.drawable.ic_clinic_est_2, "Clínica", "extendida"));
            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_SUPPLY, R.drawable.ic_vac, "Suministro", null));
            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_STUDY, R.drawable.ic_labs_48px, "Estudio", "complementario"));
            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_RECIPE, R.drawable.ic_add_recipe, "Receta", null));
            gridActions.addView(getButton(gridActions, Buttons.SELLS_TO_PET_NEW, R.drawable.ic_add_sell, "Venta", null));
            gridActions.addView(getButton(gridActions, Buttons.WAITING_ROOM_PET, R.drawable.ic_hourglass, "Sala", "de espera"));
            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_AGENDA, R.drawable.ic_add_task, "Agenda", null));
//            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_CLINIC, R.drawable.ic_clinic, "Nueva", "clínica"));
//            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_CLINIC_2, R.drawable.ic_clinic_est_2, "Nueva", "clínica ext."));
//            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_SUPPLY, R.drawable.ic_vac, "Nuevo", "suministro"));
//            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_STUDY, R.drawable.ic_labs_48px, "Nuevo", "estudio"));
//            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_RECIPE, R.drawable.ic_add_recipe, "Nueva", "receta"));
//            gridActions.addView(getButton(gridActions, Buttons.SELLS_TO_PET_NEW, R.drawable.ic_add_sell, "Venta", null));
//            gridActions.addView(getButton(gridActions, Buttons.WAITING_ROOM_PET, R.drawable.ic_hourglass, "Sala", "de espera"));
//            gridActions.addView(getButton(gridActions, Buttons.PETS_NEW_AGENDA, R.drawable.ic_add_task, "Nuevo", "agenda"));
        }
        AddToRoot(gridActions, "Acciones");

        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.pets.update == 1) grid.addView(getButton(grid, Buttons.PET_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.pets.delete == 1) grid.addView(getButton(grid, Buttons.PET_DELETE, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(grid, DoctorVetApp.get().getPetNaming());
    }
    private void createViewOwner() {
        GridLayout gridActions = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.pets.create == 1) gridActions.addView(getButton(gridActions, Buttons.OWNER_ADD_PET, R.drawable.ic_add_pet_2, "Agregar", DoctorVetApp.get().getPetNaming().toLowerCase()));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.sells.create == 1) gridActions.addView(getButton(gridActions, Buttons.SELLS_TO_OWNER_NEW, R.drawable.ic_add_sell, "Venta", null));
        AddToRoot(gridActions, "Acciones");

        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.owners.update == 1) grid.addView(getButton(grid, Buttons.OWNER_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.owners.delete == 1) grid.addView(getButton(grid, Buttons.OWNER_DELETE, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(grid, DoctorVetApp.get().getOwnerNaming());

        AddToRoot(getCommGrid(), "Comunicación");
    }
    private void createViewMovement() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.internal_movements.delete == 1) grid.addView(getButton(grid, Buttons.MOVEMENT_CANCEL, R.drawable.ic_action_delete, "Eliminar", null));
        grid.addView(getButton(grid, Buttons.MOVEMENT_PDF, R.drawable.ic_receipt, "PDF", null));
        AddToRoot(grid, "Remito");
    }
    private void createViewManufacturer() {
        AddToRoot(getCommGrid(), "Comunicación");
    }
    private void createCashMovement() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.cash_movements.delete == 1) grid.addView(getButton(grid, Buttons.CASH_MOVEMENT_CANCEL, R.drawable.ic_action_delete, "Eliminar", null));
        AddToRoot(grid, "Movimiento manual");
    }
    private void createAgendaEvent() {
        GridLayout grid = getGrid();
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.agenda.update == 1) grid.addView(getButton(grid, Buttons.AGENDA_UPDATE, R.drawable.ic_action_edit_light, "Editar", null));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.agenda.delete == 1) grid.addView(getButton(grid, Buttons.AGENDA_DELETE, R.drawable.ic_action_delete, "Eliminar", null));
        if (DoctorVetApp.get().getUser().getPermissions().actions_permissions.agenda.create == 1) grid.addView(getButton(grid, Buttons.AGENDA_CHECK, R.drawable.ic_check_light, "Realizado", null));
        AddToRoot(grid, "Agenda");
    }

    private void fixLastRow(GridLayout gridLayout) {
        int COLUMNS = 4;
        int itemsToCompleteRow = COLUMNS - (gridLayout.getChildCount() % 4);
        for (int i = 0; i < itemsToCompleteRow; i++)
            gridLayout.addView(getEmptyButton(gridLayout));
    }

    private RelativeLayout getButton(GridLayout grid, Buttons type, int drawable, String title, String subtitle) {
        RelativeLayout button = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_layout_button, grid, false);

        ImageButton imageButton = button.findViewById(R.id.btn_image);
        imageButton.setImageResource(drawable);

        TextView txtTitle = button.findViewById(R.id.txt_title);
        txtTitle.setText(title);

        if (subtitle != null) {
            TextView txtSubitle = button.findViewById(R.id.txt_subtitle);
            txtSubitle.setText(subtitle);
        }

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BottomSheetListener2)mContext).onButtonClicked(type);
                dismiss();
            }
        });

        return button;
    }
    private RelativeLayout getEmptyButton(GridLayout grid) {
        RelativeLayout button = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_layout_button_empty, grid, false);
        return button;
    }

    private GridLayout getGrid() {
        GridLayout gridLayout = (GridLayout) LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_layout_grid, root_linear, false);
        return gridLayout;
    }

    private TextView getTitle(String title) {
        TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.bottom_sheet_layout_title, root_linear, false);
        textView.setText(title);
        return textView;
    }
    private GridLayout getCommGrid() {
        GridLayout gridComm = getGrid();

        //row1
        gridComm.addView(getButton(gridComm, Buttons.COMUNICATION_CALL, R.drawable.ic_phone_2, "Llamar", null));
        gridComm.addView(getButton(gridComm, Buttons.COMUNICATION_WHATSAPP, R.drawable.ic_whatsapp_svg, "WhatsApp", null));
        gridComm.addView(getButton(gridComm, Buttons.COMUNICATION_SMS, R.drawable.ic_sms, "SMS", null));
        gridComm.addView(getButton(gridComm, Buttons.COMUNICATION_EMAIL, R.drawable.ic_email, "Email", null));

        return gridComm;
    }

    private void AddToRoot(GridLayout grid, String title) {
        if (grid.getChildCount() == 0)
            return;

        fixLastRow(grid);

        root_linear.addView(getTitle(title));
        root_linear.addView(grid);
    }

    public enum Buttons {
        PETS_SEARCH, PETS_NEW, PETS_NEW_CLINIC, PETS_NEW_CLINIC_2,
        PETS_NEW_SUPPLY, PETS_NEW_STUDY, PETS_NEW_AGENDA, PETS_NEW_RECIPE,
        OWNERS_SEARCH, OWNERS_NEW,
        PRODUCTS_SEARCH, PRODUCTS_NEW, PRODUCTS_NEW_SERVICE, PRODUCTS_ASSOC,
        OWNER_ADD_PET,
        OWNER_UPDATE, OWNER_DELETE,
        COMUNICATION_CALL, COMUNICATION_WHATSAPP, COMUNICATION_SMS, COMUNICATION_EMAIL,
        PET_UPDATE, PET_DELETE,
        AGENDA_UPDATE, AGENDA_DELETE, AGENDA_CHECK,
        USER_UPDATE, USER_DELETE, /*USER_EDIT_SERVICES_SCHEDULES,*/
        VET_UPDATE, VET_EDIT_SCHEDULES, /*VET_EDIT_SERVICES,*/
        PRODUCT_UPDATE, PRODUCT_DELETE, /*PRODUCT_ASSOCIATE,*/
        SELLS_NEW, SELLS_TO_OWNER_NEW, SELLS_TO_PET_NEW,
        SELL_CANCEL, SELL_PDF,
        PROVIDER_UPDATE, PROVIDER_DELETE, PROVIDER_NEW, PROVIDER_BUY,
        SPENDING_NEW,
        MANUAL_IN_OUT,
        PURCHASES_NEW, PURCHASE_CANCEL,
        IMPORT_NEW,
        AGENDA_NEW,
        SERVICES_SCHEDULES_NEW,
        PROVIDERS_SEARCH,
        MANUFACTURERS_SEARCH,
        WAITING_ROOM_PET,
        VET_USERS, VET_CREATE_BRANCH, VET_SELL_POINTS, VET_DEPOSITS, VET_ELECTRONIC_INVOICING,
        USER_CHANGE_PASSWORD, USER_LOGOUT,
        MOVEMENTS_NEW, MOVEMENT_CANCEL, MOVEMENT_ACCEPT, MOVEMENT_PDF,
        CASH_MOVEMENT_CANCEL,
        SPENDING_CANCEL,
    }

    public interface BottomSheetListener2 {
        void onButtonClicked(Buttons buttonClicked);
    }

}