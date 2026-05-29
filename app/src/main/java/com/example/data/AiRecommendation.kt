package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_recommendations")
data class AiRecommendation(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val recommendationText: String,
    val basedOnData: String
)
