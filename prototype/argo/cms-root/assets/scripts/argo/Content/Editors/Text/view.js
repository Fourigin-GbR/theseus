if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.text = com.fourigin.argo.content.editor.text || {};
com.fourigin.argo.content.editor.text.View = com.fourigin.argo.content.editor.text.View || (function ()
{
    var View = function (jTarget, oContent) {
        this.jTarget = jTarget;
        this.jPrototype = null;
        this.oContent = oContent;
        this.sEditorHtmlId = "tinyMceEditor_" + Math.random().toString(36).substr(2, 16);
        this.TinyMceInstance = null;
        //
        this.findHtmlPrototype();
        //
        return this;
    };
    //
    View.prototype = new com.fourigin.argo.content.editor.View();

    View.prototype.findHtmlPrototype = function() {
        this.jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=text]").clone();
    };

    View.prototype.generateEditor = function(oContent) {
        if(!this.jPrototype || 0 === this.jPrototype.length) {
            console.error("Prototype not defined!");
        }
        console.info("Genrate Text Editor.", oContent, this.sEditorHtmlId);
        // fill with data
        this.jPrototype.find("input[name=name]").val(oContent.name);
        this.jPrototype.find("textarea[name=content]").val(oContent.content).attr("id", this.sEditorHtmlId);
        this.jPrototype.find(".contentHtmlRendered").html(oContent.content);
        // add to html
        this.jTarget.append(this.jPrototype);
        // add and init html-editor "tinyMCE"

        this.TinyMceInstance = tinymce.init({
            selector: '#' + this.sEditorHtmlId,
            menubar: false,
            content_css: '/assets/styles/argo-tinymce.css',
            setup : function(editor) {
                editor.on("change", function(e){
                    console.log('saving');
                    editor.save(); // updates this instance's textarea
                });
            }
        });

        this.setEvents();
    };

    View.prototype.setEvents = function() {
        /**
         * Update textarea by tinyMCE
          */
        var self = this;

        this.jPrototype.on("save.argo", function() {
            console.info("got trigger save.");
            //self.TinyMceInstance.triggerSave();
        });

        this.setEventsButtonMoreOptions();
    };

    View.prototype.showHtmlEditor = function () {
        return "HTML Editor active";
    };
    //
    return View;
}());