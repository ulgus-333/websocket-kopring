package com.practice.api.domain.presentation.type

enum class FilePathType (
    private val pathFormat: String
) {
    PROFILE("user/profiles/%s/"),
    ;

    fun generateFilePath(vararg variables: String?): String {
        if (variables.isEmpty()) {
            return pathFormat
        }
        return String.format(this.pathFormat, *variables)
    }
}