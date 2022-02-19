package com.aej.screen.documentation

import com.aej.repository.cart.Cart
import com.aej.repository.transaction.Transaction
import me.hana.docs.annotation.DocFieldDescription
import java.time.Instant

class Transaction(
    @DocFieldDescription("status response")
    var status: Boolean = true,
    @DocFieldDescription("code response")
    var code: Int = 200,
    @DocFieldDescription("message response")
    var message: String = "Success",
    @DocFieldDescription("data response")
    var data: TransactionData = TransactionData()
)

data class TransactionData(
    @DocFieldDescription("id of, random UUID string")
    var id: String = "",
    @DocFieldDescription("id of user customer")
    var customerId: String = "",
    @DocFieldDescription("id of user customer")
    var sellerId: String = "",
    @DocFieldDescription("id of user cart")
    var cartId: String = "",
    var products: List<Cart.ProductData> = emptyList(),
    var amount: Long = 0,
    var statusTransaction: Transaction.StatusTransaction = Transaction.StatusTransaction.WAITING,
    var paymentTransaction: Transaction.PaymentTransaction = Transaction.PaymentTransaction(),
    var createdAt: String = "${Instant.now()}",
    var updatedAt: String = "${Instant.now()}"
)