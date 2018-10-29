var inputs = document.getElementsByTagName("input");
var fieldsets = document.getElementsByTagName("fieldset");
var formular = document.querySelector("#fccFormular form");
console.log("form, inputs, fieldsets", formular, inputs, fieldsets);

var inputsWithBoundFieldSets = Array.prototype.filter.call(inputs, function(element, index, aElements) {
    return element.hasAttribute("data-activate-fieldset-name");
});

var setBoundedFieldSetsStatusOnMasterStatus = function(htmlNode_master, htmlNode_boundedFieldSet) {
    if(!htmlNode_boundedFieldSet) {
        return;
    }
    if(!htmlNode_master.checked && !htmlNode_master.selected) {
        htmlNode_boundedFieldSet.setAttribute("disabled", "disabled")
    }
    else {
        htmlNode_boundedFieldSet.removeAttribute("disabled");
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

Array.prototype.forEach.call(inputsWithBoundFieldSets, function(el){
    var targetFieldset = Array.prototype.filter.call(fieldsets, function(element, index, aElements) {
        return (element.getAttribute("name") === el.getAttribute('data-activate-fieldset-name'));
    })[0];
    el.addEventListener("change", function() {
        console.info("Changed. Target is: ", targetFieldset);
        setBoundedFieldSetsStatusOnMasterStatus(this, targetFieldset);
        // if radio, only one element can be active - check other fields:
        var htmlNode_parentForm = el.closest("form"),
            radioName = el.getAttribute("name");
        if(el.getAttribute("type") === "radio") {
            var htmlNode_radioFields = htmlNode_parentForm.querySelectorAll("input[name='" + radioName + "']");

            console.log("Diese Radios habe ich gefunden:", htmlNode_radioFields);
            Array.prototype.forEach.call(htmlNode_radioFields, function(elRadio){

                var currentTargetFieldset = Array.prototype.filter.call(fieldsets, function(element, index, aElements) {
                    if(elRadio.getAttribute('data-activate-fieldset-name')) {
                        return (element.getAttribute("name") === elRadio.getAttribute('data-activate-fieldset-name'));
                    }
                })[0];
                console.log("Dieses Feld will ich disabeln:", currentTargetFieldset);
                if(currentTargetFieldset) {
                    setBoundedFieldSetsStatusOnMasterStatus(elRadio, currentTargetFieldset);
                }
            });
        }

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
                "formDefinition": "register-vehicle",
                "customer": "tsp",
                "base": "DE",
                "locale": "en_GB",
                "referrer": {
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