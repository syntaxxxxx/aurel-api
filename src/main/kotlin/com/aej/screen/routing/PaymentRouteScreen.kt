package com.aej.screen.routing

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.repository.payment.Payment
import com.aej.repository.payment.PaymentType
import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.screen.response.MainResponse
import com.aej.services.payment.PaymentServices
import com.aej.services.payment.merchant.request.MerchantRequestBody
import com.aej.services.payment.va.request.VaRequestBody
import com.aej.utils.mapToResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

object PaymentRouteScreen {
    private val userRepository = KoinContainer.userRepository
    private val transactionRepository = KoinContainer.transactionRepository
    private val paymentRepository = KoinContainer.paymentRepository

    suspend fun createPayment(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val transactionId = parameters["transaction_id"].orEmpty()
        if (transactionId.isEmpty()) throw MainException("\'transaction_id\' required!", HttpStatusCode.BadRequest)

        val transaction = transactionRepository.getTransaction(transactionId)
        if (transaction.customerId != user.id) throw MainException("Transaction invalid", HttpStatusCode.BadRequest)

        val payment = Payment.of(user, transaction)
        when (payment.type) {
            PaymentType.VA -> {
                val requestBody = VaRequestBody.of(user, transaction)
                val responsePayment = PaymentServices.VirtualAccount.createVa(requestBody)
                payment.run {
                    expirationDate = responsePayment.expirationDate
                    externalData = Payment.ExternalData(
                        data = responsePayment.accountNumber,
                        flag = "VA Number"
                    )
                }
            }
            PaymentType.MERCHANT -> {
                val requestBody = MerchantRequestBody.of(user, transaction)
                val responsePayment = PaymentServices.Merchant.createMerchant(requestBody)
                payment.run {
                    expirationDate = responsePayment.expirationDate
                    externalData = Payment.ExternalData(
                        data = responsePayment.paymentCode,
                        flag = "Payment code"
                    )
                }
            }
            PaymentType.INVOICE -> {
                // nanti ya
            }
        }

        paymentRepository.createPayment(payment)
        val paymentData = paymentRepository.getPayment(payment.id)
        val newTransaction = transaction.apply {
            paymentTransaction.statusPayment = paymentData.status
        }
        transactionRepository.updateTransaction(newTransaction.id, newTransaction)

        respond(MainResponse.bindToResponse(paymentData.mapToResponse(), "Create payment"))
    }

    suspend fun getPayment(applicationCall: ApplicationCall) = applicationCall.run {
        when (parameters.contains("payment_id")) {
            true -> getUserPayment(this)
            false -> getCurrentPayment(this)
        }
    }

    private suspend fun getCurrentPayment(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val payment = paymentRepository.getAllPayment(user.id)
            .filter {
                it.status == Transaction.StatusPayment.WAITING || it.status == Transaction.StatusPayment.PENDING
            }
            .map { it.mapToResponse() }

        respond(MainResponse.bindToResponse(payment, "Get current payment"))
    }

    private suspend fun getUserPayment(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val paymentId = parameters["payment_id"].orEmpty()
        val payment = paymentRepository.getPayment(paymentId).mapToResponse()
        if (payment.ownerId != user.id) throw MainException("Payment not found!", HttpStatusCode.BadRequest)
        respond(MainResponse.bindToResponse(payment, "Get payment"))
    }

    suspend fun getAllPayment(applicationCall: ApplicationCall) = applicationCall.run {
        val user = User.fromToken(request, userRepository)
        val payment = paymentRepository.getAllPayment(user.id)
            .map { it.mapToResponse() }

        respond(MainResponse.bindToResponse(payment, "Get all payment"))
    }

    suspend fun getAllAvailableMethod(applicationCall: ApplicationCall) = applicationCall.run {
        val list = PaymentServices.getAllPaymentMethod().map { it.mapToResponse() }
        respond(MainResponse.bindToResponse(list, "Get all available payment method"))
    }
}