package com.gominitta.android.domain.model.session

import java.time.LocalDateTime

data class SessionRecord(
    val id: Long,
    val recordType: String,
    val contentText: String?,
    val mediaUrl: String?,
    val createdAt: LocalDateTime,
)