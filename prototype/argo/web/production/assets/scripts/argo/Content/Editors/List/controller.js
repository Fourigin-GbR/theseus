if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.List = com.fourigin.argo.content.editor.List || (function ()
{
    var List = function(jEditorTarget, oContent) {
        this.Model  = new com.fourigin.argo.content.editor.list.Model();
        this.View   = new com.fourigin.argo.content.editor.list.View(jEditorTarget);
        this.oContent = oContent;
        this.generateEditor();
        //
        return this;
    };
    //
    List.prototype = new com.fourigin.argo.content.Editor();

    List.prototype.generateEditor = function() {
        this.View.generateEditor(this.oContent);
    };
    //
    return List;
}());