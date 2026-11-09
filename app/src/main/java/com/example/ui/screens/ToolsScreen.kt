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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuraToolDefinition
import com.example.ui.AuraViewModel
import com.example.ui.components.AuraGlassCard
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
fun ToolsScreen(viewModel: AuraViewModel) {
    val tools = viewModel.toolRegistry.tools
    var selectedTool by remember { mutableStateOf(tools.first()) }
    var inputParam by remember { mutableStateOf("sqrt(256) + 42") }
    var showApprovalDialog by remember { mutableStateOf(false) }
    val result by viewModel.toolExecutionResult.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AuraBackground)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "MODULAR TOOL ENGINE",
                    color = AuraCyan,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tool Execution Sandbox",
                    color = AuraTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Real tool implementations with input/output schemas and explicit user authorization gates.",
                    color = AuraTextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        // Tool Selector Tabs
        item {
            Text(
                text = "AVAILABLE TOOLS (${tools.size})",
                color = AuraTextTertiary,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tools) { tool ->
                    val isSelected = tool.id == selectedTool.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) AuraCyan.copy(alpha = 0.2f) else AuraSurfaceElevated)
                            .border(1.dp, if (isSelected) AuraCyan else AuraBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                selectedTool = tool
                                inputParam = when (tool.id) {
                                    "calculator" -> "sqrt(256) + 42"
                                    "date_time" -> "UTC"
                                    "web_search" -> "OpenJarvis multi-agent architecture"
                                    "document_search" -> "autonomous reasoner"
                                    "file_reader" -> "system_manifest.json"
                                    "code_analysis" -> "fun execute() { println(\"test\") }"
                                    "data_analysis" -> "{\"records\": [12, 45, 78]}"
                                    "knowledge_retrieval" -> "language"
                                    "text_processing" -> "aura autonomous engine"
                                    else -> ""
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tool.name,
                            color = if (isSelected) AuraCyan else AuraTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Active Tool Execution Card
        item {
            AuraGlassCard(modifier = Modifier.fillMaxWidth(), glowing = true) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Build, contentDescription = null, tint = AuraCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = selectedTool.name,
                                color = AuraTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (selectedTool.requiresUserApproval) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFFFB703).copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFFB703), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Approval Required", color = Color(0xFFFFB703), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = selectedTool.description,
                        color = AuraTextSecondary,
                        fontSize = 12.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Input Schema: ${selectedTool.inputSchema}",
                        color = AuraTextTertiary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = inputParam,
                        onValueChange = { inputParam = it },
                        label = { Text("Input Parameters") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = AuraTextPrimary,
                            unfocusedTextColor = AuraTextPrimary,
                            focusedBorderColor = AuraCyan,
                            unfocusedBorderColor = AuraBorder,
                            cursorColor = AuraCyan
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (selectedTool.requiresUserApproval) {
                                showApprovalDialog = true
                            } else {
                                viewModel.runToolInSandbox(selectedTool.id, inputParam)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AuraCyan, contentColor = AuraBackground),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("execute_tool_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Execute Tool", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Execution Output Card
        if (result != null) {
            item {
                Text(
                    text = "EXECUTION RESULT",
                    color = AuraEmerald,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                AuraGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tool: ${result?.toolName}",
                                color = AuraTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${result?.executionTimeMs}ms • ${if (result?.isSuccess == true) "SUCCESS" else "ERROR"}",
                                color = if (result?.isSuccess == true) AuraEmerald else Color(0xFFFF3366),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AuraBackgroundVoid)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = result?.output ?: "",
                                color = AuraTextPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // User Approval Gate Dialog for sensitive tools
    if (showApprovalDialog) {
        AlertDialog(
            onDismissRequest = { showApprovalDialog = false },
            containerColor = AuraSurfaceElevated,
            icon = { Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFFFB703), modifier = Modifier.size(32.dp)) },
            title = { Text("Permission Required", color = AuraTextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "The tool '${selectedTool.name}' requires explicit user authorization before accessing local files and internal descriptors.\n\nAuthorize execution?",
                    color = AuraTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showApprovalDialog = false
                        viewModel.runToolInSandbox(selectedTool.id, inputParam)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AuraEmerald, contentColor = AuraBackground)
                ) {
                    Text("Authorize & Run", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showApprovalDialog = false }) {
                    Text("Deny", color = AuraTextSecondary)
                }
            }
        )
    }
}
