package com.diklat.tanyapakar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MyPreference @Inject constructor(private val appContext: Context)  {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    fun getToken(): Flow<String> {
        return appContext.dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }


    suspend fun saveToken(token: String) {
        appContext.dataStore.edit { preferences ->
            preferences[TOKEN] = token
        }
    }

    companion object {
        private val TOKEN = stringPreferencesKey("token")
    }
}