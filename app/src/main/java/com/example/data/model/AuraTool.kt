package com.example.data.model

enum class ToolCategory(val title: String) {
    UTILITY("General Utility"),
    RESEARCH("Research & Web"),
    DATA("Data & Math"),
    SYSTEM("System & Code"),
    FILES("Document & Files")
}

data class AuraToolDefinition(
    val id: String,
    val name: String,
    val description: String,
    val category: ToolCategory,
    val inputSchema: String,
    val outputSchema: String,
    val requiresUserApproval: Boolean = false,
    val isEnabled: Boolean = true
)
