if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.PageEditor = com.fourigin.argo.PageEditor || (function ()
{
    var PageEditor = function(oConfiguration) {
        this.Model = new com.fourigin.argo.pageEditor.Model();
        this.Overlays = new com.fourigin.argo.Overlays();
        this.overlayPageEditor = null;
        this.setData(oConfiguration);
        this.initOverlays();
        this.writeHotspots();
        this.setEvents();
        //
        return this;
    };
    //
    PageEditor.prototype.initOverlays = function() {
        var self = this;
        jQuery(".overlay").each(function () {
            self.overlayPageEditor = self.Overlays.createOverlay(jQuery(this));
        });
    };
    //
    PageEditor.prototype.setData = function(oData) {
        this.Model.setBase(oData.base);
        this.Model.setPath(oData.path);
        this.Model.setHotspots(oData.hotspots);
    };
    //

    PageEditor.prototype.writeHotspots = function() {
        var oHotSpots = this.Model.getHotspots(),
            jEditorRoot = jQuery(".overlay [data-type='editor-fields']"),
            jPrototype = jQuery("[data-prototype=hotspot]");
        //
        if(!jPrototype || 0 === jPrototype.length) {
            console.error("Prototype is missing!");
            return;
        }
        console.log("write hotspots", oHotSpots);
        for(property in oHotSpots) {
            if(oHotSpots.hasOwnProperty(property)) {

                var jHotSpotPrototypeClone = jPrototype.clone().removeAttr("data-prototype");

                new com.fourigin.argo.content.HotspotEditor(
                    {
                        "siteBase": this.Model.getBase(),
                        "sitePath": this.Model.getPath(),
                        "contentPath": property,
                        "hotSpotDefinition": oHotSpots[property],
                        "view_target": jEditorRoot,
                        "view_prototype": jHotSpotPrototypeClone
                    }
                );
            }
        }

    };

    PageEditor.prototype.writeSpecificEditor = function(oContent, jTarget) {
        console.log("Write specific editor", oContent, jTarget);
        if(!jTarget || 0 === jTarget.length) {
            console.error("Target-is missing!");
        }
        switch(oContent.type) {
            case "text":
                this.writeTextEditor(oContent, jTarget);
                break;
            case "object":
                this.writeObjectEditor(oContent, jTarget);
                break;
            case "list":
                this.writeListEditor(oContent, jTarget);
                break;
            case "list-group":
                this.writeListGroupEditor(oContent, jTarget);
                break;
            case "group":
                this.writeGroupEditor(oContent, jTarget);
                break;
        }
    };

    PageEditor.prototype.writeTextEditor = function (oTextItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=text]").clone();
        //
        // fill with data
        jPrototype.find("input[name=name]").val(oTextItem.name);
        jPrototype.find("textarea[name=content]").val(oTextItem.content);
        jPrototype.find(".contentHtmlRendered").html(oTextItem.content);
        // append to editor
        jTarget.append(jPrototype);
    };
    PageEditor.prototype.writeObjectEditor = function (oObjectItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=object]").clone();
        //
        // fill with data
        jPrototype.find("input[name=name]").val(oObjectItem.name);
        jPrototype.find("input[name=source]").val(oObjectItem.source);
        jPrototype.find("input[name=referenceId]").val(oObjectItem.referenceId);
        jPrototype.find("input[name=alternativeText]").val(oObjectItem.alternateText);
        jPrototype.find("input[name=mimeType]").val(oObjectItem.mimeType);
        // image preview:
        jPrototype.find(".imagePreview img").attr("src", oObjectItem.source);
        // append to editor
        jTarget.append(jPrototype);
    };
    PageEditor.prototype.writeListEditor = function (oListItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=list]").clone();
        //
        // fill with data
        jPrototype.find("input[name=name]").val(oListItem.name);
        // fill with data: write the elements
        for(var i=0, il=oListItem.elements.length; i<il; i++) {
            this.writeSpecificEditor(oListItem.elements[i], jPrototype.find("> fieldset > ul"));
        }
        // append to editor
        jTarget.append(jPrototype);
    };
    PageEditor.prototype.writeListGroupEditor = function (oListGroupItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes [data-prototype=listGroup]").clone().removeAttr("data-prototype");
        //
        // fill with data: write the elements
        for(var i=0, il=oListGroupItem.elements.length; i<il; i++) {
            this.writeSpecificEditor(oListGroupItem.elements[i], jPrototype.find("> fieldset > fieldset"));
        }
        // append to editor
        jTarget.append(jPrototype);
    };
    PageEditor.prototype.writeGroupEditor = function (oGroupItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=group]").clone();
        //
        // fill with data
        jPrototype.find("input[name=name]").val(oGroupItem.name);
        // fill with data: write the elements
        for(var i=0, il=oGroupItem.elements.length; i<il; i++) {
            this.writeSpecificEditor(oGroupItem.elements[i], jPrototype.find("> fieldset"));
        }
        // append to editor
        jTarget.append(jPrototype);
    };

    PageEditor.prototype.setEvents = function() {
        var self = this;

        jQuery("[data-overlay-id='overlay_editPageContent']").on("click", function() {
            self.overlayPageEditor.show();
        });

        var updateTextPreviewBoxAsHtml = function(jTextElement) {
            jTextElement.find(".contentHtmlRendered").html(jTextElement.find("textarea").val());
        };


        var openCloseListEditorItems = function() {
            var jTrigger = jQuery("fieldset[data-type=list] > fieldset > ul > li [data-action=openClose], fieldset[data-type=list] > fieldset > ul > li .listOpenTrigger");
            jTrigger.on("click", function(e) {
                if("TEXTAREA" !== e.target.nodeName || "INPUT" !== e.target.nodeName) {
                    jQuery(this).closest("li").toggleClass("compactView");
                    window.tinyMCE.triggerSave();
                    updateTextPreviewBoxAsHtml(jQuery(this).closest("li").find("[data-type=text]"));
                }
            })
        };
        openCloseListEditorItems();

        var deleteListEntry = function() {
            jTrigger = jQuery("fieldset[data-type=list] [data-action=delete]");
            jTrigger.on("click", function() {
                jQuery(this).closest("li").slideUp(function(){jQuery(this).remove()});
            });
        };

        deleteListEntry();

        var addEntryTop = function() {
            jTrigger = jQuery("fieldset[data-type=list] [data-action=addTop]");
            jTrigger.on("click", function() {
                var jUl = jQuery(this).closest("fieldset").find("ul:first"),
                    jFirstLiClone = jUl.find("li:first").clone();
                jUl.prepend(jFirstLiClone);
            });
        };
        addEntryTop();

        var addEntryBottom = function() {
            jTrigger = jQuery("fieldset[data-type=list] [data-action=addBottom]");
            jTrigger.on("click", function() {
                var jUl = jQuery(this).closest("fieldset").find("ul:first"),
                    jFirstLiClone = jUl.find("li:last").clone();
                jUl.append(jFirstLiClone);
            });
        };

        addEntryBottom();

        var adjustHeightOfTextareaToContentSize = function(jTextarea) {
            if(jTextarea[0].scrollHeight > Math.ceil(jTextarea.height() + 30)) { // add padding TODO: read padding dynamically
                jTextarea.addClass("enlarge");
            }
        };

        var jAllTextareas = jQuery(".overlay textarea");

        jAllTextareas.on('input, focus', function() {
            //adjustHeightOfTextareaToContentSize(jQuery(this));
        });
        jAllTextareas.on('focusout', function() {
            //var jCurrentFocusedElement = jQuery(this);
            //window.setTimeout(function(){jCurrentFocusedElement.removeClass("enlarge");}, 300);
        });

    };
    //
    return PageEditor;
}());