package com.damtoy.rewear.ui.guide

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class FabricGuideData(
    val name: String,
    val origin: String,
    val characteristics: String,
    val care: String
)

val fabricGuides = listOf(
    FabricGuideData(
        name = "Katun (Cotton)",
        origin = "Serat alami dari tanaman kapas.",
        characteristics = "Terasa lembut di kulit, sangat mudah menyerap keringat (breathable), namun rentan kusut dan bisa menyusut setelah pencucian pertama.",
        care = "Aman dicuci dengan mesin menggunakan air dingin/hangat. Hindari menjemur di bawah terik matahari langsung agar warna tidak pudar. Aman disetrika dengan suhu panas tinggi."
    ),
    FabricGuideData(
        name = "Poliester (Polyester)",
        origin = "Serat sintetis buatan (plastik).",
        characteristics = "Sangat kuat, ringan, cepat kering, tidak mudah kusut, namun kurang menyerap keringat sehingga terasa panas jika dipakai di luar ruangan.",
        care = "Cuci dengan air dingin. Gunakan setrika bersuhu SANGAT RENDAH agar kain tidak meleleh atau mengkilap."
    ),
    FabricGuideData(
        name = "Denim (Jeans)",
        origin = "Tenunan katun kepar (twill) yang tebal.",
        characteristics = "Tebal, berat, kaku di awal, namun sangat awet dan tahan gesekan.",
        care = "Jangan terlalu sering dicuci. Jika harus dicuci, balik pakaian (bagian dalam di luar) dan gunakan air dingin. Jemur di tempat teduh dan setrika bagian dalamnya."
    ),
    FabricGuideData(
        name = "Sutra (Silk)",
        origin = "Serat alami dari kepompong ulat sutra.",
        characteristics = "Sangat halus, berkilau, mewah, dan menyejukkan. Sangat rapuh jika dalam kondisi basah.",
        care = "Cuci dengan tangan menggunakan air dingin & deterjen cair berbahan lembut. Jangan diperas keras. Jemur mendatar di tempat teduh. Setrika suhu rendah berlapis kain."
    ),
    FabricGuideData(
        name = "Wol (Wool)",
        origin = "Bulu hewan (domba, alpaca, kambing).",
        characteristics = "Penahan panas yang sangat baik, agak elastis, dan tidak mudah kusut.",
        care = "Direkomendasikan cuci kering (Dry Clean). JANGAN masukkan ke mesin cuci air panas (akan menyusut permanen). Jemur secara mendatar, jangan digantung."
    ),
    FabricGuideData(
        name = "Linen",
        origin = "Serat alami tanaman rami.",
        characteristics = "Sangat kuat, sejuk dipakai, cepat menyerap lembap, namun SANGAT mudah kusut (ini justru nilai estetikanya).",
        care = "Cuci tangan/mesin mode lembut. Jangan gunakan mesin pengering panas. Setrika suhu tinggi dengan banyak uap air saat agak lembap."
    ),
    FabricGuideData(
        name = "Rayon / Viscose",
        origin = "Serat semi-sintetis (regenerasi selulosa/kayu).",
        characteristics = "Drape/jatuh yang indah menyerupai sutra, sangat sejuk, namun rapuh jika basah dan mudah melar.",
        care = "Cuci tangan air dingin. Jangan diperas kuat-kuat. Jemur datar (jangan digantung) agar pakaian tidak memanjang/melar. Setrika suhu sedang/rendah dari dalam."
    ),
    FabricGuideData(
        name = "Kulit Asli (Leather)",
        origin = "Kulit hewan yang disamak.",
        characteristics = "Mewah, kuat, anti-angin, dan akan menyesuaikan bentuk tubuh pemakai seiring waktu.",
        care = "JANGAN PERNAH dicuci dengan air/mesin cuci. Bersihkan noda dengan kain lembap. Bawa ke spesialis cuci kulit. Gunakan lotion kulit secara berkala agar tidak retak."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FabricGuideScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panduan Perawatan Pakaian", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Kenali bahan pakaianmu!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Setiap bahan pakaian memerlukan perlakuan yang berbeda. Ketuk kartu di bawah ini untuk melihat detail perawatan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(fabricGuides) { guide ->
                FabricGuideCard(guide)
            }
        }
    }
}

@Composable
fun FabricGuideCard(guide: FabricGuideData) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = guide.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Tutup" else "Buka",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Asal",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Terbuat Dari", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                            Text(guide.origin, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info, // Should be something else maybe, but Info is fine
                            contentDescription = "Karakteristik",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Ciri-ciri", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.tertiary)
                            Text(guide.characteristics, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Perawatan",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Cara Perawatan", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(guide.care, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
