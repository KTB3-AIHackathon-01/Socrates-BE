package com.socrates.app.webflux.chat.infrastructure.adapter.memory;

public record MemoryExtractionConfig(
	String model,
	int conversationThreshold,
	float importanceBoost,
	float importanceThreshold
) {
}
