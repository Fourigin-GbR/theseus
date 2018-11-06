package com.fourigin.argo.forms.initialization;

import com.fourigin.argo.forms.CustomerRepository;
import com.fourigin.argo.forms.customer.Customer;
import com.fourigin.argo.forms.customer.payment.BankAccount;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CustomerExternalValueResolver implements ExternalValueResolver {
    private CustomerRepository customerRepository;

    public static final String STORED_ACCOUNTS = "stored-accounts";

    public CustomerExternalValueResolver(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Set<String> getSupportedKeys() {
        return Collections.singleton(
            STORED_ACCOUNTS
        );
    }

    @Override
    public Map<String, Object> resolveExternalValue(String customerId, String key) {
        Objects.requireNonNull(customerId, "customerId must not be null!");

        Customer customer = customerRepository.retrieveCustomer(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("No customer found for id '" + customerId + "'!");
        }

        Set<String> supportedKeys = getSupportedKeys();
        if (!supportedKeys.contains(key)) {
            throw new IllegalArgumentException("Unsupported key '" + key + "' found, expected one of " + supportedKeys + "!");
        }

        switch (key) {
            case STORED_ACCOUNTS:
                return resolveStoredAccounts(customer);
            default:
                throw new IllegalArgumentException("Unsupported key '" + key + "' found, expected one of " + supportedKeys + "!");

        }
    }

    private Map<String, Object> resolveStoredAccounts(Customer customer) {
        Set<BankAccount> bankAccounts = customer.getBankAccounts();
        if (bankAccounts == null || bankAccounts.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Object> result = new HashMap<>();

        for (BankAccount bankAccount : bankAccounts) {
            String name = bankAccount.getName();
            String displayName = bankAccount.getDisplayName();
            result.put(name, displayName);
        }

        return result;
    }
}
