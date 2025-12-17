package com.socrates.app.webflux.chat.infrastructure.config;

import com.socrates.app.webflux.chat.infrastructure.adapter.memory.MemoryExtractionConfig;
import com.socrates.app.webflux.chat.infrastructure.config.properties.RagDialogueProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;


@Configuration
public class MemoryConfiguration {

	@Bean
	public MemoryExtractionConfig memoryExtractionConfig(RagDialogueProperties properties) {
		var memory = properties.getMemory();
		return new MemoryExtractionConfig(memory.getExtractionModel(),
			memory.getConversationThreshold(), memory.getImportanceBoost(),
			memory.getImportanceThreshold());
	}

	@Bean
	public int conversationThreshold(RagDialogueProperties properties) {
		return properties.getMemory().getConversationThreshold();
	}

	@Bean
	public ReactiveRedisTemplate<String, Long> reactiveRedisLongTemplate(
		ReactiveRedisConnectionFactory connectionFactory) {
		RedisSerializationContext<String, Long> context = RedisSerializationContext
			.<String, Long>newSerializationContext(new StringRedisSerializer())
			.value(new GenericToStringSerializer<>(Long.class)).build();

		return new ReactiveRedisTemplate<>(connectionFactory, context);
	}
}
