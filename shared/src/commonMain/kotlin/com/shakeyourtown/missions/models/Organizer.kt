package com.shakeyourtown.missions.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class Organizer @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val username: String,
    val passwordHash: String,
    val name: String,
    val organization: String = "",
    val createdAt: String = "",
    val lastLoginAt: String = ""
)
