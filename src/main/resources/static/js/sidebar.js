document.addEventListener("DOMContentLoaded", function(){

const input = document.getElementById("sidebarSearch");

if(!input) return;

input.addEventListener("keyup", function(){

let filter = input.value.toLowerCase();
let links = document.querySelectorAll(".sidebar-link");

links.forEach(function(link){

let text = link.textContent.toLowerCase();

if(text.includes(filter)){
link.style.display = "flex";
}else{
link.style.display = "none";
}

});

});

});