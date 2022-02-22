package com.aej.repository.transaction

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.repository.product.Product
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.*

class TransactionRepositoryImpl : TransactionRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<Transaction>("transaction")

    override suspend fun createTransaction(transaction: Transaction): Boolean {
        val transactionExist = collection.findOne(Transaction::id eq transaction.id)
        if (transactionExist != null) throw MainException("Transaction already exist", HttpStatusCode.Conflict)
        collection.insertOne(transaction)
        return true
    }

    override suspend fun createTransactions(transactions: List<Transaction>): Boolean {
        transactions.forEach { transaction ->
            val transactionExist = collection.findOne(Transaction::id eq transaction.id)
            if (transactionExist != null) throw MainException("Transaction already exist", HttpStatusCode.Conflict)
        }
        collection.insertMany(transactions)
        return true
    }

    override suspend fun getTransaction(transactionId: String): Transaction {
        return collection.findOne(Transaction::id eq transactionId).orThrow()
    }

    override suspend fun getAllTransaction(page: Int, limit: Int, userId: String): List<Transaction> {
        val offset = (page - 1) * limit

        return collection.find(Transaction::customerId eq userId)
            .sort(descending(Transaction::updatedAt))
            .skip(offset)
            .limit(limit)
            .toList()
    }

    override suspend fun getAllTransactionByStatus(
        page: Int,
        limit: Int,
        userId: String,
        status: String
    ): List<Transaction> {
        val enumStatus = Transaction.StatusTransaction.valueOf(status.uppercase())
        val offset = (page - 1) * limit

        return collection.find(Transaction::customerId eq userId)
            .filter(Transaction::statusTransaction eq enumStatus)
            .sort(descending(Transaction::updatedAt))
            .skip(offset)
            .limit(limit)
            .toList()
    }

    override suspend fun getSizeCount(status: String): Long {
        val count = if (status.isNotEmpty()) {
            val enumStatus = Transaction.StatusTransaction.valueOf(status.uppercase())
            collection.countDocuments(Transaction::statusTransaction eq enumStatus)
        } else {
            collection.countDocuments()
        }
        return count
    }

    override suspend fun getTransactionGroup(groupId: String): List<Transaction> {
        return collection.find(Transaction::groupId eq groupId).toList()
    }

    override suspend fun getCartTransaction(cartId: String): List<Transaction> {
        return collection.find(Transaction::cartId eq cartId).toList()
    }

    override suspend fun updateTransaction(transactionId: String, transaction: Transaction): Transaction {
        println("transaction update --> ${transaction.json}")
        collection.updateOne(Transaction::id eq transactionId, transaction)
        return getTransaction(transactionId)
    }

    override suspend fun updatePaymentTransaction(
        transactionId: String,
        paymentTransaction: Transaction.PaymentTransaction
    ): Transaction {
        collection.updateOne(Transaction::id eq transactionId, setValue(Transaction::paymentTransaction, paymentTransaction))
        return getTransaction(transactionId)
    }
}