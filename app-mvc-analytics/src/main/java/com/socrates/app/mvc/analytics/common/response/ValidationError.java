package com.socrates.app.mvc.analytics.common.response;

public record ValidationError(
		String field,
		String message
) {
}

