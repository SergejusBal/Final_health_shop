
document.addEventListener('DOMContentLoaded', async function () {  
    populateSelectWithDates()
});


function populateSelectWithDates() {
    const selectElement = document.getElementById("selectDay");

    const today = new Date();

  
    for (let i = 0; i <= 28; i++) {
        const option = document.createElement("option");       
        const date = new Date();
        date.setDate(today.getDate() + i);
        const formattedDate = date.toISOString().split("T")[0];
        
        option.value = formattedDate;
        option.textContent = formattedDate;
       
        selectElement.appendChild(option);
    }
}


async function check(){    

    document.getElementById("service-order-Div").innerHTML = "";

    let jSonData = await getServiceOrders();
    if(!jSonData) return;    

    jSonData.forEach(order => {
        console.log(order);
        addServiceOrderDiv(order);
    });


}

async function getServiceOrders(){

    let jwToken = getCookie("jwToken");    
    
    let serviceOrderStatus = document.getElementById("serviceOrderStatus").value;
    let selectDay = document.getElementById("selectDay").value;

    let response = await getAPI(javaURL + "/employee/get/" + serviceOrderStatus +"/" + selectDay, jwToken);     
    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('employeeMessage').textContent = "No available services found";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('employeeMessage').textContent = "Ops something went wrong! Sorry!";   
        return null;
    }
};

async function addServiceOrderDiv(order) {

    const mainDiv = document.getElementById("service-order-Div");
    const orderDiv = document.createElement("div");
        orderDiv.className = "orderCard";

        const timeLabel = document.createElement("h1");
        timeLabel.textContent = order.timeSlot.substring(11);
        const statusLabel = document.createElement("p");
        statusLabel.textContent = "(" + order.paymentStatus + ")";
        statusLabel.style.textAlign = "center";


        orderDiv.appendChild(timeLabel);
        orderDiv.appendChild(statusLabel);

        const columnDiv = document.createElement("div");
        columnDiv.className = "columnDiv";

        const customerNameDiv = document.createElement("div");

                const customerNameLabel = document.createElement("label");
                customerNameLabel.textContent = "Name:";

                const customerNameInput = document.createElement("input");               
                customerNameInput.disabled = true;
                customerNameInput.value = order.customerName;

            customerNameDiv.appendChild(customerNameLabel);
            customerNameDiv.appendChild(customerNameInput);
            columnDiv.appendChild(customerNameDiv);  

        const customerPhoneDiv = document.createElement("div");

                const customerPhoneLabel = document.createElement("label");
                customerPhoneLabel.textContent = "Phone:";

                const customerPhoneInput = document.createElement("input");               
                customerPhoneInput.disabled = true;
                customerPhoneInput.value = order.customerPhone;

            customerPhoneDiv.appendChild(customerPhoneLabel);
            customerPhoneDiv.appendChild(customerPhoneInput);
        columnDiv.appendChild(customerPhoneDiv);
        orderDiv.appendChild(columnDiv);

        const alignDiv = document.createElement("div")
        alignDiv.style.padding = "20px";

            const customerEmailDiv = document.createElement("div");         

                const customerEmailLabel = document.createElement("label");
                customerEmailLabel.textContent = "E-mail:";

                const customerEmailInput = document.createElement("input");          
                customerEmailInput.disabled = true;
                customerEmailInput.value = order.customerEmail;

                customerEmailDiv.appendChild(customerEmailLabel);
                customerEmailDiv.appendChild(customerEmailInput);

            alignDiv.appendChild(customerEmailDiv);

        orderDiv.appendChild(alignDiv);

        const buttonDiv = document.createElement("div"); 
                buttonDiv.className = "buttonDiv"; 
        
                    const completeOrder = document.createElement('button');
                    completeOrder.className = "action-button";
                    completeOrder.textContent = "Complete";
                    completeOrder.onclick = async function() {
                        setPaymentStatus("COMPLETED",order.orderId);
                    };

                    
                    const checkUserInfo = document.createElement('button');
                    checkUserInfo.className = "action-button";
                    checkUserInfo.textContent = "User Profile";
                    checkUserInfo.style.background = "rgb(139, 125, 0)";
                    checkUserInfo.onclick = async function() {
                        checkUserProfile(order.userUUID)
                    };
        
                    const cancelOrder = document.createElement('button');
                    cancelOrder.className = "action-button";
                    cancelOrder.style.background = "rgb(139, 0, 0)";
                    cancelOrder.textContent = "Cancel";
                    cancelOrder.onclick = async function() {
                        setPaymentStatus("CANCELLED",order.orderId);
                    };

                    const setToPaid = document.createElement('button');
                    setToPaid.className = "action-button";                   
                    setToPaid.textContent = "Set To Paid";
                    setToPaid.onclick = async function() {
                        setPaymentStatus("PAID",order.orderId);
                    };                
                 
                buttonDiv.appendChild(cancelOrder); 
                buttonDiv.appendChild(checkUserInfo);  
                buttonDiv.appendChild(setToPaid); 
                buttonDiv.appendChild(completeOrder);   
                           
                
        
                orderDiv.appendChild(buttonDiv);

                const messagePar = document.createElement('p');
                messagePar.id = "orderMessage" + order.orderId;   
                
                orderDiv.appendChild(messagePar);


        mainDiv.appendChild(orderDiv);
    
}


async function setPaymentStatus(paymentStatus, orderID){

    let jwToken = getCookie("jwToken");      

    let response = await getAPI(javaURL + "/employee/set/" + paymentStatus +"/order/" + orderID, jwToken); 

    let responseData;
    if (response && response.ok) {       
        responseData = await response.text();  
        document.getElementById('orderMessage' + orderID).textContent = responseData;   

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('orderMessage' + orderID).textContent = "Payment Status was not changed!";   
        return;
    }

}


async function checkUserProfile(userUUID) {   
    window.location.href = "/profile.html?userUUID=" + userUUID;    
}
