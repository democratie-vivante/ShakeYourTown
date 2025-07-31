package io.mbras.syt

import io.mbras.syt.model.Proposal
import io.mbras.syt.model.ProposalCategory
import io.mbras.syt.model.ProposalStatus
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class ProposalRepository {
    
    private val proposals = mutableListOf<Proposal>().apply {
        addAll(createSampleProposals())
    }

    fun findAll(): List<Proposal> {
        return proposals.toList()
    }
    
    private fun createSampleProposals(): List<Proposal> {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        
        return listOf(
            Proposal(
                id = "1",
                title = "Solar Panel Installation Program",
                description = "Implement a city-wide solar panel installation program for public buildings to reduce energy costs and carbon footprint. This initiative would include schools, libraries, and municipal offices.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.APPROVED,
                authorId = "user123",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "2",
                title = "Community Composting Initiative",
                description = "Create neighborhood composting centers to reduce organic waste and provide free compost for residents' gardens. Include educational workshops on sustainable waste management.",
                category = ProposalCategory.WASTE,
                status = ProposalStatus.IN_PROGRESS,
                authorId = null,
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "3",
                title = "Bike-Sharing Network Expansion",
                description = "Expand the current bike-sharing program to cover all neighborhoods, with electric bikes and improved docking stations. Include dedicated bike lanes for safer commuting.",
                category = ProposalCategory.TRANSPORT,
                status = ProposalStatus.OPEN,
                authorId = "cyclist_advocate",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "4",
                title = "Local Food Market Support",
                description = "Establish weekly farmers markets in each district to support local agriculture and provide fresh, affordable produce to all residents.",
                category = ProposalCategory.FOOD,
                status = ProposalStatus.OPEN,
                authorId = "farmer_joe",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "5",
                title = "Community Garden Network",
                description = "Convert unused public spaces into community gardens where residents can grow their own vegetables and herbs. Include tool sharing and gardening education programs.",
                category = ProposalCategory.COMMUNITY,
                status = ProposalStatus.APPROVED,
                authorId = null,
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "6",
                title = "Smart Traffic Light System",
                description = "Upgrade traffic lights with smart sensors to optimize traffic flow and reduce waiting times. Include pedestrian priority features during peak walking hours.",
                category = ProposalCategory.TRANSPORT,
                status = ProposalStatus.IN_PROGRESS,
                authorId = "tech_citizen",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "7",
                title = "Plastic-Free Public Events",
                description = "Mandate that all city-sponsored events use only biodegradable or reusable materials. Provide incentives for private events to follow the same guidelines.",
                category = ProposalCategory.WASTE,
                status = ProposalStatus.REJECTED,
                authorId = "green_activist",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "8",
                title = "Neighborhood Watch App",
                description = "Develop a mobile app for residents to report non-emergency issues, organize community events, and stay informed about local news and initiatives.",
                category = ProposalCategory.OTHER,
                status = ProposalStatus.OPEN,
                authorId = null,
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "9",
                title = "Renewable Energy Cooperative",
                description = "Form a citizen-owned renewable energy cooperative to collectively invest in wind and solar projects, reducing energy costs for all participants.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.COMPLETED,
                authorId = "energy_coop",
                imageUrl = null,
                createdAt = now
            )
        )
    }
    
}
