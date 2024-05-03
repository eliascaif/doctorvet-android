package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Date;

public class OwnerNotification {

    public enum OwnerNotificationsTypes {
        WELCOME,
        PENDING_SUPPLY,
        DOMICILIARY_PENDING_SUPPLY,
        EXPIRED_SUPPLY,
        DOMICILIARY_EXPIRED_SUPPLY,
        NEXT_DAY_SUPPLY,
        DOMICILIARY_NEXT_DAY_SUPPLY,
        BIRTHDAY_PETS,
        DEBTORS_TOLERANCE,
        PENDING_AGENDA,
        NEXT_DAY_AGENDA,
        LOW_FOOD
    }

    private Integer id;
    private OwnerNotificationsTypes notification_type;
    private String show_type;
    private String show_type_value;
    private Date shown_at;
    private Date created_at;
    private String origin_table;
    private Integer origin_table_id_record;
    private String priority;
    private Data data;
    private String to_dest;
    private String time_zone;
    private String caption;
    private Integer message_sended;
    private String error;

    private Owner owner;
    private Pet pet;
    private String message;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNotification_type(OwnerNotificationsTypes notification_type) {
        this.notification_type = notification_type;
    }

    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
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
    public String getCaption() {
        return caption;
    }
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public OwnerNotification() {
    }

    public OwnerNotificationsTypes getNotification_type() {
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
        private ArrayList<OwnerNotification> content;

        public ArrayList<OwnerNotification> getContent() {
            return content;
        }
        public void setContent(ArrayList<OwnerNotification> content) {
            this.content = content;
        }
    }

    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public Integer getMessage_sended() {
        return message_sended;
    }
    public void setMessage_sended(Integer message_sended) {
        this.message_sended = message_sended;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

    public class Data {
        private String subject;
        private String message;

        public String getSubject() {
            return subject;
        }
        public void setSubject(String subject) {
            this.subject = subject;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
    }

}
