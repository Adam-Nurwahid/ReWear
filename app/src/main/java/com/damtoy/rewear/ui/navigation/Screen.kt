package com.damtoy.rewear.ui.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    // Hidden from Bottom Nav
    object Splash : Screen("splash", "Splash")
    // Bottom Nav Items
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object Wardrobe : Screen("wardrobe", "Wardrobe", Icons.Default.Checkroom)
    object Outfit : Screen("outfit", "AI Outfit", Icons.Default.AutoAwesome)
    object Swap : Screen("swap", "Swap", Icons.Default.SwapHoriz)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)

    // Hidden from Bottom Nav
    object Auth : Screen("auth", "Authentication")
    object AddClothing : Screen("add_clothing", "Add Clothing")
    object FabricGuide : Screen("fabric_guide", "Panduan Bahan")
    object Detail : Screen("detail/{itemId}", "Clothing Detail") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
}