package com.app.ares.query;

public class CustomerQuery {

    public static final String STATISTICS_QUERY
            ="SELECT c.total_customers, i.total_invoices, inv.total_bill FROM " +
            "(SELECT COUNT(*) total_customers FROM customer) c, " +
            "(SELECT COUNT(*) total_invoices FROM invoice) i, " +
            "(SELECT ROUND(SUM(total)) total_bill FROM invoice) inv";

}
