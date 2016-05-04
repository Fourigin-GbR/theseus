/**
 * Created by Karsten Schäfer on 18.04.16.
 */

jQuery(document).ready(function() {
    // LIST ITEM CONTEXT MENU
    var setContextMenu = function(jCurrentRow, oData) {
        var jListItemContextMenu = jQuery("#listItemContextMenu"),
            ooD = oData;
        // Open List Item Context Menu:
        jCurrentRow.find("td").on("click", function (e) {
            var jCurrentTd = jQuery(this),
                oD = ooD,
                oOffset = {};
            //
            e.stopPropagation();
            //
            jCurrentRow.removeClass("active");
            jCurrentTd.closest("tr").addClass("active");
            oOffset = jCurrentTd.offset();
            jListItemContextMenu.find("a[href='#editClassificationType']").on("click", function(e){
                var oeD = oD;
                e.preventDefault();
                e.stopPropagation();
                openEditClassificationTypeOverlay(oeD);
            });
            jListItemContextMenu.find("a[href='#deleteClassificationType']").on("click", function(e){
                var oeD = oD;
                e.preventDefault();
                e.stopPropagation();
                openDeleteClassificationTypeOverlay(oeD);
            });
            jListItemContextMenu.css({
                "top": oOffset['top'] + "px",
                "left": e.pageX + "px"
            }).fadeIn();
        });

        // Close List Item Context Menu on body-click:
        jQuery("body").on("click", function () {
            jCurrentRow.removeClass("active");
            jListItemContextMenu.fadeOut();
        });
    };
    // OVERLAY WINDOW
    var jOverlayWindow = jQuery(".overlayWindow");

    // Close on close-button-click:
    jOverlayWindow.find(".close.button").on("click", function () {
        jQuery(this).closest(".overlayWindow").fadeOut();
    });

    // SPECIFIC OVERLAY WINDOWS
    var openEditClassificationTypeOverlay = function(oData) {
        var jOverlayWindowEditClassification = jQuery(".overlayWindow#editClassificationType");
        //
        // Fill:
        jOverlayWindowEditClassification.find("input[name=id]").val(oData["id"]);
        jOverlayWindowEditClassification.find("textarea[name=description]").val(oData["description"]);
        // Show overlay:
        jOverlayWindowEditClassification.fadeIn();
    };

    var openDeleteClassificationTypeOverlay = function(oData) {
        var jOverlayWindowDeleteClassification = jQuery(".overlayWindow#deleteClassificationType");
        //
        // Fill:
        jOverlayWindowDeleteClassification.find("input[name=id]").val(oData["id"]);
        // Show overlay:
        jOverlayWindowDeleteClassification.fadeIn();
    };

    // CATCH <A> LINKS
    jQuery("a[href^='#']").on("click", function (e) {
        var jThis = jQuery(this),
            sInternalLinkId = jThis.attr("href");
        //
        //
        e.preventDefault();
        if ("#addClassificationType" === sInternalLinkId) {
            var jOverlayWindowAddClassificationType = jQuery(".overlayWindow#addClassificationType");
            //
            // Show overlay:
            jOverlayWindowAddClassificationType.fadeIn();
        }
        //else if("#")
    });

    // LIST SEARCH / FILTER
    jQuery("input#listSearch").on("keyup", function () {
        var sNewFilter = jQuery(this).val();
        //
        jQuery("table#listView tbody tr").each(function () {
            var jThis = jQuery(this);
            //                           string.indexOf(substring) > -1
            if (jThis.find("td").text().indexOf(sNewFilter) > -1) {
                jThis.show();
            }
            else {
                jThis.hide();
            }
        });
    });

    // LOAD TYPES
    var loadClassificationTypeIds = function() {
        jQuery.ajax({
                method: "get",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                url: "http://fourigin.com/spring/prototype/classificationType/_all "
            })
            .done(function (aIds) {
                loadTypesByIds(aIds);
            })
            .fail(function (msg) {
                console.error("Can not load classification type ids. Reason: ", msg);
            });
    };

    loadClassificationTypeIds();

    // LOAD CLASSIFICATIONS AND SHOW TABLE
    //
    var loadTypesByIds = function (aIds) {
        var sParams = "";
        for(var i= 0, il=aIds.length; i<il; i++) {
            if(0 !== sParams.length) {
                sParams = sParams + "&";
            }
            sParams = sParams + "code=" + aIds[i];
        }
        //
        jQuery.ajax({
                method: "get",
                data: sParams, //aIds, //{code: aIds},
                headers: {
                    'Accept': 'application/json'
                    //'Content-Type': 'application/json'
                },
                contentType: "application/json; charset=utf-8",
                //dataType: "json",
                processData: false,
                url: "http://fourigin.com/spring/prototype/classificationType/"
            })
            .done(function (aTypes) {
                var jListTable = jQuery("#listView"),
                    jRowPrototype = jListTable.find("tr.prototype"),
                    jTarget = jRowPrototype.closest("tbody");
                //
                for (var i = 0, il = aTypes.length; i < il; i++) {
                    var jCurrentRow = jRowPrototype.clone().removeClass("prototype"),
                        oData = {id: aTypes[i]["id"], description: aTypes[i]["description"]};
                    //
                    jCurrentRow.find("td[data-field=code]").attr("data-value", aTypes[i]["id"]).html(aTypes[i]["id"]);
                    jCurrentRow.find("td[data-field=description]").attr("data-value", aTypes[i]["description"]).html(aTypes[i]["description"]);
                    setContextMenu(jCurrentRow, oData);
                    jTarget.append(jCurrentRow);
                }
            })
            .fail(function (msg) {
                alert("Something wrong: " + msg);
                console.error("Can not load classification. Reason: ", msg);
            });
    };
    //
    //
    //
    var reloadListAndTable = function() {
        jQuery("#listView").find("tbody tr").not(".prototype").remove();
        loadClassificationTypeIds();
    };


    // FORMS
    jQuery.fn.helperSerializeObject = function()
    {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

    jQuery("form").on("submit", function(e) {
        var jForm = jQuery(this),
            oForm = jForm.helperSerializeObject();
        console.log("oForm:", oForm);
        //
        //
        e.preventDefault();
        //
        jQuery.ajax({
                method: jForm.attr("method"),
                headers: {
                    //'Accept': "*", //'application/json',
                    //'Content-Type': 'application/json'
                },
                contentType: "application/json; charset=utf-8",
                //dataType: "json",
                url: "http://fourigin.com/spring/prototype" + jForm.attr("action"),
                data: JSON.stringify({
                    "id": oForm["id"],
                    "typeCode": oForm["typeCode"],
                    "description": oForm["description"]
                })
            })
            .done(function( msg ) {
                // Clean all fields and close:
                jForm.find("input, textarea").val("");
                jForm.closest(".overlayWindow").fadeOut();
                reloadListAndTable();
            })
            .fail(function (msg) {
                console.error("Can not add. Reason: ", msg);
            });
    });
});
