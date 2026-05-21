package com.damtoy.rewear.ui.donate


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.damtoy.rewear.model.ClothingItem
import com.damtoy.rewear.model.Yayasan

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonateScreen(viewModel: DonateViewModel) {
    val donationList by viewModel.donationList.collectAsState()
    var selectedItemForDonation by remember { mutableStateOf<ClothingItem?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(24.dp)
    ) {
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

        // Foundation Picker Sheet Context
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