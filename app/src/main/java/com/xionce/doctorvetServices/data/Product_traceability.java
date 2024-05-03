package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Product_traceability {

    private User user;
    private Date created_at;
    private BigDecimal quantity;
    private String type;
    private BigDecimal accumulator;
    private String receipt;

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
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public BigDecimal getAccumulator() {
        return accumulator;
    }
    public void setAccumulator(BigDecimal accumulator) {
        this.accumulator = accumulator;
    }
    public String getReceipt() {
        return receipt;
    }
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public class Get_pagination_products extends Get_pagination {
        @Expose
        private ArrayList<Product_traceability> content;

        public ArrayList<Product_traceability> getContent() {
            return content;
        }
        public void setContent(ArrayList<Product_traceability> content) {
            this.content = content;
        }
    }

}
