package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;

public class LowFood {

    private Integer days_from_sell;
    private BigDecimal total_quantity;
    private BigDecimal rest_percentage;
    private Pet pet;
    private Product product;

    public Integer getDays_from_sell() {
        return days_from_sell;
    }
    public void setDays_from_sell(Integer days_from_sell) {
        this.days_from_sell = days_from_sell;
    }
    public BigDecimal getTotal_quantity() {
        return total_quantity;
    }
    public void setTotal_quantity(BigDecimal total_quantity) {
        this.total_quantity = total_quantity;
    }
    public BigDecimal getRest_percentage() {
        return rest_percentage;
    }
    public void setRest_percentage(BigDecimal rest_percentage) {
        this.rest_percentage = rest_percentage;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public class Get_pagination_low_food extends Get_pagination {
        @Expose
        private ArrayList<LowFood> content;

        public ArrayList<LowFood> getContent() {
            return content;
        }
        public void setContent(ArrayList<LowFood> content) {
            this.content = content;
        }
    }

}
