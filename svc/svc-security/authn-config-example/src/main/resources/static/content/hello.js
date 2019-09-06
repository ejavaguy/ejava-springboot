$(document).ready(function() {
    $.ajax({
        url: "http://localhost:8080/api/anonymous/hello?name=jim"
    }).then(function(data, status, jqxhr) {
       $('.greeting-content').append(data);
       console.log(jqxhr);
    });
});