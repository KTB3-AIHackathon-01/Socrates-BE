package com.socrates.app.webflux.chat.domain.port.out;

import com.socrates.app.webflux.chat.domain.model.memory.ExtractedMemory;
import com.socrates.app.webflux.chat.domain.model.memory.MemoryExtractionContext;
import reactor.core.publisher.Flux;

/**
 * MemoryExtractionPort는 메모리 추출 작업을 수행하는 역할을 담당합니다.
 */
public interface MemoryExtractionPort {

	/**
	 * 메모리 추출 작업을 수행합니다.
	 *
	 * @param context
	 *            메모리 추출 컨텍스트
	 * @return 추출된 메모리 항목들의 Flux 스트림
	 */
	Flux<ExtractedMemory> extractMemories(MemoryExtractionContext context);
}
