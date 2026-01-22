package im.bigs.pg.api.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Configuration
class RestTemplateConfig {
    // 외부 API 통신
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        return builder
            .connectTimeout(Duration.ofSeconds(5))   // 서버 연결 대기시간
            .readTimeout(Duration.ofSeconds(10))     // 데이터 읽기 대기시간
            .build()
    }

}