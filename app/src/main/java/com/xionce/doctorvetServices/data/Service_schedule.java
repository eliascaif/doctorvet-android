package com.xionce.doctorvetServices.data;

import android.content.Context;

import com.google.gson.annotations.Expose;
import com.xionce.doctorvetServices.EditVetScheduleActivity;
import com.xionce.doctorvetServices.utilities.HelperClass;

import java.util.Date;

public class Service_schedule implements Cloneable {
    @Expose
    private EditVetScheduleActivity.weekdays weekday;
    @Expose
    private String starting_hour;
    @Expose
    private String ending_hour;
    @Expose
    private Product service;
    @Expose
    private User user;

    private Integer id;
    private String starting_hour_local;
    private String ending_hour_local;

    public String getStarting_hour() {
        return starting_hour;
    }
    public void setStarting_hour(String starting_hour) {
        this.starting_hour = starting_hour;
    }
    public String getEnding_hour() {
        return ending_hour;
    }
    public void setEnding_hour(String ending_hour) {
        this.ending_hour = ending_hour;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public EditVetScheduleActivity.weekdays getWeekday() {
        return weekday;
    }
    public void setWeekday(EditVetScheduleActivity.weekdays weekday) {
        this.weekday = weekday;
    }
    public String getStarting_hour_local(Context ctx) {
        Date hour = HelperClass.getShortTimeFromMySqlString(starting_hour);
        return HelperClass.getShortTime_inStr(hour, ctx);

    }
    public void setStarting_hour_local(String starting_hour_local) {
        this.starting_hour_local = starting_hour_local;
    }
    public String getEnding_hour_local(Context ctx) {
        Date hour = HelperClass.getShortTimeFromMySqlString(ending_hour);
        return HelperClass.getShortTime_inStr(hour, ctx);
    }
    public void setEnding_hour_local(String ending_hour_local) {
        this.ending_hour_local = ending_hour_local;
    }
    public Product getService() {
        return service;
    }
    public void setService(Product service) {
        this.service = service;
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
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
