package com.socrates.app.webflux.chat.infrastructure.adapter.retrieval;

import com.socrates.app.webflux.chat.application.service.MemoryRetrievalService;
import com.socrates.app.webflux.chat.domain.model.memory.MemoryRetrievalResult;
import com.socrates.app.webflux.chat.domain.model.rag.RetrievalContext;
import com.socrates.app.webflux.chat.domain.model.rag.RetrievalDocument;
import com.socrates.app.webflux.chat.domain.port.out.ConversationRepository;
import com.socrates.app.webflux.chat.domain.port.out.RetrievalPort;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Primary
public class VectorMemoryRetrievalAdapter implements RetrievalPort {

	private final MemoryRetrievalService memoryRetrievalService;
	private final ConversationRepository conversationRepository;

	@Override
	public Mono<RetrievalContext> retrieve(String query, int topK) {
		Mono<RetrievalContext> conversationContext = conversationRepository.findRecent(topK * 10)
			.collectList().map(turns -> {
				var sorted = turns.stream().map(turn -> {
					int score = calculateSimilarity(query, turn.query());
					return RetrievalDocument.of(turn.query(), score);
				}).filter(doc -> doc.score().isRelevant())
					.sorted((a, b) -> Integer.compare(b.score().value(), a.score().value()))
					.toList();
				return sorted.size() > topK ? sorted.subList(0, topK) : sorted;
			})
			.map(docs -> RetrievalContext.of(query, docs));

		return conversationContext;
	}

	@Override
	public Mono<MemoryRetrievalResult> retrieveMemories(String query, int topK) {
		return memoryRetrievalService.retrieveMemories(query, topK).onErrorResume(error -> {
			return Mono.just(MemoryRetrievalResult.empty());
		});
	}

	private int calculateSimilarity(String query, String candidate) {
		Set<String> queryWords = tokenize(query);
		Set<String> candidateWords = tokenize(candidate);
		Set<String> intersection = new HashSet<>(queryWords);
		intersection.retainAll(candidateWords);
		return intersection.size();
	}

	private Set<String> tokenize(String text) {
		if (text == null || text.isBlank()) {
			return Set.of();
		}
		return Arrays.stream(text.toLowerCase().split("\\s+")).filter(word -> !word.isEmpty())
			.collect(Collectors.toSet());
	}
}
