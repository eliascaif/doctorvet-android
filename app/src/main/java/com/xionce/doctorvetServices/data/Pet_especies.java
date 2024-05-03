package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Pet_especies {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String name_coloquial;
    @Expose
    private String name_coloquial_plural;

    public Pet_especies() {
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
    public String getname_coloquial() {
        return name_coloquial;
    }
    public void setname_coloquial(String name_coloquial) {
        this.name_coloquial = name_coloquial;
    }
    public String getname_coloquial_plural() {
        return name_coloquial_plural;
    }
    public void setname_coloquial_plural(String name_coloquial_plural) {
        this.name_coloquial_plural = name_coloquial_plural;
    }

    @Override
    public String toString() {
        return name;
    }

}
