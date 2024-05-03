package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;

public class Movement_item implements Cloneable {
    @Expose
    private BigDecimal quantity;
    @Expose
    private Product_unit unit;
    @Expose
    private String selected_unit;
    @Expose
    private Product product;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Product_unit getUnit() {
        return unit;
    }
    public void setUnit(Product_unit unit) {
        this.unit = unit;
    }
    public String getSelected_unit() {
        return selected_unit;
    }
    public void setSelected_unit(String selected_unit) {
        this.selected_unit = selected_unit;
    }
    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return null;
    }

}
