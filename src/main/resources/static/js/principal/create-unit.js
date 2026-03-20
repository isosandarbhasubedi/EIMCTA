const classSelect = document.getElementById("classroomSelect");
const sectionsContainer = document.getElementById("sectionsContainer");
const subjectsContainer = document.getElementById("subjectsContainer");

// Load sections after selecting class
classSelect.addEventListener("change", function() {
    const classId = this.value;
    sectionsContainer.innerHTML = "";
    subjectsContainer.innerHTML = "";
    if (!classId) return;

    fetch("/principal/sections-by-classroom/" + classId)
        .then(res => res.json())
        .then(data => {
            // Create professional Select All bar
            const selectAllDiv = document.createElement("div");
            selectAllDiv.className = "select-all-bar";

            const selectAllText = document.createElement("span");
            selectAllText.innerText = "Select All Sections";

            const selectAllInput = document.createElement("input");
            selectAllInput.type = "checkbox";
            selectAllInput.id = "selectAllSections";

            selectAllDiv.appendChild(selectAllText);
            selectAllDiv.appendChild(selectAllInput);
            sectionsContainer.appendChild(selectAllDiv);

            // Add section cards
            data.forEach(section => {
                const div = document.createElement("div");
                div.className = "checkbox-card";

                const cb = document.createElement("input");
                cb.type = "checkbox";
                cb.name = "sectionIds";
                cb.value = section.id;
                cb.className = "section-checkbox";
                cb.id = "section-" + section.id;

                const label = document.createElement("label");
                label.htmlFor = cb.id;
                label.innerText = section.name;

                div.appendChild(label);
                div.appendChild(cb);
                sectionsContainer.appendChild(div);
            });

            // Select All toggle
            selectAllInput.addEventListener("change", function() {
                const checked = this.checked;
                sectionsContainer.querySelectorAll(".section-checkbox").forEach(cb => {
                    cb.checked = checked;
                });
                loadSubjects();
            });

            // Individual section checkbox toggle
            sectionsContainer.querySelectorAll(".section-checkbox").forEach(cb => {
                cb.addEventListener("change", loadSubjects);
            });
        });
});

// Load subjects based on selected sections
function loadSubjects() {
    const selectedSections = Array.from(
        sectionsContainer.querySelectorAll(".section-checkbox:checked")
    ).map(cb => cb.value);

    subjectsContainer.innerHTML = "";
    if (selectedSections.length === 0) return;

    // Select All bar for subjects
    const selectAllDiv = document.createElement("div");
    selectAllDiv.className = "select-all-bar";

    const selectAllText = document.createElement("span");
    selectAllText.innerText = "Select All Subjects";

    const selectAllInput = document.createElement("input");
    selectAllInput.type = "checkbox";
    selectAllInput.id = "selectAllSubjects";

    selectAllDiv.appendChild(selectAllText);
    selectAllDiv.appendChild(selectAllInput);
    subjectsContainer.appendChild(selectAllDiv);

    // Fetch subjects for each selected section
    selectedSections.forEach(sectionId => {
        fetch("/principal/subjectss-by-section/" + sectionId)
            .then(res => res.json())
            .then(data => {
                data.forEach(subject => {
                    const div = document.createElement("div");
                    div.className = "checkbox-card";

                    const cb = document.createElement("input");
                    cb.type = "checkbox";
                    cb.name = "subjectIds";
                    cb.value = subject.id;
                    cb.className = "subject-checkbox";
                    cb.id = "subject-" + subject.id;

                    const label = document.createElement("label");
                    label.htmlFor = cb.id;
                    label.innerText = subject.name + " (" + subject.sectionName + ")";

                    div.appendChild(label);
                    div.appendChild(cb);
                    subjectsContainer.appendChild(div);
                });

                selectAllInput.addEventListener("change", function() {
                    const checked = this.checked;
                    subjectsContainer.querySelectorAll(".subject-checkbox").forEach(cb => {
                        cb.checked = checked;
                    });
                });
            });
    });
}