package com.damtoy.rewear.ui.donate

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.model.Yayasan
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(viewModel: DonateViewModel) {
    val context = LocalContext.current
    val donationList by viewModel.donationList.collectAsState()
    val userLat by viewModel.userLat.collectAsState()
    val userLng by viewModel.userLng.collectAsState()

    var selectedItemForDonation by remember { mutableStateOf<ClothingItem?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedYayasan by remember { mutableStateOf<Yayasan?>(null) }

    // GPS permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            fetchLastKnownLocation(context, viewModel)
        }
    }

    // Auto-request location on first composition
    LaunchedEffect(Unit) {
        val fineOk = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarseOk = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fineOk || coarseOk) {
            fetchLastKnownLocation(context, viewModel)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Header
        Spacer(modifier = Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Charity Donation",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kirim pakaian pre-loved ke yayasan terdekatmu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // GPS Location badge
        Spacer(modifier = Modifier.height(12.dp))
        AnimatedVisibility(
            visible = userLat != null,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut()
        ) {
            SuggestionChip(
                onClick = { fetchLastKnownLocation(context, viewModel) },
                label = {
                    Text(
                        "📍 Lokasi terdeteksi — yayasan diurutkan dari terdekat",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                icon = { Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }
        AnimatedVisibility(
            visible = userLat == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SuggestionChip(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                label = {
                    Text(
                        "Izinkan lokasi untuk menampilkan yayasan terdekat",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                icon = { Icon(Icons.Outlined.LocationOff, contentDescription = null, modifier = Modifier.size(16.dp)) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clothing list
        if (donationList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Checkroom,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Belum ada pakaian untuk didonasikan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "Tandai pakaian dengan opsi 'Donasi' dari lemari pakaianmu",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(donationList) { item ->
                    DonationItemCard(
                        item = item,
                        onShipClick = {
                            selectedItemForDonation = item
                            showBottomSheet = true
                        }
                    )
                }
            }
        }
    }

    // Foundation picker bottom sheet
    if (showBottomSheet && selectedItemForDonation != null) {
        val sortedFoundations = viewModel.getFoundationsSortedByDistance()
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
                selectedItemForDonation = null
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    "Pilih Yayasan Tujuan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (userLat != null) "Diurutkan dari yang paling dekat denganmu"
                    else "Aktifkan lokasi untuk melihat jarak",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(sortedFoundations) { yayasan ->
                        val distanceKm = if (userLat != null) viewModel.getDistanceKm(yayasan) else null

                        FoundationPickerCard(
                            yayasan = yayasan,
                            distanceKm = distanceKm,
                            onClick = {
                                selectedYayasan = yayasan
                                showBottomSheet = false
                            }
                        )
                    }
                }
            }
        }
    }

    // Foundation detail dialog
    if (selectedYayasan != null && selectedItemForDonation != null) {
        YayasanDetailDialog(
            yayasan = selectedYayasan!!,
            item = selectedItemForDonation!!,
            distanceKm = if (userLat != null) viewModel.getDistanceKm(selectedYayasan!!) else null,
            onDismiss = {
                selectedYayasan = null
                // Return to picker
                showBottomSheet = true
            },
            onConfirmDonation = {
                viewModel.confirmDonationRealized(selectedItemForDonation!!, selectedYayasan!!.id)
                selectedYayasan = null
                selectedItemForDonation = null
                showBottomSheet = false
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────
// Sub-composables
// ─────────────────────────────────────────────────────────────────

@Composable
private fun DonationItemCard(item: ClothingItem, onShipClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUri,
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    "${item.category.name.lowercase().replaceFirstChar { it.uppercase() }} · ${item.weightKg} kg",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = {},
                        label = { Text("🌿 ${String.format("%.1f", item.totalCo2Impact)} kg CO₂", style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onShipClick,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Kirim")
            }
        }
    }
}

@Composable
private fun FoundationPickerCard(
    yayasan: Yayasan,
    distanceKm: Double?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foundation icon placeholder
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.VolunteerActivism,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(yayasan.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                Text(
                    yayasan.city,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (distanceKm != null) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = if (distanceKm < 1.0) "${(distanceKm * 1000).toInt()} m"
                        else "${String.format("%.1f", distanceKm)} km",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Icon(
                    Icons.Filled.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun YayasanDetailDialog(
    yayasan: Yayasan,
    item: ClothingItem,
    distanceKm: Double?,
    onDismiss: () -> Unit,
    onConfirmDonation: () -> Unit
) {
    val context = LocalContext.current

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                // Top row: Icon + Name + Close
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(52.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Filled.VolunteerActivism,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(yayasan.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(yayasan.city, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Tutup")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    yayasan.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info rows
                InfoRow(icon = Icons.Filled.LocationOn, label = "Alamat", value = yayasan.address)
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(icon = Icons.Filled.Phone, label = "Telepon", value = yayasan.phone)
                if (distanceKm != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    InfoRow(
                        icon = Icons.Filled.NearMe,
                        label = "Jarak dari lokasi Anda",
                        value = if (distanceKm < 1.0) "${(distanceKm * 1000).toInt()} meter"
                        else "${String.format("%.1f", distanceKm)} km"
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Accepted categories chips
                Text("Menerima kategori:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(yayasan.acceptedCategories) { cat ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(cat.name.lowercase().replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Item being donated
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Checkroom,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Pakaian yang akan didonasikan:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(item.name, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // WhatsApp button
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(yayasan.waLink))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("WhatsApp", style = MaterialTheme.typography.labelMedium)
                    }

                    // Maps button
                    OutlinedButton(
                        onClick = {
                            val mapsUri = Uri.parse("geo:${yayasan.latitude},${yayasan.longitude}?q=${Uri.encode(yayasan.name)}")
                            val intent = Intent(Intent.ACTION_VIEW, mapsUri)
                            intent.setPackage("com.google.android.apps.maps")
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                // Fallback: open in browser
                                val browserIntent = Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://maps.google.com/?q=${yayasan.latitude},${yayasan.longitude}"))
                                context.startActivity(browserIntent)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Maps", style = MaterialTheme.typography.labelMedium)
                    }
                }

                // Website button (if available)
                if (yayasan.website != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(yayasan.website))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Filled.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kunjungi Website", style = MaterialTheme.typography.labelMedium)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(12.dp))

                // Confirm donation
                Button(
                    onClick = onConfirmDonation,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Konfirmasi Donasi ke ${yayasan.name}", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
        }
    }
}

// ─────────────────────────────────────────────────────────────────
// Helper — fetch last known or current GPS location
// ─────────────────────────────────────────────────────────────────
@SuppressLint("MissingPermission")
private fun fetchLastKnownLocation(context: android.content.Context, viewModel: DonateViewModel) {
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    // Try last known location first (instant, no battery impact)
    fusedClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            viewModel.updateUserLocation(location.latitude, location.longitude)
        } else {
            // Fallback: request a fresh location fix
            val cancellationToken = CancellationTokenSource()
            fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationToken.token)
                .addOnSuccessListener { freshLocation ->
                    if (freshLocation != null) {
                        viewModel.updateUserLocation(freshLocation.latitude, freshLocation.longitude)
                    }
                }
        }
    }
}