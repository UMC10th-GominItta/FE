package com.gominitta.android.presentation

import androidx.lifecycle.ViewModel
import com.gominitta.android.data.auth.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    sessionManager: SessionManager,
) : ViewModel() {
    val sessionExpired = sessionManager.sessionExpired
}
