package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Pet_supply_POST_object {
    @Expose
    private Pet pet;
    @Expose
    private ArrayList<Pet_supply> supply_array;

    public ArrayList<Pet_supply> getSupply_array() {
        if (supply_array == null)
            supply_array = new ArrayList<>();

        return supply_array;
    }
    public void setSupply_array(ArrayList<Pet_supply> supply_array) {
        this.supply_array = supply_array;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }

}