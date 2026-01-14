package com.example.annam

import kotlinx.serialization.Serializable

@Serializable
data class UserCredential (val email: String)

@Serializable
data class UserToken (val token: String)

@Serializable
data class TokenResponse(val code: Int, val message: String)

@Serializable
data class AudioRequest(val word: String, val email: String, val token: String)

@Serializable
data class AudioResponse(val code: Int, val message: String)

@Serializable
data class SearchResultRoute(
    val englishQuery: String,
    val vietnameseQuery: String,
    val englishExact: Boolean,
    val vietnameseExact: Boolean
)
