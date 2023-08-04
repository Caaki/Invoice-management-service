package com.app.ares.service;

import com.app.ares.domain.Customer;
import com.app.ares.domain.Invoice;
import org.springframework.data.domain.Page;

public interface CustomerService {

    Customer createCustomer(Customer customer);
    Customer updateCustomer(Customer customer);
    Page<Customer> getCustomers(int page, int size);
    Iterable<Customer> getCustomers();
    Customer getCustomer(Long id);
    Page<Customer> searchCustomers(String name, int page, int size);



    Invoice createInvoice(Invoice invoice);
    Page<Invoice> getInvoices(int page, int size);
    void addInvoiceToCustomer(Long id, Invoice invoiceId);
    Invoice getInvoice(Long id);
}
