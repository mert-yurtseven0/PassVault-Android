package com.merttalip.passvault.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merttalip.passvault.security.PasswordGenerator
import com.merttalip.passvault.services.VaultLockState
import com.merttalip.passvault.services.VaultStore

@Composable
fun LockScreen(
    vaultStore: VaultStore,
    lockState: VaultLockState,
    onTriggerBiometrics: () -> Unit
) {
    val isSetup = lockState == VaultLockState.UNINITIALIZED
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var enableBiometrics by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "PassVault",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = if (isSetup) "Çevrimdışı ve AES-256 ile korunan yeni kasanızı kurun" else "Kasa Kilitli. Giriş yapmak için ana şifrenizi girin.",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMessage = null },
                label = { Text("Ana Şifre") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            if (isSetup) {
                if (password.isNotEmpty()) {
                    val strength = PasswordGenerator.evaluateStrength(password)
                    LinearProgressIndicator(
                        progress = strength.progress,
                        color = strength.color,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = null },
                    label = { Text("Ana Şifreyi Onaylayın") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(checked = enableBiometrics, onCheckedChange = { enableBiometrics = it })
                    Text("Parmak İzi / Biyometrik Kilit Açma", fontSize = 14.sp)
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (isSetup) {
                        if (password != confirmPassword) {
                            errorMessage = "Şifreler eşleşmiyor!"
                        } else if (password.length < 6) {
                            errorMessage = "Şifre en az 6 karakter olmalı!"
                        } else {
                            vaultStore.setupNewVault(password, enableBiometrics)
                        }
                    } else {
                        val success = vaultStore.unlock(password)
                        if (!success) {
                            errorMessage = "Hatalı şifre!"
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(if (isSetup) Icons.Default.LockOpen else Icons.Default.LockOpen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (isSetup) "Kasamı Oluştur" else "Kilidi Aç", fontWeight = FontWeight.Bold)
            }

            if (!isSetup) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(onClick = onTriggerBiometrics) {
                    Icon(Icons.Default.Fingerprint, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Parmak İzi ile Aç")
                }
            }
        }
    }
}
