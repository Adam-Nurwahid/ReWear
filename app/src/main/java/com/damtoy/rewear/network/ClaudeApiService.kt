package com.damtoy.rewear.network


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class ClaudeApiService {
    // Increased timeout since AI generation can take a few seconds
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // IMPORTANT: Replace this with your actual API key for testing.
    private val apiKey = "YOUR_ANTHROPIC_API_KEY_HERE"

    suspend fun getOutfitSuggestions(prompt: String): String = withContext(Dispatchers.IO) {
        // Construct the JSON payload for Claude 3 Haiku (fastest model)
        val jsonBody = JSONObject().apply {
            put("model", "claude-3-haiku-20240307")
            put("max_tokens", 1000)
            // The system prompt locks Claude into its specific role
            put("system", "You are an expert sustainable fashion stylist. The user will provide a list of clothing items they own. You must suggest exactly 3 different outfit combinations using ONLY the items provided. Format the output clearly with a Title, Description, and an Eco-Reason for each outfit. Use markdown. Do not include any intro or outro text.")

            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }
            put("messages", messages)
        }

        val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("https://api.anthropic.com/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("API Error: ${response.code} ${response.message}")

            val responseBody = response.body?.string() ?: throw IOException("Empty response body")
            val jsonResponse = JSONObject(responseBody)

            // Parse Anthropic's specific JSON response structure
            jsonResponse.getJSONArray("content").getJSONObject(0).getString("text")
        }
    }
}