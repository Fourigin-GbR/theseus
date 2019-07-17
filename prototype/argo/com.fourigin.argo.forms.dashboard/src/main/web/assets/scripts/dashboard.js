var admin = false;
var usersDataTable;
var requestsDataTable;
var translationBundle = resolveLanguageBundle('de');
let requestData, stages, amountOfStages, stagesNamesList = [];

function init() {
    translationBundle = resolveLanguageBundle('de');

    // Stages
    $.when(loadStages()).done(
        function (data) {
            stages = data;
            amountOfStages = stages.length;
            for(let i=0; i<amountOfStages; i++) {
                stagesNamesList.push(stages[i]['name']);
            }
            console.log("stagesNamesList", stagesNamesList);

            initUsersTable();
            initRequestsTable();
        }
    );

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

    jQuery('#dataTableStatusFilter > option').each(function() {
        var option = jQuery(this);
        var code = option.attr('name');
        if (code === "") {
            return;
        }

        var state = formatState(code);
        option.attr('value', state);
        option.text(state);
    });

    jAllFilters.on('change',
        function () {
            var sSearch = jFilterByType[0].value + ' ' + jFilterByStatus[0].value;
            requestsDataTable.search(sSearch ).draw();
        });
}

function loadStages() {
    return $.ajax({
        url: "/forms-dashboard/stages?formId=register-vehicle"
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
        "order": [[ 6, 'desc' ]],
        "drawCallback": function ( settings ) {
            var api = this.api();
            var rows = api.rows( {page:'current'} ).nodes();
            var last=null;

            api.column(5, {page:'current'} ).data().each( function ( group, i ) {
                var parseTimestampToDate = function (group) {
                    var date = new Date(group);
                    return formatDateForGrouping(date);
                };
                var parsedDate = parseTimestampToDate(group);

                if ( last !== parsedDate ) {
                    var date = new Date(group);
                    var viewDateFormatted = window.formatDateForGroupingView(date);
                    $(rows).eq( i ).before(
                        '<tr class="group"><td colspan="7">' + viewDateFormatted + '</td></tr>'
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
                "data": "stage",
                "render": function (data, type, full) {
                    let processHandleWidth = ((stagesNamesList.indexOf(data) + 1) * 100) / amountOfStages;
                    return $('<div></div>')
                        .attr('class', data + ' stage-value-cell')
                        .append('<div class="processBar"><span class="processBar__handle" style="width:' + processHandleWidth + '%"></span><span class="processBar__label">' + processHandleWidth + '%</span></div>')
                        .append($('<span class="processName"></span>')
                            .html(getStageTranslation(data))
                        )[0].outerHTML;
                }
            },
            {
                "data": "state",
                "render": function (data, type, full) {
                    return $('<div></div>')
                        .attr('class', data + ' state-value-cell')
                        .append($('<span></span>')
                            .html(getStateHtml(data))
                        )[0].outerHTML;
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

    let jOverlayStagePrototype = $("<div class=\"request-stage\">\n" +
        "                                <div class=\"request-stage-title\"></div>\n" +
        "                                <div class=\"request-stage-status\">\n" +
        "                                    <i class=\"statusComplete fa fa-check-circle\"></i>\n" +
        "                                    <i class=\"statusIncomplete fa fa-circle-thin\"></i>\n" +
        "                                </div>\n" +
        "                                <div class=\"request-stage-edit-action\">\n" +
        "                                </div>\n" +
        "                            </div>");
    let jOverlayStageEditButton = $("<form>\n" +
        "                                        <input type=\"submit\" class=\"buttonLca\" value=\"\"/>\n" +
        "                                        <input type=\"hidden\" name=\"entryId\" value=\"\"/>\n" +
        "                                    </form>\n");
    let jOverlayStageActionButton = $("<span><input type=\"submit\" class=\"buttonLca\" value=\"\"/>\n" +
        "                                        <input type=\"hidden\" name=\"entryId\" value=\"\"/></span>\n");

    $.when(loadRequests()).done(
        function (data) {
            console.log(data);

            var table = $('#requests');

            requestData = data;
            internalInitRequestsTable(data);

            var requestDetails = $('#request-details');
            var jRequestStages = requestDetails.find(".request-stages");

            requestDetails.on('click', '.close', function(e) {
                e.stopPropagation();
                requestDetails.hide();
            });

            // Events for show selected row status and showing details on: row click.
            table.find('tbody').off().on('click', 'tr:not(.group)', function () {
                var jThis = $(this);
                if (jThis.hasClass('selected')) {
                    jThis.removeClass('selected');
                    requestDetails.hide();
                }
                else {
                    // Overlay
                    table.find('tr.selected').removeClass('selected');
                    jThis.addClass('selected');
                    var rowId = requestsDataTable.row(this).id();
                    var content = $('#attachment-' + rowId).clone();
                    var currentRequestData = getRequestDataItemById(rowId);
                    var bFoundRequestStage = false;
                    var jRequestState = getStateHtml(currentRequestData.state);
                    var currentStageObject;
                    var jCurrentStageActionsWrapper = $(".request-current-stage-actions-wrapper");
                    var jCurrentStageActions = jCurrentStageActionsWrapper.find(".request-current-stage-actions");

                    requestDetails.find(".requestDetails__content").html(content);
                    // Set stages:
                    console.log("Zeige details von dem: ", rowId, requestData, currentRequestData);
                    jRequestStages.empty();
                    for(let i=0; i<amountOfStages; i++) {
                        let jStageCurrent = jOverlayStagePrototype.clone();
                        //
                        if(!bFoundRequestStage) {
                            // Not passed the current stage yet, so guess all previous stages are done:
                            jStageCurrent.find(".request-stage-status").addClass("request-stage-status--done");
                        }
                        if(!bFoundRequestStage && stages[i].name === currentRequestData.stage) {
                            bFoundRequestStage = true;
                            currentStageObject = stages[i];
                        }
                        jStageCurrent.find(".request-stage-title").text(stages[i].name);
                        // Stage-field-edit-action
                        if(!stages[i].editFields) {
                            let jStageEditButton = jOverlayStageEditButton.clone();
                            //
                            jStageEditButton.find("input[type=submit]").val("Daten erneut editieren");
                            jStageEditButton.find("input[name=entryId]").val(rowId);
                            jStageCurrent.find(".request-stage-edit-action").append(jStageEditButton);
                        }
                        jRequestStages.append(jStageCurrent);
                    }
                    // State:
                    requestDetails.find(".request-status").empty().append(jRequestState);
                    // Stage actions:
                    jCurrentStageActions.empty();
                    for(let key in currentStageObject.actions) {
                        let jCurrentButton = jOverlayStageActionButton.clone();
                        //
                        jCurrentButton.find("input[type=submit]").val(key);
                        jCurrentStageActions.append(jCurrentButton);
                    }
                    //
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
                        .prop('class', 'buttonLca')
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
                    .prop('class', 'buttonLca')
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
                    var divAttachments = $('<div class="request-attachments"></div>');
                    var h5 = $('<h5></h5>')
                        .append('Anhänge (' + visibleAttachmentsCount + '):');

                    divAttachments
                        .append(h5)
                        .append(ul);

                    div.append(divAttachments)
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

function getStateHtml(state) {
    let stateCopy = formatState(state),
        stateIconClassName = state,
        jState;
    //
    switch(state) {
        case "PENDING":
        case "WAITING_FOR_INPUT":
        case "WAITING_FOR_APPROVAL":
            stateIconClassName += " fa fa-clock-o";
            break;
        case "PROCESSING":
            stateIconClassName += " fa fa-cogs";
            break;
        case "FAILED":
            stateIconClassName += " fa fa-exclamation-triangle";
            break;
        case "SUSPENDED":
            stateIconClassName += " fa fa-pause";
            break;
        case "READY_TO_APPROVE":
            stateIconClassName += " fa fa-flag";
            break;
        case "REJECTED":
            stateIconClassName += " fa fa-times";
            break;
        case "DONE":
        case "PROCESSED":
            stateIconClassName += " fa fa-check-square-o";
            break;
    }
    jState = $("<i class=\"statusIcon " + stateIconClassName + "\" aria-hidden=\"true\"></i><span>" + stateCopy + "</span>");

    return jState;
}

function getRequestDataItemById(id) {
    for(let i=0, il = requestData.length; i<il; i++) {
        let currentRequestItem = requestData[i];
        if(currentRequestItem.id === id) {
            return currentRequestItem;
        }
    }

    return null;
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