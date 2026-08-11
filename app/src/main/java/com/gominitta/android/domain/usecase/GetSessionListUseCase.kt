package com.gominitta.android.domain.usecase

import com.gominitta.android.domain.model.session.Session
import com.gominitta.android.domain.model.session.SessionStatus
import com.gominitta.android.domain.repository.SessionRepository
import javax.inject.Inject

/**
 * 마음 세션 목록(C101). [status] 없이 호출하면 예정된 세션 + 미완료 세션만 온다 —
 * 서버가 파라미터 없는 기본 목록엔 완료된 세션을 포함하지 않으므로, 완료 탭은
 * `status = SessionStatus.COMPLETED` 로 별도 호출해야 한다.
 */
class GetSessionListUseCase @Inject constructor(
    private val repository: SessionRepository,
) {
    suspend operator fun invoke(status: SessionStatus? = null): List<Session> = repository.getSessions(status)
}
