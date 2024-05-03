package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Spending {
    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private Finance_payment_method finance_payment_method;
    @Expose
    private BigDecimal amount;
    @Expose
    private String receipt;
    @Expose
    private String reason;

    private Integer deleted;
    private User user;

    public String getReceipt() {
        return receipt;
    }
    public void setReceipt(String receipt) {
        this.receipt = receipt;
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
    public User getUser() {
        return user;
    }
    public Finance_payment_method getFinance_payment_method() {
        return finance_payment_method;
    }
    public void setFinance_payment_method(Finance_payment_method finance_payment_method) {
        this.finance_payment_method = finance_payment_method;
    }

    public class Get_pagination_spendings extends Get_pagination {
        @Expose
        private ArrayList<Spending> content;

        public ArrayList<Spending> getContent() {
            return content;
        }
        public void setContent(ArrayList<Spending> content) {
            this.content = content;
        }
    }

}
