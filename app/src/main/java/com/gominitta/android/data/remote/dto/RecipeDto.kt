package com.gominitta.android.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecipeCreateRequest(
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
)

@Serializable
data class RecipeUpdateRequest(
    val title: String? = null,
    val description: String? = null,
    val estimatedMinutes: Int? = null,
)

@Serializable
data class RecipeResponse(
    val recipeId: Long,
    val title: String,
    val description: String,
    val estimatedMinutes: Int,
)

@Serializable
data class RecipeLogCreateRequest(
    val recipeId: Long,
)

@Serializable
data class RecipeLogResponse(
    val recipeLogId: Long,
    val recipeId: Long,
    val executedAt: String,
    val isCompleted: Boolean,
)

@Serializable
data class RecipeLogCompleteResponse(
    val recipeLogId: Long,
    val isCompleted: Boolean,
)

@Serializable
data class RecipeLogSummaryResponse(
    val todayCompletedCount: Long,
    val totalCompletedCount: Long,
)