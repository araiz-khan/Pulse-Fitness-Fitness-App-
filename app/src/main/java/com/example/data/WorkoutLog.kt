package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long, // timestamp miliseconds
    val exerciseName: String,
    val category: String, // "Strength", "Cardio", "Flexibility", "Other"
    val weightKg: Double, // 0 if Cardio
    val reps: Int, // 0 if Cardio
    val durationMin: Double, // 0 if Strength
    val notes: String = ""
)
