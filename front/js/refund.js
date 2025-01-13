async function refund(){

    let jwToken = getCookie("jwToken");   

    let refundKey = document.getElementById("refundKey").value;

    let response = await getAPI(javaURL + "/stripe/refund/key/" + refundKey, jwToken);   

    let textData;
    if (response && response.status == 404){
        document.getElementById('refundMessage').textContent = "No available refund found!"; 
    }
    else if(response && response.status == 401){
        document.getElementById('refundMessage').textContent = "Unauthorized action!"; 
    }
    else if (response && response.ok) {       
        textData = await response.text();        
        document.getElementById('refundMessage').textContent = textData; 
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('refundMessage').textContent = "Error during Fetch!";   
        return;
    }

}