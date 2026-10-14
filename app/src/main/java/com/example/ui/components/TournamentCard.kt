package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TournamentCard(
    tournament: Tournament,
    isRegistered: Boolean,
    language: AppLanguage,
    onViewDetails: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val scheduledTimeStr = tournament.scheduledAt?.toDate()?.let { dateFormat.format(it) } ?: "TBD"
    val slotProgress = if (tournament.maxSlots > 0) {
        (tournament.filledSlots.toFloat() / tournament.maxSlots.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val isFull = tournament.filledSlots >= tournament.maxSlots

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    if (tournament.status == "live") listOf(LiveRed, FieryOrange)
                    else if (isRegistered) listOf(NeonPurpleBright, ElectricBlue)
                    else listOf(EsportsBorder, NeonPurple.copy(alpha = 0.4f))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onViewDetails(tournament) }
            .testTag("tournament_card_${tournament.id}"),
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Status Badge & Game Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = when (tournament.status.lowercase()) {
                                    "live" -> LiveRed.copy(alpha = 0.2f)
                                    "completed" -> Color.Gray.copy(alpha = 0.2f)
                                    else -> NeonPurple.copy(alpha = 0.2f)
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = when (tournament.status.lowercase()) {
                                    "live" -> LiveRed
                                    "completed" -> Color.Gray
                                    else -> NeonPurpleBright
                                },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = tournament.status.uppercase(),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (tournament.status.lowercase()) {
                                    "live" -> LiveRed
                                    "completed" -> Color.LightGray
                                    else -> NeonPurpleBright
                                }
                            )
                        )
                    }

                    if (isRegistered) {
                        Box(
                            modifier = Modifier
                                .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                .border(1.dp, SuccessGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = AppStrings.registeredBadge(language),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen
                                )
                            )
                        }
                    }
                }

                // Format & Map
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Format",
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = tournament.format,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = ElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(text = "•", color = TextMuted)
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Map",
                        tint = FieryOrange,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = tournament.map,
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = FieryOrange,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tournament Title
            Text(
                text = tournament.title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Schedule info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "Schedule",
                    tint = TextMuted,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = scheduledTimeStr,
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Prize Pool & Entry Fee Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141328), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prize Pool
                Column {
                    Text(
                        text = AppStrings.prizePool(language),
                        style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Prize",
                            tint = BooyahGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "₹${tournament.prizePool}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = BooyahGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                // Entry Fee
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = AppStrings.entryFee(language),
                        style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                    )
                    Text(
                        text = if (tournament.entryFee == 0) AppStrings.freeEntry(language) else "${tournament.entryFee} Coins",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = if (tournament.entryFee == 0) SuccessGreen else CyanGlow,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    if (tournament.entryFee > 0) {
                        Text(
                            text = "Ref: ₹${tournament.entryFee}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slot Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${AppStrings.slots(language)}: ${tournament.filledSlots}/${tournament.maxSlots}",
                    style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                )
                Text(
                    text = if (isFull) AppStrings.fullSlots(language) else "${tournament.maxSlots - tournament.filledSlots} left",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (isFull) ErrorRed else ElectricBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { slotProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (slotProgress >= 0.9f) ErrorRed else NeonPurpleBright,
                trackColor = Color(0xFF2A284A)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button
            Button(
                onClick = { onViewDetails(tournament) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("tournament_details_button_${tournament.id}"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRegistered) NeonPurple.copy(alpha = 0.6f) else NeonPurpleBright
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (isRegistered) AppStrings.viewDetails(language) else AppStrings.registerNow(language),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
