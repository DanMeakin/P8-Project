$(document).ready(function () {
    $('#datetimepicker1').datetimepicker({
        format: 'dd-mm-yyyy hh:ii'
    });

    $('#datetimepicker2').datetimepicker({
        format: 'dd-mm-yyyy hh:ii'
    });

    $('#birthday').datepicker({
        format: "dd-mm-yyyy"
    });

    $('#list-clubs-submit').on('click', function() {
        var value = $('#list-clubs :selected').val()
        $('#list-clubs-id').val(value);
    });

    $('#lifter-club-submit').on('click', function() {
        var value = $('#lifter-club :selected').val();
        $('#lifter-club-id').val(value);
    });

    $('i .glyphicon-pencil').on('click', function() {
        var id = $('i .glyphicon-pencil').closest('td .lifter-list-id').html();
        $('.lifter-id').val(id);
    });
});