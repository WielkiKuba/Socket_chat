let refreshButton = document.getElementById('refreshButton');
let textBox = document.getElementById('textarea1');
let reciverIpBox = document.getElementById('reciverIp');
let messageBox = document.getElementById('message');
let sendButton = document.getElementById('sendButton');
let myIp = "";
function getList(){
    fetch('http://127.0.0.1:8080/getList',{
        method: 'GET',
    })
    .then(response => {
        if (response.ok) {
          return response.text();
        } else {
          throw new Error('Błąd w odpowiedzi: ' + response.status);
        }
      })
    .then(data => {
        textBox.value = null;
        if(data===null||data===''){
          textBox.value += "There's no online users";
        }
        else{
          const dataArray = JSON.parse(data);
          dataArray.forEach(element => {
            textBox.value += element+'\n';
          });
        }
    })
    .catch(error =>{
      console.error("cannot connect with local server")
    })
}
function ip(){
  fetch('http://127.0.0.1:8080/myIP',{
    method: 'GET',
  })
  .then(response =>{
    return response.text();
  })
  .then(data =>{
    myIp = data;
  })
  .catch(error =>{
    console.error("cannot connect with local server")
  })
}
sendButton.addEventListener('click',function(){
  let reciverIp = reciverIpBox.value;
  let message = messageBox.value;
  fetch('http://127.0.0.1:8080/send',{
    method: 'PUT',
    body: JSON.stringify('MESSAGE#'+reciverIp+'#'+message+'#'+myIp)
  })
  .catch(error =>{
    console.error("cannot connect with local server")
  })
});
ip();
refreshButton.addEventListener('click',getList)