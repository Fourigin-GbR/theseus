if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.pageEditor = com.fourigin.argo.pageEditor || {};
com.fourigin.argo.pageEditor.Model = com.fourigin.argo.pageEditor.Model || (function () {
    var Model = function () {
        this.oContent = null;
        this.oHotspots = null;
        this.sPath = null;
        this.sBase = null;
        //
        return this;
    };
    //

    Model.prototype.setBase = function (oBase) {
        this.oBase = oBase;
    };
    Model.prototype.getBase = function () {
        return this.oBase;
    };


    Model.prototype.setPath = function (sPath) {
        this.sPath = sPath;
    };
    Model.prototype.getPath = function () {
        return this.sPath;
    };


    Model.prototype.setContent = function (oContent) {
        this.oContent = oContent;
    };
    //
    Model.prototype.getContent = function () {
        return this.oContent;
    };
    Model.prototype.getContentByPath = function (sPath) {
        // TODO: Supports only one-level search
        var sCleanPath = sPath.replace(/^\/|\/$/g, ''),
            aPath = sCleanPath.split("/"),
            self = this;
        //
        for (var i = 0, il = self.oContent.length; i < il; i++) {
            if (self.oContent[i].name === aPath[0]) {
                return self.oContent[i];
            }
        }

        return false
    };


    Model.prototype.setHotspots = function (oHotspots) {
        this.oHotspots = oHotspots;
    };
    Model.prototype.getHotspots = function () {
        return this.oHotspots;
    };


    Model.prototype.generateArgoContentElementJsonByMarkup = function (jMarkupElement, oGeneratedArgoContent) {
        oGeneratedArgoContent = oGeneratedArgoContent || {};
        if (1 === jMarkupElement.length && jMarkupElement.is("fieldset")) {
            var sType = jMarkupElement.attr("data-type");
            switch (sType) {
                case "text":
                    oGeneratedArgoContent = this.generateArgoContentElementJsonTypeText(jMarkupElement);
                    break;
                case "object":
                    oGeneratedArgoContent = this.generateArgoContentElementJsonTypeObject(jMarkupElement);
                    break;
                case "list":
                    oGeneratedArgoContent = this.generateArgoContentElementJsonTypeList(jMarkupElement);
                    break;
                case "listGroup":
                    oGeneratedArgoContent = this.generateArgoContentElementJsonTypeListGroup(jMarkupElement);
                    break;
                default:
                    oGeneratedArgoContent.error = "Type is not supported ('" + sType + "')";
                    break;
            }
        }
        return oGeneratedArgoContent;
    };
    Model.prototype.generateArgoContentElementJsonTypeText = function (jMarkupElement) {
        var oText = {
                "type": "text",
                "name": null,
                "content": null
            },
            sName = jMarkupElement.find("input[name='name']").val(),
            sContent = jMarkupElement.find("textarea[name='content']").val();
        //
        if (sName) {
            oText.name = sName;
        }
        if (sContent) {
            oText.content = sContent;
        }
        //
        return oText;
    };
    Model.prototype.generateArgoContentElementJsonTypeObject = function (jMarkupElement) {
        var oObject = {
                "type": "object",
                "name": null,
                "referenceId": null,
                "source": null,
                "alternateText": null,
                "mimeType": null
            },
            sName = jMarkupElement.find("input[name='name']").val(),
            sReferenceId = jMarkupElement.find("input[name='referenceId']").val(),
            sSource = jMarkupElement.find("input[name='source']").val(),
            sAlternativeText = jMarkupElement.find("input[name='alternativeText']").val(),
            sMimeType = jMarkupElement.find("input[name='mimeType']").val();
        //
        if (sName) {
            oObject.name = sName;
        }
        if (sReferenceId) {
            oObject.referenceId = sReferenceId;
        }
        if (sSource) {
            oObject.source = sSource;
        }
        if (sAlternativeText) {
            oObject.alternateText = sAlternativeText;
        }
        if (sMimeType) {
            oObject.mimeType = sMimeType;
        }
        //
        return oObject;
    };
    Model.prototype.generateArgoContentElementJsonTypeList = function (jMarkupElement) {
        var oList = {
                "type": "list",
                "name": null,
                "elements": []
            },
            sName = jMarkupElement.find("input[name='name']").val(),
            jElements = jMarkupElement.find(" > fieldset > fieldset"),
            self = this;
        //
        if (sName) {
            oList.name = sName;
        }
        jElements.each(function(){
            oList.elements.push(self.generateArgoContentElementJsonByMarkup(jQuery(this)));
        });
        //
        return oList;
    };
    Model.prototype.generateArgoContentElementJsonTypeListGroup = function (jMarkupElement) {
        var oListGroup = {
                "type": "list-group",
                "elements": []
            },
            jElements = jMarkupElement.find(" > fieldset > fieldset"),
            self = this;
        //
        jElements.each(function(){
            oListGroup.elements.push(self.generateArgoContentElementJsonByMarkup(jQuery(this)));
        });
        //
        return oListGroup;
    };
    //
    return Model;
}());