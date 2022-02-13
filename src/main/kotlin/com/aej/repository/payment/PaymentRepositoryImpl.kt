package com.aej.repository.payment

import com.aej.KoinContainer
import com.aej.MainException
import com.aej.repository.transaction.Transaction
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.eq

class PaymentRepositoryImpl : PaymentRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Payment>("payment")

    override suspend fun createPayment(payment: Payment): Boolean {
        val paymentExist = collection.findOne(Payment::transactionId eq payment.transactionId)
        if (paymentExist != null) throw MainException("Payment already exist", HttpStatusCode.Conflict)
        collection.insertOne(payment)
        return true
    }

    override suspend fun getPayment(paymentId: String): Payment {
        return collection.findOne(Payment::id eq paymentId).orThrow()
    }

    override suspend fun getPaymentByExternalId(externalId: String): Payment {
        return collection.findOne(Payment::referenceId eq externalId).orThrow()
    }

    override suspend fun updatePayment(payment: Payment): Payment {
        collection.updateOne(Payment::id eq payment.id, payment)
        return getPayment(payment.id)
    }

    override suspend fun getAllPayment(ownerId: String): List<Payment> {
        return collection.find(Payment::ownerId eq ownerId).toList()
    }

    override suspend fun confirmedCreatedPayment(externalId: String) {
        val payment = collection.findOne(Payment::referenceId eq externalId).orThrow()
        payment.status = Transaction.StatusPayment.PENDING
        updatePayment(payment)
    }

    override suspend fun confirmedPaidPayment(externalId: String) {
        val payment = collection.findOne(Payment::referenceId eq externalId).orThrow()
        payment.status = Transaction.StatusPayment.SUCCESS
        updatePayment(payment)
    }
}