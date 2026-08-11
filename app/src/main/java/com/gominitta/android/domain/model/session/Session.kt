package com.gominitta.android.domain.model.session

import java.time.LocalDateTime

/**
 * 마음 세션 — 목록(C101)과 상세(C102)를 하나로 표현한다. 목록 응답엔 없는 필드는
 * 기본값(null/empty)으로 채워지므로, 상세 화면 진입 전까지는 해당 값들을 비어있는 채로 쓴다.
 */
data class Session(
    val id: Long,
    val worryId: Long,
    val worryTitle: String,
    val worryContent: String,
    val scheduledStartAt: LocalDateTime,
    val scheduledEndAt: LocalDateTime,
    val status: SessionStatus,
    /** 목록 응답엔 없어서 nullable. */
    val emotionScoreBefore: Int? = null,
    /** 상세 응답에만 온다. */
    val themeCategory: String? = null,
    /** 세션 시작 전에는 null. */
    val startedAt: LocalDateTime? = null,
    /** 세션 완료 전에는 null. */
    val completedAt: LocalDateTime? = null,
    /** 완료 처리(PATCH) 전에는 null. */
    val emotionScoreAfter: Int? = null,
    val records: List<SessionRecord> = emptyList(),
)

enum class SessionStatus(val raw: String) {
    SCHEDULED("scheduled"),
    INCOMPLETE("incomplete"),
    IN_PROGRESS("in_progress"),
    COMPLETED("completed"),
    ;

    companion object {
        /** 서버가 엔드포인트마다 대소문자를 다르게 준다(세션은 소문자, 홈은 대문자) — 대소문자 무시. */
        fun fromRaw(raw: String): SessionStatus =
            entries.find { it.raw.equals(raw, ignoreCase = true) } ?: error("Unknown session status: $raw")
    }
}
