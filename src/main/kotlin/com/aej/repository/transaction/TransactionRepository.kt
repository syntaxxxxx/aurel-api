package com.aej.repository.transaction

interface TransactionRepository {
    suspend fun createTransaction(transaction: Transaction): Boolean
    suspend fun createTransactions(transactions: List<Transaction>): Boolean
    suspend fun getTransaction(transactionId: String): Transaction
    suspend fun getTransactionGroup(groupId: String): List<Transaction>
    suspend fun getCartTransaction(cartId: String): List<Transaction>
    suspend fun getCustomerTransaction(userId: String): List<Transaction>
    suspend fun updateTransaction(transactionId: String, transaction: Transaction): Transaction
}