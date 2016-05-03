/**
 * Created by Karsten Schäfer on 18.04.16.
 */

jQuery(document).ready(function() {
    // LIST ITEM CONTEXT MENU
    var jListItemContextMenu = jQuery("#listItemContextMenu");
    // Open List Item Context Menu:
    jQuery(".listWrapper table tbody tr td").on("click", function (e) {
        var jCurrentTd = jQuery(this),
            oOffset = {};
        //
        e.stopPropagation();
        //
        jQuery(".listWrapper table tbody tr").removeClass("active");
        jCurrentTd.closest("tr").addClass("active");
        oOffset = jCurrentTd.offset();
        jListItemContextMenu.css({
            "top": oOffset['top'] + "px",
            "left": e.pageX + "px"
        }).fadeIn();
    });

    // Close List Item Context Menu on body-click:
    jQuery("body").on("click", function () {
        jQuery(".listWrapper table tbody tr").removeClass("active");
        jListItemContextMenu.fadeOut();
    });

    // OVERLAY WINDOW
    var jOverlayWindow = jQuery(".overlayWindow");

    // Close on close-button-click:
    jOverlayWindow.find(".close.button").on("click", function () {
        jQuery(this).closest(".overlayWindow").fadeOut();
    });

    // CATCH <A> LINKS
    jQuery("a[href^='#']").on("click", function (e) {
        var jThis = jQuery(this),
            sInternalLinkId = jThis.attr("href");
        //
        //
        e.preventDefault();
        if ("#addClassification" === sInternalLinkId) {
            jQuery(".overlayWindow#addClassification").fadeIn();
        }
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

    // LOAD CLASSIFICATIONS AND SHOW TABLE
    var loadClassificationIds = function() {
        jQuery.ajax({
                method: "get",
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                url: "http://fourigin.com/spring/prototype/classification/_all"
            })
            .done(function (aIds) {
                loadClassificationsByIds(aIds);
            })
            .fail(function (msg) {
                alert("Something wrong: " + msg);
                console.error("Can not load classification ids. Reason: ", msg);
            });
    };
    //
    var loadClassificationsByIds = function (aIds) {
        var sParams = "";
        for(var i= 0, il=aIds.length; i<il; i++) {
            if(0 !== sParams.length) {
                sParams = sParams + "&";
            }
            sParams = sParams + "code=" + aIds[i];
        }
        console.log(sParams);
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
                url: "http://fourigin.com/spring/prototype/classification/"
            })
            .done(function (aClassifications) {
                var jListTable = jQuery("#listView"),
                    jRowPrototype = jListTable.find("tr.prototype"),
                    jTarget = jRowPrototype.closest("tbody");
                //
                for (var i = 0, il = aClassifications.length; i < il; i++) {
                    var jCurrentRow = jRowPrototype.clone().removeClass("prototype");
                    //
                    jCurrentRow.find("td[data-value=code]").html(aClassifications[i]["id"]);
                    jCurrentRow.find("td[data-value=type]").html(aClassifications[i]["typeCode"]);
                    jCurrentRow.find("td[data-value=description]").html(aClassifications[i]["description"]);
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
    loadClassificationIds();

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
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                url: "http://fourigin.com/spring/prototype" + jForm.attr("action"),
                data: JSON.stringify({
                    "id": oForm["id"],
                    "typeCode": oForm["type"],
                    "description": oForm["description"]
                })
            })
            .done(function( msg ) {
                alert( "Data Saved: " + msg );
            })
            .fail(function (msg) {
                alert("Something wrong: " + msg);
                console.error("Can not add. Reason: ", msg);
            });
    });
});
