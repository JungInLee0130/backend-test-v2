package im.bigs.pg.external.pg.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pg.test")
data class PgProperties (
    val apiKey: String,
    val ivBase64Url: String,
)