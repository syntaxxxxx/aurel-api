package me.jfenn.ktordocs.model

data class ParameterInfo(
    val name: String,
    var desc: String = "No description provided.",
    var type: String = TYPE_STRING,
    var isRequired: Boolean = false,
    var location: In = In.Query,
    var example: String = "{${name}}"
) {

    sealed class In(val value: String) {
        object Header : In("header")
        object Path : In("path")
        object Query : In("query")
        object FormData : In("FormData")
    }

    companion object {
        const val TYPE_STRING = "string"
        const val TYPE_INTEGER = "integer"
    }
}