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
});
