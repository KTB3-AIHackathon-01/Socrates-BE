# WebFlux Chat API 명세 (간단 버전)

## 공통 정보

- Base URL: `http://{host}:{port}` (기본 포트: `8080`)
- 공통 Prefix: `/api/chat`
- 인증: 없음 (개발용)

---

## 1. 채팅 스트림 요청

- 메서드 / URL  
  - `POST /api/chat/stream`

- 설명  
  - 사용자의 메시지를 전송하면, 서버가 **Server-Sent Events (SSE)** 방식으로 응답을 스트리밍합니다.
  - 한 번의 요청에 대해 여러 개의 텍스트 조각이 순차적으로 전송됩니다.

- 요청 헤더  
  - `Content-Type: application/json`
  - `Accept: text/event-stream`

- 요청 바디 (`ChatRequest`)

```json
{
  "message": "안녕하세요",   // 필수, 공백 불가
  "userId": "demo-user",   // 선택
  "sessionId": "session-1" // 선택 (없으면 클라이언트에서 생성 권장)
}
```

- 정상 응답
  - HTTP Status: `200 OK`
  - 헤더: `Content-Type: text/event-stream;charset=UTF-8`
  - 바디 형식: SSE 텍스트 스트림

    예시 (data: 라인이 여러 번 도착):

    ```text
    data: 더미 응답입니다. 입력하신 메시지: 안녕하세요

    data: (userId=demo-user, sessionId=session-1)

    ```

  - 실제 FastAPI 연동 시에는 `FastApiChatResponse` 형식의 스트림을 받아
    - `content`: 모델이 생성한 텍스트 조각
    - `isComplete`: 세션 종료 여부
    를 기준으로 WebFlux 서버가 조합/저장을 수행합니다.

- 에러 응답 (예시)
  - `400 Bad Request`
    - `message` 필드가 비어 있는 경우 (Bean Validation 실패)
  - `5xx`
    - 내부 서버 오류, 하위 FastAPI 서버 오류 등

---

## 2. 헬스 체크

- 메서드 / URL  
  - `GET /api/chat/health`

- 설명  
  - WebFlux 채팅 서버의 기동 여부를 확인하기 위한 단순 헬스 체크 엔드포인트입니다.

- 요청 바디  
  - 없음

- 응답
  - HTTP Status: `200 OK`
  - Body (`text/plain`): `"OK"`

---

## 3. 개발용 더미 클라이언트

- 정적 HTML 클라이언트  
  - 경로: `/chat-client.html`  
  - 파일: `src/main/resources/static/chat-client.html`
  - 기능:
    - `userId`, `sessionId`, `message` 입력 UI 제공
    - `/api/chat/stream` 엔드포인트와 SSE 기반으로 직접 연동
    - 한글 IME(조합) 입력 시 Enter 처리 보완 (조합 중 Enter는 전송하지 않음)

