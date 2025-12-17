package com.socrates.app.webflux.chat.application.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.socrates.app.webflux.chat.application.monitoring.DialoguePipelineMonitor;
import com.socrates.app.webflux.chat.domain.model.llm.CompletionRequest;
import com.socrates.app.webflux.chat.domain.port.out.ConversationCounterPort;
import com.socrates.app.webflux.chat.domain.port.out.ConversationRepository;
import com.socrates.app.webflux.chat.domain.port.out.LlmPort;
import com.socrates.app.webflux.chat.domain.port.out.RetrievalPort;
import com.socrates.app.webflux.chat.domain.port.out.PromptTemplatePort;
import com.socrates.app.webflux.chat.domain.port.out.TtsPort;
import com.socrates.app.webflux.chat.domain.service.SentenceAssembler;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class DialoguePipelineServiceTest {

	@Mock
	private LlmPort llmPort;

	@Mock
	private TtsPort ttsPort;

	@Mock
	private RetrievalPort retrievalPort;

	@Mock
	private ConversationRepository conversationRepository;

	@Mock
	private ConversationCounterPort conversationCounterPort;

	@Mock
	private MemoryExtractionService memoryExtractionService;

	@Mock
	private PromptTemplatePort promptTemplatePort;

	private SentenceAssembler sentenceAssembler;

	private DialoguePipelineService service;
	private DialoguePipelineMonitor pipelineMonitor;

	@BeforeEach
	void setUp() {
		sentenceAssembler = new SentenceAssembler();
		pipelineMonitor = new DialoguePipelineMonitor(summary -> {
		});

		when(ttsPort.prepare()).thenReturn(Mono.empty());
		when(promptTemplatePort.buildDefaultPrompt()).thenReturn("기본 프롬프트");

		service = new DialoguePipelineService(llmPort, ttsPort, retrievalPort,
			conversationRepository, sentenceAssembler, pipelineMonitor, conversationCounterPort,
			memoryExtractionService, promptTemplatePort, 5);
	}

	@Test
	void executeStreaming_shouldReturnBase64EncodedAudio() {
		String testText = "Hello";
		byte[] audioBytes = "audio-data".getBytes();
		String expectedBase64 = Base64.getEncoder().encodeToString(audioBytes);

		when(llmPort.complete(any(CompletionRequest.class)))
			.thenReturn(Mono.just("Hello world."));
		when(ttsPort.streamSynthesize(anyString())).thenReturn(Flux.just(audioBytes));

		StepVerifier.create(service.executeStreaming(testText)).expectNext(expectedBase64)
			.verifyComplete();

		verify(llmPort).complete(any(CompletionRequest.class));
		verify(ttsPort).streamSynthesize("Hello world.");
	}

	@Test
	void executeAudioStreaming_shouldReturnRawAudioBytes() {
		String testText = "Test query";
		byte[] audioBytes = "audio".getBytes();

		when(llmPort.complete(any(CompletionRequest.class)))
			.thenReturn(Mono.just("TTS response"));
		when(ttsPort.streamSynthesize("TTS response")).thenReturn(Flux.just(audioBytes));

		StepVerifier.create(service.executeAudioStreaming(testText)).expectNext(audioBytes)
			.verifyComplete();

		verify(llmPort).complete(any(CompletionRequest.class));
		verify(ttsPort).streamSynthesize("TTS response");
	}
}
