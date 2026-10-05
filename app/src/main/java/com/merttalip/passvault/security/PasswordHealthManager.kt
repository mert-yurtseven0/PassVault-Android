package com.merttalip.passvault.security

import com.merttalip.passvault.models.VaultCategory
import com.merttalip.passvault.models.VaultItem

data class SecurityAuditReport(
    val overallScore: Int,
    val weakItems: List<VaultItem>,
    val reusedItemGroups: Map<String, List<VaultItem>>,
    val oldItems: List<VaultItem>,
    val missing2FAItems: List<VaultItem>,
    val totalItemsCount: Int
)

object PasswordHealthManager {
    fun analyze(items: List<VaultItem>): SecurityAuditReport {
        if (items.isEmpty()) {
            return SecurityAuditReport(100, emptyList(), emptyMap(), emptyList(), emptyList(), 0)
        }

        val weak = mutableListOf<VaultItem>()
        val passMap = mutableMapOf<String, MutableList<VaultItem>>()
        val old = mutableListOf<VaultItem>()
        val missing2FA = mutableListOf<VaultItem>()

        val ninetyDaysMillis = 90L * 24 * 60 * 60 * 1000
        val thresholdDate = System.currentTimeMillis() - ninetyDaysMillis

        for (item in items) {
            if (item.password.isEmpty()) continue

            val strength = PasswordGenerator.evaluateStrength(item.password)
            if (strength <= PasswordStrength.WEAK) {
                weak.add(item)
            }

            passMap.getOrPut(item.password) { mutableListOf() }.add(item)

            if (item.updatedAt < thresholdDate) {
                old.add(item)
            }

            if (item.category == VaultCategory.LOGIN && !item.hasTOTP) {
                missing2FA.add(item)
            }
        }

        val reusedGroups = passMap.filter { it.value.size > 1 }
        val totalReusedCount = reusedGroups.values.sumOf { it.size }

        var deductions = 0
        deductions += weak.size * 15
        deductions += totalReusedCount * 10
        deductions += old.size * 3

        val calculated = maxOf(10, 100 - (deductions * 100) / maxOf(1, items.size * 20))
        val score = minOf(100, calculated)

        return SecurityAuditReport(
            overallScore = score,
            weakItems = weak,
            reusedItemGroups = reusedGroups,
            oldItems = old,
            missing2FAItems = missing2FA,
            totalItemsCount = items.size
        )
    }
}
