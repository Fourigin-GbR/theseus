var admin = false;
var usersDataTable;
var requestsDataTable;
var translationBundle = resolveLanguageBundle('de');

function init() {
    translationBundle = resolveLanguageBundle('de');

    initUsersTable();
    initRequestsTable();

    var languageDE = $('#language-de');
    var languageEN = $('#language-en');

    var jFilterByType = jQuery('#dataTableFilter');
    var jFilterByStatus = jQuery('#dataTableStatusFilter');
    var jAllFilters = jFilterByType.add(jFilterByStatus);

    languageDE.click(function () {
        selectLanguage('de');
        languageDE.prop('disabled', true);
        languageEN.prop('disabled', false);
    });
    languageEN.click(function () {
        selectLanguage('en');
        languageEN.prop('disabled', true);
        languageDE.prop('disabled', false);
    });

    languageDE.prop('disabled', true);

    jAllFilters.on('change',
        function () {
            var sSearch = jFilterByType[0].value + ' ' + jFilterByStatus[0].value;
            requestsDataTable.search(sSearch ).draw();
        });
}

function loadCustomers() {
    return $.ajax({
        url: "/forms-dashboard/customers"
    });
}

function loadRequests() {
    return $.ajax({
        url: "/forms-dashboard/requests"
    });
}

function deleteUser(userId) {
    $.ajax({
        url: "forms-dashboard/delete-customer?customerId=" + userId
    });
}

function resolveLanguageBundle(language) {
    if (language === 'en') {
        return "//cdn.datatables.net/plug-ins/1.10.19/i18n/English.json";
    }

    if (language === 'de') {
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

function internalInitUsersTable(data) {
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
        "order": [[ 5, 'desc' ]],
        "drawCallback": function ( settings ) {
            var api = this.api();
            var rows = api.rows( {page:'current'} ).nodes();
            var last=null;

            api.column(6, {page:'current'} ).data().each( function ( group, i ) {
                var parseTimestampToDate = function (group) {
                    var date = new Date(group);
                    return formatDateForGrouping(date);
                };
                var parsedDate = parseTimestampToDate(group);

                if ( last !== parsedDate ) {
                    var date = new Date(group);
                    var viewDateFormatted = window.formatDateForGroupingView(date);
                    $(rows).eq( i ).before(
                        '<tr class="group"><td colspan="4">' + viewDateFormatted + '</td></tr>'
                    );

                    last = parsedDate;
                }
            } );
        },
        "columns": [
            {
                "data": "id",
                "visible": false
            },
            {
                "data": "formDefinition",
                "render": function (data) {
                    if ("register-vehicle" === data) {
                        return "KFZ-Anmeldung";
                    }
                    if ("register-customer" === data) {
                        return "Registrierung";
                    }

                    return data;
                }
            },
            {
                "data": "customer"
            },
            {
                "data": "customerName"
            },
            {
                "data": "requestData"
            },
            {
                "data": "creationTimestamp",
                "render": function (data) {
                    var date = new Date(data);
                    return formatDateForGroupedView(date);
                }
            },
            {
                "data": "creationTimestamp",
                "render": function (data) {
                    var date = new Date(data);
                    return formatDateForSorting(date);
                },
                "visible": false
            },
            {
                "data": "creationTimestamp",
                "render": function (data) {
                    var date = new Date(data);
                    return formatDateForGrouping(date);
                },
                "visible": false
            },
            {
                "data": "state",
                "render": function (data) {
                    return formatState(data);
                }
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
        function (data) {
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

    deleteUserButton.off().click(function () {
        var rowId = usersDataTable.row($('tr.selected')).id();
        if (confirm("Sind Sie sicher, dass der Kunde " + rowId + " gelöscht werden soll?")) {
            deleteUser(rowId);
            usersDataTable.destroy();
            initUsersTable();
        }
        else {
            return false;
        }
    });

    addUserButton.off().click(function () {
        window.open('form-benutzer-anlegen.html', '_blank'); // TODO: fix it!
    });
}

function initRequestsTable() {
    var dataTable = null;

    $.when(loadRequests()).done(
        function (data) {
            console.log(data);

            var table = $('#requests');

            internalInitRequestsTable(data);

            var requestDetails = $('#request-details');

            requestDetails.on('click', '.close', function() {
                requestDetails.hide();
                table.find('tbody tr.selected').removeClass('selected');
            });

            // Events for show selected row status and showing details on: row click.
            table.find('tbody').off().on('click', 'tr:not(.group)', function () {
                var jThis = $(this);
                if (jThis.hasClass('selected')) {
                    jThis.removeClass('selected');
                    requestDetails.hide();
                }
                else {
                    table.find('tr.selected').removeClass('selected');
                    jThis.addClass('selected');
                    var rowId = requestsDataTable.row(this).id();
                    var content = $('#attachment-' + rowId).clone();
                    requestDetails.find(".requestDetails__content").html(content);
                    requestDetails.show();
                }
            });

            table.find('tbody').on('click', 'tr.group', function () {
                var jThis = $(this);
                if(jThis.hasClass('closed')) {
                    jThis.removeClass('closed');
                    jThis.nextUntil('tr.group').show();
                }
                else {
                    jThis.addClass('closed');
                    jThis.nextUntil('tr.group').hide();
                }

            });

            var attachments = $('#attachments');
            attachments.empty();

            // Generate processing state information.
            for (pos in data) {
                var req = data[pos];
                var processingState = req.processingState;
                var divId = "attachment-" + req.id;
                var div = $('<div></div>').prop('id', divId);

                if(processingState.state === 'WAITING'){
                    var approveForm = $('<form></form>')
                        .prop('class', 'ajax')
                        .prop('action', '/forms-dashboard/change-state');
                    var approveIdField = $('<input></input>')
                        .prop('type', 'hidden')
                        .prop('name', 'entryId')
                        .prop('value', req.id);
                    var approveCustomerIdField = $('<input></input>')
                        .prop('type', 'hidden')
                        .prop('name', 'customerId')
                        .prop('value', req.customer);
                    var approveStateField = $('<input></input>')
                        .prop('type', 'hidden')
                        .prop('name', 'processingState')
                        .prop('value', 'DONE');
                    var approveMessageField = $('<input></input>')
                        .prop('type', 'hidden')
                        .prop('name', 'comment')
                        .prop('value', '');
                    var approveButton = $('<input></input>')
                        .prop('id', 'approveButton-' + req.id)
                        .prop('type', 'submit')
                        .prop('value', 'Bearbeitung abschliessen');
                    approveForm.append(approveCustomerIdField);
                    approveForm.append(approveMessageField);
                    approveForm.append(approveStateField);
                    approveForm.append(approveIdField);
                    approveForm.append(approveButton);

                    div.append(approveForm);
                }

                var infoButton = $('<input></input>')
                    .prop('id', 'infoButton-' + req.id)
                    .prop('type', 'submit')
                    .prop('value', 'Auftragsdetails ...');

                var reqIdHidden = $('<input></input>')
                    .prop('name', 'entryId')
                    .prop('value', req.id)
                    .prop('type', 'hidden');

                var form = $('<form></form>')
                    .prop('action', '/request-info.html')
                    .prop('target', '_blank')
                    .append(infoButton)
                    .append(reqIdHidden);

                var ul = $('<ul></ul>');

                var visibleAttachmentsCount = 0;
                for (n in req.attachments) {
                    var attachment = req.attachments[n];
                    if (admin || attachment.mimeType === 'application/pdf') {
                        visibleAttachmentsCount++;
                        var faType = resolveFAType(attachment.mimeType);

                        var span = $('<span></span>')
                            .prop('class', 'fileIcon fa fa-lg ' + faType)
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
                }

                if (processingState.currentStatusMessage) {
                    var spanMessage = $('<div></div>')
                        .prop('class', 'message')
                        .append(processingState.currentStatusMessage);

                    div.append(spanMessage);
                }

                div.append(form);

                if (visibleAttachmentsCount > 0) {
                    var h4 = $('<h4></h4>')
                        .append('Anhänge (' + visibleAttachmentsCount + '):');

                    div
                        .append(h4)
                        .append(ul);
                }

                attachments
                    .append(div);
            }
        });

    var requestDetails = $('#request-details');

    requestDetails.hide();
    $('#attachments').hide();

    return dataTable;
}

function approveEntry(entryId){
    alert('Approving entry ' + entryId);
}

function resolveFAType(mimeType) {
    if ("application/pdf" === mimeType) {
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

function formatState(data) {
    switch (data) {
        case 'FAILED':
            return 'Fehlerhaft';
        case 'PENDING':
            return 'Wartet auf Verarbeitung';
        case 'PROCESSING':
            return 'Wird intern verarbeitet';
        case 'SUSPENDED':
            return 'Pausiert';
        case 'REJECTED':
            return 'Abgelehnt';
        case 'WAITING':
            return 'Wartet auf Dateneingabe';
        case 'IN_PROGRESS':
            return 'In Arbeit';
        case 'DONE':
            return 'Erfolgreich abgeschlossen';
    }

    return data;
}

var processAjaxForms = function() {
    $('body').on('submit', function(e) {
        if( e.target.nodeName !== "FORM") {
            return true;
        }
        if(!$(e.target).hasClass('ajax')) {
            return true;
        }

        var jForm = $(e.target);

        function ConvertFormToJSON(jForm){
            var array = jForm.serializeArray();
            var json = {};

            $.each(array, function() {
                json[this.name] = this.value || '';
            });

            return json;
        }

        e.preventDefault();

        console.log("send form:", jForm, "->", jForm.serializeArray(), " -- ", ConvertFormToJSON(jForm));

        $.ajax({
            url: jForm.attr('action'),
            type : "POST",
            dataType : 'json',
            data: ConvertFormToJSON(jForm),
            success : function(result) {
                console.log(result);
            },
            error: function(xhr, resp, text) {
                console.error(xhr, resp, text);
            }
        })
    });
};

$(document).ready(function(){
    processAjaxForms();
});