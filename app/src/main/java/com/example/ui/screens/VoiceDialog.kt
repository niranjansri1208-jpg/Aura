package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AgentType
import com.example.data.voice.VoiceState
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraVisualizer
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBackgroundVoid
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary

@Composable
fun VoiceDialog(
    viewModel: AuraViewModel,
    onDismiss: () -> Unit
) {
    val voiceManager = viewModel.voiceManager
    val voiceState by voiceManager.voiceState.collectAsState()
    val transcript by voiceManager.transcript.collectAsState()
    val audioLevel by voiceManager.audioLevel.collectAsState()
    val errorMessage by voiceManager.errorMessage.collectAsState()

    var permissionGranted by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            voiceManager.startListening()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
    }

    // Auto-respond when speech recognition finishes with transcript
    LaunchedEffect(transcript, voiceState) {
        if (voiceState == VoiceState.IDLE && transcript.isNotBlank()) {
            val spokenPrompt = transcript
            val result = viewModel.modelEngine.generateResponse(
                prompt = spokenPrompt,
                agent = AgentType.VOICE_ASSISTANT
            )
            voiceManager.speak(result.content)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AuraBackground.copy(alpha = 0.96f))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Close Button Top-End
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AuraSurfaceElevated)
                    .border(1.dp, AuraBorder, CircleShape)
                    .testTag("close_voice_modal_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Voice Assistant",
                    tint = AuraTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "AURA VOICE ASSISTANT",
                    color = AuraCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Animated Holographic Visualizer
                AuraVisualizer(
                    visualizerSize = 220.dp,
                    voiceState = voiceState,
                    audioLevel = audioLevel
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Voice State Badge
                val stateText = when (voiceState) {
                    VoiceState.LISTENING -> "LISTENING... SPEAK NOW"
                    VoiceState.PROCESSING -> "PROCESSING QUERY..."
                    VoiceState.SPEAKING -> "AURA IS SPEAKING..."
                    VoiceState.ERROR -> "SYSTEM PAUSED"
                    VoiceState.IDLE -> if (transcript.isNotBlank()) "STANDBY" else "TAP MIC TO SPEAK"
                }

                val stateColor = when (voiceState) {
                    VoiceState.LISTENING -> AuraCyan
                    VoiceState.PROCESSING -> AuraElectricBlue
                    VoiceState.SPEAKING -> AuraEmerald
                    VoiceState.ERROR -> Color(0xFFFF3366)
                    VoiceState.IDLE -> AuraTextSecondary
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(stateColor.copy(alpha = 0.15f))
                        .border(1.dp, stateColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stateText,
                        color = stateColor,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Transcript or Prompt Preview
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AuraBackgroundVoid)
                        .border(1.dp, AuraBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (transcript.isNotBlank()) "\"$transcript\"" else (errorMessage ?: "Say something or ask any multi-agent task..."),
                        color = if (transcript.isNotBlank()) AuraTextPrimary else AuraTextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        maxLines = 3
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Controls Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stop audio output
                    IconButton(
                        onClick = { voiceManager.stopSpeaking() },
                        enabled = voiceState == VoiceState.SPEAKING,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AuraSurfaceElevated)
                            .border(1.dp, AuraBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop speech",
                            tint = if (voiceState == VoiceState.SPEAKING) AuraCyan else AuraTextSecondary
                        )
                    }

                    // Main Mic Trigger
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(AuraCyan, AuraElectricBlue)
                                )
                            )
                            .clickable {
                                if (voiceState == VoiceState.LISTENING) {
                                    voiceManager.stopListening()
                                } else {
                                    voiceManager.startListening()
                                }
                            }
                            .testTag("voice_dialog_mic_trigger"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Mic toggle",
                            tint = AuraBackground,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Reset
                    IconButton(
                        onClick = { voiceManager.reset() },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AuraSurfaceElevated)
                            .border(1.dp, AuraBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Voice Active",
                            tint = AuraTextSecondary
                        )
                    }
                }
            }
        }
    }
}
