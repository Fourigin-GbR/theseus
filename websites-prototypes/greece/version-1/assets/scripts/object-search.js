var objectSearch = function() {
    this.jForm = jQuery("form#searchObjects");
    this.jObjects = jQuery("ul.smallItemGalleryList > li");

    objectSearch.prototype.getMatchingObjectsRequest = function() {
        var self = this,
            requestData = {
                "index": "objects-to-rent",
                "categories": {
                    "region": []
                },
                "fields": {}
            };

        var formularData = self.jForm.serializeArray();
        for(var i=0, il= formularData.length; i<il; i++) {
            if(formularData[i].name === "region"){
                requestData.categories.region.push(formularData[i].value);
            }

            if(formularData[i].name === "type"){
                requestData.fields = {
                    "type": {
                        "comparator": "EQUAL",
                        "value": formularData[i].value
                    }
                }
            }
        }

        self.deactivateAllOverlays();

        jQuery.ajax({
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            method: "post",
            processData: false,
            url: "http://argo.greekestate.fourigin.com/cms/search/?base=DE&path=objects/rent/search",
            data: JSON.stringify(requestData), //self.jForm.serialize(),
            success: function(data){
                self.showMatchingObjects(data);
            },
            error: function() {
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

    objectSearch.prototype.deactivateAllOverlays = function() {
        jQuery(".overlay").removeClass("active");
    };

    objectSearch.prototype.deactivateAllObjects = function() {
        this.jObjects.each(function() {
            jQuery(this).addClass("hidden");
        });
    };

    objectSearch.prototype.showMatchingObjects = function(objectsList2) {
        var self = this;

        var objectsList = ["object_3813300"];

        //console.info("Show matching objects...:", self.jObjects);
        self.jObjects.each(function() {
            var jThis =  jQuery(this),
                currentObjectId = jThis.attr("data-object-id");
            //
            //console.info("Show matching objects...: Handle ", currentObjectId, " --> ", objectsList.indexOf(currentObjectId));
            if(objectsList.indexOf(currentObjectId) > -1) {
                jThis.removeClass("hidden");
                self.enableImages(jThis);
            }
            else {
                jThis.addClass("hidden");
            }
        });

        if(objectsList.length === 0) {
            this.showNoResultsOverlay();
        }
    };

    objectSearch.prototype.enableImages = function(jObject) {
        var jUnAbledImages = jObject.find("img[data-image-src]");
        console.info("Found this images: ", jObject, jUnAbledImages);
        jUnAbledImages.each(function() {
            var jThis = jQuery(this);
            console.info("Handle this image, get src: ", jThis, jThis.attr("src"), !jThis.attr("src"));
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
