package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentType
import com.example.ui.components.AuraGlassCard
import com.example.ui.components.AuraTopBar
import com.example.ui.screens.AgentsScreen
import com.example.ui.screens.AutomationsScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HomeDashboardScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.ResearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SkillsScreen
import com.example.ui.screens.SystemMonitorScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.VoiceDialog
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBackgroundVoid
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraTextTertiary
import kotlinx.coroutines.launch

@Composable
fun AuraWorkstationLayout(viewModel: AuraViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val isEndpointConnected by viewModel.isEndpointConnected.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()
    val currentModel by viewModel.currentModelName.collectAsState()
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val isVoiceModalOpen by viewModel.isVoiceModalOpen.collectAsState()
    val isIntelligenceSheetOpen by viewModel.isIntelligenceSheetOpen.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWide = maxWidth >= 840.dp
        val isMedium = maxWidth in 600.dp..839.dp

        if (isWide) {
            // Full 3-Column Desktop / Tablet Workstation
            Row(modifier = Modifier.fillMaxSize()) {
                // 1. Left Sidebar (Fixed 240.dp)
                AuraSidebar(
                    currentScreen = currentScreen,
                    selectedAgent = selectedAgent,
                    onNavigate = { viewModel.navigateTo(it) },
                    onNewChat = { viewModel.createNewSession() },
                    modifier = Modifier
                        .width(240.dp)
                        .fillMaxHeight()
                )

                // 2. Center Stage (Weight 1f)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    AuraTopBar(
                        isEndpointConnected = isEndpointConnected,
                        latencyMs = latencyMs,
                        currentModel = currentModel,
                        onMenuClick = null,
                        onIntelligenceClick = null,
                        onVoiceClick = { viewModel.setVoiceModalOpen(true) },
                        onSettingsClick = { viewModel.navigateTo(AuraScreen.SETTINGS) }
                    )

                    Box(modifier = Modifier.weight(1f)) {
                        RenderCurrentScreen(currentScreen, viewModel)
                    }
                }

                // 3. Right Intelligence Panel (Fixed 280.dp)
                AuraIntelligencePanel(
                    viewModel = viewModel,
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight()
                )
            }
        } else {
            // Compact / Phone Layout with Modal Navigation Drawer
            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerContainerColor = AuraBackgroundVoid,
                        modifier = Modifier.width(280.dp)
                    ) {
                        AuraSidebar(
                            currentScreen = currentScreen,
                            selectedAgent = selectedAgent,
                            onNavigate = {
                                viewModel.navigateTo(it)
                                scope.launch { drawerState.close() }
                            },
                            onNewChat = {
                                viewModel.createNewSession()
                                scope.launch { drawerState.close() }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        AuraTopBar(
                            isEndpointConnected = isEndpointConnected,
                            latencyMs = latencyMs,
                            currentModel = currentModel,
                            onMenuClick = { scope.launch { drawerState.open() } },
                            onIntelligenceClick = { viewModel.setIntelligenceSheetOpen(!isIntelligenceSheetOpen) },
                            onVoiceClick = { viewModel.setVoiceModalOpen(true) },
                            onSettingsClick = { viewModel.navigateTo(AuraScreen.SETTINGS) }
                        )

                        Box(modifier = Modifier.weight(1f)) {
                            RenderCurrentScreen(currentScreen, viewModel)
                        }
                    }

                    // Sliding Right Intelligence Drawer for Mobile
                    AnimatedVisibility(
                        visible = isIntelligenceSheetOpen,
                        enter = slideInHorizontally(initialOffsetX = { it }),
                        exit = slideOutHorizontally(targetOffsetX = { it }),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(300.dp)
                                .background(AuraBackgroundVoid)
                                .border(1.dp, AuraBorder)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("INTELLIGENCE PANEL", color = AuraCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { viewModel.setIntelligenceSheetOpen(false) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Close, contentDescription = "Close", tint = AuraTextSecondary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                AuraIntelligencePanel(
                                    viewModel = viewModel,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Voice Assistant Overlay Modal
        if (isVoiceModalOpen) {
            VoiceDialog(
                viewModel = viewModel,
                onDismiss = { viewModel.setVoiceModalOpen(false) }
            )
        }
    }
}

@Composable
private fun RenderCurrentScreen(
    currentScreen: AuraScreen,
    viewModel: AuraViewModel
) {
    when (currentScreen) {
        AuraScreen.HOME -> HomeDashboardScreen(
            viewModel = viewModel,
            onNavigate = { viewModel.navigateTo(it) },
            onLaunchVoice = { viewModel.setVoiceModalOpen(true) }
        )
        AuraScreen.CHAT -> ChatScreen(
            viewModel = viewModel,
            onLaunchVoice = { viewModel.setVoiceModalOpen(true) }
        )
        AuraScreen.RESEARCH -> ResearchScreen(viewModel = viewModel)
        AuraScreen.AGENTS -> AgentsScreen(
            viewModel = viewModel,
            onNavigate = { viewModel.navigateTo(it) }
        )
        AuraScreen.MEMORY -> MemoryScreen(viewModel = viewModel)
        AuraScreen.SKILLS -> SkillsScreen(viewModel = viewModel)
        AuraScreen.TOOLS -> ToolsScreen(viewModel = viewModel)
        AuraScreen.AUTOMATIONS -> AutomationsScreen(viewModel = viewModel)
        AuraScreen.MONITOR -> SystemMonitorScreen(viewModel = viewModel)
        AuraScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
    }
}

@Composable
fun AuraSidebar(
    currentScreen: AuraScreen,
    selectedAgent: AgentType,
    onNavigate: (AuraScreen) -> Unit,
    onNewChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(AuraBackgroundVoid)
            .border(1.dp, AuraBorder)
            .padding(16.dp)
            .navigationBarsPadding()
    ) {
        // App Title Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(AuraCyan)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "AURA",
                    color = AuraTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "AI OPERATING SYSTEM",
                    color = AuraCyan,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // New Task Button
        Button(
            onClick = onNewChat,
            colors = ButtonDefaults.buttonColors(
                containerColor = AuraCyan,
                contentColor = AuraBackground
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("sidebar_new_chat_button")
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("New Conversation", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "NAVIGATION",
            color = AuraTextTertiary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 6.dp, bottom = 6.dp)
        )

        // Nav items
        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            item {
                SidebarNavItem(
                    title = "Home",
                    icon = Icons.Default.Home,
                    isSelected = currentScreen == AuraScreen.HOME,
                    onClick = { onNavigate(AuraScreen.HOME) }
                )
            }
            item {
                SidebarNavItem(
                    title = "AI Chat",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    isSelected = currentScreen == AuraScreen.CHAT,
                    onClick = { onNavigate(AuraScreen.CHAT) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Research",
                    icon = Icons.Default.Search,
                    isSelected = currentScreen == AuraScreen.RESEARCH,
                    onClick = { onNavigate(AuraScreen.RESEARCH) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Agents (8)",
                    icon = Icons.Default.People,
                    isSelected = currentScreen == AuraScreen.AGENTS,
                    onClick = { onNavigate(AuraScreen.AGENTS) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Memory Vault",
                    icon = Icons.Default.Memory,
                    isSelected = currentScreen == AuraScreen.MEMORY,
                    onClick = { onNavigate(AuraScreen.MEMORY) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Skills",
                    icon = Icons.Default.Bolt,
                    isSelected = currentScreen == AuraScreen.SKILLS,
                    onClick = { onNavigate(AuraScreen.SKILLS) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Tools",
                    icon = Icons.Default.Build,
                    isSelected = currentScreen == AuraScreen.TOOLS,
                    onClick = { onNavigate(AuraScreen.TOOLS) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Automations",
                    icon = Icons.Default.Schedule,
                    isSelected = currentScreen == AuraScreen.AUTOMATIONS,
                    onClick = { onNavigate(AuraScreen.AUTOMATIONS) }
                )
            }
            item {
                SidebarNavItem(
                    title = "System Monitor",
                    icon = Icons.Default.MonitorHeart,
                    isSelected = currentScreen == AuraScreen.MONITOR,
                    onClick = { onNavigate(AuraScreen.MONITOR) }
                )
            }
            item {
                SidebarNavItem(
                    title = "Settings",
                    icon = Icons.Default.Settings,
                    isSelected = currentScreen == AuraScreen.SETTINGS,
                    onClick = { onNavigate(AuraScreen.SETTINGS) }
                )
            }
        }
    }
}

@Composable
private fun SidebarNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AuraCyan.copy(alpha = 0.15f) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isSelected) AuraCyan else AuraTextSecondary,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = if (isSelected) AuraTextPrimary else AuraTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun AuraIntelligencePanel(
    viewModel: AuraViewModel,
    modifier: Modifier = Modifier
) {
    val selectedAgent by viewModel.selectedAgent.collectAsState()
    val modelName by viewModel.currentModelName.collectAsState()
    val latencyMs by viewModel.lastLatencyMs.collectAsState()
    val isConnected by viewModel.isEndpointConnected.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val tools = viewModel.toolRegistry.tools

    LazyColumn(
        modifier = modifier
            .background(AuraBackgroundVoid)
            .border(1.dp, AuraBorder)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "INTELLIGENCE ENGINE",
                color = AuraCyan,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }

        // Active Agent Card
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ACTIVE AGENT", color = AuraTextTertiary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(selectedAgent.getAccentColor())
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = selectedAgent.displayName,
                        color = AuraTextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedAgent.subtitle,
                        color = selectedAgent.getAccentColor(),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Active Model & Telemetry
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Text("MODEL SPECIFICATION", color = AuraTextTertiary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = modelName, color = AuraTextPrimary, fontSize = 13.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isConnected) "Ollama Online" else "Local Standalone",
                            color = if (isConnected) AuraEmerald else AuraCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${latencyMs}ms",
                            color = AuraTextSecondary,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Available Tools In Sandbox
        item {
            Text(
                text = "AVAILABLE TOOLS (${tools.size})",
                color = AuraTextTertiary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                tools.take(5).forEach { tool ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuraSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(tool.name, color = AuraTextSecondary, fontSize = 11.sp)
                        Text("Ready", color = AuraEmerald, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Memory References In Context
        item {
            Text(
                text = "MEMORY REFERENCES (${memories.size})",
                color = AuraTextTertiary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                memories.take(4).forEach { mem ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .background(AuraSurfaceElevated)
                            .padding(8.dp)
                    ) {
                        Column {
                            Text(mem.key, color = AuraCyan, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            Text(mem.value, color = AuraTextSecondary, fontSize = 10.sp, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}
