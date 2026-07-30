package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.session.SessionSummary
import com.gominitta.android.domain.repository.SessionRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * 마음 세션 목록(C101) — 예정된 세션 + 미완료 세션을 함께, 저장소 변경에 반응하는 Flow로 반환한다.
 * 화면에서 [com.gominitta.android.domain.model.session.SessionStatus] 별로 나눠 표시한다.
 */
class GetSessionListUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    operator fun invoke(): Flow<List<SessionSummary>> = repository.getSessions()
}
