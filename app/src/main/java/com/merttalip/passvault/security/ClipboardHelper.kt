package com.merttalip.passvault.security

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ClipboardHelper {
    private val handler = Handler(Looper.getMainLooper())
    private var clearRunnable: Runnable? = null
    private var lastCopiedText: String? = null

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage

    fun copy(context: Context, text: String, label: String = "Şifre", timeoutSeconds: Int = 30) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        lastCopiedText = text

        _bannerMessage.value = "$label panoya kopyalandı (${timeoutSeconds}s sonra silinecek)"

        clearRunnable?.let { handler.removeCallbacks(it) }

        if (timeoutSeconds > 0) {
            clearRunnable = Runnable {
                val currentClip = clipboard.primaryClip?.getItemAt(0)?.text?.toString()
                if (currentClip == lastCopiedText) {
                    clipboard.setPrimaryClip(ClipData.newPlainText("", ""))
                    _bannerMessage.value = "Pano temizlendi"
                    handler.postDelayed({ _bannerMessage.value = null }, 2000)
                }
            }
            handler.postDelayed(clearRunnable!!, timeoutSeconds * 1000L)
        } else {
            handler.postDelayed({ _bannerMessage.value = null }, 2500)
        }
    }
}
