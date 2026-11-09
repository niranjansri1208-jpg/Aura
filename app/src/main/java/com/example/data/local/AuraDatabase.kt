package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.AgentType
import com.example.data.model.AutomationEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.ChatSessionEntity
import com.example.data.model.MemoryCategory
import com.example.data.model.MemoryEntity
import com.example.data.model.MessageRole
import com.example.data.model.SkillEntity
import com.example.data.model.TelemetryLogEntity
import com.example.data.model.TriggerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        ChatMessageEntity::class,
        ChatSessionEntity::class,
        MemoryEntity::class,
        SkillEntity::class,
        AutomationEntity::class,
        TelemetryLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class AuraDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun memoryDao(): MemoryDao
    abstract fun skillDao(): SkillDao
    abstract fun automationDao(): AutomationDao
    abstract fun telemetryDao(): TelemetryDao

    companion object {
        @Volatile
        private var INSTANCE: AuraDatabase? = null

        fun getInstance(context: Context): AuraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AuraDatabase::class.java,
                    "aura_os.db"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: AuraDatabase) {
            // Seed Default Session
            val defaultSessionId = UUID.randomUUID().toString()
            val initialSession = ChatSessionEntity(
                id = defaultSessionId,
                title = "AURA System Initialization",
                agentType = AgentType.ORCHESTRATOR
            )
            database.chatDao().insertSession(initialSession)

            database.chatDao().insertMessage(
                ChatMessageEntity(
                    sessionId = defaultSessionId,
                    role = MessageRole.ASSISTANT,
                    content = "AURA OS Core v1.0 online. Local reasoning engine initialized. Connected tools, memory vault, and multi-agent coordination active. How may I assist you today?",
                    agentType = AgentType.ORCHESTRATOR,
                    latencyMs = 42
                )
            )

            // Seed Built-in Memories
            val defaultMemories = listOf(
                MemoryEntity(
                    category = MemoryCategory.PREFERENCE,
                    key = "Primary Language",
                    value = "Kotlin / Jetpack Compose for Android",
                    isApproved = true
                ),
                MemoryEntity(
                    category = MemoryCategory.FACT,
                    key = "Deployment Target",
                    value = "AURA Personal AI Operating System - Local-first privacy",
                    isApproved = true
                ),
                MemoryEntity(
                    category = MemoryCategory.PROJECT_CONTEXT,
                    key = "Architecture Model",
                    value = "Inspired by OpenJarvis autonomous agent pipeline",
                    isApproved = true
                ),
                MemoryEntity(
                    category = MemoryCategory.INSTRUCTION,
                    key = "Response Tone",
                    value = "Crisp, concise, deeply knowledgeable, cyberpunk command aesthetic",
                    isApproved = true
                )
            )
            defaultMemories.forEach { database.memoryDao().insertMemory(it) }

            // Seed Built-in Skills
            val defaultSkills = listOf(
                SkillEntity(
                    name = "Deep Synthesis Protocol",
                    description = "Performs exhaustive multi-angle research with citations and structured takeaways.",
                    instructions = "Search indexed sources, cross-examine claims, structure into Summary, Key Findings, and Action Steps.",
                    supportedTools = "web_search, doc_search",
                    requiredPermissions = "NETWORK",
                    isEnabled = true,
                    isBuiltIn = true
                ),
                SkillEntity(
                    name = "Code Architecture Review",
                    description = "Examines code patterns, detects security vulnerabilities, and optimizes algorithms.",
                    instructions = "Evaluate complexity, suggest functional idiomatic improvements, and output markdown code snippets.",
                    supportedTools = "code_analyzer",
                    requiredPermissions = "NONE",
                    isEnabled = true,
                    isBuiltIn = true
                ),
                SkillEntity(
                    name = "Data & Math Computation",
                    description = "Evaluates mathematical expressions, financial calculations, and parses structured datasets.",
                    instructions = "Parse numerical input, verify formulas step-by-step, and output verified calculations.",
                    supportedTools = "calculator, data_processor",
                    requiredPermissions = "NONE",
                    isEnabled = true,
                    isBuiltIn = true
                ),
                SkillEntity(
                    name = "Document Summarization",
                    description = "Reads uploaded documents and extracts executive summaries and tabular data.",
                    instructions = "Scan document blocks, extract key metrics and high-priority paragraphs.",
                    supportedTools = "file_reader, doc_search",
                    requiredPermissions = "STORAGE",
                    isEnabled = true,
                    isBuiltIn = true
                )
            )
            database.skillDao().insertSkills(defaultSkills)

            // Seed Built-in Automations
            val defaultAutomations = listOf(
                AutomationEntity(
                    title = "System Telemetry Heartbeat",
                    description = "Pings configured local Ollama endpoint and records latency every 30 minutes.",
                    triggerType = TriggerType.RECURRING,
                    scheduleExpression = "*/30 * * * *",
                    agentType = AgentType.MONITORING_AGENT,
                    actionPrompt = "Check local model endpoint connectivity and log response latency.",
                    isEnabled = true,
                    lastStatus = "Active"
                ),
                AutomationEntity(
                    title = "Daily Knowledge Briefing",
                    description = "Summarizes recent research topics and updates user preference index.",
                    triggerType = TriggerType.SCHEDULED,
                    scheduleExpression = "0 08:00 daily",
                    agentType = AgentType.DEEP_RESEARCHER,
                    actionPrompt = "Synthesize active research notes and compile morning briefing.",
                    isEnabled = true,
                    lastStatus = "Scheduled"
                ),
                AutomationEntity(
                    title = "Memory Vault Verification",
                    description = "Scans conversation memories to prune duplicates and ensure user approval.",
                    triggerType = TriggerType.ONE_TIME,
                    scheduleExpression = "On demand",
                    agentType = AgentType.TASK_PLANNER,
                    actionPrompt = "Verify memory consistency and resolve conflicting user facts.",
                    isEnabled = false,
                    lastStatus = "Pending Approval"
                )
            )
            database.automationDao().insertAutomations(defaultAutomations)
        }
    }
}
