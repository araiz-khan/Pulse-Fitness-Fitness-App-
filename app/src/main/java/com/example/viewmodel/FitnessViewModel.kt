package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AiRecommendation
import com.example.data.WorkoutLog
import com.example.data.WorkoutRepository
import com.example.network.Content
import com.example.network.GenerateContentRequest
import com.example.network.Part
import com.example.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class FitnessViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val workoutLogs: StateFlow<List<WorkoutLog>> = repository.allWorkoutLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val latestRecommendation: StateFlow<AiRecommendation?> = repository.latestRecommendation
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Calculated metrics
    val streakCount: StateFlow<Int> = workoutLogs
        .map { logs -> calculateStreak(logs) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalWorkouts: StateFlow<Int> = workoutLogs
        .map { logs -> logs.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val strengthVolume: StateFlow<Double> = workoutLogs
        .map { logs ->
            logs.filter { it.category == "Strength" }.sumOf { it.weightKg * it.reps }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cardioMinutes: StateFlow<Double> = workoutLogs
        .map { logs ->
            logs.filter { it.category == "Cardio" }.sumOf { it.durationMin }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val topExercise: StateFlow<String> = workoutLogs
        .map { logs ->
            if (logs.isEmpty()) {
                "None yet"
            } else {
                logs.groupBy { it.exerciseName }
                    .maxByOrNull { it.value.size }?.key ?: "None yet"
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "None yet")

    fun addWorkoutLog(
        exerciseName: String,
        category: String,
        weightKg: Double,
        reps: Int,
        durationMin: Double,
        notes: String = ""
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val log = WorkoutLog(
                date = System.currentTimeMillis(),
                exerciseName = exerciseName.trim(),
                category = category,
                weightKg = if (category == "Strength") weightKg else 0.0,
                reps = if (category == "Strength") reps else 0,
                durationMin = if (category == "Cardio") durationMin else 0.0,
                notes = notes.trim()
            )
            repository.insertWorkoutLog(log)
        }
    }

    fun deleteWorkout(log: WorkoutLog) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkoutLog(log)
        }
    }

    fun deleteWorkoutById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWorkoutLogById(id)
        }
    }

    fun generateAiRecommendation() {
        if (_isGenerating.value) return

        viewModelScope.launch {
            _isGenerating.value = true
            _errorMessage.value = null

            val logs = workoutLogs.value
            val prompt = generatePrompt(logs)

            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                _errorMessage.value = "Gemini API key is not configured. Please add it securely through the Secrets panel."
                _isGenerating.value = false
                return@launch
            }

            var lastException: Exception? = null
            var aiText: String? = null
            
            // Try different models sequentially to handle potential server status issues (like HTTP 503)
            val modelsToTry = listOf(
                "gemini-3.5-flash",
                "gemini-3.1-flash-lite-preview",
                "gemini-3.1-pro-preview",
                "gemini-2.5-flash"
            )

            for (modelName in modelsToTry) {
                try {
                    val response = withContext(Dispatchers.IO) {
                        val request = GenerateContentRequest(
                            contents = listOf(Content(parts = listOf(Part(text = prompt))))
                        )
                        RetrofitClient.service.generateContent(modelName, apiKey, request)
                    }
                    val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (text != null) {
                        aiText = text
                        break
                    }
                } catch (e: Exception) {
                    lastException = e
                }
            }

            try {
                if (aiText != null) {
                    val baseSummary = if (logs.isEmpty()) "Initial split setup" else "Based on ${logs.size} recent sets"
                    val recommendation = AiRecommendation(
                        timestamp = System.currentTimeMillis(),
                        recommendationText = aiText,
                        basedOnData = baseSummary
                    )
                    withContext(Dispatchers.IO) {
                        repository.insertRecommendation(recommendation)
                    }
                } else {
                    val fallbackError = lastException?.localizedMessage ?: "Unknown connection error"
                    _errorMessage.value = "Failed to parse a response from Gemini: $fallbackError"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: ${e.localizedMessage ?: "Unknown connection error"}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

    private fun generatePrompt(logs: List<WorkoutLog>): String {
        return if (logs.isEmpty()) {
            """
            The user has starting using Pulse Fitness and has not logged any workouts yet.
            
            Please:
            1. Provide a warm, highly motivating welcome to 'Pulse Fitness'.
            2. Design a structured, optimal weekly workout split (e.g., beginner-friendly upper/lower or full-body split) for their first week.
            3. List actual exercises, target sets, and repetition ranges.
            4. Gently explain that once they log their workouts, this coach will automatically analyze their history to provide progressive overload adjustments and advanced fatigue tips.
            
            Formatting:
            - Return Markdown.
            - Organize with sharp and consistent titles and spacing.
            - Keep the advice direct, scientific, and motivating.
            """.trimIndent()
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val recentSummary = logs.take(15).joinToString("\n") { log ->
                val dateStr = sdf.format(Date(log.date))
                if (log.category == "Strength") {
                    "- $dateStr: ${log.exerciseName} (${log.category}), lifted ${log.weightKg}kg for ${log.reps} reps."
                } else {
                    "- $dateStr: ${log.exerciseName} (${log.category}), duration ${log.durationMin} mins."
                }
            }

            """
            You are an elite, certified personal trainer and strength coach. Analyse the user's recent workouts and supply a personalized workout recommendation and custom optimization tips.

            User's recent workout performance log:
            $recentSummary

            Task:
            1. **Performance Diagnostics**: Briefly evaluate their recent strength lifts or cardio conditioning (identify focus, progressive overload points, or stamina indicators).
            2. **Next Guided Routine**: Recommend a highly specific, customized NEXT session or full routine (naming specific exercises, target weights/reps, or durations) tailored to challenge them and avoid performance plateaus.
            3. **Form & Recovery Recommendations**: Provide 2 target recovery, form, or nutrition hacks directly applicable to their training logs.

            Formatting Rules:
            - Return beautiful, structured Markdown.
            - Organize into clear, elegant sections. Avoid generic, boilerplate text.
            - Keep the tone elite, professional, precise, and encouraging.
            """.trimIndent()
        }
    }

    private fun calculateStreak(logs: List<WorkoutLog>): Int {
        if (logs.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val uniqueDays = logs.map { sdf.format(Date(it.date)) }.distinct().sortedDescending()

        val todayStr = sdf.format(System.currentTimeMillis())
        val yesterdayStr = sdf.format(System.currentTimeMillis() - 86400000)

        // Streak is only valid if they logged today or yesterday
        if (uniqueDays.first() != todayStr && uniqueDays.first() != yesterdayStr) {
            return 0
        }

        var streak = 1
        for (i in 0 until uniqueDays.size - 1) {
            val currentDay = uniqueDays[i]
            val nextDay = uniqueDays[i + 1]

            val currentParsed = sdf.parse(currentDay) ?: break
            val nextParsed = sdf.parse(nextDay) ?: break

            val diff = currentParsed.time - nextParsed.time
            val diffDays = diff / (1000 * 60 * 60 * 24)
            if (diffDays == 1L) {
                streak++
            } else if (diffDays > 1L) {
                break // Gap of more than 1 day, streak ends
            }
        }
        return streak
    }
}

class FitnessViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FitnessViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FitnessViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
