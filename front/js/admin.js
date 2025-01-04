function showModifyProducts(){
    hideAll();
    document.getElementById("product-controlPannel").style.display = "flex"; 

}
function showModifyUsers(){
    hideAll();
    document.getElementById("user-controlPannel").style.display = "flex"; 

}
function showModifyFootItems(){
    hideAll();
    document.getElementById("modifyFood-controlPannel").style.display = "flex"; 

}
function showCheckOrders(){
    hideAll();
    document.getElementById("order-controlPannel").style.display = "flex"; 
}

function hideAll(){
    document.getElementById("product-controlPannel").style.display = "none"; 
    document.getElementById("user-controlPannel").style.display = "none"; 
    document.getElementById("modifyFood-controlPannel").style.display = "none"; 
    document.getElementById("order-controlPannel").style.display = "none"; 
}