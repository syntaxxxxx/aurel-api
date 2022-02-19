package me.jfenn.ktordocs.`interface`

import me.jfenn.ktordocs.model.ParameterInfo

interface HasParams {

    val params: HashMap<String, ParameterInfo>

    /**
     * Provide information about a parameter or argument of a
     * particular endpoint / request.
     */
    fun param(name: String, configure: ParameterInfo.() -> Unit = {}) {
        params[name] = (params[name] ?: ParameterInfo(name)).apply { configure() }
    }

}