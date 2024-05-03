package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Pet_race {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private Pet_especies especies;

    private String thumb_url;

    public Pet_race() {
        setEspecies(new Pet_especies());
    }
    public Pet_race(Integer id, String name) {
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
    public String getThumb_url() {
        return thumb_url;
    }
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }
    public Pet_especies getEspecies() {
        return especies;
    }
    public void setEspecies(Pet_especies especies) {
        this.especies = especies;
    }

    @Override
    public String toString() {
        return name;
    }

    public class Get_pagination_races extends Get_pagination {
        @Expose
        private ArrayList<Pet_race> content;

        public ArrayList<Pet_race> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet_race> content) {
            this.content = content;
        }
    }

}
