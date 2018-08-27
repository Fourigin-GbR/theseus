if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.group = com.fourigin.argo.content.editor.group || {};
com.fourigin.argo.content.editor.group.View = com.fourigin.argo.content.editor.group.View || (function ()
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
        this.jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=group]").clone();
    };

    View.prototype.generateEditor = function(oContent) {
        if(!this.jPrototype || 0 === this.jPrototype.length) {
            console.error("Prototype not defined!");
        }
        var jTarget = this.jPrototype.find("> fieldset");
        // fill with data
        this.jPrototype.find("input[name=name]").val(oContent.name);
        // add to html
        this.jTarget.append(this.jPrototype);
        // fill with data: write the elements
        for(var i=0, il=oContent.elements.length; i<il; i++) {
            var oCurrentContent = oContent.elements[i];
            switch(oCurrentContent.type) {
                case "text":
                    new com.fourigin.argo.content.editor.Text(jTarget, oCurrentContent);
                    break;
                case "object":
                    new com.fourigin.argo.content.editor.Object(jTarget, oCurrentContent);
                    break;
                case "list":
                    new com.fourigin.argo.content.editor.List(jTarget, oCurrentContent);
                    break;
            }
        }
    };

    View.prototype.showHtmlEditor = function () {
        return "HTML Editor active";
    };
    //
    return View;
}());