package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;

public class UserNotification {

    public enum UserNotificationsTypes {
        WELCOME,
        USER_JOIN_REQUEST,
        USER_ACCEPTED_JOIN_REQUEST,

        PENDING_AGENDA_VET,
        PENDING_AGENDA_USER,
        EXPIRED_AGENDA_VET,
        EXPIRED_AGENDA_USER,
        EXPIRED_AGENDA_ALL_VET,
        TOMORROW_AGENDA_VET,
        TOMORROW_AGENDA_USER,

        BIRTHDAY_PETS,

        PENDING_SUPPLY_VET,
        PENDING_SUPPLY_USER,
        DOMICILIARY_PENDING_SUPPLY_VET,
        DOMICILIARY_PENDING_SUPPLY_USER,
        EXPIRED_SUPPLY_VET,
        EXPIRED_SUPPLY_USER,
        DOMICILIARY_EXPIRED_SUPPLY_VET,
        NEXT_DAY_SUPPLY,
        DOMICILIARY_NEXT_DAY_SUPPLY,
        NEXT_SUPPLY_WITHOUT_STOCK,
        EXPIRED_SUPPLY_ALL_VET,
        TOMORROW_SUPPLY_VET,
        TOMORROW_SUPPLY_USER,

        OWNERS_NOTIFICATIONS,

        DEBTORS,
        DEBTORS_TOLERANCE,
        CREDITORS,
        CREDITORS_TOLERANCE,

        PRODUCTS_BELOW_MINIMUN,

        LIFE_EXPECTANCY,

        WAITING_ROOM,

        WAITING_ROOM_AUTO_DELETED,

        SUBSCRIPTION_EXP,

        IN_TRANSIT_MOVEMENT,

        LOW_FOOD,

        FIRST_HELP,

        PAYMENT_RECEIVED,
    }

    private UserNotificationsTypes notification_type;
    private String show_type;
    private String show_type_value;
    private Date shown_at;
    private Date created_at;
    private String origin_table;
    private Integer origin_table_id_record;
    private String priority;
    private String data;
    private String to_dest;
    private String time_zone;

    public String getShow_type() {
        return show_type;
    }
    public void setShow_type(String show_type) {
        this.show_type = show_type;
    }
    public String getShow_type_value() {
        return show_type_value;
    }
    public void setShow_type_value(String show_type_value) {
        this.show_type_value = show_type_value;
    }
    public Date getShown_at() {
        return shown_at;
    }
    public void setShown_at(Date shown_at) {
        this.shown_at = shown_at;
    }
    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public String getOrigin_table() {
        return origin_table;
    }
    public void setOrigin_table(String origin_table) {
        this.origin_table = origin_table;
    }
    public Integer getOrigin_table_id_record() {
        return origin_table_id_record;
    }
    public void setOrigin_table_id_record(Integer origin_table_id_record) {
        this.origin_table_id_record = origin_table_id_record;
    }
    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getTo_dest() {
        return to_dest;
    }
    public void setTo_dest(String to_dest) {
        this.to_dest = to_dest;
    }
    public String getTime_zone() {
        return time_zone;
    }
    public void setTime_zone(String time_zone) {
        this.time_zone = time_zone;
    }
    private String message;

    public UserNotification() {
    }

    public UserNotificationsTypes getNotification_type() {
        return notification_type;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public class Get_pagination_owner_notif extends Get_pagination {
        @Expose
        private ArrayList<UserNotification> content;

        public ArrayList<UserNotification> getContent() {
            return content;
        }
        public void setContent(ArrayList<UserNotification> content) {
            this.content = content;
        }
    }

}
