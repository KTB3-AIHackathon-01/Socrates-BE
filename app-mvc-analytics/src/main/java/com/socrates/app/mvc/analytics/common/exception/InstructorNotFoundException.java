package com.socrates.app.mvc.analytics.common.exception;

public class InstructorNotFoundException extends ApiException {
	public InstructorNotFoundException() {
		super(ErrorCode.INSTRUCTOR_NOT_FOUND);
	}
}

