package io.mbras.syt.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.mbras.syt.model.Proposal
import io.mbras.syt.model.ProposalCategory
import io.mbras.syt.model.ProposalStatus

@Composable
fun ProposalCard(
    proposal: Proposal,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header with title and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = proposal.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                StatusChip(status = proposal.status)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Description
            Text(
                text = proposal.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Footer with category and author
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(category = proposal.category)
                
                proposal.authorId?.let { authorId ->
                    Text(
                        text = "by $authorId",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                } ?: Text(
                    text = "Anonymous",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun StatusChip(status: ProposalStatus) {
    val (backgroundColor, textColor) = when (status) {
        ProposalStatus.OPEN -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        ProposalStatus.APPROVED -> Color(0xFF4CAF50) to Color.White
        ProposalStatus.REJECTED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        ProposalStatus.IN_PROGRESS -> Color(0xFFFF9800) to Color.White
        ProposalStatus.COMPLETED -> Color(0xFF2196F3) to Color.White
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = status.name.replace("_", " "),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun CategoryChip(category: ProposalCategory) {
    val backgroundColor = when (category) {
        ProposalCategory.ENERGY -> Color(0xFFFFEB3B)
        ProposalCategory.WASTE -> Color(0xFF8BC34A)
        ProposalCategory.TRANSPORT -> Color(0xFF03DAC5)
        ProposalCategory.FOOD -> Color(0xFFFF5722)
        ProposalCategory.COMMUNITY -> Color(0xFF9C27B0)
        ProposalCategory.OTHER -> Color(0xFF607D8B)
    }
    
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, backgroundColor)
    ) {
        Text(
            text = category.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = backgroundColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
