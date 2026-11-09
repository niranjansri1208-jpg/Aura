package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class MemoryCategory(val displayName: String) {
    PREFERENCE("Preference"),
    FACT("User Fact"),
    PROJECT_CONTEXT("Project Context"),
    INSTRUCTION("Instruction"),
    SUMMARY("Conversation Summary")
}

@Entity(tableName = "aura_memories")
data class MemoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val category: MemoryCategory,
    val key: String,
    val value: String,
    val sourceSessionId: String? = null,
    val isApproved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
