package com.myme.mywarehome.infrastructure.config.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myme.mywarehome.domains.mrp.application.domain.MrpExceptionReport;
import com.myme.mywarehome.domains.mrp.application.service.dto.MrpCalculateResultDto;
import com.myme.mywarehome.domains.mrp.application.service.dto.UnifiedBomDataDto;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {
    private final Logger logger = LoggerFactory.getLogger(ExecutionTimeAspect.class);

    @Around("@annotation(logExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, LogExecutionTime logExecutionTime) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        // 기존 로그 포맷을 따르되 필요한 메트릭만 추가
        if (result instanceof MrpCalculateResultDto mrpResult) {
            MDC.put("metric_type", "mrp_metrics");
            MDC.put("execution_time_ms", String.valueOf(executionTime));
            MDC.put("purchase_count", String.valueOf(mrpResult.purchaseOrderReports().size()));
            MDC.put("production_count", String.valueOf(mrpResult.productionPlanningReports().size()));
            MDC.put("exception_count", String.valueOf(mrpResult.mrpExceptionReports().size()));

            log.info("MRP metrics collected");

            MDC.remove("metric_type");
            MDC.remove("execution_time_ms");
            MDC.remove("purchase_count");
            MDC.remove("production_count");
            MDC.remove("exception_count");
        }

        return result;
    }

}
