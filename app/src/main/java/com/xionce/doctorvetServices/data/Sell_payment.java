package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.Date;

public class Sell_payment {
    @Expose
    private Integer id;
    @Expose
    private Owner owner;
    @Expose
    private Finance_payment_method finance_payment_method;
    @Expose
    private Date date;
    @Expose
    private BigDecimal amount;

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
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public Finance_payment_method getFinance_payment_method() {
        return finance_payment_method;
    }
    public void setFinance_payment_method(Finance_payment_method finance_payment_method) {
        this.finance_payment_method = finance_payment_method;
    }

}
