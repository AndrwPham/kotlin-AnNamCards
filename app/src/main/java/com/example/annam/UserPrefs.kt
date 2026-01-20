package com.example.annam

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore(
    name = "user_credentials"
)

val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")
