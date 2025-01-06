

document.addEventListener('DOMContentLoaded', async function () {  
    await setToWebSocketChooces();
    loadRegisterPage();
});

document.getElementById("serviceCategories").addEventListener('click', function () {
    loadRegisterPage(); 
});


async function setToWebSocketChooces() {

    let data = await getWebsocketMap()
    const selectElement = document.getElementById("serviceCategories"); 

   
    const entries = Object.entries(data);

    for (const entry of entries) {
        const websocketKey = entry[0]; 
        const serviceName = entry[1]; 

        const option = document.createElement("option");
        option.value = websocketKey; 
        option.textContent = serviceName;
        selectElement.appendChild(option);  
         }   

    const firstServiceKey = Object.keys(data)[0];
    if (firstServiceKey) {
        selectElement.value = firstServiceKey; 
    }
}

function signInRegister(){
    openSignIn();    

    let timer = setInterval(function() {
        let flag = checkRole();
        if (flag) {          
            loadRegisterPage();
            clearInterval(timer);
        }
        else {           
            loadRegisterPage();            
        }
    }, 2000);    
}

async function loadRegisterPage(){

    let websocketID = document.getElementById("serviceCategories").value;

    if (checkRole()) {
        document.getElementById("logOutState").style.display = "none";
        document.getElementById("logInState").style.display = "flex";
        connectToWebSocket(websocketID);
        loadTable(websocketID);
    }
    else{
        return;
    }    
}

function connectToWebSocket(websocketID){
    let jwToken = getCookie("jwToken");   
    joinCalendar(websocketID, jwToken);
}


function sendToWebSocket(message){  
    
    let websocketID = document.getElementById("serviceCategories").value;

    let jwToken = getCookie("jwToken");   
    sendDate(websocketID,jwToken,message);
}


async function loadTable(websocketID){
    let dateMap = await getDates(websocketID);   
    createTable(dateMap)

}


async function getWebsocketMap(){

    let jwToken = getCookie("jwToken");  
    let response = await getAPI(javaURL + "/product/public/get/weSockets" , jwToken); 

    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('celandarMessage').textContent = "No available services found";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('celandarMessage').textContent = "Ops something went wrong! Sorry!";   
        return null;
    }
};


async function getDates(websocketID){

    let jwToken = getCookie("jwToken");  
    let response = await getAPI(javaURL + "/date/get/" + websocketID, jwToken); 

    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('celandarMessage').textContent = "No available dates found";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('celandarMessage').textContent = "Ops something went wrong! Sorry!";   
        return null;
    }
};

function createTable(data) {
    const table = document.getElementById("dateTable");   
    table.innerHTML = "";   
   
    const dates = getUniqueDates(data);
    const times = getUniqueTimes(data);
   
    createHeaderRow(table, dates);

    createTimeRows(table, dates, times, data);
}

function getUniqueDates(data) {
    const keys = Object.keys(data); 
    const datesOnly = keys.map(key => key.split("T")[0]); 
    const uniqueDates = new Set(datesOnly); 
    const sortedDates = Array.from(uniqueDates).sort(); 
    return sortedDates; 
}

function getUniqueTimes(data) {
    const keys = Object.keys(data); 
    const timesOnly = keys.map(key => key.split("T")[1]); 
    const uniqueTimes = new Set(timesOnly); 
    const sortedTimes = Array.from(uniqueTimes).sort(); 
    return sortedTimes; 
}


function createHeaderRow(table, dates) {
    const headerRow = table.insertRow();
    dates.forEach(date => {
        const th = document.createElement("th");
        th.textContent = date.substring(5);
        headerRow.appendChild(th);
    });
}


function createTimeRows(table, dates, times, data) {

    let hiddenContainer = document.getElementById("hiddenValueContainer");
    times.forEach(time => {
        const row = table.insertRow();
        dates.forEach(date => {
            const cell = row.insertCell();
            const key = date + "T" + time;
            cell.id = key;
           
            let hiddenValue = document.createElement('input');
            hiddenValue.type = "hidden";
            hiddenValue.value = data[key]; 
            hiddenValue.id = "hidden" + key;
            hiddenContainer.appendChild(hiddenValue);
           
            setCellContent(cell, data, key, time);
        });
    });
}

function setCellContent(cell, data, key, time) {
    cell.textContent = time;

    cell.onclick  = async function() {
        registerCellWasClicked(key)        
    }; 

    if (!(data[key] === null)) {
        if (getUserUUID() === data[key]) {
            cell.style.backgroundColor = "green";
        } else {
            cell.style.backgroundColor = "red";
        }
    }
}

function registerCellWasClicked(key) {   

    const hiddenInput = document.getElementById("hidden" + key).value;  
    
    if (!hiddenInput || hiddenInput.trim() === "" || hiddenInput == getUserUUID()) {
        modifyRegistration(key);
    }        
    else{
        console.log("Invalid Field Clicked");
    }   
}


function modifyRegistration(key){
    
    const message = JSON.stringify({
        "userUUID": getUserUUID(),
        "reservedTime": key
    });

    sendToWebSocket(message);
}


function calendarMessageResieved(messageData){

    if(messageData.reservedTime == null || messageData.reservedTime.trim() == "") return;

    let key  = messageData.reservedTime.substring(0,16)

    let cell = document.getElementById(key);
    let hiddenValue = document.getElementById("hidden" + key);

    if(hiddenValue.value == messageData.userUUID){
         hiddenValue.value = "";
         cell.style.backgroundColor = "";
    }
    else if(messageData.userUUID == getUserUUID()){
        cell.style.backgroundColor = "green";
        hiddenValue.value = messageData.userUUID;
    }else{
        cell.style.backgroundColor = "red";
        hiddenValue.value = messageData.userUUID;
    } 
}



async function addServiceToCartClick(){

    let selectElement = document.getElementById("serviceCategories");
    let productName = selectElement.options[selectElement.selectedIndex].text;
    

    let product = await getProductByName(productName);
    console.log(product);
    await addProductToCart(product.id, product.price,product.category, product.name);
    window.location.href = 'cart_page.html';

}


async function getProductByName(productName){

    let jwToken = getCookie("jwToken");    

    let response = await getAPI(javaURL + "/product/public/precise/" + productName, jwToken);     
    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('celandarMessage').textContent = "No available services found";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('celandarMessage').textContent = "Ops something went wrong! Sorry!";   
        return null;
    }
};




async function addProductToCart(id, price, category, productName) {
   
    let jsonStringItemCart = await getCookie("productCart");

    let tempCart = jsonStringItemCart ? JSON.parse(jsonStringItemCart) : []; 
    
    let productIndex = findProductIndex(tempCart, id);

    if (productIndex !== -1) {    
        if (category === "Service") return;    
        tempCart[productIndex].quantity++;
    } else {        
        tempCart.push({
            name: productName,
            productId: id,
            category: category,
            price: price, 
            quantity: 1
        });
    }
   
    await setCookie("productCart", JSON.stringify(tempCart), 7);
}


function findProductIndex(cart, productId) {
    for (let i = 0; i < cart.length; i++) {
        if (cart[i].productId === productId) {
            return i;
        }
    }
    return -1;
}


