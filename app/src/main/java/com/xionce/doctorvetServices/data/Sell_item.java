package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Sell_item implements Cloneable {

    @Expose
    private Product product;
    @Expose
    private BigDecimal price;
    @Expose
    private BigDecimal quantity;
    @Expose
    private BigDecimal discount_surcharge;
    @Expose
    private String selected_unit;

    private BigDecimal tax;
    private BigDecimal subtotal;
    private BigDecimal subtotal_tax;

    public void setProduct(Product product) {
        this.product = product;
    }
    public Product getProduct() {
        return product;
    }
    public String getSelected_unit() {
        return selected_unit;
    }
    public void setSelected_unit(String selected_unit) {
        this.selected_unit = selected_unit;
    }
    public BigDecimal getPrice() {
        return price;//.setScale(2, RoundingMode.HALF_UP);
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getQuantity() {
        return quantity;//.setScale(2, RoundingMode.HALF_UP);
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
    public BigDecimal getDiscount_surcharge() {
        return discount_surcharge;//.setScale(2, RoundingMode.HALF_UP);
    }
    public void setDiscount_surcharge(BigDecimal discount_surcharge) {
        this.discount_surcharge = discount_surcharge;
    }
    public BigDecimal getTax() {
        return tax.setScale(2, RoundingMode.HALF_UP);
    }
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
    public BigDecimal getSubtotal() {
        BigDecimal quantity = new BigDecimal(0);
        BigDecimal price = new BigDecimal(0);
        BigDecimal discount = new BigDecimal(0);
        BigDecimal subtotal = new BigDecimal(0);

        if (getQuantity() != null)
            quantity = getQuantity();

        if (getPrice() != null)
            price = getPrice();

        if (getDiscount_surcharge() != null)
            discount = getDiscount_surcharge();

        if (!discount.equals(BigDecimal.valueOf(0))) {
            price = price.subtract(price.multiply(discount).divide(BigDecimal.valueOf(100)));
        }

        subtotal = quantity.multiply(price);

        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }
    public BigDecimal getSubtotal_tax() {
        return subtotal_tax;
    }
    public void setSubtotal_tax(BigDecimal subtotal_tax) {
        this.subtotal_tax = subtotal_tax;
    }
    public String getDescription() {
        String unit = getSelected_unit();
        if (unit == null && getProduct().getUnit() != null) {
            if (getProduct().getUnit().getIs_complex() == 1)
                unit = getProduct().getUnit().getSecond_unit_string();
            else
                unit = getProduct().getUnit().getFirst_unit_string();
        }

        String description = getQuantity() + " " + unit;
        description += " x ";
        description += getPrice();

        //discount_surcharge
        BigDecimal discount_surcharge = getDiscount_surcharge();
        if (discount_surcharge == null)
            return description;

        if (discount_surcharge.compareTo(BigDecimal.ZERO) > 0) {
            description += " (x -" + getDiscount_surcharge().toString() + "%)";
        } else if (discount_surcharge.compareTo(BigDecimal.ZERO) < 0) {
            description += " (x +" + getDiscount_surcharge().abs().toString() + "%)";
        }

        return description;
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
