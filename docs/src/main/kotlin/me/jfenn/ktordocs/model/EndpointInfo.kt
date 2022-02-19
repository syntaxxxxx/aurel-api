package me.jfenn.ktordocs.model

import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import me.jfenn.ktordocs.`interface`.HasParams
import me.jfenn.ktordocs.`interface`.HasReferences
import me.jfenn.ktordocs.util.slugify

class EndpointInfo(
    val path: String,
    val method: HttpMethod,
    var title: String = path,
    var desc: String? = null,
    var auth: List<String> = listOf()
) : HasReferences, HasParams {

    val id get() = title.slugify()

    override var references = ArrayList<ReferenceInfo>()

    override val params = HashMap<String, ParameterInfo>()
    internal val responses = HashMap<HttpStatusCode, ResponseInfo>()

    /**
     * Specify a particular response behavior of the current endpoint.
     */
    fun responds(code: HttpStatusCode = HttpStatusCode.OK, configure: ResponseInfo.() -> Unit = {}) {
        responses[code] = (responses[code] ?: ResponseInfo(code)).apply { configure() }
    }

    fun copy(
        path: String = this.path,
        method: HttpMethod = this.method,
        authentication: List<String> = this.auth
    ) : EndpointInfo {
        return EndpointInfo(
            path = path,
            method = method,
            title = title,
            desc = desc,
            auth = authentication
        ).also { new ->
            params.forEach { (name, param) ->
                new.params[name] = param.copy()
            }

            responses.forEach { (code, response) ->
                new.responses[code] = response.copy()
            }

            new.references = ArrayList(references.map { it.copy() })
        }
    }

}