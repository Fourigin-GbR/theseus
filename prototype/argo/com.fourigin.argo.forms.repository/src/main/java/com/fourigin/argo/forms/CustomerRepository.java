package com.fourigin.argo.forms;

import com.fourigin.argo.forms.customer.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerRepository {
    List<String> listCustomerIds();
    Map<String, String> listCustomerEntryIds();
    Customer retrieveCustomer(String id);
    void createCustomer(Customer customer);
    void updateCustomer(Customer customer);
    void deleteCustomer(String id);
}
