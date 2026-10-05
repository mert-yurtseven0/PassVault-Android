package com.merttalip.passvault.security

import java.nio.ByteBuffer
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.math.pow

object TOTPManager {
    fun decodeBase32(input: String): ByteArray? {
        val cleaned = input.uppercase().replace(" ", "").replace("-", "")
        val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"
        var buffer = 0L
        var bitsLeft = 0
        val result = mutableListOf<Byte>()

        for (char in cleaned) {
            val value = alphabet.indexOf(char)
            if (value == -1) {
                if (char == '=') break
                return null
            }
            buffer = (buffer shl 5) or value.toLong()
            bitsLeft += 5
            if (bitsLeft >= 8) {
                result.add(((buffer shr (bitsLeft - 8)) and 0xFF).toByte())
                bitsLeft -= 8
            }
        }
        return if (result.isEmpty()) null else result.toByteArray()
    }

    fun generateCode(secret: String, timeMillis: Long = System.currentTimeMillis(), periodSeconds: Long = 30, digits: Int = 6): String? {
        val keyBytes = decodeBase32(secret) ?: return null
        val counter = timeMillis / 1000 / periodSeconds
        val data = ByteBuffer.allocate(8).putLong(counter).array()

        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(keyBytes, "HmacSHA1"))
        val hash = mac.doFinal(data)

        val offset = hash[hash.size - 1].toInt() and 0x0F
        val binary = ((hash[offset].toInt() and 0x7F) shl 24) or
                ((hash[offset + 1].toInt() and 0xFF) shl 16) or
                ((hash[offset + 2].toInt() and 0xFF) shl 8) or
                (hash[offset + 3].toInt() and 0xFF)

        val otp = binary % (10.0.pow(digits.toDouble()).toInt())
        return String.format("%0${digits}d", otp)
    }

    fun remainingSeconds(periodSeconds: Long = 30): Int {
        val current = (System.currentTimeMillis() / 1000) % periodSeconds
        return (periodSeconds - current).toInt()
    }

    fun progress(periodSeconds: Long = 30): Float {
        return remainingSeconds(periodSeconds).toFloat() / periodSeconds
    }
}
