package io.mbras.syt.model

data class Vote(
    val id: String,           // Unique identifier (UUID)
    val proposalId: String,   // The proposal this vote is for
    val userId: String?,      // The user who voted (null if anonymous or device-based)
    val value: Int,           // 1 for upvote, -1 for downvote (or just 1 if only upvotes)
    val votedAt: Long         // Timestamp (epoch millis)
)
