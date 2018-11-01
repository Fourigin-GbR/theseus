package com.fourigin.argo.forms.models.payment.mapping;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fourigin.argo.forms.models.payment.BankAccount;
import com.fourigin.argo.forms.models.payment.Paypal;
import com.fourigin.argo.forms.models.payment.Prepayment;
import com.fourigin.argo.forms.models.payment.Sofort;

@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = BankAccount.class, name = "bank-account"),
    @JsonSubTypes.Type(value = Paypal.class, name = "paypal"),
    @JsonSubTypes.Type(value = Prepayment.class, name = "prepayment"),
    @JsonSubTypes.Type(value = Sofort.class, name = "sofort"),
})
@SuppressWarnings({
    "PMD.AbstractClassWithoutAnyMethod",
    "PMD.AbstractClassWithoutAbstractMethod"
})
public abstract class PaymentMethodMixin {
}
