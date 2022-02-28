import com.github.doyaaaaaken.kotlincsv.dsl.csvReader

suspend fun main(args: Array<String>) {
    createNewUser()
}

suspend fun createNewUser() {
    println("start")

    val customer = readUserCsv(Client.getCsv(Client.CSV_USER_CUSTOMER))
    val seller = readUserCsv(Client.getCsv(Client.CSV_USER_SELLER))

    println("customer --> $customer")
    println("----")
    println("seller --> $seller")

    Client.ping()

    customer.forEach {
        println("create customer for -> ${it["username"]}")
        val response = Client.addCustomer(it)
        println("response -> $response")
    }

    seller.forEach {
        println("create seller for -> ${it["username"]}")
        val response = Client.addSeller(it)
        println("response -> $response")
    }
}

fun readUserCsv(csv: String): List<Map<String, String>> {
    return csvReader().readAll(csv).map {
        mapOf(
            "username" to it[0],
            "password" to it[1],
            "full_name" to it[2],
            "city" to it[3]
        )
    }.toMutableList().apply {
        removeFirst()
    }
}

