if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.Overlays = com.fourigin.argo.Overlays || (function ()
{
    var Overlays = function() {
        this.Model = new com.fourigin.argo.overlays.Model();
        //
        return this;
    };
    //
    Overlays.prototype.createOverlay = function(jOverlay) {
        return this.Model.createOverlay(jOverlay, this);
    };

    Overlays.prototype.showOverlay = function(oOverlay) {
        var jOverlayWrapper = jQuery(".overlayWrapper");
        //
        jOverlayWrapper.addClass("active");
    };

    Overlays.prototype.hideOverlay = function(oOverlay) {
        var jOverlayWrapper = jQuery(".overlayWrapper");
        //
        jOverlayWrapper.removeClass("active");
    };
    //
    //
    return Overlays;
}());