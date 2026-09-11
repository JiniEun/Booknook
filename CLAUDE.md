# CLAUDE.md

이 파일은 Claude Code가 이 프로젝트에서 작업할 때 참고하는 컨텍스트 파일입니다.

## 프로젝트 개요
**Booknook (책다락)** — 귀엽고 쓸만한 개인용 독서기록 안드로이드 앱.

- 자세한 기획/기능 스펙: `docs/spec.md` (항상 최신 상태로 유지, 기능 추가/변경 시 여기부터 업데이트)
- 상세 설계 이력: `docs/decisions.md` (아래 "결정 기록" 섹션 참고, 새 결정은 여기에도 추가)

## 핵심 원칙 (변경 시 반드시 사용자 확인)
1. **서버 없음** — 서버 비용 0원 원칙. 네트워크 통신 추가하지 말 것 (클라우드 동기화는 옵션으로만, 로컬이 항상 source of truth)
2. **이북리더기 호환** — 저사양 e-ink 기기에서도 동작해야 함. 애니메이션/트랜지션 최소화, 고대비 흑백 + 포인트 컬러 1개, 큰 터치 영역
3. **APK 사이드로드 우선** — Play 스토어 출시는 나중 옵션. 단, targetSdk는 항상 Play 정책 최신 기준 유지 (minSdk는 낮게 유지해 구형 기기 호환)
4. **로컬 DB가 원본** — Room/SQLite. 백업/복원은 수동 JSON export/import (SAF 사용, `Download/` 직접 접근 금지 — Scoped Storage 정책)

## 기술 스택
- Kotlin, 전통 View 시스템 (XML) — Compose 아님 (e-ink 리컴포지션 오버헤드 회피 목적)
- Room (로컬 DB), Coroutines + Flow
- minSdk 21 / targetSdk 36 (Play 정책 변경 시 이 파일과 `app/build.gradle.kts` 동시 업데이트)
- 패키지명: `com.booknook.app`

## 아키텍처
- `data/` — Room Entity, DAO, `AppDatabase`, `BookRepository`(인터페이스) / `LocalBookRepository`(구현체)
  - 나중에 클라우드 동기화 추가 시 `CloudSyncRepository`를 새로 구현해서 교체하는 방식 유지 (기존 `LocalBookRepository` 로직 건드리지 말 것)
- `ui/` — Activity/Fragment, 화면별 로직
- 새 기능 추가 시: Entity/DAO 변경 → Repository 인터페이스에 메서드 추가 → UI에서 Repository를 통해서만 접근 (DAO 직접 호출 금지)

## 데이터 모델 요약 (자세한 건 docs/spec.md 참고)
- `Book` — 책 1권당 1행 (제목/저자/상태/진행률/별점/태그)
- `Note` — 책에 딸린 메모/독후감/하이라이트 (다대일)
- `ReadingLog` — 일별 기록 (스트릭/뱃지 계산용)
- `Badge` — 획득한 업적

## 결정 기록
- 앱 이름: 책다락 / Booknook (은유: book nook = 아늑한 독서 공간)
- UI 프레임워크: Compose 대신 View 시스템 선택 (이북리더기 성능 이유)
- 동기화 방식: 자동 동기화 없음, 수동 백업/복원 파일(JSON)로 충분 (2026-09 결정)
- Play 스토어 출시: 나중에 고려하되 처음부터 호환되게 설계 (minSdk/targetSdk 분리, SAF 사용)

## 코딩 컨벤션
- 한글 주석/커밋 메시지 무방 (개인 프로젝트)
- Repository 패턴 우회해서 DAO 직접 호출하는 코드 작성 금지
- 새 화면 만들 때 이북리더기 모드(애니메이션 off 토글) 고려해서 트랜지션 없는 버전도 항상 동작하게 작성

## 다음 할 일 (우선순위 순)
1. 홈 화면 UI — 오늘의 기록 버튼, 읽는 중인 책 카드, 스트릭 표시
2. 빠른 기록 화면 — 진행률/페이지 입력, 2탭 이내 저장
3. 책 상세 화면 — 메모/독후감 리스트, 진행률 슬라이더
4. 백업/복원 (SAF 기반 JSON export/import)
5. 뱃지/업적 로직 및 화면
6. 이북리더기 모드 토글 (설정 화면)

## 자주 쓰는 명령
```bash
./gradlew assembleDebug      # 디버그 APK 빌드
./gradlew installDebug       # 연결된 기기에 설치
./gradlew test               # 유닛 테스트
```
