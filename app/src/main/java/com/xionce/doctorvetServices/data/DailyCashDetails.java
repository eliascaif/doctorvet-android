package com.xionce.doctorvetServices.data;

import java.math.BigDecimal;
import java.util.ArrayList;

public class DailyCashDetails {

    private final ArrayList<DailyCashDetailsItem> details = new ArrayList<>();
    private final BigDecimal total_sells = BigDecimal.ZERO;
    private final BigDecimal total_manual_cash_in = BigDecimal.ZERO;
    private final BigDecimal total_manual_cash_out = BigDecimal.ZERO;
    private final BigDecimal total_spendings = BigDecimal.ZERO;
    private final BigDecimal total_purchases = BigDecimal.ZERO;

    public ArrayList<DailyCashDetailsItem> getDetails() {
        return details;
    }
    public BigDecimal getTotal_sells() {
        return total_sells;
    }
    public BigDecimal getTotal_manual_cash_in() {
        return total_manual_cash_in;
    }
    public BigDecimal getTotal_manual_cash_out() {
        return total_manual_cash_out;
    }
    public BigDecimal getTotal_spendings() {
        return total_spendings;
    }
    public BigDecimal getTotal_purchases() {
        return total_purchases;
    }

    public class DailyCashDetailsItem {
        private String name;
        private String type;
        private BigDecimal amount = BigDecimal.ZERO;

        public String getName() {
            return name;
        }
        public String getType() {
            return type;
        }
        public BigDecimal getAmount() {
            return amount;
        }
    }

}
