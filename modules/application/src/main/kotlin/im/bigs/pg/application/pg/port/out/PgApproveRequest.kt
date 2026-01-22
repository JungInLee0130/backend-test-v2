package im.bigs.pg.application.pg.port.out

import java.math.BigDecimal

/** PG 승인 요청 최소 정보. */
data class PgApproveRequest(
    val partnerId: Long,
    val amount: BigDecimal,
    val cardNumber: String,
    val cardBin: String?,
    val cardLast4: String?,
    val productName: String?,
    val birthDate: String, // 생년월일 8자리
    val expiry: String,    // 유효기간 4자리 (YYMM)
    val password: String,  // 비밀번호 앞 2자리
)
