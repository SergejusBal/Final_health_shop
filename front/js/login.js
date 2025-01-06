var javaURL = "http://localhost:8080";

document.addEventListener('DOMContentLoaded', async function () {
  
    await refrechJWTCookies();    
    if (checkRole()) {
        loadUserElements();
    }

    setInterval(() => {
        refrechJWTCookies();
    }, 600000);
});

async function refrechJWTCookies() {
    let jwToken = getCookie("jwToken");
    let response = await getAPI(javaURL + "/noFilter/refresh", jwToken);

    if (response && response.ok) {
        const textData = await response.text();
        setCookie("jwToken",textData,1);        
    } else {
        console.error("Response:", response ? response.status : "No response");
    }
}

 function checkRole() {
    const role = getRole();

    if (!role) {
        console.error("No valid role found.");
        return false;
    }

    const allowedRoles = ["ADMIN", "REGULAR", "EMPLOYEE"];
    return allowedRoles.includes(role);
}

 function getRole() {
    let jwToken = getCookie("jwToken");

    if (!jwToken) {
        console.error("No JWT token found.");
        return null;
    }

    try {
        const claims = parseJwt(jwToken);
        return claims.role || null;
    } catch (error) {
        console.error("JWT parse error:", error);
        return null;
    }
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

async function loadUserElements() {
    
    let role = await getRole();
    if (role === "ADMIN") {
          loadAdmin();
    } else if (role === "EMPLOYEE") {
          loadEmployee();
    } else if (role === "REGULAR") {
        loadRegular();
    } else {
        return;
    }
    document.getElementById('signInButton').textContent = "Log out"; 
}

function loadAdmin() {
    document.getElementById('hide-settings').style.display = 'block';
    document.getElementById('hide-admin').style.display = 'block';
    document.getElementById('hide-employee').style.display = 'block';
}

function unloadAll() {
    document.getElementById('hide-settings').style.display = 'none';
    document.getElementById('hide-admin').style.display = 'none';
    document.getElementById('hide-employee').style.display = 'none';
}

function loadEmployee() {
    document.getElementById('hide-settings').style.display = 'block';
    document.getElementById('hide-employee').style.display = 'block';
}

function loadRegular(){
    document.getElementById('hide-settings').style.display = 'block';
}


function openSignIn(){

    let loginButtonValue = document.getElementById('signInButton').textContent;

    if(loginButtonValue == "Log out") {
        deleteCookie("jwToken");
        deleteCookie("username");
        document.getElementById('signInButton').textContent = "Sign in";
        refrechJWTCookies();
        unloadAll();
        window.location.href = "index.html";
        return;
    }

    document.getElementById('SignInpopup').style.display = 'flex';
}

function closeSignIn(){
    document.getElementById('SignInpopup').style.display = 'none';
    document.getElementById('signIn_message').textContent = "";  
}


async function signIn(){   

    let jwToken = getCookie("jwToken"); 
    let userJSon = getUserCredentials();    
    let response = await postAPI(javaURL + "/guest/login", userJSon , jwToken);

    if (response && response.ok) {       
        const textData = await response.text();
        setCookie("jwToken",textData,1);    
        loadUserElements();

        document.getElementById('username').value = "";
        document.getElementById('password').value = "";  
          
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('signIn_message').textContent = "Invalid Username or Password!";   
        return;
    }

    document.getElementById('signIn_message').value = "";
    closeSignIn();
}

function getUserCredentials() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;
   
    setCookie("username",username,1); 

    const userJson = JSON.stringify({
        "username": username,
        "password": password
    });
    
    return userJson;
}



async function register(){

    let jwToken = getCookie("jwToken"); 

    if (!checkIfUserInfoValid()){
        document.getElementById('signIn_message').textContent = "Invalid Username or Password!";
        return;
    } 

    let userJSon = getUserCredentials();    
    let response = await postAPI(javaURL + "/guest/register", userJSon , jwToken);


    if(response && response.status == 409){
        document.getElementById('signIn_message').textContent = "User already exists!?.";
        return;   
    }
    else if (response && response.ok) {       
        const textData = await response.text();
        signIn();
        return;
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('signIn_message').textContent = "Connection error!";   
        return;
    }   

}

function checkIfUserInfoValid(){

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(username)) {
        return false; 
    }

    const passwordRegex = /^(?=.*\d).{8,}$/;
    if (!passwordRegex.test(password)) {
        return false; // Password is invalid
    }

    return true;
}




