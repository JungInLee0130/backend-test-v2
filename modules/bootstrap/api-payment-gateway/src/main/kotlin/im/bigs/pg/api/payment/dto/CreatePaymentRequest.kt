package im.bigs.pg.api.payment.dto

import jakarta.validation.constraints.Min
import java.math.BigDecimal

data class CreatePaymentRequest(
    val partnerId: Long,
    @field:Min(1)
    val amount: BigDecimal,
    val cardNumber: String,
    val cardBin: String? = null,
    val cardLast4: String? = null,
    val productName: String? = null,
    val birthDate: String, // 생년월일 8자리
    val expiry: String,    // 유효기간 4자리 (YYMM)
    val password: String,  // 비밀번호 앞 2자리
)