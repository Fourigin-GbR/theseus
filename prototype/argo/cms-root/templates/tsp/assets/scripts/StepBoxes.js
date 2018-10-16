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

    StepsBox.prototype.init = function() {
        /**
         * Scan all Steps
         */
        var htmlNodes_steps = this.htmlNodes.stepsBox.getElementsByClassName(this.htmlNodes.stepCssSelector);
        //
        for(var i= 0, il= htmlNodes_steps.length; i<il; i++) {
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

    StepsBox.prototype.setStepsNavigationButtons = function() {
        var self = this;
        this.htmlNodes.stepsBox.addEventListener("click", function (event) {
            var targetElement = event.target || event.srcElement;
            if (targetElement.tagName = "BUTTON") {
                if (targetElement.getAttribute("data-action") === "nextTab") {
                    self.setNextStepActive(this);
                }
                else if (targetElement.getAttribute("data-action") === "previousTab") {
                    self.setPreviousStepActive(this);
                }
            }
        });
    };

    StepsBox.prototype.resetAllStepsActiveStatus = function() {
        for(var i=0, il= this.steps.length; i<il; i++) {
            this.steps[i].htmlNode_content.classList.remove("active", "validated");
            this.steps[i].htmlNode_stepTab.classList.remove("active", "validated");
        }
    };

    StepsBox.prototype.updateAllStepsView = function() {
        for(var i=0, il= this.steps.length; i<il; i++) {
            var currentStep = this.steps[i];
            if(currentStep.validated) {
                currentStep.htmlNode_content.classList.add("validated");
                currentStep.htmlNode_stepTab.classList.add("validated");
            }
        }
        // Set current step active:
        this.steps[this.currentStep -1].htmlNode_content.classList.add("active");
        this.steps[this.currentStep -1].htmlNode_stepTab.classList.add("active");
    };


    StepsBox.prototype.setNextStepActive = function () {
        /**
         * Get current step, get next step and activate him, if it is validated
         */
        if(this.validateStep()) {
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

    StepsBox.prototype.validateStep = function() {
        /**
         * Collect all form fields in the step and validate them
          */
        var htmlNode_currentStep = this.steps[(this.currentStep-1)].htmlNode_content,
            allFormFieldInStep = htmlNode_currentStep.getElementsByTagName("input", "textarea");
        //
        this.function_validateFormElements(allFormFieldInStep);
    };
    //
    return StepsBox;
})();

var htmlNode_setsBox = document.getElementById("fccFormular");
var xyz = new Fourigin.StepsBox(htmlNode_setsBox, function(allFormFieldInStep){
    console.log("Validate me: ", allFormFieldInStep);
    $.ajax({
        url: 'http://argo.tsp.fourigin.com/forms/pre-validate',
        data: $(allFormFieldInStep).serialize()
    })
        .done(function(res) {
            console.log(res);
        })
        .fail(function(err) {
            console.log('Error: ' + err.status);
        });
    return true;
        }
);


/*var TabBoxes = document.getElementsByClassName('Tabs');
var activeTabIndex = 0;

var setTabActive = function (Tab, TabNavigationItems, TabNavigationItem) {
    return function () {
        // Find tab-content and activate:
        var targetId = TabNavigationItem.getAttribute("data-target-tab-id");
        var allTabContents = Tab.querySelectorAll("[data-tab-id]");

        /!* tab contents *!/

        // Reset all tab contents:
        Array.prototype.forEach.call(allTabContents, function (el) {
            el.classList.remove("active");
        });
        // Set current active:
        Tab.querySelectorAll("[data-tab-id=\"" + targetId + "\"]")[0].classList.add("active");

        /!* tab navigation *!/

        // Reset all tab contents:
        Array.prototype.forEach.call(TabNavigationItems, function (el) {
            el.classList.remove("active");
        });
        // Set current active:
        TabNavigationItem.classList.add("active");
    }
};

var setTabActiveByIndex = function (TabBox, index) {
    //return function() {
    console.info("setTabActiveByIndex", TabBox, index);
    var TabNavigationItems = TabBox.querySelectorAll(".TabNavigationItem"),
        TabContentItems = TabBox.querySelectorAll(".Tab");
    // reset:
    Array.prototype.forEach.call(TabNavigationItems, function (el) {
        el.classList.remove("active");
    });
    Array.prototype.forEach.call(TabContentItems, function (el) {
        el.classList.remove("active");
    });
    // Set current active:
    TabNavigationItems[index].classList.add("active");
    TabContentItems[index].classList.add("active");
    activeTabIndex = index;
    //}
};

var setNextTabActive = function (TabBox) {
    var TabContentItems = TabBox.querySelectorAll(".Tab");
    if (activeTabIndex < TabContentItems.length) {
        activeTabIndex++;
        setTabActiveByIndex(TabBox, activeTabIndex);
    }
};
var setPrevTabActive = function (TabBox) {
    if (activeTabIndex > 0) {
        activeTabIndex--;
        setTabActiveByIndex(TabBox, activeTabIndex);
    }
};

for (var i = 0, il = TabBoxes.length; i < il; i++) {
    var TabBox = TabBoxes[i];
    // Set events
    // + Set events on buttons prev/next
    TabBox.addEventListener("click", function (event) {
        var targetElement = event.target || event.srcElement;
        if (targetElement.tagName = "BUTTON") {
            if (targetElement.getAttribute("data-action") === "nextTab") {
                setNextTabActive(this);
            }
            else if (targetElement.getAttribute("data-action") === "previousTab") {
                setPrevTabActive(this);
            }
        }
    });
    // + Set events on tab-bar
    var TabNavigationItems = TabBox.getElementsByTagName("nav")[0].getElementsByClassName("TabNavigationItem");

    for (var j = 0, jl = TabNavigationItems.length; j < jl; j++) {
        var TabNavigationItem = TabNavigationItems[j];
        TabNavigationItem.addEventListener("click", function (TabBox, j) {
            return function () {
                setTabActiveByIndex(TabBox, j);
            }
        }(TabBox, j), false);
    }
}*/
