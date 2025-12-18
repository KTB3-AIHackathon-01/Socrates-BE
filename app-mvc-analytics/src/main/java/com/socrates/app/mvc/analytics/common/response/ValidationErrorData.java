package com.socrates.app.mvc.analytics.common.response;

import java.util.List;

public record ValidationErrorData(
		List<ValidationError> errors
) {
}

