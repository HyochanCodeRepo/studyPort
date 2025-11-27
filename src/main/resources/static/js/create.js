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


    // 폼 제출 처리
    document.getElementById('studyCreateForm').addEventListener('submit', function(e) {
    console.log('스터디 생성 폼 제출');
});
});

// 썸네일 미리보기 함수
function previewThumbnail(event) {
    const file = event.target.files[0];
    if (file) {
        // 파일 크기 체크 (10MB)
        if (file.size > 10 * 1024 * 1024) {
            alert('파일 크기는 10MB를 초과할 수 없습니다.');
            event.target.value = '';
            return;
        }

        // 이미지 파일인지 확인
        if (!file.type.startsWith('image/')) {
            alert('이미지 파일만 업로드할 수 있습니다.');
            event.target.value = '';
            return;
        }

        const reader = new FileReader();
        reader.onload = function(e) {
            document.getElementById('previewImage').src = e.target.result;
            document.getElementById('selectedFileName').textContent = '선택된 파일: ' + file.name;
            document.getElementById('thumbnailPreview').style.display = 'block';
            document.getElementById('fileUpload').style.display = 'none';
        };
        reader.readAsDataURL(file);
    }
}

// 썸네일 제거 함수
function removeThumbnail() {
    document.getElementById('thumbnail').value = '';
    document.getElementById('previewImage').src = '';
    document.getElementById('selectedFileName').textContent = '';
    document.getElementById('thumbnailPreview').style.display = 'none';
    document.getElementById('fileUpload').style.display = 'block';
}
