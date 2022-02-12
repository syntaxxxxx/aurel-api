package com.aej.repository.user

interface UserRepository {
    suspend fun createUser(user: User): Boolean
    suspend fun getUser(id: String): User
    suspend fun getUserByName(name: String): User
    suspend fun updateUser(user: User): User
    suspend fun getUserCount(): Long
    suspend fun deleteUser(id: String): Boolean
    suspend fun updateFcmToken(id: String, fcmToken: String): User
}