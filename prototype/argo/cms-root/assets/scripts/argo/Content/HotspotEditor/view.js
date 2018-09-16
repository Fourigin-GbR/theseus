if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.hotspotEditor = com.fourigin.argo.content.hotspotEditor || {};
com.fourigin.argo.content.hotspotEditor.View = com.fourigin.argo.content.hotspotEditor.View || (function ()
{
    var View = function (jTarget, jPrototype) {
        this.jTarget = jTarget;
        this.jPrototype = jPrototype;
        this.Controller = null;
        //
        return this;
    };
    //

    View.prototype.generateEditor = function (oHotspotDefinition) {
        /**
         * Generate the container and basic elements.
         * Set the target for adding the specific Content Editors.
         */
        this.jPrototype.find("h4").text(oHotspotDefinition.title);
        if(oHotspotDefinition.description) {
            this.jPrototype.find(".information").addClass("active").text(oHotspotDefinition.description);
        }
        this.jPrototype.find("form").attr("data-content-path", property);
        //
        this.jTarget.append(this.jPrototype);
        this.setEvents();
    };

    View.prototype.getEditorTargetForContentEditors = function() {
        return this.jPrototype.find("form [data-type='editor-fields']");
    };

    View.prototype.setEvents = function () {
        var self = this;
        this.jPrototype.find("form").on("submit", function (e) {
            var jThis = jQuery(this),
                jMessageSuccessfullySaved = jThis.find(".messages__message[data-type='successfully-stored']"),
                jGlobalMessagePageReloaded = jQuery(".globalNotifications__globalNotification[data-type='page-reloaded']"),
                jFieldsetElementWithContentElement = jThis.find("fieldset:first"),
                jBlockingLayer = jThis.find(".blockingOverlay"),
                blockerTimeoutHandleCache = null;
            //
            e.preventDefault();
            e.stopPropagation();
            //
            blockerTimeoutHandleCache = blockerTimeoutHandleCache || null;

            if(blockerTimeoutHandleCache) {
                window.clearTimeout(blockerTimeoutHandleCache);
            }
            blockerTimeoutHandleCache = window.setTimeout(function(){jBlockingLayer.addClass("active")}, 1000);

            // Trigger event "save.argo" on all contained content-items, so the attached content-editors can catch it
            jThis.find("fieldset[data-type]").trigger("save.argo");

            // TODO: need attr-path?  oArgoContentStorageElement = this.Model.buildArgoContentStoreDataElement(jThis.attr("data-content-path"), oContentElementData);

            self.Controller.save(
                jFieldsetElementWithContentElement,
                function(){
                    jBlockingLayer.removeClass("active");
                    if(blockerTimeoutHandleCache) {
                        window.clearTimeout(blockerTimeoutHandleCache);
                        blockerTimeoutHandleCache = null;
                    }
                    jMessageSuccessfullySaved.addClass("active");
                    window.setTimeout(function(){
                        jMessageSuccessfullySaved.removeClass("active");
                    }, 2500);
                    jQuery("iframe#pageContent")[0].contentWindow.location.reload(true);
                    jGlobalMessagePageReloaded.addClass("active");
                    window.setTimeout(function(){
                        jGlobalMessagePageReloaded.removeClass("active");
                    }, 5000);
                },
                function(){
                    alert("Request failed: " + textStatus);
                    jBlockingLayer.removeClass("active");
                    if(blockerTimeoutHandleCache) {
                        window.clearTimeout(blockerTimeoutHandleCache);
                        blockerTimeoutHandleCache = null;
                    }
                }
            );
        });
    };
    //
    return View;
}());