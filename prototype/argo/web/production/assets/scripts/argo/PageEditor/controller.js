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
            jEditorRoot = jQuery("#contentList"),
            jPrototype = jEditorRoot.find("li[data-prototype=hotspot]");
        //
        console.log("write hotspots", oHotSpots);
        for(property in oHotSpots) {
            if(oHotSpots.hasOwnProperty(property)) {
                var jHotSpot = jPrototype.clone().removeAttr("data-prototype"),
                    oHotSpot = oHotSpots[property],
                    oContent = this.Model.getContentByPath(property);
                //
                jHotSpot.find(".listItemsAsFlyoutTargets__title").text(oHotSpot.title);
                jHotSpot.find("h5").text(oHotSpot.title);
                //
                this.writeSpecificEditor(oContent, jHotSpot.find("form .editorFields"));
                //
                jEditorRoot.append(jHotSpot);
            }
        }

    };

    PageEditor.prototype.writeSpecificEditor = function(oContent, jTarget) {
        switch(oContent.type) {
            case "text":
                this.writeTextEditor(oContent, jTarget);
                break;
            case "list":
                this.writeListEditor(oContent, jTarget);
                break;
        }
    };

    PageEditor.prototype.writeTextEditor = function (oTextItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=text]");
        //
        // fill with data
        jPrototype.find("input[name=content]").val(oTextItem.content);
        // append to editor
        jTarget.append(jPrototype);
    };
    PageEditor.prototype.writeListEditor = function (oListItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=list]");
        //
        // fill with data

        // append to editor
        jTarget.append(jPrototype);
    };

    PageEditor.prototype.setEvents = function() {
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
    };
    //
    return PageEditor;
}());