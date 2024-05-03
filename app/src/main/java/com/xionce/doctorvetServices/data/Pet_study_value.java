package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Pet_study_value implements Cloneable {
    @Expose
    private Pet_study_item item;
    @Expose
    private String value;

    public Pet_study_value() {
    }

    public Pet_study_item getItem() {
        return item;
    }
    public void setItem(Pet_study_item item) {
        this.item = item;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

}