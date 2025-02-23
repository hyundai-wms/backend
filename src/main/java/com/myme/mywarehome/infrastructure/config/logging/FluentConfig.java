package com.myme.mywarehome.infrastructure.config.logging;

import java.io.IOException;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.fluentd.FluencyBuilderForFluentd;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FluentConfig {
    @Bean
    public Fluency fluency() throws IOException {
        // 최신 버전의 Fluency 빌더 사용
        return new FluencyBuilderForFluentd()
                .build("localhost", 24224);
    }
}