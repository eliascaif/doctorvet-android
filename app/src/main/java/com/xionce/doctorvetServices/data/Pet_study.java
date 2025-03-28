package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.util.ArrayList;
import java.util.Date;

public class Pet_study implements DoctorVetApp.IResourceObject, Cloneable {
    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private String notes;
    @Expose
    private Pet pet;
    @Expose
    private Product product;
    @Expose
    private ArrayList<Pet_study_value> values;
    @Expose
    private ArrayList<Resource> resources;

    private String age;
    private User user;
    private Vet vet;

    public Pet_study() {
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
    public String getNotas() {
        return notes;
    }
    public ArrayList<Pet_study_value> getValues() {
        if (values == null)
            values = new ArrayList<>();

        return values;
    }
    public void setValues(ArrayList<Pet_study_value> values) {
        this.values = values;
    }
    public Vet getVet() {
        return vet;
    }
    public User getUser() {
        return user;
    }
    public Product getProduct() {
        return product;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setResources(ArrayList<Resource> resources) {
        this.resources = resources;
    }
    public String getAge() {
        return age;
    }

    @Override
    public void setThumb_url(String thumb_url) {

    }

    @Override
    public void setPhoto_url(String photo_url) {

    }

    @Override
    public String getPhoto_url() {
        return null;
    }

    @Override
    public void setThumb_deleted(Integer value) {

    }

    @Override
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class Get_pagination_estudies extends Get_pagination {
        @Expose
        private ArrayList<Pet_study> content;

        public ArrayList<Pet_study> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet_study> content) {
            this.content = content;
        }
    }

}
