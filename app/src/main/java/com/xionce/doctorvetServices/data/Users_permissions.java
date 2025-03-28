package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Users_permissions {

    @Expose
    public ActionsPermissions actions_permissions;
    @Expose
    public ReportsPermissions reports_permissions;

    public Users_permissions() {
        this.actions_permissions = new ActionsPermissions();
        this.reports_permissions = new ReportsPermissions();
    }

//    public void setOwner() {
//        this.actions_permissions.setOwner();
//        this.reports_permissions.setOwner();
//    }
//    public void setAdmin() {
//        this.actions_permissions.setAdmin();
//        this.reports_permissions.setAdmin();
//    }
//    public void setUser() {
//        this.actions_permissions.setUser();
//        this.reports_permissions.setUser();
//    }

    public class ActionsPermissions {

        @Expose
        public Users_permissions_crud agenda;
        @Expose
        public Users_permissions_crud cash_movements;
        @Expose
        public Users_permissions_crud internal_movements;
        @Expose
        public Users_permissions_crud owners;
        @Expose
        public Users_permissions_crud pets_clinic;
        @Expose
        public Users_permissions_crud pets;
        @Expose
        public Users_permissions_crud products_manufacturers;
        @Expose
        public Users_permissions_crud products_providers;
        @Expose
        public Users_permissions_crud products;
        @Expose
        public Users_permissions_crud purchases_payments;
        @Expose
        public Users_permissions_crud purchases;
        @Expose
        public Users_permissions_crud sells_payments;
        @Expose
        public Users_permissions_crud sells;
        @Expose
        public Users_permissions_crud spendings;
        @Expose
        public Users_permissions_crud vets;
        @Expose
        public Users_permissions_crud waiting_rooms;

        public ActionsPermissions() {
            this.agenda = new Users_permissions_crud();
            this.cash_movements = new Users_permissions_crud();
            this.internal_movements = new Users_permissions_crud();
            this.owners = new Users_permissions_crud();
            this.pets_clinic = new Users_permissions_crud();
            this.pets = new Users_permissions_crud();
            this.products_manufacturers = new Users_permissions_crud();
            this.products_providers = new Users_permissions_crud();
            this.products = new Users_permissions_crud();
            this.purchases_payments = new Users_permissions_crud();
            this.purchases = new Users_permissions_crud();
            this.sells_payments = new Users_permissions_crud();
            this.sells = new Users_permissions_crud();
            this.spendings = new Users_permissions_crud();
            this.vets = new Users_permissions_crud();
            this.waiting_rooms = new Users_permissions_crud();
        }

//        public void setOwner() {
//            this.agenda.setAllOne();
//            this.cash_movements.setAllOne();
//            this.internal_movements.setAllOne();
//            this.owners.setAllOne();
//            this.pets_clinic.setAllOne();
//            this.pets.setAllOne();
//            this.products_manufacturers.setAllOne();
//            this.products_providers.setAllOne();
//            this.products.setAllOne();
//            this.purchases_payments.setAllOne();
//            this.purchases.setAllOne();
//            this.sells_payments.setAllOne();
//            this.sells.setAllOne();
//            this.spendings.setAllOne();
//            this.vets.setAllOne();
//            this.waiting_rooms.setAllOne();
//        }

//        public void setAdmin() {
//            this.setOwner();
//
//            this.cash_movements.update = 0;
//            this.cash_movements.delete = 0;
//
//            this.internal_movements.update = 0;
//            this.internal_movements.delete = 0;
//
//            this.products.update = 0;
//            this.products.delete = 0;
//
//            this.purchases_payments.update = 0;
//            this.purchases_payments.delete = 0;
//
//            this.purchases.update = 0;
//            this.purchases.delete = 0;
//
//            this.sells_payments.update = 0;
//            this.sells_payments.delete = 0;
//
//            this.sells.update = 0;
//            this.sells.delete = 0;
//
//            this.spendings.update = 0;
//            this.spendings.delete = 0;
//
//            this.vets.update = 0;
//            this.vets.delete = 0;
//        }

//        public void setUser() {
//            this.agenda.setAllOne();
//
//            this.cash_movements.create = 1;
//            this.cash_movements.read = 1;
//            this.cash_movements.update = 0;
//            this.cash_movements.delete = 0;
//
//            this.internal_movements.setAllZero();
//
//            this.owners.create = 1;
//            this.owners.read = 1;
//            this.owners.update = 0;
//            this.owners.delete = 0;
////            this.owners.search = 1;
//
//            this.pets_clinic.create = 1;
//            this.pets_clinic.read = 1;
//            this.pets_clinic.update = 0;
//            this.pets_clinic.delete = 0;
//
//            this.pets.create = 1;
//            this.pets.read = 1;
//            this.pets.update = 0;
//            this.pets.delete = 0;
////            this.pets.search = 1;
//
//            this.products_manufacturers.setAllZero();
//
//            this.products_providers.setAllZero();
//
//            this.products.create = 0;
//            this.products.read = 1;
//            this.products.update = 0;
//            this.products.delete = 0;
////            this.products.search = 1;
//
//            this.purchases_payments.setAllZero();
//
//            this.purchases.setAllZero();
//
//            this.sells_payments.create = 1;
//            this.sells_payments.read = 1;
//            this.sells_payments.update = 0;
//            this.sells_payments.delete = 0;
//
//            this.sells.create = 1;
//            this.sells.read = 1;
//            this.sells.update = 0;
//            this.sells.delete = 0;
//
//            this.spendings.create = 1;
//            this.spendings.read = 1;
//            this.spendings.update = 0;
//            this.spendings.delete = 0;
//
//            this.vets.create = 0;
//            this.vets.read = 1;
//            this.vets.update = 0;
//            this.vets.delete = 0;
//
//            this.waiting_rooms.create = 1;
//            this.waiting_rooms.read = 1;
//            this.waiting_rooms.update = 0;
//            this.waiting_rooms.delete = 0;
//        }
    }
    public class ReportsPermissions {

        @Expose
        public int creditors;
        @Expose
        public int creditors_tolerance;
        @Expose
        public int agenda;
        @Expose
        public int tomorrow_agenda_vet;
        @Expose
        public int tomorrow_agenda_user;
        @Expose
        public int pending_agenda_vet;
        @Expose
        public int pending_agenda_user;
        @Expose
        public int expired_agenda_vet;
        @Expose
        public int expired_agenda_user;
        @Expose
        public int expired_agenda_all_vet;
        @Expose
        public int low_food;
        @Expose
        public int purchases;
        @Expose
        public int birthday_pets;
        @Expose
        public int debtors;
        @Expose
        public int debtors_tolerance;
        @Expose
        public int life_expectancy;
        @Expose
        public int spendings;
        @Expose
        public int logs;
        @Expose
        public int cash_movements;
        @Expose
        public int owners_notifications;
        @Expose
        public int new_vet_users;
        @Expose
        public int products;
//        @Expose
//        public int products_traceability;
//        @Expose
//        public int products_below_minimun;
        @Expose
        public int movements;
        @Expose
        public int in_transit_movements;
        @Expose
        public int waiting_room;
        @Expose
        public int waiting_room_auto_deleted;
//        @Expose
//        public int services_barcodes;
        @Expose
        public int user_join_request;
        @Expose
        public int supply;
        @Expose
        public int tomorrow_supply_vet;
        @Expose
        public int tomorrow_supply_user;
        @Expose
        public int domiciliary_pending_supply_vet;
        @Expose
        public int domiciliary_pending_supply_user;
        @Expose
        public int domiciliary_expired_supply_vet;
        @Expose
        public int pending_supply_vet;
        @Expose
        public int pending_supply_user;
        @Expose
        public int expired_supply_vet;
        @Expose
        public int expired_supply_user;
        @Expose
        public int expired_supply_all_vet;
        @Expose
        public int sells;
//        @Expose
//        public int sells_xlsx;
//        @Expose
//        public int supply_xlsx;

//        public void setOwner() {
//            this.creditors = 1;
//            this.creditors_tolerance = 1;
//            this.agenda = 1;
//            this.tomorrow_agenda_vet = 1;
//            this.tomorrow_agenda_user = 1;
//            this.pending_agenda_vet = 1;
//            this.pending_agenda_user = 1;
//            this.expired_agenda_vet = 1;
//            this.expired_agenda_user = 1;
//            this.expired_agenda_all_vet = 1;
//            this.low_food = 1;
//            this.purchases = 1;
//            this.birthday_pets = 1;
//            this.debtors = 1;
//            this.debtors_tolerance = 1;
//            this.life_expectancy = 1;
//            this.spendings = 1;
//            this.logs = 1;
//            this.cash_movements = 1;
//            this.owners_notifications = 1;
//            this.new_vet_users = 1;
//            this.products_excel = 1;
//            this.products_traceability = 1;
//            this.products_below_minimun = 1;
//            this.movements = 1;
//            this.in_transit_movements = 1;
//            this.waiting_room = 1;
//            this.waiting_room_auto_deleted = 1;
//            this.services_barcodes = 1;
//            this.user_join_request = 1;
//            this.supply = 1;
//            this.tomorrow_supply_vet = 1;
//            this.tomorrow_supply_user = 1;
//            this.domiciliary_pending_supply_vet = 1;
//            this.domiciliary_pending_supply_user = 1;
//            this.domiciliary_expired_supply_vet = 1;
//            this.pending_supply_vet = 1;
//            this.pending_supply_user = 1;
//            this.expired_supply_vet = 1;
//            this.expired_supply_user = 1;
//            this.expired_supply_all_vet = 1;
//            this.sells = 1;
//            this.sells_xlsx = 1;
//            this.supply_xlsx = 1;
//        }

//        public void setAdmin() {
//            this.setOwner();
//        }

//        public void setUser() {
//            this.creditors = 0;
//            this.creditors_tolerance = 0;
//            this.agenda = 1;
//            this.tomorrow_agenda_vet = 0;
//            this.tomorrow_agenda_user = 1;
//            this.pending_agenda_vet = 0;
//            this.pending_agenda_user = 1;
//            this.expired_agenda_vet = 0;
//            this.expired_agenda_user = 1;
//            this.expired_agenda_all_vet = 0;
//            this.low_food = 0;
//            this.purchases = 0;
//            this.birthday_pets = 1;
//            this.debtors = 0;
//            this.debtors_tolerance = 0;
//            this.life_expectancy = 0;
//            this.spendings = 0;
//            this.logs = 0;
//            this.cash_movements = 0;
//            this.owners_notifications = 0;
//            this.new_vet_users = 0;
//            this.products_excel = 0;
//            this.products_traceability = 0;
//            this.products_below_minimun = 0;
//            this.movements = 0;
//            this.in_transit_movements = 0;
//            this.waiting_room = 1;
//            this.waiting_room_auto_deleted = 1;
//            this.services_barcodes = 0;
//            this.user_join_request = 0;
//            this.supply = 0;
//            this.tomorrow_supply_vet = 0;
//            this.tomorrow_supply_user = 1;
//            this.domiciliary_pending_supply_vet = 0;
//            this.domiciliary_pending_supply_user = 1;
//            this.domiciliary_expired_supply_vet = 0;
//            this.pending_supply_vet = 0;
//            this.pending_supply_user = 1;
//            this.expired_supply_vet = 0;
//            this.expired_supply_user = 1;
//            this.expired_supply_all_vet = 0;
//            this.sells = 0;
//            this.sells_xlsx = 0;
//            this.supply_xlsx = 0;
//        }
    }
}
