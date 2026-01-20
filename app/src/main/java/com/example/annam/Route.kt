package com.example.annam

import kotlinx.serialization.Serializable

//type-safe navigation route.

@Serializable
data class ShowCard(
    val cardId: Int
)
@Serializable
object LoginRoute

@Serializable
object HomeRoute

@Serializable
object AddCardRoute

@Serializable
object StudyCardsRoute

@Serializable
data class EditCardRoute(
    val english: String,
    val vietnamese: String
)

@Serializable
data class SearchResultsRoute(
    val en: String,
    val vn: String,
    val searchByEnglish: Boolean,
    val searchByVietnamese: Boolean
)
@Serializable
object SearchScreenRoute

@Serializable
data class TokenRoute(
    val email: String
)

