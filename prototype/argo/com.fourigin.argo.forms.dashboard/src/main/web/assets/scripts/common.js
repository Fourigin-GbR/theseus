function formatDate(date, short) {
    var month = date.getMonth() + 1;
    var m = month < 10 ? "0" + month : "" + month;

    var day = date.getDate();
    var d = day < 10 ? "0" + day : day;

    var hh = date.getHours();

    var minutes = date.getMinutes();
    var mm = minutes < 10 ? "0" + minutes : minutes;

    if (!short) {
        var y = date.getFullYear();
        var seconds = date.getSeconds();
        var ss = seconds < 10 ? "0" + seconds : seconds;
        return d + "." + m + "." + y + "&nbsp;" + hh + ":" + mm + ":" + ss;
    }
    else {
        var dateYear = date.getYear();
        if (dateYear === new Date().getYear()) {
            return d + "." + m + "&nbsp;" + hh + ":" + mm;
        }
        else {
            var y = date.getYear() > 100 ? date.getYear() - 100 : date.getYear();
            return d + "." + m + "." + y + "&nbsp;" + hh + ":" + mm;
        }
    }
}

function formatTime(date, short) {
    var hh = date.getHours();

    var minutes = date.getMinutes();
    var mm = minutes < 10 ? "0" + minutes : minutes;

    if (!short) {
        var seconds = date.getSeconds();
        var ss = seconds < 10 ? "0" + seconds : seconds;
        return hh + ":" + mm + ":" + ss;
    }
    else {
        return hh + ":" + mm;
    }
}

function formatDate(date) {
    var month = date.getMonth() + 1;
    var y = date.getFullYear();
    var m = month < 10 ? "0" + month : "" + month;
    var day = date.getDate();
    var d = day < 10 ? "0" + day : day;
    var hh = date.getHours();
    var minutes = date.getMinutes();
    var mm = minutes < 10 ? "0" + minutes : minutes;
    return d + "." + m + "." + y + "&nbsp;" + hh + ":" + mm;
}

function formatDateForGroupedView(date) {
    var hh = date.getHours();
    var minutes = date.getMinutes();
    var mm = minutes < 10 ? "0" + minutes : minutes;
    return hh + ":" + mm;
}

function formatDateForSorting(date) {
    var month = date.getMonth() + 1;
    var y = date.getFullYear();
    var m = month < 10 ? "0" + month : "" + month;
    var day = date.getDate();
    var d = day < 10 ? "0" + day : day;
    var hh = date.getHours();
    var minutes = date.getMinutes();
    var mm = minutes < 10 ? "0" + minutes : minutes;
    return y + "-" + m + "-" + d + " " + hh + ":" + mm;
}

function formatDateForGrouping(date) {
    var month = date.getMonth() + 1;
    var y = date.getFullYear();
    var m = month < 10 ? "0" + month : "" + month;
    var day = date.getDate();
    var d = day < 10 ? "0" + day : day;
    return y + "-" + m + "-" + d;
}

function formatDateForGroupingView(date) {
    var month = date.getMonth() + 1;
    var y = date.getFullYear();
    var m = month < 10 ? "0" + month : "" + month;
    var day = date.getDate();
    var d = day < 10 ? "0" + day : day;
    return d + "." + m + "." + y;
}

function getStageTranslation(stage) {
    let translation = "---";
    let stagesTranslations = {
        "base-data-without-approved-nameplate" : "Basis-Daten ohne genehmigtes KFZ-Kennzeichen",
        "final-data-with-approved-nameplate" : "Finale Daten mit genehmigtem KFZ-Kennzeichen",
        "request-delivered-to-registration-authority" : "Daten wurden der Registrierungsbehörde übergeben",
        "request-returned-from-registration-authority" : "Daten wurden von der Registrierungsbehörde zurückgeschickt"
    };
    if(stagesTranslations[stage]) {
        translation = stagesTranslations[stage];
    }

    return translation;
}

function formatStateAction(data) {
    switch (data) {
        case 'mark-as-send-to-approval':
            return 'Markieren als fertig zur Freigabe';
        case 'suspend':
            return 'Pausieren';
    }

    return data;
}

function formatState(data) {
    switch (data) {
        case 'PENDING':
            return 'Wartet auf Verarbeitung';
        case 'PROCESSING':
            return 'Wird verarbeitet';
        case 'WAITING_FOR_INPUT':
            return 'Wartet auf Dateneingabe';
        case 'FAILED':
            return 'Fehlerhaft';
        case 'SUSPENDED':
            return 'Pausiert';
        case 'READY_TO_APPROVE':
            return 'Bereit für Freigabe';
        case 'WAITING_FOR_APPROVAL':
            return 'Wartet auf Freigabe';
        case 'REJECTED':
            return 'Abgelehnt';
        case 'DONE':
            return 'Erfolgreich abgeschlossen';
    }

    return data;
}

function formatStateType(data) {
    switch (data) {
        case 'processing/change':
            return 'Statusänderung';
        case 'processing/message':
            return 'Nachricht verfasst';
        case 'processing/start':
            return 'Eintrag wird verarbeitet';
        case 'processing/done':
            return 'Verarbeitung abgeschlossen';
        case 'processing/FULFILL-INTERNAL_CARD':
            return 'Registrierungskarte erstellt';
        case 'processing/FULFILL-TAX-PAYMENT-FORM':
            return 'KFZ-Steuer-Einzugsermächtigung erstellt';
        case 'processing/FULFILL-VEHICLE-REGISTRATION-FORM':
            return 'Zulassungsantrag erstellt';
    }

    return data;
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = window.location.search.substring(1),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURIComponent(sParameterName[1]);
        }
    }
}
