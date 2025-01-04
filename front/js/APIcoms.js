async function getAPI(url, jwToken) { 
    try {   
        let response = await fetch(url, {
            method: 'GET',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + jwToken,                                           
            },
        });
        return response; 
    } catch (error) {
        console.error('Error during GET request:', error);
        return response; 
    }       
}


async function postAPI(url, jsonString, jwToken) { 
    try {   
        let response = await fetch(url, {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + jwToken,                                          
            },
            body: jsonString,   
        });
        return response; 
    } catch (error) {
        console.error('Error during POST request:', error);
        return response;
    }   
}

async function putAPI(url, jsonString, jwToken) { 
    try {   
        let response = await fetch(url, {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + jwToken,                                      
            },
            body: jsonString,   
        });
        return response; 
    } catch (error) {
        console.error('Error during PUT request:', error);
        return response; 
    }   
}

async function deleteAPI(url, jwToken) { 
    try {   
        let response = await fetch(url, {
            method: 'DELETE',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': "Bearer " + jwToken,                                         
            },
        });
        return response; 
    } catch (error) {
        console.error('Error during DELETE request:', error);
        return response; 
    }   
}