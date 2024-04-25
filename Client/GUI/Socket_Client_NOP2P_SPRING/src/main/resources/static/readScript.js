let readBox = document.getElementById('textarea2');
let refreshButton = document.getElementById('refreshButton');
function read(){
    readBox.value = "";
    fetch('http://127.0.0.1:8080/read',{
        method:'GET',
    })
    .then(response =>{
        return response.text();
    })
    .then(data =>{
        readBox.value = data;
    })
    .catch(error =>{
        console.error("cannot connect with local server")
      })
}
read();
refreshButton.addEventListener('click',read);