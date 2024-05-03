package com.xionce.doctorvetServices.data;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;

public class Pet_study_item {
    @Expose
    private Integer id;
    @Expose
    private String name;
    @Expose
    private BigDecimal min;
    @Expose
    private BigDecimal max;
    @Expose
    private Product_unit unit;

    public Pet_study_item() {
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public BigDecimal getMin() {
        return min;
    }
    public void setMin(BigDecimal min) {
        this.min = min;
    }
    public BigDecimal getMax() {
        return max;
    }
    public void setMax(BigDecimal max) {
        this.max = max;
    }
    public Product_unit getUnit() {
        return unit;
    }
    public void setUnit(Product_unit unit) {
        this.unit = unit;
    }
    public String getDescription() {
        String description = "";

        description += name;

        if (min != null)
            description += " " + min.toString();

        if (min != null && max != null)
            description += " -";

        if (max != null)
            description += " " + max.toString();

        if (unit != null)
            description += " " + unit.getName();

        return description;
    }

    @NonNull
    @Override
    public String toString() {
        return getName();
    }

    public class Get_pagination_study_items extends Get_pagination {
        @Expose
        private ArrayList<Pet_study_item> content;

        public ArrayList<Pet_study_item> getContent() {
            return content;
        }
        public void setContent(ArrayList<Pet_study_item> content) {
            this.content = content;
        }
    }

}
