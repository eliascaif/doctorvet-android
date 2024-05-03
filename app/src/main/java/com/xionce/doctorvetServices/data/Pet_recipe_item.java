package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Pet_recipe_item implements Cloneable {
    @Expose
    private String dosage;
    @Expose
    private Product product;
    @Expose
    private Integer total_days;
    @Expose
    private Integer every_hours;

    public String getDosage() {
        return dosage;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Integer getTotal_days() {
        return total_days;
    }
    public void setTotal_days(Integer total_days) {
        this.total_days = total_days;
    }
    public Integer getEvery_hours() {
        return every_hours;
    }
    public void setEvery_hours(Integer every_hours) {
        this.every_hours = every_hours;
    }

    public String getDescription() {
        String product_name = "";
        if (product != null)
            product_name = product.getName();

        String dosage_str = "";
        if (dosage != null)
            dosage_str = dosage;

        String every_hours_str = "";
        if (every_hours != null)
            every_hours_str = every_hours.toString();

        String total_days_str = "";
        if (total_days != null)
            total_days_str = total_days.toString();

        String description = dosage_str + " cada " + every_hours_str + " horas. ";
        if (!total_days_str.isEmpty())
            description += "Días totales: " + total_days_str + ".";

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
