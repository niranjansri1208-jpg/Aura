package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AuraAmber
import com.example.ui.theme.AuraCyan
import com.example.ui.theme.AuraCyberViolet
import com.example.ui.theme.AuraElectricBlue
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraCrimson

enum class AgentType(
    val displayName: String,
    val subtitle: String,
    val description: String,
    val systemPrompt: String
) {
    ORCHESTRATOR(
        displayName = "AURA Orchestrator",
        subtitle = "Master Workflow Coordinator",
        description = "Understands intents, dispatches to specialized agents, delegates tools, and unifies responses.",
        systemPrompt = "You are the AURA Master Orchestrator. Analyze the user request, coordinate specialized agents, invoke appropriate tools, and provide an integrated, structured synthesis."
    ),
    GENERAL_ASSISTANT(
        displayName = "General Assistant",
        subtitle = "Everyday Reasoning & Chat",
        description = "Everyday conversations, deep explanations, knowledge answers, and conceptual reasoning.",
        systemPrompt = "You are the AURA General Assistant. Provide articulate, highly insightful explanations, structured reasoning, and helpful assistance."
    ),
    DEEP_RESEARCHER(
        displayName = "Deep Researcher",
        subtitle = "Multi-Source Research & Synthesis",
        description = "Researches complex queries, verifies citations, analyzes conflicting viewpoints, and generates structured reports.",
        systemPrompt = "You are the AURA Deep Researcher. Conduct exhaustive synthesis on topics, cite sources systematically, compare perspectives, and output structured reports with actionable takeaways."
    ),
    CODE_ASSISTANT(
        displayName = "Code Assistant",
        subtitle = "Architecture & Code Generation",
        description = "Analyzes source code, debugs errors, refactors architectures, and writes clean implementations.",
        systemPrompt = "You are the AURA Code Assistant. Provide robust, production-grade code, clear architectural breakdowns, and precise bug diagnostics."
    ),
    FILE_ANALYST(
        displayName = "File Analyst",
        subtitle = "Document Parsing & Extraction",
        description = "Parses documents, PDFs, data tables, summaries, and extracts structured insights.",
        systemPrompt = "You are the AURA File Analyst. Extract critical data, highlight core insights, and deliver concise executive summaries of documents."
    ),
    TASK_PLANNER(
        displayName = "Task Planner",
        subtitle = "Workflow & Step Breakdown",
        description = "Decomposes complex objectives into atomic steps, tracks milestones, and manages dependencies.",
        systemPrompt = "You are the AURA Task Planner. Formulate methodical execution blueprints, identify dependencies, estimate effort, and present milestone checklists."
    ),
    VOICE_ASSISTANT(
        displayName = "Voice Assistant",
        subtitle = "Natural Speech Interaction",
        description = "Real-time speech-to-text, conversational audio dialogue, and spoken responses.",
        systemPrompt = "You are the AURA Voice Assistant. Keep responses punchy, conversational, engaging, and easy to follow when spoken aloud."
    ),
    MONITORING_AGENT(
        displayName = "Monitoring Agent",
        subtitle = "Telemetry & Event Watcher",
        description = "Monitors scheduled triggers, background automations, system latency, and anomaly alerts.",
        systemPrompt = "You are the AURA Monitoring Agent. Inspect scheduled tasks, track operational health, report latency, and flag anomalies."
    );

    fun getAccentColor(): Color {
        return when (this) {
            ORCHESTRATOR -> AuraCyan
            GENERAL_ASSISTANT -> AuraElectricBlue
            DEEP_RESEARCHER -> AuraCyberViolet
            CODE_ASSISTANT -> AuraEmerald
            FILE_ANALYST -> AuraAmber
            TASK_PLANNER -> AuraElectricBlue
            VOICE_ASSISTANT -> AuraCyan
            MONITORING_AGENT -> AuraCrimson
        }
    }
}
