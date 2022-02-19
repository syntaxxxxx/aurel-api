package me.jfenn.ktordocs.model

import me.jfenn.ktordocs.`interface`.HasParams
import me.jfenn.ktordocs.`interface`.HasReferences

data class AuthenticationInfo(
    val name: String,
    var title: String = name,
    var desc: String = "No description provided.",
    var subDesc: String? = null,
    internal var type: Type = Type.Unknown
) : HasReferences, HasParams {

    override var references = arrayListOf(
        ReferenceInfo("https://ktor.io/servers/features/authentication", "Ktor Documentation")
    )

    override val params = HashMap<String, ParameterInfo>()

    sealed class Type(val value: String) {
        object Unknown : Type("")
        object Basic : Type("basic")
        class Form(
            val userParamName: String = "user",
            val passwordParamName: String = "password"
        ) : Type("form")
        object Digest : Type("digest")
        object JWT : Type("jwt")
        object OAuth : Type("oauth")
    }

    fun type(type: Type) {
        when (type) {
            is Type.Basic -> {
                subDesc = "The username and password are provided as raw values in a request header."
                param("Basic") {
                    desc = "A string containing the username and password separated by a colon (':') character."
                    location = ParameterInfo.In.Header
                    example = "username:password"
                }
            }
            is Type.Form -> {
                subDesc = "The username and password are provided as raw values from a form submission."
                param(type.userParamName) {
                    desc = "The user's username / identifier."
                    location = ParameterInfo.In.FormData
                }
                param(type.passwordParamName) {
                    desc = "The user's raw password."
                    location = ParameterInfo.In.FormData
                }
                reference("https://developer.mozilla.org/en-US/docs/Web/API/FormData/Using_FormData_Objects", "Web FormData API")
            }
            is Type.Digest -> {
                subDesc = "The username and password are provided in a `Digest` authorization header."
                param("Authorization") {
                    desc = "A string containing various request authorization properties."
                    location = ParameterInfo.In.Header
                    example = """Digest realm="{realm}" username="{username}" uri="{uri}" nonce="{nonce}" opaque="{opaque}" nc="{nc}" algorithm="MD5" response="{response}" cnonce="{cnonce}" qop="{qop}""""
                }
                reference("https://en.wikipedia.org/wiki/Digest_access_authentication", "Digest access authentication (Wikipedia)")
            }
            is Type.JWT -> {
                subDesc = "The client provides a stateless JSON Web Token in a `Bearer` authorization header."
                param("Authorization") {
                    desc = "A string containing various request authorization properties."
                    location = ParameterInfo.In.Header
                    example = "Bearer {token}"
                }
                reference("https://jwt.io/", "JWT.io")
            }
            is Type.OAuth -> {
                subDesc = "The client must authenticate with an external OAuth provider."
                reference("https://ktor.io/servers/features/authentication/oauth.html", "Ktor OAuth Guide")
                reference("https://oauth.net/2/", "OAuth 2.0 Specification")
            }
            else -> {}
        }
    }

}