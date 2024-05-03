package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Pet_pelage {
    @Expose
    private Integer id;
    @Expose
    private String name;

    public Pet_pelage() {
    }
    public Pet_pelage(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    @Override
    public String toString() {
        return name;
    }

    public class Get_pagination_pelages extends Get_pagination {
        @Expose
        private ArrayList<Pet_pelage> content;

        public ArrayList<Pet_pelage> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet_pelage> content) {
            this.content = content;
        }
    }
}
