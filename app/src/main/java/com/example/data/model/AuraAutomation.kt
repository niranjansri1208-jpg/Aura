package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class TriggerType(val displayName: String) {
    ONE_TIME("One-Time Task"),
    SCHEDULED("Scheduled Event"),
    RECURRING("Recurring Cron"),
    CONDITIONAL("Trigger Condition")
}

@Entity(tableName = "aura_automations")
data class AutomationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val triggerType: TriggerType = TriggerType.SCHEDULED,
    val scheduleExpression: String = "Every day at 09:00",
    val agentType: AgentType = AgentType.MONITORING_AGENT,
    val actionPrompt: String,
    val isEnabled: Boolean = true,
    val lastRunTime: Long? = null,
    val nextRunTime: Long? = null,
    val lastStatus: String = "Ready",
    val executionCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)
