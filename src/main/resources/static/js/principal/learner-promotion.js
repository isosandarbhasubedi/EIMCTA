// LOAD SOURCE SECTIONS
document.getElementById("classroomSelect").addEventListener("change", function(){

let classId = this.value;

if(!classId){
document.getElementById("sectionContainer").innerHTML="";
return;
}

fetch("/principal/sections-by-classroom/" + classId)
.then(res=>res.json())
.then(data=>{

let container=document.getElementById("sectionContainer");
container.innerHTML="";

data.forEach(section=>{
container.innerHTML+=`
<div class="option-item">
<input type="radio" name="sourceSection" value="${section.id}" onchange="loadLearners(${section.id})">
 ${section.name}
</div>`;
});

});
});


// LOAD LEARNERS
function loadLearners(sectionId){

fetch("/principal/learners-by-section-for-promotion/" + sectionId)
.then(res=>res.json())
.then(data=>{

let container=document.getElementById("learnerContainer");
container.innerHTML="";

data.forEach(l=>{
container.innerHTML+=`
<div class="option-item">
<input type="checkbox" name="enrollmentIds" value="${l.enrollmentId}" class="learnerCheckbox">
 ${l.learnerName} (${l.learnerEmail})
</div>`;
});

});
}


// SELECT ALL
document.getElementById("selectAllLearners").addEventListener("change",function(){
let learners=document.querySelectorAll(".learnerCheckbox");
learners.forEach(cb=>cb.checked=this.checked);
});


// TARGET CLASS BY YEAR
document.getElementById("academicYearSelect").addEventListener("change",function(){

let yearId=this.value;

fetch("/principal/classrooms-by-academic-year/"+yearId)
.then(res=>res.json())
.then(data=>{

let select=document.getElementById("targetClassroomSelect");
select.innerHTML="<option value=''>Select Classroom</option>";

data.forEach(c=>{
select.innerHTML+=`<option value="${c.id}">${c.name}</option>`;
});

});
});


// TARGET SECTIONS
document.getElementById("targetClassroomSelect").addEventListener("change",function(){

let classId=this.value;

fetch("/principal/sections-by-classroom/"+classId)
.then(res=>res.json())
.then(data=>{

let container=document.getElementById("targetSectionContainer");
container.innerHTML="";

data.forEach(section=>{
container.innerHTML+=`
<div class="option-item">
<input type="radio" name="targetSectionId" value="${section.id}" required>
 ${section.name}
</div>`;
});

});
});
