package com.shakeyourtown.missions.models

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
enum class MissionTheme {
    MOBILITY,
    RESOURCES,
    FOOD
}

@Serializable
enum class MissionStatus {
    DRAFT,
    PUBLISHED,
    FULL,
    CANCELLED,
    DONE
}

@Serializable
data class Mission @OptIn(ExperimentalUuidApi::class) constructor(
    val id: String = Uuid.random().toString(),
    val title: String,
    val description: String,
    val theme: MissionTheme,
    val dateTime: String,
    val location: String,
    val maxParticipants: Int,
    val currentParticipants: Int = 0,
    val status: MissionStatus = MissionStatus.DRAFT,
    val whatToBring: String = "",
    val organizerId: String,
    val createdAt: String = "",
    val updatedAt: String = "",
    val completionSummary: String = "",
    val actualParticipants: Int = 0
)
