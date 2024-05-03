package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;

public class Waiting_room {
//    @Expose
    private Integer id;
    @Expose
    private Integer id_vet;
    @Expose
    private Integer created_by_id_user;
    @Expose
    private Integer id_pet;
    @Expose
    private Integer id_owner;
    @Expose
    private Integer attended_by_id_user;
    @Expose
    private Integer pre_attended_by_id_user;

    private Date created_at;
    private Integer processed;
    private Integer deleted;

    @Expose
    private String site;

    // added
    private User created_by_user;
    private User pre_attended_by_user;
    private User attended_by_user;
    private Pet pet;
    private Owner owner;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId_vet() {
        return id_vet;
    }
    public void setId_vet(Integer id_vet) {
        this.id_vet = id_vet;
    }
    public Integer getCreated_by_id_user() {
        return created_by_id_user;
    }
    public void setCreated_by_id_user(Integer created_by_id_user) {
        this.created_by_id_user = created_by_id_user;
    }
    public Integer getId_pet() {
        return id_pet;
    }
    public void setId_pet(Integer id_pet) {
        this.id_pet = id_pet;
    }
    public Integer getId_owner() {
        return id_owner;
    }
    public void setId_owner(Integer id_owner) {
        this.id_owner = id_owner;
    }
    public Integer getAttended_by_id_user() {
        return attended_by_id_user;
    }
    public void setAttended_by_id_user(Integer attended_by_id_user) {
        this.attended_by_id_user = attended_by_id_user;
    }
    public Integer getPre_attended_by_id_user() {
        return pre_attended_by_id_user;
    }
    public void setPre_attended_by_id_user(Integer pre_attended_by_id_user) {
        this.pre_attended_by_id_user = pre_attended_by_id_user;
    }
    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public Integer getProcessed() {
        return processed;
    }
    public void setProcessed(Integer processed) {
        this.processed = processed;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public String getSite() {
        return site;
    }
    public void setSite(String site) {
        this.site = site;
    }

    public User getCreated_by_user() {
        return created_by_user;
    }
    public void setCreated_by_user(User created_by_user) {
        this.created_by_user = created_by_user;
    }
    public User getAttended_by_user() {
        return attended_by_user;
    }
    public void setAttended_by_user(User attended_by_user) {
        this.attended_by_user = attended_by_user;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public User getPre_attended_by_user() {
        return pre_attended_by_user;
    }
    public void setPre_attended_by_user(User pre_attended_by_user) {
        this.pre_attended_by_user = pre_attended_by_user;
    }

    public class Get_pagination_waiting_rooms extends Get_pagination {
        @Expose
        private ArrayList<Waiting_room> content;
        public ArrayList<Waiting_room> getContent() {
            return content;
        }
        public void setContent(ArrayList<Waiting_room> content) {
            this.content = content;
        }
    }

}
