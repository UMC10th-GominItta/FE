package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * 마음 세션 목록(C101) — 예정된 세션 + 미완료 세션을 함께 반환한다.
 * 화면에서 [com.gominitta.android.domain.model.session.SessionStatus] 별로 나눠 표시한다.
 */
class GetSessionListUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(): List<SessionSummary> = repository.getSessions()
}
