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
    //
    return PageEditor;
}());