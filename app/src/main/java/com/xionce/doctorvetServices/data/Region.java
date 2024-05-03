package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Region {
    @Expose
    protected Integer id;
    @Expose
    protected String city;
    @Expose
    protected String province;
    @Expose
    protected String country;

    private String friendly_name;

    public Region() {
    }
    public Region(Integer id, String friendly_name) {
        this.id = id;
        this.friendly_name = friendly_name;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getProvince() {
        return province;
    }
    public void setProvince(String province) {
        this.province = province;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getFriendly_name() {
        return friendly_name;
    }

    @Override
    public String toString() {
        return friendly_name;
    }

    public class Get_pagination_regiones extends Get_pagination {
        @Expose
        private ArrayList<Region> content;
        public ArrayList<Region> getContent() {
            return content;
        }
        public void setContent(ArrayList<Region> content) {
            this.content = content;
        }
    }

}
