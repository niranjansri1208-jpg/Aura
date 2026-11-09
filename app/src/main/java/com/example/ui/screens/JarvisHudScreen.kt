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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
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
import com.example.data.model.AgentType
import com.example.data.voice.VoiceState
import com.example.ui.AuraScreen
import com.example.ui.AuraViewModel
import com.example.ui.components.ArcReactorVisualizer
import com.example.ui.components.AuraGlassCard
import com.example.ui.theme.AuraAmber
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBackgroundVoid
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraTextTertiary

@Composable
fun JarvisHudScreen(
    viewModel: AuraViewModel,
    onNavigate: (AuraScreen) -> Unit
) {
    val voiceManager = viewModel.voiceManager
    val voiceState by voiceManager.voiceState.collectAsState()
    val transcript by voiceManager.transcript.collectAsState()
    val audioLevel by voiceManager.audioLevel.collectAsState()
    val isEndpointConnected by viewModel.isEndpointConnected.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()
    val modelName by viewModel.currentModelName.collectAsState()
    val memories by viewModel.memories.collectAsState()

    var lastJarvisReply by remember { mutableStateOf("At your service, Sir. Arc Reactor core online. All subsystems nominal.") }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            voiceManager.startListening()
        }
    }

    // Auto-respond when vocal transcript finishes
    LaunchedEffect(transcript, voiceState) {
        if (voiceState == VoiceState.IDLE && transcript.isNotBlank()) {
            val spokenPrompt = transcript
            val result = viewModel.modelEngine.generateResponse(
                prompt = spokenPrompt,
                agent = AgentType.ORCHESTRATOR
            )
            lastJarvisReply = result.content
            voiceManager.speak(result.content)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackground)
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Holographic Header
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "J.A.R.V.I.S. PROTOCOL // MARK VII",
                    color = AuraCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Stark Tactical Interface",
                    color = AuraTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(AuraEmerald)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CORE ONLINE • ALL SYSTEMS NOMINAL, SIR",
                        color = AuraEmerald,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Center Arc Reactor Visualizer
        item {
            Box(
                modifier = Modifier
                    .size(270.dp)
                    .clip(CircleShape)
                    .background(AuraBackgroundVoid)
                    .border(1.dp, AuraBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                ArcReactorVisualizer(
                    reactorSize = 250.dp,
                    voiceState = voiceState,
                    audioLevel = audioLevel
                )
            }
        }

        // Voice Directive Indicator & Main Mic Trigger
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val stateText = when (voiceState) {
                    VoiceState.LISTENING -> "AWAITING DIRECTIVE, SIR..."
                    VoiceState.PROCESSING -> "COMPUTING TACTICAL MATRIX..."
                    VoiceState.SPEAKING -> "J.A.R.V.I.S. SPEAKING..."
                    VoiceState.ERROR -> "SYSTEM PAUSED"
                    VoiceState.IDLE -> "READY FOR DIRECTIVE, SIR"
                }

                Text(
                    text = stateText,
                    color = AuraCyan,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mute / Stop Speech
                    IconButton(
                        onClick = { voiceManager.stopSpeaking() },
                        enabled = voiceState == VoiceState.SPEAKING,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AuraSurfaceElevated)
                            .border(1.dp, AuraBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop speech",
                            tint = if (voiceState == VoiceState.SPEAKING) AuraCyan else AuraTextTertiary
                        )
                    }

                    // Main Vocal Directive Trigger
                    Box(
                        modifier = Modifier
                            .size(64.dp)
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
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                }
                            }
                            .testTag("jarvis_hud_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Trigger J.A.R.V.I.S. Speech",
                            tint = AuraBackground,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Replay voice
                    IconButton(
                        onClick = { voiceManager.speak(lastJarvisReply) },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AuraSurfaceElevated)
                            .border(1.dp, AuraBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Replay speech",
                            tint = AuraCyan
                        )
                    }
                }
            }
        }

        // Live J.A.R.V.I.S. Audio & Speech Transcript Card
        item {
            AuraGlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowing = true
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "J.A.R.V.I.S. LIVE AUDIO STREAM",
                            color = AuraCyan,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (transcript.isNotBlank()) "Directive Logged" else "Standing By",
                            color = AuraEmerald,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (transcript.isNotBlank()) {
                        Text(
                            text = "You: \"$transcript\"",
                            color = AuraTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = lastJarvisReply,
                        color = AuraTextSecondary,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Fast Tactical Directives (One-Tap Protocols)
        item {
            Text(
                text = "TACTICAL DIRECTIVES",
                color = AuraTextTertiary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TacticalDirectiveButton(
                        title = "Diagnostic Protocol",
                        subtitle = "Full Subsystem Health Scan",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val reply = "Running full diagnostic protocol, Sir. Arc Reactor core at 100% output. Memory vault secure. Local reasoning engine ready. All subsystems nominal."
                            lastJarvisReply = reply
                            voiceManager.speak(reply)
                        }
                    )
                    TacticalDirectiveButton(
                        title = "Deep Research Matrix",
                        subtitle = "Multi-Source Investigation",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AuraScreen.RESEARCH) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TacticalDirectiveButton(
                        title = "Code Architecture",
                        subtitle = "Inspect & Optimize Algorithms",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            viewModel.selectAgent(AgentType.CODE_ASSISTANT)
                            onNavigate(AuraScreen.CHAT)
                        }
                    )
                    TacticalDirectiveButton(
                        title = "Security Perimeter",
                        subtitle = "${memories.size} Encrypted Items",
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AuraScreen.MEMORY) }
                    )
                }
            }
        }

        // Subsystem Telemetry Bar
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TelemetryPod(title = "CORE MATRIX", value = "100% OK", color = AuraEmerald)
                    TelemetryPod(title = "AI ENGINE", value = if (isEndpointConnected) "OLLAMA" else "STANDALONE", color = AuraCyan)
                    TelemetryPod(title = "LATENCY", value = "${latencyMs}ms", color = AuraTextPrimary)
                    TelemetryPod(title = "PROTOCOLS", value = "8 ACTIVE", color = AuraAmber)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TacticalDirectiveButton(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AuraSurfaceElevated)
            .border(1.dp, AuraBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = AuraTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = AuraCyan,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = AuraTextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun TelemetryPod(title: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title, color = AuraTextTertiary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = color, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
