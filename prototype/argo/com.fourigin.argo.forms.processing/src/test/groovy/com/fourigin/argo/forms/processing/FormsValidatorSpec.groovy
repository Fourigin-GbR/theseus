package com.fourigin.argo.forms.processing

import com.fourigin.argo.forms.definition.FieldDefinition
import com.fourigin.argo.forms.definition.FormDefinition
import com.fourigin.argo.forms.definition.Type
import com.fourigin.argo.forms.validation.FailureReason
import com.fourigin.argo.forms.validation.FormData
import com.fourigin.argo.forms.validation.FormValidationResult
import com.fourigin.argo.forms.validation.FormsValidator
import spock.lang.Specification

class FormsValidatorSpec extends Specification {

    def 'validate with one specified text field works as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [:],
                fields: [
                        'a': new FieldDefinition(
                                type: Type.TEXT,
                                validation: null
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: false,
                validateFields: [
                        'a': 'A'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        result.valid
        result.fields['a'].valid
    }

    def 'validate with one unspecified text field fails as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [:],
                fields: [
                        'a': new FieldDefinition(
                                type: Type.TEXT,
                                validation: null
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: false,
                validateFields: [
                        'a': 'A',
                        'b': 'B'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        !result.valid
        result.fields['a'].valid
        !result.fields['b'].valid
        result.fields['b'].failureReasons == [
                new FailureReason(
                        validator: 'FormsValidator',
                        failureCode: FormsValidator.VALIDATION_ERROR_MISSING_FIELD_DEFINITION
                )
        ]
    }

    def 'validate with one specified mandatory text field works as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [:],
                fields: [
                        'a': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'mandatory': true
                                ]
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: false,
                validateFields: [
                        'a': 'A'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        result.valid
        result.fields['a'].valid
    }

    def 'validate with one specified mandatory text field without value fails as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [:],
                fields: [
                        'a': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'mandatory': true
                                ]
                        ),
                        'b': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'mandatory': true
                                ]
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: false,
                validateFields: [
                        'a': 'A'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        !result.valid
        result.fields['a'].valid
        !result.fields['b'].valid
    }

    def 'pre-validate with one specified mandatory text field without value works as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [:],
                fields: [
                        'a': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'mandatory': true
                                ]
                        ),
                        'b': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'mandatory': true
                                ]
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: true,
                validateFields: [
                        'a': 'A'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        result.valid
        result.fields['a'].valid
        result.fields['b'] == null
    }

    def 'validate with one specified text field with pattern works as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [
                        'nameplate': '[A-ZÖÜÄ]{1,3} [A-ZÖÜÄ]{1,2} [1-9]{1}[0-9]{1,3}'
                ],
                fields: [
                        'nameplate': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'pattern': 'nameplate'
                                ]
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: false,
                validateFields: [
                        'nameplate': 'DA VN 757'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        result.valid
        result.fields['nameplate'].valid
    }

    def 'validate with one specified text field with a wrong pattern fails as expected'() {
        FormDefinition definition = new FormDefinition(
                form: 'test-form',
                validationPatterns: [
                        'nameplate': '[A-ZÖÜÄ]{1,3} [A-ZÖÜÄ]{1,2} [1-9]{1}[0-9]{1,3}'
                ],
                fields: [
                        'nameplate': new FieldDefinition(
                                type: Type.TEXT,
                                validation: [
                                        'pattern': 'nameplate'
                                ]
                        )
                ]
        )

        FormData data = new FormData(
                formId: 'my-test-form-1',
                formDefinitionId: 'test-form',
                preValidation: false,
                validateFields: [
                        'nameplate': 'DA-VN 757'
                ],
                stateFields: [:]
        )

        FormValidationResult result = FormsValidator.validate(definition, data)

        expect:
        result != null
        !result.valid
        !result.fields['nameplate'].valid
    }
}
