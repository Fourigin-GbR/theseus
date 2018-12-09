var keysDataTable;
var propertiesDataTable;
var historyDataTable;

function init(){
    var entryId = getUrlParameter('entryId');
    console.log("Retrieving request entry for id " + entryId);

    $.when(loadRequestInfo(entryId)).done(
        function(data) {
            console.log("info:");
            console.log(data);

            var keysTable = $('#request-keys');
            keysDataTable = keysTable.DataTable({
                "paging":   false,
                "searching": false,
                "ordering": false,
                "info": false
            });

            var propertiesTable = $('#request-properties');
            propertiesDataTable = propertiesTable.DataTable({
                "dom": '<"toolbar">frtip',
                "paging":   false,
                "searching": false,
                "info": false
            });

            $("div.toolbar").html('<h4>Eigenschaften des Auftrages</h4>');
        }
    );

    $.when(loadRequestData(entryId)).done(
        function(data) {
            console.log("data:");
            console.log(data);

            var historyTable = $('#request-history');
            historyDataTable = historyTable.DataTable({
                "paging":   false,
                "searching": false,
                "ordering": false,
                "info": false,
                "columns": [
                    {
                        "render": function (data) {
                            var date = new Date(parseInt(data));
                            console.log('data: ' + data + ', date: ' + date);
                            return formatDate(date);
                        }
                    },
                    {
                        "render": function (data) {
                            return data;
                        }
                    },
                    {
                        "render": function (data) {
                            return data;
                        }
                    }
                ]
            });
        }
    );
}

function loadRequestInfo(entryId){
    // return $.ajax({
    //     url: "/forms-dashboard/request?entryId=" + entryId
    // });
    return 0;
}

function loadRequestData(entryId){
    // return $.ajax({
    //     url: "/forms-dashboard/data?entryId=" + entryId
    // });
    return 0;
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
