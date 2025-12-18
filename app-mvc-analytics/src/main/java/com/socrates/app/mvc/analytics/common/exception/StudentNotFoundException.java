package com.socrates.app.mvc.analytics.common.exception;

public class StudentNotFoundException extends ApiException {
	public StudentNotFoundException() {
		super(ErrorCode.STUDENT_NOT_FOUND);
	}
}

