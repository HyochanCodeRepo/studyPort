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

    // 필터 컨테이너가 없으면 (주석 처리된 경우) 함수 종료
    if (!activeFiltersContainer || !tagsContainer) {
        return;
    }

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
    const toggleBtn = document.querySelector('.sidebar-toggle-btn');

    sidebar.classList.toggle('collapsed');
    mainContainer.classList.toggle('sidebar-collapsed');
    
    // 버튼 표시/숨김 처리
    if (mainContainer.classList.contains('sidebar-collapsed')) {
        toggleBtn.style.display = 'flex';
    } else {
        toggleBtn.style.display = 'none';
    }
}




// ========================================
// 검색 기능 (AJAX)
// ========================================

// 검색 실행 함수
async function searchStudies() {
    const keyword = document.querySelector('.search-input').value.trim();
    const studyGrid = document.querySelector('.study-grid');

    // 로딩 표시
    showLoading(true);

    try {
        // 서버에 검색 요청
        const response = await fetch(`/api/studies/search?keyword=${encodeURIComponent(keyword)}`);

        if (!response.ok) {
            throw new Error('검색 실패');
        }

        const studies = await response.json();

        // 결과 렌더링
        renderSearchResults(studies);

    } catch (error) {
        console.error('검색 오류:', error);
        showError('검색 중 오류가 발생했습니다.');
    } finally {
        // 로딩 숨김
        showLoading(false);
    }
}

// 검색 결과 렌더링
function renderSearchResults(studies) {
    const studyGrid = document.querySelector('.study-grid');

    // 기존 카드 모두 제거
    studyGrid.innerHTML = '';

    // 결과 없을 때
    if (studies.length === 0) {
        studyGrid.innerHTML = `
            <div class="no-studies-message" style="grid-column: 1 / -1; text-align: center; padding: 3rem;">
                <svg style="width: 64px; height: 64px; margin: 0 auto 1rem; opacity: 0.5;" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"></path>
                </svg>
                <h3 style="margin-bottom: 0.5rem; color: #374151;">검색 결과가 없습니다</h3>
                <p style="color: #64748b;">다른 검색어로 시도해보세요.</p>
            </div>
        `;
        return;
    }

    // 각 스터디를 카드로 생성
    studies.forEach(study => {
        const card = createStudyCard(study);
        studyGrid.appendChild(card);
    });
}

// 스터디 카드 HTML 생성
function createStudyCard(study) {
    const card = document.createElement('div');
    card.className = 'study-card fade-in';
    card.setAttribute('data-topic', study.topic || '');
    card.setAttribute('data-location', study.location || '');
    card.setAttribute('data-level', study.levelTag || '');
    card.setAttribute('data-status', study.isPrivate ? 'private' : 'recruiting');

    // 레벨 텍스트 변환
    const levelText = {
        'BEGINNER': '초급',
        'INTERMEDIATE': '중급',
        'ADVANCED': '고급'
    }[study.levelTag] || study.levelTag;

    // 스터디 타입 텍스트 변환
    const typeText = {
        'hybrid': '온/오프라인 병행',
        'online': '온라인',
        'offline': '오프라인'
    }[study.studyType] || '';

    card.innerHTML = `
        <div class="study-header">
            <span class="study-category">${study.topic || ''}</span>
            <div class="study-status">
                <svg class="meta-icon" fill="currentColor" viewBox="0 0 20 20">
                    <path fill-rule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                </svg>
                <span>${study.isPrivate ? '비공개' : '모집중'}</span>
            </div>
        </div>
        <h3 class="study-title">${study.name || ''}</h3>
        <p>${study.description || ''}</p>

        <div class="study-meta">
            ${study.frequency ? `
                <div class="meta-item">
                    <svg class="meta-icon" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"></path>
                    </svg>
                    <span>${study.frequency}</span>
                </div>
            ` : ''}

            ${study.location ? `
                <div class="meta-item">
                    <svg class="meta-icon" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M5.05 4.05a7 7 0 119.9 9.9L10 18.9l-4.95-4.95a7 7 0 010-9.9zM10 11a2 2 0 100-4 2 2 0 000 4z" clip-rule="evenodd"></path>
                    </svg>
                    <span>${study.location}</span>
                    ${typeText ? `<span>(${typeText})</span>` : ''}
                </div>
            ` : ''}

            ${study.capacity ? `
                <div class="meta-item">
                    <svg class="meta-icon" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3z"></path>
                    </svg>
                    <span>1/${study.capacity}명</span>
                </div>
            ` : ''}

            ${study.levelTag ? `
                <div class="meta-item">
                    <svg class="meta-icon" fill="currentColor" viewBox="0 0 20 20">
                        <path fill-rule="evenodd" d="M3 3a1 1 0 000 2v8a2 2 0 002 2h2.586l-1.293 1.293a1 1 0 101.414 1.414L10 15.414l2.293 2.293a1 1 0 001.414-1.414L12.414 15H15a2 2 0 002-2V5a1 1 0 100-2H3zm11.707 4.707a1 1 0 00-1.414-1.414L10 9.586 8.707 8.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clip-rule="evenodd"></path>
                    </svg>
                    <span>${levelText || ''}</span>
                </div>
            ` : ''}
        </div>

        <button class="join-btn" onclick="joinStudy(${study.id})">
            <span>${study.isPrivate ? '참여 신청' : '스터디 참여하기'}</span>
        </button>
    `;

    return card;
}

// 로딩 표시
function showLoading(show) {
    const studyGrid = document.querySelector('.study-grid');

    if (show) {
        studyGrid.innerHTML = `
            <div class="loading-spinner" style="grid-column: 1 / -1; text-align: center; padding: 3rem;">
                <div style="display: inline-block; width: 50px; height: 50px; border: 4px solid #e2e8f0; border-top-color: #3b82f6; border-radius: 50%; animation: spin 1s linear infinite;"></div>
                <p style="margin-top: 1rem; color: #64748b;">검색 중...</p>
            </div>
        `;
    }
}

// 에러 메시지 표시
function showError(message) {
    const studyGrid = document.querySelector('.study-grid');
    studyGrid.innerHTML = `
        <div class="error-message" style="grid-column: 1 / -1; text-align: center; padding: 3rem; color: #ef4444;">
            <h3 style="margin-bottom: 0.5rem;">오류 발생</h3>
            <p>${message}</p>
        </div>
    `;
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', function() {
    // 초기 필터 상태 설정
    updateActiveFilterTags();

    // 검색 이벤트 리스너 등록
    const searchInput = document.querySelector('.search-input');
    const searchBtn = document.querySelector('.search-btn');

    // Enter 키로 검색
    if (searchInput) {
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchStudies();
            }
        });
    }

    // 버튼 클릭으로 검색
    if (searchBtn) {
        searchBtn.addEventListener('click', searchStudies);
    }
});

// ========================================
// 내 스터디 탭 전환 함수
// ========================================
function switchMyStudyTab(event, tabName) {
    // 모든 탭 버튼 비활성화
    const tabButtons = document.querySelectorAll('.tab-container .tab-btn');
    tabButtons.forEach(btn => btn.classList.remove('active'));

    // 모든 탭 콘텐츠 숨기기
    const tabContents = document.querySelectorAll('.my-study-content');
    tabContents.forEach(content => content.classList.remove('active'));

    // 클릭한 버튼 활성화
    event.target.classList.add('active');

    // 해당 탭 콘텐츠 표시
    const tabContent = document.getElementById(tabName);
    if (tabContent) {
        tabContent.classList.add('active');
    }
}