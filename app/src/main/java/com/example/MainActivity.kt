package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AuraScreen
import com.example.ui.AuraViewModel
import com.example.ui.AuraWorkstationLayout
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AuraTheme {
                val auraViewModel: AuraViewModel = viewModel()
                val currentScreen by auraViewModel.currentScreen.collectAsState()

                // Hardware / gesture back handling: return to HOME if on sub-screen
                BackHandler(enabled = currentScreen != AuraScreen.HOME) {
                    auraViewModel.navigateTo(AuraScreen.HOME)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AuraBackground
                ) {
                    AuraWorkstationLayout(viewModel = auraViewModel)
                }
            }
        }
    }
}
