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
        var htmlNodes_steps = this.htmlNodes.stepsBox.getElementsByClassName(this.htmlNodes.stepCssSelector);
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
        }
    };

    StepsBox.prototype.setStepsNavigationButtons = function () {
        var self = this;
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
        if (this.validateStep()) {
            this.resetAllStepsActiveStatus();
            this.steps[this.currentStep - 1].validated = true;
            this.currentStep = this.currentStep < this.steps.length ? this.currentStep + 1 : this.currentStep;
            this.updateAllStepsView();
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

    StepsBox.prototype.validateStep = function () {
        /**
         * Collect all form fields in the step and validate them
         */
        var htmlNode_currentStep = this.steps[(this.currentStep - 1)].htmlNode_content,
            allFormFieldInStep = htmlNode_currentStep.getElementsByTagName("input", "textarea");
        //
        this.function_validateFormElements(allFormFieldInStep);
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
            }
        }
    };
    //
    return StepsBox;
})();

var htmlNode_setsBox = document.getElementById("fccFormular");
var xyz = new Fourigin.StepsBox(htmlNode_setsBox, function (allFormFieldInStep) {
        console.log(allFormFieldInStep, typeof allFormFieldInStep);

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
        $.ajax({
            url: '/forms/pre-validate',
            dataType: 'JSON',
            contentType: 'application/json',
            method: 'POST',
            data: JSON.stringify(dataJson)
        })
            .done(function (res) {
                console.log(res);
                alert("Prima! Sie haben das erste Level gelöst! Sobald der Karsten Zeit hat, geht es hier dann auch weiter :)");
            })
            .fail(function (err) {
                console.log('Error: ' + err.status);
                (function (context) {
                    self.handleValidationMessage(err);
                })(self);
            });
        return true;
    }
);
