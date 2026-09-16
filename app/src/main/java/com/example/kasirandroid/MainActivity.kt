package com.example.kasirandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.kasirandroid.ui.screens.HistoryScreen
import com.example.kasirandroid.ui.screens.KasirScreen
import com.example.kasirandroid.ui.screens.ProdukScreen
import com.example.kasirandroid.ui.theme.KasirandroidTheme
import com.example.kasirandroid.ui.viewmodel.*

sealed class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Produk : Screen("produk", "Produk", Icons.Filled.Inventory2, Icons.Outlined.Inventory2)
    object Kasir : Screen("kasir", "Kasir", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale)
    object Riwayat : Screen("riwayat", "Riwayat", Icons.Filled.History, Icons.Outlined.History)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = (application as KasirApplication).repository
        
        val productViewModel: ProductViewModel by viewModels { ProductViewModelFactory(repository) }
        val posViewModel: PosViewModel by viewModels { PosViewModelFactory(repository) }
        val historyViewModel: HistoryViewModel by viewModels { HistoryViewModelFactory(repository) }

        enableEdgeToEdge()
        setContent {
            KasirandroidTheme {
                MainApp(productViewModel, posViewModel, historyViewModel)
            }
        }
    }
}

@Composable
fun MainApp(
    productViewModel: ProductViewModel,
    posViewModel: PosViewModel,
    historyViewModel: HistoryViewModel,
) {
    val navController = rememberNavController()
    val items = listOf(Screen.Produk, Screen.Kasir, Screen.Riwayat)

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) },
                        selected = isSelected,
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
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Produk.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Produk.route) { ProdukScreen(productViewModel) }
            composable(Screen.Kasir.route) { KasirScreen(posViewModel, productViewModel) }
            composable(Screen.Riwayat.route) { HistoryScreen(historyViewModel) }
        }
    }
}
