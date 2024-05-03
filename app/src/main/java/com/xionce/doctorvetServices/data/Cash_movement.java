package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Cash_movement {

    public enum manual_cash_type {MANUAL_CASH_IN, MANUAL_CASH_OUT}

    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private BigDecimal amount;
    @Expose
    private manual_cash_type type;
    @Expose
    private String reason;
    @Expose
    private Finance_payment_method finance_payment_method;

    private Integer deleted;
    private User user;

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
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public void setType(manual_cash_type type) {
        this.type = type;
    }
    public manual_cash_type getType() {
        return type;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public User getUser() {
        return user;
    }
    public Finance_payment_method getFinance_payment_method() {
        return finance_payment_method;
    }
    public void setFinance_payment_method(Finance_payment_method finance_payment_method) {
        this.finance_payment_method = finance_payment_method;
    }

    public class Get_pagination_cash_movements extends Get_pagination {
        @Expose
        private ArrayList<Cash_movement> content;

        public ArrayList<Cash_movement> getContent() {
            return content;
        }
        public void setContent(ArrayList<Cash_movement> content) {
            this.content = content;
        }
    }

}
