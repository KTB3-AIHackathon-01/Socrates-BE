package com.socrates.app.mvc.analytics.common.exception;

public class ForbiddenException extends ApiException {
	public ForbiddenException() {
		super(ErrorCode.FORBIDDEN);
	}

	public ForbiddenException(String message) {
		super(ErrorCode.FORBIDDEN, message);
	}
}

