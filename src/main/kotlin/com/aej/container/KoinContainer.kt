package com.aej.container

import com.aej.repository.cart.CartRepository
import com.aej.repository.payment.PaymentRepository
import com.aej.repository.product.ProductRepository
import com.aej.repository.transaction.TransactionRepository
import com.aej.repository.user.UserRepository
import com.aej.services.authentication.JwtConfig
import io.ktor.client.*
import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypes
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.coroutine.CoroutineClient

object KoinContainer : KoinComponent {

    val valueContainer: ValueContainer by inject()
    val jwtConfig: JwtConfig by inject()

    val userRepository: UserRepository by inject()
    val productRepository: ProductRepository by inject()
    val cartRepository: CartRepository by inject()
    val transactionRepository: TransactionRepository by inject()
    val paymentRepository: PaymentRepository by inject()

    val tika: Tika by inject()
    val mimeTypes: MimeTypes by inject()

    val mongoCoroutineClient: CoroutineClient by inject()

    val httpClient: HttpClient by inject()
}