package im.bigs.pg.external.pg.adapter

import com.fasterxml.jackson.databind.ObjectMapper
import im.bigs.pg.application.pg.port.out.PgApproveRequest
import im.bigs.pg.application.pg.port.out.PgApproveResult
import im.bigs.pg.application.pg.port.out.PgClientOutPort
import im.bigs.pg.domain.payment.PaymentStatus
import im.bigs.pg.external.pg.config.PgProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.String

@Component
class TestPgAdapter (
    private val restTemplate: RestTemplate,
    private val pgProperties: PgProperties,
    private val objectMapper: ObjectMapper
) : PgClientOutPort{
    override fun supports(partnerId: Long): Boolean = true

    override fun approve(request: PgApproveRequest): PgApproveResult {
        // 1. 요청 평문 JSON 생성
        val externalRequst = TestPgRequest(
            cardNumber = request.cardNumber,
            birthDate = request.birthDate,
            expiry = request.expiry,
            password = request.password,
            amount = request.amount
        )
        val jsonPayload = objectMapper.writeValueAsString(externalRequst)

        // 2. 암호화 수행
        val encryptedData = encrypt(jsonPayload)

        // 3. Http 요청
        val headers = HttpHeaders().apply {
            set("API-KEY", pgProperties.apiKey)
            contentType = MediaType.APPLICATION_JSON
        }
        val body = mapOf("enc" to encryptedData)

        // 4. 외부 API 호출
        return try {
            val response = restTemplate.postForEntity(
                "https://api-test-pg.bigs.im/api/v1/pay/credit-card",
                HttpEntity(body, headers),
                Map::class.java
            )

            if (response.statusCode == HttpStatus.OK) {
                val resBody = response.body as Map<*,*>
                PgApproveResult(
                    approvalCode = resBody["approvalCode"]?.toString() ?: "",
                    approvedAt = LocalDateTime.now(ZoneOffset.UTC),
                    status = PaymentStatus.APPROVED,
                )
            } else {
                throw RuntimeException("결제 승인 실패 : ${response.statusCode}")
            }
        } catch (e : Exception) {
            // 실패카드
            throw RuntimeException("PG 연동 오류: ${e.message}")
        }
    }

    private fun encrypt(plainText: String) : String {
        // 1. 알고리즘
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // 2. Key
        val md = MessageDigest.getInstance("SHA-256")
        val hashedKeyBytes  = md.digest(pgProperties.apiKey.toByteArray(Charsets.UTF_8))

        val keySpec = SecretKeySpec(hashedKeyBytes , "AES")

        // 3. IV
        val iv = Base64.getUrlDecoder().decode(pgProperties.ivBase64Url.trim())
        val gcmSpec = GCMParameterSpec(128, iv)

        // 4. Cipher Text
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec)

        val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        // 5. enc
        return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted)
    }

    private data class TestPgRequest(
        val cardNumber: String,
        val birthDate: String, // 생년월일 8자리
        val expiry: String,    // 유효기간 4자리 (YYMM)
        val password: String,  // 비밀번호 앞 2자리
        val amount: BigDecimal,
    )
}