package com.aej.repository.payment

import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.utils.toPaymentType
import java.util.*

data class Payment(
    var id: String = "",
    var ownerId: String = "",
    var transactionId: String = "",
    var amount: Long = 0,
    var status: Transaction.StatusPayment = Transaction.StatusPayment.WAITING,
    var type: PaymentType = PaymentType.VA,
    var method: String = "",
    var referenceId: String = "",
    var externalData: ExternalData? = null,
    var expirationDate: String = ""
) {

    object Suffix {
        const val VIRTUAL_ACCOUNT = "-VA"
        const val INVOICE = "-INV"
        const val MERCHANT = "-MRC"
    }

    companion object {
        fun of(user: User, transaction: Transaction): Payment {
            val paymentId = UUID.randomUUID().toString()
            val paymentType = transaction.paymentTransaction.toPaymentType()
            val referenceId = "payment-${user.username}-${transaction.id}"

            return Payment(
                id = paymentId,
                ownerId = user.id,
                transactionId = transaction.id,
                amount = transaction.amount,
                status = Transaction.StatusPayment.WAITING,
                type = paymentType,
                method = transaction.paymentTransaction.method,
                referenceId = referenceId
            )
        }

        fun codeVaOf(code: String): String {
            return "$code${Suffix.VIRTUAL_ACCOUNT}".uppercase()
        }

        fun codeInvoiceOf(code: String): String {
            return "$code${Suffix.INVOICE}".uppercase()
        }

        fun codeMerchantOf(code: String): String {
            return "$code${Suffix.MERCHANT}".uppercase()
        }
    }

    data class ExternalData(
        var data: String = "",
        var flag: String = ""
    )
}
