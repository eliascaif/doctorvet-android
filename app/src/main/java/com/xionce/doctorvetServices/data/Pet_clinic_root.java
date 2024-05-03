package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Pet_clinic_root {

    @Expose
    private Pet_clinic clinic;
    @Expose
    private Pet_supply supply;
    @Expose
    private Pet_study study;
    @Expose
    private Pet_recipe recipe;
    @Expose
    private Pet_clinic2 clinic2;

    public Pet_clinic getClinic() {
        return clinic;
    }
    public void setClinic(Pet_clinic clinic) {
        this.clinic = clinic;
    }
    public Pet_supply getSupply() {
        return supply;
    }
    public void setSupply(Pet_supply supply) {
        this.supply = supply;
    }
    public Pet_study getStudy() {
        return study;
    }
    public void setStudy(Pet_study study) {
        this.study = study;
    }
    public Pet_recipe getRecipe() {
        return recipe;
    }
    public void setRecipe(Pet_recipe recipe) {
        this.recipe = recipe;
    }
    public Pet_clinic2 getClinic2() {
        return clinic2;
    }
    public void setClinic2(Pet_clinic2 clinic2) {
        this.clinic2 = clinic2;
    }
    public Pet_clinicAdapter.ClinicaAdapter_types getClinicaType() {
        if (clinic != null)
            return Pet_clinicAdapter.ClinicaAdapter_types.CLINIC;
        else if (supply != null)
            return Pet_clinicAdapter.ClinicaAdapter_types.SUPPLY;
        else if (study != null)
            return  Pet_clinicAdapter.ClinicaAdapter_types.STUDY;
        else if (recipe != null)
            return  Pet_clinicAdapter.ClinicaAdapter_types.RECIPE;
        else if (clinic2 != null)
            return Pet_clinicAdapter.ClinicaAdapter_types.CLINIC2;

        //TODO: trow error para el caso que no encuentre el tipo
        return Pet_clinicAdapter.ClinicaAdapter_types.CLINIC;
    }
    public Integer getIdUser() {
        Integer id_user = null;
        if (clinic != null)
            id_user = getClinic().getUser().getId();
        else if (supply != null)
            id_user = getSupply().getUser().getId();
        else if (study != null)
            id_user = getStudy().getUser().getId();
        else if (recipe != null)
            id_user = getRecipe().getUser().getId();
        else if (clinic2 != null)
            id_user = getClinic2().getUser().getId();

        return id_user;
    }

    public class Get_pagination_clinics extends Get_pagination {
        @Expose
        private ArrayList<Pet_clinic_root> content;

        public ArrayList<Pet_clinic_root> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet_clinic_root> content) {
            this.content = content;
        }
    }

}
