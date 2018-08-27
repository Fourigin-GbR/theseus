if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.Group = com.fourigin.argo.content.editor.Group || (function ()
{
    var Group = function(jEditorTarget, oContent) {
        this.Model  = new com.fourigin.argo.content.editor.group.Model();
        this.View   = new com.fourigin.argo.content.editor.group.View(jEditorTarget);
        this.oContent = oContent;
        this.generateEditor();
        //
        return this;
    };
    //
    Group.prototype = new com.fourigin.argo.content.Editor();

    Group.prototype.generateEditor = function() {
        this.View.generateEditor(this.oContent);
    };
    //
    return Group;
}());