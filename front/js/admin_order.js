async function getLast20Orders(){
    
    let jwToken = getCookie("jwToken"); 
    
    let orderstatus = document.getElementById("orderstatus").value;      

    let response = await getAPI(javaURL + "/order/secured/get/all/" + orderstatus + "?limit=20&offset=0", jwToken); 

    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('orderMessage').textContent = "No orders Found!";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('orderMessage').textContent = "Error during Fetch!";   
        return null;
    }

};

async function getOrders() {

    document.getElementById("order-Div").innerHTML = "";  

    let jSonData = await getLast20Orders();
    if(!jSonData) return;   
   
    jSonData.forEach(order => {
        addDiv(order);
    });

    
}

function addDiv(order){
    const mainDiv = document.getElementById("order-Div");
        
        const orderDiv = document.createElement("div");
        orderDiv.className = "orderCard";

            const columnDiv = document.createElement("div");
            columnDiv.className = "columnDiv";
            
                
                const orderIDDiv = document.createElement("div");

                const orderIDLabel = document.createElement("label");                
                orderIDLabel.textContent = "Order ID:";

                const idInput = document.createElement("input");
                idInput.id = "customerID" + order.id;
                idInput.value = order.id;
                idInput.readOnly = true;

                orderIDDiv.appendChild(orderIDLabel);
                orderIDDiv.appendChild(idInput);
                columnDiv.appendChild(orderIDDiv);

                
                const customerNameDiv = document.createElement("div");

                const customerNameLabel = document.createElement("label");
                customerNameLabel.textContent = "Customer Name:";

                const customerNameInput = document.createElement("input");
                customerNameInput.id = "customerName" + order.id;
                customerNameInput.value = order.customerName;

                customerNameDiv.appendChild(customerNameLabel);
                customerNameDiv.appendChild(customerNameInput);
                columnDiv.appendChild(customerNameDiv);

                
                const customerEmailDiv = document.createElement("div");

                const customerEmailLabel = document.createElement("label");

                customerEmailLabel.textContent = "Customer Email:";
                const customerEmailInput = document.createElement("input");

                customerEmailInput.id = "customerEmail" + order.id;
                customerEmailInput.value = order.customerEmail;

                customerEmailDiv.appendChild(customerEmailLabel);
                customerEmailDiv.appendChild(customerEmailInput);
                columnDiv.appendChild(customerEmailDiv);

                
                const customerAddressDiv = document.createElement("div");
                
                const customerAddressLabel = document.createElement("label");
                customerAddressLabel.textContent = "Customer Home Address:";

                
                const customerAddressInput = document.createElement("input");
           
                customerAddressInput.id = "customerAddress" + order.id;
                customerAddressInput.value = order.customerAddress;

               
                customerAddressDiv.appendChild(customerAddressLabel);
                customerAddressDiv.appendChild(customerAddressInput);

                
                columnDiv.appendChild(customerAddressDiv);

                
                const customerPhoneDiv = document.createElement("div");

                const customerPhoneLabel = document.createElement("label");
                customerPhoneLabel.textContent = "Customer Phone:";

                const customerPhoneInput = document.createElement("input");
                customerPhoneInput.id = "customerPhone" + order.id;                
                customerPhoneInput.value = order.customerPhone;

                customerPhoneDiv.appendChild(customerPhoneLabel);
                customerPhoneDiv.appendChild(customerPhoneInput);
                columnDiv.appendChild(customerPhoneDiv);

                
                const paymentStatusDiv = document.createElement("div");

                const paymentStatusLabel = document.createElement("label");
                paymentStatusLabel.textContent = "Payment Status:";

                const paymentStatusInput = createPaymentStatusField(order.id, order.paymentStatus);    

                paymentStatusDiv.appendChild(paymentStatusLabel);
                paymentStatusDiv.appendChild(paymentStatusInput);
                columnDiv.appendChild(paymentStatusDiv);

                
                const promoCodeDiv = document.createElement("div");
                const promoCodeLabel = document.createElement("label");                                
                promoCodeLabel.textContent = "Promo Code:";

                const promoCodeInput = document.createElement("input");
                promoCodeInput.id = "promoCode" + order.id;
                promoCodeInput.value = order.promoCode;

                promoCodeDiv.appendChild(promoCodeLabel);
                promoCodeDiv.appendChild(promoCodeInput);
                columnDiv.appendChild(promoCodeDiv);

                
                const userUUIDDiv = document.createElement("div");
                const userUUIDLabel = document.createElement("label");                
                userUUIDLabel.textContent = "User UUID:";

                const userUUIDInput = document.createElement("input");
                userUUIDInput.id = "userUUID" + order.id;
                userUUIDInput.value = order.userUUID;

                userUUIDDiv.appendChild(userUUIDLabel);
                userUUIDDiv.appendChild(userUUIDInput);
                columnDiv.appendChild(userUUIDDiv);

        orderDiv.appendChild(columnDiv);

        const cartTable = createTable(order.orderCart);

        orderDiv.appendChild(cartTable);

        const buttonDiv = document.createElement("div"); 
        buttonDiv.className = "buttonDiv";

            const updateOrder = document.createElement('button');
            updateOrder.className = "action-button";
            updateOrder.textContent = "Update Order";
            updateOrder.onclick = async function() {
                await setUpdateOrder(order.id);
            };

            const completeOrder = document.createElement('button');
            completeOrder.className = "action-button";
            completeOrder.textContent = "Complete Order";
            completeOrder.onclick = async function() {
                await setCompleteOrder(order.id);
            };

            const cancelOrder = document.createElement('button');
            cancelOrder.className = "action-button";
            cancelOrder.style.background = "rgb(139, 0, 0)";
            cancelOrder.textContent = "Cancel Order";
            cancelOrder.onclick = async function() {
                await setCancelOrder(order.id);
            };
        
        buttonDiv.appendChild(cancelOrder);    
        buttonDiv.appendChild(updateOrder);
        buttonDiv.appendChild(completeOrder);  

        orderDiv.appendChild(buttonDiv);


        const messagePar = document.createElement('p');
        messagePar.id = "orderMessage" + order.id;   
        
        orderDiv.appendChild(messagePar);

        const orderCart = document.createElement("input");
        orderCart.id = "orderCart" + order.id;
        orderCart.type = "hidden";
        orderCart.value = order.orderCart;

        orderDiv.appendChild(orderCart);




    mainDiv.appendChild(orderDiv);

}

function createPaymentStatusField(id, paymentStatus){

    const paymentStatusSelect = document.createElement("select");
    paymentStatusSelect.id = "orderstatus" + id;


    const options = [
        { value: "PENDING", text: "Pending" },
        { value: "COMPLETED", text: "Completed" },
        { value: "FAILED", text: "Failed" },
        { value: "PAID", text: "Paid" },
        { value: "CANCELLED", text: "Cancelled" },
        { value: "REFUNDED", text: "Refunded" }
    ];

    options.forEach(option => {
        const opt = document.createElement("option");
        opt.value = option.value;
        opt.textContent = option.text;
        if (option.value === paymentStatus) {
            opt.selected = true; 
        }
        paymentStatusSelect.appendChild(opt);
    });

    return paymentStatusSelect;

}

function createTable(jsonString) {

    const table = document.createElement("table");;
    table.className = "tableStyle";
    
    const headerRow = document.createElement("tr");
    const headers = ["Product Name", "Category", "Quantity", "Product ID"];
    headers.forEach(headerText => {
        const th = document.createElement("th");
        th.textContent = headerText;       
        headerRow.appendChild(th);
    });
    table.appendChild(headerRow);


    try {
        orderCart = JSON.parse(jsonString); 
    } catch (error) {
        orderCart = table; 
    }
    
    orderCart.forEach(product => {
        const row = document.createElement("tr");
       
        const cells = [
            product.name,
            product.category,
            product.quantity,
            product.productId
        ];

        cells.forEach(cellText => {
            const td = document.createElement("td");
            td.textContent = cellText;           
            row.appendChild(td);
        });

        table.appendChild(row);
    });
   
    return table;
}


async function setUpdateOrder(id){

    const confirmUpload = confirm('Are you sure you want to update this product?');
    if(!confirmUpload) return;

    let jwToken = getCookie("jwToken"); 
    
    let orderDetailsJson = getOrderDatails(id);  

    let response = await putAPI(javaURL + "/order/secured/update", orderDetailsJson , jwToken); 

    let textData;
    if (response && response.ok) {       
        textData = await response.text();       
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('orderMessage' + id).textContent = "Error during update!";   
        return;
    }

    document.getElementById('orderMessage' + id).textContent = textData;   
    

}

async function setCompleteOrder(id){

    const confirmUpload = confirm('Are you sure you want to Complete order?');
    if(!confirmUpload) return;

    await setPaymentStatus("COMPLETED",id);
    
}

async function setCancelOrder(id){

    const confirmUpload = confirm('Are you sure you want to Cancel order?');
    if(!confirmUpload) return;

    await setPaymentStatus("CANCELLED",id);  

}

async function setPaymentStatus(paymentStatus, id){

    let jwToken = getCookie("jwToken");      

    let response = await getAPI(javaURL + "/order/secured/set/" + paymentStatus +"/order/" + id, jwToken); 

    let responseData;
    if (response && response.ok) {       
        responseData = await response.text();                   
        document.getElementById("orderstatus" + id).value = paymentStatus;
        document.getElementById('orderMessage' + id).textContent = responseData;   

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('orderMessage' + id).textContent = "Payment Status was not changed!";   
        return;
    }

}


function getOrderDatails(id){
    
    const customerName = document.getElementById('customerName' + id).value;
    const customerEmail = document.getElementById('customerEmail' + id).value;
    const customerAddress = document.getElementById('customerAddress' + id).value;
    const orderCart = document.getElementById('orderCart' + id).value;
    const paymentStatus = document.getElementById('orderstatus' +id).value;
    const promoCode = document.getElementById('promoCode' + id).value;
    const customerPhone = document.getElementById('customerPhone' + id).value;
    const userUUID = document.getElementById('userUUID' +id).value;  
   
   const orderJson = JSON.stringify({
        "id": id,
        "customerName": customerName,
        "customerEmail": customerEmail,
        "customerAddress": customerAddress,
        "orderCart": orderCart,
        "paymentStatus": paymentStatus,
        "promoCode": promoCode,
        "customerPhone": customerPhone,
        "userUUID": userUUID
    });  
    
    return orderJson;


}