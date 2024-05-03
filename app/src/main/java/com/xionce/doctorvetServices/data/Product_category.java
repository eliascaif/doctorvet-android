package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Product_category {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private Integer is_service_category;
    @Expose
    private Integer is_food_category;

    public Product_category() {
    }
    public Product_category(Integer id, String name, Integer is_service_category) {
        super();
        this.id = id;
        this.name = name;
        this.is_service_category = is_service_category;
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
    public Integer getIs_service_category() {
        return is_service_category;
    }
    public void setIs_service_category(Integer is_service_category) {
        this.is_service_category = is_service_category;
    }
    public Integer getIs_food_category() {
        return is_food_category;
    }
    public void setIs_food_category(Integer is_food_category) {
        this.is_food_category = is_food_category;
    }

    @Override
    public String toString() {
        return name;
    }

    public class Get_pagination_categorias extends Get_pagination {
        @Expose
        private ArrayList<Product_category> content;

        public ArrayList<Product_category> getContent() {
            return content;
        }
        public void setContent(ArrayList<Product_category> content) {
            this.content = content;
        }
    }

}