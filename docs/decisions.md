# 결정 기록 (Decision Log)

새로운 중요한 설계 결정이 생기면 여기에 날짜와 함께 짧게 추가. CLAUDE.md의 "결정 기록" 섹션 요약과 동기화 유지.

## 2026-09
- **앱 이름**: 책다락 / Booknook 확정. 패키지명 `com.booknook.app`
- **기술 스택**: Kotlin 네이티브, View 시스템(XML) 선택 — 이북리더기 성능 고려해 Compose 제외
- **동기화**: 자동 클라우드 동기화 없음. 수동 백업/복원(JSON, SAF 기반)으로 결정
- **Google Play Services**: 초기 미의존, Repository 패턴으로 추상화해두어 추후 확장 가능하게 설계
- **Play 스토어 출시**: 지금 설계로도 가능 — minSdk(21, 이북리더기용)와 targetSdk(36, Play 정책용) 분리해서 관리
- **DB**: Room 4개 엔티티로 시작 — Book / Note / ReadingLog / Badge
