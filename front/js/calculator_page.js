
document.addEventListener('DOMContentLoaded', async function () {   
    loadRegisterPage();
});

async function loadRegisterPage(){


    if (checkRole()) {
        document.getElementById("logOutState").style.display = "none";
        document.getElementById("logInState").style.display = "flex";        
    }
    else{
        return;
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

function calculateYourCalories() {

   
    const weight = parseFloat(document.getElementById("weightInput").value);
    const height = parseFloat(document.getElementById("heightInput").value);
    const age = parseInt(document.getElementById("fetchUUID").value);
    const gender = parseInt(document.getElementById("genderValue").value);
    const activityLevel = parseFloat(document.getElementById("activityLvl").value);

    
    if (isNaN(weight) || isNaN(height) || isNaN(age) || isNaN(activityLevel)) {
        document.getElementById("calculateMessage").textContent = "Please fill in all fields correctly.";
        return;
    }

    document.getElementById("resultDiv").style.display = "block";
    document.getElementById("calculateMessage").textContent = "";
        
    let bmr = 0;
    if (gender === 1) {
        bmr = 10 * weight + 6.25 * height - 5 * age + 5;
    } else if (gender === 2) 
        { 
        bmr = 10 * weight + 6.25 * height - 5 * age - 161;
    }

    let tdee = 0;
    let proteins = 0;
    let fats = 0;
    let carbohydrates = 0;
    let fiber = 0;

    if (activityLevel === 1) {
        tdee = bmr * 1.4; 
        proteins = weight * 1.2;
        fats = (tdee * 0.30) / 9; 
        carbohydrates = (tdee - (proteins * 4 + fats * 9)) / 4; 
        fiber = (tdee / 1000) * 14; 
    } else if (activityLevel === 2) {
        tdee = bmr * 1.6; 
        proteins = weight * 1.4; 
        fats = (tdee * 0.30) / 9; 
        carbohydrates = (tdee - (proteins * 4 + fats * 9)) / 4; 
        fiber = (tdee / 1000) * 14; 
    } else if (activityLevel === 3) {
        tdee = bmr * 1.8; 
        proteins = weight * 1.8; 
        fats = (tdee * 0.30) / 9; 
        carbohydrates = (tdee - (proteins * 4 + fats * 9)) / 4; 
        fiber = (tdee / 1000) * 14; 
    } else if (activityLevel === 4) {
        tdee = bmr * 2.0; 
        proteins = weight * 2.2; 
        fats = (tdee * 0.35) / 9; 
        carbohydrates = (tdee - (proteins * 4 + fats * 9)) / 4; 
        fiber = (tdee / 1000) * 14; 
    }

   
    document.getElementById("caloriesValue").textContent = tdee.toFixed(1) + " kcal";
    document.getElementById("proteinsValue").textContent = proteins.toFixed(1)+  " g";
    document.getElementById("fatValue").textContent = fats.toFixed(1) + " g";
    document.getElementById("carbohydratesValue").textContent = carbohydrates.toFixed(1) + " g";
    document.getElementById("fiber").textContent = fiber.toFixed(1) +" g";    
 
}


async function checkCalories(){

    document.getElementById("foodresultDiv").style.display = "none";   

    let jwToken = getCookie("jwToken");     

    let foodName = document.getElementById('foodNameInput').value;

    if(!foodName) {
        document.getElementById('foodCalculationMessage').textContent = "Please enter food name first!"; 
        return;
    }

    let response = await getAPI(javaURL + "/food/user/get/" + foodName, jwToken);   

    let jSonData;

    if (response && response.status == 404){
        document.getElementById('foodCalculationMessage').textContent = foodName + " is not a food item!"; 
    }
    else if (response && response.status == 204){
        document.getElementById('foodCalculationMessage').textContent = "Food not Found!"; 
    }
    else if (response && response.ok) {       
        jSonData = await response.json();  
        document.getElementById('foodCalculationMessage').textContent = ""; 
        setFoodValues(jSonData);
        document.getElementById("foodresultDiv").style.display = "block";           
    
    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('foodCalculationMessage').textContent = "Error during Fetch!";   
        return;
    }

}

function setFoodValues(data) {
    
    document.getElementById("fetchFoodName").textContent = data.food ? "100 g of " + data.food.toLowerCase() + " contains:" : "Unknown Food";
    document.getElementById("hiddenfetchFoodName").textContent = data.food ? data.food: "Unknown Food";    
    
    document.getElementById("fetchCaloriesValue").textContent = (data.calories ? data.calories : "0.0") + " kcal";
    document.getElementById("fetchProteinsValue").textContent = (data.proteins ? data.proteins : "0.0") + " g";
    document.getElementById("fetchFatValue").textContent = (data.fats ? data.fats : "0.0") + " g";
    document.getElementById("fetchCarbohydratesValue").textContent = (data.carbohydrates ? data.carbohydrates : "0.0") + " g";
    document.getElementById("fetchFiber").textContent = (data.fibers ? data.fibers : "0.0") + " g";
}




function addThisProduct() {   
    
    const foodName = document.getElementById("hiddenfetchFoodName").textContent;
    const amount = parseFloat(document.getElementById("foodAmountInput").value);

    if (isNaN(amount) || amount <= 0) {
        document.getElementById('foodCalculationMessage').textContent = "Please enter a valid amount.";
        return;
    }

    document.getElementById("hiddenTableDiv").style.display = "block";
    document.getElementById("foodAmountInput").value = "";
   
    const calories = parseFloat(document.getElementById("fetchCaloriesValue").textContent) || 0;
    const proteins = parseFloat(document.getElementById("fetchProteinsValue").textContent) || 0;
    const fats = parseFloat(document.getElementById("fetchFatValue").textContent) || 0;
    const carbohydrates = parseFloat(document.getElementById("fetchCarbohydratesValue").textContent) || 0;
    const fiber = parseFloat(document.getElementById("fetchFiber").textContent) || 0;

    const factor = amount / 100;
    const adjustedCalories = (calories * factor).toFixed(2);
    const adjustedProteins = (proteins * factor).toFixed(2);
    const adjustedFats = (fats * factor).toFixed(2);
    const adjustedCarbohydrates = (carbohydrates * factor).toFixed(2);
    const adjustedFiber = (fiber * factor).toFixed(2);
    
    const tableBody = document.getElementById("bodyOfFoodItems");
        
    const newRow = tableBody.insertRow();
   
    const foodCell = newRow.insertCell(0);
    foodCell.style.fontWeight = "bold";
    const amountCell = newRow.insertCell(1);
    const caloriesCell = newRow.insertCell(2);
    const proteinsCell = newRow.insertCell(3);
    const fatsCell = newRow.insertCell(4);
    const carbohydratesCell = newRow.insertCell(5);
    const fiberCell = newRow.insertCell(6);

    const removeRow = newRow.insertCell(7);

    const removeButton = document.createElement("button");
    removeButton.className = "action-button";    
    removeButton.style.background = "rgb(139, 0, 0)";
    removeButton.onclick = () => {
        tableBody.removeChild(newRow);
        updateTotals();
    };
    removeRow.appendChild(removeButton);

    
    foodCell.textContent = foodName;
    amountCell.textContent = amount.toFixed(2) + " g";
    caloriesCell.textContent = adjustedCalories + " kcal";
    proteinsCell.textContent = adjustedProteins + " g";
    fatsCell.textContent = adjustedFats + " g";
    carbohydratesCell.textContent = adjustedCarbohydrates + " g";
    fiberCell.textContent = adjustedFiber + " g";

    updateTotals();
}

function updateTotals() {

    const tableBody = document.getElementById("bodyOfFoodItems");
    const rows = tableBody.rows;

    let totalCalories = 0;
    let totalProteins = 0;
    let totalFats = 0;
    let totalCarbohydrates = 0;
    let totalFiber = 0;
    
    for (let i = 0; i < rows.length; i++) {

        totalCalories += parseFloat(rows[i].cells[2].textContent) || 0;
        totalProteins += parseFloat(rows[i].cells[3].textContent) || 0;
        totalFats += parseFloat(rows[i].cells[4].textContent) || 0;
        totalCarbohydrates += parseFloat(rows[i].cells[5].textContent) || 0;
        totalFiber += parseFloat(rows[i].cells[6].textContent) || 0;
    }
    
    let totalRow = document.getElementById("totalRow");

    const totalRowTableBody = document.getElementById("bodyForTotal");

    if (!totalRow) {
        totalRow = totalRowTableBody.insertRow();
        totalRow.id = "totalRow";
        totalRow.style.fontWeight = "bold";

        for(let i = 0; i < 8; i++)
            totalRow.insertCell(i)
    }    
      

    const totalFoodCell = totalRow.cells[0];
    const totalAmountCell = totalRow.cells[1];
    const totalCaloriesCell = totalRow.cells[2];
    const totalProteinsCell = totalRow.cells[3];
    const totalFatsCell = totalRow.cells[4];
    const totalCarbohydratesCell = totalRow.cells[5];
    const totalFiberCell = totalRow.cells[6]

   
    totalFoodCell.textContent = "Total";
    totalAmountCell.textContent = "-";
    totalCaloriesCell.textContent = totalCalories.toFixed(2) + " kcal";
    totalProteinsCell.textContent = totalProteins.toFixed(2) + " g";
    totalFatsCell.textContent = totalFats.toFixed(2) + " g";
    totalCarbohydratesCell.textContent = totalCarbohydrates.toFixed(2) + " g";
    totalFiberCell.textContent = totalFiber.toFixed(2) + " g";
}


function clearFoodItems(){
    document.getElementById("bodyOfFoodItems").innerHTML = "";
    updateTotals() 
}