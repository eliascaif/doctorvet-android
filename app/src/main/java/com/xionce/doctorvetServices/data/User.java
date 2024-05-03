package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.DoctorVetApp;

import java.util.ArrayList;
import java.util.Date;

public class User implements DoctorVetApp.IResourceObject {

    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private String regional_id;
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
    private String notes;
    @Expose
    private String login_type;
    @Expose
    private String password;
    @Expose
    private String rol;
    @Expose
    private String external_thumb_url;
    @Expose
    private Integer photo_deleted;
    @Expose
    private ArrayList<Resource> resources;
    @Expose
    private Vet vet;

    private String thumb_url;
    private String photo_url;
    private String access_token;
    private Date created;
    private Integer multivet;

    public User() {
    }
    public User(String email) {
        this.email = email;
    }
    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User(String name, String email, String password) {
        super();
        this.name = name;
        this.email = email;
        this.password = password;
    }
    public User(Integer id, String name, String thumb_url) {
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
    public String getNotas() {
        return notes;
    }
    public void setNotas(String notes) {
        this.notes = notes;
    }
    public String getLogin_type() {
        return login_type;
    }
    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getAccess_token() {
        return access_token;
    }
    public Date getCreated() {
        return created;
    }
    public void setCreated(Date created) {
        this.created = created;
    }
    public Vet getVet() {
        return vet;
    }
    public void setVet(Vet vet) {
        this.vet = vet;
    }
    public Region getRegion() {
        return region;
    }
    public Integer getPhoto_deleted() {
        return photo_deleted;
    }
    public void setRegion(Region region) {
        this.region = region;
    }
    public Integer getMultivet() {
        return multivet;
    }
    public String getRol() {
        return rol;
    }
    public void setRol(String rol) {
        this.rol = rol;
    }
    public void setExternal_thumb_url(String external_thumb_url) {
        this.external_thumb_url = external_thumb_url;
    }
    public ArrayList<Resource> getResources() {
        if (this.resources == null)
            this.resources = new ArrayList<Resource>();

        return resources;
    }

    public String getThumb_url() {
        return thumb_url;
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
    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    @Override
    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public class Get_pagination_users extends Get_pagination {
        @Expose
        private ArrayList<User> content;
        public ArrayList<User> getContent() {
            return content;
        }
        public void setContent(ArrayList<User> content) {
            this.content = content;
        }
    }

}
