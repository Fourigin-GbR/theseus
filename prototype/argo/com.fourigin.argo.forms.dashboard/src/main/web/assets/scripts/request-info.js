var keysDataTable;
var propertiesDataTable;
var historyDataTable;

function init() {
    var entryId = getUrlParameter('entryId');
    console.log("Retrieving request entry for id " + entryId);

    $.when(loadRequestInfo(entryId)).done(
        function (data) {
            console.log("info:");
            console.log(data);

            // request data
            var keysTable = $('#request-keys');

            var keysBody = keysTable.find('tbody');
            keysBody.empty();

            var creationDate = new Date(parseInt(data.creationTimestamp));

            keysBody
                .append($('<tr></tr>')
                    .append($('<td></td>').attr('class', 'key').text('ID'))
                    .append($('<td></td>').attr('class', 'value').attr('colspan', 5).text(data.id))
                    .append($('<td></td>').attr('style', 'display: none;'))
                    .append($('<td></td>').attr('style', 'display: none;'))
                    .append($('<td></td>').attr('style', 'display: none;'))
                    .append($('<td></td>').attr('style', 'display: none;'))
                )
                .append($('<tr></tr>')
                    .append($('<td></td>').attr('class', 'key').text('Typ'))
                    .append($('<td></td>').attr('class', 'value').text(data.formDefinition))
                    .append($('<td></td>').attr('class', 'key').text('Kundennummer'))
                    .append($('<td></td>').attr('class', 'value').text(data.customer))
                    .append($('<td></td>').attr('class', 'key').text('Datum'))
                    .append($('<td></td>').attr('class', 'value').html(formatDate(creationDate, true)))
                );

            keysDataTable = keysTable.DataTable({
                "paging": false,
                "searching": false,
                "ordering": false,
                "info": false
            });

            // current status
            var processingState = data.processingState;

            var currentStateDiv = $('#current-state');
            var stateMessageSpan = $(currentStateDiv).find('.state-message')
                .empty();

            var message = processingState.currentStatusMessage;
            if (message) {
                stateMessageSpan.append(message)
            }

            $(currentStateDiv).find('#state-value')
                .empty()
                .removeClass()
                .addClass(processingState.state)
                .append(formatState(processingState.state));

            // form
            $('select#new-state').val(processingState.state);
            $('input#form-customerId').prop('value', data.customer);
            $('input#form-entryId').prop('value', entryId);

            // status history
            var historyTable = $('#request-history');

            var historyBody = historyTable.find('tbody');
            historyBody.empty();

            var historyData = processingState.history;

            var indent = '..................&nbsp;'
            var lastDate = null;
            $.each(historyData, function (index, historyEntry) {
                var date = new Date(historyEntry.timestamp);

                var currentDate = date.getFullYear() + '.' + (date.getMonth()+1) + '.' + date.getDate();

                console.log('check: ' + (currentDate === lastDate) + ', lastDate: ' + lastDate + ', currentDate: ' + currentDate + ': ' + historyEntry.key + ' - ' + historyEntry.value);

                if (historyEntry.value) {
                    historyBody.prepend(
                        $('<tr></tr>')
                            .append($('<td></td>')
                                .attr('class', 'timestamp')
                                .append((currentDate === lastDate) ? indent + formatTime(date, true) : formatDate(date, true))
                            )
                            .append($('<td></td>')
                                .attr('class', 'state')
                                .append(formatStateType(historyEntry.key))
                            )
                            .append($('<td></td>')
                                .attr('class', 'message')
                                .append(formatState(historyEntry.value))
                            )
                    );
                }
                else {
                    historyBody.prepend(
                        $('<tr></tr>')
                            .append($('<td></td>')
                                .attr('class', 'timestamp')
                                .append((currentDate === lastDate) ? indent + formatTime(date, true) : formatDate(date, true))
                            )
                            .append($('<td></td>')
                                .attr('colspan', 2)
                                .attr('class', 'state')
                                .append(formatStateType(historyEntry.key))
                            )
                            .append($('<td></td>')
                                .attr('style', 'display: none;')
                            )
                    );
                }

                lastDate = currentDate;
            });

            historyDataTable = historyTable.DataTable({
                "paging": false,
                "searching": false,
                "ordering": false,
                "info": false
            });
        }
    );

    $.when(loadRequestData(entryId)).done(
        function (data) {
            console.log("data:");
            console.log(data);

            var propertiesTable = $('#request-properties');

            var tbody = propertiesTable.find('tbody');
            tbody.empty();

            $.each(data, function (key, value) {
                var cell1 = $('<td></td>').attr('class', 'key').append(key);
                var cell2 = $('<td></td>').attr('class', 'value').append(value);
                var row = $('<tr></tr>')
                    .append(cell1)
                    .append(cell2);
                tbody.append(row);
            });

            propertiesDataTable = propertiesTable.DataTable({
                "dom": '<"toolbar">frtip',
                "paging": false,
                "searching": false,
                "info": false
            });

            $("div.toolbar").html('<h4>Eigenschaften des Auftrages</h4>');
        }
    );

    //

    var jStateChangeButton = $("input#state-change-button"),
        jFieldsetStateChange = $("fieldset#state-change"),
        jStateChangeForm = jFieldsetStateChange.find("form#state-change-form");
    //

    // event button
    jStateChangeButton.button();
    jStateChangeButton.on("change", function () {
        setFieldsetStateChangeVisibility();
    });

    // slide
    var setFieldsetStateChangeVisibility = function () {
        if (jStateChangeButton.is(":checked")) {
            jFieldsetStateChange.slideDown();
        }
        else {
            if(jFieldsetStateChange.is(':visible')) {
                jFieldsetStateChange.slideUp();
            }
        }
    };

    // ajax
    jStateChangeForm.on("submit", function (e) {
        e.preventDefault();

        $.ajax({
            method: "POST",
            url: jStateChangeForm.attr("action"),
            data: jStateChangeForm.serializeArray()
        })
            .done(function (msg) {
                location.reload();
            })
            .fail(function (msg) {
                alert("There was an error: " + msg);
            });
    });

    // init
    jFieldsetStateChange.hide();
    setFieldsetStateChangeVisibility();

}

function loadRequestInfo(entryId) {
    return $.ajax({
        url: "/forms-dashboard/request?entryId=" + entryId
    });
}

function loadRequestData(entryId) {
    return $.ajax({
        url: "/forms-dashboard/data?entryId=" + entryId
    });
}
