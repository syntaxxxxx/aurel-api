import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

suspend fun main(args: Array<String>) {
    createCategory()
}

suspend fun createCategory() {
    Client.ping()
    readCategoryCsv(Client.getCsv(Client.CSV_CATEGORY)).forEach {
        Client.addCategory(it)
    }
}

private fun readCategoryCsv(csv: String): List<Map<String, String>> {
    return csvReader().readAll(csv).map {
        mapOf(
            "name" to it[0],
            "image_cover" to it[1],
            "image_icon" to it[2],
            "filename_cover" to it[3],
            "filename_icon" to it[4]
        )
    }.toMutableList().apply {
        removeFirst()
    }.filterNot { it.containsValue("") }
}