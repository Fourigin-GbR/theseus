package com.fourigin.argo.forms.customer.payment.mapping;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fourigin.argo.forms.customer.payment.PaymentMethod;

public class PaymentModule extends SimpleModule {
    private static final long serialVersionUID = 782594281898560434L;

    public PaymentModule() {
        super("PaymentMethod", Version.unknownVersion());

        setMixInAnnotation(PaymentMethod.class, PaymentMethodMixin.class);
    }
}
