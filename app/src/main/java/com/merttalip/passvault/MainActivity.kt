package com.merttalip.passvault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.FragmentActivity
import com.merttalip.passvault.security.BiometricHelper
import com.merttalip.passvault.services.VaultLockState
import com.merttalip.passvault.services.VaultStore
import com.merttalip.passvault.ui.screens.LockScreen
import com.merttalip.passvault.ui.screens.MainScreen
import com.merttalip.passvault.ui.theme.PassVaultTheme

class MainActivity : FragmentActivity() {
    private lateinit var vaultStore: VaultStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Güvenlik Katmanı: Ekran görüntüsü almayı engelle ve Son Uygulamalar ekranında gizle
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        vaultStore = VaultStore(this)

        setContent {
            PassVaultTheme {
                val lockState by vaultStore.lockState.collectAsState()

                if (lockState == VaultLockState.UNLOCKED) {
                    MainScreen(vaultStore = vaultStore)
                } else {
                    LockScreen(
                        vaultStore = vaultStore,
                        lockState = lockState,
                        onTriggerBiometrics = {
                            triggerBiometrics()
                        }
                    )
                }
            }
        }
    }

    private fun triggerBiometrics() {
        if (BiometricHelper.isBiometricAvailable(this)) {
            BiometricHelper.showBiometricPrompt(
                activity = this,
                onSuccess = {
                    // Parmak izi başarılı - varsayılan olarak kilidi aç
                },
                onError = {
                    // Hata mesajı
                }
            )
        }
    }

    override fun onPause() {
        super.onPause()
        // Kullanıcı uygulamadan çıktığında otomatik kilitleme
        if (vaultStore.settings.value.autoLockTimeoutSeconds == 0) {
            vaultStore.lock()
        }
    }
}
