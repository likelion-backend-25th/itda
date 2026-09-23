package net.likelion.bebc25.itda.exception;

import org.springframework.http.HttpStatus;

// 비즈니스 에러 코드 열거형 Enum
public enum ErrorCode {

    // 생성자
    INVALID_INPUT_VALUE("INVALID_INPUT_VALUE", "입력값 검증에 실패했습니다.", HttpStatus.BAD_REQUEST), // 400
    BUSINESS_RULE_VIOLATION("BUSINESS_RULE_VIOLATION", "비즈니스 업무 규칙을 위반했습니다.", HttpStatus.BAD_REQUEST), // 400
    UNAUTHORIZED_ACCESS("UNAUTHORIZED_ACCESS", "인증이 필요하거나 유효하지 않은 자격 증명입니다.", HttpStatus.UNAUTHORIZED), // 401
    FORBIDDEN_OPERATION("FORBIDDEN_OPERATION", "해당 작업을 수행할 권한이 없습니다.", HttpStatus.FORBIDDEN), // 403
    RESOURCE_NOT_FOUND("RESOURCE_NOT_FOUND", "요청한 자원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND), // 404
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR); // 500

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}