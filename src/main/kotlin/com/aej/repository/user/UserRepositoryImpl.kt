package com.aej.repository.user

import com.aej.container.KoinContainer
import com.aej.MainException
import com.aej.utils.orThrow
import io.ktor.http.*
import org.litote.kmongo.eq

class UserRepositoryImpl : UserRepository {
    private val client = KoinContainer.mongoCoroutineClient
    private val database = client.getDatabase("aurel-market")
    private val collection = database.getCollection<User>()

    override suspend fun createUser(user: User): Boolean {
        val userExist = collection.findOne(User::id eq user.id)
        if (userExist != null) throw MainException("User already exist", HttpStatusCode.Conflict)
        collection.insertOne(user)
        return true
    }

    override suspend fun getUser(id: String): User {
        return collection.findOne(User::id eq id).orThrow()
    }

    override suspend fun getUserByName(name: String): User {
        return collection.findOne(User::username eq name).orThrow()
    }

    override suspend fun updateUser(user: User): User {
        collection.updateOne(User::id eq user.id, user)
        return getUser(user.id)
    }

    override suspend fun getUserCount(): Long {
        return collection.countDocuments()
    }

    override suspend fun deleteUser(id: String): Boolean {
        collection.deleteOne(User::id eq id)
        return true
    }

    override suspend fun updateFcmTokenAndServerKey(id: String, fcmToken: String, fcmServerKey: String): User {
        val currentUser = getUser(id)
        currentUser.fcmToken = fcmToken
        currentUser.fcmServerKey = fcmServerKey
        return updateUser(currentUser)
    }
}