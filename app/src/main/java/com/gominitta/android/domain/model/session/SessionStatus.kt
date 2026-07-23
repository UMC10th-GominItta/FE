package com.gominitta.android.domain.model.session

enum class SessionStatus(val raw: String) {
    SCHEDULED("scheduled"),
    INCOMPLETE("incomplete"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    ;

    companion object {
        fun fromRaw(raw: String): SessionStatus =
            entries.find { it.raw == raw } ?: error("Unknown session status: $raw")
    }
}