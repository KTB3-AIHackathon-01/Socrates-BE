# 1단계: 빌드 스테이지
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Gradle 래퍼 및 설정 파일 복사
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle/

# 각 모듈의 build.gradle 복사 (캐싱을 위해 먼저 수행)
COPY app-webflux-chat/build.gradle app-webflux-chat/
COPY app-mvc-analytics/build.gradle app-mvc-analytics/

# 의존성 미리 다운로드 (소스 코드 복사 전 수행하여 캐싱 극대화)
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true

# 전체 소스 코드 복사 (명시적으로 소스 디렉토리 포함)
COPY app-webflux-chat/src app-webflux-chat/src
COPY app-mvc-analytics/src app-mvc-analytics/src

# 빌드 인자 설정
ARG MODULE=app-mvc-analytics
RUN ./gradlew :${MODULE}:bootJar --no-daemon -x test

# JAR 파일 경로 정리
RUN cp ${MODULE}/build/libs/*.jar app.jar

# 2단계: 실행 스테이지
FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app

# 보안 및 타임존 설정
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup && \
    apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

COPY --from=builder /app/app.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser

# 환경 변수 및 포트 설정
ARG APP_PORT=8080
ENV PORT=${APP_PORT}

# JVM 최적화 옵션
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

# 헬스체크 (정적 포트가 아닌 환경 변수 활용)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
