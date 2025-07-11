package io.mbras.syt.model

data class Proposal(
    val id: String,                 // Unique identifier (UUID)
    val title: String,              // Short title of the idea
    val description: String,        // Detailed description
    val category: ProposalCategory, // Enum: e.g., ENERGY, WASTE, TRANSPORT, etc.
    val authorId: String?,          // Optional: user who submitted (null if anonymous)
    val imageUrl: String?,          // Optional: link to an image or attachment
    val createdAt: Long,            // Timestamp (epoch millis)
    val status: ProposalStatus      // Enum: e.g., OPEN, APPROVED, REJECTED, IN_PROGRESS
)

enum class ProposalCategory {
    ENERGY, WASTE, TRANSPORT, FOOD, COMMUNITY, OTHER
}

enum class ProposalStatus {
    OPEN, APPROVED, REJECTED, IN_PROGRESS, COMPLETED
}
