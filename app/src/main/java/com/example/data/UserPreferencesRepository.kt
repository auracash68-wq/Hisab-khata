package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.strings.AppLanguage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hisab_khata_prefs")

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val HAS_SEEDED = booleanPreferencesKey("has_seeded")
        val LAST_BACKUP_MILLIS = longPreferencesKey("last_backup_millis")
        val HAS_UNREAD_ALERT = booleanPreferencesKey("has_unread_alert")
    }

    val languageFlow: Flow<AppLanguage> = context.dataStore.data.map { prefs ->
        AppLanguage.fromCode(prefs[Keys.LANGUAGE_CODE] ?: AppLanguage.ENGLISH.code)
    }

    val lastBackupMillisFlow: Flow<Long?> = context.dataStore.data.map { prefs ->
        prefs[Keys.LAST_BACKUP_MILLIS]
    }

    val hasUnreadAlertFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.HAS_UNREAD_ALERT] ?: true
    }

    suspend fun hasSeededInitialData(): Boolean {
        return context.dataStore.data.map { it[Keys.HAS_SEEDED] ?: false }.first()
    }

    suspend fun markSeeded() {
        context.dataStore.edit { it[Keys.HAS_SEEDED] = true }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.dataStore.edit { it[Keys.LANGUAGE_CODE] = language.code }
    }

    suspend fun setLastBackupMillis(millis: Long) {
        context.dataStore.edit { it[Keys.LAST_BACKUP_MILLIS] = millis }
    }

    suspend fun setHasUnreadAlert(unread: Boolean) {
        context.dataStore.edit { it[Keys.HAS_UNREAD_ALERT] = unread }
    }
}
