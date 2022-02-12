package com.aej.utils

import com.aej.MainException
import com.aej.repository.cart.Cart
import com.aej.repository.payment.Payment
import com.aej.repository.payment.PaymentType
import com.aej.repository.product.Product
import com.aej.repository.transaction.Transaction
import com.aej.repository.user.User
import com.aej.services.image.ImageFileData
import io.ktor.http.*
import java.util.*

fun Int.isNol() = this == 0
fun Double.isNol() = this == 0.0
fun Long.isNol() = this == 0L

fun Int?.orNol() = this ?: 0
fun Double?.orNol() = this ?: 0.0
fun Long?.orNol() = this ?: 0L

fun String.replaceSpacer(): String = replace("\\s+".toRegex(), "-")
fun String?.orRandom() = this ?: UUID.randomUUID().toString()

fun User?.orThrow(): User = this ?: throw MainException("User not found", HttpStatusCode.BadGateway)
fun Product?.orThrow(): Product = this ?: throw MainException("Product not found", HttpStatusCode.BadGateway)
fun Cart?.orThrow(): Cart = this ?: throw MainException("Cart not found", HttpStatusCode.BadGateway)
fun ImageFileData?.orThrow(): ImageFileData = this ?: throw MainException("Image not found", HttpStatusCode.BadGateway)
fun Transaction?.orThrow(): Transaction = this ?: throw MainException("Image not found", HttpStatusCode.BadGateway)
fun Payment?.orThrow(): Payment = this ?: throw MainException("Payment not found", HttpStatusCode.BadGateway)

fun Double.fixSum(double: Double): Double {
    return (this.toFloat() * double.toFloat()).toDouble()
}

fun ContentType.isImagesType(): Boolean {
    val imageContentType = listOf(
        ContentType.Image.PNG,
        ContentType.Image.JPEG,
        ContentType.Image.SVG,
        ContentType.Image.GIF
    )
    return imageContentType.contains(this)
}

fun Transaction.PaymentTransaction.toPaymentType(): PaymentType {
    return when {
        method.contains(Payment.Suffix.VIRTUAL_ACCOUNT) -> PaymentType.VA
        method.contains(Payment.Suffix.INVOICE) -> PaymentType.INVOICE
        else -> PaymentType.MERCHANT
    }
}

fun String.removeVASuffix(): String = removeSuffix(Payment.Suffix.VIRTUAL_ACCOUNT)
fun String.removeInvoiceSuffix(): String = removeSuffix(Payment.Suffix.INVOICE)
fun String.removeMerchantSuffix(): String = removeSuffix(Payment.Suffix.MERCHANT)