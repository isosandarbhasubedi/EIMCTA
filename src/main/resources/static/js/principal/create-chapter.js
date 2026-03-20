const classroomSelect = document.getElementById("classroomSelect");
const sectionSelect = document.getElementById("sectionSelect");
const subjectSelect = document.getElementById("subjectSelect");
const unitSelect = document.getElementById("unitSelect");

// Load Sections
classroomSelect.addEventListener("change", () => {
    const classId = classroomSelect.value;
    sectionSelect.innerHTML = "<option value=''>Select Section</option>";
    subjectSelect.innerHTML = "<option value=''>Select Subject</option>";
    unitSelect.innerHTML = "<option value=''>Select Unit</option>";
    if(!classId) return;
    fetch("/principal/sections-by-classroom/" + classId)
        .then(res => res.json())
        .then(data => {
            data.forEach(section => {
                const option = document.createElement("option");
                option.value = section.id;
                option.text = section.name;
                sectionSelect.appendChild(option);
            });
        });
});

// Load Subjects
sectionSelect.addEventListener("change", () => {
    const sectionId = sectionSelect.value;
    subjectSelect.innerHTML = "<option value=''>Select Subject</option>";
    unitSelect.innerHTML = "<option value=''>Select Unit</option>";
    if(!sectionId) return;
    fetch("/principal/subjects-by-section/" + sectionId)
        .then(res => res.json())
        .then(data => {
            data.forEach(subject => {
                const option = document.createElement("option");
                option.value = subject.id;
                option.text = subject.name;
                subjectSelect.appendChild(option);
            });
        });
});

// Load Units
subjectSelect.addEventListener("change", () => {
    const subjectId = subjectSelect.value;
    unitSelect.innerHTML = "<option value=''>Select Unit</option>";
    if(!subjectId) return;
    fetch("/principal/units-by-subject/" + subjectId)
        .then(res => res.json())
        .then(data => {
            data.forEach(unit => {
                const option = document.createElement("option");
                option.value = unit.id;
                option.text = unit.name;
                unitSelect.appendChild(option);
            });
        });
});