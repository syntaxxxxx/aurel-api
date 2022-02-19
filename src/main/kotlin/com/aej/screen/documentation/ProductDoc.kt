package com.aej.screen.documentation

import me.hana.docs.annotation.DocFieldDescription
import me.hana.docs.data.DocFile
import me.hana.docs.data.isRequired
import me.hana.docs.endpoint.EndPoint
import me.hana.docs.endpoint.multipartParameter
import me.hana.docs.endpoint.queryParameter
import me.hana.docs.endpoint.responseSample

data class Product(
    @DocFieldDescription("status response")
    var status: Boolean = true,
    @DocFieldDescription("code response")
    var code: Int = 200,
    @DocFieldDescription("message response")
    var message: String = "Success",
    @DocFieldDescription("data response")
    var data: ProductData = ProductData()
)

data class ProductPaging(
    @DocFieldDescription("status response")
    var status: Boolean = true,
    @DocFieldDescription("code response")
    var code: Int = 200,
    @DocFieldDescription("message response")
    var message: String = "Success",
    @DocFieldDescription("data response")
    var data: ProductPagingData = ProductPagingData()
)

data class ProductPagingData(
    @DocFieldDescription("count all product")
    var size: Int = 12,
    @DocFieldDescription("count products per page")
    var size_per_page: Int = 4,
    @DocFieldDescription("current page")
    var current_page: Int = 1,
    @DocFieldDescription("product data for current page")
    var products: List<ProductData> = listOf(
        ProductData(),
        ProductData(),
        ProductData(),
        ProductData()
    )
)

data class ProductData(
    var id: String = "1234",
    var name: String = "kipas angin",
    @DocFieldDescription("User info of product owner")
    var seller: UserInfo = UserInfo(),
    @DocFieldDescription("stock availability for this product")
    var stock: Int = 12,
    var category: String = "elektronik",
    var price: Long = 20000,
    var imageUrl: String = "https://image.url/img.png"
) {
    data class UserInfo(
        var id: String = "qwe123",
        var name: String = "ucup"
    )
}

typealias DocumentationProduct = Product
typealias DocumentationProductUserInfo = ProductData.UserInfo

fun EndPoint.productParent() {
    description = """
        All about product, *authentication* is required. As customer, you can get product, and get category <br>.
        As seller, you can add product, get product, and get category
    """.trimIndent()
}

fun EndPoint.addProductSeller() {
    description = """
                Add product, only user with role seller
            """.trimIndent()

    multipartParameter("name", String::class) {
        isRequired()
        description = "name of product"
        sample = "kipas angin"
    }

    multipartParameter("quantity", Int::class) {
        isRequired()
        description = "quantity of product"
        sample = 20
    }

    multipartParameter("price", Long::class) {
        isRequired()
        description = "price of product"
        sample = 20000
    }

    multipartParameter("category", String::class) {
        isRequired()
        description = "category of product"
        sample = "elektronik"
    }

    multipartParameter("image", DocFile::class) {
        isRequired()
        description = "name of product"
        sample = DocFile("kipas.png")
    }

    authRequired()
    responseSample(DocumentationProduct())
}

fun EndPoint.getProduct() {
    description = """
        Get available product, you can get all product with param `product_id` for get single product. For all product
        used paging, default page size is 4.
    """.trimIndent()

    queryParameter("product_id", String::class) {
        description = "get current product is, ***response is single product, not array or paging***"
        sample = "pXyz123"
    }

    authRequired()
    responseSample(ProductPaging())
}