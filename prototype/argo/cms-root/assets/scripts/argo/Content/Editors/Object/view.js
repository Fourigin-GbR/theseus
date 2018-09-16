if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.object = com.fourigin.argo.content.editor.object || {};
com.fourigin.argo.content.editor.object.View = com.fourigin.argo.content.editor.object.View || (function ()
{
    var View = function (jTarget, oContent) {
        this.jTarget = jTarget;
        this.jPrototype = null;
        this.oContent = oContent;
        this.sEditorHtmlId = "tinyMceEditor_" + Math.random().toString(36).substr(2, 16);
        //
        this.findHtmlPrototype();
        //
        return this;
    };
    //
    View.prototype = new com.fourigin.argo.content.editor.View();

    View.prototype.findHtmlPrototype = function() {
        this.jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=object]").clone();
    };

    View.prototype.generateEditor = function(oContent) {
        if(!this.jPrototype || 0 === this.jPrototype.length) {
            console.error("Prototype not defined!");
        }
        // fill with data
        this.jPrototype.find("input[name=name]").val(oContent.name);
        this.jPrototype.find("input[name=source]").val(oContent.source);
        this.jPrototype.find("input[name=referenceId]").val(oContent.referenceId);
        this.jPrototype.find("input[name=alternativeText]").val(oContent.alternateText);
        this.jPrototype.find("input[name=mimeType]").val(oContent.mimeType);
        // image preview:
        this.jPrototype.find(".imagePreview img").attr("src", oContent.source);
        // add to html
        this.jTarget.append(this.jPrototype);
    };

    View.prototype.showHtmlEditor = function () {
        return "HTML Editor active";
    };

    View.prototype.setEvents = function() {
        this.setEventsButtonMoreOptions();
    };
    //
    return View;
}());