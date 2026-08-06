package com.gominitta.android.domain.model.recipe

data class Recipe(
    val id: Long,
    val title: String,
    val description: String,
    val estimatedMinutes: Long,
)