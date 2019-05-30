var staticSearchResult = function() {

    $.urlParam = function(name){
        var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(window.location.href);
        if (results==null) {
            return null;
        }
        return decodeURI(results[1]) || 0;
    };

    this.jPageResultEntries = jQuery("ul.pageSearchResultList > li");
    this.jResultAmount = jQuery("[data-content='result-amount']");
    this.objectIds = $.urlParam('object-id');

    jQuery("input[name='object-id']").val(this.objectIds);

    // overlays, info-boxes

    staticSearchResult.prototype.showErrorOverlay = function() {
        jQuery(".overlay").removeClass("active");
        jQuery(".overlay.overlayError").addClass("active");
    };

    staticSearchResult.prototype.showNoResultsOverlay = function() {
        jQuery(".overlay").removeClass("active");
        jQuery(".overlay.overlayNoResult").addClass("active");
    };

    staticSearchResult.prototype.showLoadingBox = function() {
        if(this.hideLoaderTimeoutId) {
            window.clearTimeout(this.hideLoaderTimeoutId);
        }
        jQuery(".box.boxLoading").addClass("active");
    };
    staticSearchResult.prototype.hideLoadingBox = function() {
        if(this.hideLoaderTimeoutId) {
            window.clearTimeout(this.hideLoaderTimeoutId);
        }
        this.hideLoaderTimeoutId = window.setTimeout(function(){
            jQuery(".box.boxLoading").removeClass("active");
        }, 800);
    };

    staticSearchResult.prototype.deactivateAllOverlays = function() {
        jQuery(".overlay").removeClass("active");
    };


    // result entries

    staticSearchResult.prototype.deactivateAllObjects = function() {
        var self = this;
        this.jPageResultEntries.each(function() {
            self.hideObject(jQuery(this));
        });
    };

    staticSearchResult.prototype.hideObject = function(jObject) {
        jObject.addClass("hidden").removeClass("active");
    };
    staticSearchResult.prototype.showObject = function(jObject) {
        jObject.addClass("active").removeClass("hidden");
    };

    staticSearchResult.prototype.showMatchingObjects = function(objectIds) {
        var self = this,
            counterMatchingObjects = 0;

        if(objectIds) {
            self.jPageResultEntries.each(function () {
                var jThis = jQuery(this),
                    currentObjectId = jThis.attr("data-object-id"),
                    currentObjectCode = jThis.attr("data-object-code");
                //

                if((objectIds.indexOf(currentObjectId) > -1) || (objectIds.indexOf(currentObjectCode) > -1)) {
                    self.showObject(jThis);
                    self.enableImages(jThis);
                    counterMatchingObjects++;
                } else {
                    self.hideObject(jThis);
                }
            });
        }

        self.jResultAmount.html(counterMatchingObjects);

        if(!objectIds || objectIds.length === 0) {
            this.showNoResultsOverlay();
        }
    };

    staticSearchResult.prototype.enableImages = function(jObject) {
        var jUnAbledImages = jObject.find("img[data-image-src]:first");
        //console.info("Found this images: ", jObject, jUnAbledImages);
        jUnAbledImages.each(function() {
            var jThis = jQuery(this);
            //console.info("Handle this image, get src: ", jThis, jThis.attr("src"), !jThis.attr("src"));
            if(undefined === jThis.attr("src")) {
                jThis.attr("src", (jThis.attr("data-image-src")));
            }
        });
    };

    staticSearchResult.prototype.setEvents = function() {
        var self = this;


    };


    this.showMatchingObjects(this.objectIds ? [this.objectIds] : null);
};

jQuery("document").ready(function(){
    new staticSearchResult();
});
