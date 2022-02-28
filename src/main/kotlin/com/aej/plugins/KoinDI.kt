package com.aej.plugins

import com.aej.container.ValueContainer
import com.aej.repository.banner.BannerRepository
import com.aej.repository.banner.BannerRepositoryImpl
import com.aej.repository.cart.CartRepository
import com.aej.repository.cart.CartRepositoryImpl
import com.aej.repository.payment.PaymentRepository
import com.aej.repository.payment.PaymentRepositoryImpl
import com.aej.repository.product.ProductRepository
import com.aej.repository.product.ProductRepositoryImpl
import com.aej.repository.transaction.TransactionRepository
import com.aej.repository.transaction.TransactionRepositoryImpl
import com.aej.repository.user.UserRepository
import com.aej.repository.user.UserRepositoryImpl
import com.aej.services.authentication.JwtConfig
import com.google.gson.FieldNamingPolicy
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypes
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun Application.configureKoin() {
    val dbUrl = "mongodb+srv://utsman:IndonesiaRaya1945@cluster0.p6ysg.mongodb.net"

    val valueContainer = module {
        single { ValueContainer() }
    }

    val jwtModule = module {
        single { JwtConfig() }
    }

    val userRepositoryModule = module {
        single<UserRepository> { UserRepositoryImpl() }
    }

    val productRepositoryModule = module {
        single<ProductRepository> { ProductRepositoryImpl() }
    }

    val cartRepositoryModule = module {
        single<CartRepository> { CartRepositoryImpl() }
    }

    val paymentRepositoryModule = module {
        single<PaymentRepository> { PaymentRepositoryImpl() }
    }

    val transactionModule = module {
        single<TransactionRepository> { TransactionRepositoryImpl() }
    }

    val bannerModule = module {
        single<BannerRepository> { BannerRepositoryImpl() }
    }

    val tikaModule = module {
        single { Tika() }
        single { MimeTypes.getDefaultMimeTypes() }
    }

    val mongoServiceModule = module {
        single { KMongo.createClient(dbUrl).coroutine }
    }

    val httpClientModule = module {
        single {
            HttpClient(CIO) {
                install(ContentNegotiation) {
                    gson {
                        setPrettyPrinting()
                        setLenient()
                        setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    }
                }
            }
        }
    }

    startKoin {
        modules(
            valueContainer,
            jwtModule,
            userRepositoryModule,
            productRepositoryModule,
            cartRepositoryModule,
            paymentRepositoryModule,
            transactionModule,
            bannerModule,
            tikaModule,
            mongoServiceModule,
            httpClientModule
        )
    }
}