if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.list = com.fourigin.argo.content.editor.list || {};
com.fourigin.argo.content.editor.list.View = com.fourigin.argo.content.editor.list.View || (function ()
{
    var View = function (jTarget, oContent) {
        this.jTarget = jTarget;
        this.jPrototype = null;
        this.oContent = oContent;
        //
        this.findHtmlPrototype();
        //
        return this;
    };
    //
    View.prototype = new com.fourigin.argo.content.editor.View();

    View.prototype.findHtmlPrototype = function() {
        this.jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=list]").clone();
    };

    View.prototype.generateEditor = function(oContent) {
        if(!this.jPrototype || 0 === this.jPrototype.length) {
            console.error("Prototype not defined!");
        }
        var jListItemPrototype = this.jPrototype.find("li[data-prototype=listItem]").clone().removeAttr('data-prototype'),
            jTarget = this.jPrototype.find("> fieldset > ul");
        // add to html
        this.jTarget.append(this.jPrototype);
        // fill with data
        this.jPrototype.find("input[name=name]").val(oContent.name);
        // fill with data: write the elements
        for(var i=0, il=oContent.elements.length; i<il; i++) {
            var oCurrentContent = oContent.elements[i],
                jCurrentListItem = jListItemPrototype.clone(),
                jListItemContentTarget = jCurrentListItem.find(".listItemContent");
            // Attach listItem Prototype in target (=> li into ul):
            jTarget.append(jCurrentListItem);

            switch(oCurrentContent.type) {
                case "text":
                    new com.fourigin.argo.content.editor.Text(jListItemContentTarget, oCurrentContent);
                    break;
                case "list-group":
                    this.generateEditorListGroup(jListItemContentTarget, oCurrentContent);
                    break;
            }

            this.generateListItemPreviews(oCurrentContent, jCurrentListItem);
        }

        this.setEvents();
    };

    View.prototype.generateListItemPreviews = function(oContent, jListItem) {
        // TODO: Now is using content, but needs to use content-prototype and resolve  the contents!
        var jPrototypeImage = jListItem.find("[data-prototype=previewImage]").clone().removeAttr("data-prototype"),
            jPrototypeText = jListItem.find("[data-prototype=previewText]").clone().removeAttr("data-prototype"),
            jTarget = jListItem.find(".touchBar .titleElements .previewItems");
        //
        jListItem.find("[data-prototype=previewImage]").remove();
        jListItem.find("[data-prototype=previewText]").remove();
        console.log("### jTarget, jPrototypeImage", jTarget, jPrototypeImage, this.jPrototype);

            var jListContentPreviewItem = null;
            // Content can be an single item, of if multiple a group with the items. Respect that:
            console.log("### Direct list item:",oContent );
            switch(oContent.type) {
                case "list-group":
                    for(var j=0, jl= oContent.elements.length; j < jl; j++) {
                        var oListChildGroupContent = oContent.elements[j];
                        jListContentPreviewItem = null;

                        switch(oListChildGroupContent.type) {
                            case "object":
                                jListContentPreviewItem = jPrototypeImage.clone();
                                jListContentPreviewItem.find("img").attr("src", oListChildGroupContent.source);
                                break;
                            case "text":
                                jListContentPreviewItem = jPrototypeText.clone();
                                jListContentPreviewItem.html(oListChildGroupContent.content);
                                break;
                            default:
                                console.info("### Kann listContentPreviewItem nicht erstellen...", oListChildGroupContent);
                                break;
                        }

                        if(jListContentPreviewItem) {
                            console.info("### Add preview items??", jTarget, jListContentPreviewItem);
                            jTarget.append(jListContentPreviewItem);
                        }
                    }
                    break;
            }

    };

    View.prototype.generateEditorListGroup = function(jTarget, oContent) {
        var jPrototype = jQuery("#argoEditorPrototypes [data-prototype=listGroup]").clone().removeAttr("data-prototype");
        //
        // append to editor
        jTarget.append(jPrototype);
        // fill with data: write the elements
        for(var i=0, il=oContent.elements.length; i<il; i++) {
            var oCurrentContent = oContent.elements[i],
                jCurrentTarget = jPrototype.find("> fieldset > fieldset");
            if(0 === jCurrentTarget.length) {
                console.error("Target not found.", jPrototype);
            }
            switch(oCurrentContent.type) {
                case "text":
                    new com.fourigin.argo.content.editor.Text(jCurrentTarget, oCurrentContent);
                    break;
                case "object":
                    new com.fourigin.argo.content.editor.Object(jCurrentTarget, oCurrentContent);
                    break;
                case "list-group":
                    this.generateEditorListGroup(jCurrentTarget, oCurrentContent);
                    break;
            }
        }
    };

    View.prototype.setEvents = function() {
        this.openCloseListEditorItems();
    };

    View.prototype.openCloseListEditorItems = function() {
        console.log("...");
        var jTrigger = this.jPrototype.find("> fieldset > ul > li [data-action=openClose], > fieldset > ul > li .listOpenTrigger");
        console.log("   ... Trigger:", jTrigger);
        jTrigger.on("click", function(e) {
            console.info("yes...close li");
            if("TEXTAREA" !== e.target.nodeName || "INPUT" !== e.target.nodeName) {
                jQuery(this).closest("li").toggleClass("compactView");
                //updateTextPreviewBoxAsHtml(jQuery(this).closest("li").find("[data-type=text]"));
            }
        })
    };

    View.prototype.showHtmlEditor = function () {
        return "HTML Editor active";
    };
    //
    return View;
}());