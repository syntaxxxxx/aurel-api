package com.aej.repository.payment

interface PaymentRepository {
    suspend fun createPayment(payment: Payment): Boolean
    suspend fun getPayment(paymentId: String): Payment
    suspend fun getPaymentByExternalId(externalId: String): Payment
    suspend fun updatePayment(payment: Payment): Payment
    suspend fun getAllPayment(ownerId: String): List<Payment>

    suspend fun confirmedCreatedPayment(externalId: String)
    suspend fun confirmedPaidPayment(externalId: String)
}