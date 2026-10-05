package com.merttalip.passvault.services

import android.content.Context
import com.merttalip.passvault.models.EncryptedVaultEnvelope
import com.merttalip.passvault.models.VaultCategory
import com.merttalip.passvault.models.VaultData
import com.merttalip.passvault.models.VaultItem
import com.merttalip.passvault.models.VaultSettings
import com.merttalip.passvault.security.CryptoManager
import com.merttalip.passvault.security.PasswordGenerator
import com.merttalip.passvault.security.PasswordGeneratorConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import javax.crypto.SecretKey

enum class VaultLockState {
    UNINITIALIZED,
    LOCKED,
    UNLOCKED
}

class VaultStore(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }
    private val vaultFile = File(context.filesDir, "vault.enc")

    private val _lockState = MutableStateFlow(VaultLockState.LOCKED)
    val lockState: StateFlow<VaultLockState> = _lockState

    private val _items = MutableStateFlow<List<VaultItem>>(emptyList())
    val items: StateFlow<List<VaultItem>> = _items

    private val _settings = MutableStateFlow(VaultSettings())
    val settings: StateFlow<VaultSettings> = _settings

    private var currentKey: SecretKey? = null

    init {
        checkInitialization()
    }

    fun checkInitialization() {
        _lockState.value = if (vaultFile.exists()) VaultLockState.LOCKED else VaultLockState.UNINITIALIZED
    }

    fun setupNewVault(masterPassword: String, enableBiometrics: Boolean) {
        val salt = CryptoManager.secureRandomBytes(32)
        val key = CryptoManager.deriveKey(masterPassword, salt)

        val welcomeItem = VaultItem(
            title = "PassVault Hoş Geldiniz",
            username = "kullanici@ornek.com",
            password = PasswordGenerator.generate(PasswordGeneratorConfig(length = 20)),
            url = "https://android.com",
            notes = "Bu sizin ilk şifrenizdir. Tamamen çevrimdışı ve AES-256-GCM ile korunur.",
            category = VaultCategory.LOGIN,
            isFavorite = true
        )

        val newSettings = VaultSettings(isBiometricEnabled = enableBiometrics)
        val initialData = VaultData(items = listOf(welcomeItem), settings = newSettings)
        val rawJson = json.encodeToString(initialData).toByteArray(Charsets.UTF_8)

        val envelope = CryptoManager.encrypt(rawJson, key, salt)
        val envelopeJson = json.encodeToString(envelope)
        vaultFile.writeText(envelopeJson)

        currentKey = key
        _items.value = initialData.items
        _settings.value = newSettings
        _lockState.value = VaultLockState.UNLOCKED
    }

    fun unlock(masterPassword: String): Boolean {
        if (!vaultFile.exists()) return false

        return try {
            val envelopeJson = vaultFile.readText()
            val envelope = json.decodeFromString<EncryptedVaultEnvelope>(envelopeJson)

            val salt = android.util.Base64.decode(envelope.saltBase64, android.util.Base64.NO_WRAP)
            val key = CryptoManager.deriveKey(masterPassword, salt, envelope.kdfIterations)

            val decryptedBytes = CryptoManager.decrypt(envelope, key)
            val decryptedJson = String(decryptedBytes, Charsets.UTF_8)
            val data = json.decodeFromString<VaultData>(decryptedJson)

            currentKey = key
            _items.value = data.items
            _settings.value = data.settings
            _lockState.value = VaultLockState.UNLOCKED
            true
        } catch (e: Exception) {
            false
        }
    }

    fun lock() {
        currentKey = null
        _items.value = emptyList()
        _lockState.value = VaultLockState.LOCKED
    }

    fun saveVault() {
        val key = currentKey ?: return
        val currentEnvelope = if (vaultFile.exists()) {
            json.decodeFromString<EncryptedVaultEnvelope>(vaultFile.readText())
        } else null

        val salt = if (currentEnvelope != null) {
            android.util.Base64.decode(currentEnvelope.saltBase64, android.util.Base64.NO_WRAP)
        } else {
            CryptoManager.secureRandomBytes(32)
        }

        val data = VaultData(items = _items.value, settings = _settings.value)
        val rawJson = json.encodeToString(data).toByteArray(Charsets.UTF_8)

        val envelope = CryptoManager.encrypt(rawJson, key, salt)
        vaultFile.writeText(json.encodeToString(envelope))
    }

    fun addItem(item: VaultItem) {
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        _items.value = listOf(updated) + _items.value
        saveVault()
    }

    fun updateItem(item: VaultItem) {
        val updated = item.copy(updatedAt = System.currentTimeMillis())
        _items.value = _items.value.map { if (it.id == item.id) updated else it }
        saveVault()
    }

    fun deleteItem(id: String) {
        _items.value = _items.value.filter { it.id != id }
        saveVault()
    }

    fun toggleFavorite(item: VaultItem) {
        val updated = item.copy(isFavorite = !item.isFavorite)
        updateItem(updated)
    }
}
