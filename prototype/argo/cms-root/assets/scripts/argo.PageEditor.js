if ("undefined" === typeof com)
{
    var com;
}
com = com || {};
com.fourigin = com.fourigin || {};
com.fourigin.argo = com.fourigin.argo || {};
com.fourigin.argo.PageEditor = com.fourigin.argo.PageEditor || (function ()
{
    var PageEditor = function(sBase, sPath) {
        this.pageContent = null;
        this.sBase = sBase;
        this.sPath = sPath;

        return this;
    };
    //
    PageEditor.prototype.loadContent = function () {
        var jRequest = jQuery.ajax({
            "url": "/cms/compile/prepare-content?base=" + this.sBase + "&path=" + this.sPath + "&verbose=true"
        });
        jRequest.done(function (data) {
            console.log("loaded content.");
           this.pageContent = data;
        }
        );
    };

    PageEditor.prototype.writeTextEditor = function (oTextItem, jTarget) {
        var jPrototype = jQuery("#argoEditorPrototypes fieldset[data-type=text]");
        //
        // fill with data
        jPrototype.find("input[name=content]").val(oTextItem.content);
        // append to editor
        jTarget.append(jPrototype);
    };
    //
    return PageEditor;
}());