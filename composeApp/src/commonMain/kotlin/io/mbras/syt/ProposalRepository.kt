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
            ),
            Proposal(
                id = "10",
                title = "Green Building Standards",
                description = "Mandate passive house standards for new constructions to reduce heating/cooling energy by 90%. All new buildings must meet strict insulation and ventilation requirements.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.OPEN,
                authorId = "climate_architect",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "11",
                title = "District Heating Network",
                description = "Install community-wide geothermal heating system to replace individual gas boilers. This would reduce carbon emissions and heating costs for all residents.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.OPEN,
                authorId = "energy_engineer",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "12",
                title = "Energy Poverty Support Program",
                description = "Subsidized heat pump installations for low-income households to improve energy efficiency and reduce utility bills while fighting climate change.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.OPEN,
                authorId = "social_worker",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "13",
                title = "Car-Free City Center",
                description = "Pedestrianize downtown core with electric shuttle service for accessibility. Create a vibrant, pollution-free zone for businesses and residents.",
                category = ProposalCategory.TRANSPORT,
                status = ProposalStatus.OPEN,
                authorId = "urban_planner",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "14",
                title = "15-Minute Neighborhoods",
                description = "Ensure all essential services are accessible within a 15-minute walk or bike ride from every residence. Reduce car dependency and improve quality of life.",
                category = ProposalCategory.TRANSPORT,
                status = ProposalStatus.OPEN,
                authorId = "neighborhood_advocate",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "15",
                title = "Cargo Bike Delivery Hub",
                description = "Replace diesel delivery trucks with electric cargo bikes for last-mile delivery. Create centralized hubs for efficient, clean urban logistics.",
                category = ProposalCategory.TRANSPORT,
                status = ProposalStatus.OPEN,
                authorId = "logistics_coordinator",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "16",
                title = "Municipal Food Forest",
                description = "Plant edible forests in parks to provide free food and carbon sequestration. Include fruit trees, nut trees, and edible understory plants.",
                category = ProposalCategory.FOOD,
                status = ProposalStatus.OPEN,
                authorId = "permaculture_designer",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "17",
                title = "School Meal Localization",
                description = "Source 80% of school meals from local organic farms within 50km. Support local agriculture while providing healthy, fresh food to students.",
                category = ProposalCategory.FOOD,
                status = ProposalStatus.OPEN,
                authorId = "school_nutritionist",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "18",
                title = "Community Seed Library",
                description = "Preserve local plant varieties and promote food sovereignty through a community-managed seed sharing system. Include heirloom and native species.",
                category = ProposalCategory.FOOD,
                status = ProposalStatus.APPROVED,
                authorId = "seed_keeper",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "19",
                title = "Local Currency System",
                description = "Create community currency to keep wealth circulating locally. Support local businesses and strengthen neighborhood economic resilience.",
                category = ProposalCategory.COMMUNITY,
                status = ProposalStatus.OPEN,
                authorId = "local_economist",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "20",
                title = "Repair Café Network",
                description = "Monthly workshops teaching repair skills to reduce waste and build resilience. Bring broken items and learn to fix them with expert volunteers.",
                category = ProposalCategory.COMMUNITY,
                status = ProposalStatus.APPROVED,
                authorId = "repair_volunteer",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "21",
                title = "Skill-Sharing Platform",
                description = "Digital marketplace for neighbors to exchange services without money. Trade skills, time, and knowledge to build community connections.",
                category = ProposalCategory.COMMUNITY,
                status = ProposalStatus.OPEN,
                authorId = "community_organizer",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "22",
                title = "Zero Waste Bulk Store",
                description = "Municipal bulk store selling unpackaged goods to eliminate packaging waste. Bring your own containers for grains, spices, and household items.",
                category = ProposalCategory.WASTE,
                status = ProposalStatus.OPEN,
                authorId = "zero_waste_advocate",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "23",
                title = "Community Tool Library",
                description = "Shared tools and equipment to reduce individual consumption. Borrow power tools, gardening equipment, and specialty items from neighborhood hubs.",
                category = ProposalCategory.WASTE,
                status = ProposalStatus.IN_PROGRESS,
                authorId = "tool_librarian",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "24",
                title = "Upcycling Workshop Space",
                description = "Creative reuse center turning waste into valuable products. Provide tools, materials, and instruction for transforming trash into treasures.",
                category = ProposalCategory.WASTE,
                status = ProposalStatus.OPEN,
                authorId = "upcycle_artist",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "25",
                title = "Neighborhood Energy Descent Plan",
                description = "Community-led strategy to reduce energy consumption 50% by 2030. Include home weatherization, behavior change campaigns, and renewable energy adoption.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.OPEN,
                authorId = "transition_coordinator",
                imageUrl = null,
                createdAt = now
            ),
            Proposal(
                id = "26",
                title = "Micro-Hydro Community Project",
                description = "Harness local streams for neighborhood electricity generation. Small-scale renewable energy system owned and operated by the community.",
                category = ProposalCategory.ENERGY,
                status = ProposalStatus.OPEN,
                authorId = "hydro_engineer",
                imageUrl = null,
                createdAt = now
            )
        )
    }
    
}
