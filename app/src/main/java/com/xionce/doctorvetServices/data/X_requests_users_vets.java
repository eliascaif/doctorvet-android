package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class X_requests_users_vets {

    @Expose
    private Integer id;
    @Expose
    private Integer id_user;
    @Expose
    private Integer id_vet;

    private String name_vet;
    private String name_user;
    @Expose
    private String user_email;
    private String user_photourl;

    private User user;

    public X_requests_users_vets() {
    }
    public X_requests_users_vets(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
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
    public String getname_vet() {
        return name_vet;
    }
    public void setname_vet(String name_vet) {
        this.name_vet = name_vet;
    }
    public String getName_user() {
        return name_user;
    }
    public void setName_user(String name_user) {
        this.name_user = name_user;
    }
    public String getUser_email() {
        return user_email;
    }
    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
    public String getUser_photourl() {
        return user_photourl;
    }
    public void setUser_photourl(String user_photourl) {
        this.user_photourl = user_photourl;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public class Get_pagination_requests extends Get_pagination {
        @Expose
        private ArrayList<X_requests_users_vets> content;

        public ArrayList<X_requests_users_vets> getContent() {
            return content;
        }
        public void setContent(ArrayList<X_requests_users_vets> content) {
            this.content = content;
        }
    }

}