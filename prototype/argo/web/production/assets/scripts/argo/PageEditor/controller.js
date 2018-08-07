if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.PageEditor = com.fourigin.argo.PageEditor || (function ()
{
    var PageEditor = function() {
        this.Model = new com.fourigin.argo.pageEditor.Model();
        //
        return this;
    };
    //
    PageEditor.prototype.setData = function(oData) {
        this.Model.setBase(oData.base);
        this.Model.setPath(oData.path);
        this.Model.setHotspots(oData.hotspots);
    };
    //
    PageEditor.prototype.loadContent = function () {
        var self = this,
            jRequest = jQuery.ajax({
            "url": "/cms/compile/prepare-content?base=" + this.Model.getBase() + "&path=" + this.Model.getPath() + "&verbose=true"
        });
        jRequest.done(function (data) {
                console.log("loaded content.");
                self.Model.setContent(data.content);
                self.writeHotspots();
                self.setEvents();
            }
        );
    };

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
                var jHotSpot = jPrototype.clone().removeAttr("data-prototype"),
                    oHotSpot = oHotSpots[property],
                    jItemTypeIcon = null,
                    oContent = this.Model.getContentByPath(property);
                //
                //jItemTypeIcon = jQuery("#argoEditorPrototypes").find(".contentTypeIcon[data-content-type='" + oContent.type + "']").clone();
                jHotSpot.find("h4").text(oHotSpot.title);
                //jHotSpot.find(".listItemsAsFlyoutTargets_contentTypeIcon").append(jItemTypeIcon);
                //
                this.writeSpecificEditor(oContent, jHotSpot.find("form .editableFields"));
                jHotSpot.find("form").attr("data-content-path", property);
                //
                jEditorRoot.append(jHotSpot);
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

        var openLayer = function (jLayer) {
            jLayer.addClass("active");
        };
        var closeLayer = function (jLayer) {
            jLayer.removeClass("active");
            jLayer.find(".layer").removeClass("active");
        };

        var toggleNextLayer = function (jLayoutTrigger) {
            var jNextChildLayer = jLayoutTrigger.siblings(".layer:first");
            //
            // Hide all other layer from this level first:
            jLayoutTrigger.closest(".layer").find(".layer").removeClass("active");
            jLayoutTrigger.closest(".listItemsAsFlyoutTargets").find(".layoutTrigger").filter(":not(jLayoutTrigger)").removeClass("active");
            jLayoutTrigger.addClass("active");
            jNextChildLayer.toggleClass("active");
            // TODO: Close ALL others on the same level
            if (!jNextChildLayer.hasClass("active")) {
                /* Close ALL child layers */
                jNextChildLayer.find(".layer").removeClass("active");
            }
        };

        jQuery("[data-target-layer-id]").each(function () {
            jQuery(this).on("click", function () {
                var jThis = jQuery(this);
                // get target layer:
                var sId = jThis.attr("data-target-layer-id");
                var jTargetLayer = jQuery("[data-layer-id='" + sId + "']");
                //
                if (jThis.hasClass("active")) {
                    jThis.removeClass("active");
                    closeLayer(jTargetLayer);
                }
                else {
                    jThis.addClass("active");
                    openLayer(jTargetLayer);
                }
            });
        });

        jQuery(".layoutTrigger").each(function () {
            jQuery(this).on("click", function () {
                toggleNextLayer(jQuery(this));
            });
        });

        var openCloseListEditorItems = function() {
            var jTrigger = jQuery("fieldset[data-type=list] > fieldset > ul > li .listStatusIcon, fieldset[data-type=list] > fieldset > ul > li .listOpenTrigger");
            jTrigger.on("click", function(e) {
                if("TEXTAREA" !== e.target.nodeName || "INPUT" !== e.target.nodeName) {
                    jQuery(this).closest("li").toggleClass("compactView");
                }
            })
        };
        openCloseListEditorItems();

        jQuery(".layer button.close").on("click", function() {
            var jLayout = jQuery(this).closest(".layer");
            jLayout.removeClass("active");
            jLayout.parent().find(".layoutTrigger").removeClass("active");
        });

        jQuery("form").on("submit", function (e) {
            e.preventDefault();
            e.stopPropagation();
            //
            var jData = self.Model.generateArgoContentElementJsonByMarkup(jQuery(this).find("fieldset:first"));
            // TODO: das klappt so nicht. ich muss über die DOM-Elemente interieren, damit ich jedes <fieldset> umwandeln kann in ein Type-Element oder ein Elements.

            var oNewDataObject = {
                "base": self.Model.getBase(),
                "path": self.Model.getPath(),
                "contentPath": $(this).attr("data-content-path"),
                "originalChecksum": "",
                "modifiedContentElement": jData
            };
            console.log("for mdata as json: ", oNewDataObject);

            var request = $.ajax({
                url: "http://argo.greekestate.fourigin.com/cms/editors/save",
                method: "POST",
                data: JSON.stringify(oNewDataObject),
                contentType: "application/json"
            });

            request.done(function (msg) {
                jQuery("iframe#pageContent")[0].contentWindow.location.reload(true);
            });

            request.fail(function (jqXHR, textStatus) {
                alert("Request failed: " + textStatus);
            });
        });
    };
    //
    return PageEditor;
}());