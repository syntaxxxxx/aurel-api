package me.jfenn.ktordocs.model

import io.ktor.http.HttpStatusCode
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf

data class ResponseInfo(
    val code: HttpStatusCode,
    var desc: String = "No description provided.",
    var example: String? = null
) {

    /**
     * Create an "example" JSON preview based on the
     * provided data class.
     *
     * (this is intended for when there is a specific
     * data class which is passed through a serializer
     * to provide a response)
     */
    fun exampleJson(type: KClass<*>) {
        example = buildString {
            appendln("{")
            val props = type.declaredMemberProperties
            props.forEachIndexed { index, prop ->
                var returnType = prop.returnType.toString()
                if (returnType.startsWith("kotlin.Array")
                    || returnType.startsWith("kotlin.collections.List")
                    || returnType.startsWith("kotlin.collections.ArrayList")) {
                    prop.returnType.arguments.firstOrNull()?.type?.let { listType ->
                        returnType = "[\n\t\t$listType\n\t]"
                    }
                }

                append("\t\"${prop.name}\": $returnType")
                if (index < props.size - 1) appendln(",") else appendln()
            }
            appendln("}")
        }
    }

    // TODO: header properties

}