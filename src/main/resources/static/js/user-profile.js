function showSection(sectionName, buttonElement) {

        document.querySelectorAll('.profile-content-section').forEach(section => {
            section.classList.remove('active');
        });

        document.querySelectorAll('.profile-sidebar-button').forEach(button => {
            button.classList.remove('active');
        });

        document.getElementById(sectionName + '-section').classList.add('active');

        buttonElement.classList.add('active');
    }