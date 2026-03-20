
// Load sections
document.getElementById("classroomSelect").addEventListener("change", function() {
    let classId = this.value;
    if(!classId) return;

    fetch("/principal/sections-by-classroom/" + classId)
        .then(res => res.json())
        .then(data => {
            let sectionSelect = document.getElementById("sectionSelect");
            sectionSelect.innerHTML = "<option value=''>Select Section</option>";

            data.forEach(section => {
                let option = document.createElement("option");
                option.value = section.id;
                option.text = section.name;
                sectionSelect.appendChild(option);
            });
        });
});

// Load learners
document.getElementById("sectionSelect").addEventListener("change", function() {
    let sectionId = this.value;
    if(!sectionId) return;

    loadLearners(sectionId);
});

// Populate table
function loadLearners(sectionId) {

    fetch("/principal/learners-by-section/" + sectionId)
        .then(res => res.json())
        .then(data => {

            let table = document.getElementById("studentTable");
            table.innerHTML = "";

            data.forEach(enrollment => {

                // Determine status from database
                const statusClass = enrollment.active ? 'badge-active' : 'badge-restricted';
                const statusText = enrollment.active ? 'Active' : 'Inactive';

                // Toggle button text
                const toggleText = enrollment.active ? 'Deactivate' : 'Activate';

                table.innerHTML += `
                    <tr>
                        <td>${enrollment.learner.username}</td>
                        <td>${enrollment.learner.email}</td>
                        <td>${enrollment.section.classRoom.name}</td>
                        <td>${enrollment.section.name}</td>
                        <td>
                        <span class="badge ${statusClass}">${statusText}</span>
                    </td>
                        <td>
                            <div class="action-group">
                                <!-- NEW: Profile Button -->
            <a href="/principal/learner-profile/${enrollment.learner.id}" 
               class="btn btn-primary">
               Profile
            </a>

                                <form action="/principal/learners/enrollment/toggle/${enrollment.id}" method="post">
                                    <button type="submit" class="btn btn-toggle">
                                        ${toggleText}
                                    </button>
                                </form>

                                <form action="/principal/learners/enrollment/delete/${enrollment.id}" method="post">
                                    <button type="submit" class="btn btn-delete">
                                        Delete
                                    </button>
                                </form>

                            </div>
                        </td>
                    </tr>
                `;
            });

        });
}
