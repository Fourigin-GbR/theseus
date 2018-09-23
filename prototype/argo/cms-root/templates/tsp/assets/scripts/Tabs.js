var Tabs = document.getElementsByClassName('Tabs');

var setTabActive = function (Tab, TabNavigationItems, TabNavigationItem) {
    return function () {
        // Find tab-content and activate:
        var targetId = TabNavigationItem.getAttribute("data-target-tab-id");
        var allTabContents = Tab.querySelectorAll("[data-tab-id]");

        /* tab contents */

        // Reset all tab contents:
        Array.prototype.forEach.call(allTabContents, function(el){
            el.classList.remove("active");
        });
        // Set current active:
        Tab.querySelectorAll("[data-tab-id=\"" + targetId + "\"]")[0].classList.add("active");

        /* tab navigation */

        // Reset all tab contents:
        Array.prototype.forEach.call(TabNavigationItems, function(el){
            el.classList.remove("active");
        });
        // Set current active:
        TabNavigationItem.classList.add("active");
    }
};

for (var i = 0, il = Tabs.length; i < il; i++) {
    var Tab = Tabs[i];
    // Set events:
    var TabNavigationItems = Tab.getElementsByTagName("nav")[0].getElementsByClassName("TabNavigationItem");
    for (var j = 0, jl = TabNavigationItems.length; j < jl; j++) {
        var TabNavigationItem = TabNavigationItems[j];
        TabNavigationItem.onclick = setTabActive(Tab, TabNavigationItems, TabNavigationItem);
    }
}