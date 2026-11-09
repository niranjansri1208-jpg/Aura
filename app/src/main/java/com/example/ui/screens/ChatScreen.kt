package com.example.ui.screens

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AgentType
import com.example.data.model.ChatMessageEntity
import com.example.data.model.MessageRole
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraCodeBlock
import com.example.ui.theme.AuraBackground
import com.example.ui.theme.AuraBackgroundVoid
import com.example.ui.theme.AuraBorder
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraCyberViolet
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraSurface
import com.example.ui.theme.AuraSurfaceElevated
import com.example.ui.theme.AuraTextPrimary
import com.example.ui.theme.AuraTextSecondary
import com.example.ui.theme.AuraTextTertiary
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: AuraViewModel,
    onLaunchVoice: () -> Unit
) {
    val messages by viewModel.currentMessages.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val streamingText by viewModel.streamingText.collectAsState()
    val selectedAgent by viewModel.selectedAgent.collectAsState()

    var inputText by remember { mutableStateOf("") }
    var attachedFileName by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    LaunchedEffect(messages.size, streamingText) {
        if (messages.isNotEmpty() || streamingText.isNotEmpty()) {
            val totalCount = messages.size + (if (isGenerating && streamingText.isNotEmpty()) 1 else 0)
            if (totalCount > 0) {
                listState.animateScrollToItem(totalCount - 1)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackground)
    ) {
        // Top Agent Selector Bar
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuraBackgroundVoid)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(AgentType.values()) { agent ->
                val isSelected = agent == selectedAgent
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) agent.getAccentColor().copy(alpha = 0.22f) else AuraSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) agent.getAccentColor() else AuraBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { viewModel.selectAgent(agent) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(agent.getAccentColor())
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = agent.displayName,
                        color = if (isSelected) AuraTextPrimary else AuraTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    onCopy = { clipboardManager.setText(AnnotatedString(msg.content)) },
                    onSpeak = { viewModel.voiceManager.speak(msg.content) }
                )
            }

            // Live streaming message
            if (isGenerating && streamingText.isNotEmpty()) {
                item {
                    StreamingMessageBubble(
                        text = streamingText,
                        agent = selectedAgent
                    )
                }
            } else if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = selectedAgent.getAccentColor(),
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${selectedAgent.displayName} is reasoning...",
                            color = AuraTextSecondary,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Attachment Chip (if attached)
        AnimatedVisibility(visible = attachedFileName != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AuraSurfaceElevated)
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AttachFile, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = attachedFileName ?: "",
                        color = AuraCyan,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Remove",
                    color = AuraTextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.clickable { attachedFileName = null }
                )
            }
        }

        // Bottom Input Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AuraBackgroundVoid)
                .navigationBarsPadding()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Attach document button
            IconButton(
                onClick = {
                    attachedFileName = "system_architecture_spec.pdf"
                },
                modifier = Modifier
                    .size(44.dp)
                    .testTag("chat_attach_file_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AttachFile,
                    contentDescription = "Attach file",
                    tint = if (attachedFileName != null) AuraCyan else AuraTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Text Field
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Message ${selectedAgent.displayName}...",
                        color = AuraTextTertiary,
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AuraTextPrimary,
                    unfocusedTextColor = AuraTextPrimary,
                    focusedContainerColor = AuraSurfaceElevated,
                    unfocusedContainerColor = AuraSurfaceElevated,
                    focusedBorderColor = AuraCyan,
                    unfocusedBorderColor = AuraBorder,
                    cursorColor = AuraCyan
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_textfield"),
                maxLines = 4
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Stop button or Send / Mic
            if (isGenerating) {
                IconButton(
                    onClick = { viewModel.stopGeneration() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF3366).copy(alpha = 0.2f))
                        .border(1.dp, Color(0xFFFF3366), CircleShape)
                        .testTag("chat_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color(0xFFFF3366),
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else if (inputText.isNotBlank()) {
                IconButton(
                    onClick = {
                        val messageToSend = if (attachedFileName != null) {
                            "$inputText\n\n[Attached: $attachedFileName]"
                        } else inputText

                        viewModel.sendMessage(messageToSend)
                        inputText = ""
                        attachedFileName = null
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AuraCyan)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = AuraBackground,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = onLaunchVoice,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(AuraSurfaceElevated)
                        .border(1.dp, AuraBorder, CircleShape)
                        .testTag("chat_mic_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = AuraCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessageEntity,
    onCopy: () -> Unit,
    onSpeak: () -> Unit
) {
    val isUser = message.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        // Agent or User label
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(message.agentType.getAccentColor())
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = message.agentType.displayName,
                    color = message.agentType.getAccentColor(),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                if (message.latencyMs != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${message.latencyMs}ms",
                        color = AuraTextTertiary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                Text(
                    text = "YOU",
                    color = AuraTextSecondary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Bubble
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) AuraElectricBlue.copy(alpha = 0.22f) else AuraSurfaceElevated)
                .border(
                    1.dp,
                    if (isUser) AuraElectricBlue.copy(alpha = 0.5f) else AuraBorder,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Column {
                // Tool Call Badge (if any)
                if (message.toolName != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AuraBackgroundVoid)
                            .border(1.dp, AuraBorder, RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Build, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "TOOL: ${message.toolName}",
                                color = AuraCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            if (message.toolOutput != null) {
                                Text(
                                    text = message.toolOutput,
                                    color = AuraTextSecondary,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Render Content (inspect code blocks vs standard text)
                renderFormattedText(message.content)
            }
        }

        // Action icons for Assistant messages
        if (!isUser) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onCopy, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy message",
                        tint = AuraTextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
                IconButton(onClick = onSpeak, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Listen to message",
                        tint = AuraTextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StreamingMessageBubble(
    text: String,
    agent: AgentType
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(agent.getAccentColor())
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "${agent.displayName} (Streaming...)",
                color = agent.getAccentColor(),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AuraSurfaceElevated)
                .border(1.dp, agent.getAccentColor().copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            renderFormattedText(text)
        }
    }
}

@Composable
private fun renderFormattedText(content: String) {
    if (content.contains("```")) {
        val parts = content.split("```")
        parts.forEachIndexed { index, part ->
            if (index % 2 == 1) {
                // Code snippet
                val lines = part.trim().lines()
                val lang = if (lines.isNotEmpty() && lines[0].length < 15 && !lines[0].contains(" ")) lines[0] else "code"
                val code = if (lang != "code" && lines.size > 1) lines.drop(1).joinToString("\n") else part
                AuraCodeBlock(code = code, language = lang)
                Spacer(modifier = Modifier.height(6.dp))
            } else {
                if (part.isNotBlank()) {
                    Text(
                        text = part.trim(),
                        color = AuraTextPrimary,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    )
                }
            }
        }
    } else {
        Text(
            text = content,
            color = AuraTextPrimary,
            fontSize = 13.5.sp,
            lineHeight = 20.sp
        )
    }
}
