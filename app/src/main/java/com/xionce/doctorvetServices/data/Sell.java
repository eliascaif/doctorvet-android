package com.xionce.doctorvetServices.data;

import android.view.View;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Sell {
    @Expose
    private Integer id;
    @Expose
    private Vet_deposit deposit;
    @Expose
    private Vet_point sell_point;
    @Expose
    private Owner owner;
    @Expose
    private Pet pet;
    @Expose
    private Date date;
    @Expose
    private BigDecimal subtotal;
    @Expose
    private BigDecimal discount_surchrage;
    @Expose
    private BigDecimal total;
    @Expose
    private ArrayList<Sell_item> items;
    @Expose
    private ArrayList<Finance_payment_method> payments;
    @Expose
    private Vet_info vet_info;

    private Integer receipt_number;
    private Integer invoiced;
    private Integer deleted;
    private String cae;
    private Date date_cae;
    private BigDecimal tax;
    private BigDecimal balance;
    private BigDecimal payments_sum;
    private Finance_types_receipts finance_types_receipt;
    private Integer sell_point_number;
    private User user;

    public Finance_types_receipts getFinance_types_receipt() {
        return finance_types_receipt;
    }
    public void setFinance_types_receipt(Finance_types_receipts finance_types_receipt) {
        this.finance_types_receipt = finance_types_receipt;
    }
    public BigDecimal getPayments_sum() {
        return payments_sum;
    }
    public void setPayments_sum(BigDecimal payments_sum) {
        this.payments_sum = payments_sum;
    }
    public Vet_info getVet_info() {
        return vet_info;
    }
    public void setVet_info(Vet_info vet_info) {
        this.vet_info = vet_info;
    }
    public Integer getSell_point_number() {
        return sell_point_number;
    }
    public void setSell_point_number(Integer sell_point_number) {
        this.sell_point_number = sell_point_number;
    }
    public Vet_deposit getDeposit() {
        return deposit;
    }
    public void setDeposit(Vet_deposit deposit) {
        this.deposit = deposit;
    }
    public Vet_point getSell_point() {
        return sell_point;
    }
    public void setSell_point(Vet_point vet_point) {
        this.sell_point = vet_point;
    }
    public ArrayList<Finance_payment_method> getPayments() {
        if (payments == null)
            payments = new ArrayList<>();

        return payments;
    }
    public BigDecimal calculateSumOfPayments() {
        return calculateSumOfPayments(payments);
    }
    public static BigDecimal calculateSumOfPayments(ArrayList<Finance_payment_method> payments) {
        BigDecimal sum_of_payments = new BigDecimal(0);
        for (Finance_payment_method payment:payments) {
            sum_of_payments = sum_of_payments.add(payment.getAmount());
        }
        return sum_of_payments;
    }
    public void setPayments(ArrayList<Finance_payment_method> payments) {
        this.payments = payments;
    }
    public BigDecimal calculateTotal() {
        BigDecimal total = new BigDecimal(0);

        for (Sell_item sell_item: items) {
            total = total.add(sell_item.getSubtotal());
        }

        return total;
    }
    public BigDecimal calculateBalance() {
        BigDecimal total = calculateTotal();
        BigDecimal sumOfPayments = calculateSumOfPayments();
        BigDecimal balance = total.subtract(sumOfPayments);
        return balance;
    }
    public ArrayList<Sell_item> getItems() {
        if (items == null)
            items = new ArrayList<>();

        return items;
    }
    public String getProductsDetails() {
        //its tricky. And confusing for the user, how do you count products in L o in Kg?
        //products details
        BigDecimal productsCount = new BigDecimal(0);
        BigDecimal servicesCount = new BigDecimal(0);

        //bulk count
        for (Sell_item sellItem:items) {
            if (sellItem.getProduct().getIs_service()) {
                servicesCount = servicesCount.add(sellItem.getQuantity());
            } else {
                if (sellItem.getSelected_unit().equalsIgnoreCase("u")
                    || sellItem.getSelected_unit().equalsIgnoreCase("pz")) {

                    productsCount = productsCount.add(sellItem.getQuantity());
                } else {
                    productsCount = productsCount.add(BigDecimal.ONE);
                }
            }
        }

        String details = "";
        if (productsCount.compareTo(BigDecimal.ZERO) > 0)
            details += "Productos: " + productsCount + " ";

        if (servicesCount.compareTo(BigDecimal.ZERO) > 0)
            details += "Servicios: " + servicesCount;

        return details;
    }
    public void setItems(ArrayList<Sell_item> items) {
        this.items = items;
    }
    public Owner getOwner() {
        return owner;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
    }
    public Pet getPet() {
        return pet;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
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
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    public BigDecimal getDiscount_surchrage() {
        return discount_surchrage;
    }
    public void setDiscount_surchrage(BigDecimal discount_surchrage) {
        this.discount_surchrage = discount_surchrage;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public Integer getReceipt_number() {
        return receipt_number;
    }
    public void setReceipt_number(Integer receipt_number) {
        this.receipt_number = receipt_number;
    }
    public Integer getInvoiced() {
        return invoiced;
    }
    public void setInvoiced(Integer invoiced) {
        this.invoiced = invoiced;
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
    public BigDecimal getTax() {
        return tax;
    }
    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public String getReceipt() {
        return getFinance_types_receipt().getShort_denomination() + getSell_point_number().toString() + "-" + getReceipt_number().toString();
    }
    public User getUser() {
        return user;
    }

    public class Get_pagination_sells extends Get_pagination {
        @Expose
        private ArrayList<Sell> content;

        private BigDecimal total;
        private BigDecimal balance;

        public ArrayList<Sell> getContent() {
            return content;
        }
        public void setContent(ArrayList<Sell> content) {
            this.content = content;
        }
        public BigDecimal getTotal() {
            return total;
        }
        public void setTotal(BigDecimal total) {
            this.total = total;
        }
        public BigDecimal getBalance() {
            return balance;
        }
        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }

    }

    public class SellsForInput {
//        private ArrayList<Product> products;
        private Vet_info vet_info;
        private ArrayList<Finance_payment_method> finance_types_payments;
        private ArrayList<Vet_deposit> deposits;
        private ArrayList<Vet_point> sell_points;

//        public ArrayList<Product> getProducts() {
//            return products;
//        }
//
//        public void setProducts(ArrayList<Product> products) {
//            this.products = products;
//        }

        public Vet_info getVet_info() {
            return vet_info;
        }
        public void setVet_info(Vet_info vet_info) {
            this.vet_info = vet_info;
        }
        public ArrayList<Finance_payment_method> getFinance_types_payments() {
            return finance_types_payments;
        }
        public void setFinance_types_payments(ArrayList<Finance_payment_method> finance_types_payments) {
            this.finance_types_payments = finance_types_payments;
        }
        public ArrayList<Vet_deposit> getDeposits() {
            return deposits;
        }
        public void setDeposits(ArrayList<Vet_deposit> deposits) {
            this.deposits = deposits;
        }
        public ArrayList<Vet_point> getSell_points() {
            return sell_points;
        }
        public void setSell_points(ArrayList<Vet_point> vet_points) {
            this.sell_points = vet_points;
        }

    }

}
