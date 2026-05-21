package com.damtoy.rewear.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.damtoy.rewear.BuildConfig
import com.damtoy.rewear.model.ClothingItem
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

data class OutfitSuggestion(
    val title: String,
    val itemsUsed: List<String>,
    val co2SavedKg: Double,
    val styleNote: String
)

class GeminiOutfitService(private val context: Context) {

    private val apiKey = BuildConfig.GEMINI_API_KEY

    // FIXED: Removed the generationConfig block containing responseMimeType to prevent compilation errors
    private val model = GenerativeModel(
        modelName = "gemini-flash-lite-latest",
        apiKey = apiKey
    )

    private fun loadBitmapFromUri(uriString: String): Bitmap? {
        return try {
            val uri = Uri.parse(uriString)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun generateMultimodalOutfits(wardrobe: List<ClothingItem>): List<OutfitSuggestion> = withContext(Dispatchers.IO) {
        val promptBuilder = StringBuilder()
        promptBuilder.append("You are an advanced eco-fashion stylist. Below are the clothes available in the user's wardrobe. ")
        promptBuilder.append("Generate exactly 3 creative, trendy, and stylish outfit combinations using only these items. ")
        promptBuilder.append("Make sure each outfit is a full outfit (e.g. top and bottom, or a dress). ")
        promptBuilder.append("For each outfit, estimate how many kg of CO2 are saved compared to buying a new outfit (give a realistic decimal value between 1.5 and 5.0 kg based on the fabric metrics). ")
        promptBuilder.append("Also, provide a detailed and engaging 'styleNote' explaining why this style is trendy and the positive environmental impact of wearing this specific combination (e.g., 'By choosing this instead of buying new, you saved X liters of water!').\n\n")

        promptBuilder.append("Wardrobe Registry:\n")
        wardrobe.forEachIndexed { index, item ->
            promptBuilder.append("Item #$index: ${item.name} (Category: ${item.category.name}, Fabric: ${item.fabricType.displayName})\n")
        }

        promptBuilder.append("\nReturn your response as a raw JSON array string where each object has exactly these keys: 'title', 'itemsUsed' (a JSON array of strings matching the exact names of the items you chose), 'co2SavedKg' (a double), and 'styleNote'. Do not include markdown tags or surrounding block formatting indicators like ```json.")

        try {
            val response = model.generateContent(
                content {
                    text(promptBuilder.toString())
                    wardrobe.take(10).forEach { item ->
                        loadBitmapFromUri(item.imageUri)?.let { bitmap ->
                            image(bitmap)
                        }
                    }
                }
            )

            val jsonText = response.text?.trim()
                ?.removePrefix("```json")
                ?.removePrefix("```")
                ?.removeSuffix("```")
                ?.trim() ?: throw Exception("Empty response from Gemini")

            val jsonArray = JSONArray(jsonText)
            val suggestions = mutableListOf<OutfitSuggestion>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val itemsList = mutableListOf<String>()
                val itemsArray = obj.getJSONArray("itemsUsed")
                for (j in 0 until itemsArray.length()) {
                    itemsList.add(itemsArray.getString(j))
                }

                suggestions.add(
                    OutfitSuggestion(
                        title = obj.optString("title", "Eco Outfit Combo"),
                        itemsUsed = itemsList,
                        co2SavedKg = obj.optDouble("co2SavedKg", 2.4),
                        styleNote = obj.optString("styleNote", "A great matching sustainable look.")
                    )
                )
            }
            suggestions
        } catch (e: Exception) {
            listOf(
                OutfitSuggestion(
                    title = "Daily Casual Look",
                    itemsUsed = wardrobe.take(2).map { it.name },
                    co2SavedKg = 1.8,
                    styleNote = "A perfect combination using your wardrobe staples."
                )
            )
        }
    }
}