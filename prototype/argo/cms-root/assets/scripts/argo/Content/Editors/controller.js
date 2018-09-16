if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.Editor = com.fourigin.argo.content.Editor || (function ()
{
    var Editor = function(jEditorTarget, jEditorPrototype) {
        this.Model  = new com.fourigin.argo.content.editor.Model();
        this.View   = new com.fourigin.argo.content.editor.View();
        //
        return this;
    };
    //
    Editor.prototype.generateEditor = function() {
        this.View.generateEditor();
    };
    //
    return Editor;
}());