package com.merttalip.passvault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merttalip.passvault.security.ClipboardHelper
import com.merttalip.passvault.services.VaultStore

@Composable
fun MainScreen(vaultStore: VaultStore) {
    var selectedTab by remember { mutableStateOf(0) }
    val items by vaultStore.items.collectAsState()
    val settings by vaultStore.settings.collectAsState()
    val bannerMessage by ClipboardHelper.bannerMessage.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Key, contentDescription = "Kasa") },
                    label = { Text("Kasa") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Casino, contentDescription = "Üretici") },
                    label = { Text("Üretici") }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Güvenlik") },
                    label = { Text("Güvenlik") }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ayarlar") },
                    label = { Text("Ayarlar") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> VaultListScreen(vaultStore = vaultStore, items = items)
                1 -> PasswordGeneratorScreen()
                2 -> SecurityAuditScreen(items = items)
                3 -> SettingsScreen(vaultStore = vaultStore, settings = settings)
            }

            // Pano Bildirim Kapsülü
            if (bannerMessage != null) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(bannerMessage!!, color = Color.White, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
