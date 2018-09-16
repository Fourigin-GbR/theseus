if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.editor = com.fourigin.argo.content.editor || {};
com.fourigin.argo.content.editor.View = com.fourigin.argo.content.editor.View || (function ()
{
    var View = function (jTarget, jPrototype, oContent) {
        this.jTarget = jTarget;
        this.jPrototype = jPrototype;
        this.oContent = oContent;
        //
        return this;
    };
    //

    View.prototype.setEventsButtonMoreOptions = function() {
        this.jTarget.find(".button_moreOptions").on("click", function() {
            //jQuery(this).siblings(".moreOptions").toggle();
            var jThis = jQuery(this),
                jMoreOptions = jThis.siblings(".moreOptions");
            if(jThis.hasClass("opened")) {
                jMoreOptions.slideUp();
                jThis.removeClass("opened");
            }
            else {
                jMoreOptions.slideDown();
                jThis.addClass("opened");
            }
        });
    };

    View.prototype.generateEditor = function () {
       alert("Parent - Generate Editor.");

    };
    //
    return View;
}());