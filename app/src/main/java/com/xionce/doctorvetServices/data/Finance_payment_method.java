package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Finance_payment_method {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private BigDecimal amount;
    @Expose
    private Date date;

    private Integer deleted = 0;

    public Finance_payment_method() {
    }
    public Finance_payment_method(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public Integer getDeleted() {
        return deleted;
    }

    @Override
    public String toString() {
        return name;
    }

    public class Get_pagination_cash_movement extends Get_pagination {
        @Expose
        private ArrayList<Finance_payment_method> content;

        public ArrayList<Finance_payment_method> getContent() {
            return content;
        }
        public void setContent(ArrayList<Finance_payment_method> content) {
            this.content = content;
        }
    }

}
