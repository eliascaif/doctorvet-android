package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.util.ArrayList;
import java.util.Date;

public class Vet implements DoctorVetApp.IResourceObject {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String owner;
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
    private String web_page;
    @Expose
    private String notes;
    @Expose
    private String unit_system;
    @Expose
    private String hour_format;
    @Expose
    private Integer mobile_services;
    @Expose
    private DoctorVetApp.Owners_naming owner_naming;
    @Expose
    private DoctorVetApp.Pets_naming pet_naming;
    @Expose
    private Integer photo_deleted;
    @Expose
    private String user_email;
    @Expose
    private ArrayList<Resource> resources;
    @Expose
    private Integer email_messaging;
    @Expose
    private Integer sells_save_p1;
    @Expose
    private Integer sells_planning_activity;
    @Expose
    private Integer sells_accept_suggested;
    @Expose
    private Integer sells_lock_price;
    @Expose
    private Vet_point default_sell_point;
    @Expose
    private String fiscal_id;
    @Expose
    private Finance_types_fiscal fiscal_type;

    private String photo_url;
    private String thumb_url;
    private Integer associated;
    private Integer requested;
    private Integer multiuser;
    private Boolean init_config;
    private Date subscription_until;

    public Vet() {
    }
    public Vet(Integer id) {
        this.id = id;
    }
    public Vet(Integer id, String name, String photo_url) {
        this.id = id;
        this.name = name;
        this.photo_url = photo_url;
    }

    public Vet Init() {
        owner_naming = DoctorVetApp.Owners_naming.OWNER;
        pet_naming = DoctorVetApp.Pets_naming.PATIENT;
        hour_format = "24_HS";
        mobile_services = 0;
        email_messaging = 1;
        sells_save_p1 = 0;
        sells_planning_activity = 1;
        sells_accept_suggested = 1;
        return this;
    }

    public DoctorVetApp.Owners_naming getOwner_naming() {
        return owner_naming;
    }
    public void setOwner_naming(DoctorVetApp.Owners_naming owner_naming) {
        this.owner_naming = owner_naming;
    }
    public DoctorVetApp.Pets_naming getPet_naming() {
        return pet_naming;
    }
    public void setPet_naming(DoctorVetApp.Pets_naming pet_naming) {
        this.pet_naming = pet_naming;
    }
    public String getHour_format() {
        return hour_format;
    }
    public void setHour_format(String hour_format) {
        this.hour_format = hour_format;
    }
    public Integer getMobile_services() {
        return mobile_services;
    }
    public void setMobile_services(Integer mobile_services) {
        this.mobile_services = mobile_services;
    }
    public Integer getMultiuser() {
        return multiuser;
    }
    public Integer getAssociated() {
        return associated;
    }
    public Integer getRequested() {
        return requested;
    }
    public void setRequested(Integer requested) {
        this.requested = requested;
    }
    public void setUser_email(String user_email) {
        this.user_email = user_email;
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
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
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
    public String getNotas() {
        return notes;
    }
    public void setNotas(String notes) {
        this.notes = notes;
    }
    public String getUnit_system() {
        return unit_system;
    }
    public void setUnit_system(String unit_system) {
        this.unit_system = unit_system;
    }
    public String getThumb_url() {
        return thumb_url;
    }
    public void setThumb_deleted(Integer photo_deleted) {
        this.photo_deleted = photo_deleted;
    }
    public Region getRegion() {
        return region;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public String getRegionEmail() {
        String region_email = "";
        String aux = getRegion().getFriendly_name();
        if (aux != null) region_email = aux;
        aux = email;
        if (aux != null) {
            if (!region_email.isEmpty()) region_email += " / ";
            region_email += aux;
        }
        return region_email;
    }
    public String getTempUnit() {
        if (this.unit_system.equals(DoctorVetApp.unit_system.METRIC.name()))
            return "°C";

        return "°F";
    }
    public String getWeightUnit() {
        if (this.unit_system.equals(DoctorVetApp.unit_system.METRIC.name()))
            return "kg";

        return "lb";
    }
    public Date getSubscription_until() {
        return subscription_until;
    }
    public void setSubscription_until(Date subscription_until) {
        this.subscription_until = subscription_until;
    }
    public Integer getEmail_messaging() {
        return email_messaging;
    }
    public void setEmail_messaging(Integer email_messaging) {
        this.email_messaging = email_messaging;
    }
    public Integer getSells_save_p1() {
        return sells_save_p1;
    }
    public void setSells_save_p1(Integer sells_save_p1) {
        this.sells_save_p1 = sells_save_p1;
    }
    public Integer getSells_planning_activity() {
        return sells_planning_activity;
    }
    public void setSells_planning_activity(Integer sells_planning_activity) {
        this.sells_planning_activity = sells_planning_activity;
    }
    public Integer getSells_accept_suggested() {
        return sells_accept_suggested;
    }
    public void setSells_accept_suggested(Integer sells_accept_suggested) {
        this.sells_accept_suggested = sells_accept_suggested;
    }
    public Integer getSells_lock_price() {
        return sells_lock_price;
    }
    public void setSells_lock_price(Integer sells_lock_price) {
        this.sells_lock_price = sells_lock_price;
    }
    public String getFiscal_id() {
        return fiscal_id;
    }
    public void setFiscal_id(String fiscal_id) {
        this.fiscal_id = fiscal_id;
    }
    public Vet_point getDefault_sell_point() {
        return default_sell_point;
    }
    public void setDefault_sell_point(Vet_point default_sell_point) {
        this.default_sell_point = default_sell_point;
    }
    public Finance_types_fiscal getFiscal_type() {
        return fiscal_type;
    }
    public void setFiscal_type(Finance_types_fiscal finance_types_fiscal) {
        this.fiscal_type = finance_types_fiscal;
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
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    public class Get_pagination_vets extends Get_pagination {
        @Expose
        private ArrayList<Vet> content;
        public ArrayList<Vet> getContent() {
            return content;
        }
        public void setContent(ArrayList<Vet> content) {
            this.content = content;
        }
    }

}