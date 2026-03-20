const classSelect = document.getElementById("classSelect");
const sectionContainer = document.getElementById("sectionContainer");
const subjectContainer = document.getElementById("subjectContainer");


// LOAD SECTIONS
classSelect.addEventListener("change", function(){

    const classId = this.value;

    sectionContainer.innerHTML="";
    subjectContainer.innerHTML="";

    if(!classId) return;

    fetch("/principal/sections-by-classroom/" + classId)
    .then(res=>res.json())
    .then(data=>{

        // SELECT ALL SECTIONS
        const selectAllDiv = document.createElement("div");

        const selectAll = document.createElement("input");
        selectAll.type="checkbox";
        selectAll.id="selectAllSections";

        const label = document.createElement("label");
        label.innerText=" Select All Sections";

        selectAllDiv.appendChild(selectAll);
        selectAllDiv.appendChild(label);

        sectionContainer.appendChild(selectAllDiv);


        data.forEach(section=>{

            const div = document.createElement("div");

            const cb = document.createElement("input");
            cb.type="checkbox";
            cb.value=section.id;
            cb.name="sectionIds";
            cb.className="section-checkbox";
            
            const label = document.createElement("label");
            label.innerText=" "+section.name;

            div.appendChild(cb);
            div.appendChild(label);

            sectionContainer.appendChild(div);

            cb.addEventListener("change", loadSubjects);
        });


        // SELECT ALL LOGIC
        selectAll.addEventListener("change", function(){

            const checked = this.checked;

            document.querySelectorAll(".section-checkbox")
            .forEach(cb=>{
                cb.checked = checked;
            });

            loadSubjects();
        });

    });

});



// LOAD SUBJECTS
function loadSubjects(){

    const selectedSections = Array.from(
        document.querySelectorAll(".section-checkbox:checked")
    ).map(cb=>cb.value);

    subjectContainer.innerHTML="";

    if(selectedSections.length === 0) return;


    // SELECT ALL SUBJECTS
    const selectAllDiv = document.createElement("div");

    const selectAll = document.createElement("input");
    selectAll.type="checkbox";
    selectAll.id="selectAllSubjects";

    const label = document.createElement("label");
    label.innerText=" Select All Subjects";

    selectAllDiv.appendChild(selectAll);
    selectAllDiv.appendChild(label);

    subjectContainer.appendChild(selectAllDiv);


    selectedSections.forEach(sectionId=>{

        fetch("/principal/subjectss-by-section/" + sectionId)
        .then(res=>res.json())
        .then(data=>{

            data.forEach(subject=>{

                const div = document.createElement("div");

                const cb = document.createElement("input");
                cb.type="checkbox";
                cb.name="subjectIds";
                cb.value=subject.id;
                cb.className="subject-checkbox";
                
                const label = document.createElement("label");
                label.innerText = subject.name + " ("+subject.sectionName+")";

                div.appendChild(cb);
                div.appendChild(label);

                subjectContainer.appendChild(div);

            });

        });

    });


    // SELECT ALL SUBJECTS LOGIC
    selectAll.addEventListener("change", function(){

        const checked = this.checked;

        document.querySelectorAll(".subject-checkbox")
        .forEach(cb=>{
            cb.checked = checked;
        });

    });

}


