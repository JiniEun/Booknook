# Booknook (책다락)

귀엽고 쓸만한 개인용 독서기록 앱. 로컬 우선(local-first), 이북리더기 최적화, 서버 비용 없음.

자세한 기획/스펙은 `docs/spec.md` 참고.

## 로컬 세팅
1. 이 zip을 `/Users/jules/Projects/AllProjects/AIProjects/Booknook` 에 풀기
2. Android Studio에서 `Open` → 해당 폴더 선택
3. Gradle sync (처음엔 의존성 다운로드로 시간이 걸릴 수 있음)
4. `app` 모듈 실행 (에뮬레이터 또는 실기기)

## 현재 포함된 것
- Gradle 프로젝트 골격 (minSdk 21 / targetSdk 36)
- Room 엔티티: Book, Note, ReadingLog, Badge
- DAO + AppDatabase
- Repository 패턴 (`BookRepository` / `LocalBookRepository`) — 추후 클라우드 동기화 확장 대비
- 최소 MainActivity (빈 화면, 타이틀만 표시)

## 다음 할 일
- 홈 / 책장 / 책 상세 / 빠른 기록 / 설정 화면 UI
- 백업(JSON export) / 복원 (SAF 사용)
- 스트릭·뱃지 로직
