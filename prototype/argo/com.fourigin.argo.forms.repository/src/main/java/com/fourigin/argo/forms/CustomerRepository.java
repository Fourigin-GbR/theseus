package com.fourigin.argo.forms;

import com.fourigin.argo.forms.customer.Customer;

import java.util.List;

public interface CustomerRepository {
    List<String> listCustomerIds();
    Customer retrieveCustomer(String id);
    void createCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(String id);
}
