package com.damtoy.rewear.ui.swap

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.ui.donate.DonateViewModel

@Composable
fun SwapScreen(swapViewModel: SwapViewModel, donateViewModel: DonateViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Marketplace", "Donation")

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) },
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            if (selectedTabIndex == 0) {
                SwapMarketplaceContent(swapViewModel)
            } else {
                DonateContent(donateViewModel)
            }
        }
    }
}

@Composable
fun SwapMarketplaceContent(viewModel: SwapViewModel) {
    val swapList by viewModel.swapList.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        Text(
            text = "Swap Marketplace",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tukar atau jual pakaian pre-loved Anda untuk mendukung fashion sirkular yang ramah lingkungan.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        if (swapList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items marked for swap yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(swapList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().aspectRatio(0.65f),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column {
                            Box(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = item.imageUri,
                                    contentDescription = item.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { viewModel.unmarkForSwap(item) },
                                    modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = item.name, fontWeight = FontWeight.Bold, maxLines = 1)
                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = {
                                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_SUBJECT, "Selling my ${item.name} on ReWear")
                                            putExtra(Intent.EXTRA_TEXT, "Check out my pre-loved clothing garment listed on ReWear!\nItem: ${item.name}\nMaterial: ${item.fabricType.displayName}\nCategory: ${item.category.name}\nLet's trade sustainably on Carousell or OLX!")
                                        }
                                        context.startActivity(Intent.createChooser(shareIntent, "Post to Marketplace"))
                                    },
                                    modifier = Modifier.fillMaxWidth().height(36.dp),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("List Item", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateContent(viewModel: DonateViewModel) {
    val donationList by viewModel.donationList.collectAsState()
    var selectedItemForDonation by remember { mutableStateOf<ClothingItem?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(top = 24.dp)) {
        Text(
            text = "Charity Donation",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Kirim pakaian pre-loved Anda ke yayasan pilihan.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )

        if (donationList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No items designated for donation yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(donationList) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.imageUri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(72.dp).clip(RoundedCornerShape(8.dp))
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("Weight: ${item.weightKg} kg", style = MaterialTheme.typography.bodySmall)
                            }
                            Button(onClick = {
                                selectedItemForDonation = item
                                showBottomSheet = true
                            }) {
                                Text("Ship")
                            }
                        }
                    }
                }
            }
        }

        if (showBottomSheet && selectedItemForDonation != null) {
            ModalBottomSheet(onDismissRequest = { showBottomSheet = false }) {
                Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                    Text("Select Target Foundation", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(viewModel.getAvailableFoundations()) { yayasan ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    viewModel.confirmDonationRealized(selectedItemForDonation!!, yayasan.id)
                                    showBottomSheet = false
                                    selectedItemForDonation = null
                                }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(yayasan.name, fontWeight = FontWeight.Bold)
                                    Text("City: ${yayasan.city}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                                    Text(yayasan.address, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}