package com.socrates.app.mvc.analytics.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "STUDENT_NOT_FOUND", "해당 학생을 찾을 수 없습니다."),
	INSTRUCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "INSTRUCTOR_NOT_FOUND", "해당 강사를 찾을 수 없습니다."),
	CHAT_SESSION_NOT_FOUND(HttpStatus.NOT_FOUND, "CHAT_SESSION_NOT_FOUND", "해당 채팅 세션을 찾을 수 없습니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "해당 요청에 대한 권한이 없습니다."),
	INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "요청 값이 올바르지 않습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	ErrorCode(HttpStatus httpStatus, String code, String message) {
		this.httpStatus = httpStatus;
		this.code = code;
		this.message = message;
	}

	public HttpStatus httpStatus() {
		return httpStatus;
	}

	public String code() {
		return code;
	}

	public String message() {
		return message;
	}
}
