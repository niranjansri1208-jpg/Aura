# AURA — Advanced Unified Reasoning Assistant

> **Tagline**: *Your Intelligence. Your Device. Your Control.*  
> Inspired by the architectural principles of [OpenJarvis](https://github.com/open-jarvis/OpenJarvis).

AURA is an autonomous, private, desktop-class personal AI operating system developed natively for Android using Kotlin and Jetpack Compose. Built with a local-first philosophy, AURA connects directly to self-hosted Ollama or OpenAI-compatible model endpoints, and persists all memories, conversation histories, skills, and scheduled automations locally using Android's Room SQLite database.

---

## Key Capabilities & Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          AURA Workstation Shell                             │
│  ┌──────────────────┬───────────────────────────────┬────────────────────┐  │
│  │   Left Sidebar   │         Center Stage          │ Right Intelligence │  │
│  │  - Navigation    │ - Home Dashboard              │ - Active Agent     │  │
│  │  - Workspaces    │ - AI Chat (Streaming)         │ - Model Telemetry  │  │
│  │  - Quick Action  │ - Deep Research Workspace     │ - Tool Pipeline    │  │
│  │  - New Session   │ - Memory / Skills / Sandbox   │ - In-Context Mem   │  │
│  └─────────┬────────┴───────────────┬───────────────┴─────────┬──────────┘  │
└────────────┼────────────────────────┼─────────────────────────┼─────────────┘
             │                        │                         │
             ▼                        ▼                         ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     AURA Multi-Agent & State Layer                          │
│  ┌───────────────────────────────────────────────────────────────────────┐  │
│  │                             AuraViewModel                             │  │
│  │   - Reactive StateFlows      - Agent Routing      - Hardware Mon      │  │
│  └──────────────────────────────────┬────────────────────────────────────┘  │
│                                     │                                       │
│          ┌──────────────────────────┼──────────────────────────┐            │
│          ▼                          ▼                          ▼            │
│  ┌───────────────┐        ┌──────────────────┐        ┌────────────────┐    │
│  │  Multi-Agent  │◄──────►│  Tool Execution  │        │ Speech Engine  │    │
│  │  Orchestrator │        │     Registry     │        │  (STT & TTS)   │    │
│  └───────┬───────┘        └─────────┬────────┘        └────────────────┘    │
└──────────┼──────────────────────────┼───────────────────────────────────────┘
           │                          │
           ▼                          ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                      Data, Persistence & Network Layer                      │
│  ┌──────────────────────────────────────┬────────────────────────────────┐  │
│  │          ModelEngine (Network)       │       Room SQLite Database     │  │
│  │  - Ollama (/api/chat, /tags)         │  - chat_sessions & messages    │  │
│  │  - OpenAI-Compatible (/v1)           │  - aura_memories (vault)       │  │
│  │  - Autonomous Reasoner Fallback      │  - aura_skills & automations   │  │
│  │  - Latency & Token Tracking          │  - aura_telemetry logs         │  │
│  └──────────────────────────────────────┴────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## The 8 Specialized Autonomous Agents

1. **AURA Orchestrator**: Master coordinator that understands high-level intents, decomposes objectives, assigns tools, and coordinates multi-step workflows into unified responses.
2. **Deep Researcher**: Conducts multi-source investigation, cites repositories, compares perspectives, and outputs structured dossiers.
3. **Code Assistant**: Analyzes source code, checks syntax balance, evaluates algorithmic complexity, and refactors architectures.
4. **File Analyst**: Analyzes documents, summaries, and structured records, extracting key metrics and tabular data.
5. **Task Planner**: Decomposes complex objectives into atomic milestones and creates execution blueprints.
6. **Voice Assistant**: Natural conversational interaction with low-latency spoken responses and live audio visualizer feedback.
7. **Monitoring Agent**: Monitors background scheduled tasks, tracks endpoint latencies, and flags system anomalies.
8. **General Assistant**: Everyday reasoning, deep explanations, conceptual knowledge, and conversational assistance.

---

## Workstation User Interface

- **Adaptive 3-Pane Layout**: On desktop and tablet screens, displays a 3-column workstation:
  - **Left Sidebar**: Fast switching across Home, Chat, Research, Agents, Memory, Skills, Tools, Automations, Monitor, and Settings.
  - **Center Stage**: High-density workspace rendering rich markdown, code blocks with 1-tap copy, tool invocation callouts, and streaming text.
  - **Right Intelligence Panel**: Live telemetry, current active agent badge, endpoint status, tool invocation stream, and memory references.
- **Mobile Responsiveness**: Automatically collapses the sidebar into a Material 3 navigation drawer and the intelligence panel into a slide-over sheet.
- **Visual Aesthetic**: Deep Midnight Navy (`#060B14`), Slate Void (`#0B1220`), Electric Cyan (`#00F2FE`), Neon Blue (`#4FACFE`), and glowing glassmorphism borders.

---

## Local AI Model Connectivity

AURA is built for privacy and user control:

1. **Ollama Local**: Connects to `http://10.0.2.2:11434` (Android emulator to host machine) or custom LAN IP (e.g. `http://192.168.1.50:11434`). Queries `/api/tags` for installed models (`llama3:8b`, `mistral`, `deepseek-coder`, `qwen2.5`) and streams responses via `/api/chat`.
2. **OpenAI-Compatible Local**: Connects to local vLLM, LM Studio, or LocalAI servers (`/v1/chat/completions`).
3. **Autonomous Reasoner (Local Fallback)**: Built-in offline reasoning engine with realistic token streaming and latency simulation when external model servers are starting or unreachable.

---

## Setup & Running

1. **Start Ollama on Host Machine (Optional)**:
   ```bash
   ollama run llama3:8b
   ```
2. **Run in Android Studio / AI Studio Build**:
   - The app automatically launches with default parameters and pre-populated memories, skills, and automations.
   - Go to **Settings** to customize the endpoint URL or model name.
