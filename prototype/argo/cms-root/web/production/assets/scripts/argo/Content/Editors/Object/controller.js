if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.Object = com.fourigin.argo.content.editor.Object || (function ()
{
    var Object = function(jEditorTarget, oContent) {
        this.Model  = new com.fourigin.argo.content.editor.object.Model();
        this.View   = new com.fourigin.argo.content.editor.object.View(jEditorTarget);
        this.oContent = oContent;
        this.generateEditor();
        //
        return this;
    };
    //
    Object.prototype = new com.fourigin.argo.content.Editor();

    Object.prototype.generateEditor = function() {
        this.View.generateEditor(this.oContent);
    };
    //
    return Object;
}());