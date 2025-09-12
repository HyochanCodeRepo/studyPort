function joinStudy(studyId) {
    // 스터디 상세 페이지로 이동
    window.location.href = '/study/read/' + studyId;
}

// 사용자 드롭다운 토글 함수
function toggleDropdown() {
    const dropdown = document.getElementById('userDropdown');
    const welcomeMessage = document.querySelector('.welcome-message');
    
    if (dropdown && welcomeMessage) {
        dropdown.classList.toggle('show');
        welcomeMessage.classList.toggle('active');
    }
}

// 문서 클릭 시 모든 드롭다운 닫기 (통합된 이벤트 리스너)
document.addEventListener('click', function(event) {
    // 사용자 드롭다운 처리
    const userDropdown = document.getElementById('userDropdown');
    const userInfo = document.querySelector('.user-info');
    
    if (userDropdown && userInfo && !userInfo.contains(event.target)) {
        userDropdown.classList.remove('show');
        const welcomeMessage = document.querySelector('.welcome-message');
        if (welcomeMessage) {
            welcomeMessage.classList.remove('active');
        }
    }
    
    // 필터 드롭다운 처리
    if (!event.target.closest('.filter-dropdown')) {
        document.querySelectorAll('.dropdown-menu').forEach(menu => {
            if (menu.id !== 'userDropdown') { // 사용자 드롭다운 제외
                menu.classList.remove('show');
                if (menu.previousElementSibling) {
                    menu.previousElementSibling.classList.remove('active');
                }
                if (menu.closest('.filter-dropdown')) {
                    menu.closest('.filter-dropdown').classList.remove('active');
                }
            }
        });
    }
});

// data 속성에서 필터 선택 (Thymeleaf 보안 문제 해결)
function selectFilterFromData(element) {
    const filterType = element.dataset.type;
    const value = element.dataset.value;
    const displayText = element.dataset.display;
    selectFilter(filterType, value, displayText);
}

// 필터링 관련 함수들 추가
let activeFilters = {
    topic: 'all',
    location: 'all',
    level: 'all'
};

// 필터 드롭다운 토글
function toggleFilterDropdown(filterType) {
    const dropdown = document.getElementById(`dropdown-${filterType}`);
    
    if (!dropdown) return;
    
    const toggle = dropdown.previousElementSibling;

    // 다른 필터 드롭다운들 닫기
    document.querySelectorAll('.dropdown-menu').forEach(menu => {
        if (menu.id !== `dropdown-${filterType}` && menu.id !== 'userDropdown') {
            menu.classList.remove('show');
            if (menu.previousElementSibling) {
                menu.previousElementSibling.classList.remove('active');
            }
        }
    });

    // 현재 드롭다운 토글
    dropdown.classList.toggle('show');
    if (toggle) {
        toggle.classList.toggle('active');
    }
}

// 필터 선택
function selectFilter(filterType, value, displayText) {
    activeFilters[filterType] = value;

    // 드롭다운 텍스트 업데이트
    const dropdownText = document.querySelector(`[data-filter="${filterType}"] .dropdown-text`);
    dropdownText.textContent = displayText;

    // 활성 상태 업데이트
    const dropdown = document.getElementById(`dropdown-${filterType}`);
    dropdown.querySelectorAll('.dropdown-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.value === value) {
            item.classList.add('active');
        }
    });

    // 드롭다운 닫기
    dropdown.classList.remove('show');
    dropdown.previousElementSibling.classList.remove('active');

    // 필터링 적용
    applyFilters();
    updateActiveFilterTags();
}

// 모든 필터 적용
function applyFilters() {
    const studyCards = document.querySelectorAll('.study-card');
    let visibleCount = 0;

    studyCards.forEach(card => {
        let shouldShow = true;

        // 카테고리 필터
        if (activeFilters.topic !== 'all') {
            const cardTopic = card.dataset.topic;
            if (cardTopic !== activeFilters.topic) {
                shouldShow = false;
            }
        }

        // 지역 필터
        if (activeFilters.location !== 'all') {
            const cardLocation = card.dataset.location;
            if (!cardLocation || cardLocation !== activeFilters.location) {
                shouldShow = false;
            }
        }

        // 난이도 필터
        if (activeFilters.level !== 'all') {
            const cardLevel = card.dataset.level;
            if (!cardLevel || cardLevel !== activeFilters.level) {
                shouldShow = false;
            }
        }

        // 카드 표시/숨김
        if (shouldShow) {
            card.style.display = 'block';
            card.classList.add('fade-in');
            visibleCount++;
        } else {
            card.style.display = 'none';
            card.classList.remove('fade-in');
        }
    });

    // 결과가 없을 때 메시지 표시
    showNoResultsMessage(visibleCount === 0);
}

// 결과 없음 메시지 표시
function showNoResultsMessage(show) {
    const existingMessage = document.querySelector('.no-results-message');
    const studyGrid = document.getElementById('study-list');

    if (show && !existingMessage) {
        const message = document.createElement('div');
        message.className = 'no-results-message';
        message.innerHTML = `
            <svg style="width: 64px; height: 64px; margin: 0 auto 1rem; opacity: 0.5;" fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"></path>
            </svg>
            <h3 style="margin-bottom: 0.5rem; color: #374151;">해당 조건의 스터디가 없습니다</h3>
            <p style="margin-bottom: 1.5rem;">다른 필터를 선택해보세요!</p>
            <button onclick="resetAllFilters()" style="display: inline-block; padding: 0.75rem 1.5rem; background: linear-gradient(135deg, #3b82f6, #8b5cf6); color: white; border: none; border-radius: 50px; font-weight: 500; cursor: pointer;">필터 초기화</button>
        `;
        studyGrid.appendChild(message);
    } else if (!show && existingMessage) {
        existingMessage.remove();
    }
}

// 활성 필터 태그 업데이트
function updateActiveFilterTags() {
    const activeFiltersContainer = document.getElementById('active-filters');
    const tagsContainer = document.getElementById('active-filter-tags');

    // 활성 필터가 있는지 확인
    const hasActiveFilters = Object.values(activeFilters).some(value => value !== 'all');

    if (!hasActiveFilters) {
        activeFiltersContainer.style.display = 'none';
        return;
    }

    activeFiltersContainer.style.display = 'flex';
    tagsContainer.innerHTML = '';

    // 각 필터 타입별로 태그 생성
    Object.entries(activeFilters).forEach(([type, value]) => {
        if (value !== 'all') {
            const filterNames = {
                topic: '카테고리',
                location: '지역',
                level: '난이도'
            };

            const levelNames = {
                'BEGINNER': '초급',
                'INTERMEDIATE': '중급',
                'ADVANCED': '고급'
            };

            const displayValue = type === 'level' ? (levelNames[value] || value) : value;

            const tag = document.createElement('div');
            tag.className = 'active-filter-tag';
            tag.innerHTML = `
                <span>${filterNames[type]}: ${displayValue}</span>
                <svg class="active-filter-remove" onclick="removeFilter('${type}')" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"></path>
                </svg>
            `;
            tagsContainer.appendChild(tag);
        }
    });
}

// 개별 필터 제거
function removeFilter(filterType) {
    selectFilter(filterType, 'all', '전체');
}

// 모든 필터 초기화
function resetAllFilters() {
    activeFilters = {
        topic: 'all',
        location: 'all',
        level: 'all'
    };

    // 모든 드롭다운 텍스트 초기화
    document.querySelectorAll('.dropdown-text').forEach(text => {
        text.textContent = '전체';
    });

    // 모든 드롭다운 아이템 활성 상태 초기화
    document.querySelectorAll('.dropdown-item').forEach(item => {
        item.classList.remove('active');
        if (item.dataset.value === 'all') {
            item.classList.add('active');
        }
    });

    // 필터링 적용
    applyFilters();
    updateActiveFilterTags();
}

// 사이드바 토글 (기존 함수가 있다면 유지, 없다면 추가)
function toggleSidebar() {
    const sidebar = document.querySelector('.sidebar');
    const mainContainer = document.querySelector('.main-container');

    sidebar.classList.toggle('collapsed');
    mainContainer.classList.toggle('sidebar-collapsed');
}




// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 초기 필터 상태 설정
    updateActiveFilterTags();
});