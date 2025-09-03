    function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const mainContainer = document.querySelector('.main-container');

    sidebar.classList.toggle('collapsed');
    mainContainer.classList.toggle('sidebar-collapsed');
}

    document.addEventListener('DOMContentLoaded', function() {
    // 비공개 토글 기능
    document.getElementById('privateChk').addEventListener('change', function () {
        const passwordArea = document.getElementById('passwordArea');
        if (this.checked) {
            passwordArea.classList.add('show');
            document.getElementById('password').required = true;
        } else {
            passwordArea.classList.remove('show');
            document.getElementById('password').required = false;
            document.getElementById('password').value = '';
        }
    });

    // 파일 업로드 기능
    const fileUpload = document.getElementById('fileUpload');
    const fileInput = document.getElementById('mainimg');
    const selectedFileDiv = document.getElementById('selectedFile');

    fileUpload.addEventListener('click', () => {
    fileInput.click();
});

    fileInput.addEventListener('change', function() {
    if (this.files && this.files[0]) {
    const fileName = this.files[0].name;
    selectedFileDiv.innerHTML = `
                    <div class="file-selected">
                        <svg width="16" height="16" fill="currentColor" viewBox="0 0 20 20">
                            <path fill-rule="evenodd" d="M4 3a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V5a2 2 0 00-2-2H4zm12 12H4l4-8 3 6 2-4 3 6z" clip-rule="evenodd"></path>
                        </svg>
                        ${fileName}
                    </div>
                `;
}
});

    // 폼 제출 처리
    document.getElementById('studyCreateForm').addEventListener('submit', function(e) {
    console.log('스터디 생성 폼 제출');
});
});
