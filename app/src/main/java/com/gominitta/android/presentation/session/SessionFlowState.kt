package com.gominitta.android.presentation.session

import javax.inject.Inject
import javax.inject.Singleton

/**
 * 세션 진행 화면(Active → Detail → Complete → Rating) 사이에서 세션 id와 방금 만든
 * 기록(record) id·내용을 들고 다니는 임시 상태. 앱은 한 번에 세션 하나만 진행하므로
 * 단일 슬롯으로 충분하다. 평가 저장(completeSession) 후 [clear] 로 비운다.
 */
@Singleton
class SessionFlowState @Inject constructor() {
    var sessionId: Long? = null
        private set
    var recordId: Long? = null
        private set
    var recordText: String? = null
        private set

    /** 서버에서 이미 in_progress 인지. 시작 처리를 두 번 하지 않으려고 들고 있는다. */
    var isSessionStarted: Boolean = false
        private set

    fun start(sessionId: Long, isSessionStarted: Boolean) {
        this.sessionId = sessionId
        this.isSessionStarted = isSessionStarted
        recordId = null
        recordText = null
    }

    fun markSessionStarted() {
        isSessionStarted = true
    }

    fun setRecord(recordId: Long, recordText: String) {
        this.recordId = recordId
        this.recordText = recordText
    }

    /** 세션은 두고 기록만 비운다. */
    fun clearRecord() {
        recordId = null
        recordText = null
    }

    fun clear() {
        sessionId = null
        recordId = null
        recordText = null
        isSessionStarted = false
    }
}
