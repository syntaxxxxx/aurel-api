package com.aej.repository.transaction

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.eq

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

    override suspend fun getTransactionGroup(groupId: String): List<Transaction> {
        return collection.find(Transaction::groupId eq groupId).toList()
    }

    override suspend fun getCartTransaction(cartId: String): List<Transaction> {
        return collection.find(Transaction::cartId eq cartId).toList()
    }

    override suspend fun getCustomerTransaction(userId: String): List<Transaction> {
        return collection.find(Transaction::customerId eq userId)
            .sort(Transaction::statusTransaction eq Transaction.StatusTransaction.WAITING)
            .toList()
    }

    override suspend fun updateTransaction(transactionId: String, transaction: Transaction): Transaction {
        collection.updateOne(Transaction::id eq transactionId, transaction)
        return getTransaction(transactionId)
    }
}