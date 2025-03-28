package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Owner implements DoctorVetApp.IResourceObject {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String address;
    @Expose
    private Region region;
    @Expose
    private String latlng;
    @Expose
    private String phone;
    @Expose
    private String email;
    @Expose
    private String regional_id;
    @Expose
    private Finance_types_fiscal fiscal_type;
    @Expose
    private String notes;
    @Expose
    private Integer photo_deleted;
    @Expose
    private ArrayList<Resource> resources;

    private String photo_url;
    private String thumb_url;
    private ArrayList<Pet> pets;
    private Date last_visit;
    private Integer is_principal;
    private BigDecimal balance;
    private String reason;
    private String reason_es;
    private User user;

    public Owner() {
    }

    public Owner(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public Owner(Integer id, String name, String thumb_url) {
        this.id = id;
        this.name = name;
        this.thumb_url = thumb_url;
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
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Finance_types_fiscal getFiscal_type() {
        return fiscal_type;
    }
    public void setFiscal_type(Finance_types_fiscal fiscal_type) {
        this.fiscal_type = fiscal_type;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getThumb_url() {
        return thumb_url;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public Region getRegion() {
        return region;
    }
    public ArrayList<Pet> getPets() {
        if (pets == null)
            pets = new ArrayList<>();

        return pets;
    }
    public void setPets(ArrayList<Pet> pets) {
        this.pets = pets;
    }
    public Date getLast_visit() {
        return last_visit;
    }
    public String getEmailTelefono() {
        String email_telefono = "";
        String aux = email;
        if (aux != null) email_telefono = aux;
        aux = phone;
        if (aux != null) {
            if (!email_telefono.isEmpty()) email_telefono += " / ";
            email_telefono += aux;
        }
        return email_telefono;
    }
    public Integer getIs_principal() {
        return is_principal;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public String getReason_es() {
        if (this.reason_es == null)
            return "";
        
        return reason_es;
    }
    public void setReason_es(String reason) {
        this.reason_es = reason;
    }
    public Owner getPolish() {
        return new Owner(this.getId(), this.getName(), this.getThumb_url());
    }

    @Override
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    @Override
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    @Override
    public String getPhoto_url() {
        return photo_url;
    }

    @Override
    public void setThumb_deleted(Integer value) {
        this.photo_deleted = value;
    }

    @Override
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    @Override
    public String toString() {
        return getName();
    }

    public class Get_pagination_owners extends Get_pagination {
        @Expose
        private ArrayList<Owner> content;
        public ArrayList<Owner> getContent() {
            return content;
        }
        public void setContent(ArrayList<Owner> content) {
            this.content = content;
        }
    }

    public class Owners_for_input {

        private ArrayList<Region> regions;
        private ArrayList<Finance_types_fiscal> finance_types_fiscal;

        public ArrayList<Region> getRegions() {
            return regions;
        }
        public void setRegions(ArrayList<Region> regions) {
            this.regions = regions;
        }
        public ArrayList<Finance_types_fiscal> getFinance_types_fiscal() {
            return finance_types_fiscal;
        }
        public void setFinance_types_fiscal(ArrayList<Finance_types_fiscal> finance_types_fiscal) {
            this.finance_types_fiscal = finance_types_fiscal;
        }
    }

}

