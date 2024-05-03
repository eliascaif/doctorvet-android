package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class Update_table {

    @Expose
    private String table_name;
    @Expose
    private Date update_time;

    public Update_table() {
    }

    public String getTable_name() {
        return table_name;
    }
    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }
    public Date getUpdate_time() {
        return update_time;
    }
    public void setUpdate_time(Date update_time) {
        this.update_time = update_time;
    }
}
