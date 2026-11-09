package com.example.data.network

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OllamaTagResponse(
    val models: List<OllamaModelItem>? = null
)

@JsonClass(generateAdapter = true)
data class OllamaModelItem(
    val name: String,
    val size: Long? = null,
    val modified_at: String? = null
)

@JsonClass(generateAdapter = true)
data class OllamaChatMessage(
    val role: String,
    val content: String
)

@JsonClass(generateAdapter = true)
data class OllamaChatRequest(
    val model: String,
    val messages: List<OllamaChatMessage>,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class OllamaChatResponse(
    val model: String? = null,
    val message: OllamaChatMessage? = null,
    val done: Boolean? = null,
    val total_duration: Long? = null,
    val eval_count: Int? = null
)

// OpenAI-compatible format (vLLM / LM Studio / LocalAI)
@JsonClass(generateAdapter = true)
data class OpenAiChatRequest(
    val model: String,
    val messages: List<OllamaChatMessage>,
    val temperature: Double = 0.7,
    val stream: Boolean = false
)

@JsonClass(generateAdapter = true)
data class OpenAiChatResponse(
    val id: String? = null,
    val choices: List<OpenAiChoice>? = null,
    val usage: OpenAiUsage? = null
)

@JsonClass(generateAdapter = true)
data class OpenAiChoice(
    val message: OllamaChatMessage? = null
)

@JsonClass(generateAdapter = true)
data class OpenAiUsage(
    val prompt_tokens: Int? = null,
    val completion_tokens: Int? = null,
    val total_tokens: Int? = null
)
