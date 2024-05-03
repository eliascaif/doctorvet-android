package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Pet_gender {
    @Expose
    private Integer id;
    @Expose
    private String name;

    public Pet_gender() {
    }
    public Pet_gender(Integer id, String name) {
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

}
