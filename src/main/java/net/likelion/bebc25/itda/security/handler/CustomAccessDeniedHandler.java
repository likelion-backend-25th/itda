package net.likelion.bebc25.itda.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.likelion.bebc25.itda.exception.ErrorCode;
import net.likelion.bebc25.itda.security.dto.ApiErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {


    // Jackson의 ObjectMapper를 사용하여 Java 객체를 JSON 문자열로 변환(직렬화)
    // Java 8 날짜/시간 타입(LocalDateTime 등)을 JSON으로 처리하기 위해 JavaTimeModule 등록
    // 날짜/시간을 Timestamp 형태가 아닌 ISO-8601 문자열 형태로 직렬화
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {

        log.warn("권한 부족 예외 발생: URI={}, 사유={}", request.getRequestURI(), accessDeniedException.getMessage());

        ApiErrorResponse errorResponse = ApiErrorResponse.of(ErrorCode.FORBIDDEN_OPERATION);

        response.setStatus(errorResponse.status());
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.getWriter().flush();
    }
}