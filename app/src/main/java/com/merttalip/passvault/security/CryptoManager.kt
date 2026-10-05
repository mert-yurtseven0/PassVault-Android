package com.merttalip.passvault.security

import android.util.Base64
import com.merttalip.passvault.models.EncryptedVaultEnvelope
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

object CryptoManager {
    private const val KDF_ITERATIONS = 100_000
    private const val KEY_LENGTH = 256
    private const val GCM_TAG_LENGTH = 128
    private const val GCM_IV_LENGTH = 12

    fun secureRandomBytes(size: Int): ByteArray {
        val bytes = ByteArray(size)
        SecureRandom().nextBytes(bytes)
        return bytes
    }

    fun deriveKey(password: String, salt: ByteArray, iterations: Int = KDF_ITERATIONS): SecretKey {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val keyBytes = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    fun encrypt(data: ByteArray, key: SecretKey, salt: ByteArray, iterations: Int = KDF_ITERATIONS): EncryptedVaultEnvelope {
        val iv = secureRandomBytes(GCM_IV_LENGTH)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec)

        val cipherResult = cipher.doFinal(data)
        
        // Java GCM append authentication tag to the end of ciphertext
        val tagLengthBytes = GCM_TAG_LENGTH / 8
        val ciphertextLength = cipherResult.size - tagLengthBytes
        
        val actualCiphertext = cipherResult.copyOfRange(0, ciphertextLength)
        val tag = cipherResult.copyOfRange(ciphertextLength, cipherResult.size)

        return EncryptedVaultEnvelope(
            version = 1,
            kdfIterations = iterations,
            saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP),
            nonceBase64 = Base64.encodeToString(iv, Base64.NO_WRAP),
            ciphertextBase64 = Base64.encodeToString(actualCiphertext, Base64.NO_WRAP),
            tagBase64 = Base64.encodeToString(tag, Base64.NO_WRAP),
            updatedAt = System.currentTimeMillis()
        )
    }

    fun decrypt(envelope: EncryptedVaultEnvelope, key: SecretKey): ByteArray {
        val iv = Base64.decode(envelope.nonceBase64, Base64.NO_WRAP)
        val ciphertext = Base64.decode(envelope.ciphertextBase64, Base64.NO_WRAP)
        val tag = Base64.decode(envelope.tagBase64, Base64.NO_WRAP)

        // Combine ciphertext + tag for Java Cipher
        val combined = ByteArray(ciphertext.size + tag.size)
        System.arraycopy(ciphertext, 0, combined, 0, ciphertext.size)
        System.arraycopy(tag, 0, combined, ciphertext.size, tag.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val gcmSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec)

        return cipher.doFinal(combined)
    }
}
