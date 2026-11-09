package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AgentType
import com.example.data.model.MemoryCategory
import com.example.data.model.MessageRole
import com.example.data.model.TriggerType

class DatabaseConverters {
    @TypeConverter
    fun fromMessageRole(role: MessageRole): String = role.name

    @TypeConverter
    fun toMessageRole(value: String): MessageRole = try {
        MessageRole.valueOf(value)
    } catch (_: Exception) {
        MessageRole.ASSISTANT
    }

    @TypeConverter
    fun fromAgentType(type: AgentType): String = type.name

    @TypeConverter
    fun toAgentType(value: String): AgentType = try {
        AgentType.valueOf(value)
    } catch (_: Exception) {
        AgentType.ORCHESTRATOR
    }

    @TypeConverter
    fun fromMemoryCategory(category: MemoryCategory): String = category.name

    @TypeConverter
    fun toMemoryCategory(value: String): MemoryCategory = try {
        MemoryCategory.valueOf(value)
    } catch (_: Exception) {
        MemoryCategory.FACT
    }

    @TypeConverter
    fun fromTriggerType(triggerType: TriggerType): String = triggerType.name

    @TypeConverter
    fun toTriggerType(value: String): TriggerType = try {
        TriggerType.valueOf(value)
    } catch (_: Exception) {
        TriggerType.SCHEDULED
    }
}
