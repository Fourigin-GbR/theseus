import com.fourigin.argo.forms.models.Vehicle
import com.fourigin.argo.forms.models.VehicleRegistration
import com.fourigin.argo.forms.customer.payment.BankAccount
import com.fourigin.argo.forms.customer.payment.Paypal
import com.fourigin.argo.forms.customer.payment.Prepayment
import com.fourigin.argo.forms.customer.payment.Sofort
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class VehicleMapper {

    Logger logger = LoggerFactory.getLogger('com.fourigin.argo.forms.mapping.register-vehicle/vehicle-registration-mapper')

    def binding

    VehicleRegistration convert() {
        logger.info('binding: {}', binding.variables)

        // new nameplate
        def newNameplateOption
        def newNameplateAdditionalInfo
        def newNameplateOptionValue = binding.variables['vehicle.new-nameplate']
        if(newNameplateOptionValue == 'portal') {
            newNameplateOption = 'TO_REGISTER_BY_PORTAL'
            newNameplateAdditionalInfo = binding.variables['vehicle.new-nameplate/nameplate-customer-requirements']
        }
        else if(newNameplateOptionValue == 'self') {
            newNameplateOption = 'ALREADY_REGISTERED_BY_CLIENT'
            newNameplateAdditionalInfo = binding.variables['vehicle.new-nameplate/nameplate-customer-reservation']
        }
        else {
            newNameplateOption = 'NOT_REGISTERED'
            newNameplateAdditionalInfo = ''
        }

        // leasing option
        String leasingOptionValue = binding.variables['vehicle-data.leasing']
        boolean leasingOption = Boolean.parseBoolean(leasingOptionValue)

        // tax account
        BankAccount taxBankAccount = new BankAccount() // TODO: fill the bank account object

        // payment method
        def paymentMethod
        def paymentMethodValue = binding.variables['payment.methods']
        if(paymentMethodValue == 'paypal'){
            paymentMethod = new Paypal()
        }
        else if(paymentMethodValue == 'prepayment'){
            paymentMethod = new Prepayment()
        }
        else if(paymentMethodValue == 'sofort'){
            paymentMethod = new Sofort()
        }
        else {
            paymentMethod = new BankAccount()  // TODO: fill the bank account object
        }

        // handover option
        def handoverOption
        def handoverOptionValue = binding.variables['handover.options']
        if(handoverOptionValue == 'office-delivering'){
            handoverOption = 'OFFICE_DELIVERING'
        }
        else {
            handoverOption = 'PICKUP'
        }

        Vehicle vehicle = new Vehicle (
                previousNameplate: binding.variables['vehicle.existing-nameplate'],
                newNameplateOption: newNameplateOption,
                newNameplateAdditionalInfo: newNameplateAdditionalInfo,
                vehicleIdentNumber: binding.variables['vehicle-data.ident'],
                vehicleId: binding.variables['vehicle-data.vehicle-id'],
                leasingOrFinancingOption: leasingOption,
                insuranceId: binding.variables['vehicle-insurance.id']
        )

        return new VehicleRegistration(
                customerId: binding.variables['customer.id'],
                vehicle: vehicle,
                bankAccountForTaxPayment: taxBankAccount,
                servicePaymentMethod: paymentMethod,
                handoverOption: handoverOption
        )
    }

}

new VehicleMapper(binding: binding).convert()