import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

suspend fun main(args: Array<String>) {
    createNewProduct()
}

suspend fun createNewProduct() {
    Client.ping()

    readUserCsv(Client.getCsv(Client.CSV_USER_SELLER)).forEachIndexed { index, map ->
        val result = Client.getToken(map)
        val url = when (index) {
            0 -> Client.CSV_PRODUCT_1
            1 -> Client.CSV_PRODUCT_2
            else -> Client.CSV_PRODUCT_3
        }

        readProductCsv(Client.getCsv(url)).forEach { product ->
            Client.addProduct(product, result)
        }
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