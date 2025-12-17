# Contributing to Socrates-BE

이 문서는 Socrates-BE 레포지토리에 기여할 때 따라야 할 **브랜치·커밋·PR·테스트·CI/CD 컨벤션**을 정리한 가이드입니다.  
세부적인 훅/테스트 설정은 추후 `docs/` 하위 문서가 추가되면 함께 참고해 주세요.

---

## 1. 전체 워크플로우

1. Issue/작업 범위 정의  
2. 브랜치 생성 (`feature/*` 등)  
3. 작업 + 로컬 테스트 실행  
4. 커밋 (Husky 훅을 통과해야 함)  
5. GitHub에 브랜치 푸시  
6. PR 생성 → 리뷰 → 머지  

---

## 2. 브랜치 전략

- 메인 브랜치  
  - `main`: 배포용 안정 브랜치  
  - `develop`: 개발용 통합 브랜치  

- 작업 브랜치 (예시)  
  - `feature/<scope>`: 기능 추가 (`feature/post-like`, `feature/webflux-ai` 등)  
  - `fix/<scope>`: 버그 수정 (`fix/login-error`, `fix/file-upload-timeout` 등)  
  - `refactor/<scope>`: 리팩터링 (`refactor/querydsl-post`, …)  
  - `docs/<scope>`: 문서 작업  

- 규칙  
  - 브랜치 이름에는 **작업 범위가 드러나도록** 작성합니다.  
  - 단일 PR에는 가급적 **하나의 논리적 변경**만 포함합니다.  

---

## 3. 커밋 컨벤션 (Husky 연동)

- 커밋 메시지 첫 줄 포맷  
  - `타입(소문자): 설명` 형식으로 작성하면, Husky가 자동으로 **이모지 + 타입**으로 변환합니다.  
  - 예:  
    - `feat: 게시글 좋아요 기능 추가`  
    - `fix: 로그인 실패 예외 처리 수정`  
    - `docs: WebFlux README 추가`  

- 허용 타입  
  - `feat`, `fix`, `perf`, `refactor`, `test`, `docs`, `style`, `chore`, `delopy`, `revert`  
  - 다른 타입으로 시작하면 커밋이 거절됩니다.  

- Git 기본 메시지  
  - `Merge`, `Revert` 등 Git이 자동 생성한 메시지는 예외 처리됩니다.  

- 현재 Husky 훅 개요  
  - `pre-commit`: Staged 된 Java 파일에 대해 `./gradlew spotlessApply` 를 실행해 코드 포맷을 맞춥니다.  
  - `prepare-commit-msg`: 커밋 타입을 검증하고, 앞에 자동으로 이모지를 붙여줍니다.  
  - `pre-push`: `./gradlew test` 를 실행해 기본 테스트를 통과해야 푸시가 진행됩니다.  

추가적인 Husky 규칙/예시는 추후 `docs/husky/` 하위 문서로 분리할 수 있습니다.  

---

## 4. PR 규칙

- PR 제목  
  - 가능하면 커밋 타입과 유사한 형태로 간단히 요약합니다.  
    - 예: `[feat] 게시글 조회 API 정렬 옵션 추가`  

- PR 설명  
  - **무엇을 / 왜** 수정했는지 3~5줄 이내로 요약  
  - 주요 변경 파일/영향 범위, 테스트 여부를 간단히 명시  

- 리뷰  
  - 큰 PR보다는 **자주, 작은 단위**의 PR을 선호합니다.  
  - 리뷰 코멘트에 맞춰 추가 커밋을 푸시합니다. (가능하면 force-push 지양)  

또한 CodeRabbit 설정(`.coderabbit.yml`)에 따라 **develop 브랜치로 향하는 PR**에 대해서만 자동 코드 리뷰가 수행됩니다.  

---

## 5. 테스트 & 테스트 컨벤션

- 로컬에서 최소한 아래를 확인해 주세요.  
  - 전체 테스트: `./gradlew test`  
  - (선택) 필요 시 모듈별 테스트 실행:  
    - 예: `./gradlew :app-webflux-chat:test`  
    - 예: `./gradlew :app-mvc-analytics:test`  

- 테스트 작성 원칙 (요약)  
  - **도메인/정책 로직**: 유닛 테스트 위주  
  - **JPA/쿼리**: 리포지토리 통합 테스트  
  - **서비스/보안/필터**: 통합 테스트 또는 슬라이스 테스트  
  - **컨트롤러**: `@WebMvcTest` 또는 통합 테스트 기반 API 검증  

추가적인 세부 가이드는 `docs/test/` 디렉터리 하위 문서로 분리해 나갈 수 있습니다.  

---

## 6. CI / CD 파이프라인

현재 레포에서는 **빌드·테스트 기준을 엄격하게 지키는 것**을 목표로 합니다.

- GitHub Actions CI (예정 또는 도입 시 기준)  
  - 동작 예시:  
    - `main`, `develop` 브랜치로의 push 시 실행  
    - `docs/**`, `*.md` 만 변경된 커밋은 CI 스킵  
    - `./gradlew test` 실행 후, `./gradlew build -x test` 빌드 수행  
    - 테스트 리포트 및 JaCoCo 커버리지를 아티팩트로 업로드  

- Husky `pre-push` 훅 (로컬)  
  - 로컬에서 `git push` 시  
    - 현재는 `./gradlew test` 를 실행합니다.  
    - 추후 필요 시 `jacocoTestCoverageVerification` 등을 함께 실행하도록 확장할 수 있습니다.  

CD(배포)는 별도 스크립트/인프라 레포 또는 수동 절차를 사용할 수 있으며,  
이 레포에서는 **기본 빌드·테스트 기준을 보장하는 것**에 집중합니다.  

---

## 7. 기여 체크리스트

PR을 열기 전에 아래 항목을 한 번 더 확인해 주세요.

- [ ] 브랜치 이름이 작업 범위를 잘 표현하는가?  
- [ ] 커밋 메시지가 컨벤션(Husky)을 통과하는가?  
- [ ] 관련 테스트를 작성/수정했고, 로컬에서 성공하는가?  
- [ ] (도입된 경우) CI 상태가 초록(성공)인가?  
- [ ] 변경 사항이 기존 아키텍처/도메인/테스트 컨벤션과 어긋나지 않는가?  

