package com.example.zoosmartcare.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.example.zoosmartcare.AlertList
import com.example.zoosmartcare.AnimalList
import com.example.zoosmartcare.Dashboard
import com.example.zoosmartcare.EnclosureList

sealed class BottomNavItem(val key: NavKey, val title: String, val icon: ImageVector) {
    object DashboardTab : BottomNavItem(Dashboard, "Dashboard", Icons.Default.Dashboard)
    object EnclosuresTab : BottomNavItem(EnclosureList, "Enclosures", Icons.Default.Place)
    object AnimalsTab : BottomNavItem(AnimalList, "Animals", Icons.Default.Pets)
    object AlertsTab : BottomNavItem(AlertList, "Alerts", Icons.Default.Notifications)
}

@Composable
fun ZooBottomNavBar(
    currentKey: NavKey,
    onTabSelected: (NavKey) -> Unit
) {
    val items = listOf(
        BottomNavItem.DashboardTab,
        BottomNavItem.EnclosuresTab,
        BottomNavItem.AnimalsTab,
        BottomNavItem.AlertsTab
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        items.forEach { item ->
            val isSelected = currentKey::class == item.key::class
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        onTabSelected(item.key)
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
