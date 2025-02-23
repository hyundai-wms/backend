package com.myme.mywarehome.infrastructure.config.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.springframework.beans.factory.annotation.Value;
import net.logstash.logback.encoder.LogstashEncoder;
import org.komamitsu.fluency.Fluency;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(Fluency.class)
public class LoggingConfig {

    @Value("${logging.level.root:INFO}")
    private String rootLogLevel;

    @Value("${logging.level.com.mywarehome:DEBUG}")
    private String applicationLogLevel;

    @Bean
    public LoggingSystem loggingSystem() {
        // 로깅 시스템 설정
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        // Root 로거 설정
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(Level.toLevel(rootLogLevel));

        // 애플리케이션 로거 설정
        Logger appLogger = loggerContext.getLogger("com.mywarehome");
        appLogger.setLevel(Level.toLevel(applicationLogLevel));

        return LoggingSystem.get(getClass().getClassLoader());
    }

    @Bean
    public LogstashEncoder logstashEncoder() {
        LogstashEncoder encoder = new LogstashEncoder();
        encoder.setIncludeContext(true);
        encoder.setIncludeMdc(true);
        return encoder;
    }
}