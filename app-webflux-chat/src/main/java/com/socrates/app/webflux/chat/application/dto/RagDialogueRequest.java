package com.socrates.app.webflux.chat.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public record RagDialogueRequest(
	@NotBlank String text,
	@NotNull Instant requestedAt
) {
}
