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
//    @Expose
//    private Date starting_date;
//    @Expose
//    private Date ending_date;
//    @Expose
//    private String dosage;
//    @Expose
//    private BigDecimal every_hours;
//    @Expose
//    private Integer total_days;
    @Expose
    private String notes;
    @Expose
    private Pet pet;
    @Expose
    private ArrayList<Pet_recipe_item> items;
    @Expose
    private Treatment treatment;

    private Vet vet;
    private User user;

//    private Product product;

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
//    public Date getStarting_date() {
//        return starting_date;
//    }
//    public void setStarting_date(Date starting_date) {
//        this.starting_date = starting_date;
//    }
//    public Date getEnding_date() {
//        return ending_date;
//    }
//    public void setEnding_date(Date ending_date) {
//        this.ending_date = ending_date;
//    }
//    public String getDosage() {
//        return dosage;
//    }
//    public void setDosage(String dosage) {
//        this.dosage = dosage;
//    }
//    public BigDecimal getEvery_hours() {
//        return every_hours;
//    }
//    public void setEvery_hours(BigDecimal every_hours) {
//        this.every_hours = every_hours;
//    }
//    public Integer getTotal_days() {
//        return total_days;
//    }
//    public void setTotal_days(Integer total_days) {
//        this.total_days = total_days;
//    }
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
//    public Product getProduct() {
//        return product;
//    }
//    public void setProduct(Product product) {
//        this.product = product;
//    }
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
