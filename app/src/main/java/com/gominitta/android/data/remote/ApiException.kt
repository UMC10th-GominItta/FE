package com.gominitta.android.data.remote

/**
 * Business-level failure — server responded with `success: false` (or a non-2xx
 * status carrying the same envelope). [code] is the server's error code
 * (e.g. "AUTH_401", "SESSION_404"); presentation maps it to user-facing copy.
 */
class ApiException(val code: String, override val message: String) : Exception(message)

/** Request never reached the server, or the response couldn't be read (no connectivity, timeout, DNS, ...). */
class NetworkException(override val message: String = "네트워크 연결을 확인해주세요.") : Exception(message)