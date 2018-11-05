var inputs = document.getElementsByTagName("input");
var fieldsets = document.getElementsByTagName("fieldset");
var formular = document.querySelector("#fccFormular form");
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
    var allLFormularItems = formular.querySelectorAll("input, textarea");

    var htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets = Array.prototype.filter.call(allLFormularItems, function(element) {
        /**
         * Filter out all form-fields, who do not have somewhere on their ancestors a fieldset which is 'disabled'.
         */
        return !element.closest("fieldset[disabled='disabled']");
    });

    Array.prototype.forEach.call(htmlNodes_formFieldsWithoutDisabledByDisabledFieldsets, function(el) {
        var newItem = prototype.cloneNode(true);
        var title = newItem.querySelector("[data-element='summary-list-title']");
        var value = newItem.querySelector("[data-element='summary-list-value']");

        if(el.closest("label") && el.closest("label").querySelector("span")) {
            title.innerHTML = el.closest("label").querySelector("span").textContent;
            value.innerHTML = el.getAttribute("value");
            summaryListItemsTarget.appendChild(newItem);
        }
    });
};

Array.prototype.forEach.call(inputs, function(el){
    // Set change event listeners. Don't apply to only those ones with specific attribute, as in a radio-list
    // not all radios neet to have bound elements! So in general: Tale all inputs and look for bound elements.
    el.addEventListener("change", function() {
        setBoundedFieldSetsStatusOnMasterStatus(this);
    });
});

iterateOverAllBoundInputsAndUpdateStatusOfFieldsets();

var sendForm = function() {

    $("#fccFormular form").on("submit", function(e) {
        e.preventDefault();

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

        var data = $(this).serializeFormJSON();

    var self = this,
        dataJson = {
            "header": {
                "formDefinition": "register-vehicle",  // TODO: replace with the value of hidden input 'formDefinition'
                "customer": "tsp",
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
        url: '/forms/register-form',
        dataType: 'JSON',
        contentType: 'application/json',
        method: 'POST',
        data: JSON.stringify(dataJson)
    })
        .done(function (res) {
            console.log(res);
            alert("Prima! Sie haben ALLE Level gelöst!!");

        })
        .fail(function (err) {
            console.log('Error: ' + err.status);
            alert("Oh nein! Das letzte Level haben Sie nicht geschafft. Bitte neu starten.");
        });
        return true;
    });
};

sendForm();