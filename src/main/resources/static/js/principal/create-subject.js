const classSelect = document.getElementById("classroomSelect");
const container = document.getElementById("sectionsContainer");

classSelect.addEventListener("change", function() {
    const classId = this.value;

    container.innerHTML = "";
    if(!classId) return;

    fetch("/principal/sections-by-class/" + classId)
        .then(res => res.json())
        .then(data => {

            const selectAllDiv = document.createElement("div");
            selectAllDiv.classList.add("section-item","select-all");

            const selectAll = document.createElement("input");
            selectAll.type = "checkbox";
            selectAll.id = "selectAllSections";

            const selectAllLabel = document.createElement("label");
            selectAllLabel.htmlFor = "selectAllSections";
            selectAllLabel.innerHTML = "<strong>Select All</strong>";

            selectAllDiv.appendChild(selectAll);
            selectAllDiv.appendChild(selectAllLabel);
            container.appendChild(selectAllDiv);

            data.forEach(section => {

                const div = document.createElement("div");
                div.classList.add("section-item");

                const checkbox = document.createElement("input");
                checkbox.type = "checkbox";
                checkbox.name = "sectionIds";
                checkbox.value = section.id;
                checkbox.id = "section-" + section.id;

                const label = document.createElement("label");
                label.htmlFor = "section-" + section.id;
                label.innerText = " " + section.name;

                div.appendChild(checkbox);
                div.appendChild(label);
                container.appendChild(div);
            });

            selectAll.addEventListener("change", function() {
                const checked = this.checked;
                container.querySelectorAll("input[name='sectionIds']").forEach(cb => {
                    cb.checked = checked;
                });
            });

        });
});

document.querySelectorAll(".section-item").forEach(item => {
    item.addEventListener("click", function(e) {

        // prevent double toggle when clicking checkbox itself
        if (e.target.tagName === "INPUT") return;

        const checkbox = this.querySelector("input[type='checkbox']");
        checkbox.checked = !checkbox.checked;
    });
});

// make rows clickable
container.querySelectorAll(".section-item").forEach(item => {
    item.addEventListener("click", function(e) {
        if (e.target.tagName === "INPUT") return;

        const checkbox = this.querySelector("input[type='checkbox']");
        checkbox.checked = !checkbox.checked;
    });
});