package com.damtoy.rewear.ui.addclothing

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import android.widget.Toast
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collectLatest
import coil.compose.AsyncImage
import com.damtoy.rewear.model.ClothingCategory
import com.damtoy.rewear.model.FabricType
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddClothingScreen(
    viewModel: AddClothingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToFabricGuide: () -> Unit = {}
) {
    val context = LocalContext.current
    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid ?: "" }
    val aiState by viewModel.aiState.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted -> hasCameraPermission = granted }
    )

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCameraPreview by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                capturedImageUri = uri
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambahkan Pakaian", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (showCameraPreview) {
                            showCameraPreview = false
                        } else {
                            onNavigateBack()
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (capturedImageUri != null) {
                ClothingDetailsForm(
                    imageUri = capturedImageUri!!,
                    onSave = { name, category, fabric ->
                        val defaultWeight = 0.5
                        viewModel.saveClothingItem(
                            userId = currentUserId,
                            name = name,
                            category = category,
                            fabricType = fabric,
                            weightKg = defaultWeight,
                            imageUri = capturedImageUri.toString()
                        ) {
                            onNavigateBack()
                        }
                    },
                    onRetake = {
                        capturedImageUri = null
                        showCameraPreview = false
                        viewModel.resetAiState()
                    },
                    onNavigateToFabricGuide = onNavigateToFabricGuide,
                    aiState = aiState,
                    onAnalyze = { uri -> viewModel.autoDetectFromUri(uri) }
                )
            } else if (showCameraPreview) {
                if (hasCameraPermission) {
                    CameraPreviewView(
                        onImageCaptured = { uri -> 
                            capturedImageUri = uri
                            showCameraPreview = false 
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Camera permission is required to take photo.")
                    }
                }
            } else {
                // Initial selection screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Checkroom,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = { showCameraPreview = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Ambil dari Kamera")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { galleryLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pilih dari Galeri")
                    }
                }
            }
        }
    }
}

@Composable
fun CameraPreviewView(onImageCaptured: (Uri) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            }
        )

        Button(
            onClick = {
                val photoFile = File(context.cacheDir, "clothing_${System.currentTimeMillis()}.jpg")
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                cameraController.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                            onImageCaptured(Uri.fromFile(photoFile))
                        }
                        override fun onError(exc: ImageCaptureException) {
                            Log.e("CameraX", "Photo capture failed: ${exc.message}", exc)
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Jepret Foto", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClothingDetailsForm(
    imageUri: Uri,
    onSave: (String, ClothingCategory, FabricType) -> Unit,
    onRetake: () -> Unit,
    onNavigateToFabricGuide: () -> Unit = {},
    aiState: GeminiVisionUiState,
    onAnalyze: (Uri) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(ClothingCategory.TOPS) }

    var fabricExpanded by remember { mutableStateOf(false) }
    var selectedFabric by remember { mutableStateOf(FabricType.COTTON) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Captured Item",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(MaterialTheme.shapes.medium)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onRetake) {
                Text(
                    "Ubah Foto",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = {
                    onAnalyze(imageUri)
                },
                enabled = aiState !is GeminiVisionUiState.Analyzing,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                if (aiState is GeminiVisionUiState.Analyzing) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Menganalisis...")
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analisis dengan AI")
                }
            }
        }
        
        val context = LocalContext.current
        LaunchedEffect(aiState) {
            when (aiState) {
                is GeminiVisionUiState.Success -> {
                    val result = aiState.result
                    name = result.name
                    selectedCategory = result.category
                    selectedFabric = result.fabricType
                }
                is GeminiVisionUiState.Error -> {
                    Toast.makeText(context, "Analisis AI gagal: ${aiState.message}", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nama Pakaian") },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = categoryExpanded,
            onExpandedChange = { categoryExpanded = !categoryExpanded }
        ) {
            OutlinedTextField(
                value = selectedCategory.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategori") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            ExposedDropdownMenu(
                expanded = categoryExpanded,
                onDismissRequest = { categoryExpanded = false }
            ) {
                ClothingCategory.values().forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            selectedCategory = category
                            categoryExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Fabric Type Dropdown
        ExposedDropdownMenuBox(
            expanded = fabricExpanded,
            onExpandedChange = { fabricExpanded = !fabricExpanded }
        ) {
            OutlinedTextField(
                value = selectedFabric.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Jenis Bahan") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fabricExpanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth(),
                shape = MaterialTheme.shapes.small,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )
            ExposedDropdownMenu(
                expanded = fabricExpanded,
                onDismissRequest = { fabricExpanded = false }
            ) {
                FabricType.values().forEach { fabric ->
                    DropdownMenuItem(
                        text = { Text(fabric.displayName, style = MaterialTheme.typography.bodyMedium) },
                        onClick = {
                            selectedFabric = fabric
                            fabricExpanded = false
                        }
                    )
                }
            }
        }
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onNavigateToFabricGuide) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bingung pilih bahan?", style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onSave(name, selectedCategory, selectedFabric)
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = name.isNotBlank(),
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Simpan ke Lemari", style = MaterialTheme.typography.labelLarge)
        }
    }
}