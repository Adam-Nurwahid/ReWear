package com.damtoy.rewear.ui.addclothing

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.damtoy.rewear.model.ClothingCategory
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.model.FabricType
import com.damtoy.rewear.network.DetectedClothingResult
import com.damtoy.rewear.network.GeminiVisionService
import com.damtoy.rewear.repository.ClothingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.LocalDate
import java.util.UUID
import android.content.Context

sealed class GeminiVisionUiState {
    object Idle : GeminiVisionUiState()
    object Analyzing : GeminiVisionUiState()
    data class Success(val result: DetectedClothingResult) : GeminiVisionUiState()
    data class Error(val message: String) : GeminiVisionUiState()
}

class AddClothingViewModel(
    private val repository: ClothingRepository,
    private val context: Context
) : ViewModel() {

    private val visionService = GeminiVisionService()

    private val _aiState = MutableStateFlow<GeminiVisionUiState>(GeminiVisionUiState.Idle)
    val aiState: StateFlow<GeminiVisionUiState> = _aiState

    fun autoDetectFromUri(uri: Uri) {
        _aiState.value = GeminiVisionUiState.Analyzing
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmap = loadBitmapSafely(uri)
                    ?: throw Exception("Tidak dapat membaca gambar dari URI: $uri")
                val result = visionService.analyzeClothingImage(bitmap)
                _aiState.value = GeminiVisionUiState.Success(result)
            } catch (e: Exception) {
                _aiState.value = GeminiVisionUiState.Error(e.localizedMessage ?: "AI analysis failed")
            }
        }
    }

    private fun loadBitmapSafely(uri: Uri): Bitmap? {
        return try {
            when (uri.scheme) {
                "file" -> {
                    BitmapFactory.decodeFile(uri.path)
                }
                else -> {
                    // Use decodeStream via ContentResolver - universal and safe
                    context.contentResolver.openInputStream(uri)?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    fun autoDetectFabric(bitmap: Bitmap) {
        _aiState.value = GeminiVisionUiState.Analyzing
        viewModelScope.launch {
            try {
                val result = visionService.analyzeClothingImage(bitmap)
                _aiState.value = GeminiVisionUiState.Success(result)
            } catch (e: Exception) {
                _aiState.value = GeminiVisionUiState.Error(e.localizedMessage ?: "AI analysis failed")
            }
        }
    }

    fun resetAiState() {
        _aiState.value = GeminiVisionUiState.Idle
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveClothingItem(
        userId: String,
        name: String,
        category: ClothingCategory,
        fabricType: FabricType,
        weightKg: Double,
        imageUri: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val persistentUri = copyImageToInternalStorage(imageUri) ?: imageUri
            
            val newItem = ClothingItem(
                userId = userId,
                name = name,
                category = category,
                fabricType = fabricType,
                weightKg = weightKg,
                imageUri = persistentUri,
                dateAdded = LocalDate.now()
            )
            repository.addClothing(newItem)
            onSuccess()
        }
    }

    private suspend fun copyImageToInternalStorage(tempUri: String): String? = withContext(Dispatchers.IO) {
        try {
            val uri = Uri.parse(tempUri)
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "rewear_${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(file).toString()
        } catch (e: Exception) {
            null
        }
    }
}

class AddClothingViewModelFactory(
    private val repository: ClothingRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddClothingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return AddClothingViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}