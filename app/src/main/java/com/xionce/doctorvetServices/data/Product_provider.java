package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Product_provider implements DoctorVetApp.IResourceObject {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String address;
    @Expose
    private Region region;
    @Expose
    private String latlng;
    @Expose
    private String phone;
    @Expose
    private String email;
    @Expose
    private String web_page;
    @Expose
    private String notes;
    @Expose
    private Integer photo_deleted;
    @Expose
    private ArrayList<Resource> resources;

    private String photo_url;
    private String thumb_url;
    private BigDecimal balance;

    public Product_provider() {
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getNotas() {
        return notes;
    }
    public void setNotas(String notes) {
        this.notes = notes;
    }
    public String getThumb_url() {
        return thumb_url;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public String getPhoto_url() {
        return photo_url;
    }
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    public Product_provider getPolish() {
        Product_provider productProvider = new Product_provider();
        productProvider.setId(id);
        productProvider.setName(name);
        productProvider.setThumb_url(thumb_url);
        return productProvider;
    }

    @Override
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    @Override
    public void setThumb_deleted(Integer photo_deleted) {
        this.photo_deleted = photo_deleted;
    }

    public class Get_pagination_providers extends Get_pagination {
        @Expose
        private ArrayList<Product_provider> content;

        public ArrayList<Product_provider> getContent() {
            return content;
        }
        public void setContent(ArrayList<Product_provider> content) {
            this.content = content;
        }
    }

}
