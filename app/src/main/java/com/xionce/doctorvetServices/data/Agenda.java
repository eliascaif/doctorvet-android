package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;

public class Agenda {

    @Expose
    private Integer id;
    @Expose
    private String event_name;
    @Expose
    private Date begin_time;
    @Expose
    private Date end_time;
    @Expose
    private String description;
    @Expose
    private String location;
    @Expose
    private Integer executed;
    @Expose
    private Integer private_task;
    @Expose
    private User user;
    @Expose
    private Product product;
    @Expose
    private Pet pet;
    @Expose
    private Owner owner;

    public Agenda() {
    }
    public Agenda(Integer id) {
        this.id = id;
    }
    public Agenda(User user, Date begin_time) {
        this.user = user;
        this.begin_time = begin_time;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getEvent_name() {
        return event_name;
    }
    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }
    public Date getBegin_time() {
        return begin_time;
    }
    public void setBegin_time(Date begin_time) {
        this.begin_time = begin_time;
    }
    public Date getEnd_time() {
        return end_time;
    }
    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public Integer getExecuted() {
        return executed;
    }
    public void setExecuted(Integer executed) {
        this.executed = executed;
    }
    public Integer getPrivate_task() {
        return private_task;
    }
    public void setPrivate_task(Integer private_task) {
        this.private_task = private_task;
    }
    public Pet getPet() {
        return pet;
    }
    public User getUser() {
        return user;
    }
    public Product getProduct() {
        return product;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public class Get_pagination_agendas extends Get_pagination {
        @Expose
        private ArrayList<Agenda> content;

        public ArrayList<Agenda> getContent() {
            return content;
        }
        public void setContent(ArrayList<Agenda> content) {
            this.content = content;
        }
    }

}
