package com.damtoy.rewear.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.damtoy.rewear.ReWearApp
import com.damtoy.rewear.ui.addclothing.AddClothingScreen
import com.damtoy.rewear.ui.addclothing.AddClothingViewModel
import com.damtoy.rewear.ui.addclothing.AddClothingViewModelFactory
import com.damtoy.rewear.ui.auth.AuthScreen
import com.damtoy.rewear.ui.auth.AuthViewModel
import com.damtoy.rewear.ui.auth.AuthViewModelFactory
import com.damtoy.rewear.ui.dashboard.DashboardScreen
import com.damtoy.rewear.ui.dashboard.DashboardViewModel
import com.damtoy.rewear.ui.dashboard.DashboardViewModelFactory
import com.damtoy.rewear.ui.detail.ClothingDetailScreen
import com.damtoy.rewear.ui.detail.ClothingDetailViewModel
import com.damtoy.rewear.ui.detail.ClothingDetailViewModelFactory
import com.damtoy.rewear.ui.donate.DonateScreen
import com.damtoy.rewear.ui.donate.DonateViewModel
import com.damtoy.rewear.ui.donate.DonateViewModelFactory
import com.damtoy.rewear.ui.navigation.Screen
import com.damtoy.rewear.ui.outfit.OutfitScreen
import com.damtoy.rewear.ui.outfit.OutfitViewModel
import com.damtoy.rewear.ui.outfit.OutfitViewModelFactory
import com.damtoy.rewear.ui.profile.ProfileScreen
import com.damtoy.rewear.ui.profile.ProfileViewModel
import com.damtoy.rewear.ui.profile.ProfileViewModelFactory
import com.damtoy.rewear.ui.swap.SwapScreen
import com.damtoy.rewear.ui.swap.SwapViewModel
import com.damtoy.rewear.ui.swap.SwapViewModelFactory
import com.damtoy.rewear.ui.wardrobe.WardrobeScreen
import com.damtoy.rewear.ui.wardrobe.WardrobeViewModel
import com.damtoy.rewear.ui.wardrobe.WardrobeViewModelFactory
import com.damtoy.rewear.ui.splash.SplashScreen
import com.damtoy.rewear.ui.guide.FabricGuideScreen
import com.google.firebase.auth.FirebaseAuth

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReWearAppContent() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Get the current user ID dynamically
    var currentUserId by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid ?: "") }
    
    DisposableEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUserId = firebaseAuth.currentUser?.uid ?: ""
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }

    val bottomBarScreens = listOf(
        Screen.Dashboard,
        Screen.Wardrobe,
        Screen.Outfit,
        Screen.Swap,
        Screen.Profile
    )

    val showBottomBar = bottomBarScreens.any { it.route == currentDestination?.route }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    bottomBarScreens.forEach { screen ->
                        val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                        NavigationBarItem(
                            icon = { Icon(screen.icon!!, contentDescription = screen.title) },
                            label = { Text(screen.title, style = MaterialTheme.typography.labelMedium) },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onSplashFinished = {
                        val nextScreen = if (currentUserId.isEmpty()) Screen.Auth.route else Screen.Dashboard.route
                        navController.navigate(nextScreen) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Auth.route) {
                val authViewModel: AuthViewModel = viewModel(
                    factory = AuthViewModelFactory()
                )
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Dashboard.route) {
                val context = LocalContext.current
                val app = context.applicationContext as ReWearApp

                val dashboardViewModel: DashboardViewModel = viewModel(
                    // FIXED: Used app.repository to match the rest of your code
                    factory = DashboardViewModelFactory(app.repository, currentUserId)
                )

                DashboardScreen(viewModel = dashboardViewModel)
            }
            composable(Screen.Wardrobe.route) {
                val context = LocalContext.current
                val app = context.applicationContext as ReWearApp

                // FIXED: Passed currentUserId
                val wardrobeViewModel: WardrobeViewModel = viewModel(
                    factory = WardrobeViewModelFactory(app.repository, currentUserId)
                )

                WardrobeScreen(
                    viewModel = wardrobeViewModel,
                    onNavigateToAdd = { navController.navigate(Screen.AddClothing.route) },
                    onNavigateToDetail = { itemId -> navController.navigate(Screen.Detail.createRoute(itemId)) }
                )
            }
            composable(Screen.Outfit.route) {
                val context = LocalContext.current
                val app = context.applicationContext as ReWearApp

                val outfitViewModel: OutfitViewModel = viewModel(
                    factory = OutfitViewModelFactory(app.repository, currentUserId, context)
                )

                OutfitScreen(viewModel = outfitViewModel)
            }
            composable(Screen.Swap.route) {
                val context = LocalContext.current
                val app = context.applicationContext as ReWearApp

                val swapViewModel: SwapViewModel = viewModel(
                    factory = SwapViewModelFactory(app.repository, currentUserId)
                )
                val donateViewModel: DonateViewModel = viewModel(
                    factory = DonateViewModelFactory(app.repository, currentUserId)
                )

                SwapScreen(swapViewModel = swapViewModel, donateViewModel = donateViewModel)
            }
            composable(Screen.AddClothing.route) {
                val context = LocalContext.current
                val app = context.applicationContext as ReWearApp

                // FIXED: Pass context to save image locally
                val addClothingViewModel: AddClothingViewModel = viewModel(
                    factory = AddClothingViewModelFactory(app.repository, context)
                )

                AddClothingScreen(
                    viewModel = addClothingViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToFabricGuide = { navController.navigate(Screen.FabricGuide.route) }
                )
            }
            composable(Screen.Profile.route) {
                val profileViewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModelFactory()
                )
                ProfileScreen(
                    viewModel = profileViewModel,
                    onLogoutSuccess = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToFabricGuide = {
                        navController.navigate(Screen.FabricGuide.route)
                    }
                )
            }
            composable(Screen.FabricGuide.route) {
                FabricGuideScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                // FIXED: Removed "private val userId: Stringz" syntax error
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: return@composable

                val context = LocalContext.current
                val app = context.applicationContext as ReWearApp

                val detailViewModel: ClothingDetailViewModel = viewModel(
                    // FIXED: Passed currentUserId instead of the typo variable
                    factory = ClothingDetailViewModelFactory(app.repository, itemId, currentUserId)
                )

                ClothingDetailScreen(
                    viewModel = detailViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}