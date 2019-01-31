import com.fourigin.argo.forms.models.Vehicle
import com.fourigin.argo.forms.models.VehicleRegistration
import com.fourigin.argo.forms.customer.Customer
import com.fourigin.argo.forms.customer.payment.BankAccount
import com.fourigin.argo.forms.customer.payment.Paypal
import com.fourigin.argo.forms.customer.payment.Prepayment
import com.fourigin.argo.forms.customer.payment.Sofort
import com.fourigin.argo.forms.CustomerRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VehicleMapper {

    Logger logger = LoggerFactory.getLogger('com.fourigin.argo.forms.mapping.register-vehicle/vehicle-registration-mapper')

    def binding

    VehicleRegistration convert() {
        logger.info('binding: {}', binding.variables)

        def id = binding.variables['id']
        def customerId = binding.variables['customer.id']
        CustomerRepository customerRepository = (CustomerRepository) binding.variables['customerRepository']
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
        }
        else {
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
            taxBankAccount.accountHolder = binding.variables['tax.account/new-account-owner']
            if (taxBankAccount.accountHolder == null || taxBankAccount.accountHolder.isEmpty()) {
                taxBankAccount.accountHolder = customer.firstname + ' ' + customer.lastname
            }

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
            paymentMethod.accountHolder = binding.variables['payment.methods/new-account-owner']
            if (paymentMethod.accountHolder == null || paymentMethod.accountHolder.isEmpty()) {
                paymentMethod.accountHolder = customer.firstname + ' ' + customer.lastname
            }

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
                customerId: customerId,
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
}

new VehicleMapper(binding: binding).convert()