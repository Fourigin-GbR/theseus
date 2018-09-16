if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.group = com.fourigin.argo.content.editor.group || {};
com.fourigin.argo.content.editor.group.Model = com.fourigin.argo.content.editor.group.Model || (function ()
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