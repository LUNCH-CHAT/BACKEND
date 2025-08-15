
<img width="947" height="434" alt="스크린샷 2025-08-13 오후 9 09 39" src="https://github.com/user-attachments/assets/a95280ee-6551-4bfe-b51e-6ae88fa7b5be" />


# 👋 소개

밥약부터 커리어까지, 관심사로 연결되는 교내 커피챗 플랫폼

런치챗(LunchChat)은 시간표와 관심사로 매칭되는 교내 커피챗 플랫폼입니다.

관심사와 시간표를 기반으로,  같은 학교에서 나와 잘 맞는 선후배를 연결해  밥약·커피챗을 통해 네트워킹하고 커리어 대화를 이어주는 서비스입니다.


## 🖥️ 서비스 화면

<img width="567" height="570" alt="스크린샷 2025-08-13 오후 9 13 19" src="https://github.com/user-attachments/assets/f028bc0b-152c-42ba-9c4f-980e1063bc77" />


## 🛠 BE Stacks

- Spring Boot: Java 웹 어플리케이션 프레임워크
- Java 17: 최신 LTS 런타임으로 안정성과 성능 확보
- Spring Security + JWT: 토큰 기반 stateless 인증/인가 구현
- Redis Pub/Sub: 저지연 메시지 브로커로 실시간 채팅 전파
- MySQL: 트랜잭션 중심 도메인용 관계형 DB
- MongoDB : 문서지향 NoSQL(채팅 데이터 저장)
- JPA (Hibernate): 객체-관계 매핑으로 도메인 모델과 RDB 연동
- WebSocket (STOMP): 양방향 실시간 통신(채팅)
- GitHubActions : CI/CD 자동화(빌드·테스트·배포 워크플로)
- Prometheus&Grafana : 매트릭 수집/알림 + 대시보드 시각화
- SpringAI : LLM 연동으로 키워드/추천 등 AI 기능 구현
- AWS(S3) : 프로필 이미지 구현
- FCM : 모바일 푸시 알림 전송(매칭,메시지 알림)
- Flyway : 데이터베이스 스키마 버전 관리/마이그레이션


---

## 🪾 Branch

- 컨벤션/이슈번호-이슈내용
    - setting/1-init
    - feat/2-loginAPI

---

## 📝 Commit Convention

|    커밋 타입    |                  설명                  | 커밋 메시지 예시                     |
|:-----------:|:------------------------------------:|:------------------------------|
|   ✨ Feat    |              새로운 기능 추가               | [FEAT] #123: 로그인 기능 추가        |
|   🐛 Fix    |                버그 수정                 | [FIX] #99: 회원가입 오류 수정         |
|   📄 Docs   |                문서 수정                 | [DOCS] #45: README 파일 수정      |
| ♻️ Refactor |               코드 리팩토링                | [REFACTOR] #32: 회원 도메인 구조 개선  |
|  📦 Chore   | 빌드/패키지 매니저 등 production code와 무관한 변경 | [CHORE] #11: .gitignore 파일 수정 |
| 💬 Comment  |              주석 추가 및 변경              | [COMMENT] #101: 함수 설명 주석 추가   |
|  🔥 Remove  |             파일 또는 폴더 삭제              | [REMOVE] #88: 불필요한 파일 삭제      |
|  🚚 Rename  |             파일 또는 폴더명 수정             | [RENAME] #75: util 폴더명 변경     |


---


## 🛠️ Architecture

<img width="2125" height="814" alt="diagram-export-8-13-2025-6_00_21-PM (1)" src="https://github.com/user-attachments/assets/5f2b22c3-6706-4169-abd3-d68f2ba3e200" />


---

## 🙋‍♂️ 팀원들

| BE(팀장) | BE | BE | BE | BE |
|:--------:|:--:|:--:|:--:|:--:|
| <img src="https://avatars.githubusercontent.com/u/150355097?v=4" width="120"/> <br> [박규민](https://github.com/FrontHeadlock) | <img src="https://avatars.githubusercontent.com/u/162654709?v=4" width="120"/> <br> [최민수](https://github.com/CMIN-SU) | <img src="https://avatars.githubusercontent.com/u/127833888?v=4" width="120"/> <br> [이성원](https://github.com/lsw71311) | <img src="https://avatars.githubusercontent.com/u/144093044?v=4" width="120"/> <br> [이해령](https://github.com/haerxeong) | <img src="https://avatars.githubusercontent.com/u/185888445?v=4" width="120"/> <br> [최진서](https://github.com/jinseo4829) |



