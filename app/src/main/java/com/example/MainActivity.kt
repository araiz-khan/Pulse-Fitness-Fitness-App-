package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.data.AppDatabase
import com.example.data.WorkoutRepository
import com.example.ui.FitnessApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FitnessViewModel
import com.example.viewmodel.FitnessViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    val database = AppDatabase.getDatabase(applicationContext)
    val repository = WorkoutRepository(database.workoutDao)
    val viewModel: FitnessViewModel by viewModels {
      FitnessViewModelFactory(repository)
    }

    setContent {
      MyApplicationTheme {
        FitnessApp(viewModel = viewModel)
      }
    }
  }
}
