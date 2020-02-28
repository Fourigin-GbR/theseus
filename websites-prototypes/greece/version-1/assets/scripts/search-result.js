var searchResult = function() {
    this.jForm = jQuery("form#searchObjects");
    this.jResultItemPrototype = null;
    this.jResultItemImagePrototype = null;

    searchResult.prototype.fetchPrototypes = function() {
        this.jResultItemImagePrototype = jQuery("[data-prototype='search-result-item-image']").remove().clone().removeAttr("data-prototype");
        this.jResultItemPrototype = jQuery("[data-prototype='search-result-item']").remove().clone().removeAttr("data-prototype");
    };

    searchResult.prototype.showResults = function(data) {
        var results = data || this.results;
        var jTarget = jQuery("[data-element='search-result-list-container']");

        jTarget.empty();

        for (var key in results) {
            if(results.hasOwnProperty(key)) {
                var jPrototype = this.jResultItemPrototype.clone();
                // Data:
                jPrototype.find("[data-content='category']").html(results[key]["category"]);
                jPrototype.find("[data-content='description']").html(results[key]["description"]);
                // Images:
                if(results[key]["images"]) {
                    var jImagesTarget = jPrototype.find("[data-element='search-result-item-images-container']");
                    for(var i=0, il= results[key]["images"].length; i<il; i++) {
                        var imagePrototype = this.jResultItemImagePrototype.clone();
                        imagePrototype.find("[data-content='image-src']").attr("src", results[key]["images"][i]);
                        jImagesTarget.append(imagePrototype);
                    }
                }
                // Append to DOM:
                jTarget.append(jPrototype);
                this.applySlider(jTarget);
            }
        }
    };

    searchResult.prototype.setEvents = function() {
        var self = this;

        this.jForm.find("input, select").on("change", function() {
            self.jForm.trigger("submit");
        });

        this.jForm.on("submit", function(e) {
            e.preventDefault();
            self.loadResultList();
        });
    };

    searchResult.prototype.loadResultList = function() {
        var self = this;
        jQuery.ajax({
            dataType: "json",
            url: "immobilien.json",
            cache: false,
            data: this.jForm.serialize(),
            success: function(data){self.showResults(data)}
        });
    };

    searchResult.prototype.applySlider = function(jTarget) {
        jTarget.find(".slider").each(function () {
            new Slider(
                jQuery(this),
                {
                    "slideItemsAmountPerPage": [
                        {
                            "min": "0px",
                            "max": null,
                            "slideItems": 1
                        }
                    ]
                }
            );
        });
    };

    this.fetchPrototypes();
    this.setEvents();
};

jQuery("document").ready(function() {
    var sr = new searchResult();
    sr.loadResultList();
});