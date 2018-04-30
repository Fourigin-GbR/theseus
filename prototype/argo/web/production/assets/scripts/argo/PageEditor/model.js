if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.pageEditor = com.fourigin.argo.pageEditor || {};
com.fourigin.argo.pageEditor.Model = com.fourigin.argo.pageEditor.Model || (function ()
{
    var Model = function() {
        this.oContent = null;
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
    Model.prototype.getBase = function() {
        return this.oBase;
    };


    Model.prototype.setPath = function (sPath) {
        this.sPath = sPath;
    };
    Model.prototype.getPath = function() {
        return this.sPath;
    };


    Model.prototype.setContent = function (oContent) {
        this.oContent = oContent;
    };
    //
    Model.prototype.getContent = function() {
        return this.oContent;
    };
    Model.prototype.getContentByPath = function(sPath) {
        // TODO: Supports only one-level search
        var sCleanPath = sPath.replace(/^\/|\/$/g, ''),
            aPath = sCleanPath.split("/"),
            self = this;
        //
        for(var i=0, il=self.oContent.length; i<il; i++) {
            if(self.oContent[i].name === aPath[0]) {
               return self.oContent[i];
            }
        }

        return false
    };


    Model.prototype.setHotspots = function (oHotspots) {
        this.oHotspots = oHotspots;
    };
    Model.prototype.getHotspots = function() {
        return this.oHotspots;
    };

    //
    return Model;
}());