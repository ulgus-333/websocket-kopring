package com.practice.infra.domain.type

enum class FilePathType (
    private val pathFormat: String,
    private val paramCount: Int
) {
    PROFILE("user/profiles/%s/", 1),
    CHAT_ATTACHMENT("chat/attachments/%s/%s/", 2),
    ;

    fun generateFilePath(vararg variables: String?): String {
        if (this.paramCount != variables.size) {
            throw IllegalArgumentException("Invalid number of variables")
        }
        if (variables.isEmpty()) {
            return pathFormat
        }
        return String.format(this.pathFormat, *variables)
    }
}