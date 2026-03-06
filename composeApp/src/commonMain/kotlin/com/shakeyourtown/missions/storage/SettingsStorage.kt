package com.shakeyourtown.missions.storage

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Settings stored locally on the organizer's device.
 */
@Serializable
data class AppSettings(
    val missionsJsonUrl: String = "",
    val githubOwner: String = "",
    val githubRepo: String = "",
    val githubFilePath: String = "data/missions.json",
    val githubToken: String = "",
    val googleFormUrl: String = "",
    val googleFormEntryName: String = "",
    val googleFormEntryEmail: String = "",
    val googleFormEntryPhone: String = "",
    val googleFormEntryMissionId: String = "",
    val googleFormEntryMissionTitle: String = "",
    val pinHash: String = "",
    val organizerName: String = "Organisateur",
    val organizerOrganization: String = ""
)

object SettingsStorage {
    private const val KEY = "syt_settings"
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun load(): AppSettings {
        val raw = PlatformStorage.getString(KEY) ?: return AppSettings()
        return try {
            json.decodeFromString<AppSettings>(raw)
        } catch (_: Exception) {
            AppSettings()
        }
    }

    fun save(settings: AppSettings) {
        PlatformStorage.putString(KEY, json.encodeToString(settings))
    }

    fun isConfigured(): Boolean {
        val s = load()
        return s.missionsJsonUrl.isNotBlank() || s.githubRepo.isNotBlank()
    }

    fun isPinSet(): Boolean = load().pinHash.isNotBlank()

    fun verifyPin(pin: String): Boolean {
        val stored = load().pinHash
        return stored.isNotBlank() && stored == hashPin(pin)
    }

    fun setPin(pin: String) {
        val settings = load()
        save(settings.copy(pinHash = hashPin(pin)))
    }

    /**
     * Simple hash for a 4-digit PIN.
     * Not cryptographically strong — just to prevent casual access.
     */
    private fun hashPin(pin: String): String {
        var hash = 7L
        for (c in pin) {
            hash = hash * 31L + c.code.toLong()
        }
        return hash.toString(16)
    }
}
