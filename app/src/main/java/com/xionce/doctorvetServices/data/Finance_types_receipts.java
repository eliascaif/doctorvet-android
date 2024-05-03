package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Finance_types_receipts {
    @Expose
    private Integer id;
    @Expose
    private Region region;
    @Expose
    private String denomination;
    @Expose
    private String short_denomination;
    @Expose
    private String type;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getDenomination() {
        return denomination;
    }
    public String getShort_denomination() {
        return short_denomination;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return denomination;
    }

}