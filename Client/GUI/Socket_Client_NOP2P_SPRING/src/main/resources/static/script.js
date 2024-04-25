let button = document.getElementById('button');
button.addEventListener('click',function(){
    var inputLogin = document.getElementById("login")
    var inputPassword = document.getElementById("password")
    var login = inputLogin.value;
    var password = inputPassword.value;
    fetch('http://127.0.0.1:8080/getLogin', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({ message: login+"#"+password }),
})
.then(response => {
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return response.json();
})
.then(data => {
  if(data === false){
    let invalidLogin = document.getElementById('invalidLogin');
    invalidLogin.style.visibility = "visible";
  }else{
    fetch('http://127.0.0.1:8080/bio',{
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: login
    });
    window.location.assign("send.html")
  }
})
.catch(error =>{
  console.error("cannot connect with local server")
})
})