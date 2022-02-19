package me.jfenn.ktordocs

import io.ktor.http.HttpMethod
import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import me.jfenn.ktordocs.`interface`.HasParams
import me.jfenn.ktordocs.`interface`.HasReferences
import me.jfenn.ktordocs.model.EndpointInfo
import me.jfenn.ktordocs.model.ParameterInfo
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

class HtmlBuilder(val docs: RestDocs) {

    private fun DIV.referenceInfo(item: HasReferences) {
        if (item.references.isNotEmpty()) {
            h5 { +"Links" }
            ul {
                item.references.forEach { reference ->
                    li {
                        a(href = reference.url) { +reference.title }
                    }
                }
            }
        }
    }

    private fun DIV.parameterInfo(item: HasParams) {
        if (item.params.isNotEmpty()) {
            h5 { +"Parameters" }
            table("table") {
                thead {
                    tr {
                        th { +"Name" }
                        th { +"Type" }
                        th { +"In" }
                        th { +"Description" }
                    }
                }
                tbody {
                    item.params.forEach { (name, param) ->
                        tr {
                            td {
                                span("text-monospace font-weight-bold") { +name }
                                if (param.isRequired) {
                                    span("text-danger") { +"*" }
                                }
                            }
                            td("text-muted") { +param.type }
                            td("text-muted") { +param.location.value }
                            td("text-muted") {
                                markdown(param.desc, inline = true)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun DIV.endpointInfo(endpoint: EndpointInfo) {
        div("mb-5") {
            div {
                h4("d-inline") {
                    id = endpoint.id
                    a(href = "#" + endpoint.id) { +endpoint.title }
                }
                if (endpoint.auth.isNotEmpty()) {
                    span("float-end") {
                        endpoint.auth.forEach {
                            button(type = ButtonType.button, classes = "btn btn-link text-monospace") {
                                attributes["data-bs-toggle"] = "modal"
                                attributes["data-bs-target"] = "#auth_${it}"

                                +it
                            }
                        }

                        // "lock" icon
                        +"\uD83D\uDD12"
                    }
                }
            }
            endpoint.desc?.let {
                div("text-muted") {
                    markdown(it)
                }
            }
            div("alert bg-light") {
                span("me-3 badge bg-" + when (endpoint.method) {
                    HttpMethod.Get -> "primary"
                    HttpMethod.Post -> "success"
                    HttpMethod.Put -> "warning"
                    HttpMethod.Patch -> "info"
                    HttpMethod.Delete -> "danger"
                    else -> "secondary"
                }) {
                    +endpoint.method.value
                }
                span("text-monospace") {
                    +endpoint.path
                }
            }

            parameterInfo(endpoint)

            h5 { +"Code samples" }
            codeblock(tabSize = 2) {
                +buildString {
                    appendln("curl -X ${endpoint.method.value} \\")

                    endpoint.params.filterValues { it.location == ParameterInfo.In.Header }.forEach {
                        appendln("\t-H '${it.value.name}: ${it.value.example}' \\")
                    }

                    if (endpoint.method == HttpMethod.Post || endpoint.method == HttpMethod.Put)
                        appendln("\t-d '{data}' \\")

                    appendln("\t${docs.config.baseUrl}${endpoint.path}")
                }
            }

            if (endpoint.responses.isNotEmpty()) {
                h5 { +"Responses" }
                table("table") {
                    thead {
                        tr {
                            th { +"Code" }
                            th { +"Description" }
                            th { +"Value" }
                        }
                    }
                    tbody {
                        endpoint.responses.forEach { (code, response) ->
                            tr {
                                td {
                                    a(href = "https://http.cat/${code.value}") {
                                        span("text-monospace") { +code.value.toString() }
                                        span("text-muted") { +" (${code.description})" }
                                    }
                                }
                                td("text-muted") {
                                    markdown(response.desc, inline = true)
                                }
                                td {
                                    response.example?.let {
                                        codeblock { +it }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            referenceInfo(endpoint)
        }
    }

    fun toHtml() : String = buildString {
        appendln("<!DOCTYPE html>")
        appendHTML().html {
            head {
                meta(charset = "utf-8")
                meta(name = "viewport", content = "width=device-width,initial-scale=1")
                title(docs.config.title)

                link(
                    rel = "stylesheet",
                    href = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css"
                ) {
                    attributes["integrity"] = "sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x"
                    attributes["crossorigin"] = "anonymous"
                }

                style {
                    +"a:not(:hover){text-decoration:none;}table{font-size:0.8rem;display:inline-block;max-width:100%;overflow-x:auto;}.bg-dark a{color:#b1d4fb !important;}"
                }
            }
            body {
                div("row m-0") {
                    div("col-12 col-md-3 bg-dark text-light") {
                        style = "background-color: #05264c !important; font-size: 0.9rem;"

                        div("py-4 px-2") {
                            style = "position: sticky; top: 0;"

                            h3 { +docs.config.title }
                            div("text-white-50") {
                                markdown(docs.config.desc)
                            }

                            h5 { +"Contents" }
                            docs.endpoints.forEach {
                                div("py-1") {
                                    a(href = "#" + it.id) { +it.title }
                                }
                            }
                        }
                    }
                    div("col-12 col-md-9") {
                        div("container my-4") {
                            h1("mb-4") { +"Endpoints" }
                            docs.endpoints.forEach { endpointInfo(it) }
                        }

                        footer("footer py-3 mt-5") {
                            div("container") {
                                span(classes = "text-muted") {
                                    +"Documentation generated by "
                                    a(href = "https://code.horrific.dev/james/ktordocs") { +"Ktor-Docs" }
                                }
                                a(classes = "text-muted float-end", href = "https://www.mozilla.org/en-US/MPL/2.0/") {
                                    +"MPL v2.0"
                                }
                            }
                        }
                    }
                }

                docs.config.authMethods.forEach { (name, auth) ->
                    modal("auth_${name}", auth.title) {
                        markdown(auth.desc)

                        auth.subDesc?.let {
                            p { small("text-muted") { +it } }
                        }

                        parameterInfo(auth)
                        referenceInfo(auth)
                    }
                }

                script(src = "https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js") {
                    attributes["integrity"] = "sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4"
                    attributes["crossorigin"] = "anonymous"
                }
            }
        }
        appendln()
    }
}

fun FlowContent.modal(modalId: String, modalTitle: String, content: DIV.() -> Unit) {
    div("modal fade") {
        id = modalId
        attributes["tabindex"] = "-1"

        div("modal-dialog") {
            div("modal-content") {
                div("modal-header") {
                    h5("modal-title") { +modalTitle }
                    button(type = ButtonType.button, classes = "btn-close") {
                        attributes["data-bs-dismiss"] = "modal"
                    }
                }
                div("modal-body") {
                    content()
                }
            }
        }
    }
}

fun FlowContent.codeblock(tabSize: Int = 4, content: CODE.() -> Unit) {
    pre("alert bg-light") {
        code {
            style = "tab-size: ${tabSize};"
            content()
        }
    }
}

fun HTMLTag.markdown(markdown: String, inline: Boolean = false) {
    val parser = Parser.builder().build()
    val renderer = HtmlRenderer.builder().build()

    val html = renderer.render(parser.parse(markdown))

    unsafe {
        if (inline)
            +html.replace(Regex("</?p>"), "")
        else +html
    }
}
