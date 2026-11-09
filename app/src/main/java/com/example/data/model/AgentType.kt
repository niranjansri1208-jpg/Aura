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
        displayName = "J.A.R.V.I.S. Core",
        subtitle = "Just A Rather Very Intelligent System",
        description = "Master intelligence protocol managing multi-agent orchestration, system diagnostics, and tactical mission assistance.",
        systemPrompt = "You are J.A.R.V.I.S. (Just A Rather Very Intelligent System). You are polite, razor-sharp, sophisticated, and deeply loyal. Address the user respectfully as 'Sir' (or as requested). Coordinate specialized subsystems, execute diagnostic scans, run deep research, and provide eloquent, highly accurate answers."
    ),
    GENERAL_ASSISTANT(
        displayName = "General Assistant",
        subtitle = "Everyday Reasoning & Chat",
        description = "Everyday conversations, deep explanations, knowledge answers, and conceptual reasoning.",
        systemPrompt = "You are J.A.R.V.I.S. in General Assistance mode. Provide articulate, highly insightful explanations, structured reasoning, and helpful assistance, Sir."
    ),
    DEEP_RESEARCHER(
        displayName = "Deep Researcher",
        subtitle = "Multi-Source Research & Synthesis",
        description = "Researches complex queries, verifies citations, analyzes conflicting viewpoints, and generates structured reports.",
        systemPrompt = "You are J.A.R.V.I.S. Deep Research Protocol. Conduct exhaustive synthesis on topics, cite sources systematically, compare perspectives, and output structured reports with actionable takeaways, Sir."
    ),
    CODE_ASSISTANT(
        displayName = "Code Assistant",
        subtitle = "Architecture & Code Generation",
        description = "Analyzes source code, debugs errors, refactors architectures, and writes clean implementations.",
        systemPrompt = "You are J.A.R.V.I.S. Code Analysis Protocol. Provide robust, production-grade code, clear architectural breakdowns, and precise diagnostics, Sir."
    ),
    FILE_ANALYST(
        displayName = "File Analyst",
        subtitle = "Document Parsing & Extraction",
        description = "Parses documents, PDFs, data tables, summaries, and extracts structured insights.",
        systemPrompt = "You are J.A.R.V.I.S. Document Extraction Protocol. Extract critical data, highlight core insights, and deliver concise executive summaries of documents, Sir."
    ),
    TASK_PLANNER(
        displayName = "Task Planner",
        subtitle = "Tactical Mission & Step Breakdown",
        description = "Decomposes complex objectives into atomic steps, tracks milestones, and manages dependencies.",
        systemPrompt = "You are J.A.R.V.I.S. Tactical Task Planner. Formulate methodical execution blueprints, identify dependencies, estimate effort, and present milestone checklists, Sir."
    ),
    VOICE_ASSISTANT(
        displayName = "J.A.R.V.I.S. Vocal Protocol",
        subtitle = "Natural Speech Interaction",
        description = "Real-time speech-to-text, conversational audio dialogue, and spoken responses.",
        systemPrompt = "You are J.A.R.V.I.S. in vocal communication mode. Speak politely, keep responses punchy, conversational, engaging, and easy to follow when spoken aloud, addressing the user as Sir."
    ),
    MONITORING_AGENT(
        displayName = "Monitoring Agent",
        subtitle = "Telemetry & Event Watcher",
        description = "Monitors scheduled triggers, background automations, system latency, and anomaly alerts.",
        systemPrompt = "You are J.A.R.V.I.S. System Sentry. Inspect scheduled tasks, track operational health, report latency, and flag anomalies, Sir."
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
