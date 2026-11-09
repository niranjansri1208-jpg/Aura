package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AuraDatabase
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
import com.example.data.network.ModelEngine
import com.example.data.network.ModelProvider
import com.example.data.network.OllamaChatMessage
import com.example.data.tools.ToolExecutionResult
import com.example.data.tools.ToolRegistry
import com.example.data.voice.VoiceManager
import com.example.data.voice.VoiceState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AuraScreen(val title: String) {
    HOME("Home"),
    CHAT("AI Chat"),
    RESEARCH("Deep Research"),
    AGENTS("Agents Hub"),
    MEMORY("Memory Vault"),
    SKILLS("Skills Registry"),
    TOOLS("Tool Sandbox"),
    AUTOMATIONS("Automations"),
    MONITOR("System Monitor"),
    SETTINGS("Settings")
}

class AuraViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AuraDatabase.getInstance(application)
    private val chatDao = database.chatDao()
    private val memoryDao = database.memoryDao()
    private val skillDao = database.skillDao()
    private val automationDao = database.automationDao()
    private val telemetryDao = database.telemetryDao()

    val modelEngine = ModelEngine()
    val toolRegistry = ToolRegistry(memoryDao)
    val voiceManager = VoiceManager(application)

    // Navigation State
    private val _currentScreen = MutableStateFlow(AuraScreen.HOME)
    val currentScreen: StateFlow<AuraScreen> = _currentScreen.asStateFlow()

    // Active Agent
    private val _selectedAgent = MutableStateFlow(AgentType.ORCHESTRATOR)
    val selectedAgent: StateFlow<AgentType> = _selectedAgent.asStateFlow()

    // Chat Sessions & Active Session
    val sessions: StateFlow<List<ChatSessionEntity>> = chatDao.getSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeSessionId = MutableStateFlow<String?>(null)
    val activeSessionId: StateFlow<String?> = _activeSessionId.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessageEntity>> = _currentMessages.asStateFlow()

    // Streaming state
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _streamingText = MutableStateFlow("")
    val streamingText: StateFlow<String> = _streamingText.asStateFlow()

    private var activeGenerationJob: Job? = null

    // Voice Modal State
    private val _isVoiceModalOpen = MutableStateFlow(false)
    val isVoiceModalOpen: StateFlow<Boolean> = _isVoiceModalOpen.asStateFlow()

    // Mobile Intelligence Panel Sheet State
    private val _isIntelligenceSheetOpen = MutableStateFlow(false)
    val isIntelligenceSheetOpen: StateFlow<Boolean> = _isIntelligenceSheetOpen.asStateFlow()

    // Telemetry & Model Connection State
    private val _isEndpointConnected = MutableStateFlow(false)
    val isEndpointConnected: StateFlow<Boolean> = _isEndpointConnected.asStateFlow()

    private val _lastLatencyMs = MutableStateFlow(14L)
    val lastLatencyMs: StateFlow<Long> = _lastLatencyMs.asStateFlow()

    private val _currentModelName = MutableStateFlow("llama3:8b")
    val currentModelName: StateFlow<String> = _currentModelName.asStateFlow()

    // Persistent Database Flows
    val memories: StateFlow<List<MemoryEntity>> = memoryDao.getAllMemories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val skills: StateFlow<List<SkillEntity>> = skillDao.getAllSkills()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val automations: StateFlow<List<AutomationEntity>> = automationDao.getAllAutomations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val telemetryLogs: StateFlow<List<TelemetryLogEntity>> = telemetryDao.getRecentLogs(60)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Research Workspace State
    private val _researchQuery = MutableStateFlow("")
    val researchQuery: StateFlow<String> = _researchQuery.asStateFlow()

    private val _researchReport = MutableStateFlow<String?>(null)
    val researchReport: StateFlow<String?> = _researchReport.asStateFlow()

    private val _isResearching = MutableStateFlow(false)
    val isResearching: StateFlow<Boolean> = _isResearching.asStateFlow()

    // Tool Sandbox Execution State
    private val _toolExecutionResult = MutableStateFlow<ToolExecutionResult?>(null)
    val toolExecutionResult: StateFlow<ToolExecutionResult?> = _toolExecutionResult.asStateFlow()

    init {
        checkServerPing()
        observeActiveSessionMessages()
    }

    fun navigateTo(screen: AuraScreen) {
        _currentScreen.value = screen
    }

    fun selectAgent(agent: AgentType) {
        _selectedAgent.value = agent
    }

    fun setVoiceModalOpen(open: Boolean) {
        _isVoiceModalOpen.value = open
        if (!open) {
            voiceManager.reset()
        }
    }

    fun setIntelligenceSheetOpen(open: Boolean) {
        _isIntelligenceSheetOpen.value = open
    }

    fun checkServerPing() {
        viewModelScope.launch {
            val (connected, latency) = modelEngine.checkConnection()
            _isEndpointConnected.value = connected
            _lastLatencyMs.value = latency
            _currentModelName.value = modelEngine.currentModel
        }
    }

    fun updateModelConfig(url: String, provider: ModelProvider, model: String) {
        modelEngine.updateConfig(url, provider, model)
        _currentModelName.value = model
        checkServerPing()
    }

    private fun observeActiveSessionMessages() {
        viewModelScope.launch {
            sessions.collect { list ->
                if (_activeSessionId.value == null && list.isNotEmpty()) {
                    _activeSessionId.value = list.first().id
                }
            }
        }

        viewModelScope.launch {
            _activeSessionId.collect { sessionId ->
                if (sessionId != null) {
                    chatDao.getMessagesForSession(sessionId).collect { msgs ->
                        _currentMessages.value = msgs
                    }
                } else {
                    _currentMessages.value = emptyList()
                }
            }
        }
    }

    fun createNewSession(agent: AgentType = _selectedAgent.value) {
        viewModelScope.launch {
            val newSession = ChatSessionEntity(
                title = "New ${agent.displayName} Task",
                agentType = agent
            )
            chatDao.insertSession(newSession)
            _activeSessionId.value = newSession.id
            _selectedAgent.value = agent
            _currentScreen.value = AuraScreen.CHAT
        }
    }

    fun selectSession(sessionId: String) {
        _activeSessionId.value = sessionId
        _currentScreen.value = AuraScreen.CHAT
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            chatDao.deleteSession(sessionId)
            chatDao.clearSessionMessages(sessionId)
            if (_activeSessionId.value == sessionId) {
                val remaining = sessions.value.filter { it.id != sessionId }
                _activeSessionId.value = remaining.firstOrNull()?.id
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isGenerating.value) return

        val sessionId = _activeSessionId.value ?: run {
            val newId = UUID.randomUUID().toString()
            val newSession = ChatSessionEntity(
                id = newId,
                title = text.take(30),
                agentType = _selectedAgent.value
            )
            viewModelScope.launch { chatDao.insertSession(newSession) }
            _activeSessionId.value = newId
            newId
        }

        val userMessage = ChatMessageEntity(
            sessionId = sessionId,
            role = MessageRole.USER,
            content = text,
            agentType = _selectedAgent.value
        )

        activeGenerationJob = viewModelScope.launch(Dispatchers.IO) {
            chatDao.insertMessage(userMessage)
            _isGenerating.value = true
            _streamingText.value = ""

            // Retrieve active memory context for the agent
            val approvedMemories = memoryDao.getApprovedMemories().firstOrNull() ?: emptyList()
            val memoryContext = approvedMemories.joinToString("\n") { "- ${it.key}: ${it.value}" }

            // Check if request implies tool usage (e.g. calculator or date)
            val lower = text.lowercase()
            var toolCallInfo: Pair<String, String>? = null

            if (lower.contains("calculate") || lower.contains("sqrt") || lower.contains("+") || lower.contains("*")) {
                val mathExpr = text.substringAfter("calculate").trim().ifEmpty { text }
                val toolRes = toolRegistry.executeTool("calculator", mathExpr)
                toolCallInfo = Pair("Mathematical Calculator", toolRes.output)
            } else if (lower.contains("time") || lower.contains("date") || lower.contains("today")) {
                val toolRes = toolRegistry.executeTool("date_time", "")
                toolCallInfo = Pair("System Time & Date", toolRes.output)
            }

            val history = _currentMessages.value.map {
                OllamaChatMessage(
                    role = if (it.role == MessageRole.USER) "user" else "assistant",
                    content = it.content
                )
            }

            val genResult = modelEngine.generateResponse(
                prompt = text,
                agent = _selectedAgent.value,
                contextMemories = memoryContext,
                conversationHistory = history
            )

            // Stream tokens to UI
            modelEngine.streamTokens(genResult.content).collect { partial ->
                _streamingText.value = partial
            }

            // Save final assistant message to Room
            val assistantMessage = ChatMessageEntity(
                sessionId = sessionId,
                role = MessageRole.ASSISTANT,
                content = genResult.content,
                agentType = _selectedAgent.value,
                toolName = toolCallInfo?.first,
                toolOutput = toolCallInfo?.second,
                latencyMs = genResult.latencyMs
            )
            chatDao.insertMessage(assistantMessage)

            // Record telemetry log
            val telemetryLog = TelemetryLogEntity(
                provider = genResult.providerUsed,
                model = genResult.modelName,
                latencyMs = genResult.latencyMs,
                tokensUsed = genResult.tokensUsed,
                agentName = _selectedAgent.value.displayName,
                toolName = toolCallInfo?.first,
                status = "SUCCESS"
            )
            telemetryDao.insertLog(telemetryLog)

            _isGenerating.value = false
            _streamingText.value = ""
        }
    }

    fun stopGeneration() {
        activeGenerationJob?.cancel()
        _isGenerating.value = false
        _streamingText.value = ""
    }

    // Deep Research
    fun runDeepResearch(query: String) {
        if (query.isBlank()) return
        _researchQuery.value = query
        _isResearching.value = true
        _researchReport.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val genResult = modelEngine.generateResponse(
                prompt = query,
                agent = AgentType.DEEP_RESEARCHER
            )
            _researchReport.value = genResult.content
            _isResearching.value = false

            telemetryDao.insertLog(
                TelemetryLogEntity(
                    provider = genResult.providerUsed,
                    model = genResult.modelName,
                    latencyMs = genResult.latencyMs,
                    tokensUsed = genResult.tokensUsed,
                    agentName = AgentType.DEEP_RESEARCHER.displayName,
                    toolName = "Deep Research Engine"
                )
            )
        }
    }

    // Memory Actions
    fun addMemory(category: MemoryCategory, key: String, value: String) {
        viewModelScope.launch {
            val memory = MemoryEntity(
                category = category,
                key = key,
                value = value,
                isApproved = true
            )
            memoryDao.insertMemory(memory)
        }
    }

    fun updateMemoryApproval(memory: MemoryEntity, approved: Boolean) {
        viewModelScope.launch {
            memoryDao.updateMemory(memory.copy(isApproved = approved, updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            memoryDao.deleteMemory(id)
        }
    }

    // Skills Actions
    fun toggleSkill(skill: SkillEntity) {
        viewModelScope.launch {
            skillDao.updateSkill(skill.copy(isEnabled = !skill.isEnabled))
        }
    }

    fun addCustomSkill(name: String, desc: String, instructions: String, tools: String) {
        viewModelScope.launch {
            val newSkill = SkillEntity(
                name = name,
                description = desc,
                instructions = instructions,
                supportedTools = tools,
                requiredPermissions = "USER_DEFINED",
                isEnabled = true,
                isBuiltIn = false
            )
            skillDao.insertSkill(newSkill)
        }
    }

    // Tool Sandbox Execution
    fun runToolInSandbox(toolId: String, input: String) {
        viewModelScope.launch {
            val res = toolRegistry.executeTool(toolId, input)
            _toolExecutionResult.value = res
            telemetryDao.insertLog(
                TelemetryLogEntity(
                    provider = "Local Tool Engine",
                    model = "Sandbox",
                    latencyMs = res.executionTimeMs,
                    tokensUsed = 0,
                    agentName = "Tool Sandbox",
                    toolName = res.toolName,
                    status = if (res.isSuccess) "SUCCESS" else "ERROR"
                )
            )
        }
    }

    // Automation Trigger
    fun triggerAutomationNow(automation: AutomationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            val startTime = System.currentTimeMillis()
            val genResult = modelEngine.generateResponse(
                prompt = automation.actionPrompt,
                agent = automation.agentType
            )
            val updated = automation.copy(
                lastRunTime = System.currentTimeMillis(),
                executionCount = automation.executionCount + 1,
                lastStatus = "Executed in ${genResult.latencyMs}ms"
            )
            automationDao.updateAutomation(updated)

            telemetryDao.insertLog(
                TelemetryLogEntity(
                    provider = genResult.providerUsed,
                    model = genResult.modelName,
                    latencyMs = genResult.latencyMs,
                    tokensUsed = genResult.tokensUsed,
                    agentName = automation.agentType.displayName,
                    toolName = "Automation Scheduler"
                )
            )
        }
    }

    fun addAutomation(title: String, desc: String, triggerType: TriggerType, prompt: String, agent: AgentType) {
        viewModelScope.launch {
            val item = AutomationEntity(
                title = title,
                description = desc,
                triggerType = triggerType,
                actionPrompt = prompt,
                agentType = agent,
                isEnabled = true
            )
            automationDao.insertAutomation(item)
        }
    }

    fun clearTelemetryLogs() {
        viewModelScope.launch {
            telemetryDao.clearLogs()
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.destroy()
    }
}
