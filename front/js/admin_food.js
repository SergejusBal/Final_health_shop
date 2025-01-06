


async function getFoodItem(){

    let jwToken = getCookie("jwToken");     

    let foodName = document.getElementById('foodNameInput').value;

    if(!foodName) {
        document.getElementById('foodMessage').textContent = "Please enter food name first!"; 
        return;
    }

    let response = await getAPI(javaURL + "/food/user/get/" + foodName, jwToken);   

    let jSonData;
    if (response && response.status == 204){
        document.getElementById('foodMessage').textContent = "Food not Found!"; 
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        console.log(jSonData);
        emptyFoodDetails();
        fillFoodDetails(jSonData);
    
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('foodMessage').textContent = "Error during Fetch!";   
        return;
    }

}

function emptyFoodDetails(){
    document.getElementById("caloriesInput").value ="";
    document.getElementById("proteinsInput").value ="";
    document.getElementById("fatsInput").value ="";
    document.getElementById("carbohydratesInput").value ="";
    document.getElementById("fibersInput").value ="";
}


function fillFoodDetails(jSonData){
    document.getElementById("caloriesInput").value =jSonData.calories;
    document.getElementById("proteinsInput").value =jSonData.proteins;
    document.getElementById("fatsInput").value =jSonData.fats;
    document.getElementById("carbohydratesInput").value =jSonData.carbohydrates;
    document.getElementById("fibersInput").value =jSonData.fibers;   
}



function getFoodDetails() {
    
    let food = document.getElementById("foodNameInput").value;
    let calories = document.getElementById("caloriesInput").value;
    let proteins = document.getElementById("proteinsInput").value;
    let fats = document.getElementById("fatsInput").value;
    let carbohydrates = document.getElementById("carbohydratesInput").value;
    let fibers = document.getElementById("fibersInput").value;   


    const foodJson = JSON.stringify({
        "food": food,
        "calories": calories,
        "proteins": proteins,
        "fats": fats,
        "carbohydrates": carbohydrates,
        "fibers": fibers
    });
    
    return foodJson;
}




async function updateFoodItem(){

    let jwToken = getCookie("jwToken"); 

    let foodDetailsJson = getFoodDetails();  

    let response = await putAPI(javaURL + "/food/secured/update", foodDetailsJson , jwToken); 

    let textData;

    if(response && response.status == 404){
        document.getElementById('foodMessage').textContent = "Food not found";   
        return;
    }
    else if (response && response.ok) {       
        textData = await response.text(); 
        document.getElementById('foodMessage').textContent = textData;  
        return;      
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById("foodMessage").textContent = "Error during update!";   
        return;
    }
  
}

async function createFoodItem(){

    let jwToken = getCookie("jwToken"); 

    let foodDetailsJson = getFoodDetails();  

    let response = await postAPI(javaURL + "/food/secured/new", foodDetailsJson , jwToken); 

    let textData;

    if (response && response.ok) {       
        textData = await response.text(); 
        document.getElementById('foodMessage').textContent = textData;  
        return;      
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById("foodMessage").textContent = "Error during create!";   
        return;
    }

}


async function deleteFoodItem(){

    const confirmUpload = confirm('Are you sure you want to delete this food?');
    if(!confirmUpload) return;        


    let jwToken = getCookie("jwToken");  

    let foodName = document.getElementById("foodNameInput").value;  
    
    let response = await deleteAPI(javaURL + "/food/secured/delete/" + foodName, jwToken); 
   
    let textDate;
    if(response && response.status == 404){
        document.getElementById('foodMessage').textContent = "Food not found";   
        return;
    }
    else if (response && response.ok) {       
        textDate = await response.text(); 
        document.getElementById('foodMessage').textContent = textDate;   
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('foodMessage').textContent = "Error during delete operation (Name not Found)!";   
        return;
    }


}



