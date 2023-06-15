function sendReport(id) {
    if (confirm('신고하시겠습니까?')) {
        let header = $("meta[name='_csrf_header']").attr('content');
        let token = $("meta[name='_csrf']").attr('content');

        debugger;

        $.ajax({
            url: "/account/report",
            type: "POST",
            data: {
                id: id
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader(header, token);
            },
            success: function (result) {
                alert(result);
            },
            error: function (request, status, error) {
                alert(status);
            }
        })
    }
}