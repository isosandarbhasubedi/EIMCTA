
// Load sections based on classroom
document.getElementById("classroomSelect").addEventListener("change", function(){

    let classId = this.value;

    if(!classId){
        document.getElementById("sectionContainer").innerHTML = "";
        return;
    }

    fetch("/principal/sections-by-classroom/" + classId)
        .then(res => res.json())
        .then(data => {

            let container = document.getElementById("sectionContainer");
            container.innerHTML = "";

            data.forEach(section => {

                container.innerHTML += `
                    <div class="option-item">
                        <input type="checkbox" name="sectionIds" value="${section.id}">
                        ${section.name}
                    </div>
                `;

            });

        });

});


// Select All Learners
document.getElementById("selectAllLearners").addEventListener("change", function(){

    let learners = document.querySelectorAll(".learnerCheckbox");

    learners.forEach(cb => {
        cb.checked = this.checked;
    });

});
