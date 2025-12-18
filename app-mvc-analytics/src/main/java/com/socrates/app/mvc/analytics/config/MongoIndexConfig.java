package com.socrates.app.mvc.analytics.config;

import com.socrates.app.mvc.analytics.chat.domain.ChatMessage;
import com.socrates.app.mvc.analytics.learninganalytics.domain.LearningAnalytics;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MongoIndexConfig {

    private final MongoTemplate mongoTemplate;

    @PostConstruct
    public void initIndexes() {
        createLearningAnalyticsIndexes();
        createChatMessageIndexes();
        log.info("MongoDB indexes created successfully");
    }

    private void createLearningAnalyticsIndexes() {
        // studentId + updatedAt 복합 인덱스 (학생별 최신 분석 조회)
        mongoTemplate.indexOps(LearningAnalytics.class)
                .ensureIndex(new Index()
                        .on("studentId", Sort.Direction.ASC)
                        .on("updatedAt", Sort.Direction.DESC)
                        .named("idx_student_updated"));

        // updatedAt 단일 인덱스 (기간별 조회)
        mongoTemplate.indexOps(LearningAnalytics.class)
                .ensureIndex(new Index()
                        .on("updatedAt", Sort.Direction.DESC)
                        .named("idx_updated"));

        // sessionId 단일 인덱스 (세션별 조회)
        mongoTemplate.indexOps(LearningAnalytics.class)
                .ensureIndex(new Index()
                        .on("sessionId", Sort.Direction.ASC)
                        .named("idx_session"));

        // conceptMastery.concept + conceptMastery.status 복합 인덱스
        mongoTemplate.indexOps(LearningAnalytics.class)
                .ensureIndex(new Index()
                        .on("conceptMastery.concept", Sort.Direction.ASC)
                        .on("conceptMastery.status", Sort.Direction.ASC)
                        .named("idx_concept_status"));

        // learningSummary.overallProgressScore 인덱스
        mongoTemplate.indexOps(LearningAnalytics.class)
                .ensureIndex(new Index()
                        .on("learningSummary.overallProgressScore", Sort.Direction.ASC)
                        .named("idx_progress_score"));
    }

    private void createChatMessageIndexes() {
        // userId + createdAt 복합 인덱스 (사용자별 최신 메시지 조회)
        mongoTemplate.indexOps(ChatMessage.class)
                .ensureIndex(new Index()
                        .on("userId", Sort.Direction.ASC)
                        .on("createdAt", Sort.Direction.DESC)
                        .named("idx_user_created"));

        // createdAt 단일 인덱스 (기간별 조회)
        mongoTemplate.indexOps(ChatMessage.class)
                .ensureIndex(new Index()
                        .on("createdAt", Sort.Direction.DESC)
                        .named("idx_created"));

        // sessionId 단일 인덱스 (세션별 조회)
        mongoTemplate.indexOps(ChatMessage.class)
                .ensureIndex(new Index()
                        .on("sessionId", Sort.Direction.ASC)
                        .named("idx_session"));

        // status 인덱스 (상태별 조회)
        mongoTemplate.indexOps(ChatMessage.class)
                .ensureIndex(new Index()
                        .on("status", Sort.Direction.ASC)
                        .named("idx_status"));
    }
}
