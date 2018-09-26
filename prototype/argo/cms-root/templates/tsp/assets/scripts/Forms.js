var inputs = document.getElementsByTagName("input");
var fieldsets = document.getElementsByTagName("fieldset");
console.log("inputs, fieldsets", inputs, fieldsets);

var inputsWithBoundFieldSets = Array.prototype.filter.call(inputs, function(element, index, aElements) {
    return element.hasAttribute("data-activate-fieldset-name");
});

var _inputsWithBoundFieldSets = null; //inputs.querySelectorAll("[data-activate-fieldset-name]");

Array.prototype.forEach.call(inputsWithBoundFieldSets, function(el){
    var targetFieldset = Array.prototype.filter.call(fieldsets, function(element, index, aElements) {
        return (element.getAttribute("name") === el.getAttribute('data-activate-fieldset-name'));
    })[0];
    el.addEventListener("change", function() {
        console.info("Changed. Target is: ", targetFieldset);
        if(!this.checked || !this.selected) {
            targetFieldset.setAttribute("disabled", "disabled")
        }
        else {
            targetFieldset.removeAttribute("disabled");
        }
    });
});