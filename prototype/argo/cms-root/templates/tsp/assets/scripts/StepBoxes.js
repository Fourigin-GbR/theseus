if ("undefined" === typeof Fourigin) {
    var Fourigin = {};
}

Fourigin.StepsBox = Fourigin.StepsBox || (function () {
    var StepsBox = function (htmlNode_stepsBox, function_validateFormElements) {
        this.function_validateFormElements = function_validateFormElements;
        this.steps = [];
        this.currentStep = 1;
        this.totalSteps = null;
        this.htmlNodes = {
            "stepsBox": htmlNode_stepsBox,
            "stepCssSelector": "StepsBox__step"
        };
        //
        this.init();
        this.setStepsNavigationButtons();
        //
        return this;
    };

    StepsBox.prototype.init = function () {
        /**
         * Scan all Steps
         */
        var htmlNodes_steps = this.htmlNodes.stepsBox.getElementsByClassName(this.htmlNodes.stepCssSelector),
            self = this;
        //
        for (var i = 0, il = htmlNodes_steps.length; i < il; i++) {
            /**
             * Get Tab by attribute 'data-tab-id'
             * @type {number}
             */
            var htmlNode_currentStep = htmlNodes_steps[i],
                targetTabId = htmlNode_currentStep.getAttribute("data-target-tab-id"),
                htmlNode_tab = this.htmlNodes.stepsBox.querySelectorAll("[data-tab-id=\"" + targetTabId + "\"]")[0];
            this.steps.push(
                {"htmlNode_content": htmlNode_currentStep, "htmlNode_stepTab": htmlNode_tab, "validated": false}
            );
            // On any change in the step, set the status to "not-validated":
            //
            htmlNode_currentStep.addEventListener("change", (function(context) {
                return function() {
                    self.setStepValidationStatusInvalid((context + 1));
                };
            })(i));
        }
    };

    StepsBox.prototype.setStepsNavigationButtons = function () {
        var self = this;
        // Event listener:
        this.htmlNodes.stepsBox.addEventListener("click", function (event) {
            var targetElement = event.target || event.srcElement;
            if (targetElement.tagName = "BUTTON") {
                if (targetElement.getAttribute("data-action") === "nextTab") {
                    self.setNextStepActive(this);
                    self.setAllMessagesInactive();
                }
                else if (targetElement.getAttribute("data-action") === "previousTab") {
                    self.setPreviousStepActive(this);
                }
            }
        });
    };

    StepsBox.prototype.resetAllStepsActiveStatus = function () {
        for (var i = 0, il = this.steps.length; i < il; i++) {
            this.steps[i].htmlNode_content.classList.remove("active", "validated");
            this.steps[i].htmlNode_stepTab.classList.remove("active", "validated");
        }
    };

    StepsBox.prototype.updateAllStepsView = function () {
        for (var i = 0, il = this.steps.length; i < il; i++) {
            var currentStep = this.steps[i];
            if (currentStep.validated) {
                currentStep.htmlNode_content.classList.add("validated");
                currentStep.htmlNode_stepTab.classList.add("validated");
            }
        }
        // Set current step active:
        this.steps[this.currentStep - 1].htmlNode_content.classList.add("active");
        this.steps[this.currentStep - 1].htmlNode_stepTab.classList.add("active");
    };


    StepsBox.prototype.setNextStepActive = function () {
        /**
         * Get current step, get next step and activate him, if it is validated
         */
        if (this.isCurrentStepValidated()) {
            this.resetAllStepsActiveStatus();
            this.steps[this.currentStep - 1].validated = true;
            this.currentStep = this.currentStep < this.steps.length ? this.currentStep + 1 : this.currentStep;
            this.updateAllStepsView();
        }
        else {
            this.removeAllFieldValidationMessages();
            this.validateStep();
        }
    };

    StepsBox.prototype.setPreviousStepActive = function () {
        /**
         * Get current step, get next step and activate him, if it is validated
         */
        this.resetAllStepsActiveStatus();
        this.currentStep = this.currentStep > 0 ? this.currentStep - 1 : 0;
        this.updateAllStepsView();
    };

    StepsBox.prototype.setCurrentStepValidationStatus = function(isValid) {
        this.steps[(this.currentStep - 1)].validated = isValid;
    };
    StepsBox.prototype.isCurrentStepValidated = function() {
        return this.steps[(this.currentStep - 1)].validated;
    };

    StepsBox.prototype.setStepValidationStatusInvalid = function(step) {
        this.steps[(step - 1)].validated = false;
    };

    StepsBox.prototype.validateStep = function () {
        /**
         * Collect all form fields in the step and validate them
         */
        var nodeList_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content,
            htmlNode_currentStep_copy = nodeList_currentStep.cloneNode(true),
            htmlNodes_formFields = null,
            htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets = null;
        //
        htmlNodes_formFields = htmlNode_currentStep_copy.querySelectorAll("input[type='text'], input[type='checkbox']:checked, input[type='radio']:checked, textarea");

        htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets = Array.prototype.filter.call(htmlNodes_formFields, function(element) {
            /**
             * Filter out all form-fields, who do not have somewhere on their ancestors a fieldset which is 'disabled'.
             */
            return !element.closest("fieldset[disabled='disabled']");
        });
       this.function_validateFormElements(htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets);
    };

    StepsBox.prototype.setMessageActive = function (messageType) {
        var htmlNode_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content,
            htmlNode_message = null;
        //
        htmlNode_message = htmlNode_currentStep.querySelectorAll("[data-validation-message-type=\"" + messageType + "\"]")[0];
        htmlNode_message.classList.add("active");
    };

    StepsBox.prototype.setAllMessagesInactive = function () {
        var htmlNode_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content,
            htmlNode_message = null;
        //
        htmlNode_messages = htmlNode_currentStep.querySelectorAll("[data-validation-message-type]");
        Array.prototype.forEach.call(htmlNode_messages, function (el) {
            el.classList.remove("active");
        });
    };

    StepsBox.prototype.removeAllFieldValidationMessages = function() {
        var htmlNode_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content;
        //
        htmlNode_messages = htmlNode_currentStep.querySelectorAll(".FieldValidationMessage");
        Array.prototype.forEach.call(htmlNode_messages, function (el) {
            el.classList.remove("active");
            el.innerHTML = "";
        });
    };

    StepsBox.prototype.handleValidationMessage = function (message) {
        console.log("error: ", message);
        //
        switch (message.responseJSON.valid) {
            case false:
                this.setMessageActive("invalid");
                this.getAllInvalidFieldsAndMarkThem(message);
                break;
            default:
                this.setMessageActive("error")
        }
    };

    StepsBox.prototype.getAllInvalidFieldsAndMarkThem = function (message) {
        var fields = message.responseJSON.fields,
            htmlNode_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content;
        //
        for (var fieldKey in fields) {
            if (fields.hasOwnProperty(fieldKey)) {
                var htmlNode_field = htmlNode_currentStep.querySelectorAll("[name=\"" + fieldKey + "\"]")[0];
                if (!htmlNode_field) {
                    console.warn("Can not find htmlNode to mark invalid element.");
                    return false;
                }
                if (fields[fieldKey].valid) {
                    htmlNode_field.classList.remove("invalid");
                }
                else {
                    htmlNode_field.classList.add("invalid");
                }

                // Write message to field:
                var messageField = htmlNode_currentStep.querySelector("[data-input='" + fieldKey + "']");
                if(messageField && !fields[fieldKey].valid) {
                    var messagesString = "";
                    // TODO:Alle Fehlermeldungen als <p> ausgeben!
                    console.info("### ", fieldKey, fields[fieldKey]);
                    for(var i=0, il= fields[fieldKey]['failureReasons'].length; i<il; i++) {
                        messagesString = messagesString + "<p>" + fields[fieldKey]['failureReasons'][i].formattedMessage + "</p>";
                    }
                    messageField.innerHTML = messagesString;
                    messageField.classList.add("active");
                }
            }
        }
    };

    StepsBox.prototype.unMarkAllFields = function() {
        var htmlNode_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content,
            htmlNode_invalidFormFields = htmlNode_currentStep.querySelectorAll(".invalid");
        Array.prototype.forEach.call(htmlNode_invalidFormFields, function (el) {
            el.classList.remove("invalid");
        });
    };
    //
    return StepsBox;
})();

var htmlNode_setsBox = document.getElementById("fccFormular");
var xyz = new Fourigin.StepsBox(htmlNode_setsBox, function (allFormFieldInStep) {
        console.log(">>>> ", allFormFieldInStep, typeof allFormFieldInStep);

        var formToJSON = function (form) {
            var data = {};
            for (var i = 0; i < form.length; i++) {
                var item = form[i];
                data[item.name] = item.value;
            }
            return data;
        };

        var self = this,
            dataJson = {
                "header": {
                    "formDefinition": "register-vehicle",
                    "customer": "tsp",
                    "base": "DE",
                    "locale": "en_GB",
                    "referrer": {
                        "url": "www.tsp.de/registrierung/neu",
                        "client": "IE6"
                    }
                },
                "data": formToJSON(allFormFieldInStep)
            };
        console.log("Validate me: ", allFormFieldInStep);

        self.unMarkAllFields();

        $.ajax({
            url: '/forms/pre-validate',
            dataType: 'JSON',
            contentType: 'application/json',
            method: 'POST',
            data: JSON.stringify(dataJson)
        })
            .done(function (res) {
                console.log(res);
                alert("Prima! Sie haben das Level gelöst! Auf zu Level " + (self.currentStep +1));
                self.setCurrentStepValidationStatus(true);
                self.setNextStepActive();
            })
            .fail(function (err) {
                console.log('Error: ' + err.status);
                (function () {
                    self.handleValidationMessage(err);
                })(self);
            });
        return true;
    }
);
