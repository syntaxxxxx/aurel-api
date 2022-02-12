package com.aej.screen.response

import com.aej.repository.cart.Cart
import com.aej.repository.transaction.Transaction
import java.time.Instant

data class TransactionResponse(
    var id: String = "",
    var customerId: String = "",
    var sellerId: String = "",
    var cartId: String = "",
    var products: List<Cart.ProductData> = emptyList(),
    var amount: Long = 0,
    var statusTransaction: Transaction.StatusTransaction = Transaction.StatusTransaction.WAITING,
    var paymentTransaction: Transaction.PaymentTransaction = Transaction.PaymentTransaction(),
    var createdAt: String = "${Instant.now()}",
    var updatedAt: String = "${Instant.now()}"
)