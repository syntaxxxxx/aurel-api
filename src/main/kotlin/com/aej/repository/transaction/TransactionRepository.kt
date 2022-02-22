package com.aej.repository.transaction

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Boolean
    suspend fun createTransactions(transactions: List<Transaction>): Boolean
    suspend fun getTransaction(transactionId: String): Transaction
    suspend fun getAllTransaction(page: Int, limit: Int, userId: String): List<Transaction>
    suspend fun getAllTransactionByStatus(page: Int, limit: Int, userId: String, status: String): List<Transaction>
    suspend fun getSizeCount(status: String = ""): Long
    suspend fun getTransactionGroup(groupId: String): List<Transaction>
    suspend fun getCartTransaction(cartId: String): List<Transaction>
    suspend fun updateTransaction(transactionId: String, transaction: Transaction): Transaction
    suspend fun updatePaymentTransaction(transactionId: String, paymentTransaction: Transaction.PaymentTransaction): Transaction
}