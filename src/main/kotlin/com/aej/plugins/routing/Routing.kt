package com.aej.plugins.routing

import com.aej.KoinContainer
import com.aej.repository.transaction.Transaction
import com.aej.screen.response.MainResponse
import com.aej.services.fcm.request.FcmData
import com.aej.services.fcm.FcmServices
import com.aej.services.image.ImageStorageServices
import com.aej.services.payment.merchant.callback.MerchantPaidData
import com.aej.services.payment.va.callback.VaCreatedData
import com.aej.services.payment.va.callback.VaPaidData
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val paymentRepository = KoinContainer.paymentRepository
    val transactionRepository = KoinContainer.transactionRepository
    val userRepository = KoinContainer.userRepository

    val paidConfirmed: suspend (externalId: String) -> Unit = {
        paymentRepository.confirmedPaidPayment(it)
        val payment = paymentRepository.getPaymentByExternalId(it)

        val transaction = transactionRepository.getTransaction(payment.transactionId).apply {
            statusTransaction = Transaction.StatusTransaction.PROCESS
            paymentTransaction.statusPayment = payment.statusPayment
        }
        val user = userRepository.getUser(transaction.customerId)

        val fcmData = FcmData(
            type = "transaction",
            externalId = transaction.id
        )
        FcmServices.createNotification(user, fcmData)
        transactionRepository.updateTransaction(transaction.id, transaction)
    }

    routing {
        get("/ping") { call.respond("Hai") }

        get("/image/{image_name}") {
            val imageName = call.parameters["image_name"].orEmpty()
            val data = ImageStorageServices.getFile(imageName)
            call.respondBytes(
                contentType = ContentType.parse(data.contentType),
                bytes = data.byteArray
            )
        }

        route("/payment") {
            route("/callback") {
                route("/va") {
                    post("/created") {
                        if (isXenditCallback(call)) {
                            val responseBody = call.receive<VaCreatedData>()
                            paymentRepository.confirmedCreatedPayment(responseBody.externalId)
                            val payment = paymentRepository.getPaymentByExternalId(responseBody.externalId)

                            val transaction = transactionRepository.getTransaction(payment.transactionId).apply {
                                statusTransaction = Transaction.StatusTransaction.WAITING
                                paymentTransaction.statusPayment = payment.statusPayment
                            }
                            transactionRepository.updateTransaction(transaction.id, transaction)

                            call.respond(MainResponse.bindToResponse(responseBody, "Va created"))
                        }
                    }

                    post("/paid") {
                        if (isXenditCallback(call)) {
                            val responseBody = call.receive<VaPaidData>()
                            println("body -> $responseBody")
                            println("fcm notification incoming body -> $responseBody")
                            paidConfirmed.invoke(responseBody.externalId)
                            call.respond(MainResponse.bindToResponse(responseBody, "Va paid"))
                        }
                    }
                }

                route("/merchant") {
                    post("/paid") {
                        if (isXenditCallback(call)) {
                            val responseBody = call.receive<MerchantPaidData>()
                            paidConfirmed.invoke(responseBody.externalId)
                            call.respond(MainResponse.bindToResponse(responseBody, "Merchant paid"))
                        }
                    }
                }
            }
        }
    }
}

private fun isXenditCallback(applicationCall: ApplicationCall): Boolean {
    val xenditVaCallbackToken = System.getenv("XENDIT_CALLBACK_TOKEN")
    val header = applicationCall.request.header("x-callback-token")
    return header == xenditVaCallbackToken
}

fun Route.basicAuth(route: Route.() -> Unit) = authenticate("auth-basic") { route.invoke(this) }
