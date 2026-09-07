package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.DrabatecApp
import com.example.ui.theme.DrabatecServiceTheme
import com.example.ui.viewmodel.DrabatecViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DrabatecServiceTheme {
                val viewModel: DrabatecViewModel = viewModel()
                DrabatecApp(viewModel = viewModel)
            }
        }
    }
}
