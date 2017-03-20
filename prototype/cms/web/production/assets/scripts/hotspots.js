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
    //var aHotspotItems;
    //
    this.getAllHotspots();
    this.setSideBar();

/*  Old version with buttons:  //
    aHotspotItems = this.aHotspots;
    //
    for (var i = 0, l = aHotspotItems.length; i < l; i++) {
        this.setHotspotButton(aHotspotItems[i]);
    }
    this.fitHotspotButtonsPosition();
    this.setEvents();
    this.updateHotspotsAmountIndicator();*/
};

fourigin.cms.Hotspot.prototype.setSideBar = function() {
    var aHotspotItems = this.aHotspots,
        i, il,
        self = this,
        jSidePanel = jQuery(".desktop .sidePanel"),
        jHotspotItemPrototype = jSidePanel.find("li.prototype.hotspotItem"),
        jHotspotItemTarget = jSidePanel.find("ul");
    //
    jHotspotItemTarget.find("li:not(.prototype)").remove();
    for(i=0, il=aHotspotItems.length; i<il; i++) {
        var jNewHotspotItem = jHotspotItemPrototype.clone().removeClass(".prototype");
        jNewHotspotItem.find(".label").text(aHotspotItems[i]["oHotspotDefinitionData"]["contentEditorName"]);
        jHotspotItemTarget.append(jNewHotspotItem);
        self.setSidebarHotspotEvents(jNewHotspotItem, aHotspotItems[i]);
    }
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

fourigin.cms.Hotspot.prototype.setEvents = function() {
    var aAllHotspots = this.aHotspots;
    //
    for(var i = 0, il = aAllHotspots.length; i<il; i++) {
        var oHotspot = aAllHotspots[i];
        //
        console.warn("Hotspot-Object", oHotspot);
        oHotspot["jMarker"].on("mouseenter", (function() {
            oHotspot["jHotspotContentElement"].css("outline", "1px solid red");
        })(oHotspot))
    }
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
        console.info("click on marker", jQuery(this).html(), oHotspotItem);
        //self.oEditor.show(jQuery(this), oHotspotDefinitionData);
        showOverlay(oHotspotItem);
    });
};

fourigin.cms.Hotspot.prototype.setSidebarHotspotEvents = function (jSidebarHotspotItem, oHotspotItem) {
    jSidebarHotspotItem.on("click", function () {
        showOverlay(oHotspotItem);
    });
};

jQuery(document).ready(function() {
    jQuery(".overlay .controls .close, a.button[data-action=cancel]").on("click", function() {
        closeOverlay();
    });
});

var closeOverlay = function() {
    var jLayer = jQuery(".overlayBackgroundLayer"),
        jOverlay = jQuery(".overlay");
    jOverlay.fadeOut();
    jLayer.hide();
};
var showOverlay = function(oHotspotItem) {
    var jLayer = jQuery(".overlayBackgroundLayer"),
        jOverlay = jQuery(".overlay");
    jOverlay.fadeIn();
    jLayer.show();
    // Hole daten:
    sUrl = oHotspotItem["oHotspotDefinitionData"]["contentPath"]; //"http://fourigin.de/cms/editors/retrieveP?base=DE&sitePath=/dir-1/page-1&contentPath=/";

    jQuery.ajax({
        url: sUrl,
        dataType: "json"
    }).done(function(oData) {
        fillOverlay(oData);
    });
};

var writeContentObjectToFormItem = function(oContentObject, sParentNamePath) {
    var jOverlay = jQuery(".overlay"),
        jTarget = jOverlay.find("form"),
        jMasterText = jOverlay.find("[data-prototype=text]"),
        jMasterHtml = jOverlay.find("[data-prototype=html]"),
        jCurrentDataTypeElement;
    //
    //
    sParentNamePath = sParentNamePath || "";
    console.log("lalala", oContentObject);
    switch (oContentObject["type"]) {
        case "text":
            jCurrentDataTypeElement = jMasterText.clone().removeAttr("data-prototype").removeClass("prototype");
            jCurrentDataTypeElement.find("input").val(oContentObject["content"]);
            jCurrentDataTypeElement.find("input").attr("name", sParentNamePath + "/" + oContentObject["name"]);
            jTarget.append(jCurrentDataTypeElement);
            jCurrentDataTypeElement.find("label span").text(oContentObject["contentPath"]);
            jCurrentDataTypeElement.find("input").data(oContentObject, "contentObject");
            break;
        case "group":
            for(var i=0, il=oContentObject["elements"].length; i<il; i++) {
                writeContentObjectToFormItem(oContentObject["elements"][i], sParentNamePath + "/" + oContentObject["name"]);
            }
            break;
    }
    //
};

var updateCurrentContentElementByFormData = function (oCurrentContentElement, oContentElementSnippet, sParentNamedPath, jForm) {
    switch (oContentElementSnippet["type"]) {
        case "group":
            for(var i=0, il=oContentElementSnippet["elements"].length; i<il; i++) {
                updateCurrentContentElementByFormData(oCurrentContentElement, oContentElementSnippet["elements"][i], sParentNamedPath + "/" + oContentElementSnippet["name"], jForm);
            }
            break;
        default:
            // find input with that name and update:
            oContentElementSnippet["content"] = jForm.find("input[name='" + sParentNamedPath + "/" + oContentElementSnippet["name"] + "']").val();
    }
};

var fillOverlay = function(oData) {
    var jOverlay = jQuery(".overlay"),
        jForm = jOverlay.find("form");
    //
    jForm.find(".contentElement:not(.prototype)").remove();
    jOverlay.find("a.button[data-action=save]").unbind();
    //
    writeContentObjectToFormItem(oData["currentContentElement"]);

    jOverlay.find("a.button[data-action=save]").on("click", function() {
        var sUrl = "http://fourigin.de/cms/editors/save",
            oUpdatedData = {
                "base": oData["base"],
                "siteStructurePath": oData["siteStructurePath"],
                "contentPath": oData["contentPath"],
                "originalChecksum": oData["currentChecksum"],
                "modifiedContentElement": oData["currentContentElement"]
            };

        // collect all data:
        updateCurrentContentElementByFormData(oData["currentContentElement"], oData["currentContentElement"], "", jForm);


        console.log("__________________________Aktualisiert: ", oData);

        jQuery.ajax({
            url: sUrl,
            data: JSON.stringify(oUpdatedData),
            method: "post",
            contentType: "application/json; charset=utf-8",
            dataType: "json"
        }).done(function(oData) {
            jQuery("iframe")[0].contentWindow.location.reload(true);
            closeOverlay();
        }).fail(function(){alert("Das hat nicht geklappt. Hier ist wohl noch ein Bug....");});
    });
};
