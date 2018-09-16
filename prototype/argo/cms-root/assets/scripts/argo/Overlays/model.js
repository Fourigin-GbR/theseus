if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.overlays = com.fourigin.argo.overlays || {};
com.fourigin.argo.overlays.Model = com.fourigin.argo.overlays.Model || (function () {
    var Model = function () {
        this.aOverlays = [];
        //
        return this;
    };
    //

    Model.prototype.createOverlay = function (jOverlay, oOverlays) {
        var Overlay = new com.fourigin.argo.overlays.Overlay(jOverlay, oOverlays);
        this.aOverlays.push(Overlay);
        return Overlay;
    };

    Model.prototype.deleteOverlay = function(sId) {

    };

    Model.prototype.deleteAllOverlays = function () {
        this.aOverlays = [];
    };
    //
    return Model;
}());