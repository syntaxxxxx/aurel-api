package com.aej.utils

import com.aej.repository.cart.Cart
import com.aej.repository.payment.Payment
import com.aej.repository.product.Product
import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.screen.response.*
import com.aej.services.payment.PaymentAvailableData

fun User.mapToResponse(): UserResponse {
    return UserResponse(
        id = id,
        name = name,
        role = role,
        imageUrl = imageUrl,
        fullName = fullName,
        simpleAddress = simpleAddress
    )
}

fun Product.mapToResponse(): ProductResponse {
    return ProductResponse(
        id = id,
        name = name,
        stock = stock,
        price = price,
        imageUrl = imageUrl,
        category = category,
        seller = ProductResponse.UserInfo(userInfo.id, userInfo.name)
    )
}

fun Cart.mapToResponse(): CartResponse {
    val productCart = product.map { CartResponse.ProductCart(it.product, it.quantity, it.createdAt, it.updatedAt) }
    return CartResponse(
        id = id,
        owner = owner,
        amount = amount,
        product = productCart
    )
}

fun Transaction.mapToResponse(): TransactionResponse {
    return TransactionResponse(
        id = id,
        customerId = customerId,
        sellerId = sellerId,
        cartId = cartId,
        products = productData,
        amount = amount,
        statusTransaction = statusTransaction,
        paymentTransaction = paymentTransaction,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Payment.mapToResponse(): PaymentResponse {
    return PaymentResponse(
        id = id,
        ownerId = ownerId,
        amount = amount,
        status = status.name,
        type = type.name,
        method = method,
        referenceId = referenceId,
        transactionId = transactionId,
        externalData = externalData,
        expirationDate = expirationDate
    )
}

fun PaymentAvailableData.mapToResponse(): PaymentAvailableResponse {
    return PaymentAvailableResponse(
        name = name,
        code = code,
        paymentType = paymentType.name,
        isActivated = isActivated
    )
}