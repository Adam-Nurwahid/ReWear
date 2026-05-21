package com.damtoy.rewear.network

import android.graphics.Bitmap
import android.util.Log
import com.damtoy.rewear.BuildConfig
import com.damtoy.rewear.model.ClothingCategory
import com.damtoy.rewear.model.FabricType
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

data class DetectedClothingResult(
    val name: String,
    val category: ClothingCategory,
    val fabricType: FabricType,
    val estimatedWeightKg: Double
)

class GeminiVisionService {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    private val model = GenerativeModel(
        modelName = "gemini-flash-lite-latest",
        apiKey = apiKey
    )

    suspend fun analyzeClothingImage(bitmap: Bitmap): DetectedClothingResult = withContext(Dispatchers.IO) {
        val categoryOptions = ClothingCategory.values().joinToString(", ") { "\"${it.name}\" (${it.displayName})" }
        val fabricOptions = FabricType.values().joinToString(", ") { "\"${it.name}\" (${it.displayName})" }

        val prompt = """
            You are an expert clothing analyst. Analyze this clothing image carefully and identify what you see.
            
            You MUST respond with ONLY a valid JSON object. No markdown. No explanation. No code blocks.
            
            Here are the EXACT values you must choose from:
            
            For "category", pick exactly one of these enum names:
            $categoryOptions
            
            For "fabricType", pick exactly one of these enum names:
            $fabricOptions
            
            Examples of correct mapping:
            - Blue jeans / denim pants -> category: "BOTTOMS", fabricType: "DENIM"
            - White t-shirt / polo shirt -> category: "TOPS", fabricType: "COTTON"
            - Jacket / hoodie / coat -> category: "OUTERWEAR", fabricType: "POLYESTER" or "FLEECE"
            - Dress / skirt gown -> category: "DRESSES", fabricType: "COTTON" or "VISCOSE"
            - Sneakers / sandals / boots -> category: "SHOES", fabricType: "LEATHER" or "NYLON"
            - Bags / belts / hats -> category: "ACCESSORIES", fabricType: "LEATHER" or "BLENDED"
            - Dress pants / trousers -> category: "BOTTOMS", fabricType: "BLENDED"
            - Silk blouse -> category: "TOPS", fabricType: "SILK"
            - Wool sweater / knit top -> category: "TOPS", fabricType: "WOOL"
            
            Respond with this exact JSON structure:
            {"name": "<descriptive name in Indonesian, e.g. Celana Jeans Biru>", "category": "<ENUM_NAME>", "fabricType": "<ENUM_NAME>", "estimatedWeightKg": <number>}
        """.trimIndent()

        try {
            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(prompt)
                }
            )

            val rawText = response.text ?: throw Exception("Empty AI response")
            Log.d("GeminiVision", "Raw AI response: $rawText")

            // Aggressively strip any markdown formatting
            val jsonText = rawText.trim()
                .replace(Regex("^```json\\s*", RegexOption.MULTILINE), "")
                .replace(Regex("^```\\s*", RegexOption.MULTILINE), "")
                .replace(Regex("```\\s*$", RegexOption.MULTILINE), "")
                .trim()

            Log.d("GeminiVision", "Parsed JSON: $jsonText")

            val jsonObject = JSONObject(jsonText)

            val name = jsonObject.optString("name", "").ifBlank { "Pakaian Terdeteksi" }
            val categoryStr = jsonObject.optString("category", "TOPS").trim().uppercase()
            val fabricStr = jsonObject.optString("fabricType", "COTTON").trim().uppercase()
            val weight = jsonObject.optDouble("estimatedWeightKg", 0.3)

            val category = runCatching { ClothingCategory.valueOf(categoryStr) }
                .getOrElse { smartMatchCategory(categoryStr) }
            val fabric = runCatching { FabricType.valueOf(fabricStr) }
                .getOrElse { smartMatchFabric(fabricStr) }

            Log.d("GeminiVision", "Result: name=$name, category=$category, fabric=$fabric")

            DetectedClothingResult(
                name = name,
                category = category,
                fabricType = fabric,
                estimatedWeightKg = weight
            )
        } catch (e: Exception) {
            Log.e("GeminiVision", "AI analysis failed: ${e.message}", e)
            DetectedClothingResult(
                name = "Pakaian Terdeteksi",
                category = ClothingCategory.TOPS,
                fabricType = FabricType.COTTON,
                estimatedWeightKg = 0.3
            )
        }
    }

    private fun smartMatchCategory(str: String): ClothingCategory {
        val lower = str.lowercase()
        return when {
            lower.contains("bottom") || lower.contains("pants") || lower.contains("trouser") 
                || lower.contains("jeans") || lower.contains("skirt") -> ClothingCategory.BOTTOMS
            lower.contains("dress") -> ClothingCategory.DRESSES
            lower.contains("outer") || lower.contains("jacket") || lower.contains("coat") -> ClothingCategory.OUTERWEAR
            lower.contains("shoe") || lower.contains("boot") || lower.contains("sandal") -> ClothingCategory.SHOES
            lower.contains("access") || lower.contains("bag") || lower.contains("hat") -> ClothingCategory.ACCESSORIES
            else -> ClothingCategory.TOPS
        }
    }

    private fun smartMatchFabric(str: String): FabricType {
        val lower = str.lowercase()
        return when {
            lower.contains("denim") || lower.contains("jean") -> FabricType.DENIM
            lower.contains("poly") -> FabricType.POLYESTER
            lower.contains("wool") || lower.contains("knit") -> FabricType.WOOL
            lower.contains("silk") -> FabricType.SILK
            lower.contains("linen") -> FabricType.LINEN
            lower.contains("nylon") -> FabricType.NYLON
            lower.contains("fleece") -> FabricType.FLEECE
            lower.contains("leather") || lower.contains("kulit") -> FabricType.LEATHER
            lower.contains("viscose") || lower.contains("rayon") -> FabricType.VISCOSE
            lower.contains("spandex") || lower.contains("lycra") -> FabricType.SPANDEX
            lower.contains("acrylic") -> FabricType.ACRYLIC
            lower.contains("hemp") || lower.contains("rami") -> FabricType.HEMP
            lower.contains("bamboo") -> FabricType.BAMBOO
            lower.contains("blend") || lower.contains("mixed") -> FabricType.BLENDED
            else -> FabricType.COTTON
        }
    }
}
