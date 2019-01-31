import com.fourigin.argo.forms.customer.Customer
import com.fourigin.argo.forms.customer.CustomerAddress
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.text.SimpleDateFormat

class CustomerMapper {

    Logger logger = LoggerFactory.getLogger('com.fourigin.argo.forms.mapping.register-customer/customer-mapper')

    def binding

    SimpleDateFormat dateFormat = new SimpleDateFormat('dd.MM.yyyy')

    Customer convert() {
        logger.info('binding: {}', binding.variables)
    
        // gender
        def genderOption = binding.variables['gender']

        // birthdate
        def birthdateValue = binding.variables['birthdate']
        def birthdate = dateFormat.parse(birthdateValue)

        // address
        CustomerAddress mainAddress = new CustomerAddress(
                street: binding.variables['street'],
                houseNumber: binding.variables['house-number'],
                additionalInfo: binding.variables['additional-info'],
                zipCode: binding.variables['zip-code'],
                city: binding.variables['city'],
                country: 'DE'
        )

        def nationalityOption
        def nationalityOptionValue = binding.variables['nationality']
        if (nationalityOptionValue == 'german') {
            nationalityOption = 'DE'
        } else {
            nationalityOption = binding.variables['nationality/country']
        }

        return new Customer(
                gender: genderOption,
                firstname: binding.variables['firstname'],
                lastname: binding.variables['lastname'],
                birthname: binding.variables['birthname'],
                birthdate: birthdate,
                cityOfBorn: binding.variables['cityOfBorn'],
                mainAddress: mainAddress,
                additionalAddresses: null,
                bankAccounts: null,
                nationality: nationalityOption,
                email: binding.variables['contact.email'],
                phone: binding.variables['contact.phone'],
                fax: binding.variables['contact.fax']
        )
    }
}

new CustomerMapper(binding: binding).convert()