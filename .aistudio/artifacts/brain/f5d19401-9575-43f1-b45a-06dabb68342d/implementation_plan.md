# AURA — Advanced Personal AI Operating System

A futuristic, extensible personal AI operating system inspired by OpenJarvis, built natively for Android in Kotlin and Jetpack Compose. AURA features an adaptive multi-pane desktop/tablet workstation with mobile drawer support, 8 specialized AI agents with orchestration, local Ollama and self-hosted model connectivity, persistent memory, tool execution, workflow automation, and an interactive voice assistant with live audio visualization.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following architectural decisions were confirmed during the interactive clarification phase:

- **AI Model Engine (Confirmed)**: **Local Ollama and self-hosted server endpoints only**. AURA will integrate directly with local Ollama (`/api/tags`, `/api/generate`, `/api/chat`) and OpenAI-compatible (`/v1/chat/completions`) endpoints with host discovery (defaulting to `http://10.0.2.2:11434` for emulator host or customizable LAN IP). It includes an offline autonomous fallback reasoning engine when the local endpoint is unreachable.
- **UI Layout Hierarchy (Confirmed)**: **Adaptive 3-pane layout for desktop and tablet with mobile drawer**. On wide and tablet displays, AURA displays a 3-column workstation (Left Navigation Sidebar, Center Chat & Workspace, Right Intelligence & Telemetry Panel). On phone/compact screens, it smoothly collapses the intelligence panel into a slide-over sheet and the sidebar into a bottom navigation rail or drawer.
- **Voice Experience (Confirmed)**: **Interactive voice mode with animated visualizer and audio speech**. Built using Android's native `SpeechRecognizer` for real-time dictation, an animated holographic Canvas visualizer reacting to audio/speech states, and Android's native `TextToSpeech` engine for spoken assistant responses.

---

## 1. Overview & Core Concept

- **What It Does**: AURA (Advanced Unified Reasoning Assistant) is a personal AI operating system designed to manage knowledge, perform autonomous multi-step reasoning, execute specialized tasks, and run scheduled automations while keeping user data private and processed locally.
- **Target Audience / Persona**: Power users, researchers, developers, and tech enthusiasts who want an autonomous, private, desktop-class AI command center on their Android devices without cloud lock-in.
- **Key Value**:
  1. **Local-First Privacy**: Communicates with user-controlled Ollama and local LLM servers; all memory, conversations, skills, and automations are persisted locally via Room SQLite.
  2. **Multi-Agent Orchestration**: Eight specialized agents (General Assistant, Deep Researcher, Code Assistant, File Analyst, Task Planner, Voice Assistant, Monitoring Agent, and Master Orchestrator) coordinated to solve multi-step problems.
  3. **Extensible Tools & Skills**: Real tool execution (Calculators, Document Reader, Date/Time, Code & Data Processors, Web Search Proxy) with explicit user approval gates for sensitive actions.

---

## 2. User Experience & Visual Design

### Visual Identity & Theme
- **Aesthetic Direction**: Futuristic cyberpunk / sci-fi command center with sleek glassmorphism, electric lighting, and high-density information architecture.
- **Color Palette**:
  - `Background`: Deep Midnight Navy (`#060B14`) and Slate Void (`#0B1220`)
  - `Surface / Glass Cards`: Dark Translucent Navy (`#111D33` with 80% opacity) with subtle cyan borders (`#1B3154`)
  - `Primary Accent`: Electric Cyan (`#00F2FE`) and Neon Blue (`#4FACFE`)
  - `Secondary Accent`: Cyber Violet (`#7F00FF`) and Emerald Active (`#00F5A0`)
  - `Text Hierarchy`: Primary White (`#F0F6FC`), Muted Cyan/Slate (`#8B9EB7`), Dimmed Labels (`#546A85`)
- **Typography & Atmosphere**:
  - Monospace accents for agent telemetry, latency, token metrics, and tool execution status.
  - High-contrast clean sans-serif for reading chat and research reports.
  - Holographic animated pulsing Canvas visualizer for active voice mode.

### Workstation Navigation & Views
1. **Left Sidebar (Navigation Hub)**:
   - Quick "New Conversation" action.
   - Core workspaces: **Home Dashboard**, **AI Chat**, **Research**, **Agents**, **Memory**, **Skills**, **Tools**, **Automations**, **Files**, **System Monitor**, **Settings**.
2. **Center Stage (Dynamic Workspace)**:
   - **Home Dashboard**: "Good evening. How can I help?", AI system status, local model selector, active agent pills, quick action cards, recent workflows, and telemetry summary.
   - **AI Chat Area**: Multi-turn conversation with agent tags, streaming simulation/real stream, code block styling, tool execution callout boxes, file attachment chips, voice mic trigger, and copy/regenerate actions.
   - **Research Workspace**: Deep search query input, source citations, synthesis report generator, and markdown export.
   - **Agents Hub**: Cards for all 8 agents displaying role, capabilities, status (Idle, Reasoning, Executing), and direct launch.
   - **Memory Vault**: Inspect user preferences, project context, facts, and conversation summaries with delete/edit controls.
   - **Skills Registry**: Enable/disable skills, view required permissions, and create custom user skills.
   - **Tools Registry**: Test tools (Calculator, Time/Date, File Analyst, Data Parser, Web Proxy) with input/output inspect panels.
   - **Automation Engine**: List of scheduled/recurring workflows, trigger conditions, manual run, and execution logs.
   - **System Monitor**: Real-time Ollama endpoint connection status, latency meter, token metrics, agent activity feed, and error logs.
3. **Right Intelligence Panel (Collapsible on mobile)**:
   - Current active agent card with avatar and status badge.
   - Active model info (e.g., `llama3:8b`, `mistral`, or custom endpoint).
   - Real-time tool invocation pipeline.
   - Memory items active in current context window.
   - Latency gauge & token usage statistics.
4. **Interactive Voice Overlay**:
   - Holographic multi-ring animated audio visualizer.
   - Live speech recognition transcription.
   - Spoken AI responses with TTS waveform animations and mute/stop controls.

---

## 3. Key Product Decisions & Trade-Offs

- **Local Endpoint Client with Robust Fallback**:
  - *Approach*: Built with OkHttp + Moshi to talk directly to local Ollama (`http://10.0.2.2:11434` or user-defined IP) and OpenAI-compatible `/v1/chat/completions`. If the local server is offline or not yet configured, AURA gracefully switches to an on-device Autonomous Simulation Engine that realistically demonstrates agent reasoning, multi-step tool execution, and memory retrieval without crashing or failing.
  - *Why*: Allows immediate standalone testing on emulator/device while fully supporting real Ollama/vLLM/LM Studio instances running on the user's host machine or local network.
- **Persistence via Android Room Database**:
  - *Approach*: Room database with tables for `ChatSessionEntity`, `ChatMessageEntity`, `MemoryEntity`, `SkillEntity`, `AutomationEntity`, and `TelemetryLogEntity`.
  - *Why*: Provides robust, durable, zero-cloud storage that keeps user data 100% private to the device.
- **Native Android Hardware Integration**:
  - *Approach*: Uses Android `android.speech.SpeechRecognizer` for STT and `android.speech.tts.TextToSpeech` for TTS with graceful fallback to simulated voice visualizer if audio permissions are denied.
  - *Why*: Delivers low-latency real-time voice interaction without third-party cloud speech subscriptions.

---

## 4. Technical Architecture & Data Strategy

### System Architecture Diagram

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                           AURA Workstation UI                                 │
│  ┌──────────────────┬─────────────────────────────────┬────────────────────┐  │
│  │   Left Sidebar   │          Center Stage           │ Right Intelligence │  │
│  │  - Navigation    │  - Home / Chat / Research       │ - Active Agent     │  │
│  │  - Workspaces    │  - Agents / Memory / Skills     │ - Model & Latency  │  │
│  │  - New Chat      │  - Automations / Voice Modal    │ - Tool Stream      │  │
│  └─────────┬────────┴────────────────┬────────────────┴─────────┬──────────┘  │
└────────────┼─────────────────────────┼──────────────────────────┼─────────────┘
             │                         │                          │
             ▼                         ▼                          ▼
┌───────────────────────────────────────────────────────────────────────────────┐
│                      AURA Orchestration & State Layer                         │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │                        AuraViewModel & StateHolders                     │  │
│  │   - UI State Flow        - Active Agent Router      - Telemetry Monitor │  │
│  └────────────────────────────────────┬────────────────────────────────────┘  │
│                                       │                                       │
│          ┌────────────────────────────┼────────────────────────────┐          │
│          ▼                            ▼                            ▼          │
│  ┌───────────────┐          ┌──────────────────┐          ┌────────────────┐  │
│  │  Multi-Agent  │          │  Tool Execution  │          │ Speech Engine  │  │
│  │  Orchestrator │◄────────►│     Registry     │          │  (STT & TTS)   │  │
│  │  (8 Agents)   │          │  (Real Impl)     │          └────────────────┘  │
│  └───────┬───────┘          └─────────┬────────┘                              │
└──────────┼────────────────────────────┼───────────────────────────────────────┘
           │                            │
           ▼                            ▼
┌───────────────────────────────────────────────────────────────────────────────┐
│                       Data, Models & Network Layer                            │
│  ┌────────────────────────────────────────┬────────────────────────────────┐  │
│  │         Local Model Engine             │          Room Database         │  │
│  │  - Ollama Client (/api/chat, /tags)    │  - Chat Sessions & Messages    │  │
│  │  - OpenAI-Compatible (/v1/completions) │  - Persistent Memory Vault     │  │
│  │  - Autonomous Reasoner Fallback        │  - Skills & Automations        │  │
│  │  - Token & Latency Meter               │  - Telemetry Logs              │  │
│  └────────────────────────────────────────┴────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

### Data Models & Room Schema

1. **`ChatSession` & `ChatMessage`**:
   - `id: String`, `sessionId: String`, `role: Role` (USER, ASSISTANT, SYSTEM, TOOL), `content: String`, `agentType: AgentType`, `toolCallsJson: String?`, `timestamp: Long`.
2. **`MemoryItem`**:
   - `id: String`, `category: MemoryCategory` (PREFERENCE, FACT, PROJECT_CONTEXT, INSTRUCTION, SUMMARY), `key: String`, `value: String`, `sourceSessionId: String?`, `isApproved: Boolean`, `createdAt: Long`.
3. **`AuraSkill`**:
   - `id: String`, `name: String`, `description: String`, `instructions: String`, `toolsListJson: String`, `requiredPermissionsJson: String`, `isEnabled: Boolean`, `isBuiltIn: Boolean`.
4. **`AuraAutomation`**:
   - `id: String`, `title: String`, `description: String`, `triggerType: TriggerType` (ONE_TIME, SCHEDULED, RECURRING, CONDITIONAL), `cronSchedule: String?`, `agentType: AgentType`, `actionPrompt: String`, `isEnabled: Boolean`, `lastRunTime: Long?`, `nextRunTime: Long?`.
5. **`TelemetryLog`**:
   - `id: String`, `timestamp: Long`, `provider: String`, `model: String`, `latencyMs: Long`, `tokensUsed: Int`, `agentName: String`, `toolName: String?`, `status: String`.

---

## 5. Step-by-Step Implementation Strategy

1. **Project Metadata & Gradle Configuration**:
   - Set app name to "AURA" in `metadata.json` and `res/values/strings.xml`.
   - Ensure Room and Coroutines dependencies are wired with proper KSP configuration.
2. **AURA Design System & Theme**:
   - Futuristic color tokens in `Color.kt` and `Theme.kt`.
   - Glowing card borders, subtle glassmorphic modifiers, and custom neon buttons.
3. **Local Database & Repository Layer**:
   - Implement `AuraDatabase`, DAOs, and Repositories for Chat, Memory, Skills, Automations, and Telemetry.
   - Seed default skills (Web Research, Code Review, Document Summary, Data Calculator) and default memories.
4. **Model Engine & Tool Execution**:
   - `ModelEngine`: Configurable base URL (Ollama `http://10.0.2.2:11434` or custom), model switcher (`llama3`, `mistral`, `deepseek-coder`, etc.), and live endpoint ping/discovery.
   - `ToolRegistry`: Real implementations for Calculator, Date/Time, File Reader, Code Inspector, and Data Analyzer with approval dialogues.
   - `AgentOrchestrator`: 8 distinct agent personas with prompt framing, tool assignment, and multi-step execution.
5. **Interactive Voice Engine**:
   - Android `SpeechRecognizer` integration with permission launcher.
   - `TextToSpeech` integration with pitch and speech rate configuration.
   - Animated multi-ring holographic Canvas visualizer in Compose.
6. **Workstation User Interfaces**:
   - Adaptive shell with 3-pane layout for tablets/desktop and collapsible drawer for phones.
   - Top status bar with local model latency, endpoint ping indicator, and quick agent switcher.
   - Dedicated views: Home Dashboard, AI Chat, Deep Research Workspace, Agents, Memory Vault, Skills, Automations, and System Monitor.
7. **Verification & Testing**:
   - Run `compile_applet` to ensure pristine compilation and error-free execution.
