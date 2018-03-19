/**
 * Created with IntelliJ IDEA.
 * User: kschaefer1
 * Date: 24.06.14
 * Time: 09:38
 * To change this template use File | Settings | File Templates.
 */
var fourigin = fourigin || {};
fourigin.cms = fourigin.cms || {};
fourigin.cms.hotspot = fourigin.cms.hotspot || {};

fourigin.cms.hotspot.Editor = function (jUnit) {

};

/*
 Properties:
 */

fourigin.cms.hotspot.Editor.prototype.oMarkupMasters = {
    "text": {},
    "html": {},
    "image": {},
    "set": {}
};

fourigin.cms.hotspot.Editor.prototype.oItemMarkupMasters = {
    "text": {},
    "html": {},
    "image": {},
    "set": {}
};

/*
 Methods:
 */

fourigin.cms.hotspot.Editor.prototype.initializeMarkupMasters = function (sType, sMarkup) {
    if (!sType in fourigin.cms.hotspot.Editor.prototype.oMarkupMasters) {
        console.error("Can not initialize master, because type '" + sType + "' is not valid.");
        return false;
    }
    console.warn("Initialize editor-master for type: ", sType);
    fourigin.cms.hotspot.Editor.prototype.oMarkupMasters[sType] = sMarkup;
    //
    return true;
};

fourigin.cms.hotspot.Editor.prototype.initializeItemMarkupMasters = function (sType, sMarkup) {
    if (!sType in fourigin.cms.hotspot.Editor.prototype.oItemMarkupMasters) {
        console.error("Can not initialize master, because type '" + sType + "' is not valid.");
        return false;
    }
    console.warn("Initialize item-master for type: ", sType);
    fourigin.cms.hotspot.Editor.prototype.oItemMarkupMasters[sType] = sMarkup;
    //
    return true;
};

// INFILLING:

fourigin.cms.hotspot.Editor.prototype.infillingEditor = function (oHotspotData) {
    var jGeneratedEditorContentHtml = jQuery(),
        oDataLayout = oHotspotData.contentEditorDataLayout,
        aDataElements = oHotspotData.contentEditorData,
        sType = null,
        iMaxAmount = oDataLayout.maxAmount,
        jHtml = null;
    // Iterate over the array of Data Layout Elements and for each Data Element take a master-markup-unit.

    // Get the Data Layout:
    if(!oDataLayout)
    {
        console.error("Hotspot: Data Layout property is missing! Can not instantiate editor.", oHotspotData);
        return false;
    }
    //
    sType = oDataLayout.type;
    // Get Markup master:
    if (!sType in this.oMarkupMasters) {
        console.error("Can not infilling editor, because for type '" + +"' is no master found.");
        return false;
    }
    //
    console.log("Infill editor with this array of Data Elements: ", aDataElements);
    // TODO: If no Data exists, take Data-Layout !!!!!

    // Do as much as the parameter iMaxAmount specifies or, if not defined, as the maximum of the array reached:
    if(0 < aDataElements.length)
    {
        for(var i= 0, il = (aDataElements.length || iMaxAmount); i<il; i++)
        {
            var oDataElement = aDataElements[i];
            console.log("Infill editor with this Data Element: ", oDataElement);
            jHtml = jQuery(this.oMarkupMasters[sType]);
            switch(sType)
            {
                case "text":
                    this.infillingUnitText(jHtml, oDataElement.text);
                    break;
                case "html":
                    this.infillingUnitHtml(jHtml, oDataElement.html);
                    break;
                case "image":
                    this.infillingUnitImage(jHtml, oDataElement.url);
                    this.setImageUploadEvents(jHtml, "uploadImage");
                    break;
                case "set":
                    this.infillingUnitSet(jHtml, oDataElement, oDataLayout);
                    break;
                default:
                    console.error("Something is wrong.");
                    break;
            }
            // Infill global master-data for every editor-form:
            jHtml.find("input[name='id']").val(oDataElement.id);
            jHtml.find("input[name='type']").val(oDataElement.type);
            jHtml.find("input[name='elementName']").val(oDataElement.name);
            jHtml.find("input[name='action']").val(oDataElement.action);
            //
            // Add final html:
            jGeneratedEditorContentHtml = jGeneratedEditorContentHtml.add(jHtml);
        }
    }
    else
    {
        console.log("Infill editor with this Data Layout: ", oDataLayout);
        jHtml = jQuery(this.oMarkupMasters[sType]);
        switch(sType)
        {
            case "text":
                this.infillingUnitText(jHtml, "");
                break;
            case "html":
                this.infillingUnitHtml(jHtml, "");
                break;
            case "image":
                this.infillingUnitImage(jHtml, "");
                this.setImageUploadEvents(jHtml, "uploadImage");
                break;
            case "set":
                this.infillingUnitSet(jHtml, null, oDataLayout);
                break;
            default:
                console.error("Something is wrong.");
                break;
        }
        // Infill global master-data for every editor-form:
        jHtml.find("input[name='id']").val(oDataLayout.id);
        jHtml.find("input[name='type']").val(oDataLayout.type);
        jHtml.find("input[name='elementName']").val(oDataLayout.name);
        jHtml.find("input[name='action']").val(oDataLayout.createDataElementAction); // TODO: Variable :)
        //
        // Add final html:
        jGeneratedEditorContentHtml = jGeneratedEditorContentHtml.add(jHtml);
    }
    //
    if (!jGeneratedEditorContentHtml) {
        console.error("jGeneratedEditorContentHtml is empty!", jGeneratedEditorContentHtml);
    }
    console.log("Return this jGeneratedEditorContentHtml: ", jGeneratedEditorContentHtml);
    return jGeneratedEditorContentHtml;
};

fourigin.cms.hotspot.Editor.prototype.infillingSetDataElementText = function (jElement, iCounter, sElementName, oDataElement) {
    console.warn("INFILLING SET ITEM TEXT. ", jElement);
    // Search all form-elements of the data-element-text and replace the names with unique-save-ones:
    var sItemNamePrefix = "item_" + iCounter + ":",
        sTextInputName = sItemNamePrefix + "text",
        sIdInputName = sItemNamePrefix + "id",
        sElementNameInputName = sItemNamePrefix + "elementName",
        sElementTypeInputName = sItemNamePrefix + "type",
        //
        sTextValue = null,
        sId = null
        ;
    //
    if(oDataElement)
    {
        sTextValue = oDataElement.text;
        sId = oDataElement.id;
    }
    //
    jElement.find("input[name='text']").attr("name", sTextInputName).val(sTextValue);
    jElement.find("input[name='id']").attr("name", sIdInputName).val(sId);
    jElement.find("input[name='elementName']").attr("name", sElementNameInputName).val(sElementName);
    jElement.find("input[name='type']").attr("name", sElementTypeInputName);
    jElement.find("input[name='action']").remove();
};

fourigin.cms.hotspot.Editor.prototype.infillingSetDataElementHtml = function (jElement, iCounter, sElementName, oDataElement) {
    console.warn("INFILLING SET ITEM HTML. ", jElement);
    // Search all form-elements of the data-element-text and replace the names with unique-save-ones:
    var sItemNamePrefix = "item_" + iCounter + ":",
        sHtmlInputName = sItemNamePrefix + "html",
        sIdInputName = sItemNamePrefix + "id",
        sElementNameInputName = sItemNamePrefix + "elementName",
        sElementTypeInputName = sItemNamePrefix + "type",
        //
        sHtmlValue = null,
        sId = null
        ;
    //
    if(oDataElement)
    {
        sHtmlValue = oDataElement.html;
        sId = oDataElement.id;
    }
    //
    jElement.find("input[name='html']").attr("name", sHtmlInputName).val(sHtmlValue);
    jElement.find("input[name='id']").attr("name", sIdInputName).val(sId);
    jElement.find("input[name='elementName']").attr("name", sElementNameInputName).val(sElementName);
    jElement.find("input[name='type']").attr("name", sElementTypeInputName);
    jElement.find("input[name='action']").remove();
};

fourigin.cms.hotspot.Editor.prototype.infillingSetDataElementImage = function (jElement, iCounter, sElementName, oDataElement) {
    console.warn("INFILLING SET ITEM IMAGE. ", jElement);
    // Search all form-elements of the data-element-text and replace the names with unique-save-ones:
    var sItemNamePrefix = "item_" + iCounter + ":",
        sMediaManagerIdInputName = sItemNamePrefix + "mediaManagerId",
        sIdInputName = sItemNamePrefix + "id",
        sElementNameInputName = sItemNamePrefix + "elementName",
        sElementUploadImageInputName = sItemNamePrefix + "uploadImage",
        sElementTypeInputName = sItemNamePrefix + "type",
        //
        sMediaManagerId = null,
        sId = null,
        sUrl = null
        ;
    //
    if(oDataElement)
    {
        sMediaManagerId = oDataElement.mediaManagerId;
        sId = oDataElement.id;
        sUrl = oDataElement.url;
    }
    //
    jElement.find("input[name='mediaManagerId']").attr("name", sMediaManagerIdInputName).val(sMediaManagerId);
    jElement.find("input[name='id']").attr("name", sIdInputName).val(sId);
    jElement.find("input[name='elementName']").attr("name", sElementNameInputName).val(sElementName);
    jElement.find("input[name='uploadImage']").attr("name", sElementUploadImageInputName);
    jElement.find("input[name='type']").attr("name", sElementTypeInputName);
    jElement.find("input[name='action']").remove();
    jElement.find("img.imagePreview").attr("src", sUrl);
    //
    this.setImageUploadEvents(jElement, sElementUploadImageInputName);
};

fourigin.cms.hotspot.Editor.prototype.infillingUnitText = function (jUnit, sValue) {
    return jUnit.find("input.text").val(sValue);
};
fourigin.cms.hotspot.Editor.prototype.infillingUnitHtml = function (jUnit, sValue) {
    return jUnit.find("textarea.html").val(sValue);
};
fourigin.cms.hotspot.Editor.prototype.infillingUnitImage = function (jUnit, sValue) {
    return jUnit.find("img.imagePreview").attr("src", sValue);
};
fourigin.cms.hotspot.Editor.prototype.infillingUnitSet = function (jUnit, oDataElementSet, oDataLayout) {
    // For every entry in setItems of the Data Layout:
    if(!jUnit)
    {
        console.error("Parameter 'jUnit' is missing! Can not infill editor.");
        return false;
    }
    if(!oDataElementSet)
    {
        console.info("Parameter 'oDataElementSet' is missing! Infill editor by Data Layout.");
    }
    if(!oDataLayout)
    {
        console.error("Parameter 'oDataLayout' is missing! Can not infill editor.");
        return false;
    }
    //
    console.log("Infilling Unit Set: Data Element: ", oDataElementSet, " DataLayout: ", oDataLayout);
    var iCounter = 0;
    for (var sKey in oDataLayout.setItems) {
        var oSetItem = oDataLayout.setItems[sKey],
            oDataElement = null,
            sFormMaster = this.oItemMarkupMasters[oSetItem.type],
            jSetItemWrapper = jQuery("<div/>");
        //
        iCounter++;
        //
        jSetItemWrapper.append(sFormMaster);
        jUnit.find(".body").find("div.setItems").append(jSetItemWrapper);
        // TODO: DATA LAYOUT MODE !!
        // DATA ELEMENT mode:
        if(oDataElementSet)
        {
            // Get the Data Element:
            for(var j= 0, jl = oDataElementSet.items.length; j<jl; j++)
            {
                if(oDataElementSet.items[j].name === sKey)
                {
                    oDataElement = oDataElementSet.items[j];
                }
            }
        }
        else {
            oDataElement = null;
        }

        // Adapt form-names, as in a set it has to be more specific:
        if ("text" === oSetItem.type) {

            this.infillingSetDataElementText(jSetItemWrapper, iCounter, sKey, oDataElement);
        }
        else if ("image" === oSetItem.type) {
            this.infillingSetDataElementImage(jSetItemWrapper, iCounter, sKey, oDataElement);
        }
    }

};
// SHOWING:

fourigin.cms.hotspot.Editor.prototype.show = function (jClickElement, oContentElementData) {
    console.log("Show editor: ", jClickElement, oContentElementData);
    var sContentEditorElementId = oContentElementData.contentEditorElementId;
    var jEditor = this.infillingEditor(oContentElementData);

    var oWindow = new fourigin.cms.editor.Window();
    // Insert editor in master-window-content, than give that to the window-handler.
    // TODO: Move Window-Markup to.... ??

    oWindow.insertPanel(jEditor, jQuery("#editors")); // TODO: Remove fix markup-id-selector
    oWindow.show();

    new fourigin.cms.menu.submenu.Unit(jEditor);
};

// EVENTS:

fourigin.cms.hotspot.Editor.prototype.setImageUploadEvents = function (jEditor, sFileName) {
// Add upload-event:
    var jUploadInput = jEditor.find("input[name='"+sFileName+"']"),
        jResultWrapper = jEditor.find(".previewImageContainer"),
        jButtonImageUpload = jResultWrapper.find(".button.uploadImage"),
        jImagePreviewWrapper = jResultWrapper.find(".imageUploadPreview");
    //
    if(!jButtonImageUpload)
    {
        console.error("Can not find button-image-upload! Editor is not working proper.");
    }
    jButtonImageUpload.on("click", function()
    {
        jUploadInput.click();
    });
    //
    //
    console.info("Set setImageUploadEvents events here: ", jEditor);
    jUploadInput.on("change", function (event) {
        console.info("change");
        var files = event.target.files;
        //
        event.preventDefault();
        event.stopPropagation();
        //
        for (var i = 0; file = files[i]; i++) {
            var sFileSource;
            var imageType = /image.*/;
            var previewImage = document.createElement("img");
            /* TODO: Set as parameter */
            // if the file is not an image, continue
            if (!file.type.match(imageType)) {
                continue;
            }

            reader = new FileReader();
            reader.onload = (function (aImg) {
                return function (e) {
                    aImg.src = e.target.result;
                };
            })(previewImage);

            jImagePreviewWrapper.html(previewImage);
            jResultWrapper.find(".value.imageName").html(file.name);
            jResultWrapper.find(".value.imageSize").html(file.size);
            jResultWrapper.find(".value.imageType").html(file.type);

            reader.readAsDataURL(file);
        }
        //
        console.log("Add image preview here: ", jEditor.find(".previewImageContainer"), jEditor);
        jEditor.find(".previewImageContainer").show();
    });
};

fourigin.cms.hotspot.Editor.prototype.eventListener = function () {
    jQuery("body").eventListener("/fourigin: Marker clicked", function () {
        console.warn("GOT IT");
    })
};