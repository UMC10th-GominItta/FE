package com.gominitta.android.domain.model.session

import java.time.LocalDateTime
data class SessionRecord(
    val id: Long,
    val recordType: RecordType,
    val contentText: String,
    val mediaUrl: String?,
    val createdAt: LocalDateTime,
)

/** 세션 기록 유형 — 서버 `recordType` (text / voice / handwriting). */
enum class RecordType(val raw: String) {
    TEXT("text"),
    VOICE("voice"),
    HANDWRITING("handwriting"),
    ;

    companion object {
        fun fromRaw(raw: String): RecordType =
            entries.find { it.raw.equals(raw, ignoreCase = true) } ?: TEXT
    }
}
