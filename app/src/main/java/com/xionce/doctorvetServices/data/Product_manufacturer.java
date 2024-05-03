package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Product_manufacturer {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String address;
    @Expose
    private Integer id_region;
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


    private String photo_url;
    private String thumb_url;

    private ArrayList<Product> products;
    public ArrayList<Product> getProducts() {
        return products;
    }
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    //
    private Region region = new Region();

    public Product_manufacturer() {
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
    public void setId_region(Integer id_region) {
        this.id_region = id_region;
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
    public String getWeb_page() {
        return web_page;
    }
    public String getNotas() {
        return notes;
    }
    public void setNotas(String notes) {
        this.notes = notes;
    }
    public String getPhoto_url() {
        return photo_url;
    }
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    //added
    public Region getRegion() {
        return region;
    }

    public class Get_pagination_manufacturers extends Get_pagination {
        @Expose
        private ArrayList<Product_manufacturer> content;

        public ArrayList<Product_manufacturer> getContent() {
            return content;
        }
        public void setContent(ArrayList<Product_manufacturer> content) {
            this.content = content;
        }
    }

}
