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

function initUsersTable() {

    $.when(loadCustomers()).done(
        function(data){
            console.log(data);

            var table = $('#users');

            table.DataTable({
                "data": data,
                "columns": [
                    {"data": "id"},
                    {"data": "firstName"},
                    {"data": "lastName"},
                    {"data": "email"}
                ]
            });

            var requestControls = $('#request-controls');
            var deleteUserButton = $('#delete-user-button');

            table.find('tbody').on('click', 'tr', function () {
                if ($(this).hasClass('selected')) {
                    $(this).removeClass('selected');
                    requestControls.hide();
                    deleteUserButton.prop('disabled', true);
                }
                else {
                    table.find('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                    var rowId = $(this).find('td:eq(0)').text();
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

    deleteUserButton.click( function () {
        var rowId = table.$('tr.selected').find('td:eq(0)').text();
        alert('Deleting client ' + rowId);
    });
    addUserButton.click( function () {
        window.open('create-customer.html', '_blank');
    });
}

function initRequestsTable(){

    $.when(loadRequests()).done(
        function(data) {
            console.log(data);

            var table = $('#requests');

            table.DataTable({
                "data": data,
                "columns": [
                    {"data": "id"},
                    {"data": "formDefinition"},
                    {"data": "customer"},
                    {"data": "creationTimestamp"}
                ]
            });

            var requestDetails = $('#request-details');

            table.find('tbody').on('click', 'tr', function () {
                if ($(this).hasClass('selected')) {
                    $(this).removeClass('selected');
                    requestDetails.hide();
                }
                else {
                    table.find('tr.selected').removeClass('selected');
                    $(this).addClass('selected');
                    var rowId = $(this).find('td:eq(0)').text();
                    var content = $('#attachment-' + rowId).clone();
                    requestDetails.html(content);
                    requestDetails.show();
                }
            });

            var attachments = $('#attachments');
            attachments.empty();

            for(pos in data){
                var req = data[pos];
                var divId = "attachment-" + req.id;
                var div = $('<div></div>').prop('id', divId);
                var h3 = $('<h3></h3>').append('Anhänge des Auftrages ' + req.id);
                var ul = $('<ul></ul>');

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
                    .append(ul);

                attachments
                    .append(div);
            }

        });
    
    var requestDetails = $('#request-details');
    
    requestDetails.hide();
    $('#attachments').hide();
}

function resolveFAType(mimeType){
    if("application/pdf" === mimeType){
        return "fa-file-pdf-o";
    }

    return "fa-file-text-o";
}