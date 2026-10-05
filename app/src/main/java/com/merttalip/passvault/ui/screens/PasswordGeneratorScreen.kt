package com.merttalip.passvault.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.merttalip.passvault.security.ClipboardHelper
import com.merttalip.passvault.security.PasswordGenerator
import com.merttalip.passvault.security.PasswordGeneratorConfig

@Composable
fun PasswordGeneratorScreen() {
    val context = LocalContext.current
    var length by remember { mutableStateOf(18f) }
    var useUppercase by remember { mutableStateOf(true) }
    var useLowercase by remember { mutableStateOf(true) }
    var useNumbers by remember { mutableStateOf(true) }
    var useSymbols by remember { mutableStateOf(true) }
    var avoidAmbiguous by remember { mutableStateOf(true) }

    var generatedPassword by remember {
        mutableStateOf(
            PasswordGenerator.generate(
                PasswordGeneratorConfig(
                    length = 18,
                    useUppercase = true,
                    useLowercase = true,
                    useNumbers = true,
                    useSymbols = true,
                    avoidAmbiguous = true
                )
            )
        )
    }

    fun regenerate() {
        val config = PasswordGeneratorConfig(
            length = length.toInt(),
            useUppercase = useUppercase,
            useLowercase = useLowercase,
            useNumbers = useNumbers,
            useSymbols = useSymbols,
            avoidAmbiguous = avoidAmbiguous
        )
        generatedPassword = PasswordGenerator.generate(config)
    }

    val strength = PasswordGenerator.evaluateStrength(generatedPassword)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Şifre Üretici", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        // Şifre Kartı
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = generatedPassword,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LinearProgressIndicator(
                    progress = strength.progress,
                    color = strength.color,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = "Güç: ${strength.title}",
                    fontSize = 12.sp,
                    color = strength.color,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            ClipboardHelper.copy(context, generatedPassword, "Üretilen Şifre")
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kopyala")
                    }

                    OutlinedButton(
                        onClick = { regenerate() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Yenile")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Ayarlar
        Text("Uzunluk: ${length.toInt()}", fontWeight = FontWeight.Bold)
        Slider(
            value = length,
            onValueChange = { length = it; regenerate() },
            valueRange = 8f..32f,
            steps = 23
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Büyük Harfler (A-Z)")
            Switch(checked = useUppercase, onCheckedChange = { useUppercase = it; regenerate() })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Küçük Harfler (a-z)")
            Switch(checked = useLowercase, onCheckedChange = { useLowercase = it; regenerate() })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Rakamlar (0-9)")
            Switch(checked = useNumbers, onCheckedChange = { useNumbers = it; regenerate() })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Semboller (!@#$)")
            Switch(checked = useSymbols, onCheckedChange = { useSymbols = it; regenerate() })
        }
    }
}
