package com.aej.repository.transaction

import com.aej.repository.cart.Cart
import java.time.Instant
import java.util.*

data class Transaction(
    var id: String = "",
    var groupId: String = "",
    var customerId: String = "",
    var sellerId: String = "",
    var cartId: String = "",
    var productData: List<Cart.ProductData> = emptyList(),
    var amount: Long = 0,
    var statusTransaction: StatusTransaction = StatusTransaction.WAITING,
    var paymentTransaction: PaymentTransaction = PaymentTransaction(),
    var createdAt: String = "${Instant.now()}",
    var updatedAt: String = "${Instant.now()}"
) {

    companion object {
        fun of(cart: Cart, paymentTransaction: PaymentTransaction): List<Transaction> {
            val productBySeller = cart.product.groupBy { it.product.owner }
            val groupId = UUID.randomUUID().toString()
            return productBySeller.map {
                val id = UUID.randomUUID().toString()
                val amount = it.value.sumOf { data ->
                    data.product.price * data.quantity.toLong()
                }
                Transaction(
                    id = id,
                    groupId = groupId,
                    customerId = cart.owner,
                    sellerId = it.key,
                    cartId = cart.id,
                    productData = it.value,
                    amount = amount,
                    paymentTransaction = paymentTransaction
                )
            }
        }
    }

    enum class StatusPayment {
        WAITING, PENDING, SUCCESS, FAILED
    }

    enum class StatusTransaction {
        WAITING, PROCESS, DELIVERY, DONE
    }

    data class PaymentTransaction(
        var method: String = "",
        var statusPayment: StatusPayment = StatusPayment.WAITING
    ) {
        companion object {
            fun init(method: String): PaymentTransaction {
                return PaymentTransaction(method = method)
            }
        }
    }
}