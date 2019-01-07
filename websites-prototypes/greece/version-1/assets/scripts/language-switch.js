jQuery("document").ready(function(){
    var jSelector = jQuery("select#siteLanguageSwitch");

    jSelector.on("change", function() {
        var selectedValue = jSelector.val(),
            newUrl = null;
        switch (selectedValue) {
            case "de":
                newUrl = "http://www.de.bestgreekestate.com/";
                break;
            case "ru":
                newUrl = "http://www.ru.bestgreekestate.com/";
                break;
            case "en":
                newUrl = "http://www.en.bestgreekestate.com/";
                break;
        }
        if(newUrl){
            window.location = newUrl;
        }
    });
});