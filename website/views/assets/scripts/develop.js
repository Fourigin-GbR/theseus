/**
 * Created by Karsten Schäfer on 18.04.16.
 */

jQuery(document).ready(function(){
    // LIST ITEM CONTEXT MENU
    var jListItemContextMenu = jQuery("#listItemContextMenu");
    // Open List Item Context Menu:
    jQuery(".listWrapper table tbody tr td").on("click", function(e){
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
    } );

    // Close List Item Context Menu on body-click:
    jQuery("body").on("click", function() {
        jQuery(".listWrapper table tbody tr").removeClass("active");
        jListItemContextMenu.fadeOut();
    });

    // OVERLAY WINDOW
    var jOverlayWindow = jQuery(".overlayWindow");

    // Close on close-button-click:
    jOverlayWindow.find(".close.button").on("click", function() {
        jQuery(this).closest(".overlayWindow").fadeOut();
    });

    // CATCH <A> LINKS
    jQuery("a[href^='#']").on("click", function(e){
        var jThis = jQuery(this),
            sInternalLinkId = jThis.attr("href");
        //
        //
        e.preventDefault();
        if("#addClassification" === sInternalLinkId) {
            jQuery(".overlayWindow#addClassification").fadeIn();
        }
    });

    // LIST SEARCH / FILTER
    jQuery("input#listSearch").on("keyup", function() {
        var sNewFilter = jQuery(this).val();
        //
        jQuery("table#listView tbody tr").each(function() {
            var jThis = jQuery(this);
            //                           string.indexOf(substring) > -1
            if(jThis.find("td").text().indexOf(sNewFilter) > -1 ){
                jThis.show();
            }
            else {
                jThis.hide();
            }
        });
    });

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
