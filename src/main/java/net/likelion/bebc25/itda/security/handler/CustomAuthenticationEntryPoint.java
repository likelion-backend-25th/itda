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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Spring Security의 필터 계층에서 인증 예외가 발생하면 호출됨
    // 인증되지 않은 사용자의 보호된 리소스 접근 시 401 응답을 생성

    // Jackson의 ObjectMapper를 사용하여 Java 객체를 JSON 문자열로 변환(직렬화)
    // Java 8 날짜/시간 타입(LocalDateTime 등)을 JSON으로 처리하기 위해 JavaTimeModule 등록
    // 날짜/시간을 Timestamp 형태가 아닌 ISO-8601 문자열 형태로 직렬화
    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.warn("인증 실패 예외 발생: URI={}, 사유={}", request.getRequestURI(), authException.getMessage());

        // errorResponse : 객체임 - 객체는 못보내서 직렬화해야한다. - Jackson이 해준다.(jdk7까지만 해줌)
        ApiErrorResponse errorResponse = ApiErrorResponse.of(ErrorCode.UNAUTHORIZED_ACCESS);

        response.setStatus(errorResponse.status());
        response.setContentType("application/json;charset=UTF-8");

        // ApiErrorResponse 객체를 JSON 문자열로 직렬화하여 응답 버퍼에 작성
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        // 버퍼에 남아 있는 내용을 실제 응답으로 전송
        response.getWriter().flush();
    }
}