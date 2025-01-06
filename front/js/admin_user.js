async function getUser(){
    
    let jwToken = getCookie("jwToken");    
   
    let username = document.getElementById('usernameInput').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please enter username first!"; 
        return;
    }

    let response = await getAPI(javaURL + "/admin/get/user/" + username, jwToken);   

    let jSonData;
    if (response && response.status == 204){
        document.getElementById('userMessage').textContent = "User not Found!"; 
    }
    else if (response && response.ok) {       
        jSonData = await response.json();  
        emptyDetails();
        fillDetails(jSonData);

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('userMessage').textContent = "Error during Fetch!";   
        return;
    }

}

async function fillDetails(jSonData) {
    document.getElementById('fetchUsername').value = jSonData.username;
    document.getElementById('fetchUUID').value = jSonData.uuid;
    document.getElementById('fetchRole').value = jSonData.role;
    document.getElementById('fetchRole2').value = jSonData.role;

    if(jSonData.privileges){
    document.getElementById('fetchPrivileges').value = jSonData.privileges;
    }

    if(jSonData.specializations){
    document.getElementById('fetchSpecialization').value = jSonData.specializations;    
    }

}

async function emptyDetails() {
    document.getElementById('fetchUsername').value = "";
    document.getElementById('fetchUUID').value = "";
    document.getElementById('fetchRole').value = "";
    document.getElementById('fetchRole2').value = "";  
    document.getElementById('fetchPrivileges').value = "";
    document.getElementById('fetchSpecialization').value = "";    

}


async function setRole(){

    let username = document.getElementById('fetchUsername').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please fetch user first!"; 
        return;
    }

    let userRole = document.getElementById('setRole').value; 
    console.log(userRole);
    if(!userRole){
        document.getElementById('userMessage').textContent = "Please select role first!"; 
        return;
    } 

    await setValue("/admin/modify/role/" + username + "/" + userRole);
    getUser();
}

async function setPrivilege(){
    
    let username = document.getElementById('fetchUsername').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please fetch user first!"; 
        return;
    }

    let userPrivilege = document.getElementById('setPrivilege').value; 

    if(!userPrivilege){
        document.getElementById('userMessage').textContent = "Please select privilege first!"; 
        return;
    } 

    await setValue("/admin/add/privileges/" + username + "/" + userPrivilege);
    getUser();

}


async function setSpecialization(){

    let username = document.getElementById('fetchUsername').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please fetch user first!"; 
        return;
    }

    let userSpecialization = document.getElementById('setSpecialization').value; 

    if(!userSpecialization){
        document.getElementById('userMessage').textContent = "Please select privilege first!"; 
        return;
    } 

    await setValue("/admin/add/specialization/" + username + "/" + userSpecialization);
    getUser();

}


async function setValue(url) {

    let jwToken = getCookie("jwToken");  

    let response = await putAPI(javaURL + url, null, jwToken);   

    let textContent;
    if (response && response.status == 204){
        document.getElementById('userMessage').textContent = "User not Found!"; 
    }
    else if (response && response.ok) {       
        textContent = await response.text();        
        document.getElementById('userMessage').textContent = textContent;   

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('userMessage').textContent = "Error during Fetch!";   
        return;
    }
    
}


async function deleteUser(){

    let username = document.getElementById('fetchUsername').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please fetch user first!"; 
        return;
    }    

    const confirmDelete = confirm('Are you sure you want to Delete user?');
    if(!confirmDelete) return;

    await deleteValue("/admin/delete/user/" + username);
    emptyDetails();
}


async function deleteSpecialization(){

    
    let username = document.getElementById('fetchUsername').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please fetch user first!"; 
        return;
    }

    let userSpecialization = document.getElementById('setSpecialization').value; 

    if(!userSpecialization){
        document.getElementById('userMessage').textContent = "Please select privilege first!"; 
        return;
    } 

    await deleteValue("/admin/delete/specialization/" + username + "/" + userSpecialization);
    getUser();

}


async function deletePrivilege(){

    let username = document.getElementById('fetchUsername').value;

    if(!username) {
        document.getElementById('userMessage').textContent = "Please fetch user first!"; 
        return;
    }

    let userPrivilege = document.getElementById('setPrivilege').value; 

    if(!userPrivilege){
        document.getElementById('userMessage').textContent = "Please select privilege first!"; 
        return;
    } 

    await deleteValue("/admin/delete/privileges/" + username + "/" + userPrivilege);
    getUser();

}


async function deleteValue(url) {

    let jwToken = getCookie("jwToken");  

    let response = await deleteAPI(javaURL + url, jwToken);   

    let textContent;
    if (response && response.status == 204){
        document.getElementById('userMessage').textContent = "User not Found!"; 
    }
    else if (response && response.ok) {       
        textContent = await response.text();        
        document.getElementById('userMessage').textContent = textContent;   

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('userMessage').textContent = "Error during Fetch!";   
        return;
    }
    
}