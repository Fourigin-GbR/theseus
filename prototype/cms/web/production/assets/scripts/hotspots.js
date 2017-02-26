/**
 * Created by Karsten on 19.02.2017.
 */
var fourigin = fourigin || {};
fourigin.cms = fourigin.cms || {};

fourigin.cms.Hotspot = function (oEditor, jIframe, jHotspotAmountsIndicator) {
    /*
     Need the editor to pass actions.
     Need the iframe to calculate positions for the marker.
     */
    console.log("Init Hotspot");
    if (!oEditor) {
        console.error("Parameter 'editor' is missing! Can not instantiate Hotspot.");
    }
    if (!jIframe) {
        console.error("Parameter 'iFrame' is missing! Can not instantiate Hotspot.");
    }
    //
    this.oEditor = oEditor;
    this.jIframe = jIframe;
    this.jHotspotAmountsIndicator = jHotspotAmountsIndicator;
};

/*
 Properties:
 */
fourigin.cms.Hotspot.prototype.aHotspots = null;
fourigin.cms.Hotspot.prototype.oEditor = null;
fourigin.cms.Hotspot.prototype.jIframe = null;
fourigin.cms.Hotspot.prototype.jHotspotAmountsIndicator = null;
//
fourigin.cms.Hotspot.prototype.oMarkupMasters = {
    "empty": {},
    "full": {}
};

/*
 Methods:
 */

fourigin.cms.Hotspot.prototype.initializeMarkupMasters = function (sType, sMarkup) {
    console.log("Get MAsters");
    if (!sType in fourigin.cms.Hotspot.prototype.oMarkupMasters) {
        console.error("Can not initialize master, because type '" + sType + "' is not valid.");
        return false;
    }
    console.warn("Set master for type: ", sType);
    fourigin.cms.Hotspot.prototype.oMarkupMasters[sType] = sMarkup;
    //
    return true;
};

fourigin.cms.Hotspot.prototype.getAllHotspots = function () {
    var jIframe = this.jIframe,
        jHotspotsMarkers = jIframe.contents().find(".hotspotMarker"),
        aHotspots = [];
console.log("Found these hotspots:", jHotspotsMarkers, jIframe, jIframe.contents());

    for (var i = 0, l = jHotspotsMarkers.length; i < l; i++) {
        /**
         * The Marker is just necessary to mark the element *before* its position as ContentEdit.
         * So get the direct element before the marker:
         */
        var jMarker = jQuery(jHotspotsMarkers[i]),
            jHotspotContentElement = jMarker.prev(),
            oHotspotDefinitionData,
            oHotspotItem;
        if (0 === jHotspotContentElement.length) {
            console.error("Can not find proper previous element for this content-marker: ", jMarker);
        }
        else {
            // Select the variable to get the hotspot-definition-data:
            oHotspotDefinitionData = window.frames[0].window[jMarker.attr("id")];
            if (!oHotspotDefinitionData) {
                console.error("Can not find data for element ", jMarker.attr("id"));
                return false;
            }
            oHotspotItem = {"jHotspotContentElement": jHotspotContentElement, "jMarker": jMarker, "oHotspotDefinitionData": oHotspotDefinitionData};
            aHotspots.push(oHotspotItem);
        }
    }
    //
    this.aHotspots = aHotspots;
};

fourigin.cms.Hotspot.prototype.initializeAllHotspots = function () {
    console.log("Initialie All Hotspots.");
    var aHotspotItems;
    //
    this.getAllHotspots();
    aHotspotItems = this.aHotspots;
    //
    for (var i = 0, l = aHotspotItems.length; i < l; i++) {
        this.setHotspotButton(aHotspotItems[i]);
    }
    this.fitHotspotButtonsPosition();
    this.updateHotspotsAmountIndicator();
};

fourigin.cms.Hotspot.prototype.updateHotspotsAmountIndicator = function()
{
    var iHotspotsAmount = this.aHotspots.length;
    var jIndicator = this.jHotspotAmountsIndicator;
    //
    if(jIndicator) {
        jIndicator.fadeOut(200, function () {
            jQuery(this).html(iHotspotsAmount).fadeIn(200);
        });
    }
};

fourigin.cms.Hotspot.prototype.setHotspotButton = function (oHotspotItem) {
    console.log("set Button. type: ", oHotspotItem);
    var jHotspotContentElement = oHotspotItem.jHotspotContentElement,
        jMarker = oHotspotItem.jMarker,
        oHotspotDefinitionData = oHotspotItem.oHotspotDefinitionData,
        jIframe = this.jIframe,
        iIframeOffsetTop = jIframe.offset().top,
        iIframeOffsetLeft = jIframe.offset().left,
        iContentElementOffsetLeft = jHotspotContentElement.offset().left,
        iContentElementOffsetTop = jHotspotContentElement.offset().top,
        jHotspotButton = null;
    //
    if (oHotspotDefinitionData["isEmpty"]) {
        jHotspotButton = jQuery(this.oMarkupMasters.empty);
    }
    else {
        jHotspotButton = jQuery(this.oMarkupMasters.full);
    }
    // Add button to item-settings:
    oHotspotItem.jHotspotButton = jHotspotButton;
    console.log("New items: ", this.aHotspots);
    //
    // Filling the button:
    jHotspotButton.find(".name").html(oHotspotDefinitionData.contentEditorName);
    //jNewMarker.find(".type").html(oHotspotDefinitionData.contentEditorData.type);
    console.log("jContentElement: ", jHotspotContentElement, "offset()", jHotspotContentElement.offset(), " Data: ", oHotspotDefinitionData);
    jHotspotButton.data("contentElementId", jMarker.attr("id"));
    jQuery("body").append(jHotspotButton);
    jHotspotButton.css("left", (iIframeOffsetLeft + iContentElementOffsetLeft));
    jHotspotButton.css("top", (iIframeOffsetTop + iContentElementOffsetTop - jHotspotButton.outerHeight()));
    this.setHotspotButtonEvents(oHotspotItem);
};

fourigin.cms.Hotspot.prototype.fitHotspotButtonsPosition = function () {
    console.info("fitHotspotButtonsPosition.", this.aHotspots);
    var aAllHotspots = this.aHotspots;
    //
    for(var i = 1, il = aAllHotspots.length; i<il; i++) // Start with the second item
    {
        var oHotspot = aAllHotspots[i],
            oHotspotBefore = aAllHotspots[i-1],
            //
            iHotspotButtonTop = oHotspot.jHotspotButton.offset().top,
            iHotspotButtonBeforeTop = oHotspotBefore.jHotspotButton.offset().top,
            iHotspotButtonBeforeOuterHeight = oHotspotBefore.jHotspotButton.outerHeight(),
            //
            iNewTop = null;
        console.log("fitHotspotButtonsPosition", aAllHotspots[i]);
        console.warn("Offset top: ", oHotspot.jHotspotButton.offset.top);
        if(iHotspotButtonTop < (iHotspotButtonBeforeTop + iHotspotButtonBeforeOuterHeight))
        {
            iNewTop = iHotspotButtonBeforeTop + iHotspotButtonBeforeOuterHeight;
            oHotspot.jHotspotButton.css("top",  iNewTop);
            console.warn("Hotspot overlap!");
        }
    }
};

/*
 EVENTS:
 */

fourigin.cms.Hotspot.prototype.setHotspotButtonEvents = function (oHotspotItem) {
    var jHotspotButton = oHotspotItem.jHotspotButton,
        oHotspotDefinitionData = oHotspotItem.oHotspotDefinitionData,
        self = this;
    //
    jHotspotButton.on("click", function () {
        console.info("click on marker", jQuery(this).html());
        self.oEditor.show(jQuery(this), oHotspotDefinitionData);
    });
};
