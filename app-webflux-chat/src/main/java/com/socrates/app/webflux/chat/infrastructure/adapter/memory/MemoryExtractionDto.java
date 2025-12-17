package com.socrates.app.webflux.chat.infrastructure.adapter.memory;

import com.socrates.app.webflux.chat.domain.model.memory.ExtractedMemory;
import com.socrates.app.webflux.chat.domain.model.memory.MemoryType;

record MemoryExtractionDto(
	String type,
	String content,
	float importance,
	String reasoning) {
	ExtractedMemory toExtractedMemory() {
		return new ExtractedMemory(MemoryType.valueOf(type.toUpperCase()), content, importance,
			reasoning);
	}
}
