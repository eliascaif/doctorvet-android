package com.xionce.doctorvetServices;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.xionce.doctorvetServices.data.User;

public class BottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private TextView txt_owner_especifico;
    private LinearLayout lyt_owner_especifico;
    private TextView txt_actions_owners;
    private LinearLayout lyt_actions_owners;
    private TextView txt_especifico_comunicacion;
    private LinearLayout lyt_especifico_comunicacion;
    private TextView txt_pet_especific;
    private LinearLayout lyt_pet_especific;
    private TextView txt_actions_pets;
    private LinearLayout lyt_actions_pets;
    private LinearLayout lyt_actions_pets_2;
    private TextView txt_agenda_especifico;
    private LinearLayout lyt_agenda_especifico;
    private TextView txt_user_especifico;
    private LinearLayout lyt_user_especifico;
    private TextView txt_veterinaria_especifico;
    private LinearLayout lyt_veterinaria_especifico;
    private LinearLayout lyt_veterinaria_especifico_2;
    private TextView txt_product_especifico;
    private LinearLayout lyt_product_especifico;
//    private TextView txt_actions_products;
//    private LinearLayout lyt_actions_products;
    private TextView txt_sell_specific;
    private LinearLayout lyt_sell_specific;
    private TextView txt_purchase_specific;
    private LinearLayout lyt_purchase_specific;
    private TextView txt_movement_specific;
    private LinearLayout lyt_movement_specific;
    private TextView txt_provider_specific;
    private LinearLayout lyt_provider_specific;
    private TextView txt_actions_providers;
    private LinearLayout lyt_actions_providers;
    private TextView txt_service_schedule;
    private LinearLayout lyt_service_schedule_specific;
    private TextView txt_cash_movement_specific;
    private LinearLayout lyt_cash_movement_specific;
    private TextView txt_spending_specific;
    private LinearLayout lyt_spending_specific;

    private TextView txt_most_used;
    private LinearLayout lyt_most_used_general;
    private LinearLayout lyt_most_used_general_2;
    private TextView txt_others_general;
    private LinearLayout lyt_others_general;
    private LinearLayout lyt_others_general_2;
    private LinearLayout lyt_others_general_3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        txt_owner_especifico = v.findViewById(R.id.txt_owner_especifico);
        lyt_owner_especifico = v.findViewById(R.id.lyt_owner_especifico);
        txt_actions_owners = v.findViewById(R.id.txt_actions_owners);
        lyt_actions_owners = v.findViewById(R.id.lyt_actions_owners);
        txt_especifico_comunicacion = v.findViewById(R.id.txt_especifico_comunicacion);
        lyt_especifico_comunicacion = v.findViewById(R.id.lyt_especifico_comunicacion);
        txt_pet_especific = v.findViewById(R.id.txt_pet_especific);
        lyt_pet_especific = v.findViewById(R.id.lyt_pet_especific);
        txt_actions_pets = v.findViewById(R.id.txt_actions_pets);
        lyt_actions_pets = v.findViewById(R.id.lyt_actions_pets);
        lyt_actions_pets_2 = v.findViewById(R.id.lyt_actions_pets_2);
        txt_agenda_especifico = v.findViewById(R.id.txt_agenda_especifico);
        lyt_agenda_especifico = v.findViewById(R.id.lyt_agenda_especifico);
        txt_user_especifico = v.findViewById(R.id.txt_user_especifico);
        lyt_user_especifico = v.findViewById(R.id.lyt_user_especifico);
        txt_veterinaria_especifico = v.findViewById(R.id.txt_vet_especific);
        lyt_veterinaria_especifico = v.findViewById(R.id.lyt_vet_especific);
        lyt_veterinaria_especifico_2 = v.findViewById(R.id.lyt_vet_especific_2);
        txt_product_especifico = v.findViewById(R.id.txt_product_especifico);
        lyt_product_especifico = v.findViewById(R.id.lyt_product_especifico);
//        txt_actions_products = v.findViewById(R.id.txt_actions_products);
//        lyt_actions_products = v.findViewById(R.id.lyt_actions_products);
        txt_sell_specific = v.findViewById(R.id.txt_sell_specific);
        lyt_sell_specific = v.findViewById(R.id.lyt_sell_specific);

        txt_cash_movement_specific = v.findViewById(R.id.txt_cash_movement_specific);
        lyt_cash_movement_specific = v.findViewById(R.id.lyt_cash_movement_specific);
        txt_spending_specific = v.findViewById(R.id.txt_spending_specific);
        lyt_spending_specific = v.findViewById(R.id.lyt_spending_specific);

        txt_purchase_specific = v.findViewById(R.id.txt_purchase_specific);
        lyt_purchase_specific = v.findViewById(R.id.lyt_purchase_specific);

        txt_movement_specific = v.findViewById(R.id.txt_movement_specific);
        lyt_movement_specific = v.findViewById(R.id.lyt_movement_specific);
        txt_provider_specific = v.findViewById(R.id.txt_provider_especific);
        lyt_provider_specific = v.findViewById(R.id.lyt_provider_especific);
        txt_actions_providers = v.findViewById(R.id.txt_actions_providers);
        lyt_actions_providers = v.findViewById(R.id.lyt_actions_providers);
        txt_service_schedule = v.findViewById(R.id.txt_service_schedule);
        lyt_service_schedule_specific = v.findViewById(R.id.lyt_service_schedule_especific);

        txt_most_used = v.findViewById(R.id.txt_most_used_general);
        lyt_most_used_general = v.findViewById(R.id.lyt_most_used_general);
        lyt_most_used_general_2 = v.findViewById(R.id.lyt_most_used_general_2);
        txt_others_general = v.findViewById(R.id.txt_others_general);
        lyt_others_general = v.findViewById(R.id.lyt_others_general);
        lyt_others_general_2 = v.findViewById(R.id.lyt_others_general_2);
        lyt_others_general_3 = v.findViewById(R.id.lyt_others_general_3);

        //search
        ImageButton btnSearchMascota = v.findViewById(R.id.btn_search_pet);
        ((TextView)v.findViewById(R.id.txt_search_pet_2)).setText(DoctorVetApp.get().getPetNaming().toLowerCase());
        btnSearchMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_SEARCH);
                dismiss();
            }
        });
        ImageButton btnSearchPropietario = v.findViewById(R.id.btn_search_owner);
        ((TextView)v.findViewById(R.id.txt_search_owner_2)).setText(DoctorVetApp.get().getOwnerNaming().toLowerCase());
        btnSearchPropietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.OWNERS_SEARCH);
                dismiss();
            }
        });
        ImageButton btnSearchProducto = v.findViewById(R.id.btn_search_product);
        btnSearchProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCTS_SEARCH);
                dismiss();
            }
        });

        //actions
        ImageButton btnNewMascota = v.findViewById(R.id.btn_new_pet);
        ((TextView)v.findViewById(R.id.txt_new_pet_2)).setText(DoctorVetApp.get().getPetNaming().toLowerCase());
        btnNewMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW);
                dismiss();
            }
        });
        ImageButton btnNewPropietario = v.findViewById(R.id.btn_new_owner);
        ((TextView)v.findViewById(R.id.txt_new_owner_2)).setText(DoctorVetApp.get().getOwnerNaming().toLowerCase());
        btnNewPropietario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.OWNERS_NEW);
                dismiss();
            }
        });
        ImageButton btnNewProducto = v.findViewById(R.id.btn_new_product);
        btnNewProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCTS_NEW);
                dismiss();
            }
        });
        ImageButton btnNewService = v.findViewById(R.id.btn_new_service);
        btnNewService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCTS_NEW_SERVICE);
                dismiss();
            }
        });
        ImageButton btnAsocProduct = v.findViewById(R.id.btn_asoc_product);
        btnAsocProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCTS_ASSOC);
                dismiss();
            }
        });
        ImageButton btnNewProvider = v.findViewById(R.id.btn_new_provider);
        btnNewProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PROVIDER_NEW);
                dismiss();
            }
        });
        ImageButton btnNewSpending = v.findViewById(R.id.btn_new_spending);
        btnNewSpending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SPENDING_NEW);
                dismiss();
            }
        });
        ImageButton btnNewSell = v.findViewById(R.id.btn_new_sell);
        btnNewSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SELLS_NEW);
                dismiss();
            }
        });
        ImageButton btnNewManualInOut = v.findViewById(R.id.btn_new_manual_in_out);
        btnNewManualInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.MANUAL_IN_OUT);
                dismiss();
            }
        });
        ImageButton btnNewPurchase = v.findViewById(R.id.btn_new_purchase);
        btnNewPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PURCHASES_NEW);
                dismiss();
            }
        });
        ImageButton btnNewImport = v.findViewById(R.id.btn_new_import);
        btnNewImport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.IMPORT_NEW);
                dismiss();
            }
        });
        ImageButton btnNewAgenda = v.findViewById(R.id.btn_new_agenda_global);
        btnNewAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.AGENDA_NEW);
                dismiss();
            }
        });

        //especifico propietarios
        ImageButton btnPropietarioEspecificoEditar = v.findViewById(R.id.btn_modificar_owner);
        btnPropietarioEspecificoEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.OWNER_UPDATE);
                dismiss();
            }
        });
        ImageButton btnPropietarioEspecificoEliminar = v.findViewById(R.id.btn_eliminar_owner);
        btnPropietarioEspecificoEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.OWNER_DELETE);
                dismiss();
            }
        });
        ImageButton btnAgregarMascota = v.findViewById(R.id.btn_agregar_pet);
        ((TextView)v.findViewById(R.id.txt_agregar_pet_2)).setText(DoctorVetApp.get().getPetNaming().toLowerCase());
        btnAgregarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.OWNER_ADD_PET);
                dismiss();
            }
        });
        ImageButton btnNewSellToOwner = v.findViewById(R.id.btn_new_sell_to_owner);
        btnNewSellToOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SELLS_TO_OWNER_NEW);
                dismiss();
            }
        });

        //especifico propietarios comunicacion
        ImageButton btnPropietarioEspecificoComLlamar = v.findViewById(R.id.btn_com_telefono);
        btnPropietarioEspecificoComLlamar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.COMUNICATION_CALL);
                dismiss();
            }
        });
        ImageButton btnPropietarioEspecificoComWhatsApp = v.findViewById(R.id.btn_com_whatsapp);
        btnPropietarioEspecificoComWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.COMUNICATION_WHATSAPP);
                dismiss();
            }
        });
        ImageButton btnPropietarioEspecificoComSms = v.findViewById(R.id.btn_com_sms);
        btnPropietarioEspecificoComSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.COMUNICATION_SMS);
                dismiss();
            }
        });
        ImageButton btnPropietarioEspecificoComEmail = v.findViewById(R.id.btn_com_email);
        btnPropietarioEspecificoComEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.COMUNICATION_EMAIL);
                dismiss();
            }
        });

        //especifico mascotas
        ImageButton btnMascotaEspecificoModificar = v.findViewById(R.id.btn_modificar_pet);
        btnMascotaEspecificoModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PET_UPDATE);
                dismiss();
            }
        });
        ImageButton btnMascotaEspecificoEliminar = v.findViewById(R.id.btn_eliminar_pet);
        btnMascotaEspecificoEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PET_DELETE);
                dismiss();
            }
        });
        ImageButton btnNewClinic = v.findViewById(R.id.btn_new_clinic);
        btnNewClinic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW_CLINIC);
                dismiss();
            }
        });
        ImageButton btnNewClinic2 = v.findViewById(R.id.btn_new_clinic_2);
        btnNewClinic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW_CLINIC_2);
                dismiss();
            }
        });
        ImageButton btnNewSuministro = v.findViewById(R.id.btn_new_suministro);
        btnNewSuministro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW_SUPPLY);
                dismiss();
            }
        });
        ImageButton btnNewEstudio = v.findViewById(R.id.btn_new_estudio);
        btnNewEstudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW_STUDY);
                dismiss();
            }
        });
        ImageButton btnNewRecipe = v.findViewById(R.id.btn_new_recipe);
        btnNewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW_RECIPE);
                dismiss();
            }
        });
        ImageButton btnNewAgendaPet = v.findViewById(R.id.btn_new_pet_agenda);
        btnNewAgendaPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PETS_NEW_AGENDA);
                dismiss();
            }
        });
        ImageButton btnNewSellToPet = v.findViewById(R.id.btn_new_sell_to_pet);
        btnNewSellToPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SELLS_TO_PET_NEW);
                dismiss();
            }
        });
        ImageButton btnWaitingRoomPet = v.findViewById(R.id.btn_new_waiting_room_pet);
        btnWaitingRoomPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.WAITING_ROOM_PET);
                dismiss();
            }
        });

        //especifico de agenda
        ImageButton btnAgendaEspecificoModificar = v.findViewById(R.id.btn_modificar_agenda);
        btnAgendaEspecificoModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.AGENDA_UPDATE);
                dismiss();
            }
        });
        ImageButton btnAgendaEspecificoEliminar = v.findViewById(R.id.btn_eliminar_agenda);
        btnAgendaEspecificoEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.AGENDA_DELETE);
                dismiss();
            }
        });
        ImageButton btnAgendaEspecificoMarcar = v.findViewById(R.id.btn_marcar_agenda);
        btnAgendaEspecificoMarcar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.AGENDA_CHECK);
                dismiss();
            }
        });

        //users specific
        ImageButton btnUsuarioEspecificoModificar = v.findViewById(R.id.btn_modificar_user);
        btnUsuarioEspecificoModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.USER_UPDATE);
                dismiss();
            }
        });
//        ImageButton btnUserSpecificEditServices = v.findViewById(R.id.btn_edit_user_services_schedules);
//        btnUserSpecificEditServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onButtonClicked(BottomSheetButtonClicked.USER_EDIT_SERVICES_SCHEDULES);
//                dismiss();
//            }
//        });
        ImageButton btnUserChangePassword = v.findViewById(R.id.btn_user_change_password);
        btnUserChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.USER_CHANGE_PASSWORD);
                dismiss();
            }
        });
        ImageButton btnUserLogout = v.findViewById(R.id.btn_user_close_session);
        btnUserLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.USER_LOGOUT);
                dismiss();
            }
        });

        //especific vets
        ImageButton btnVeterinariaEspecificoModificar = v.findViewById(R.id.btn_modificar_veterinaria);
        btnVeterinariaEspecificoModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.VET_UPDATE);
                dismiss();
            }
        });
        ImageButton btnVetSpecificEditSchedules = v.findViewById(R.id.btn_edit_vet_schedules);
        btnVetSpecificEditSchedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.VET_EDIT_SCHEDULES);
                dismiss();
            }
        });
//        ImageButton btnVetSpecificEditServices = v.findViewById(R.id.btn_edit_vet_services);
//        btnVetSpecificEditServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onButtonClicked(BottomSheetButtonClicked.VET_EDIT_SERVICES);
//                dismiss();
//            }
//        });
        ImageButton btnVetSpecificUsers = v.findViewById(R.id.btn_edit_vet_users);
        btnVetSpecificUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.VET_USERS);
                dismiss();
            }
        });
        ImageButton btnVetSpecificCreateBranch = v.findViewById(R.id.btn_create_branch);
        btnVetSpecificCreateBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.VET_CREATE_BRANCH);
                dismiss();
            }
        });
        ImageButton btnVetSpecificSellPoints = v.findViewById(R.id.btn_points_of_sell);
        btnVetSpecificSellPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.VET_SELL_POINTS);
                dismiss();
            }
        });
        ImageButton btnVetSpecificDeposits = v.findViewById(R.id.btn_deposits);
        btnVetSpecificDeposits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.VET_DEPOSITS);
                dismiss();
            }
        });

        //products specific
        ImageButton btnProductoEspecificoModificar = v.findViewById(R.id.btn_modificar_product);
        btnProductoEspecificoModificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCT_UPDATE);
                dismiss();
            }
        });
        ImageButton btnProductoEspecificoEliminar = v.findViewById(R.id.btn_eliminar_product);
        btnProductoEspecificoEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCT_DELETE);
                dismiss();
            }
        });
//        ImageButton btnProductoEspecificoAsociar = v.findViewById(R.id.btn_asociar_product);
//        btnProductoEspecificoAsociar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onButtonClicked(BottomSheetButtonClicked.PRODUCT_ASSOCIATE);
//                dismiss();
//            }
//        });

        //sells specific
        ImageButton btnSellSpecificDelete = v.findViewById(R.id.btn_delete_sell);
        btnSellSpecificDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SELL_CANCEL);
                dismiss();
            }
        });
        ImageButton btnSellSpecificGetPDF = v.findViewById(R.id.btn_get_pdf);
        btnSellSpecificGetPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SELL_PDF);
                dismiss();
            }
        });

        //purchases specific
        ImageButton btnPurchaseSpecificDelete = v.findViewById(R.id.btn_delete_purchase);
        btnPurchaseSpecificDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PURCHASE_CANCEL);
                dismiss();
            }
        });

        //movements specific
        ImageButton btnMovementSpecificDelete = v.findViewById(R.id.btn_delete_movement);
        btnMovementSpecificDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.MOVEMENT_CANCEL);
                dismiss();
            }
        });
        ImageButton btnMovementSpecificGetPDF = v.findViewById(R.id.btn_get_pdf_movement);
        btnMovementSpecificGetPDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.MOVEMENT_PDF);
                dismiss();
            }
        });
        ImageButton btnMovementSpecificAccept = v.findViewById(R.id.btn_accept_movement);
        btnMovementSpecificAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.MOVEMENT_ACCEPT);
                dismiss();
            }
        });

        //cash_movements specific
        ImageButton btnCashMovementsSpecificDelete = v.findViewById(R.id.btn_delete_cash_movement);
        btnCashMovementsSpecificDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.CASH_MOVEMENT_CANCEL);
                dismiss();
            }
        });

        //spending specific
        ImageButton btnSpendingSpecificDelete = v.findViewById(R.id.btn_delete_spending);
        btnSpendingSpecificDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SPENDING_CANCEL);
                dismiss();
            }
        });

        //providers specific
        ImageButton btnProviderSpecificBuy = v.findViewById(R.id.btn_new_purchase_to_provider);
        btnProviderSpecificBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PROVIDER_BUY);
                dismiss();
            }
        });
        ImageButton btnProviderSpecificEdit = v.findViewById(R.id.btn_update_provider);
        btnProviderSpecificEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PROVIDER_UPDATE);
                dismiss();
            }
        });
        ImageButton btnProviderSpecific = v.findViewById(R.id.btn_delete_provider);
        btnProviderSpecific.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PROVIDER_DELETE);
                dismiss();
            }
        });
        //service schedule specific
        ImageButton btnServiceScheduleNew = v.findViewById(R.id.btn_add_service_schedule);
        btnServiceScheduleNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.SERVICES_SCHEDULES_NEW);
                dismiss();
            }
        });
        //search provider
        ImageButton btnSearchProvider = v.findViewById(R.id.btn_search_provider);
        btnSearchProvider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.PROVIDERS_SEARCH);
                dismiss();
            }
        });
        //search manufacturer
        ImageButton btnSearchManufacturer = v.findViewById(R.id.btn_search_manufacturer);
        btnSearchManufacturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.MANUFACTURERS_SEARCH);
                dismiss();
            }
        });
        //new movement
        ImageButton btnNewMovement = v.findViewById(R.id.btn_new_movement);
        btnNewMovement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked(BottomSheetButtonClicked.MOVEMENTS_NEW);
                dismiss();
            }
        });

        return v;
    }

    public enum BottomSheetButtonClicked {
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
        VET_USERS, VET_CREATE_BRANCH, VET_SELL_POINTS, VET_DEPOSITS,
        USER_CHANGE_PASSWORD, USER_LOGOUT,
        MOVEMENTS_NEW, MOVEMENT_CANCEL, MOVEMENT_ACCEPT, MOVEMENT_PDF,
        CASH_MOVEMENT_CANCEL,
        SPENDING_CANCEL,
    }

    public interface BottomSheetListener {
        void onButtonClicked(BottomSheetButtonClicked buttonClicked);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Bundle b = getArguments();
        if (b != null) {
            if (b.containsKey("propietario_especifico")) {
                showOwnerSpecific();
                showEspecificoComunicacion();
            }
            if (b.containsKey("producto_especifico")) {
                showProductoEspecifico();
            }
            if (b.containsKey("mascota_especifico")) {
                showMascotaEspecifico();
            }
            if (b.containsKey("agenda_especifico")) {
                showAgendaEspecifico();
            }
            if (b.containsKey("usuario_especifico")) {
                showUsuarioEspecifico();
                //showEspecificoComunicacion();
            }
            if (b.containsKey("veterinaria_especifico")) {
                showVeterinariaEspecifico();
                //showEspecificoComunicacion();
            }
            if (b.containsKey("sell_specific")) {
                showSellSpecific();
            }
            if (b.containsKey("purchase_specific")) {
                showPurchaseSpecific();
            }
            if (b.containsKey("cash_movement_specific")) {
                showCashMovementSpecific();
            }
            if (b.containsKey("spending_specific")) {
                showSpendingSpecific();
            }
            if (b.containsKey("movement_specific")) {
                showMovementSpecific();
            }
            if (b.containsKey("provider_specific")) {
                showProviderSpecific();
                showEspecificoComunicacion();
            }
            if (b.containsKey("manufacturer_specific")) {
                showEspecificoComunicacion();
            }

            if (b.containsKey("general")) {
                String value = b.get("general").toString();
                if (value == "false") {
                    hideMostUsedGeneral();
                    hideOthersGeneral();
                } else {
                    showMostUsedGeneral();
                    showOthersGeneral();
                }
            }

            if (b.containsKey("service_schedule_specific")) {
                showServiceScheduleSpecific();
            }

            if (b.containsKey("communication_specific")) {
                showEspecificoComunicacion();
            }

        }
    }

    public void showMostUsedGeneral() {
        txt_most_used.setVisibility(View.VISIBLE);
        lyt_most_used_general.setVisibility(View.VISIBLE);
        lyt_most_used_general_2.setVisibility(View.VISIBLE);
    }
    public void hideMostUsedGeneral() {
        txt_most_used.setVisibility(View.GONE);
        lyt_most_used_general.setVisibility(View.GONE);
        lyt_most_used_general_2.setVisibility(View.GONE);
    }
    public void showOthersGeneral() {
        if (!DoctorVetApp.get().userIsAdmin()) {
            lyt_others_general.findViewById(R.id.relative_new_import).setVisibility(View.GONE);
            lyt_others_general.findViewById(R.id.relative_assoc).setVisibility(View.GONE);
            lyt_others_general_2.findViewById(R.id.relative_new_product).setVisibility(View.GONE);
            lyt_others_general_2.findViewById(R.id.relative_new_service).setVisibility(View.GONE);
            lyt_others_general_2.findViewById(R.id.relative_new_provider).setVisibility(View.GONE);
        }

        txt_others_general.setVisibility(View.VISIBLE);
        lyt_others_general.setVisibility(View.VISIBLE);
        lyt_others_general_2.setVisibility(View.VISIBLE);
        lyt_others_general_3.setVisibility(View.VISIBLE);
    }
    public void hideOthersGeneral() {
        txt_others_general.setVisibility(View.GONE);
        lyt_others_general.setVisibility(View.GONE);
        lyt_others_general_2.setVisibility(View.GONE);
        lyt_others_general_3.setVisibility(View.GONE);
    }

    public void showOwnerSpecific() {
        txt_owner_especifico.setVisibility(View.VISIBLE);
        lyt_owner_especifico.setVisibility(View.VISIBLE);
        txt_actions_owners.setVisibility(View.VISIBLE);
        lyt_actions_owners.setVisibility(View.VISIBLE);
    }
    public void showEspecificoComunicacion() {
        txt_especifico_comunicacion.setVisibility(View.VISIBLE);
        lyt_especifico_comunicacion.setVisibility(View.VISIBLE);
    }
    public void showMascotaEspecifico() {
        txt_pet_especific.setVisibility(View.VISIBLE);
        lyt_pet_especific.setVisibility(View.VISIBLE);
        txt_actions_pets.setVisibility(View.VISIBLE);
        lyt_actions_pets.setVisibility(View.VISIBLE);
        lyt_actions_pets_2.setVisibility(View.VISIBLE);
    }
    public void showAgendaEspecifico() {
        txt_agenda_especifico.setVisibility(View.VISIBLE);
        lyt_agenda_especifico.setVisibility(View.VISIBLE);
    }
    private void showUsuarioEspecifico() {
        txt_user_especifico.setVisibility(View.VISIBLE);
        lyt_user_especifico.setVisibility(View.VISIBLE);

        User user = DoctorVetApp.get().getUser();
        if (user.getLogin_type().equalsIgnoreCase("GOOGLE") || user.getLogin_type().equalsIgnoreCase("FACEBOOK")) {
            lyt_user_especifico.findViewById(R.id.relative_change_password).setVisibility(View.GONE);
        }
    }
    private void showVeterinariaEspecifico() {
        txt_veterinaria_especifico.setVisibility(View.VISIBLE);
        lyt_veterinaria_especifico.setVisibility(View.VISIBLE);
        lyt_veterinaria_especifico_2.setVisibility(View.VISIBLE);
    }
    private void showProductoEspecifico() {
        txt_product_especifico.setVisibility(View.VISIBLE);
        lyt_product_especifico.setVisibility(View.VISIBLE);
//        txt_actions_products.setVisibility(View.VISIBLE);
//        lyt_actions_products.setVisibility(View.VISIBLE);
    }
    private void showSellSpecific() {
        txt_sell_specific.setVisibility(View.VISIBLE);
        lyt_sell_specific.setVisibility(View.VISIBLE);
    }
    private void showPurchaseSpecific() {
        txt_purchase_specific.setVisibility(View.VISIBLE);
        lyt_purchase_specific.setVisibility(View.VISIBLE);
    }
    private void showCashMovementSpecific() {
        txt_cash_movement_specific.setVisibility(View.VISIBLE);
        lyt_cash_movement_specific.setVisibility(View.VISIBLE);
    }
    private void showSpendingSpecific() {
        txt_spending_specific.setVisibility(View.VISIBLE);
        lyt_spending_specific.setVisibility(View.VISIBLE);
    }

    private void showMovementSpecific() {
        txt_movement_specific.setVisibility(View.VISIBLE);
        lyt_movement_specific.setVisibility(View.VISIBLE);
    }
    private void showProviderSpecific() {
        txt_provider_specific.setVisibility(View.VISIBLE);
        lyt_provider_specific.setVisibility(View.VISIBLE);
        txt_actions_providers.setVisibility(View.VISIBLE);
        lyt_actions_providers.setVisibility(View.VISIBLE);
    }
    public void showServiceScheduleSpecific() {
        txt_service_schedule.setVisibility(View.VISIBLE);
        lyt_service_schedule_specific.setVisibility(View.VISIBLE);
    }

}