

async function findProductsByCategory(){
    let productCategory = document.getElementById("productCategory").value;    
    if(!productCategory) return;

    const jSonData = await getProductsByCategory(productCategory,10,0);
    createProductCards(jSonData); 
}

async function findProductsByName(){

    let productName = document.getElementById("productsName").value; 

    const jSonData = await getProductsByName(productName,10,0);
    console.log();
    createProductCards(jSonData);
}



async function getProductsByCategory(productCategory,limit,offset){
    
    let jwToken = getCookie("jwToken");   
   

    let response = await getAPI(javaURL + "/product/public/category/" + productCategory + "?limit=" + limit + "&offset=" +offset, jwToken); 

    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('productMessage').textContent = "Sorry we don't have that!";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('productMessage').textContent = "Ops something went wrong! Sorry!";   
        return null;
    }
};

async function getProductsByName(productName,limit,offset){
    
    let jwToken = getCookie("jwToken");   
   

    let response = await getAPI(javaURL + "/product/public/name/" + productName + "?limit=" + limit + "&offset=" +offset, jwToken); 

    let jSonData;

    if (response && response.status == 204) {  
        document.getElementById('productMessage').textContent = "Sorry we don't have that!";   
        return null;
    }
    else if (response && response.ok) {       
        jSonData = await response.json();        
        return jSonData;

    } else {
        console.error("Response:", response ? response.status : "No response");
        document.getElementById('productMessage').textContent = "Ops something went wrong! Sorry!";   
        return null;
    }
};



async function createProductCards(jSonData) {

    if(!jSonData) return;
    document.getElementById('productMessage').textContent = "";   
    document.getElementById("productCardCointainer").innerHTML = "";  

    console.log(jSonData);


    jSonData.filter(product => !(product.category === "Service" && !checkRole()))
            .forEach(product => {              

                 addProductCard(product);
             });       
    
}

async function addProductCard(product) {

    const cardCointainer =  document.getElementById("productCardCointainer");
    document.getElementById("productCardCointainer").style.display = "grid";

    const productDiv = document.createElement("div");
    productDiv.classList = "productCart";            

            const idInput = document.createElement("input");
            idInput.id = "product" + product.id;              
            idInput.value = product.id;
            idInput.type = "hidden";

            const priceInput = document.createElement("input");
            priceInput.id = "price" + product.id;              
            priceInput.value = product.price;
            priceInput.type = "hidden";

            const categoryInput = document.createElement("input");
            categoryInput.id = "category" + product.id;              
            categoryInput.value = product.category;
            categoryInput.type = "hidden";


            const title = document.createElement("h1");
            title.textContent = product.name;
            title.id = "title" + product.id;  

            const img = document.createElement("img");
            img.src = product.imageUrl;

            const discription = document.createElement("p");
            discription.textContent = product.description;     
            
            const infoDiv = document.createElement("div");
            infoDiv.className = "columnDivPriceCategory";

                const categoryLabel = document.createElement("label");
                categoryLabel.textContent = "Category: " + product.category;               

                const priceLabel = document.createElement("label");
                priceLabel.textContent = "Price: " + product.price + " €";
                priceLabel.style.textAlign = "right";              

            infoDiv.appendChild(categoryLabel);
            infoDiv.appendChild(priceLabel);

            const buttonDiv = document.createElement("div");
            buttonDiv.classList ="buttonDiv";

                const addToCart = document.createElement("button");
                addToCart.classList = "action-button";
                addToCart.textContent = "Add to shoping cart!";
                addToCart.style.width = "100%";
                addToCart.onclick = async function() {
                    await addProductToCart(product.id, product.price, product.category, product.name);
                }; 

                buttonDiv.append(addToCart);

            productDiv.appendChild(idInput);
            productDiv.appendChild(priceInput);
            productDiv.appendChild(categoryInput);

            productDiv.appendChild(title);
            productDiv.appendChild(img);
            productDiv.appendChild(discription);
            productDiv.appendChild(infoDiv);
            productDiv.appendChild(buttonDiv);

    cardCointainer.appendChild(productDiv);  

}

async function addProductToCart(id, price, category,productName) {
   
    let jsonStringItemCart = await getCookie("productCart");

    let tempCart = jsonStringItemCart ? JSON.parse(jsonStringItemCart) : []; 
    
    let productIndex = findProductIndex(tempCart, id);

    if (productIndex !== -1) {        
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