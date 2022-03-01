import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import org.apache.tika.Tika

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

    const val CSV_PRODUCT_1 =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTORAfyMaFUH0Joyt0_GqMjr_b9OV5aB2w2BDHDXhjs-si5o228rjzxRIylL90LLuj7GPZ9f93uk1KM/pub?gid=9630758&single=true&output=csv"

    const val CSV_PRODUCT_2 =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTORAfyMaFUH0Joyt0_GqMjr_b9OV5aB2w2BDHDXhjs-si5o228rjzxRIylL90LLuj7GPZ9f93uk1KM/pub?gid=648637306&single=true&output=csv"

    const val CSV_PRODUCT_3 =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTORAfyMaFUH0Joyt0_GqMjr_b9OV5aB2w2BDHDXhjs-si5o228rjzxRIylL90LLuj7GPZ9f93uk1KM/pub?gid=1045805880&single=true&output=csv"

    const val CSV_CATEGORY =
        "https://docs.google.com/spreadsheets/d/e/2PACX-1vTORAfyMaFUH0Joyt0_GqMjr_b9OV5aB2w2BDHDXhjs-si5o228rjzxRIylL90LLuj7GPZ9f93uk1KM/pub?gid=2043188301&single=true&output=csv"

    private const val BASE_URL = "https://aurel-store.herokuapp.com"
    private const val PING = "$BASE_URL/ping"
    private const val REGISTER_CUSTOMER = "$BASE_URL/v1/user/customer/register"
    private const val REGISTER_SELLER = "$BASE_URL/v1/user/seller/register"

    private const val LOGIN = "$BASE_URL/v1/user/login"
    private const val ADD_PRODUCT = "$BASE_URL/v1/seller/product"
    private const val ADD_CATEGORY = "$BASE_URL/v1/category"

    suspend fun ping() {
        client.get(PING)
    }

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

    suspend fun getToken(user: Map<String, String>): String {
        val data = client.post(LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(user)
        }

        val loginData = Gson().fromJson<LoginResponse>(data.bodyAsText(), object : TypeToken<LoginResponse>() {
        }.type)
        return loginData.data.token
    }

    suspend fun addProduct(product: Map<String, String>, token: String): String {
        val image = getImage(product["image_url"].orEmpty())
        val fileName = product["file_name"].orEmpty()
        val tika = Tika()
        val mimeType = tika.detect(fileName)

        val data = client.post(ADD_PRODUCT) {
            header("Authorization", token)
            val content = MultiPartFormDataContent(
                formData {
                    append("name", product["name"].orEmpty())
                    append("stock", product["stock"].orEmpty())
                    append("price", product["price"].orEmpty())
                    append("category", product["category"].orEmpty())
                    append("description", product["description"].orEmpty())
                    append("sold_count", product["sold_count"].orEmpty())
                    append("image", image, Headers.build {
                        append(HttpHeaders.ContentType, mimeType)
                        append(HttpHeaders.ContentDisposition, "filename=$fileName")
                    })
                }
            )
            setBody(content)
        }

        return data.bodyAsText()
    }

    suspend fun addCategory(category: Map<String, String>): String {
        val tika = Tika()

        val name = category["name"].orEmpty()
        val imageCover = getImage(category["image_cover"].orEmpty())
        val imageIcon = getImage(category["image_icon"].orEmpty())

        val filenameCover = category["filename_cover"]
        val filenameIcon = category["filename_icon"]


        val mimeTypeCover = tika.detect(filenameCover)
        val mimeTypeIcon = tika.detect(filenameIcon)

        val data = client.post(ADD_CATEGORY) {
            val content = MultiPartFormDataContent(
                formData {
                    append("name", name)
                    append("image_cover", imageCover, Headers.build {
                        append(HttpHeaders.ContentType, mimeTypeCover)
                        append(HttpHeaders.ContentDisposition, "filename=$filenameCover")
                    })
                    append("image_icon", imageIcon, Headers.build {
                        append(HttpHeaders.ContentType, mimeTypeIcon)
                        append(HttpHeaders.ContentDisposition, "filename=$filenameIcon")
                    })
                }
            )
            setBody(content)
        }

        return data.bodyAsText()
    }

    suspend fun getImage(imageUrl: String): ByteArray {
        val data = client.get(imageUrl)
        return data.body()
    }
}