let serverIpBox = document.getElementById('serverIp');
let addButton = document.getElementById('addButton');
let selectButton = document.getElementById('selectButton');
let serverIpChoose = document.getElementById('choose');
addButton.addEventListener('click',function(){
    let serverIp = serverIpBox.value;
    fetch('http://127.0.0.1:8080/addServer',{
        method:'PUT',
        body: JSON.stringify(serverIp)
    })
    window.location.assign("server.html");
})
selectButton.addEventListener('click',function(){
    let ip = serverIpChoose.value;
    if(ip === 'DEFAULT'){
        ip = '"jakub.domain.ddns.net"';
    }
    fetch('http://127.0.0.1:8080/changeServer',{
        method:'PUT',
        body: JSON.stringify(ip)
    })
    window.location.assign("index.html");
    fetch('http://127.0.0.1:8080/logOut',{
        method:'PUT'
    })
})
function getServers(){
    fetch('http://127.0.0.1:8080/getServers',{
        method: 'GET',
    })
    .then(response =>{
        return response.json();
    })
    .then(data=>{
        serverIpBox.innerHTML = '';
        data.forEach(element => {
            // serverIpBox.value += element;
            option = document.createElement('option');
            option.value = element;
            option.text = element;
            serverIpChoose.appendChild(option);
        });
    })
}
getServers();