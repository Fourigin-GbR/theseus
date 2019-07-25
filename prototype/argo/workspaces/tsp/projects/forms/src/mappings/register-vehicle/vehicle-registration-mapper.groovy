import com.fourigin.argo.forms.models.Vehicle
import com.fourigin.argo.forms.models.VehicleRegistration
import com.fourigin.argo.forms.customer.Customer
import com.fourigin.argo.forms.customer.Address
import com.fourigin.argo.forms.customer.payment.BankAccount
import com.fourigin.argo.forms.customer.payment.Paypal
import com.fourigin.argo.forms.customer.payment.Prepayment
import com.fourigin.argo.forms.customer.payment.Sofort
import com.fourigin.argo.forms.CustomerRepository
import com.fourigin.argo.forms.FormsStoreRepository

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VehicleMapper {

    Logger logger = LoggerFactory.getLogger('com.fourigin.argo.forms.mapping.register-vehicle/vehicle-registration-mapper')

    def binding

    VehicleRegistration convert() {
        logger.info('binding: {}', binding.variables)

        def id = binding.variables['id']
        CustomerRepository customerRepository = (CustomerRepository) binding.variables['customerRepository']
        FormsStoreRepository formsStoreRepository = (FormsStoreRepository) binding.variables['formsStoreRepository']

        def info = formsStoreRepository.retrieveEntryInfo(id)
//        def customerId = binding.variables['customer.id']
        def customerId = info.header.customerId

        Customer customer = customerRepository.retrieveCustomer(customerId)

        // new nameplate
        def newNameplateOption
        def newNameplateAdditionalInfo
        def newNameplateOptionValue = binding.variables['vehicle.new-nameplate']
        if (newNameplateOptionValue == 'portal') {
            newNameplateOption = 'TO_REGISTER_BY_PORTAL'
            newNameplateAdditionalInfo = binding.variables['vehicle.new-nameplate/nameplate-customer-requirements']
        } else if (newNameplateOptionValue == 'self') {
            newNameplateOption = 'ALREADY_REGISTERED_BY_CLIENT'
            newNameplateAdditionalInfo = binding.variables['vehicle.new-nameplate/nameplate-customer-reservation']
        } else {
            newNameplateOption = 'NOT_REGISTERED'
            newNameplateAdditionalInfo = ''
        }

        // nameplate type
        def nameplateTypeOption
        def nameplateSeasonStartMonth
        def nameplateSeasonEndMonth
        def nameplateTypeOptionValue = binding.variables['vehicle.nameplate-type']
        if (nameplateTypeOptionValue == 'season') {
            nameplateTypeOption = 'SEASON'
            nameplateSeasonStartMonth = Integer.parseInt(binding.variables['vehicle.nameplate-type/season-begin'])
            nameplateSeasonEndMonth = Integer.parseInt(binding.variables['vehicle.nameplate-type/season-end'])
        } else {
            nameplateTypeOption = 'NORMAL'
            nameplateSeasonStartMonth = 0
            nameplateSeasonEndMonth = 0
        }

        // leasing option
        String leasingOptionValue = binding.variables['vehicle-data.leasing']
        boolean leasingOption = Boolean.parseBoolean(leasingOptionValue)

        // tax account
        BankAccount taxBankAccount
        def taxPaymentOptionValue = binding.variables['tax.account']
        if (taxPaymentOptionValue == 'use-stored-account') {
            String bankAccountId = binding.variables['tax.account/stored-account']

            taxBankAccount = resolveBankAccount(customer, bankAccountId)
        } else if (taxPaymentOptionValue == 'use-new-account') {
            taxBankAccount = new BankAccount()
            taxBankAccount.iban = binding.variables['tax.account/new-account-code']
            taxBankAccount.bic = binding.variables['tax.account/new-account-bankcode']
            taxBankAccount.bankName = binding.variables['tax.account/new-account-bankname']

            // applicant or other
            parseBankAccountHoldersData(taxBankAccount, 'tax.account', binding, customer)
//            def bankAccountHolderType = binding.variables['tax.account/new-account-owner']
//            switch (bankAccountHolderType) {
//                case 'applicant':
//                    taxBankAccount.accountHolder = customer.firstname + ' ' + customer.lastname
//                    taxBankAccount.accountHolderAddress = customer.mainAddress
//                    break
//                case 'other':
//                    taxBankAccount.accountHolder = binding.variables['tax.account/new-account-owner/name']
//                    taxBankAccount.accountHolderAddress = new Address(
//                            street: binding.variables['tax.account/new-account-owner/address.street'],
//                            houseNumber: binding.variables['tax.account/new-account-owner/address.house-number'],
//                            additionalInfo: binding.variables['tax.account/new-account-owner/address.additional-info'],
//                            zipCode: binding.variables['tax.account/new-account-owner/address.zip-code'],
//                            city: binding.variables['tax.account/new-account-owner/address.city'],
//                            country: 'DE'
//                    )
//                    break
//            }

//            taxBankAccount.accountHolder = binding.variables['tax.account/new-account-owner']
//            if (taxBankAccount.accountHolder == null || taxBankAccount.accountHolder.isEmpty()) {
//                taxBankAccount.accountHolder = customer.firstname + ' ' + customer.lastname
//            }

            // add a new bank account
            customer.addBankAccount(taxBankAccount)
            customerRepository.updateCustomer(customer)
        } else {
            // unsupported account type!
            taxBankAccount = null
        }

        // payment method
        def paymentMethod
        def paymentMethodValue = binding.variables['payment.methods']
        if (paymentMethodValue == 'paypal') {
            paymentMethod = new Paypal()
        } else if (paymentMethodValue == 'prepayment') {
            paymentMethod = new Prepayment()
        } else if (paymentMethodValue == 'sofort') {
            paymentMethod = new Sofort()
        } else if (paymentMethodValue == 'debit-from-existing-account') {
            String bankAccountId = binding.variables['payment.methods/stored-account']

            paymentMethod = resolveBankAccount(customer, bankAccountId)

        } else if (paymentMethodValue == 'debit-from-new-account') {
            paymentMethod = new BankAccount()
            paymentMethod.iban = binding.variables['payment.methods/new-account-code']
            paymentMethod.bic = binding.variables['payment.methods/new-account-bankcode']
            paymentMethod.bankName = binding.variables['payment.methods/new-account-bankname']

            // applicant or other
            parseBankAccountHoldersData(taxBankAccount, 'payment.methods', binding, customer)

//            paymentMethod.accountHolder = binding.variables['payment.methods/new-account-owner']
//            if (paymentMethod.accountHolder == null || paymentMethod.accountHolder.isEmpty()) {
//                paymentMethod.accountHolder = customer.firstname + ' ' + customer.lastname
//            }

            // add a new bank account
            customer.addBankAccount(paymentMethod)
            customerRepository.updateCustomer(customer)
        } else {
            // unsupported account type!
            paymentMethod = null
        }

        // handover option
        def handoverOption
        def handoverOptionValue = binding.variables['handover.options']
        if (handoverOptionValue == 'office-delivering') {
            handoverOption = 'OFFICE_DELIVERING'
        } else {
            handoverOption = 'PICKUP'
        }

        Vehicle vehicle = new Vehicle(
                previousNameplate: binding.variables['vehicle.existing-nameplate'],
                newNameplateOption: newNameplateOption,
                newNameplateAdditionalInfo: newNameplateAdditionalInfo,
                nameplateTypeOption: nameplateTypeOption,
                seasonStartMonth: nameplateSeasonStartMonth,
                seasonEndMonth: nameplateSeasonEndMonth,
                vehicleIdentNumber: binding.variables['vehicle-data.ident'],
                vehicleId: binding.variables['vehicle-data.vehicle-id'],
                leasingOrFinancingOption: leasingOption,
                insuranceId: binding.variables['vehicle-insurance.id']
        )

        return new VehicleRegistration(
                id: id,
                vehicle: vehicle,
                bankAccountForTaxPayment: taxBankAccount,
                servicePaymentMethod: paymentMethod,
                handoverOption: handoverOption
        )
    }

    static BankAccount resolveBankAccount(Customer customer, String bankAccountId) {
        for (BankAccount bankAccount : customer.bankAccounts) {
            if (bankAccount.name == bankAccountId) {
                return bankAccount
            }
        }

        return null
    }

    static void parseBankAccountHoldersData(BankAccount bankAccount, String fieldPrefix, def binding, Customer customer) {
        def bankAccountHolderType = binding.variables[fieldPrefix + '/new-account-owner']
        switch (bankAccountHolderType) {
            case 'applicant':
                bankAccount.accountHolder = customer.firstname + ' ' + customer.lastname
                bankAccount.accountHolderAddress = customer.mainAddress
                break
            case 'other':
                bankAccount.accountHolder = binding.variables[fieldPrefix + '/new-account-owner/name']
                bankAccount.accountHolderAddress = new Address(
                        street: binding.variables[fieldPrefix + '/new-account-owner/address.street'],
                        houseNumber: binding.variables[fieldPrefix + '/new-account-owner/address.house-number'],
                        additionalInfo: binding.variables[fieldPrefix + '/new-account-owner/address.additional-info'],
                        zipCode: binding.variables[fieldPrefix + '/new-account-owner/address.zip-code'],
                        city: binding.variables[fieldPrefix + '/new-account-owner/address.city'],
                        country: 'DE'
                )
                break
        }
    }
}

new VehicleMapper(binding: binding).convert()