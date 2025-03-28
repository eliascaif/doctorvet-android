package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Pet_clinic implements DoctorVetApp.IResourceObject, Cloneable {
    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private BigDecimal temp;
    @Expose
    private BigDecimal weight;
    @Expose
    private String description;
    @Expose
    private Pet pet;
    @Expose
    private ArrayList<Resource> resources;

    private String age;
    private User user;
    private Vet vet;
    private Product_unit temp_unit;
    private Product_unit weight_unit;

    public Pet_clinic() {
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
    public BigDecimal getTemp() {
        return temp;
    }
    public void setTemp(BigDecimal temp) {
        this.temp = temp;
    }
    public BigDecimal getWeight() {
        return weight;
    }
    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public User getUser() {
        return user;
    }
    public Vet getVet() {
        return vet;
    }
    public Product_unit getTemp_unit() {
        return temp_unit;
    }
    public Product_unit getWeight_unit() {
        return weight_unit;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
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

}
