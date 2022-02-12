package com.aej.services.payment

import com.aej.repository.payment.Payment
import com.aej.repository.payment.PaymentType

data class PaymentAvailableData(
    var name: String = "",
    var code: String = "",
    var isActivated: Boolean = true,
    var paymentType: PaymentType = PaymentType.VA
) {

    companion object {
        suspend fun createAll(): List<PaymentAvailableData> {
            val vaList = PaymentServices.VirtualAccount.getListVa().map {
                PaymentAvailableData(
                    name = "${it.name} - Virtual Account",
                    code = Payment.codeVaOf(it.code),
                    isActivated = it.isActivated,
                    paymentType = PaymentType.VA
                )
            }

            val merchantList = PaymentServices.Merchant.getListMerchant().map {
                PaymentAvailableData(
                    name = it,
                    code = Payment.codeMerchantOf(it),
                    isActivated = true,
                    paymentType = PaymentType.MERCHANT
                )
            }

            val invoiceList = listOf(
                PaymentAvailableData(
                    name = "Invoice",
                    code = "",
                    isActivated = true,
                    paymentType = PaymentType.INVOICE
                )
            )

            return vaList + merchantList + invoiceList
        }
    }
}