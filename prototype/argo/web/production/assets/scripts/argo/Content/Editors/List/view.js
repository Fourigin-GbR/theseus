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
        }

        this.setEvents();
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