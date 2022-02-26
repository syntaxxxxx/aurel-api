import com.google.gson.FieldNamingPolicy
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*

object Client {
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
        }

        install(ContentNegotiation) {
            gson {
                setPrettyPrinting()
                setLenient()
                setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            }
        }
    }

    const val CSV_USER_CUSTOMER =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTORAfyMaFUH0Joyt0_GqMjr_b9OV5aB2w2BDHDXhjs-si5o228rjzxRIylL90LLuj7GPZ9f93uk1KM/pub?gid=0&single=true&output=csv"
    const val CSV_USER_SELLER =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTORAfyMaFUH0Joyt0_GqMjr_b9OV5aB2w2BDHDXhjs-si5o228rjzxRIylL90LLuj7GPZ9f93uk1KM/pub?gid=955433307&single=true&output=csv"

    private const val BASE_URL = "https://aurel-store.herokuapp.com"
    private const val REGISTER_CUSTOMER = "$BASE_URL/v1/user/customer/register"
    private const val REGISTER_SELLER = "$BASE_URL/v1/user/seller/register"

    suspend fun getCsv(csvUrl: String): String {
        println("getting....")
        val data = client.get(csvUrl)
        return data.bodyAsText()
    }

    suspend fun addCustomer(user: Map<String, String>): String {
        val data = client.post(REGISTER_CUSTOMER) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return data.bodyAsText()
    }

    suspend fun addSeller(user: Map<String, String>): String {
        val data = client.post(REGISTER_SELLER) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        return data.bodyAsText()
    }
}