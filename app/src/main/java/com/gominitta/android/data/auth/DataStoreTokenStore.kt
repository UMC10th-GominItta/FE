package com.gominitta.android.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authDataStore by preferencesDataStore(name = "auth")

/** [TokenStore]의 실제 구현 — Preferences DataStore에 토큰을 저장한다. */
class DataStoreTokenStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : TokenStore {

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        context.authDataStore.edit { prefs ->
            prefs[accessTokenKey] = accessToken
            prefs[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun getAccessToken(): String? =
        context.authDataStore.data.map { it[accessTokenKey] }.first()

    override suspend fun getRefreshToken(): String? =
        context.authDataStore.data.map { it[refreshTokenKey] }.first()

    override suspend fun clear() {
        context.authDataStore.edit { it.clear() }
    }
}
