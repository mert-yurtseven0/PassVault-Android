package com.merttalip.passvault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merttalip.passvault.models.VaultItem
import com.merttalip.passvault.security.PasswordHealthManager

@Composable
fun SecurityAuditScreen(items: List<VaultItem>) {
    val report = remember(items) { PasswordHealthManager.analyze(items) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Güvenlik Raporu", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        // Skor Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (report.overallScore >= 80) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${report.overallScore} / 100",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (report.overallScore >= 80) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Text(
                    text = if (report.overallScore >= 80) "Kasa Güvenliğiniz Yüksek" else "İyileştirilmesi Gereken Alanlar Var",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (report.weakItems.isNotEmpty()) {
            Text("Zayıf Şifreler (${report.weakItems.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                items(report.weakItems) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(item.title, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxWidth().padding(top = 30.dp), contentAlignment = Alignment.Center) {
                Text("Harika! Kasanızda zayıf şifre bulunmuyor.", color = Color.Gray)
            }
        }
    }
}
