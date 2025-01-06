document.addEventListener('DOMContentLoaded', async function () {

    const url = new URL(window.location.href);   
    const params = new URLSearchParams(url.search);
    let paramUserUUID = params.get("userUUID");

    let userUUID;
    if(paramUserUUID){
        userUUID = paramUserUUID;
        document.getElementById("updateProfileButton").style.display="none";

    }
    else{
        userUUID = getUserUUID();
    }    

    getProfileDetails(userUUID);
  
});


async function getProfileDetails(userUUID){

    let jwToken = getCookie("jwToken");   

    let response = await getAPI(javaURL + "/user/get/profile/" + userUUID, jwToken);   

    let jSonData;
    if (response && response.status == 404){
        document.getElementById('profileMessage').textContent = "No profile Found!"; 
    }
    else if(response && response.status == 401){
        document.getElementById('profileMessage').textContent = "Unauthorized action!"; 
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        console.log(jSonData);
        emptyProfileDetails();
        fillProfileDetails(jSonData);    
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('profileMessage').textContent = "Error during Fetch!";   
        return;
    }

}

function emptyProfileDetails(){

    document.getElementById("phoneNumber").value = "";
    document.getElementById("address").value = "";
    document.getElementById("firstName").value = "";
    document.getElementById("lastName").value = "";
    document.getElementById("dateOfBirth").value = "";
    document.getElementById("height").value = "";
    document.getElementById("weight").value = "";
  
}


function fillProfileDetails(jSonData){
    document.getElementById("userUUID").value = jSonData.userUUID || "";
    document.getElementById("email").value = jSonData.email || "";
    document.getElementById("phoneNumber").value = jSonData.phoneNumber || "";
    document.getElementById("address").value = jSonData.address || "";
    document.getElementById("firstName").value = jSonData.firstName || "";
    document.getElementById("lastName").value = jSonData.lastName || "";
    document.getElementById("dateOfBirth").value = jSonData.dateOfBirth || "";
    document.getElementById("height").value = jSonData.height || "";
    document.getElementById("weight").value = jSonData.weight || "";
  
}



function getProfileDetailsJson() {

    let uuid = document.getElementById("userUUID").value;
    let email = document.getElementById("email").value;
    let phoneNumber = document.getElementById("phoneNumber").value;
    let address = document.getElementById("address").value;
    let firstName = document.getElementById("firstName").value;
    let lastName = document.getElementById("lastName").value;
    let dateOfBirth = document.getElementById("dateOfBirth").value;
    let height = document.getElementById("height").value;
    let weight = document.getElementById("weight").value;

    const profileJson = JSON.stringify({
        "userUUID": uuid,
        "email": email,
        "phoneNumber": phoneNumber,
        "address": address,
        "firstName": firstName,
        "lastName": lastName,
        "dateOfBirth": dateOfBirth,
        "height": parseFloat(height), 
        "weight": parseFloat(weight)  
    });

    return profileJson;
}


async function updateYourProfile(){

    let jwToken = getCookie("jwToken"); 

    let getProfileDetails = getProfileDetailsJson();  

    let response = await putAPI(javaURL + "/user/modify/profile", getProfileDetails, jwToken); 

    let textData;

    if(response && response.status == 404){
        document.getElementById('profileMessage').textContent = "Profile not found";   
        return;
    }
    else if(response && response.status == 401){
        document.getElementById('profileMessage').textContent = "Unauthorized action!"; 
    }
    else if (response && response.ok) {       
        textData = await response.text(); 
        document.getElementById('profileMessage').textContent = textData; 

        return;      
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById("profileMessage").textContent = "Error during update!";   
        return;
    }
  
}
