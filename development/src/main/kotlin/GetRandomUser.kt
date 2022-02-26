suspend fun main(args: Array<String>) {

    val customer = readUserCsv(Client.getCsv(Client.CSV_USER_CUSTOMER))
    val seller = readUserCsv(Client.getCsv(Client.CSV_USER_SELLER))


    customer.random().apply {
        println("customer token")
        println(Client.getToken(this))
    }

    seller.random().run {
        println("seller token")
        println(Client.getToken(this))
    }

}