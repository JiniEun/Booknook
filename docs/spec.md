# Booknook (책다락) 스펙 & 기능 설계

책다락 = book nook, 아늑한 독서 공간. 패키지명 `com.booknook.app`.

## 컨셉

귀엽고 쓸만한 개인용 독서기록 앱. 폰이랑 이북리더기(e-ink) 둘 다에서 쓴다.

- 서버 없음. 로컬 DB(Room/SQLite)가 항상 원본이고 백업/복원은 수동
- 클라우드 동기화는 나중에 옵션으로만, 로컬 우선 원칙은 유지
- 기록 입력은 심플하게, 여기에 독후감/메모 + 가벼운 재미 요소 얹기

## 기술 스택

- Kotlin, View 시스템(XML). Compose는 리컴포지션 오버헤드 때문에 저사양 e-ink에서 불리해서 안 씀
- Room(SQLite), 완전 로컬
- minSdk 21 / targetSdk 36 — 구형 기기 호환이랑 Play 정책 준수를 같이 챙기려고 둘을 분리해서 관리
- Play Services는 초기엔 안 씀. 대신 Repository 패턴으로 데이터 계층을 분리해둬서 나중에 클라우드 동기화 얹기 쉽게 설계
  - `BookRepository`(인터페이스) / `LocalBookRepository`(Room 구현체, 항상 동작) → 나중에 `CloudSyncRepository`를 추가해서 갈아 끼우는 구조. `LocalBookRepository` 로직은 건드리지 않기
  - Play Services 있으면 클라우드 동기화 옵션 노출, 없으면 로컬만 쓰게
- 배포는 서명된 APK 직접 빌드해서 사이드로드

## 데이터 모델 (Room Entity 초안)

### books

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Int (PK) | |
| title | String | 제목 |
| author | String? | 저자 |
| coverPath | String? | 로컬 이미지 경로 |
| status | Enum | WANT_TO_READ / READING / COMPLETED / PAUSED |
| startDate | Long? | |
| endDate | Long? | |
| progressPercent | Int | 0~100 |
| rating | Int? | 0~5 |
| tags | String? | 콤마 구분 문자열 (MVP는 심플하게) |
| createdAt / updatedAt | Long | |

### notes (독후감/메모/하이라이트)

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Int (PK) | |
| bookId | Int (FK) | |
| type | Enum | HIGHLIGHT / REVIEW / MEMO |
| content | String | |
| pageNumber | Int? | |
| createdAt | Long | |

### reading_logs (일별 기록, 스트릭/뱃지용)

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Int (PK) | |
| bookId | Int (FK) | |
| date | Long | |
| pagesRead | Int? | |
| memo | String? | |

### badges

| 필드 | 타입 | 설명 |
|---|---|---|
| id | Int (PK) | |
| code | String | 뱃지 식별자 |
| earnedAt | Long | |

## 화면 구성

1. 홈 — 오늘의 기록 버튼(원터치 진입), 읽는 중인 책 카드, 스트릭 표시, 마스코트 + 상태 메시지
2. 책장 — 상태별 탭(읽고싶어요/읽는 중/완료/중단), 리스트/그리드 뷰
3. 책 상세 — 진행률 슬라이더, 별점, 메모/독후감/하이라이트 리스트, "오늘 기록 추가" 버튼
4. 빠른 기록 — 페이지 수 or 진행률 %만 입력하고 저장, 2탭 이내 완료가 목표
5. 독후감 작성 — 텍스트 입력, 완독하면 자동으로 유도
6. 업적/뱃지 — 획득한 뱃지, 다음 목표 안내
7. 설정 — 백업/복원, 이북리더기 모드(애니메이션 끄기) 토글, 테마

## 핵심 기능

책 등록은 제목/저자 직접 입력 (검색 API 없이 수동으로, 서버 비용 없음 원칙). 표지는 갤러리에서 골라 로컬 저장. 진행률/페이지는 빠른 기록 화면에서 탭 최소로 처리.

책마다 노트 여러 개 작성 가능(하이라이트/리뷰/메모 구분). 완독 처리하면 "이 책 어땠나요?" 팝업으로 리뷰 작성 유도.

재미 요소(게이미피케이션) 가볍게:

- 스트릭: 연속 기록일 카운트, 끊기면 리셋
- 뱃지 예시: 첫 완독 / 한 달 5권 완독 / 장르 5종류 이상 / 스트릭 7일·30일
- 마스코트 성장: 완독 권수 누적에 따라 캐릭터 단계 변화 (정적 이미지 교체 방식, e-ink 친화적)
- 완독하면 스티커 획득, 책장 화면에서 모아보기

## 백업/복원

설정 화면 "백업" 버튼으로 전체 DB를 JSON으로 내보낸다. SAF(Storage Access Framework)로 사용자가 위치를 직접 고르게 하고, `Download/` 같은 경로는 직접 건드리지 않는다. 복원도 SAF로 파일을 골라서 불러오는 방식. 충돌 나면 `updatedAt` 기준 최신 우선, 아니면 덮어쓰기/병합 선택지 제공. 기기 간 이동은 USB나 블루투스 파일 전송으로.

## 이북리더기(e-ink) 최적화

- 이북리더기 모드 토글 시 전환 애니메이션/리플 이펙트 전부 off
- 고대비 흑백 팔레트 + 포인트 컬러 1개만
- 큰 터치 영역, 스와이프보다 탭 위주
- 리스트 스크롤은 RecyclerView로 부분 갱신되게 해서 전체화면 리프레시 줄이기
- 고해상도 이미지·GIF 지양, 벡터/저용량 PNG 사용
- 폰트 크기는 넉넉하게, 저대비 회색 텍스트는 피하기

## Play 스토어 출시 대비

minSdk는 낮게 유지하고 targetSdk만 Play 정책에 맞춰 올리면 되니까 둘 다 만족 가능. Scoped Storage 때문에 백업 파일은 SAF로 처리해야 Android 10+ 정책이랑 심사를 통과한다 (백업/복원 섹션 참고). 그 외에 챙길 것들:

- 개인정보처리방침 — 로컬 전용이어도 Play Console 등록엔 필요, 간단한 정적 페이지면 충분
- Data Safety 양식은 "서버로 데이터 전송 없음"으로 신고, 로컬 우선 구조라 심사가 단순함
- 개발자 계정 등록비 $25 (1회)
- 서명은 Play App Signing 권장

사이드로드 배포랑 100% 호환되는 구조라 지금 순서(사이드로드 먼저, 스토어는 나중)로 가도 문제없다.

## 향후 확장 아이디어

- 자동 동기화(Google Drive API 등) — `CloudSyncRepository` 추가해서 확장
- 통계/그래프 화면 (장르 분포, 월별 독서량 차트)
- 책 검색 API 연동 (표지/정보 자동완성) — 네트워크 비용 발생하니 보류
- 위젯 (홈 화면에서 바로 기록)

---
