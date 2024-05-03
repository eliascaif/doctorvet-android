package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

public class Vet_point {
    @Expose
    private Integer id;
    @Expose
    private Integer number;
    @Expose
    private String name;
    @Expose
    private String type;
    @Expose
    private Integer counter;
    @Expose
    private Finance_types_receipts finance_types_receipt;

    public Finance_types_receipts getFinance_types_receipt() {
        return finance_types_receipt;
    }
    public void setFinance_types_receipt(Finance_types_receipts finance_types_receipt) {
        this.finance_types_receipt = finance_types_receipt;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Integer getCounter() {
        return counter;
    }
    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Vet_point getPolish() {
        Vet_point vetPoint = new Vet_point();
        vetPoint.setId(id);
        vetPoint.setName(name);
        return vetPoint;
    }

    @Override
    public String toString() {
        return name;
    }

}
