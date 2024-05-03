package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.Date;

public class Purchase_payment {
    @Expose
    private Integer id;
    @Expose
    private Product_provider provider;
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
    public Product_provider getProvider() {
        return provider;
    }
    public void setProvider(Product_provider provider) {
        this.provider = provider;
    }
    public Finance_payment_method getFinance_payment_method() {
        return finance_payment_method;
    }
    public void setFinance_payment_method(Finance_payment_method finance_payment_method) {
        this.finance_payment_method = finance_payment_method;
    }

}
