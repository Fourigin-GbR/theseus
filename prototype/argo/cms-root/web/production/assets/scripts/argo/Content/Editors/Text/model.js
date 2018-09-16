if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.text = com.fourigin.argo.content.editor.text || {};
com.fourigin.argo.content.editor.text.Model = com.fourigin.argo.content.editor.text.Model || (function ()
{
    var Model = function () {
        this.oContent = null;
        this.oContentPrototype = null;
        //
        return this;
    };
    //
    Model.prototype = new com.fourigin.argo.content.editor.Model();

    Model.prototype.setContent = function (oContent) {
        this.oContent = oContent;
    };
    Model.prototype.getContent = function () {
        return this.oContent;
    };
    //
    return Model;
}());