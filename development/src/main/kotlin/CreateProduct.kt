import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

suspend fun main(args: Array<String>) {
    val token = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBdXRoZW50aWNhdGlvbiIsImlzcyI6ImNvbS5hZWouYXVyZWwiLCJpZCI6IjQ4ZjI4NThiLTFkMGUtNDJmNi04NmE0LWJkZWUxMDJlMTBiMSIsImV4cCI6MTY0NjUwODQ0NCwiaGFzaCI6IldXMytQaXpDOGJaemRqMldFTGFDemc9PSJ9.cbFz-iYV_PYdRJ7anwfjXZbippH28dvShkKGlARKhuMFaGDCUY0_pzKyEsyyOac-ySqzeD6jFkJn2EpL8m0k0w"

    val product = readProductCsv(Client.getCsv(Client.CSV_PRODUCT))
    product.forEach {
        Client.addProduct(it, token)
    }
}

fun readProductCsv(csv: String): List<Map<String, String>> {
    return csvReader().readAll(csv).map {
        mapOf(
            "name" to it[0],
            "stock" to it[1],
            "price" to it[2],
            "category" to it[3],
            "description" to it[4],
            "image_url" to it[5],
            "sold_count" to it[6],
            "file_name" to it[7]
        )
    }.toMutableList().apply {
        removeFirst()
    }
}