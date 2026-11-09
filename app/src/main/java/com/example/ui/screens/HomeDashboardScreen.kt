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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentType
import com.example.ui.AuraScreen
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraGlassCard
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraCyberViolet
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraTextTertiary
import java.util.Calendar

@Composable
fun HomeDashboardScreen(
    viewModel: AuraViewModel,
    onNavigate: (AuraScreen) -> Unit,
    onLaunchVoice: () -> Unit
) {
    val isEndpointConnected by viewModel.isEndpointConnected.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()
    val modelName by viewModel.currentModelName.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val skills by viewModel.skills.collectAsState()
    val automations by viewModel.automations.collectAsState()
    val recentSessions by viewModel.sessions.collectAsState()

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        in 17..22 -> "Good evening"
        else -> "Good night"
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackground)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Greeting Card
        item {
            AuraGlassCard(
                modifier = Modifier.fillMaxWidth(),
                glowing = true,
                testTag = "home_hero_card"
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AURA CORE // ONLINE",
                            color = AuraCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isEndpointConnected) AuraEmerald else AuraCyan)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isEndpointConnected) "LOCAL OLLAMA (${latencyMs}ms)" else "AUTONOMOUS REASONER",
                                color = if (isEndpointConnected) AuraEmerald else AuraCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "$greeting, Sir.\nHow can J.A.R.V.I.S. assist your objectives?",
                        color = AuraTextPrimary,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 27.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Mark VII Core Online • All Subsystems Nominal • Multi-Agent Protocols Standing By.",
                        color = AuraTextSecondary,
                        fontSize = 12.5.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(AuraScreen.JARVIS_HUD) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraCyan,
                                contentColor = AuraBackground
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("home_jarvis_hud_button")
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("JARVIS HUD", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.createNewSession(AgentType.ORCHESTRATOR)
                                onNavigate(AuraScreen.CHAT)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraSurfaceElevated,
                                contentColor = AuraCyan
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                            modifier = Modifier.testTag("home_new_chat_button")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("New Task", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onLaunchVoice,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AuraSurfaceElevated,
                                contentColor = AuraElectricBlue
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AuraBorder),
                            modifier = Modifier.testTag("home_voice_mode_button")
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Voice", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        item {
            Text(
                text = "SYSTEM WORKSPACES",
                color = AuraTextTertiary,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    title = "Deep Research",
                    desc = "Multi-source synthesis",
                    icon = Icons.Default.Search,
                    color = AuraCyberViolet,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AuraScreen.RESEARCH) }
                )
                QuickActionItem(
                    title = "Code Assistant",
                    desc = "Refactor & analyze",
                    icon = Icons.Default.Code,
                    color = AuraEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.selectAgent(AgentType.CODE_ASSISTANT)
                        viewModel.createNewSession(AgentType.CODE_ASSISTANT)
                        onNavigate(AuraScreen.CHAT)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    title = "Memory Vault",
                    desc = "${memories.size} saved facts",
                    icon = Icons.Default.Memory,
                    color = AuraElectricBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AuraScreen.MEMORY) }
                )
                QuickActionItem(
                    title = "Automations",
                    desc = "${automations.size} workflows",
                    icon = Icons.Default.Schedule,
                    color = AuraCyan,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(AuraScreen.AUTOMATIONS) }
                )
            }
        }

        // Active Specialized Agents Carousel
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SPECIALIZED AI AGENTS (8)",
                    color = AuraTextTertiary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "View All",
                    color = AuraCyan,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onNavigate(AuraScreen.AGENTS) }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(AgentType.values()) { agent ->
                    AgentPillCard(
                        agent = agent,
                        onClick = {
                            viewModel.selectAgent(agent)
                            viewModel.createNewSession(agent)
                            onNavigate(AuraScreen.CHAT)
                        }
                    )
                }
            }
        }

        // System Telemetry & Local Model Card
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AI HARDWARE & MODEL TELEMETRY",
                            color = AuraTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Inspect",
                            color = AuraCyan,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { onNavigate(AuraScreen.MONITOR) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TelemetryMetric(label = "Selected Model", value = modelName)
                        TelemetryMetric(label = "Latency", value = "${latencyMs}ms")
                        TelemetryMetric(label = "Active Skills", value = "${skills.count { it.isEnabled }}")
                        TelemetryMetric(label = "Local Storage", value = "Room DB")
                    }
                }
            }
        }

        // Recent Tasks
        if (recentSessions.isNotEmpty()) {
            item {
                Text(
                    text = "RECENT CONVERSATIONS & TASKS",
                    color = AuraTextTertiary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(recentSessions.take(4)) { session ->
                AuraGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectSession(session.id) },
                    cornerRadius = 12.dp
                ) {
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
                                    .background(session.agentType.getAccentColor())
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = session.title,
                                    color = AuraTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = session.agentType.displayName,
                                    color = AuraTextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.Explore,
                            contentDescription = "Open session",
                            tint = AuraTextTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun QuickActionItem(
    title: String,
    desc: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    AuraGlassCard(
        modifier = modifier.clickable { onClick() },
        cornerRadius = 14.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.18f))
                    .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, color = AuraTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(text = desc, color = AuraTextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun AgentPillCard(
    agent: AgentType,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(AuraSurfaceElevated)
            .border(1.dp, AuraBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(agent.getAccentColor())
                )
                Text(
                    text = "READY",
                    color = AuraEmerald,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = agent.displayName,
                color = AuraTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = agent.subtitle,
                color = AuraTextSecondary,
                fontSize = 10.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TelemetryMetric(label: String, value: String) {
    Column {
        Text(text = label, color = AuraTextSecondary, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, color = AuraCyan, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}
