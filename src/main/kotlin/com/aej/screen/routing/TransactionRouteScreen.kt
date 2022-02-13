package com.aej.screen.routing

import com.aej.KoinContainer
import com.aej.MainException
import com.aej.repository.payment.Payment
import com.aej.repository.payment.PaymentType
import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.screen.response.MainResponse
import com.aej.services.payment.PaymentServices
import com.aej.services.payment.va.PaymentVaApi
import com.aej.utils.mapToResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object TransactionRouteScreen {
    private val userRepository = KoinContainer.userRepository
    private val cartRepository = KoinContainer.cartRepository
    private val transactionRepository = KoinContainer.transactionRepository

    suspend fun createTransaction(applicationCall: ApplicationCall) = applicationCall.run {
        val paymentParameter = parameters["payment"].orEmpty().uppercase()
        val user = User.fromToken(request, userRepository)
        val cart = cartRepository.getCart(user.id)

        if (paymentParameter.isEmpty()) throw MainException("Payment required!", HttpStatusCode.BadRequest)
        if (cart.product.isEmpty()) throw MainException("Product is empty!", HttpStatusCode.BadRequest)

        val availableMethod = PaymentServices.getAllPaymentMethod()

        val method = availableMethod.find {
            it.code == paymentParameter
        } ?: throw MainException("Payment method invalid!")

        if (!method.isActivated) throw MainException("Payment method is inactive!")

        val transactions = Transaction.of(cart, Transaction.PaymentTransaction.init(paymentParameter))

        // validation amount with method
        transactions.forEach { transaction ->
            val isMerchant = method.paymentType == PaymentType.MERCHANT
            val isVa = method.paymentType == PaymentType.VA
            val isVaBCA = method.code.contains("BCA")

            when {
                isMerchant -> {
                    val maxAmount: Long = 5000000
                    if (transaction.amount > maxAmount) {
                        throw MainException(
                            "Expected amount cannot be more than 5000000 with merchant method",
                            HttpStatusCode.Forbidden
                        )
                    }
                }
                isVa -> {
                    if (isVaBCA) {
                        val minAmount = 10000
                        if (transaction.amount < minAmount) {
                            throw MainException(
                                "Minimal amount must be more than 10000 with BCA-VA method",
                                HttpStatusCode.Forbidden
                            )
                        }
                    }
                }
            }
        }

        transactionRepository.createTransactions(transactions)

        val currentTransaction = transactions.firstOrNull()
            ?: throw MainException("Create transaction failed!", HttpStatusCode.BadRequest)
        val groupTransaction = transactionRepository
            .getTransactionGroup(currentTransaction.groupId)
            .map { it.mapToResponse() }

        cartRepository.clearCart(user.id)
        respond(MainResponse.bindToResponse(groupTransaction, "Create transaction"))
    }

    suspend fun getUserTransaction(applicationCall: ApplicationCall) = applicationCall.run {
        when (parameters.contains("transaction_id")) {
            true -> getTransaction(this)
            false -> getCurrentTransaction(this)
        }
    }

    private suspend fun getCurrentTransaction(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val cart = cartRepository.getCart(user.id)
        val transaction = transactionRepository.getCartTransaction(cart.id).map { it.mapToResponse() }
        respond(MainResponse.bindToResponse(transaction, "Get current transaction"))
    }

    private suspend fun getTransaction(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val transactionId = parameters["transaction_id"].orEmpty()
        val transaction = transactionRepository.getTransaction(transactionId).mapToResponse()
        if (transaction.customerId != user.id) throw MainException("Transaction not found!", HttpStatusCode.BadRequest)
        respond(MainResponse.bindToResponse(transaction, "Get transaction"))
    }
}