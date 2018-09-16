if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.overlays = com.fourigin.argo.overlays || {};
com.fourigin.argo.overlays.Overlay = com.fourigin.argo.overlays.Overlay || (function ()
{
    var Overlay = function(jOverlay, Overlays) {
        this.jOverlay = jOverlay;
        this.Model = new com.fourigin.argo.overlays.overlay.Model();
        this.Overlays = Overlays;
            //
        this.setUiEvents();
        //
        return this;
    };
    //
    Overlay.prototype.close = function() {
        this.Overlays.hideOverlay(this);
        this.Model.setInactive();
        this.jOverlay.removeClass("active");
    };

    Overlay.prototype.show = function() {
        this.Overlays.showOverlay(this);
        this.Model.setActive();
        this.jOverlay.addClass("active");
    };
    //
    Overlay.prototype.setUiEvents = function() {
        var self = this;
        this.jOverlay.find(".interactionButton__close").on("click", function() {
            self.close();
        });
    };
    //
    return Overlay;
}());