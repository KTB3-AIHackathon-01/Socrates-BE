package com.socrates.app.webflux.chat.domain.model.llm;

public record CompletionResponse(
	String content,
	String model,
	int tokensUsed
) {
	public static CompletionResponse of(String content) {
		return new CompletionResponse(content, null, 0);
	}
}
