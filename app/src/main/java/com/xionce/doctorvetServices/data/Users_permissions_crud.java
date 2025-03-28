package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Users_permissions_crud {
    @Expose
    public int create;
    @Expose
    public int read;
    @Expose
    public int update;
    @Expose
    public int delete;

    public void setAllOne() {
        this.create = 1;
        this.read = 1;
        this.update = 1;
        this.delete = 1;
    }
    public void setAllZero() {
        this.create = 0;
        this.read = 0;
        this.update = 0;
        this.delete = 0;
    }
}
