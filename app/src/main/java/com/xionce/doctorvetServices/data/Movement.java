package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Movement {
    @Expose
    private Integer id;
    @Expose
    private Date date;
    @Expose
    private Vet_deposit origin_deposit;
    @Expose
    private Vet_deposit destination_deposit;
    @Expose
    private ArrayList<Movement_item> items;
    @Expose
    private User user;
    @Expose
    private Vet_point movement_point;

    private String type;
    private Integer receipt_number;
    private Integer deleted;
    private String cae;
    private Date date_cae;
    private User accepted_user;
    private String receipt;

    public Vet_deposit getDestination_deposit() {
        return destination_deposit;
    }
    public void setDestination_deposit(Vet_deposit destination_deposit) {
        this.destination_deposit = destination_deposit;
    }
    public Vet_deposit getOrigin_deposit() {
        return origin_deposit;
    }
    public void setOrigin_deposit(Vet_deposit origin_deposit) {
        this.origin_deposit = origin_deposit;
    }
    public Vet_point getMovement_point() {
        return movement_point;
    }
    public void setMovement_point(Vet_point vet_point) {
        this.movement_point = vet_point;
    }
    public ArrayList<Movement_item> getItems() {
        if (items == null)
            items = new ArrayList<>();

        return items;
    }
    public void setItems(ArrayList<Movement_item> items) {
        this.items = items;
    }
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
    public Integer getReceipt_number() {
        return receipt_number;
    }
    public void setReceipt_number(Integer receipt_number) {
        this.receipt_number = receipt_number;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
    public String getCae() {
        return cae;
    }
    public void setCae(String cae) {
        this.cae = cae;
    }
    public Date getDate_cae() {
        return date_cae;
    }
    public void setDate_cae(Date date_cae) {
        this.date_cae = date_cae;
    }
    public User getUser() {
        return user;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public User getAccepted_user() {
        return accepted_user;
    }
    public void setAccepted_user(User accepted_user) {
        this.accepted_user = accepted_user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public String getReceipt() {
        return receipt;
    }
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getProductsDetails() {
        //its tricky. And confusing for the user, how do you count products in L o in Kg?
        //products details
        BigDecimal productsCount = new BigDecimal(0);

        //bulk count
        for (Movement_item movementItem:items) {

            if (movementItem.getProduct().getIs_service())
                continue;

            if (movementItem.getSelected_unit().equalsIgnoreCase("u")
                || movementItem.getSelected_unit().equalsIgnoreCase("pz")) {

                productsCount = productsCount.add(movementItem.getQuantity());
            } else {
                productsCount = productsCount.add(BigDecimal.ONE);
            }
        }

        String details = "";
        if (productsCount.compareTo(BigDecimal.ZERO) > 0)
            details += "Productos: " + productsCount + " ";

        return details;
    }

    public class Get_pagination_movements extends Get_pagination {
        @Expose
        private ArrayList<Movement> content;

        public ArrayList<Movement> getContent() {
            return content;
        }
        public void setContent(ArrayList<Movement> content) {
            this.content = content;
        }
    }

    public class MovementsForInput {
        private Vet_info vet_info;
        private ArrayList<Vet_deposit> deposits;
        private ArrayList<Vet_point> movement_points;

        public Vet_info getVet_info() {
            return vet_info;
        }
        public void setVet_info(Vet_info vet_info) {
            this.vet_info = vet_info;
        }
        public ArrayList<Vet_deposit> getDeposits() {
            return deposits;
        }
        public void setDeposits(ArrayList<Vet_deposit> deposits) {
            this.deposits = deposits;
        }
        public ArrayList<Vet_point> getMovement_points() {
            return movement_points;
        }
        public void setMovement_points(ArrayList<Vet_point> vet_points) {
            this.movement_points = vet_points;
        }
    }

}
