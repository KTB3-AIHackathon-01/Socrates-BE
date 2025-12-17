package com.socrates.app.webflux.chat.domain.port.out;

import com.socrates.app.webflux.chat.domain.model.conversation.ConversationContext;
import com.socrates.app.webflux.chat.domain.model.rag.RetrievalContext;

public interface PromptTemplatePort {
	String buildPrompt(RetrievalContext context);

	String buildPromptWithConversation(RetrievalContext context,
		ConversationContext conversationContext);

	String buildDefaultPrompt();
}
