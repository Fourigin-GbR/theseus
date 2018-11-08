function initUsersTable() {
    $('#users').DataTable({
        "ajax": "assets/data/users.json"
    });

    var table = $('#users').DataTable();

    $('#users tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            $('#request-controls').hide();
            $('#delete-user-button').prop('disabled', true);
        }
        else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var rowId = $(this).find('td:eq(0)').text();
            $('#request-controls').show();
            $('#delete-user-button').prop('disabled', false);
            $('#selectedClient').val(rowId);
        }
    });

    $('#request-controls').hide();
    $('#delete-user-button').prop('disabled', true);

    $('#delete-user-button').click( function () {
        var rowId = table.$('tr.selected').find('td:eq(0)').text();
        alert('Deleting client ' + rowId);
    });
    $('#add-user-button').click( function () {
        window.open('create-customer.html', '_blank');
    });

// $('#button').click( function () {
//     table.row('.selected').remove().draw( false );
// } );
}

function initRequestsTable(){
    $('#requests').DataTable({
        "ajax": "assets/data/requests.json"
    });

    var table = $('#requests').DataTable();

    $('#requests tbody').on('click', 'tr', function () {
        if ($(this).hasClass('selected')) {
            $(this).removeClass('selected');
            $('#request-details').hide();
        }
        else {
            table.$('tr.selected').removeClass('selected');
            $(this).addClass('selected');
            var rowId = $(this).find('td:eq(0)').text();
            var content = $('#attachment-' + rowId).clone();
            $('#request-details').html(content);
            $('#request-details').show();
        }
    });

    $('#request-details').hide();
    $('#attachments').hide();
}