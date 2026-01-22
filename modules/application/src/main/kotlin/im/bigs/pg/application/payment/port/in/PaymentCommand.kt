package im.bigs.pg.application.payment.port.`in`

import java.math.BigDecimal

/**
 * 결제 생성에 필요한 최소 입력.
 *
 * @property partnerId 제휴사 식별자
 * @property amount 결제 금액(정수 금액 권장)
 * @property cardBin 카드 BIN(없을 수 있음)
 * @property cardLast4 카드 마지막 4자리(없을 수 있음)
 * @property productName 상품명(없을 수 있음)
 */
data class PaymentCommand(
    val partnerId: Long,
    val amount: BigDecimal,
    val cardNumber: String,
    val cardBin: String? = null,
    val cardLast4: String? = null,
    val productName: String? = null,
    val birthDate: String, // 생년월일 8자리
    val expiry: String,    // 유효기간 4자리 (YYMM)
    val password: String,  // 비밀번호 앞 2자리
) {
    init {
        require(partnerId > 0) { "유효하지 않은 파트너 ID 입니다."}
        require(amount > BigDecimal.ZERO) { "결제 금액은 0보다 커야합니다."}
    }
}

