package com.shakeyourtown.missions.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
enum class SignupStatus {
    CONFIRMED,
    CANCELLED
}

@Serializable
data class Signup(
    val id: String = UUID.randomUUID().toString(),
    val missionId: String,
    val participantName: String,
    val contactEmail: String = "",
    val contactPhone: String = "",
    val signedUpAt: String = "",
    val status: SignupStatus = SignupStatus.CONFIRMED
)
