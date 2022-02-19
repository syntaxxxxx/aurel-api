package com.aej.plugins.routing

import com.aej.repository.user.User
import com.aej.screen.documentation.*
import com.aej.screen.routing.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import me.hana.docs.hanaDocs
import me.hana.docs.hanaDocsParent

fun Application.configureRoutingV1() {
    routing {

        route("/v1") {
            route("/user") {
                hanaDocsParent("User", 1) { userParent() }

                post("/customer/register") { UserRouteScreen.register(call, User.Role.CUSTOMER) }
                    .hanaDocs("Register (customer)", 1) { userRegister(User.Role.CUSTOMER) }

                post("/seller/register") { UserRouteScreen.register(call, User.Role.SELLER) }
                    .hanaDocs("Register (seller)", 1) { userRegister(User.Role.SELLER) }

                post("/login") { UserRouteScreen.login(call) }
                    .hanaDocs("Login", 1) { userLogin() }

                put { UserRouteScreen.updateProfileUser(call) }
                put("/image") { UserRouteScreen.updateImageProfile(call) }

                basicAuth {
                    get { UserRouteScreen.getUser(call) }
                    post("/fcm") { UserRouteScreen.updateFcmToken(call) }
                }
            }

            /* CUSTOMER */
            route("/customer") {
                route("/product") {
                    hanaDocsParent("Product", 2) { productParent() }
                    get { ProductRouteScreen.getProductWithParameter(call) }
                        .hanaDocs("Get Product", 2) { getProduct() }

                    get("/category") { ProductRouteScreen.getAllCategory(call) }
                }

                basicAuth {
                    route("/cart") {
                        get { CartRouteScreen.getCart(call) }
                        post { CartRouteScreen.modifiedProductToCartMultipart(call, CartRouteScreen.Method.ADD) }
                        delete { CartRouteScreen.modifiedProductToCartMultipart(call, CartRouteScreen.Method.REMOVE) }
                    }

                    route("/transaction") {
                        get { TransactionRouteScreen.getUserTransaction(call) }
                        post { TransactionRouteScreen.createTransaction(call) }
                    }

                    route("/payment") {
                        basicAuth {
                            get("/method") { PaymentRouteScreen.getAllAvailableMethod(call) }
                            get("/all") { PaymentRouteScreen.getAllPayment(call) }
                            get { PaymentRouteScreen.getPayment(call) }
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
                            .hanaDocs("Add product (seller)", 2) { addProductSeller() }

                        get("/category") { ProductRouteScreen.getAllCategory(call) }
                    }
                }
            }
        }
    }
}