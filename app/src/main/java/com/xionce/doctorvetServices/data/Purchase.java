package com.xionce.doctorvetServices.data;

import com.google.gson.annotations.Expose;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

public class Purchase {
    @Expose
    private Integer id;
    @Expose
    private Product_provider provider;
    @Expose
    private Vet_deposit deposit;
    @Expose
    private Date date;
    @Expose
    private BigDecimal subtotal;
    @Expose
    private BigDecimal discount_surcharge;
    @Expose
    private BigDecimal total;
    @Expose
    private ArrayList<Purchase_item> items;
    @Expose
    private ArrayList<Finance_payment_method> payments;
    @Expose
    private String receipt;

    private Integer deleted;
    private BigDecimal tax;
    private BigDecimal balance;
    private User user;
    private BigDecimal payments_sum;

    public BigDecimal getPayments_sum() {
        return payments_sum;
    }
    public void setPayments_sum(BigDecimal payments_sum) {
        this.payments_sum = payments_sum;
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

        for (Purchase_item sell_item: items) {
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
    public ArrayList<Purchase_item> getItems() {
        if (items == null)
            items = new ArrayList<>();

        return items;
    }
    public void setItems(ArrayList<Purchase_item> items) {
        this.items = items;
    }
    public Product_provider getProvider() {
        return provider;
    }
    public void setProvider(Product_provider provider) {
        this.provider = provider;
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
    public BigDecimal getDiscount_surcharge() {
        return discount_surcharge;
    }
    public void setDiscount_surcharge(BigDecimal discount_surcharge) {
        this.discount_surcharge = discount_surcharge;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public String getReceipt() {
        return receipt;
    }
    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
    public Integer getDeleted() {
        return deleted;
    }
    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
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
    public void setDeposit(Vet_deposit deposit) {
        this.deposit = deposit;
    }
    public User getUser() {
        return user;
    }
    public Vet_deposit getDeposit() {
        return deposit;
    }

    public String getProductsDetails() {
        //its tricky. And confusing for the user, how do you count products in L o in Kg?
        //products details
        BigDecimal productsCount = new BigDecimal(0);
        BigDecimal servicesCount = new BigDecimal(0);

        //bulk count
        for (Purchase_item purchaseItem:items) {
            if (purchaseItem.getProduct().getIs_service()) {
                servicesCount = servicesCount.add(purchaseItem.getQuantity());
            } else {
                if (purchaseItem.getSelected_unit().equalsIgnoreCase("u")
                        || purchaseItem.getSelected_unit().equalsIgnoreCase("pz")) {

                    productsCount = productsCount.add(purchaseItem.getQuantity());
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

    public class Get_pagination_purchases extends Get_pagination {
        @Expose
        private ArrayList<Purchase> content;

        private BigDecimal total;
        private BigDecimal balance;

        public ArrayList<Purchase> getContent() {
            return content;
        }
        public void setContent(ArrayList<Purchase> content) {
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

    public class PurchaseForInput {
        private Vet_info vet_info;
        private ArrayList<Finance_payment_method> finance_types_payments;
        private ArrayList<Vet_deposit> deposits;

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
    }

}
