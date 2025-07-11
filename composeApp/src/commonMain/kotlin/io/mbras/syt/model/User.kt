package io.mbras.syt.model

data class User(
    val id: String,
    val displayName: String,
    val email: String?,
    val avatarUrl: String?,
    val registeredAt: Long
)
