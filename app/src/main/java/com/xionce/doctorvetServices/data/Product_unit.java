package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Product_unit {
    @Expose
    private Integer id;
    @Expose
    private String name;

    private Integer is_complex;
    private String first_unit_string;
    private String second_unit_string;

    public Product_unit() {
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
    public Integer getIs_complex() {
        return is_complex;
    }
    public String getFirst_unit_string() {
        return first_unit_string;
    }
    public String getSecond_unit_string() {
        return second_unit_string;
    }

    @Override
    public String toString() {
        return name;
    }

    public class Get_pagination_unites extends Get_pagination {
        @Expose
        private ArrayList<Product_unit> content;

        public ArrayList<Product_unit> getContent() {
            return content;
        }
        public void setContent(ArrayList<Product_unit> content) {
            this.content = content;
        }
    }

}
