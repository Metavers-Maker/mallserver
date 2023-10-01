var stompClient = null;
var notificationCount = 0;

$(document).ready(function () {
    console.log("Index page is ready");
    connect();

    $("#send").click(function () {
        sendMessage();
    });

    $("#send-private").click(function () {
        sendPrivateMessage();
    });

    $("#notifications").click(function () {
        resetNotificationCount();
    });
});

function connect() {

    var socket = new SockJS('http://192.168.11.42:9900/mall-chat/websocket');
    stompClient = Stomp.over(socket);
    var token = "" + "Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInNjb3BlIjpbImFsbCJdLCJleHAiOjE2Mzk1NDY5ODUsInVzZXJJZCI6MiwiYXV0aG9yaXRpZXMiOlsiQURNSU4iXSwianRpIjoiMmM2YzA1OWUtZTM5MC00MmUyLWI2NGEtOTA1MWMyZTE4YTgwIiwiY2xpZW50X2lkIjoiY2xpZW50IiwidXNlcm5hbWUiOiJhZG1pbiJ9.RVXIPQkNEv-fPSGpaP4KqxodSBJjRTRimiTQxOULeLSO8HA0yRWKc7lIDPkfTsDZcZA0O4tnYH1YeJ7cMSCfBx2S_-x_bBh9PgLl041UKzNu1zO0TUSyoKwlwA_q0gt5WAiy2_3sqAGN6gZ9ROGiKtXmFANkhYT4pzx7O6hLaCPHJe8k4udgsQw4fN820fLKHolGPbuIpM3pmiqsHlgwIa76ucz5JnldXC54Vn8tUzfqWIBgBkVm-bq6TicjunGGK0GGc8fa2h8K2y1XIHI8MaPXffjaCmavJBYPf4s0Ncd_CaiBQt67BkIBKpV6xskI0ZsGkDQs4Klpay77QSmX_w";
    stompClient.connect({'Authorization': token}, function (frame) {
        console.log('Connected: ' + frame);
        updateNotificationDisplay();
        stompClient.subscribe('/topic/public-messages', function (message) {
            showMessage(JSON.parse(message.body).data);
        });

        stompClient.subscribe('/user/queue/private-messages', function (message) {
            showMessage(JSON.parse(message.body).data);
        });

        stompClient.subscribe('/topic/public-notify', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });

        stompClient.subscribe('/user/queue/private-notify', function (message) {
            notificationCount = notificationCount + 1;
            updateNotificationDisplay();
        });
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function showMessage(message) {
    $("#messages").append("<tr><td>" + message + "</td></tr>");
}

function sendMessage() {
    console.log("sending message");
    stompClient.send("/ws/public-message", {}, JSON.stringify({'data': $("#message").val()}));
}

function sendPrivateMessage() {
    console.log("sending private message");
    stompClient.send("/ws/private-message", {}, JSON.stringify({'data': $("#private-message").val()}));
}

function updateNotificationDisplay() {
    if (notificationCount == 0) {
        $('#notifications').hide();
    } else {
        $('#notifications').show();
        $('#notifications').text(notificationCount);
    }
}

function resetNotificationCount() {
    notificationCount = 0;
    updateNotificationDisplay();
}
