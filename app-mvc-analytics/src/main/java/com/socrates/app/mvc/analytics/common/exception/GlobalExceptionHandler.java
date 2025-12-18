package com.socrates.app.mvc.analytics.common.exception;

import com.socrates.app.mvc.analytics.common.response.ApiResponse;
import com.socrates.app.mvc.analytics.common.response.ValidationError;
import com.socrates.app.mvc.analytics.common.response.ValidationErrorData;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException exception) {
		ErrorCode errorCode = exception.errorCode();
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), exception.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<ValidationErrorData>> handleMethodArgumentNotValid(
			MethodArgumentNotValidException exception
	) {
		List<ValidationError> errors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toValidationError)
				.toList();

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), errorCode.message(), new ValidationErrorData(errors)));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<ValidationErrorData>> handleBindException(BindException exception) {
		List<ValidationError> errors = exception.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(this::toValidationError)
				.toList();

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), errorCode.message(), new ValidationErrorData(errors)));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<ApiResponse<ValidationErrorData>> handleHandlerMethodValidation(
			HandlerMethodValidationException exception
	) {
		List<ValidationError> errors = new ArrayList<>();

		exception.getParameterValidationResults().forEach(result -> {
			String field = result.getMethodParameter().getParameterName();
			result.getResolvableErrors()
					.forEach(resolvableError -> errors.add(new ValidationError(field, resolvableError.getDefaultMessage())));
		});

		exception.getCrossParameterValidationResults()
				.forEach(resolvableError -> errors.add(new ValidationError(null, resolvableError.getDefaultMessage())));

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), errorCode.message(), new ValidationErrorData(errors)));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<ValidationErrorData>> handleConstraintViolation(
			ConstraintViolationException exception
	) {
		List<ValidationError> errors = exception.getConstraintViolations()
				.stream()
				.map(this::toValidationError)
				.toList();

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), errorCode.message(), new ValidationErrorData(errors)));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<ValidationErrorData>> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
		String field = exception.getName();
		String message = buildTypeMismatchMessage(field, exception.getRequiredType());

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(
						errorCode.code(),
						errorCode.message(),
						new ValidationErrorData(List.of(new ValidationError(field, message)))
				));
	}

	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<ApiResponse<ValidationErrorData>> handleMissingParameter(
			MissingServletRequestParameterException exception
	) {
		String field = exception.getParameterName();
		String message = field + "는 필수입니다.";

		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(
						errorCode.code(),
						errorCode.message(),
						new ValidationErrorData(List.of(new ValidationError(field, message)))
				));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
		ErrorCode errorCode = ErrorCode.INVALID_REQUEST;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), errorCode.message()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception exception) {
		ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
		return ResponseEntity
				.status(errorCode.httpStatus())
				.body(ApiResponse.failure(errorCode.code(), errorCode.message()));
	}

	private ValidationError toValidationError(FieldError fieldError) {
		return new ValidationError(fieldError.getField(), fieldError.getDefaultMessage());
	}

	private ValidationError toValidationError(ConstraintViolation<?> violation) {
		String field = violation.getPropertyPath() == null ? null : violation.getPropertyPath().toString();
		return new ValidationError(field, violation.getMessage());
	}

	private String buildTypeMismatchMessage(String field, Class<?> requiredType) {
		if (requiredType == null) {
			return field + " 값이 올바르지 않습니다.";
		}
		if (requiredType.getName().equals("java.util.UUID")) {
			return field + "는 UUID 형식이어야 합니다.";
		}
		return field + " 값이 올바르지 않습니다.";
	}
}
