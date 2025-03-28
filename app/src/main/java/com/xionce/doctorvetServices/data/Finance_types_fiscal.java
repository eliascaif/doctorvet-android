package com.xionce.doctorvetServices.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class Finance_types_fiscal {
    @Expose
    private Integer id;
    @Expose
    private String name;

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

    @NonNull
    @Override
    public String toString() {
        return getName();
    }
}
