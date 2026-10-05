package com.merttalip.passvault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merttalip.passvault.models.VaultCategory
import com.merttalip.passvault.models.VaultItem
import com.merttalip.passvault.security.ClipboardHelper
import com.merttalip.passvault.security.PasswordGenerator
import com.merttalip.passvault.services.VaultStore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultListScreen(
    vaultStore: VaultStore,
    items: List<VaultItem>
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<VaultCategory?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    val filtered = remember(items, searchQuery, selectedCategory) {
        items.filter { item ->
            val matchQuery = searchQuery.isEmpty() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.username.contains(searchQuery, ignoreCase = true) ||
                    item.url.contains(searchQuery, ignoreCase = true)

            val matchCategory = selectedCategory == null || item.category == selectedCategory
            matchQuery && matchCategory
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ekle")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Arama Çubuğu
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Kasa içinde ara...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )

            // Kategori Çipleri
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("Tümü") }
                    )
                }
                items(VaultCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = if (selectedCategory == cat) null else cat
                        },
                        label = { Text(cat.title) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (filtered.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Shield,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "Henüz kayıt bulunmuyor" else "Aramaya uygun kayıt yok",
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.id }) { item ->
                        VaultItemCard(
                            item = item,
                            onCopyPassword = {
                                ClipboardHelper.copy(context, item.password, "${item.title} Şifresi")
                            },
                            onCopyUsername = {
                                ClipboardHelper.copy(context, item.username, "${item.title} Kullanıcı Adı")
                            },
                            onToggleFavorite = {
                                vaultStore.toggleFavorite(item)
                            },
                            onDelete = {
                                vaultStore.deleteItem(item.id)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddItemDialog(
            onDismiss = { showAddDialog = false },
            onSave = { newItem ->
                vaultStore.addItem(newItem)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun VaultItemCard(
    item: VaultItem,
    onCopyPassword: () -> Unit,
    onCopyUsername: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val strength = PasswordGenerator.evaluateStrength(item.password)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.category) {
                        VaultCategory.LOGIN -> Icons.Default.Key
                        VaultCategory.CARD -> Icons.Default.CreditCard
                        VaultCategory.SECURE_NOTE -> Icons.Default.Notes
                        VaultCategory.IDENTITY -> Icons.Default.Person
                        VaultCategory.SERVER -> Icons.Default.Dns
                    },
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (item.username.isNotEmpty()) item.username else item.category.title,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Hızlı Butonlar
            if (item.username.isNotEmpty()) {
                IconButton(onClick = onCopyUsername) {
                    Icon(Icons.Default.Person, contentDescription = "Kullanıcı Adı", tint = Color.Gray)
                }
            }

            if (item.password.isNotEmpty()) {
                IconButton(onClick = onCopyPassword) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Şifre Kopyala", tint = MaterialTheme.colorScheme.primary)
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = null,
                    tint = if (item.isFavorite) Color(0xFFFFB300) else Color.Gray
                )
            }
        }
    }
}

@Composable
fun AddItemDialog(
    onDismiss: () -> Unit,
    onSave: (VaultItem) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(VaultCategory.LOGIN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yeni Kayıt Ekle", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Başlık (Örn: Google, Banka)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Kullanıcı Adı / E-posta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Şifre") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = {
                        password = PasswordGenerator.generate(com.merttalip.passvault.security.PasswordGeneratorConfig(18))
                    }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Üret")
                    }
                }
                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("Web Sitesi URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(VaultItem(title = title, username = username, password = password, url = url, notes = notes, category = category))
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Kaydet")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}
