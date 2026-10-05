package com.merttalip.passvault.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class VaultCategory(val title: String) {
    LOGIN("Giriş"),
    CARD("Kart"),
    SECURE_NOTE("Güvenli Not"),
    IDENTITY("Kimlik"),
    SERVER("Sunucu / SSH")
}

@Serializable
data class VaultItem(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var username: String = "",
    var password: String = "",
    var url: String = "",
    var notes: String = "",
    var category: VaultCategory = VaultCategory.LOGIN,
    var tags: List<String> = emptyList(),
    var totpSecret: String? = null,
    var isFavorite: Boolean = false,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis(),
    var cardNumber: String? = null,
    var cardExpiry: String? = null,
    var cardCVV: String? = null,
    var cardHolder: String? = null
) {
    val hasTOTP: Boolean
        get() = !totpSecret.isNullOrBlank()
}
