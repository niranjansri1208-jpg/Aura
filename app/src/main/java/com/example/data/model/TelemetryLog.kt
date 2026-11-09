package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "aura_telemetry")
data class TelemetryLogEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val provider: String,
    val model: String,
    val latencyMs: Long,
    val tokensUsed: Int,
    val agentName: String,
    val toolName: String? = null,
    val status: String = "SUCCESS",
    val errorMessage: String? = null
)
