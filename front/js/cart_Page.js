document.addEventListener('DOMContentLoaded', async function () {
  
    populateShoppingTable();
});

async function clearCart(){
    deleteCookie("productCart");
    populateShoppingTable();
}


async function populateShoppingTable() {
  
    let cookieValue = await getCookie("productCart");

    let productCart = cookieValue ? JSON.parse(cookieValue) : [];
    
    let shoppingTable = document.getElementById("shoppingTable");
   
    while (shoppingTable.rows.length > 1) {
        shoppingTable.deleteRow(1);
    }

   
    for (let i = 0; i < productCart.length; i++) {
        let product = productCart[i];

        
        let row = shoppingTable.insertRow();

        
        let cellIndex = row.insertCell(0);
        let cellName = row.insertCell(1);
        cellName.style.textAlign = "left";
        let cellTax = row.insertCell(2);
        let cellPrice = row.insertCell(3);
        let cellAmount = row.insertCell(4);
        let cellTotal = row.insertCell(5);

       
        let tax = (product.price * 0.21).toFixed(2); 
        let total = (product.price * product.quantity).toFixed(2);
        
        cellIndex.textContent = i + 1; 
        cellName.textContent = product.name + " " + product.productId;
        cellTax.textContent = tax;
        cellPrice.textContent = product.price.toFixed(2);

        cellAmount = fillQuantityCell(cellAmount, product);

        cellTotal.textContent = total;
    }

    let row = shoppingTable.insertRow(); 

    for (let i = 0; i < 6; i++) {
    let cell = row.insertCell(i); 
    if(i == 4){
    cell.textContent = "Total Price: "
    cell.style.textAlign = "left";
    cell.style.fontWeight = "bold";
    } 
    if(i == 5){
        let totalPrice = calculateTotalPrice(productCart);
        cell.textContent = totalPrice + " €";
        cell.style.fontWeight = "bold";        
    }
    
}

}

function fillQuantityCell(cellAmount,product){

    let decrementButton = document.createElement("button");
    decrementButton.textContent = "-"
    decrementButton.classList = "action-button-cart";
    decrementButton.onclick = async function() {
        await modifyCartDecreaseQuantity(product.productId);        
        populateShoppingTable();
    }; 

    let cellQuantity = document.createElement("span");
    cellQuantity.textContent = product.quantity;

    let incrementButton = document.createElement("button");
    incrementButton.textContent = "+";
    incrementButton.classList = "action-button-cart";
    incrementButton.onclick = async function() {
        await modifyCartIncreaseQuantity(product.productId);        
        populateShoppingTable();
    }; 

    cellAmount.appendChild(decrementButton);
    cellAmount.appendChild(cellQuantity);
    cellAmount.appendChild(incrementButton);

    cellAmount.style.display = "flex";
    cellAmount.style.justifyContent = "space-between";
    cellAmount.style.alignItems = "center";


    return cellAmount;
}

function calculateTotalPrice(productCart) {
    let totalPrice = 0;
    for (let product of productCart) {
        totalPrice += product.price * product.quantity;
    }
    return totalPrice.toFixed(2); 
}


async function modifyCartIncreaseQuantity(id) {
    
    let jsonStringItemCart = await getCookie("productCart");
    let tempCart = jsonStringItemCart ? JSON.parse(jsonStringItemCart) : [];

   
    let productIndex = findProductIndex(tempCart, id);

    if (productIndex !== -1) {        
        tempCart[productIndex].quantity++;
    }
    
    await setCookie("productCart", JSON.stringify(tempCart), 7);
}

async function modifyCartDecreaseQuantity(id) {

    let jsonStringItemCart = await getCookie("productCart");
    let tempCart = jsonStringItemCart ? JSON.parse(jsonStringItemCart) : [];

   
    let productIndex = findProductIndex(tempCart, id);

    if (productIndex === -1) {
        return;
    }

    tempCart[productIndex].quantity--;
    
    if (tempCart[productIndex].quantity <= 0) {
        tempCart.splice(productIndex, 1);
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


async function checkOut(){

    let orderID = await createOrder();
    if (orderID == 0 || !orderID) return;

    let jwToken = getCookie("jwToken");    
    
    let response = await postAPI(javaURL + "/stripe/pay/" + orderID, null , jwToken);     
    
    if (response && response.ok) {  
    const session = await response.text(); 
    const sessionId = session;
    const stripe = Stripe('pk_test_51PlENQHWGvvl25KmMUuPtb9iFyXtRJt8Xf1ttsKjNS4ryOkdvZz2FNrwr0KCNBKTQvBiCeOER6LxNUbY8KVQklkW00fZHeGmb4');
    
    deleteCookie("productCart");  
    const { error } = await stripe.redirectToCheckout({ sessionId });
    if (error) {
        console.error("Stripe Checkout error:", error.message);
        }   
    }     
}


async function createOrder() {    

    if(!areCustomerDetailsFilled()) {
        document.getElementById('cartMessage').textContent = "Not all required fields are filled.."
        return;
    }
    
    let orderJson = await fillOrderDetails();
    let jwToken = getCookie("jwToken");  
    let response = await postAPI(javaURL + "/order/public/new", orderJson , jwToken); 

    let textData;
    if (response && response.ok) {       
        textData = await response.text(); 

       return textData;       
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('cartMessage').textContent = "Check Out Stopped!";   
        return;
    }    
    
}


function areCustomerDetailsFilled() {
    const customerName = document.getElementById('customerName').value;
    const customerEmail = document.getElementById('customerEmail').value;
    const customerAddress = document.getElementById('customerAddress').value;     

    return customerName && customerEmail && customerAddress ? true : false;
}

async function fillOrderDetails(){
  
    const customerName = document.getElementById('customerName').value;
    const customerEmail = document.getElementById('customerEmail').value;
    const customerAddress = document.getElementById('customerAddress').value;    
    const promoCode = document.getElementById('promoCode').value;
    const customerPhone = document.getElementById('customerPhone').value;


    const orderCart = getCookie("productCart") || "[]";
    const userUUID = getUserUUID();     

    const orderJson = JSON.stringify({
        "customerName": customerName,
        "customerEmail": customerEmail,
        "customerAddress": customerAddress,
        "orderCart": orderCart,
        "userUUID": userUUID,
        "promoCode": promoCode,
        "customerPhone": customerPhone

    });    
    
    return orderJson;  
}


function getUserUUID() {

    let jwToken = getCookie("jwToken");

    if (!jwToken) {
        console.error("No JWT token found.");
        return null;
    }

    try {
        const claims = parseJwt(jwToken);
        return claims.userUUID || null;
    } catch (error) {
        console.error("JWT parse error:", error);
        return null;
    }
}