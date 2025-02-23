package com.myme.mywarehome.infrastructure.aspect.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;

@Slf4j
@Aspect
@Component
public class RequestLoggingAspect {
    private final ObjectMapper objectMapper;

    public RequestLoggingAspect() {
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())  // Java 8 date/time 지원
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);  // ISO-8601 형식으로 직렬화
    }

    private static final int MAX_REQUEST_CONTENT_LENGTH = 1000;
    private static final int MAX_RESPONSE_CONTENT_LENGTH = 1000;
    private static final int LONG_EXECUTION_TIME = 1000;

    @Around("execution(* com.myme.mywarehome.domains.*.adapter.in.web.*.*(..))")
    public Object loggingApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // MDC에 요청 ID 추가
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String requestContent = getRequestContent(request);

            // 구조화된 로그 형식으로 변경
            log.info("[API-START] method={}, uri={}, class={}, function={}, requestId={}",
                    request.getMethod(), request.getRequestURI(), className, methodName, requestId);

            if (requestContent != null && !requestContent.isBlank()) {
                log.info("[API-REQUEST] requestId={}, content={}",
                        requestId, truncateContent(requestContent, MAX_REQUEST_CONTENT_LENGTH));
            }

            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (result != null) {
                try {
                    String responseContent = objectMapper.writeValueAsString(result);
                    log.info("[API-RESPONSE] requestId={}, content={}, executionTime={}ms",
                            requestId, truncateContent(responseContent, MAX_RESPONSE_CONTENT_LENGTH), executionTime);
                } catch (Exception e) {
                    log.warn("[API-RESPONSE] requestId={}, Failed to serialize response: {}", requestId, e.getMessage());
                }
            }

            // 실행 시간이 긴 경우 경고
            if (executionTime > LONG_EXECUTION_TIME) {
                log.warn("[API-SLOW] requestId={}, class={}, method={}, executionTime={}ms",
                        requestId, className, methodName, executionTime);
            }

            return result;

        } catch (Exception e) {
            log.error("[API-ERROR] requestId={}, class={}, method={}, error={}, message={}",
                    requestId, className, methodName, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        } finally {
            MDC.remove("requestId");
        }
    }

    private String getRequestContent(HttpServletRequest request) {
        try {
            if ("GET".equals(request.getMethod())) {
                return request.getQueryString();
            }

            String contentType = request.getContentType();
            if (contentType != null && contentType.contains("application/json")) {
                return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            log.warn("[API-WARN] Failed to read request content", e);
        }
        return null;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null) return null;
        if (content.length() <= maxLength) return content;
        return content.substring(0, maxLength) + "...";
    }
}

