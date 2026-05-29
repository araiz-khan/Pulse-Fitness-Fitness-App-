package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.WorkoutLog
import com.example.viewmodel.FitnessViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.testTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FitnessApp(viewModel: FitnessViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    val workoutLogs by viewModel.workoutLogs.collectAsStateWithLifecycle()
    val latestRecommendation by viewModel.latestRecommendation.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    val streakCount by viewModel.streakCount.collectAsStateWithLifecycle()
    val totalWorkouts by viewModel.totalWorkouts.collectAsStateWithLifecycle()
    val strengthVolume by viewModel.strengthVolume.collectAsStateWithLifecycle()
    val cardioMinutes by viewModel.cardioMinutes.collectAsStateWithLifecycle()
    val topExercise by viewModel.topExercise.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            val dateStr = remember {
                val sdf = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
                sdf.format(Date())
            }
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(
                                text = dateStr.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.2.sp,
                                color = com.example.ui.theme.PolishTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Hello, Champion",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp,
                                letterSpacing = (-0.5).sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        // Avatar badge
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(com.example.ui.theme.PolishBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "AM",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = com.example.ui.theme.PolishOnBlueContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 0.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier.drawBehind {
                    drawLine(
                        color = com.example.ui.theme.PolishBorder,
                        start = androidx.compose.ui.geometry.Offset(0f, 0f),
                        end = androidx.compose.ui.geometry.Offset(size.width, 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                }
            ) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                    label = { Text("Home", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.PolishOnBlueContainer,
                        selectedTextColor = com.example.ui.theme.PolishOnBlueContainer,
                        indicatorColor = com.example.ui.theme.PolishBlueContainer,
                        unselectedIconColor = com.example.ui.theme.PolishTextSecondary,
                        unselectedTextColor = com.example.ui.theme.PolishTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Log Workout") },
                    label = { Text("Log", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.PolishOnBlueContainer,
                        selectedTextColor = com.example.ui.theme.PolishOnBlueContainer,
                        indicatorColor = com.example.ui.theme.PolishBlueContainer,
                        unselectedIconColor = com.example.ui.theme.PolishTextSecondary,
                        unselectedTextColor = com.example.ui.theme.PolishTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_log")
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "AI Coach") },
                    label = { Text("AI Coach", fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = com.example.ui.theme.PolishOnBlueContainer,
                        selectedTextColor = com.example.ui.theme.PolishOnBlueContainer,
                        indicatorColor = com.example.ui.theme.PolishBlueContainer,
                        unselectedIconColor = com.example.ui.theme.PolishTextSecondary,
                        unselectedTextColor = com.example.ui.theme.PolishTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tab_coach")
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedTab) {
                0 -> DashboardScreen(
                    logs = workoutLogs,
                    streak = streakCount,
                    total = totalWorkouts,
                    volume = strengthVolume,
                    cardioMins = cardioMinutes,
                    topEx = topExercise,
                    onDelete = { log -> viewModel.deleteWorkout(log) },
                    onNavigateToTab = { selectedTab = it }
                )
                1 -> LogWorkoutScreen(
                    onLog = { name, category, weight, reps, duration, notes ->
                        viewModel.addWorkoutLog(name, category, weight, reps, duration, notes)
                        selectedTab = 0 // Navigate back to Dashboard to see results
                    }
                )
                2 -> CoachScreen(
                    recommendation = latestRecommendation?.recommendationText,
                    recSummary = latestRecommendation?.basedOnData,
                    recTimestamp = latestRecommendation?.timestamp,
                    isGenerating = isGenerating,
                    errorMessage = errorMessage,
                    onGenerate = { viewModel.generateAiRecommendation() }
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    logs: List<WorkoutLog>,
    streak: Int,
    total: Int,
    volume: Double,
    cardioMins: Double,
    topEx: String,
    onDelete: (WorkoutLog) -> Unit,
    onNavigateToTab: (Int) -> Unit
) {
    // Dynamic simulated steps tracker mapping logged activity directly to visible results
    val stepsCount = 4532 + (cardioMins * 120).toInt() + (total * 150)
    val stepsGoal = 10000
    val progressFraction = (stepsCount.toFloat() / stepsGoal).coerceIn(0f, 1f)
    val percent = (progressFraction * 100).toInt()
    val stepsLeft = if (stepsGoal > stepsCount) stepsGoal - stepsCount else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Summary Dashboard Card mimicking the Professional Polish HTML template
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = com.example.ui.theme.PolishBlueContainer,
                    contentColor = com.example.ui.theme.PolishOnBlueContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = "Daily Activity",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = com.example.ui.theme.PolishOnBlueContainer.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = String.format("%,d", stepsCount),
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Black,
                                    color = com.example.ui.theme.PolishOnBlueContainer
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "steps",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = com.example.ui.theme.PolishOnBlueContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                        }
                        // Custom Activity Icon matching HTML layout
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(com.example.ui.theme.PolishSecondary)
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "Goal Icon",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress track
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.35f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progressFraction)
                                .clip(CircleShape)
                                .background(com.example.ui.theme.PolishSecondary)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$percent% of your goal",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.PolishOnBlueContainer
                        )
                        Text(
                            text = String.format("%,d left", stepsLeft),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.PolishOnBlueContainer.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        item {
            // Secondary Stats Grid of 2x2 cards
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ATHLETIC STATS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.2.sp,
                    color = com.example.ui.theme.PolishPrimary,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Streak Plan",
                        value = if (streak == 1) "1 Day" else "$streak Days",
                        subtext = "Consecutive active",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Strength Vol",
                        value = "${volume.toInt()} kg",
                        subtext = "Aggregated sets",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Cardio Time",
                        value = "${cardioMins.toInt()} min",
                        subtext = "Active endurance",
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Top Exercise",
                        value = if (topEx.isBlank() || topEx == "None") "None" else topEx,
                        subtext = "Most sets logged",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            // Premium AI Personalized Routine Promotion Card directly embedded
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = com.example.ui.theme.PolishTealContainer,
                    contentColor = Color.White
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // AI recommended badge tag with absolute premium contrast matching the layout
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(com.example.ui.theme.PolishOnTealContainer)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "AI RECOMMENDED",
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                letterSpacing = 1.sp,
                                color = com.example.ui.theme.PolishTeelButtonText
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Morning Core Stability",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Based on your active logs and trends, a rapid core stabilization set is recommended to bypass fatigue.",
                            fontSize = 12.sp,
                            color = com.example.ui.theme.PolishOnTealContainer.copy(alpha = 0.9f),
                            lineHeight = 16.sp,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { onNavigateToTab(2) }, // Navigate directly to AI Trainer Coach Screen
                            colors = ButtonDefaults.buttonColors(
                                containerColor = com.example.ui.theme.PolishOnTealContainer,
                                contentColor = com.example.ui.theme.PolishTeelButtonText
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("promo_coach_button")
                        ) {
                            Text(
                                text = "Start Routine",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "TODAY'S LOGS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    letterSpacing = 1.2.sp,
                    color = com.example.ui.theme.PolishPrimary
                )
                Text(
                    text = "View all",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = com.example.ui.theme.PolishPrimary,
                    modifier = Modifier
                        .clickable { onNavigateToTab(1) }
                        .padding(horizontal = 4.dp)
                )
            }
        }

        if (logs.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "🏋️‍♂️",
                            fontSize = 40.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "No workouts logged yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Tap on the LOG tab below to record your first set!",
                            fontSize = 13.sp,
                            color = com.example.ui.theme.PolishTextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        } else {
            items(logs, key = { it.id }) { log ->
                val sdf = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }
                val dateStr = sdf.format(Date(log.date))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .testTag("workout_log_item_${log.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PolishSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PolishBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(com.example.ui.theme.PolishPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (log.category == "Strength") "🏋️" else if (log.category == "Cardio") "🏃" else "🧘",
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = log.exerciseName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = com.example.ui.theme.PolishOnBackground
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (log.category == "Strength") {
                                    "${log.weightKg} kg × ${log.reps} reps"
                                } else {
                                    "${log.durationMin} minutes"
                                },
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = com.example.ui.theme.PolishTextSecondary
                            )
                            if (log.notes.isNotEmpty()) {
                                Text(
                                    text = log.notes,
                                    fontSize = 12.sp,
                                    color = com.example.ui.theme.PolishTextSecondary.copy(alpha = 0.6f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Text(
                                text = dateStr,
                                fontSize = 10.sp,
                                color = com.example.ui.theme.PolishTextSecondary.copy(alpha = 0.4f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            val calorieCount = if (log.category == "Strength") {
                                (log.weightKg * log.reps * 0.15).toInt() + 10
                            } else {
                                (log.durationMin * 8).toInt()
                            }
                            Text(
                                text = "+$calorieCount kcal",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = com.example.ui.theme.PolishPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            IconButton(
                                onClick = { onDelete(log) },
                                colors = IconButtonDefaults.iconButtonColors(contentColor = com.example.ui.theme.PolishTextSecondary.copy(alpha = 0.6f)),
                                modifier = Modifier
                                    .testTag("delete_workout_${log.id}")
                                    .size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Workout",
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PolishSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title.uppercase(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                color = com.example.ui.theme.PolishTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            val isShortValue = value.length < 8
            Text(
                text = value,
                fontSize = if (isShortValue) 18.sp else 14.sp,
                fontWeight = FontWeight.Black,
                color = com.example.ui.theme.PolishOnBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = com.example.ui.theme.PolishTextSecondary.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun LogWorkoutScreen(
    onLog: (String, String, Double, Int, Double, String) -> Unit
) {
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Strength") }
    var weightStr by remember { mutableStateOf("40") }
    var reps by remember { mutableIntStateOf(10) }
    var durationStr by remember { mutableStateOf("20") }
    var notes by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PolishSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PolishBorder)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "RECORD PERFORMANCE SET",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = com.example.ui.theme.PolishPrimary,
                    letterSpacing = 1.2.sp
                )

                // Category Selection Panel
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Category Selection",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = com.example.ui.theme.PolishTextSecondary
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(com.example.ui.theme.PolishSurfaceVariant)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Strength", "Cardio", "Activity").forEach { cat ->
                            val isSelected = cat == category
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) com.example.ui.theme.PolishBlueContainer else Color.Transparent
                                    )
                                    .clickable {
                                        category = cat
                                        focusManager.clearFocus()
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) com.example.ui.theme.PolishOnBlueContainer else com.example.ui.theme.PolishTextSecondary
                                )
                            }
                        }
                    }
                }

                // Exercise name input
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        showError = false
                    },
                    label = { Text("Exercise (e.g. Squat, Running, Yoga)") },
                    singleLine = true,
                    isError = showError && name.isBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("exercise_name_input"),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null, tint = com.example.ui.theme.PolishPrimary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.example.ui.theme.PolishPrimary,
                        unfocusedBorderColor = com.example.ui.theme.PolishBorder,
                        focusedLabelColor = com.example.ui.theme.PolishPrimary,
                        cursorColor = com.example.ui.theme.PolishPrimary
                    )
                )

                if (category == "Strength") {
                    // Weight control with incrementers
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Weight (kg)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = com.example.ui.theme.PolishTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val current = weightStr.toDoubleOrNull() ?: 0.0
                                    if (current >= 2.5) {
                                        weightStr = (current - 2.5).toString()
                                    }
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.PolishSurfaceVariant)
                            ) {
                                Text("-2.5", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = com.example.ui.theme.PolishPrimary)
                            }

                            OutlinedTextField(
                                value = weightStr,
                                onValueChange = { weightStr = it },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("weight_input"),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = com.example.ui.theme.PolishPrimary,
                                    unfocusedBorderColor = com.example.ui.theme.PolishBorder,
                                    cursorColor = com.example.ui.theme.PolishPrimary
                                )
                            )

                            IconButton(
                                onClick = {
                                    val current = weightStr.toDoubleOrNull() ?: 0.0
                                    weightStr = (current + 2.5).toString()
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.PolishSurfaceVariant)
                            ) {
                                Text("+2.5", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = com.example.ui.theme.PolishPrimary)
                            }
                        }
                    }

                    // Repetitions counter
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Repetitions",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = com.example.ui.theme.PolishTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(com.example.ui.theme.PolishSurfaceVariant)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            IconButton(
                                onClick = { if (reps > 1) reps-- },
                                modifier = Modifier.testTag("reps_decrement")
                            ) {
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrease reps", tint = com.example.ui.theme.PolishPrimary)
                            }

                            Text(
                                text = "$reps",
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = com.example.ui.theme.PolishOnBackground
                            )

                            IconButton(
                                onClick = { reps++ },
                                modifier = Modifier.testTag("reps_increment")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase reps", tint = com.example.ui.theme.PolishPrimary)
                            }
                        }
                    }
                } else {
                    // Cardio/Duration Input slider/field
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Duration (Minutes)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = com.example.ui.theme.PolishTextSecondary
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    val current = durationStr.toDoubleOrNull() ?: 0.0
                                    if (current >= 5) {
                                        durationStr = (current - 5).toString()
                                    }
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.PolishSurfaceVariant)
                            ) {
                                Text("-5", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = com.example.ui.theme.PolishPrimary)
                            }

                            OutlinedTextField(
                                value = durationStr,
                                onValueChange = { durationStr = it },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("duration_input"),
                                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center, fontWeight = FontWeight.Bold),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = com.example.ui.theme.PolishPrimary,
                                    unfocusedBorderColor = com.example.ui.theme.PolishBorder,
                                    cursorColor = com.example.ui.theme.PolishPrimary
                                )
                            )

                            IconButton(
                                onClick = {
                                    val current = durationStr.toDoubleOrNull() ?: 0.0
                                    durationStr = (current + 5).toString()
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(com.example.ui.theme.PolishSurfaceVariant)
                            ) {
                                Text("+5", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = com.example.ui.theme.PolishPrimary)
                            }
                        }
                    }
                }

                // General Notes input
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Progress notes (how did it feel, etc.)") },
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("workout_notes_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.example.ui.theme.PolishPrimary,
                        unfocusedBorderColor = com.example.ui.theme.PolishBorder,
                        focusedLabelColor = com.example.ui.theme.PolishPrimary,
                        cursorColor = com.example.ui.theme.PolishPrimary
                    )
                )
            }
        }

        // Save & Log Activity
        Button(
            onClick = {
                if (name.isBlank()) {
                    showError = true
                } else {
                    val weightVal = weightStr.toDoubleOrNull() ?: 0.0
                    val durationVal = durationStr.toDoubleOrNull() ?: 0.0
                    onLog(name, category, weightVal, reps, durationVal, notes)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("submit_workout_button"),
            colors = ButtonDefaults.buttonColors(containerColor = com.example.ui.theme.PolishPrimary, contentColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SAVE PERFORMANCE LOG",
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun CoachScreen(
    recommendation: String?,
    recSummary: String?,
    recTimestamp: Long?,
    isGenerating: Boolean,
    errorMessage: String?,
    onGenerate: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PolishSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PolishBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(com.example.ui.theme.PolishBlueContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 28.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "AI TRAINER & ROUTINE COACH",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 1.2.sp,
                    color = com.example.ui.theme.PolishPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Get real-time feedback, routine adjustments, and targeted progressive overload recommendations based on your performance trends.",
                    fontSize = 12.sp,
                    color = com.example.ui.theme.PolishTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onGenerate,
                    enabled = !isGenerating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("generate_recommendation_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.example.ui.theme.PolishPrimary,
                        contentColor = Color.White,
                        disabledContainerColor = com.example.ui.theme.PolishBorder
                    )
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("ANALYZING WORKOUT GRAPH...", fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (recommendation == null) "GENERATE CUSTOM ROUTINE" else "REGENERATE ROUTINE",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        if (recommendation != null) {
            val sdf = remember { SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()) }
            val dateStr = if (recTimestamp != null) sdf.format(Date(recTimestamp)) else "Just now"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recommendation_content_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PolishSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, com.example.ui.theme.PolishBorder)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI TRAINER ADVICE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = com.example.ui.theme.PolishPrimary,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(com.example.ui.theme.PolishBlueContainer)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Active Split",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = com.example.ui.theme.PolishOnBlueContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Formulated: $dateStr",
                        fontSize = 10.sp,
                        color = com.example.ui.theme.PolishTextSecondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (recSummary != null) {
                        Text(
                            text = recSummary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = com.example.ui.theme.PolishSecondary
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = com.example.ui.theme.PolishBorder
                    )

                    // Render beautifully formatted markdown recommendations
                    MarkdownCardList(markdownText = recommendation)
                }
            }
        } else if (!isGenerating) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = com.example.ui.theme.PolishSurfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🧠",
                        fontSize = 32.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Unlock Personal Recommendations",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = com.example.ui.theme.PolishOnBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your coach will study your logged movements and construct optimal training recommendations.",
                        fontSize = 12.sp,
                        color = com.example.ui.theme.PolishTextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun MarkdownCardList(markdownText: String) {
    val lines = markdownText.split("\n")
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        lines.filter { it.isNotBlank() }.forEach { line ->
            val trimmed = line.trim()
            when {
                trimmed.startsWith("# ") -> {
                    Text(
                        text = trimmed.substring(2),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.PolishPrimary,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }
                trimmed.startsWith("## ") -> {
                    Text(
                        text = trimmed.substring(3),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.PolishPrimary,
                        modifier = Modifier.padding(top = 8.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("### ") -> {
                    Text(
                        text = trimmed.substring(4),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.PolishSecondary,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                trimmed.startsWith("**") && trimmed.endsWith("**") -> {
                    Text(
                        text = trimmed.replace("**", ""),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = com.example.ui.theme.PolishOnBackground
                    )
                }
                trimmed.startsWith("- ") || trimmed.startsWith("* ") -> {
                    Row(
                        modifier = Modifier.padding(start = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "• ",
                            style = MaterialTheme.typography.bodyLarge,
                            color = com.example.ui.theme.PolishPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = trimmed.substring(2),
                            style = MaterialTheme.typography.bodyMedium,
                            color = com.example.ui.theme.PolishOnBackground.copy(alpha = 0.85f)
                        )
                    }
                }
                else -> {
                    Text(
                        text = trimmed,
                        style = MaterialTheme.typography.bodyMedium,
                        color = com.example.ui.theme.PolishOnBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// Utility extension function to guarantee minimum vertical weights or standard padding metrics safely
private fun Int.getVerticalWeight(): Float {
    return this.toFloat()
}
