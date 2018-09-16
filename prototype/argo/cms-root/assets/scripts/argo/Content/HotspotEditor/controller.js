if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.HotspotEditor = com.fourigin.argo.content.HotspotEditor || (function ()
{
    var HotspotEditor = function(oConfiguration) {
        this.Model = new com.fourigin.argo.content.hotspotEditor.Model();
        this.View = new com.fourigin.argo.content.hotspotEditor.View(oConfiguration.view_target, oConfiguration.view_prototype);

        this.View.Controller = this;

        this.configuration = oConfiguration;
        this.ContentEditor = null;
        //
        this.initialize();
        //
        return this;
    };
    //
    HotspotEditor.prototype.initialize = function() {
        var self = this;
        this.Model.setSiteBase(this.configuration.siteBase);
        this.Model.setSitePath(this.configuration.sitePath);
        this.Model.setContentPath(this.configuration.contentPath);
        this.Model.loadContent(function(){
            self.verifyStatusOfLoadingBaseContents();
        });
        this.Model.loadContentPrototype(function(){
            self.verifyStatusOfLoadingBaseContents();
        });
    };

    HotspotEditor.prototype.verifyStatusOfLoadingBaseContents = function() {
        if(this.Model.getContent() && this.Model.getContentPrototype()) {
            this.generateEditor();
            this.generateContentEditors();
        }
    };

    HotspotEditor.prototype.generateEditor = function() {
        /**
         * Generate Editor.
         */
        this.View.generateEditor(this.configuration.hotSpotDefinition);


/*        var contentType = contentPrototype.type;
        console.info("Generate content-editor. \n" +
            "Type" + contentType,
            "");*/
    };


    HotspotEditor.prototype.generateContentEditors = function () {
        var jTarget = this.View.getEditorTargetForContentEditors(),
            oContentPrototype = this.Model.getContentPrototype(),
            oContent = this.Model.getContent(),
            oHotSpotDefinition = this.configuration.hotSpotDefinition;

        console.log("oContentPrototype", oContentPrototype);

            switch (oContentPrototype.type) {
                case "text":
                    new com.fourigin.argo.content.editor.Text(jTarget, oContent);
                    break;
                case "object":
                    new com.fourigin.argo.content.editor.Object(jTarget, oContent);
                    break;
                case "group":
                    new com.fourigin.argo.content.editor.Group(jTarget, oContent);
                    break;
                case "list":
                    new com.fourigin.argo.content.editor.List(jTarget, oContent);
                    break;
                default:
                    console.error("Hotspot type '" + oContentPrototype.type + "' not supported.");
            }

    };

    HotspotEditor.prototype.save = function(jFieldset, callbackSuccess, callbackError) {
        this.Model.save(jFieldset, callbackSuccess, callbackError);
    };

    //
    return HotspotEditor;
}());