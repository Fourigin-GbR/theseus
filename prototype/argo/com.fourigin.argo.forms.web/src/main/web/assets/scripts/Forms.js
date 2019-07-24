var inputs = document.getElementsByTagName("input");
var fieldsets = document.getElementsByTagName("fieldset");
var formular = document.querySelector("#fccFormular form");
var jFormular = $(formular);
console.log("form, inputs, fieldsets", formular, inputs, fieldsets);

var inputsWithBoundFieldSets = Array.prototype.filter.call(inputs, function(element, index, aElements) {
    return element.hasAttribute("data-activate-fieldset-name");
});

var setBoundedFieldSetsStatusOnMasterStatus = function(htmlNode_master, htmlNode_boundedFieldSet) {
    /**
     * Update status of bound fieldsets. Exception: radio-lists. Not all of their options need to have bounded
     * fieldsets, but only one can be selected.
     */
    var type = htmlNode_master.getAttribute("type");
    if(type !== "radio" && !htmlNode_boundedFieldSet) {
        return;
    }
    // One process for radios, one for all others:
    if(type === "radio") {
        // Iterate over all radios and update status:
        var radios = formular.querySelectorAll("[name='" + htmlNode_master.getAttribute('name') + "']");
        Array.prototype.forEach.call(radios, function (el) {
            updateStatusOfBoundFieldset(el);
        });
    }
    else {
        updateStatusOfBoundFieldset(htmlNode_master);
    }

};

var updateStatusOfBoundFieldset = function(input) {
    console.info("Update status of bound fieldset:", input);

    // Find fieldset
    var fieldsetName = input.getAttribute("data-activate-fieldset-name");
    if(!fieldsetName){
        return;
    }
    var targetFieldset = formular.querySelector("[name='" + fieldsetName + "']");
    //
    if(!targetFieldset) {
        console.warn("Could not find a target fieldset!", fieldsetName);
        return;
    }
    if(!input.checked && !input.selected) {
        targetFieldset.setAttribute("disabled", "disabled")
    }
    else {
        targetFieldset.removeAttribute("disabled");
    }
};

var iterateOverAllBoundInputsAndUpdateStatusOfFieldsets = function() {
    /**
     * Iterate over all found input elements with attribute of target field-sets.
     * Than look for the target-field, if it exists, call method to update status.
     */
    Array.prototype.forEach.call(inputsWithBoundFieldSets, function(el){
        var targetFieldset = Array.prototype.filter.call(fieldsets, function(element, index, aElements) {
            return (element.getAttribute("name") === el.getAttribute('data-activate-fieldset-name'));
        })[0];
        if(targetFieldset) {
            setBoundedFieldSetsStatusOnMasterStatus(el, targetFieldset);
        }
    });
};

var generateSummarizedListOfAllFormData = function() {
    /**
     * Get prototype items and fill each with collected form items:
     */
    var prototype = formular.querySelector("[data-prototype='summary-list-item']");
    var summaryListItemsTarget = formular.querySelector("[data-element='summary-list']");
    var allFormularItems = formular.querySelectorAll("input[type=text], input[type=checkbox]:checked, input[type=radio]:checked, textarea, option:checked");

    var htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets = Array.prototype.filter.call(allFormularItems, function(element) {
        /**
         * Filter out all form-fields, who do not have somewhere on their ancestors a fieldset which is 'disabled'.
         */
        return !element.closest("fieldset[disabled='disabled']");
    });

    // clean:
    summaryListItemsTarget.innerHTML = "";

    Array.prototype.forEach.call(htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets, function(el) {
        var newItem = prototype.cloneNode(true);
        var title = newItem.querySelector("[data-element='summary-list-title']");
        var value = newItem.querySelector("[data-element='summary-list-value']");
        var tagName = el.tagName.toLowerCase();
        var type  = null;
        var innerTooltip = null;
        var label = null, evaluatedValue = null;

        if("input" === tagName) {
            type = el.type.toLowerCase();

            if ("text" === type) {
                if (el.closest("label").querySelector("span")) {
                    label = el.closest("label").querySelector("span").cloneNode(true);
                    if (label) {
                        // Remove optional tooltips:
                        innerTooltip = label.querySelector(".tooltip");
                        if (innerTooltip) {
                            label.removeChild(innerTooltip);
                        }
                    }
                }
                title.innerHTML = label ? label.textContent : "";
                value.innerHTML = el.value;
                summaryListItemsTarget.appendChild(newItem);
            }
            else if ("radio" === type || "checkbox" === type) {
                if (el.closest("fieldset")) {
                    label = el.closest("fieldset").querySelector("legend");
                }
                if (el.closest("label") && el.closest("label").querySelector("span")) {
                    evaluatedValue = el.closest("label").querySelector("span").cloneNode(true);
                    if (evaluatedValue) {
                        // Remove optional tooltips:
                        innerTooltip = evaluatedValue.querySelector(".tooltip");
                        if (innerTooltip) {
                            evaluatedValue.removeChild(innerTooltip);
                        }
                    }
                }
                title.innerHTML = label ? label.textContent : "";
                value.innerHTML = evaluatedValue ? evaluatedValue.textContent : el.value;
                summaryListItemsTarget.appendChild(newItem);
            }
        }
        else if("option" === tagName) {
            if (el.closest("label").querySelector("span")) {
                label = el.closest("label").querySelector("span").cloneNode(true);
                if(label) {
                    // Remove optional tooltips:
                    innerTooltip = label.querySelector(".tooltip");
                    if (innerTooltip) {
                        label.removeChild(innerTooltip);
                    }
                }
            }
            title.innerHTML = label ? label.textContent : "";
            value.innerHTML = el.value;
            summaryListItemsTarget.appendChild(newItem);
        }
        else if("textarea" === tagName) {
            if (el.closest("label").querySelector("span")) {
                label = el.closest("label").querySelector("span").cloneNode(true);
                if(label) {
                    // Remove optional tooltips:
                    innerTooltip = label.querySelector(".tooltip");
                    if (innerTooltip) {
                        label.removeChild(innerTooltip);
                    }
                }
            }
            title.innerHTML = label ? label.textContent : "";
            value.innerHTML = el.value;
            summaryListItemsTarget.appendChild(newItem);
        }
    });
};

var showThankYouPage = function() {
    var htmlNode_thankYouPage = document.getElementById("thankYouPage"),
        htmlNode_formEditOnePage = document.getElementById("formularOnePage"), // currently for the edit-base-data-pages
        htmlNodes_stepBoxSteps = document.getElementsByClassName("StepsBox__step");
    //
    Array.prototype.forEach.call(htmlNodes_stepBoxSteps, function(el){
        el.classList.remove("active");
    });
    // document.querySelector(".StepsNavigation__item--last").classList.add("validated");
    htmlNode_thankYouPage.classList.add("active");
    console.log("HIDE", htmlNode_formEditOnePage);
    htmlNode_formEditOnePage.style.display = 'none';
};

var showErrorPage = function() {
    var htmlNode_errorPage = document.getElementById("errorPage"),
        htmlNodes_stepBoxSteps = document.getElementsByClassName("StepsBox__step");
    //
    Array.prototype.forEach.call(htmlNodes_stepBoxSteps, function(el){
        el.classList.remove("active");
    });
    htmlNode_errorPage.classList.add("active");
};

Array.prototype.forEach.call(inputs, function(el){
    // Set change event listeners. Don't apply to only those ones with specific attribute, as in a radio-list
    // not all radios neet to have bound elements! So in general: Tale all inputs and look for bound elements.
    el.addEventListener("change", function() {
        setBoundedFieldSetsStatusOnMasterStatus(this);
    });
});

var getUrlParameterAndUpdateAndInitForm = function() {
    var getUrlParameter = function getUrlParameter(sParam) {
        var sPageURL = window.location.search.substring(1),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
            }
        }
    };

    var customerId = getUrlParameter('customer.id') || null,
        entryId = getUrlParameter('entry.id') || null,
        stageId = getUrlParameter('stage.id') || null;
    if(customerId) {
        jFormular.attr("data-form-customer-id", customerId);
    }
    if(entryId) {
        jFormular.attr("data-form-entry-id", entryId);
    }
    if(stageId) {
        jFormular.attr("data-form-stage-id", stageId);
    }

    initializeFormWithRequestData(customerId, entryId);

};

var setFormFieldValues = function(fieldsDataMap) {
    console.log("setFormFieldValues");
    for(var fieldProperty in fieldsDataMap) {
        if(fieldsDataMap.hasOwnProperty(fieldProperty)) {
            console.log("setFormFieldValues property:", fieldProperty);
            var formElement = document.querySelector("[name='" + fieldProperty + "']");
            if(!formElement) {
                console.error("Can not find form-field for property '" + fieldProperty + "'!");
                continue;
            }
            console.log("setFormFieldValues tagName:", formElement.tagName);
            switch (formElement.tagName) {
                case "SELECT":
                    setFormFieldSelectValues(formElement, fieldsDataMap[fieldProperty]);
                    break;
                case "INPUT":
                    setFormFieldInputValues(formElement, fieldsDataMap[fieldProperty]);
                    break;
                case "TEXTAREA":
                    setFormFieldTextareaValues(formElement, fieldsDataMap[fieldProperty]);
                    break;
                default:
                    console.warn("Can not populate field. Field is not supported: '" + formElement.tagName + "'.");
                    break;
            }
        }
    }
};

var setFormFieldSelectValues = function(formFieldSelect, dataObject) {
    console.log("Set select values", formFieldSelect, dataObject);
    formFieldSelect.innerHTML = "";
    for(var property in dataObject) {
        if (dataObject.hasOwnProperty(property)) {
            var newOption = document.createElement('option');
            newOption.innerHTML = dataObject[property];
            newOption.setAttribute("value", property);
            if(dataObject[property].active === true) {
                newOption.setAttribute("selected", "selected");
            }
            formFieldSelect.appendChild(newOption);
        }
    }
};

var setFormFieldInputValues = function(formField, dataObject) {
    console.log("Set input values", formField, dataObject);
    switch (formField.attribute("type")) {
        case "text":
            formField.value = dataObject.displayName;
            break;
        case "checkbox":
        case "radio":
            formField.setAttribute("checked", "checked");
            break;
    }
};

var setFormFieldTextareaValues = function(formField, dataObject) {
    console.log("Set textarea values", formField, dataObject);
    formField.innerHTML = dataObject.displayName;
};

var initializeFormWithRequestData = function(customerId, entryId) {
    if(!customerId) {
        console.info("Configuration parameter 'customerId' is missing. Do not pre-fill form.");
        return;
    }
    var self = this,
        dataJson = {
            "formDefinition": $("#fccFormular form").attr("data-form-definition-id"),
            "customer": customerId
        };

    if(entryId) {
        dataJson["entryId"] = entryId;
    }

    $.ajax({
        url: '/forms/init-form',
        dataType: 'JSON',
        contentType: 'application/json',
        method: 'POST',
        data: JSON.stringify(dataJson)
    })
        .done(function (res) {
            console.log(res);
            setFormFieldValues(res);
            prePopulateFormWithRequestData(customerId, entryId);
        })
        .fail(function (err) {
            console.log('Error: ' + err.status);
            alert("Internal error. Please verify your request and try again later, or contact your administrator!");
        });
};

var prePopulateFormWithRequestData = function(customerId, entryId) {
    if(!customerId) {
        alert("Internal error. Please change your request and try again!");
        return;
    }
    var self = this,
        dataJson = {
            "formDefinition": $("#fccFormular form").attr("data-form-definition-id"),
            "stage": jFormular.attr("data-form-stage-id"),
            "customer": customerId
        };

    if(entryId) {
        dataJson["entryId"] = entryId;
    }

    $.ajax({
        url: '/forms/pre-populate',
        dataType: 'JSON',
        contentType: 'application/json',
        method: 'POST',
        data: JSON.stringify(dataJson)
    })
        .done(function (res) {
            console.log(res);
            if(res) {
                prePopulateForm(res);
            }
        })
        .fail(function (err) {
            console.log('Error: ' + err.status);

        });
};

var prePopulateForm = function(data) {
    /**
     * Take data and search form-element. On fixed elements like radio, checkbox, selectbox look for the correct value,
     * on textboxes or textareas enter the current value.
     */
    var storedData, defaultValues, currentFormElement;
    //
    console.info("prePopulateForm", data);
    storedData = data["STORED_DATA"];
    defaultValues = data["DEFAULT_VALUES"];
    for(var property in storedData) {
        if (storedData.hasOwnProperty(property)) {
            currentFormElement = document.querySelector("[name='" + property + "']");
            if(!currentFormElement) {
                console.error("Can not find form element with property name", property);
                return;
            }
            switch (currentFormElement.getAttribute("type")) {
                case "text":
                    currentFormElement.value = storedData[property];
                    break;
                case "radio":
                    var currentRadio = document.querySelector("[name='" + property + "'][value='" + storedData[property] + "']");
                    currentRadio.setAttribute("checked", "checked");
                    break;
                case "checkbox":
                    currentFormElement.setAttribute("checked", "checked");
                    break;
                case "select":
                    var currentOptions = currentFormElement.querySelectorAll("option");
                    Array.prototype.forEach.call(currentOptions, function (option) {
                        if(option.value === storedData[property]) {
                            option.setAttribute("checked", "checked");
                        }
                    }());
                    break;
            }
        }
    }
    iterateOverAllBoundInputsAndUpdateStatusOfFieldsets();
};

var validateForm = function() {
    var htmlNodes_formFields = formular.querySelectorAll("input[type='text'], input[type='hidden'], input[type='checkbox']:checked, input[type='radio']:checked, textarea");
console.log("DAs ist alles was ich zum Validieren habe: ", htmlNodes_formFields);
    var htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets = Array.prototype.filter.call(htmlNodes_formFields, function(element) {
        /**
         * Filter out all form-fields, who do not have somewhere on their ancestors a fieldset which is 'disabled'.
         */
        return !element.closest("fieldset[disabled='disabled']");
    });
    sendFormDataToValidate(htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets);
};

var sendFormDataToValidate = function(formData) {
    console.log(">>>> validate form", formData);

    var formToJSON = function (form) {
        var data = {};
        for (var i = 0; i < form.length; i++) {
            var item = form[i];
            data[item.name] = item.value;
        }
        return data;
    };

    var dataJson = {
            "header": {
                "formDefinition": jFormular.attr("data-form-definition-id"),
                "stage": jFormular.attr("data-form-stage-id"),
                "entryId": jFormular.attr("data-form-entry-id"),
                "customerId": jFormular.attr("data-form-customer-id"),
                "base": "DE",
                "locale": "en_GB",
                "referrer": {
                    "url": "www.tsp.de/registrierung/neu",
                    "client": "IE6"
                }
            },
            "data": formToJSON(formData)
        };
    console.log("Validate me: ", formData);

    resetFormValidations();
    setOnePageValidationAllMessagesInactive();

    $.ajax({
        url: '/forms/validate',
        dataType: 'JSON',
        contentType: 'application/json',
        method: 'POST',
        data: JSON.stringify(dataJson)
    })
        .done(function (res) {
            console.log(res);
            sendForm();
        })
        .fail(function (err) {
            console.error('Error: ' + err.status);
            getAllInvalidFieldsAndMarkThem(err);

            // For the one-page versions (edit customer / vehicle):
            setOnePageValidationMessageActive('invalid');

        });
};

var resetFormValidations = function() {
    var htmlNode_invalidFormFields = formular.querySelectorAll(".invalid");
    Array.prototype.forEach.call(htmlNode_invalidFormFields, function (el) {
        el.classList.remove("invalid");
    });

    var htmlNode_messages = formular.querySelectorAll("[data-element='validation-message']");
    Array.prototype.forEach.call(htmlNode_messages, function (el) {
        el.classList.remove("active");
    });
};

var getAllInvalidFieldsAndMarkThem = function (message) {
    var fields = message.responseJSON.fields;
    //
    for (var fieldKey in fields) {
        if (fields.hasOwnProperty(fieldKey)) {
            var htmlNode_field = formular.querySelectorAll("[name=\"" + fieldKey + "\"]")[0];
            if (!htmlNode_field) {
                console.warn("Can not find htmlNode to mark invalid element. Field: '" + fieldKey + "'");
                continue;
            }
            if (fields[fieldKey].valid) {
                htmlNode_field.classList.remove("invalid");
            }
            else {
                htmlNode_field.classList.add("invalid");
            }

            // Write message to field:
            var messageField = formular.querySelector("[data-input='" + fieldKey + "']");
            if(messageField && !fields[fieldKey].valid) {
                var messagesString = "";
                // TODO:Alle Fehlermeldungen als <p> ausgeben!
                console.info("### ", fieldKey, fields[fieldKey]);
                for(var i=0, il= fields[fieldKey]['errorMessages'].length; i<il; i++) {
                    messagesString = messagesString + "<p class='ValidationMessages__message--error'>" + fields[fieldKey]['errorMessages'][i].formattedMessage + "</p>";
                }
                messageField.innerHTML = messagesString;
                messageField.classList.add("active");
            }
        }
    }
};


var setOnePageValidationMessageActive = function (messageType) {
    var htmlNode_message;
    //
    htmlNode_message = document.querySelectorAll("#formularOnePage [data-validation-message-type=\"" + messageType + "\"]")[0];
    htmlNode_message.classList.add("active");
};

var setOnePageValidationAllMessagesInactive = function () {
    var htmlNode_messages;
    //
    htmlNode_messages = document.querySelectorAll("#formularOnePage [data-validation-message-type]");
    Array.prototype.forEach.call(htmlNode_messages, function (el) {
        el.classList.remove("active");
    });
};


var handleTooltips = function() {
    var jTooltips = jQuery("span.tooltip"),
        jTooltipIcons = jQuery("span.tooltip .tooltip__icon"),
        jTooltipCopies = jQuery("span.tooltip .tooltip__copy"),
        closeTimeoutHandler = null;

    jTooltipIcons.on("hover mouseover click", function(e) {
        console.info("mouseover", $(this));
        var jLocalIcon = $(this),
            jTooltip = jLocalIcon.closest(".tooltip"),
            jCopy = jTooltip.find(".tooltip__copy");
        //
        if(closeTimeoutHandler) {
            window.clearTimeout(closeTimeoutHandler);
        }

        if(jCopy.is(":visible")) {
            // Is already active
            return;
        }

        if($(e.target).is("a")) {
            // Link inside the tooltip
            return;
        }

        deactivateAllTooltips();
        jCopy.show();

        jCopy.on("mouseout", function(e) {
            if($.contains(jCopy[0], e.target)) {
                console.info("mouseout from element inside tooltip", e.target);
                // Triggered from element inside the tooltip copy
                return;
            }

            if(jCopy.is(":hover")) {
                console.info("Hovered tooltip copy status.");
                return;
            }


            console.log("mouseout", $(this));
            closeTimeoutHandler = window.setTimeout(function () {
                jCopy.hide();
            }, 1000);

        });
        jLocalIcon.on("mouseout", function(e) {
            if(jCopy.is(":hover")) {
                console.info("Hovered tooltip copy status.");
                return;
            }

            console.log("mouseout", $(this));
            closeTimeoutHandler = window.setTimeout(function () {
                jCopy.hide();
            }, 1000);
        });
    });

    var deactivateAllTooltips = function() {
        jTooltipCopies.each(function() {
            jQuery(this).hide();
        });
    }


};



var sendFormEvent = function() {
    $("#fccFormular form").on("submit", function(e) {
        e.preventDefault();
        validateForm();
    });
};

var sendForm = function() {
    var jForm = $("#fccFormular form");
        (function ($) {
            $.fn.serializeFormJSON = function () {

                var o = {};
                var a = this.serializeArray();
                $.each(a, function () {
                    if (o[this.name]) {
                        if (!o[this.name].push) {
                            o[this.name] = [o[this.name]];
                        }
                        o[this.name].push(this.value || '');
                    } else {
                        o[this.name] = this.value || '';
                    }
                });
                return o;
            };
        })(jQuery);

        var data = jForm.serializeFormJSON();

        var self = this,
            dataJson = {
                "header": {
                    "formDefinition": jForm.attr("data-form-definition-id"),
                    "stage": jForm.attr("data-form-stage-id"),
                    "entryId": jForm.attr("data-form-entry-id"),
                    "customerId": jForm.attr("data-form-customer-id"),
                    "base": "DE",
                    "locale": "en_GB", // TODO: replace with browser locale
                    "referrer": { // TODO: optional map (schema free), replace with some sane values, for statistics only
                        "url": "www.tsp.de/registrierung/neu",
                        "client": "IE6"
                    }
                },
                "data": data
            };

        $.ajax({
            url: jForm.attr("action"),
            dataType: 'JSON',
            contentType: 'application/json',
            method: 'POST',
            data: JSON.stringify(dataJson)
        })
            .done(function (res) {
                console.log(res);
                showThankYouPage();
            })
            .fail(function (err) {
                console.log('Error: ' + err.status);
                showErrorPage();
            });
};

sendFormEvent();

iterateOverAllBoundInputsAndUpdateStatusOfFieldsets();
getUrlParameterAndUpdateAndInitForm();

$(document).ready(function() {
    handleTooltips();
});