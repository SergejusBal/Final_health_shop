


async function joinCalendar(websocketID, jwToken) {
    return new Promise((resolve, reject) => {
        var socket = new SockJS(javaURL + '/websocket');
        stompClient = Stomp.over(socket);
       
        const headers = {
            'Authorization': "Bearer " + jwToken
        };

        stompClient.connect(headers, function (frame) { 
            
            stompClient.subscribe('/topic/date/' + websocketID, function (message) {
                
                messageData = JSON.parse(message.body);
                calendarMessageResieved(messageData);
                
            }, headers);            
            
            resolve(frame);
        }, function (error) {
            console.log('Error: ' + error);                                
            reject(error);
        });
    });
    }

async function sendDate(websocketID, jwToken, messageJson){

    const headers = {
        'Authorization': "Bearer " + jwToken
    };

    stompClient.send("/app/date/" + websocketID, headers, messageJson);
}

