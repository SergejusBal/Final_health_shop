
async function getProductID(){
    let jwToken = getCookie("jwToken"); 
    let productName = document.getElementById("productName").value;

    let response = await getAPI(javaURL + "/product/public/id/name/" + productName, jwToken); 

    let textData;
    if (response && response.ok) {       
        textData = await response.text(); 
        return textData;
    } else {
        console.error("Response:", response ? response.status : "No response");        
        return;
    }

}

async function getProduct(){
    let jwToken = getCookie("jwToken"); 
    
    let productID = await getProductID();   
    if(productID == null) {
        document.getElementById('productMessage').textContent = "Error during ID Fetch! (Name not Found)";   
        return;
    }

    let response = await getAPI(javaURL + "/product/public/" + productID, jwToken); 

    let jSonData;
    if (response && response.ok) {       
        jSonData = await response.json();        
        setProductDetails(jSonData);

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('productMessage').textContent = "Error during Fetch!";   
        return;
    }

}

async function deleteProduct(){

    const confirmUpload = confirm('Are you sure you want to delete this product?');
    if(!confirmUpload) return;        


    let jwToken = getCookie("jwToken");  
    let productname = document.getElementById("productName").value;  
    
    let response = await deleteAPI(javaURL + "/product/secured/delete/" + productname, jwToken); 
   
    let textDate;
    if (response && response.ok) {       
        textDate = await response.text(); 
        document.getElementById('productMessage').textContent = textDate;   
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('productMessage').textContent = "Error during delete operation (Name not Found)!";   
        return;
    }

}

function setProductDetails(jSonData) {

    document.getElementById("hiddenIDInput").value = jSonData.id;   
    document.getElementById("productName").value = jSonData.name;
    document.getElementById("productDescription").value  = jSonData.description;
    document.getElementById("productPrice").value = jSonData.price;
    document.getElementById("productCategory").value = jSonData.category;
    document.getElementById("hiddenInput").value = jSonData.imageUrl;
    
}




async function uploadProduct(){

    const confirmUpload = confirm('Are you sure you want to create this product?');
    if(!confirmUpload) return;        

    let jwToken = getCookie("jwToken");   
    let userJSon = getPoroductDetails();    
    let response = await postAPI(javaURL + "/product/secured/new", userJSon , jwToken); 

    let textData;
    if (response && response.ok) {       
        textData = await response.text(); 
        document.getElementById('productMessage').textContent = textData;         
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('productMessage').textContent = "Error during upload!";   
        return;
    }

    document.getElementById('productMessage').value = textData;
}

async function updateProduct(){

    const confirmUpload = confirm('Are you sure you want to update this product?');
    if(!confirmUpload) return;

    let jwToken = getCookie("jwToken"); 
    let productID = document.getElementById("hiddenIDInput").value;
    let userJSon = getPoroductDetails();    
    let response = await putAPI(javaURL + "/product/secured/update/" + productID, userJSon , jwToken); 

    let textData;
    if (response && response.ok) {       
        textData = await response.text();       
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('productMessage').textContent = "Error during update!";   
        return;
    }

    document.getElementById('productMessage').value = textData;
}


function getPoroductDetails() {
    const productName = document.getElementById("productName").value;
    const productDescription = document.getElementById("productDescription").value;
    const productPrice = document.getElementById("productPrice").value;
    const productCategory = document.getElementById("productCategory").value;
    const imageInput = document.getElementById("hiddenInput").value;   
 
    const userJson = JSON.stringify({
        "name": productName,
        "description": productDescription,
        "price": productPrice,
        "category": productCategory,
        "imageUrl": imageInput
    });

    document.getElementById("productName").value = "";
    document.getElementById("productDescription").value = "";
    document.getElementById("productPrice").value = "";
    document.getElementById("productCategory").value = ""; 
    
    return userJson;
}



