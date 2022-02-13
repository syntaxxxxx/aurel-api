package com.aej.services.payment.simulation

import com.aej.MainException
import com.aej.repository.payment.Payment
import com.aej.services.payment.PaymentServices
import com.aej.services.payment.simulation.request.SimulationPaidBody
import com.aej.services.payment.simulation.request.merchant.MerchantSimulationPaidBody
import com.aej.services.payment.simulation.request.va.VaSimulationPaidBody
import com.aej.services.payment.simulation.response.SimulationPaidResponse
import com.aej.utils.removeMerchantSuffix
import io.ktor.http.*

object PaymentSimulationPaidServices {

    private const val EXTERNAL_ID = "externalId"
    private const val URL_VA_PAY_SIMULATION = "https://api.xendit.co/callback_virtual_accounts/external_id={$EXTERNAL_ID}/simulate_payment"
    private const val URL_MERCHANT_PAY_SIMULATION = "https://api.xendit.co/fixed_payment_code/simulate_payment"

    private suspend fun createPaidVaSimulation(requestBody: VaSimulationPaidBody, referenceId: String): SimulationPaidResponse {
        val urlWithReferenceId = URL_VA_PAY_SIMULATION.replace(EXTERNAL_ID, referenceId)
        return PaymentServices.xenditPost(urlWithReferenceId, requestBody)
    }

    private suspend fun createPaidMerchantSimulation(requestBody: MerchantSimulationPaidBody): SimulationPaidResponse {
        return PaymentServices.xenditPost(URL_MERCHANT_PAY_SIMULATION, requestBody)
    }

    suspend fun createPaidSimulation(requestBody: SimulationPaidBody, method: String): SimulationPaidResponse {
        return when {
            method.contains(Payment.Suffix.VIRTUAL_ACCOUNT) -> {
                val vaRequestBody = VaSimulationPaidBody(requestBody.amount)
                createPaidVaSimulation(vaRequestBody, requestBody.referenceId)
            }
            method.contains(Payment.Suffix.MERCHANT) -> {
                val outletName = method.removeMerchantSuffix()
                val merchantRequestBody = MerchantSimulationPaidBody(
                    paymentCode = requestBody.referenceId,
                    transferAmount = requestBody.amount,
                    retailOutletName = outletName
                )
                createPaidMerchantSimulation(merchantRequestBody)
            }
            else -> throw MainException("Method invalid", HttpStatusCode.Forbidden)
        }
    }

}