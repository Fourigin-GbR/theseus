if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.Model = com.fourigin.argo.content.editor.Model || (function ()
{
    var Model = function () {
        this.oContent = null;
        this.oContentPrototype = null;
        this.oHotspots = null;
        this.sPath = null;
        this.sBase = null;
        //
        return this;
    };
    //

    Model.prototype.setBase = function (oBase) {
        this.oBase = oBase;
    };
    //
    return Model;
}());