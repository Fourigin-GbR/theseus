var TabBoxes = document.getElementsByClassName('Tabs');
var activeTabIndex = 0;

var setTabActive = function (Tab, TabNavigationItems, TabNavigationItem) {
    return function () {
        // Find tab-content and activate:
        var targetId = TabNavigationItem.getAttribute("data-target-tab-id");
        var allTabContents = Tab.querySelectorAll("[data-tab-id]");

        /* tab contents */

        // Reset all tab contents:
        Array.prototype.forEach.call(allTabContents, function (el) {
            el.classList.remove("active");
        });
        // Set current active:
        Tab.querySelectorAll("[data-tab-id=\"" + targetId + "\"]")[0].classList.add("active");

        /* tab navigation */

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
}
