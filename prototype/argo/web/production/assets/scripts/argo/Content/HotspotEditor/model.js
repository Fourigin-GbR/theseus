if ("undefined" === typeof com) {
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.content = com.fourigin.argo.content || {};
com.fourigin.argo.content.hotspotEditor = com.fourigin.argo.content.hotspotEditor || {};
com.fourigin.argo.content.hotspotEditor.Model = com.fourigin.argo.content.hotspotEditor.Model || (function () {
    var Model = function () {
        this.site = {
            "base": null,
            "path": null
        };
        this.content = { // TODO: Rename. Is a setting information.
            "path": null
        };
        this.contentElement = null;
        this.contentPrototype = null;

        this.loadContent();
        this.loadContentPrototype();
        //
        return this;
    };
    //
    Model.prototype.setSitePath = function (sSitePath) {
        this.site.path = sSitePath;
    };
    Model.prototype.getSitePath = function () {
        return this.site.path;
    };

    Model.prototype.setSiteBase = function (sSiteBase) {
        this.site.base = sSiteBase;
    };
    Model.prototype.getSiteBase = function () {
        return this.site.base;
    };

    Model.prototype.setContent = function (oContent) {
        this.contentElement = oContent;
    };
    Model.prototype.getContent = function () {
        return this.contentElement;
    };

    Model.prototype.setContentPath = function (sContentPath) {
        this.content.path = sContentPath;
    };
    Model.prototype.getContentPath = function () {
        return this.content.path;
    };


    Model.prototype.setContentPrototype = function (oPrototype) {
        this.contentPrototype = oPrototype;
    };
    Model.prototype.getContentPrototype = function () {
        return this.contentPrototype;
    };


    Model.prototype.resolveContentElement = function () {
        /**
         * Resolve the hotspot-path and retrieve the current content-element.
         */

    };

    Model.prototype.getContentElementByPath = function (sPath, oContent) {
        if (!oContent) {
            console.error("Missing parameter 'content'");
        }
        // TODO: Supports only one-level search
        var sCleanPath = sPath.replace(/^\/|\/$/g, ''),
            aPath = sCleanPath.split("/"),
            self = this;
        //
        for (var i = 0, il = oContent.length; i < il; i++) {
            if (oContent[i].name === aPath[0]) {
                return oContent[i];
            }
        }

        return false
    };

    Model.prototype.retrieveContentType = function () {
        /**
         * Use the content prototype to get the content type.
         */
        //var currentContentElement = this.getContentElementByPath()
    };


    Model.prototype.loadContent = function (callback) {
        if(!this.getSiteBase()) {
            console.error("'site base' empty! Can not load content.");
            return;
        }
        if(!this.getSitePath()) {
            console.error("'site path' empty! Can not load content.");
            return;
        }
        var self = this,
            jRequest = jQuery.ajax({
                "url": "/cms/compile/prepare-content?base=" + this.getSiteBase() + "&path=" + this.getSitePath() + "&verbose=true"
            });
        jRequest.done(function (data) {
                console.log("loaded content.", data);
            /**
             * Get only the content for the current hotspot.
             */
            var jCurrentContentElement = self.getContentElementByPath(self.getContentPath(), data.content);
            self.setContent(jCurrentContentElement);
            if (callback) {
                callback();
            }
            }
        );
    };

    Model.prototype.loadContentPrototype = function (callback) {
        if(!this.getSiteBase()) {
            console.error("'site base' empty! Can not load prototype.");
            return;
        }
        if(!this.getSitePath()) {
            console.error("'site path' empty! Can not load prototype.");
            return;
        }
        var self = this,
            jRequest = jQuery.ajax({
                "url": "/cms/editors/prototype?base=" + this.getSiteBase() + "&path=" + this.getSitePath()
            });
        jRequest.done(function (data) {
                console.log("loaded content-prototype.", data);
                /**
                 * Get only the prototype for the current hotspot.
                 */
                var jCurrentContentPrototypeElement = self.getContentElementByPath(self.getContentPath(), data.contentPrototype.content);
                self.setContentPrototype(jCurrentContentPrototypeElement);
                if (callback) {
                    callback();
                }
            }
        );
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
                case "group":
                    oGeneratedArgoContent = this.generateArgoContentElementJsonTypeGroup(jMarkupElement);
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
            jElementsWrapper = jMarkupElement.find(" > fieldset > ul > li"),
            self = this;
        //
        if (sName) {
            oList.name = sName;
        }
        jElementsWrapper.each(function(){
            oList.elements.push(self.generateArgoContentElementJsonByMarkup(jQuery(this).find(" > fieldset")));
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
    Model.prototype.generateArgoContentElementJsonTypeGroup = function (jMarkupElement) {
        var oGroup = {
                "type": "group",
                "name": null,
                "elements": []
            },
            sName = jMarkupElement.find("input[name='name']").val(),
            jElements = jMarkupElement.find(" > fieldset > fieldset"),
            self = this;
        //
        if (sName) {
            oGroup.name = sName;
        }
        jElements.each(function(){
            oGroup.elements.push(self.generateArgoContentElementJsonByMarkup(jQuery(this)));
        });
        //
        return oGroup;
    };

    Model.prototype.buildArgoContentStoreDataElement = function(oContentElementData) {
        return {
            "base": this.getSiteBase(),
            "path": this.getSitePath(),
            "contentPath": this.getContentPath(),
            "originalChecksum": "",
            "modifiedContentElement": oContentElementData
        };
    };

    Model.prototype.save = function (jFieldset, callbackSuccess, callbackError) {
        var oContentElement = this.generateArgoContentElementJsonByMarkup(jFieldset),
            oArgoContentStorageElement = this.buildArgoContentStoreDataElement(oContentElement),
            request = $.ajax({
            url: "http://argo.greekestate.fourigin.com/cms/editors/save",
            method: "POST",
            data: JSON.stringify(oArgoContentStorageElement),
            contentType: "application/json"
        });

        request.done(function (msg) {
            if(callbackSuccess) {
                callbackSuccess(msg);
            }
        });

        request.fail(function (jqXHR, textStatus) {
            if(callbackError) {
                callbackError(msg);
            }
        });
    };
    //
    return Model;
}());