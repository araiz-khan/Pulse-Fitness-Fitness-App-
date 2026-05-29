package com.example.data

import kotlinx.coroutines.flow.Flow

class WorkoutRepository(private val workoutDao: WorkoutDao) {
    val allWorkoutLogs: Flow<List<WorkoutLog>> = workoutDao.getAllWorkoutLogs()
    val latestRecommendation: Flow<AiRecommendation?> = workoutDao.getLatestRecommendation()

    suspend fun insertWorkoutLog(log: WorkoutLog) {
        workoutDao.insertWorkoutLog(log)
    }

    suspend fun deleteWorkoutLog(log: WorkoutLog) {
        workoutDao.deleteWorkoutLog(log)
    }

    suspend fun deleteWorkoutLogById(id: Long) {
        workoutDao.deleteWorkoutLogById(id)
    }

    suspend fun insertRecommendation(rec: AiRecommendation) {
        workoutDao.insertRecommendation(rec)
    }

    suspend fun clearRecommendations() {
        workoutDao.clearRecommendations()
    }
}
