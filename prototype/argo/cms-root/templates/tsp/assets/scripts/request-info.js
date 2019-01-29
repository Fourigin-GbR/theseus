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

            var keysTable = $('#request-keys');

            /*
                        <tr>
                            <td class="key">ID</td>
                            <td class="value" colspan="5">1234567890123456789012345678901234567890</td>
                            <td style="display: none;"></td>
                            <td style="display: none;"></td>
                            <td style="display: none;"></td>
                            <td style="display: none;"></td>
                        </tr>
                        <tr>
                            <td class="key">Typ</td>
                            <td class="value">KFZ-Anmeldung</td>
                            <td class="key">Kundennummer</td>
                            <td class="value">1</td>
                            <td class="key">Datum</td>
                            <td class="value">17.02.2018</td>
                        </tr>

             */

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
                    .append($('<td></td>').attr('class', 'value').html(formatDate(creationDate, false)))
                );

            keysDataTable = keysTable.DataTable({
                "paging": false,
                "searching": false,
                "ordering": false,
                "info": false
            });

            var historyTable = $('#request-history');

            var historyBody = historyTable.find('tbody');
            historyBody.empty();

            var processingState = data.processingState;
            var historyData = processingState.history;

            historyData.sort(sortByTimestamp);

            $.each(historyData, function (index, historyEntry) {
                var row;

                var date = new Date(parseInt(historyEntry.timestamp));

                if (historyEntry.value) {
                    row = $('<tr></tr>')
                        .append($('<td></td>').attr('class', 'timestamp').append(formatDate(date, true)))
                        .append($('<td></td>').attr('class', 'state').append(historyEntry.key))
                        .append($('<td></td>').attr('class', 'message').append(historyEntry.value));
                }
                else {
                    row = $('<tr></tr>')
                        .append($('<td></td>').attr('class', 'timestamp').append(formatDate(date, true)))
                        .append($('<td></td>').attr('colspan', 2).attr('class', 'state').append(historyEntry.key))
                        .append($('<td></td>').attr('style', 'display: none;'));
                }
                historyBody.append(row);
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
    jStateChangeButton.on("change", function() {
        setFieldsetStateChangeVisibility();
    });

    // slide
    var setFieldsetStateChangeVisibility = function() {
        if(jStateChangeButton.is(":checked")) {
            jFieldsetStateChange.slideDown();
        }
        else {
            jFieldsetStateChange.slideUp();
        }
    };

    // ajax
    jStateChangeForm.on("submit", function(e) {
        e.preventDefault();

        $.ajax({
            method: "POST",
            url: jStateChangeForm.attr("action"),
            data: jStateChangeForm.serializeArray()
        })
            .done(function( msg ) {
                loadRequestInfo(entryId);
            })
            .fail(function( msg ) {
                alert( "There was an error: " + msg );
            });
    });

    // init
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
};

function sortByTimestamp(a, b) {
    var ts1 = parseInt(a.timestamp);
    var ts2 = parseInt(b.timestamp);
    return ((ts2 < ts1) ? -1 : ((ts2 > ts1) ? 1 : 0));

}

function formatDate(date, showSeconds) {
    var month = date.getMonth() + 1;
    var y = date.getFullYear();
    var m = month < 10 ? "0" + month : "" + month;
    var day = date.getDate();
    var d = day < 10 ? "0" + day : day;
    var hh = date.getHours();
    var minutes = date.getMinutes();
    var mm = minutes < 10 ? "0" + minutes : minutes;
    if (showSeconds) {
        var seconds = date.getSeconds();
        var ss = seconds < 10 ? "0" + seconds : seconds;
        return d + "." + m + "." + y + "&nbsp;" + hh + ":" + mm + ":" + ss;
    }
    else {
        return d + "." + m + "." + y + "&nbsp;" + hh + ":" + mm;
    }
}

