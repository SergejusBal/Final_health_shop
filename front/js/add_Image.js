async function uploadButtonClicked(){
    const apiKey = 'bcbd1c0361b512b67b6c337ca3c6d433';
    await selectImage();

    const imageInput = document.getElementById('imageInput');    
    const imageFile = imageInput.files[0];

    if (!imageFile) {
        console.error('No file selected');
        return;
    }

      const formData = new FormData();
      formData.append('image', imageFile);
      
      const reader = new FileReader();

      reader.onloadend = async function() {      

        const response = await fetch(`https://api.imgbb.com/1/upload?key=${apiKey}`, {
          method: 'POST',
          body: formData
        });

        const data = await response.json();

        if (data.success) {         
          document.getElementById("hiddenInput").value = data.data.url;
          console.log(data.data.url);
          document.getElementById("imgLabel").textContent = "Image uploaded.";        
        } else {
          console.error('Error uploading image:', data);
        }
      };

      reader.readAsDataURL(imageFile); 

}

function selectImage() {
    return new Promise((resolve, reject) => {
        const imageInput = document.getElementById('imageInput');

        imageInput.onchange = () => {
            if (imageInput.files.length > 0) {
                resolve(); 
            } else {
                reject();  
            }
        };
       
        imageInput.click();
    });
}