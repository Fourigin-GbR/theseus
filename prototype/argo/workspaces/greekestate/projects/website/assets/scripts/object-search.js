var objectSearch = function() {
    this.jForm = jQuery("form#searchObjects");
    this.jObjects = jQuery("ul.smallItemGalleryList > li");
    this.jResultAmount = jQuery("[data-content='result-amount']");
    this.hideLoaderTimeoutId = null;

    objectSearch.prototype.getMatchingObjectsRequest = function() {
        var self = this,
            requestData = {
                "index": self.jForm.attr("data-form-index"),
                "categories": {
                    "region": []
                }
            };

        self.showLoadingBox();

        var formularData = self.jForm.serializeArray(),
            formAction = self.jForm.attr("action");
        for(var i=0, il= formularData.length; i<il; i++) {
            if(formularData[i].name === "region"){
                requestData.categories.region.push(formularData[i].value);
            }
            if(formularData[i].name === "type"){
                if("undefined" === typeof requestData.categories.type) {
                    requestData.categories["type"] = [];
                }
                requestData.categories.type.push(formularData[i].value);
            }
        }

        self.deactivateAllOverlays();

        jQuery.ajax({
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            method: "post",
            processData: false,
            url: formAction,
            data: JSON.stringify(requestData), //self.jForm.serialize(),
            success: function(data){
                self.hideLoadingBox();
                self.showMatchingObjects(data);
            },
            error: function() {
                self.hideLoadingBox();
                self.deactivateAllObjects();
                self.showErrorOverlay();
            }
        });
    };

    objectSearch.prototype.showErrorOverlay = function() {
        jQuery(".overlay").removeClass("active");
        jQuery(".overlay.overlayError").addClass("active");
    };

    objectSearch.prototype.showNoResultsOverlay = function() {
        jQuery(".overlay").removeClass("active");
        jQuery(".overlay.overlayNoResult").addClass("active");
    };

    objectSearch.prototype.showLoadingBox = function() {
        if(this.hideLoaderTimeoutId) {
            window.clearTimeout(this.hideLoaderTimeoutId);
        }
        jQuery(".box.boxLoading").addClass("active");
    };
    objectSearch.prototype.hideLoadingBox = function() {
        if(this.hideLoaderTimeoutId) {
            window.clearTimeout(this.hideLoaderTimeoutId);
        }
        this.hideLoaderTimeoutId = window.setTimeout(function(){
            jQuery(".box.boxLoading").removeClass("active");
        }, 800);
    };

    objectSearch.prototype.deactivateAllOverlays = function() {
        jQuery(".overlay").removeClass("active");
    };

    objectSearch.prototype.deactivateAllObjects = function() {
        var self = this;
        this.jObjects.each(function() {
            self.hideObject(jQuery(this));
        });
    };

    objectSearch.prototype.hideObject = function(jObject) {
        jObject.addClass("hidden").removeClass("active");
    };
    objectSearch.prototype.showObject = function(jObject) {
        jObject.addClass("active").removeClass("hidden");
    };

    objectSearch.prototype.showMatchingObjects = function(objectsList) {
        var self = this;
        //console.info("Show matching objects...:", self.jObjects);
        self.jResultAmount.html(objectsList.length);
        self.jObjects.each(function() {
            var jThis =  jQuery(this),
                currentObjectId = jThis.attr("data-object-id");
            //
            //console.info("Show matching objects...: Handle ", currentObjectId, " --> ", objectsList.indexOf(currentObjectId));
            if(objectsList.indexOf(currentObjectId) > -1) {
                self.showObject(jThis);
                self.enableImages(jThis);
            }
            else {
                self.hideObject(jThis);
            }
        });

        if(objectsList.length === 0) {
            this.showNoResultsOverlay();
        }
    };

    objectSearch.prototype.enableImages = function(jObject) {
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

    objectSearch.prototype.setEvents = function() {
        var self = this;

        self.jForm.find("input, select").on("change", function() {
            self.jForm.trigger("submit");
        });

        self.jForm.on("submit", function(e) {
            e.preventDefault();
            self.getMatchingObjectsRequest();
        });
    }
};

jQuery("document").ready(function(){
    var searchController = new objectSearch();
    searchController.getMatchingObjectsRequest();
    searchController.setEvents();
});
