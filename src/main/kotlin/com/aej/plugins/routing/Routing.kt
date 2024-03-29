package com.aej.plugins.routing

import com.aej.container.KoinContainer
import com.aej.repository.transaction.Transaction
import com.aej.screen.response.MainResponse
import com.aej.services.authentication.JwtConfig
import com.aej.services.fcm.request.FcmData
import com.aej.services.fcm.FcmServices
import com.aej.services.fcm.response.FcmResponse
import com.aej.services.image.ImageStorageServices
import com.aej.services.payment.merchant.callback.MerchantPaidData
import com.aej.services.payment.simulation.PaymentSimulationPaidServices
import com.aej.services.payment.simulation.request.SimulationPaidBody
import com.aej.services.payment.va.callback.VaCreatedData
import com.aej.services.payment.va.callback.VaPaidData
import com.aej.utils.DefaultImageUtils
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val valueContainer = KoinContainer.valueContainer
    val paymentRepository = KoinContainer.paymentRepository
    val transactionRepository = KoinContainer.transactionRepository
    val userRepository = KoinContainer.userRepository

    val paidConfirmed: suspend (externalId: String) -> FcmResponse = {
        paymentRepository.confirmedPaidPayment(it)
        val payment = paymentRepository.getPaymentByExternalId(it)

        val transaction = transactionRepository.getTransaction(payment.transactionId).apply {
            statusTransaction = Transaction.StatusTransaction.PROCESS
            paymentTransaction.statusPayment = payment.status
        }
        val user = userRepository.getUser(transaction.customerId)

        val fcmData = FcmData(
            type = "transaction",
            externalId = transaction.id
        )
        transactionRepository.updateTransaction(transaction.id, transaction)
        FcmServices.createNotification(user, fcmData)
    }

    routing {
        get("/ping") {
            val host = call.request.host()
            val port = call.request.port()
            valueContainer.apply {
                setHost(host)
                setPort(port)
            }
            call.respond("Success")
        }

        get("/docs") {
            val postmanUrl = "https://documenter.getpostman.com/view/3885530/UVsJv6iW"
            call.respondRedirect(postmanUrl, true)
        }

        get("/default/{username}") {
            val text = call.parameters["username"] ?: "unknown"
            val bytes = DefaultImageUtils.createImage(text.first().toString())
            call.respondBytes(
                contentType = ContentType.Image.PNG,
                bytes = bytes
            )
        }

        get("/image/{image_name}") {
            val imageName = call.parameters["image_name"].orEmpty()
            val data = ImageStorageServices.getFile(imageName)
            call.respondBytes(
                contentType = ContentType.parse(data.contentType),
                bytes = data.byteArray
            )
        }

        post("/update") {
            KoinContainer.productRepository.fixProduct()
        }

        route("/payment") {
            route("/callback") {
                route("/va") {
                    post("/created") {
                        if (isXenditCallback(call)) {
                            val responseBody = call.receive<VaCreatedData>()
                            paymentRepository.confirmedCreatedPayment(responseBody.externalId)
                            val payment = paymentRepository.getPaymentByExternalId(responseBody.externalId)

                            val transaction = transactionRepository.getTransaction(payment.transactionId)
                            val newPayment = transaction.paymentTransaction.apply {
                                statusPayment = payment.status
                            }
                            transactionRepository.updatePaymentTransaction(transaction.id, newPayment)
                            call.respond(MainResponse.bindToResponse(responseBody, "Va created"))
                        }
                    }

                    post("/paid") {
                        if (isXenditCallback(call)) {
                            val responseBody = call.receive<VaPaidData>()
                            println("body -> $responseBody")
                            println("fcm notification incoming body -> $responseBody")
                            val fcmResult = paidConfirmed.invoke(responseBody.externalId)
                            val data = mapOf(
                                "fcm_result" to fcmResult,
                                "payload" to responseBody
                            )
                            call.respond(MainResponse.bindToResponse(data, "Va paid"))
                        }
                    }
                }

                route("/merchant") {
                    post("/paid") {
                        if (isXenditCallback(call)) {
                            val responseBody = call.receive<MerchantPaidData>()
                            val fcmResult = paidConfirmed.invoke(responseBody.externalId)
                            val data = mapOf(
                                "fcm_result" to fcmResult,
                                "payload" to responseBody
                            )
                            call.respond(MainResponse.bindToResponse(data, "Merchant paid"))
                        }
                    }
                }
            }

            route("/simulation") {
                post("/va") {
                    val body = call.receive<SimulationPaidBody>()
                    val referenceId = body.referenceId
                    val amount = body.amount
                    val requestBody = SimulationPaidBody(referenceId, amount)
                    val payment = paymentRepository.getPaymentByExternalId(referenceId)

                    println("request body ---> $requestBody")
                    val data = PaymentSimulationPaidServices.createPaidSimulation(requestBody, payment.method)
                    call.respond(MainResponse.bindToResponse(data, "Create virtual account paid simulation"))
                }

                post("/merchant") {
                    val body = call.receive<SimulationPaidBody>()
                    val referenceId = body.referenceId
                    val amount = body.amount
                    val payment = paymentRepository.getPaymentByExternalId(referenceId)
                    val codePayment = payment.externalData?.data.orEmpty()
                    val requestBody = SimulationPaidBody(codePayment, amount)

                    println("request body ---> $requestBody")
                    val data = PaymentSimulationPaidServices.createPaidSimulation(requestBody, payment.method)
                    call.respond(MainResponse.bindToResponse(data, "Create merchant paid simulation"))
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

fun Route.basicAuth(route: Route.() -> Unit) = authenticate(JwtConfig.NAME) { route.invoke(this) }
