if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.Text = com.fourigin.argo.content.editor.Text || (function ()
{
    var Text = function(jEditorTarget, oContent) {
        this.Model  = new com.fourigin.argo.content.editor.text.Model();
        this.View   = new com.fourigin.argo.content.editor.text.View(jEditorTarget);
        this.oContent = oContent;
        this.generateEditor();
        //
        return this;
    };
    //
    Text.prototype = new com.fourigin.argo.content.Editor();

    Text.prototype.generateEditor = function() {
        this.View.generateEditor(this.oContent);
    };
    //
    return Text;
}());