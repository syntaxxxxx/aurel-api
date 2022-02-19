package me.jfenn.ktordocs.`interface`

import me.jfenn.ktordocs.model.ReferenceInfo

interface HasReferences {

    val references: ArrayList<ReferenceInfo>

    /**
     * Reference a link or URL.
     */
    fun reference(url: String, title: String = url) {
        references.add(ReferenceInfo(url, title))
    }

}