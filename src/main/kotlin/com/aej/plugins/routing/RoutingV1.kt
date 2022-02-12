package com.aej.plugins.routing

import com.aej.repository.user.User
import com.aej.screen.routing.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutingV1() {
    routing {

        route("/v1") {
            route("/user") {
                post("/customer/register") { UserRouteScreen.register(call, User.Role.CUSTOMER) }
                post("/seller/register") { UserRouteScreen.register(call, User.Role.SELLER) }
                post("/login") { UserRouteScreen.login(call) }

                basicAuth {
                    get { UserRouteScreen.getUser(call) }
                }
            }

            /* CUSTOMER */
            route("/customer") {
                route("/product") {
                    get { ProductRouteScreen.getProductWithParameter(call) }
                    get("/category") { ProductRouteScreen.getAllCategory(call) }
                }

                basicAuth {
                    route("/cart") {
                        get { CartRouteScreen.getCart(call) }
                        post { CartRouteScreen.modifiedProductToCartMultipart(call, CartRouteScreen.Method.ADD) }
                        delete { CartRouteScreen.modifiedProductToCartMultipart(call, CartRouteScreen.Method.REMOVE) }
                    }

                    route("/transaction") {
                        get { TransactionRouteScreen.getCurrentTransaction(call) }
                        post { TransactionRouteScreen.createTransaction(call) }
                    }

                    route("/payment") {
                        basicAuth {
                            get("/method") { PaymentRouteScreen.getAllAvailableMethod(call) }
                            get("/all") { PaymentRouteScreen.getAllPayment(call) }
                            get { PaymentRouteScreen.getCurrentPayment(call) }
                            post { PaymentRouteScreen.createPayment(call) }
                        }
                    }
                }
            }

            /* SELLER */
            route("/seller") {
                basicAuth {
                    route("/product") {
                        get { ProductRouteScreen.getProductWithParameter(call) }
                        post { ProductRouteScreen.createProductFormData(call) }
                        get("/category") { ProductRouteScreen.getAllCategory(call) }
                    }
                }
            }
        }
    }
}