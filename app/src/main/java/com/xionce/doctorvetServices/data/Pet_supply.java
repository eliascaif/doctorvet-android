package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;

public class Pet_supply implements Cloneable {
    @Expose
    private Integer id;
    @Expose
    private Date date_tentative;
    @Expose
    private Date date_supply;
    @Expose
    private Product product;

    private Pet pet;
    private User user;
    private Vet vet;

    public Pet_supply() {
    }
    public Pet_supply(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getDate_tentative() {
        return date_tentative;
    }
    public void setDate_tentative(Date date_tentative) {
        this.date_tentative = date_tentative;
    }
    public Object getDate_supply() {
        return date_supply;
    }
    public void setDate_supply(Date date_supply) {
        this.date_supply = date_supply;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public boolean id_supplied() {
        return getDate_supply() != null;
    }
    public Vet getVet() {
        return vet;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

    public class Get_pagination_supply extends Get_pagination {
        @Expose
        private ArrayList<Pet_supply> content;

        public ArrayList<Pet_supply> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet_supply> content) {
            this.content = content;
        }
    }

}