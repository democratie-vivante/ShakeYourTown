package com.shakeyourtown.missions.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Organizer(
    val id: String = UUID.randomUUID().toString(),
    val username: String,
    val passwordHash: String,
    val name: String,
    val organization: String = "",
    val createdAt: String = "",
    val lastLoginAt: String = ""
)
