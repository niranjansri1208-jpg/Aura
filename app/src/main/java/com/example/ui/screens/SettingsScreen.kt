package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.network.ModelProvider
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraGlassCard
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraTextTertiary

@Composable
fun SettingsScreen(viewModel: AuraViewModel) {
    val modelEngine = viewModel.modelEngine
    var urlInput by remember { mutableStateOf(modelEngine.baseUrl) }
    var selectedProvider by remember { mutableStateOf(modelEngine.currentProvider) }
    var selectedModel by remember { mutableStateOf(modelEngine.currentModel) }
    val isConnected by viewModel.isEndpointConnected.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackground)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "AURA CONFIGURATION",
                    color = AuraCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Model & Engine Settings",
                    color = AuraTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Configure local Ollama, vLLM, or LM Studio endpoints. Local processing keeps all conversations on your private network.",
                    color = AuraTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Provider Selection Card
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "AI MODEL PROVIDER",
                        color = AuraTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        ModelProvider.values().forEach { provider ->
                            val isSelected = provider == selectedProvider
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) AuraCyan.copy(alpha = 0.15f) else AuraSurfaceElevated)
                                    .border(1.dp, if (isSelected) AuraCyan else AuraBorder, RoundedCornerShape(10.dp))
                                    .clickable { selectedProvider = provider }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = provider.displayName,
                                        color = if (isSelected) AuraCyan else AuraTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when (provider) {
                                            ModelProvider.OLLAMA -> "Default port 11434 • Real streaming /api/chat"
                                            ModelProvider.OPENAI_COMPATIBLE -> "Default port 1234/8000 • /v1/chat/completions"
                                            ModelProvider.AUTONOMOUS_FALLBACK -> "Zero external network requirement • Built-in reasoner"
                                        },
                                        color = AuraTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Endpoint URL & Model Choice
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "SERVER ENDPOINT & MODEL",
                        color = AuraTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = urlInput,
                        onValueChange = { urlInput = it },
                        label = { Text("Endpoint Base URL") },
                        supportingText = { Text("Note: 10.0.2.2 accesses host machine localhost in Android Emulator", fontSize = 10.sp) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder,
                            cursorColor = AuraCyan
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("endpoint_url_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Suggested Models:",
                        color = AuraTextTertiary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(modelEngine.discoveredModels) { model ->
                            val isChosen = model == selectedModel
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) AuraCyan else AuraSurfaceElevated)
                                    .border(1.dp, if (isChosen) AuraCyan else AuraBorder, RoundedCornerShape(8.dp))
                                    .clickable { selectedModel = model }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = model,
                                    color = if (isChosen) AuraBackground else AuraTextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isConnected) AuraEmerald else AuraCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isConnected) "Connected (${latencyMs}ms)" else "Local Autonomous",
                                color = if (isConnected) AuraEmerald else AuraCyan,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.updateModelConfig(urlInput, selectedProvider, selectedModel)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AuraCyan, contentColor = AuraBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_settings_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Apply & Ping", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Security & Privacy Card
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text(
                        text = "SECURITY & DATA ISOLATION",
                        color = AuraTextSecondary,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "AURA operates under zero-cloud telemetry principles. Conversations, persistent memories, skill definitions, and automations are stored strictly inside your on-device Room SQLite database.",
                        color = AuraTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
