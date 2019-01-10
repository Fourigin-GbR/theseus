function Slider(slider, configuration) {
    var self = this;
    //
    this.sliderPageStorage = slider.find("input.sliderPageStorage");
    this.slideHandler = slider.find(".sliderWrapper").find("ul");
    this.slideItems = this.slideHandler.find("li");
    this.sliderSlideLeft = slider.find(".sliderSlideLeft");
    this.sliderSlideRight = slider.find(".sliderSlideRight");
    this.pagination = slider.find(".sliderPagination");
    this.slidesPerPage = null;
    this.pagesAmount = 1;
    this.currentLeftPosition = null;
    this.matchMedias = [];
    this.configuration = configuration;
    //
    // Default: Start on page 1
    if(!this.getCurrentPage()) {
        this.setCurrentPage(1);
    }
    //
    //
    // Initial:
    this.getAndApplyAmountOfSlides();
    this.generatePagination();
    this.slideToPage(this.getCurrentPage());
    this.setCurrentPageActiveInPagination();
    this.updateVisibleStatusOfSlideButtons();
    //
    this.sliderSlideLeft.on("click", function (e) {
        e.stopPropagation();
        self.slideByDirection("left")
    });
    this.sliderSlideRight.on("click", function (e) {
        self.slideByDirection("right")
    });
}
//
Slider.prototype.getCurrentPage = function () {
    return parseInt(this.sliderPageStorage.val());
};
Slider.prototype.setCurrentPage = function (page) {
    return this.sliderPageStorage.val(parseInt(page));
};
//
Slider.prototype.generatePagination = function() {
    this.pagination.find("li").remove();
    for(var i=0, il=this.pagesAmount; i !== il; i++) {
        this.pagination.find("ul").append("<li></li>");
    }
};
//
Slider.prototype.setCurrentPageActiveInPagination = function() {
    var classNameActive = "active",
        currentPageIndex = this.getCurrentPage() - 1,
        paginationItems = this.pagination.find("li");
    //
    // reset:
    paginationItems.removeClass(classNameActive);
    paginationItems.eq(currentPageIndex).addClass(classNameActive);
};
//
//
Slider.prototype.getCurrentPageBySliderPosition = function() {
    return (parseInt(this.slideHandler[0].style.left) / 100 * (-1)) + 1;
};
//
Slider.prototype.getLeftPositionByPage = function(page) {
    return (page - 1) * 100 * (-1);
};
//
//
Slider.prototype.slideToPage = function(page) {
    var newLeftPosition = this.getLeftPositionByPage(page);
    //
    this.slideHandler[0].style.left = newLeftPosition + "%";
    this.setCurrentPage(page);
    this.loadSlideImageIfNotActivated();
    //
    this.updateVisibleStatusOfSlideButtons();
    this.setCurrentPageActiveInPagination();
};
//
Slider.prototype.loadSlideImageIfNotActivated = function() {
    // If the item has not yet the attribute 'src' on the img-tag, look for data-attribute 'data-image-src' and write it to the attribute 'src'
    var currentPage = this.getCurrentPage(),
        jCurrentSlide = jQuery(this.slideItems[currentPage-1]),
        jCurrentSlideImage = jCurrentSlide.find("img");
    //
    if(0 === jCurrentSlideImage.length) {
        return;
    }

    if(!jCurrentSlideImage[0].hasAttribute("src") && jCurrentSlideImage[0].hasAttribute("data-image-src")) {
        jCurrentSlideImage.attr("src", jCurrentSlideImage.attr("data-image-src"));
    }
};
//
//
Slider.prototype.getAndApplyAmountOfSlides = function () {
    var self = this;
    //
    for(var i=0, il= this.configuration.slideItemsAmountPerPage.length; i!==il; i++) {

        (function() {
            var slideAmount = self.configuration.slideItemsAmountPerPage[i],
                minWidth = slideAmount.min ? slideAmount.min : null,
                maxWidth = slideAmount.max ? slideAmount.max : null,
                queries = [],
                matchMedia = {"match": null, "callback": null};
            //
            minWidth ? queries.push("(min-width: " + minWidth + ")") : "";
            maxWidth ? queries.push("(max-width: " + maxWidth + ")") : "";
            //
            matchMedia.match = window.matchMedia(
                queries.join(" and ")
            );

            var cb = function (matchMediaObject, resizeEvent) {
                //
                if(matchMediaObject.matches) {
                    self.pagesAmount = Math.ceil(self.slideItems.length / slideAmount.slideItems);
                    self.slideItems.css("width", 100 / slideAmount.slideItems + "%");
                    //
                    // On resize: Go back to page 1:
                    if(resizeEvent) self.slideToPage(1);
                    self.updateVisibleStatusOfSlideButtons();
                    self.generatePagination();
                    self.setCurrentPageActiveInPagination();
                }
            };

            if (matchMedia.match.matches) {
                cb(matchMedia.match);
            }
            matchMedia.match.addListener(function(matchMediaObject){
                cb(matchMediaObject, true);
            });
            //
            self.matchMedias.push(matchMedia);
        })()
    }
};
//
//
Slider.prototype.updateVisibleStatusOfSlideButtons = function() {
    var hideButtonClassName = "inactive",
        currentPage = this.getCurrentPage();
    //
    if(this.pagesAmount === 1) {
        this.sliderSlideLeft.addClass(hideButtonClassName);
        this.sliderSlideRight.addClass(hideButtonClassName);
        return;
    }
    //
    if(currentPage === 1) {
        this.sliderSlideLeft.addClass(hideButtonClassName);
    }
    else {
        this.sliderSlideLeft.removeClass(hideButtonClassName);
    }

    if(currentPage === this.pagesAmount) {
        this.sliderSlideRight.addClass(hideButtonClassName);
    }
    else {
        this.sliderSlideRight.removeClass(hideButtonClassName);
    }
};
//
Slider.prototype.showOrHideSlideButtons = function(action, buttons) {
    var hideButtonClassName = "inactive";
    //
    for(var i=0, il=buttons.length; i !== il; i++) {
        switch (buttons[i]) {
            case "slideLeft":
                action === "show" ? this.sliderSlideLeft.removeClass(hideButtonClassName) : this.sliderSlideLeft.addClass(hideButtonClassName);
                break;
            case "slideRight":
                action === "show" ? this.sliderSlideRight.removeClass(hideButtonClassName) : this.sliderSlideRight.addClass(hideButtonClassName);
                break;
        }
    }
};
//
Slider.prototype.slideByDirection = function (direction) {
    var newLeftPosition, currentPage, newPage;
    //
    currentPage = this.getCurrentPage();
    //
    switch (direction) {
        case "left":
            if (currentPage === 1) {
                newLeftPosition = 0;
            }
            else {
                newPage = currentPage - 1;
                newLeftPosition = this.getLeftPositionByPage(newPage);
            }
            break;
        case "right":
            if (currentPage >= this.pagesAmount) {
                newLeftPosition = this.getLeftPositionByPage(this.pagesAmount);
            }
            else {
                newPage = currentPage + 1;
                newLeftPosition = this.getLeftPositionByPage(newPage);
            }
            break;
        default:
            newLeftPosition = 0;
            newPage = 1;
    }
    this.slideToPage(newPage);
};