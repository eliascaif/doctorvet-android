package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Region_suggested extends Region {

    @Expose
    private Integer id_user;
    @Expose
    private Integer id_vet;

    public Region_suggested() {
    }

    public Integer getid_user() {
        return id_user;
    }
    public void setid_user(Integer id_user) {
        this.id_user = id_user;
    }
    public Integer getid_vet() {
        return id_vet;
    }
    public void setid_vet(Integer id_vet) {
        this.id_vet = id_vet;
    }

}
