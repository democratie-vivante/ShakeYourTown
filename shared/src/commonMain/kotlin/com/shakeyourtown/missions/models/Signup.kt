package com.shakeyourtown.missions.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
enum class SignupStatus {
    CONFIRMED,
    CANCELLED
}

@Serializable
data class Signup @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val missionId: String,
    val participantName: String,
    val contactEmail: String = "",
    val contactPhone: String = "",
    val signedUpAt: String = "",
    val status: SignupStatus = SignupStatus.CONFIRMED
)
