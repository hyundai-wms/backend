package com.myme.mywarehome.infrastructure.aspect.logging;

import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class RepositoryLoggingAspect {

    private static final int SLOW_QUERY_THRESHOLD = 1000;

    @Around("execution(* com.myme.mywarehome.domains.*.adapter.out.persistence.*JpaRepository.*(..))")
    public Object logJpaOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return logRepositoryOperation(joinPoint, "JPA");
    }

    @Around("execution(* com.myme.mywarehome.domains.*.adapter.out.persistence.*MybatisRepository.*(..))")
    public Object logMybatisOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return logRepositoryOperation(joinPoint, "MyBatis");
    }

    private Object logRepositoryOperation(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String domain = extractDomain(joinPoint);
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            // 쿼리 파라미터 로깅
            logQueryParameters(domain, type, methodName, args);

            Object result = joinPoint.proceed();
            stopWatch.stop();

            // 실행 결과 로깅
            logQueryResult(domain, type, methodName, result, stopWatch.getTotalTimeMillis());

            return result;

        } catch (Exception e) {
            stopWatch.stop();
            log.error("[{}-{}] Exception in {}.{} - {} : {} (execution time: {}ms)",
                    domain, type, joinPoint.getTarget().getClass().getSimpleName(), methodName,
                    e.getClass().getSimpleName(), e.getMessage(), stopWatch.getTotalTimeMillis());
            throw e;
        }
    }

    private String extractDomain(ProceedingJoinPoint joinPoint) {
        // MethodSignature를 통해 실제 메서드의 클래스 정보 가져오기
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getName();

        log.debug("Declaring class name: {}", className);

        // 패키지 경로에서 도메인 추출
        if (className.contains("domains.")) {
            String domain = className.split("domains\\.")[1].split("\\.")[0].toUpperCase();
            log.debug("Extracted domain: {}", domain);
            return domain;
        }

        return "UNKNOWN";
    }

    private void logQueryParameters(String domain, String type, String methodName, Object[] args) {
        StringBuilder params = new StringBuilder();
        for (Object arg : args) {
            if (arg instanceof Pageable pageable) {
                params.append(String.format("page=%d,size=%d,sort=%s ",
                        pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()));
            } else if (arg != null) {
                params.append(String.format("%s=%s ",
                        arg.getClass().getSimpleName(), arg.toString()));
            }
        }

        log.info("[{}-{}] Executing {}: params=[{}]",
                domain, type, methodName, params.toString().trim());
    }

    private void logQueryResult(String domain, String type, String methodName, Object result, long executionTime) {
        if (result instanceof Page<?> page) {
            log.info("[{}-{}] {} completed: totalElements={}, totalPages={}, executionTime={}ms",
                    domain, type, methodName, page.getTotalElements(), page.getTotalPages(), executionTime);
        } else if (result instanceof Collection<?> collection) {
            log.info("[{}-{}] {} completed: count={}, executionTime={}ms",
                    domain, type, methodName, collection.size(), executionTime);
        } else if (result instanceof Optional<?> optional) {
            log.info("[{}-{}] {} completed: present={}, executionTime={}ms",
                    domain, type, methodName, optional.isPresent(), executionTime);
        } else {
            log.info("[{}-{}] {} completed: result={}, executionTime={}ms",
                    domain, type, methodName, result != null ? "not null" : "null", executionTime);
        }

        if (executionTime > SLOW_QUERY_THRESHOLD) {
            log.warn("[{}-{}] Slow query detected in {}: executionTime={}ms",
                    domain, type, methodName, executionTime);
        }
    }
}
