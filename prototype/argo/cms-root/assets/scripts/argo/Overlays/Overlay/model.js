if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.overlays = com.fourigin.argo.overlays || {};
com.fourigin.argo.overlays.overlay = com.fourigin.argo.overlays.overlay || {};
com.fourigin.argo.overlays.overlay.Model = com.fourigin.argo.overlays.overlay.Model || (function () {
    var Model = function () {
        this.isActive = false;
        //
        return this;
    };
    //

    Model.prototype.setActive = function () {
        this.isActive = true;
    };

    Model.prototype.setInactive = function () {
        this.isActive = false;
    };

    Model.prototype.isActive = function () {
        return this.isActive;
    };
    //
    return Model;
}());