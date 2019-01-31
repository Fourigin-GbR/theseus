var usersDataTable;
var requestsDataTable;
var translationBundle = resolveLanguageBundle('de');

function init(){
    translationBundle = resolveLanguageBundle('de');

    initUsersTable();
    initRequestsTable();

    var languageDE = $('#language-de');
    var languageEN = $('#language-en');

    languageDE.click(function (){
        selectLanguage('de');
        languageDE.prop('disabled', true);
        languageEN.prop('disabled', false);
    });
    languageEN.click(function (){
        selectLanguage('en');
        languageEN.prop('disabled', true);
        languageDE.prop('disabled', false);
    });

    languageDE.prop('disabled', true);
}

function loadCustomers(){
    return $.ajax({
        url: "/forms-dashboard/customers"
    });
}

function loadRequests(){
    return $.ajax({
        url: "/forms-dashboard/requests"
    });
}

function deleteUser(userId){
    $.ajax({
        url: "forms-dashboard/delete-customer?customerId=" + userId
    });
}

function resolveLanguageBundle(language){
    if(language === 'en'){
        return "//cdn.datatables.net/plug-ins/1.10.19/i18n/English.json";
    }

    if(language === 'de'){
        return "//cdn.datatables.net/plug-ins/1.10.19/i18n/German.json";
    }

    // unsupported language, use default (en)
    return "//cdn.datatables.net/plug-ins/1.10.19/i18n/English.json";
}

function selectLanguage(lang) {
    translationBundle = resolveLanguageBundle(lang);

    usersDataTable.destroy();
    initUsersTable();

    requestsDataTable.destroy();
    initRequestsTable();
}

function internalInitUsersTable(data){
    var table = $('#users');

    usersDataTable = table.DataTable({
        "data": data,
        "rowId": function (data) {
            return data.id;
        },
        "columns": [
            {"data": "id"},
            {"data": "firstName"},
            {"data": "lastName"},
            {"data": "email"}
        ],
        "language": {
            "url": translationBundle
        }
    });
}

function internalInitRequestsTable(data) {
    var table = $('#requests');

    requestsDataTable = table.DataTable({
        "data": data,
        "rowId": function (data) {
            return data.id;
        },
        "columns": [
            {
                "data": "formDefinition",
                "render": function (data) {
                    if("register-vehicle" === data){
                        return "KFZ-Anmeldung";
                    }
                    if("register-customer" === data){
                        return "Registrierung";
                    }

                    return data;
                }
            },
            {
                "data": "customer"
            },
            {
                "data": "creationTimestamp",
                "render": function (data) {
                    var date = new Date(data);
                    return formatDate(date);
                }
            },
            {
                "data": "state"
            }
        ],
        "language": {
            "url": translationBundle
        }
        // ,
        // "columnDefs": [
        //     {
        //         "targets": [ 0 ],
        //         "visible": false,
        //         "searchable": false
        //     }
        // ]
    });
}

function initUsersTable() {
    $.when(loadCustomers()).done(
        function(data){
            console.log(data);

            var table = $('#users');

            internalInitUsersTable(data);

            var requestControls = $('#request-controls');
            var deleteUserButton = $('#delete-user-button');

            table.find('tbody').off().on('click', 'tr', function () {
                if ($(this).hasClass('selected')) {
                    $(this).removeClass('selected');
                    requestControls.hide();
                    deleteUserButton.prop('disabled', true);
                }
                else {
                    table.find('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                    var rowId = usersDataTable.row(this).id();
                    requestControls.show();
                    deleteUserButton.prop('disabled', false);
                    $('#selectedClient').val(rowId);
                }
            });
        }
    );

    var requestControls = $('#request-controls');
    var addUserButton = $('#add-user-button');
    var deleteUserButton = $('#delete-user-button');

    requestControls.hide();
    deleteUserButton.prop('disabled', true);

    deleteUserButton.off().click( function () {
        var rowId = usersDataTable.row($('tr.selected')).id();
        if(confirm("Sind Sie sicher, dass der Kunde " + rowId + " gelöscht werden soll?")){
            deleteUser(rowId);
            usersDataTable.destroy();
            initUsersTable();
        }
        else{
            return false;
        }
    });

    addUserButton.off().click( function () {
        window.open('form-benutzer-anlegen.html', '_blank'); // TODO: fix it!
    });
}

function initRequestsTable(){
    var dataTable = null;

    $.when(loadRequests()).done(
        function(data) {
            console.log(data);

            var table = $('#requests');

            internalInitRequestsTable(data)

            var requestDetails = $('#request-details');

            table.find('tbody').off().on('click', 'tr', function () {
                if ($(this).hasClass('selected')) {
                    $(this).removeClass('selected');
                    requestDetails.hide();
                }
                else {
                    table.find('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                    var rowId = requestsDataTable.row(this).id();
                    var content = $('#attachment-' + rowId).clone();
                    requestDetails.html(content);
                    requestDetails.show();
                }
            });

            var attachments = $('#attachments');
            attachments.empty();

            for(pos in data){
                var req = data[pos];
                var processingState = req.processingState;
                var divId = "attachment-" + req.id;
                var div = $('<div></div>').prop('id', divId);
                var h3 = $('<h3></h3>').append('Auftrag ' + req.id);
                var spanStatus = $('<div></div>')
                    .prop('class', 'status')
                    .append(processingState.state);
                var spanMessage = $('<div></div>')
                    .prop('class', 'message')
                    .append(processingState.currentStatusMessage);
                var fieldsetStatus = $('<fieldset></fieldset>')
                    .prop('class', 'status-container')
                    .append(spanStatus)
                    .append(spanMessage);
                
                var h4 = $('<h4></h4>').append('Anhänge:');
                var ul = $('<ul></ul>');
                var infoLink = $('<a></a>')
                    .prop('href', '/request-info.html?entryId=' + req.id)
                    .prop('target', '_blank')
                    .append('info');

                for(n in req.attachments){
                    var attachment = req.attachments[n];
                    var faType = resolveFAType(attachment.mimeType);

                    var span = $('<span></span>')
                        .prop('class', 'fa fa-lg ' + faType)
                        .append('&nbsp;');

                    var a = $('<a></a>')
                        .prop('href', '/forms-dashboard/attachment?entryId=' + req.id + '&attachmentName=' + attachment.name + '&mimeType=' + attachment.mimeType)
                        .prop('target', '_blank')
                        .append(attachment.filename);

                    var li = $('<li></li>')
                        .data('mime', attachment.mimeType)
                        .append(span)
                        .append(a);

                    ul.append(li);
                }

                div
                    .append(h3)
                    .append(fieldsetStatus)
                    .append(h4)
                    .append(ul)
                    .append(infoLink);

                attachments
                    .append(div);
            }

        });
    
    var requestDetails = $('#request-details');
    
    requestDetails.hide();
    $('#attachments').hide();

    return dataTable;
}

function resolveFAType(mimeType){
    if("application/pdf" === mimeType){
        return "fa-file-pdf-o";
    }

    return "fa-file-text-o";
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