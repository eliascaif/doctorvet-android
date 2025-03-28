package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Pet_recipe {
    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private String notes;
    @Expose
    private Pet pet;
    @Expose
    private ArrayList<Pet_recipe_item> items;
    @Expose
    private Treatment treatment;

    private String age;
    private Vet vet;
    private User user;

    public Vet getVet() {
        return vet;
    }
    public User getUser() {
        return user;
    }

    public Pet_recipe() {
    }
    public Pet_recipe(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public void setVet(Vet vet) {
        this.vet = vet;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Treatment getTreatment() {
        return treatment;
    }
    public void setTreatment(Treatment treatment) {
        this.treatment = treatment;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public ArrayList<Pet_recipe_item> getItems() {
        return items;
    }
    public void setItems(ArrayList<Pet_recipe_item> items) {
        this.items = items;
    }
    public String getAge() {
        return age;
    }

    public class Pets_recipes_for_input {
        private ArrayList<Product> products;
        private ArrayList<Treatment> treatments;

        public ArrayList<Product> getProducts() {
            return products;
        }
        public void setProducts(ArrayList<Product> products) {
            this.products = products;
        }
        public ArrayList<Treatment> getTreatments() {
            return treatments;
        }
        public void setTreatments(ArrayList<Treatment> treatments) {
            this.treatments = treatments;
        }
    }

}
