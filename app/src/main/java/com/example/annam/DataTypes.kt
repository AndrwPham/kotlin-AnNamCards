package com.example.annam

import kotlinx.serialization.Serializable

@Serializable
data class UserCredential (val email: String)

@Serializable
data class UserToken (val token: String)