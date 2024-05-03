package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Log {

    private User user;
    private Date created_at;
    private String operation;
    private String table_name;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Date getCreated_at() {
        return created_at;
    }
    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getTable_name() {
        return table_name;
    }
    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    public class Get_pagination_logs extends Get_pagination {
        @Expose
        private ArrayList<Log> content;

        public ArrayList<Log> getContent() {
            return content;
        }
        public void setContent(ArrayList<Log> content) {
            this.content = content;
        }
    }

}
