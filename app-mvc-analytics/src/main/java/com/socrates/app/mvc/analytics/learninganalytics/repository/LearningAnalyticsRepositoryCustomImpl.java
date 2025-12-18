package com.socrates.app.mvc.analytics.learninganalytics.repository;

import com.socrates.app.mvc.analytics.learninganalytics.domain.LearningAnalytics;
import com.socrates.app.mvc.analytics.learninganalytics.dto.DailyProgressDto;
import com.socrates.app.mvc.analytics.learninganalytics.dto.QuestionTypeRatioDto;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
@RequiredArgsConstructor
public class LearningAnalyticsRepositoryCustomImpl implements LearningAnalyticsRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME = "learning_analytics";
    private static final DateOperators.Timezone DEFAULT_TIMEZONE =
            DateOperators.Timezone.valueOf("Asia/Seoul");

    @Override
    public long countActiveStudents(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("updatedAt").gte(startDate).lte(endDate)),
                group("studentId"),
                count().as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getInteger("total", 0) : 0;
    }

    @Override
    public long countActiveStudentsByStudentIds(List<String> studentIds, LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("studentId").in(studentIds)
                        .and("updatedAt").gte(startDate).lte(endDate)),
                group("studentId"),
                count().as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getInteger("total", 0) : 0;
    }

    @Override
    public long countDistinctStudents() {
        Aggregation aggregation = newAggregation(
                group("studentId"),
                count().as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getInteger("total", 0) : 0;
    }

    @Override
    public Double getAverageUnderstandingScore() {
        Aggregation aggregation = newAggregation(
                unwind("conceptMastery"),
                group().avg("conceptMastery.understandingScore").as("avgScore")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getDouble("avgScore") : null;
    }

    @Override
    public Double getAverageUnderstandingScoreByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("updatedAt").gte(startDate).lte(endDate)),
                unwind("conceptMastery"),
                group().avg("conceptMastery.understandingScore").as("avgScore")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getDouble("avgScore") : null;
    }

    @Override
    public Double getAverageUnderstandingScoreByStudentIds(List<String> studentIds) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("studentId").in(studentIds)),
                unwind("conceptMastery"),
                group().avg("conceptMastery.understandingScore").as("avgScore")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getDouble("avgScore") : null;
    }

    @Override
    public List<DailyProgressDto> getDailyProgressStats(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("updatedAt").gte(startDate).lte(endDate)),
                project()
                        .and(DateOperators.DateToString.dateOf("updatedAt")
                                .toString("%Y-%m-%d")
                                .withTimezone(DEFAULT_TIMEZONE)).as("date")
                        .and("learningSummary.overallProgressScore").as("progressScore"),
                group("date")
                        .avg("progressScore").as("avgProgress")
                        .count().as("count"),
                sort(Sort.Direction.ASC, "_id"),
                project()
                        .and("_id").as("date")
                        .and("avgProgress").as("avgProgress")
                        .and("count").as("count")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, DailyProgressDto.class)
                .getMappedResults();
    }

    @Override
    public QuestionTypeRatioDto getAverageQuestionTypeRatio() {
        Aggregation aggregation = newAggregation(
                group()
                        .avg("learningBehavior.questionTypeRatio.definition").as("avgDefinition")
                        .avg("learningBehavior.questionTypeRatio.mechanism").as("avgMechanism")
                        .avg("learningBehavior.questionTypeRatio.comparison").as("avgComparison")
        );

        AggregationResults<QuestionTypeRatioDto> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, QuestionTypeRatioDto.class
        );

        return results.getUniqueMappedResult();
    }

    @Override
    public QuestionTypeRatioDto getAverageQuestionTypeRatioByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("updatedAt").gte(startDate).lte(endDate)),
                group()
                        .avg("learningBehavior.questionTypeRatio.definition").as("avgDefinition")
                        .avg("learningBehavior.questionTypeRatio.mechanism").as("avgMechanism")
                        .avg("learningBehavior.questionTypeRatio.comparison").as("avgComparison")
        );

        AggregationResults<QuestionTypeRatioDto> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, QuestionTypeRatioDto.class
        );

        return results.getUniqueMappedResult();
    }

    @Override
    public List<LearningAnalytics> findLatestAnalyticsByStudentIds(List<String> studentIds) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("studentId").in(studentIds)),
                sort(Sort.Direction.DESC, "updatedAt"),
                group("studentId").first("$$ROOT").as("doc"),
                replaceRoot("doc")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, LearningAnalytics.class)
                .getMappedResults();
    }

    @Override
    public Double getStudentAverageUnderstanding(String studentId) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("studentId").is(studentId)),
                unwind("conceptMastery"),
                group().avg("conceptMastery.understandingScore").as("avgScore")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getDouble("avgScore") : null;
    }

    @Override
    public long countStudentsWhoMasteredConcept(String concept) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("conceptMastery").elemMatch(
                        Criteria.where("concept").is(concept)
                                .and("status").is("MASTERED")
                )),
                group("studentId"),
                count().as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getInteger("total", 0) : 0;
    }

    @Override
    public long countStudentsBelowProgressThreshold(double threshold) {
        Aggregation aggregation = newAggregation(
                sort(Sort.Direction.DESC, "updatedAt"),
                group("studentId").first("learningSummary.overallProgressScore").as("latestProgress"),
                match(Criteria.where("latestProgress").lt(threshold)),
                count().as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? result.getInteger("total", 0) : 0;
    }

    @Override
    public List<LearningAnalytics> findStudentsWithUnresolvedStuckConcepts() {
        Query query = new Query(
                Criteria.where("learningDifficulty.stuckConcepts").elemMatch(
                        Criteria.where("resolved").is(false)
                )
        );

        return mongoTemplate.find(query, LearningAnalytics.class, COLLECTION_NAME);
    }
}
