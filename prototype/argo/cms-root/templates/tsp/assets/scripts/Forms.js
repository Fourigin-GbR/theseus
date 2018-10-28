var inputs = document.getElementsByTagName("input");
var fieldsets = document.getElementsByTagName("fieldset");
console.log("inputs, fieldsets", inputs, fieldsets);

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