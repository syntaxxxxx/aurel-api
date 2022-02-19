package me.jfenn.ktordocs.util

fun String.slugify() : String {
    return this.toLowerCase()
        .replace(Regex("[^a-z0-9\\s]"), "") // remove any non-alphanumeric chars
        .take(100) // max length of 100 (longer file/dir names cause problems on some systems)
        .trim()
        .replace(Regex("\\s+"), "-") // replace groups of whitespace with hyphens
}
