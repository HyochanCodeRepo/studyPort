🏨 DeliNight
모바일로 체크인부터 룸 서비스까지, 고객 편의를 극대화하고 관리 효율성을 높인 스마트 호텔 관리 시스템

📖 목차
---
프로젝트 소개

주요 기능

기술 스택

역할 및 기여도

시연 영상
---

✒️ 프로젝트 소개
개요
프로젝트명: DeliNight

개발 기간: 2025.04.01 ~ 2025.06.15 (총 45일)

개발 인원: 5명 (팀 프로젝트)

프로젝트 주제: 관리자를 위한 통합 호텔 관리 시스템 및 고객을 위한 모바일 호텔 서비스

**주요 내용**
본 프로젝트는 호텔 관리의 디지털 전환을 목표로 개발되었습니다. 관리자에게는 전반적인 호텔 운영을 효율적으로 관리할 수 있는 시스템을, 고객에게는 모바일 기기를 통해 체크인, 룸 서비스, 룸 케어 등 다양한 서비스를 간편하게 이용할 수 있는 환경을 제공합니다.

**✨ 주요 기능**
👨‍💼 관리자 시스템
통합 관리 기능: 본사, 지사, 호텔, 가맹점의 정보를 등록, 조회, 수정, 삭제(CRUD)할 수 있는 관리자 페이지를 구현했습니다.

권한 관리: Spring Security를 활용하여 본사, 지사, 호텔별로 접근 권한을 세분화하여 관리합니다.

회원가입 승인: 새로운 가맹점 및 직원의 회원가입 요청을 관리자가 승인 또는 거절할 수 있는 기능을 구현했습니다.

📱 고객 모바일 서비스
스마트 체크인: 객실에 부착된 QR코드를 스캔하여 로그인하는 비대면 체크인 시스템을 구현했습니다.

간편 서비스 요청: 모바일을 통해 룸 서비스 및 룸 케어 서비스를 실시간으로 신청할 수 있습니다.

🛠️ 기술 스택

<img src="https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white"> ,<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"> , <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=Spring-Security&logoColor=white"> , <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=Spring-Data-JPA&logoColor=white">
<br>
<img src="https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white"> , <img src="https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white">
, <img src="https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E"> 
, <img src="https://img.shields.io/badge/jquery-%230769AD.svg?style=for-the-badge&logo=jquery&logoColor=white"> 
, <img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"> 
, <img src="https://img.shields.io/badge/ajax-006FAD?style=for-the-badge&logo=ajax&logoColor=white">

<img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white">

🧑🏻‍💻 역할 및 기여도
DB 설계: 프로젝트의 데이터 모델링을 담당하여 테이블 구조 및 관계를 설계했습니다.

관리자 권한 관리: Spring Security를 활용해 관리자(본사, 지사, 호텔)의 권한을 세분화하여 로그인 및 접근을 제어했습니다.

CRUD API 개발: 본사, 지사, 호텔, 가맹점 정보를 관리하기 위한 RESTful API를 개발했습니다.

회원가입 승인 기능: 새로운 관리자 및 가맹점의 회원가입 요청을 관리자가 승인/거절할 수 있는 기능을 구현했습니다.

스마트 체크인 시스템 구현:

로그인 로직: 고객이 객실 내 QR 코드를 스캔하면 로그인되는 시스템을 구현했습니다.

회원/비회원 구분: 회원가입이 되어있는 고객은 핸드폰 번호 뒷 4자리를 초기 비밀번호로 설정하고, 비회원의 경우 임의의 4자리 숫자를 발급해 로그인할 수 있도록 했습니다.

상태 변경: 체크인 버튼을 누르면 RESTful API를 호출하여 고객의 객실 상태가 '체크인 완료'로 변경되도록 구현했습니다.

🎬 시연 영상
(여기에 프로젝트 시연 영상 링크를 추가하세요.)

✍️ 느낀 점
(프로젝트를 통해 배운 점, 어려웠던 점, 해결 과정 등을 자유롭게 작성하세요.)
