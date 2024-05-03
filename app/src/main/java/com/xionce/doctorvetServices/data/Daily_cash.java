package com.xionce.doctorvetServices.data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Daily_cash {

    private Integer id_vet;
    private Date date;
    private BigDecimal amount;
    private String receipt;
    private String reason;
    private String type;
    private String notes;
    private Integer id;
    private String payment_type;
    private Integer deleted = 0;

    private User user;
    private Owner owner;

    private Product_provider provider;

    public Product_provider getProvider() {
        return provider;
    }

    public void setProvider(Product_provider provider) {
        this.provider = provider;
    }

    public Integer getId_vet() {
        return id_vet;
    }
    public void setId_vet(Integer id_vet) {
        this.id_vet = id_vet;
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getPayment_type() {
        return payment_type;
    }
    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }
    public Integer getDeleted() {
        return deleted;
    }

    public User getUser() {
        return user;
    }
    public Owner getOwner() {
        return owner;
    }

    public boolean is_entry(){
        if (type.equals("SELL") || type.equals("MANUAL_CASH_IN"))
            return true;

        return false;
    }

    public class Get_pagination_daily_cash extends Get_pagination {
        private ArrayList<Daily_cash> content;
        private BigDecimal total_in;
        private BigDecimal total_out;

        public ArrayList<Daily_cash> getContent() {
            return content;
        }
        public void setContent(ArrayList<Daily_cash> content) {
            this.content = content;
        }
        public BigDecimal getTotal_in() {
            return total_in;
        }
        public void setTotal_in(BigDecimal total_in) {
            this.total_in = total_in;
        }
        public BigDecimal getTotal_out() {
            return total_out;
        }
        public void setTotal_out(BigDecimal total_out) {
            this.total_out = total_out;
        }
    }

}