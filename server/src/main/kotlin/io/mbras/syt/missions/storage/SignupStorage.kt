package io.mbras.syt.missions.storage

import com.shakeyourtown.missions.models.Signup
import com.shakeyourtown.missions.models.SignupStatus
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.Instant

class SignupStorage(private val dataDir: File) {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }
    private val file = File(dataDir, "signups.json")

    private var signups: MutableList<Signup> = mutableListOf()

    init {
        load()
    }

    private fun load() {
        if (file.exists()) {
            try {
                val content = file.readText()
                if (content.isNotBlank()) {
                    signups = json.decodeFromString<MutableList<Signup>>(content)
                }
            } catch (e: Exception) {
                System.err.println("WARNING: Failed to load signups from ${file.absolutePath}: ${e.message}. Starting with empty data.")
                signups = mutableListOf()
            }
        }
    }

    private fun save() {
        dataDir.mkdirs()
        file.writeText(json.encodeToString(signups))
    }

    fun getByMission(missionId: String): List<Signup> = signups.filter { it.missionId == missionId && it.status == SignupStatus.CONFIRMED }

    fun getById(id: String): Signup? = signups.find { it.id == id }

    fun existsByMissionAndContact(missionId: String, email: String?, phone: String?): Boolean {
        return signups.any { signup ->
            signup.missionId == missionId &&
            signup.status == SignupStatus.CONFIRMED &&
            (email != null && email.isNotBlank() && signup.contactEmail == email ||
             phone != null && phone.isNotBlank() && signup.contactPhone == phone)
        }
    }

    fun create(signup: Signup): Boolean {
        val newSignup = signup.copy(
            signedUpAt = Instant.now().toString()
        )
        signups.add(newSignup)
        save()
        return true
    }

    fun cancel(id: String): Boolean {
        val index = signups.indexOfFirst { it.id == id }
        return if (index >= 0) {
            signups[index] = signups[index].copy(status = SignupStatus.CANCELLED)
            save()
            true
        } else {
            false
        }
    }

    fun getByMissionForExport(missionId: String): List<Signup> = signups.filter { it.missionId == missionId }
}
