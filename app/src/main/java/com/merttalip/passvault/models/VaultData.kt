package com.merttalip.passvault.models

import kotlinx.serialization.Serializable

@Serializable
data class VaultSettings(
    var autoLockTimeoutSeconds: Int = 60,
    var clearClipboardSeconds: Int = 30,
    var isBiometricEnabled: Boolean = false,
    var hideScreenInAppSwitcher: Boolean = true
)

@Serializable
data class VaultData(
    val version: Int = 1,
    var items: List<VaultItem> = emptyList(),
    var settings: VaultSettings = VaultSettings(),
    var updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class EncryptedVaultEnvelope(
    val version: Int = 1,
    val kdfIterations: Int = 100_000,
    val saltBase64: String,
    val nonceBase64: String,
    val ciphertextBase64: String,
    val tagBase64: String,
    val updatedAt: Long = System.currentTimeMillis()
)
