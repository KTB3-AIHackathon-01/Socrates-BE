package com.socrates.app.mvc.analytics.chat.repository;

import com.socrates.app.mvc.analytics.chat.domain.ChatMessage;
import com.socrates.app.mvc.analytics.chat.dto.*;
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
public class ChatMessageRepositoryCustomImpl implements ChatMessageRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME = "chat_messages";

    @Override
    public long countMessagesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query(
                Criteria.where("createdAt").gte(startDate).lte(endDate)
        );
        return mongoTemplate.count(query, ChatMessage.class);
    }

    @Override
    public long countTodayMessages(LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return countMessagesByDateRange(startOfDay, endOfDay);
    }

    @Override
    public long countMessagesByUserIdsAndDateRange(List<String> userIds, LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query(
                Criteria.where("userId").in(userIds)
                        .and("createdAt").gte(startDate).lte(endDate)
        );
        return mongoTemplate.count(query, ChatMessage.class);
    }

    @Override
    public List<DailyMessageCountDto> getDailyMessageCount(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("createdAt").gte(startDate).lte(endDate)),
                project()
                        .and(DateOperators.DateToString.dateOf("createdAt").toString("%Y-%m-%d")).as("date"),
                group("date").count().as("messageCount"),
                sort(Sort.Direction.ASC, "_id"),
                project()
                        .and("_id").as("date")
                        .and("messageCount").as("messageCount")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, DailyMessageCountDto.class)
                .getMappedResults();
    }

    @Override
    public List<HourlyMessageCountDto> getHourlyMessageCount(LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("createdAt").gte(startDate).lte(endDate)),
                project()
                        .and(DateOperators.Hour.hourOf("createdAt")).as("hour"),
                group("hour").count().as("messageCount"),
                sort(Sort.Direction.ASC, "_id"),
                project()
                        .and("_id").as("hour")
                        .and("messageCount").as("messageCount")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, HourlyMessageCountDto.class)
                .getMappedResults();
    }

    @Override
    public List<UserMessageStatsDto> getUserMessageStats(List<String> userIds) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("userId").in(userIds)),
                sort(Sort.Direction.DESC, "createdAt"),
                group("userId")
                        .count().as("messageCount")
                        .first("createdAt").as("lastMessageTime"),
                project()
                        .and("_id").as("userId")
                        .and("messageCount").as("messageCount")
                        .and("lastMessageTime").as("lastMessageTime")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, UserMessageStatsDto.class)
                .getMappedResults();
    }

    @Override
    public List<UserMessageStatsDto> getUserMessageStatsByDateRange(List<String> userIds, LocalDateTime startDate, LocalDateTime endDate) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("userId").in(userIds)
                        .and("createdAt").gte(startDate).lte(endDate)),
                sort(Sort.Direction.DESC, "createdAt"),
                group("userId")
                        .count().as("messageCount")
                        .first("createdAt").as("lastMessageTime"),
                project()
                        .and("_id").as("userId")
                        .and("messageCount").as("messageCount")
                        .and("lastMessageTime").as("lastMessageTime")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, UserMessageStatsDto.class)
                .getMappedResults();
    }

    @Override
    public List<ChatMessage> findLatestMessagesByUserIds(List<String> userIds) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("userId").in(userIds)),
                sort(Sort.Direction.DESC, "createdAt"),
                group("userId").first("$$ROOT").as("doc"),
                replaceRoot("doc")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, ChatMessage.class)
                .getMappedResults();
    }

    @Override
    public Long countDistinctActiveUsers(LocalDateTime sinceDateTime) {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("createdAt").gte(sinceDateTime)),
                group("userId"),
                count().as("total")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, Document.class
        );

        Document result = results.getUniqueMappedResult();
        return result != null ? (long) result.getInteger("total", 0) : 0L;
    }

    @Override
    public ResponseTimeStatsDto getAverageResponseTime() {
        Aggregation aggregation = newAggregation(
                match(Criteria.where("completedAt").exists(true)
                        .and("createdAt").exists(true)
                        .and("status").is("COMPLETED")),
                project()
                        .and(ArithmeticOperators.Subtract.valueOf("completedAt").subtract("createdAt")).as("responseTime"),
                group()
                        .avg("responseTime").as("avgResponseTime")
                        .min("responseTime").as("minResponseTime")
                        .max("responseTime").as("maxResponseTime")
        );

        AggregationResults<ResponseTimeStatsDto> results = mongoTemplate.aggregate(
                aggregation, COLLECTION_NAME, ResponseTimeStatsDto.class
        );

        return results.getUniqueMappedResult();
    }

    @Override
    public List<SessionMessageStatsDto> getSessionMessageStats() {
        Aggregation aggregation = newAggregation(
                group("sessionId")
                        .count().as("messageCount")
                        .min("createdAt").as("firstMessage")
                        .max("createdAt").as("lastMessage"),
                project()
                        .and("_id").as("sessionId")
                        .and("messageCount").as("messageCount")
                        .and("firstMessage").as("firstMessage")
                        .and("lastMessage").as("lastMessage")
        );

        return mongoTemplate.aggregate(aggregation, COLLECTION_NAME, SessionMessageStatsDto.class)
                .getMappedResults();
    }

    @Override
    public long countByStatus(ChatMessage.MessageStatus status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.count(query, ChatMessage.class);
    }
}
