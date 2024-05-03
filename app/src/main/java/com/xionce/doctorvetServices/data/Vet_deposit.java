package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Vet_deposit {
    @Expose
    private Integer id;
    @Expose
    private String name;

    private Integer is_central;
    private Vet vet;

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
    public Integer getIs_central() {
        return is_central;
    }
    public void setIs_central(Integer is_central) {
        this.is_central = is_central;
    }
    public Vet getVet() {
        return vet;
    }
    public void setVet(Vet vet) {
        this.vet = vet;
    }
    public String getCompleteName() {
        return getName() + " - " + getVet().getName();
    }

    @Override
    public String toString() {
        if (getVet() != null)
            return getCompleteName();

        return name;
    }
}
