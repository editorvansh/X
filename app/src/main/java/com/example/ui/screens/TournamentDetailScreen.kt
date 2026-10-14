package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RoomDetails
import com.example.data.model.Tournament
import com.example.ui.components.RoomDetailsCard
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
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
fun TournamentDetailScreen(
    tournament: Tournament,
    isRegistered: Boolean,
    roomDetails: RoomDetails?,
    language: AppLanguage,
    onBack: () -> Unit,
    onOpenRegisterDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy • hh:mm a", Locale.getDefault())
    val scheduleStr = tournament.scheduledAt?.toDate()?.let { dateFormat.format(it) } ?: "TBD"
    val isFull = tournament.filledSlots >= tournament.maxSlots
    val slotProgress = if (tournament.maxSlots > 0) (tournament.filledSlots.toFloat() / tournament.maxSlots.toFloat()).coerceIn(0f, 1f) else 0f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Top Back Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Text(
                text = AppStrings.tournamentDetails(language),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Map Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, NeonPurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_map_arena),
                    contentDescription = "Map Arena",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0xCC0B0A14), Color(0xF20B0A14))
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .background(NeonPurpleBright, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = tournament.format.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(FieryOrange, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = tournament.map.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = tournament.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }

            // Quick Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        Text(text = scheduleStr, style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = FieryOrange, modifier = Modifier.size(16.dp))
                            Text(text = tournament.gameTitle, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted))
                        }

                        Text(
                            text = if (tournament.entryFee == 0) AppStrings.freeEntry(language) else "Entry: ${tournament.entryFee} Coins (₹${tournament.entryFee} ref)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = if (tournament.entryFee == 0) SuccessGreen else CyanGlow,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    // Slots Bar
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Slots: ${tournament.filledSlots} / ${tournament.maxSlots}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Text(
                            text = "${tournament.maxSlots - tournament.filledSlots} slots left",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isFull) MaterialTheme.colorScheme.error else ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    LinearProgressIndicator(
                        progress = { slotProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonPurpleBright,
                        trackColor = Color(0xFF242240)
                    )
                }
            }

            // Room Credentials Card (visible if registered)
            if (isRegistered) {
                RoomDetailsCard(
                    roomDetails = roomDetails,
                    isRoomReleased = tournament.roomReleased,
                    language = language
                )
            }

            // Prize Distribution Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = BooyahGold, modifier = Modifier.size(20.dp))
                        Text(
                            text = AppStrings.prizeDistribution(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PrizeTier(rank = "1st Place", amount = "₹${tournament.firstPrize}", color = BooyahGold)
                        PrizeTier(rank = "2nd Place", amount = "₹${tournament.secondPrize}", color = Color(0xFFC0C0C0))
                        PrizeTier(rank = "3rd Place", amount = "₹${tournament.thirdPrize}", color = Color(0xFFCD7F32))
                        PrizeTier(rank = "Per Kill", amount = "₹${tournament.perKillPrize}", color = FieryOrange)
                    }
                }
            }

            // Rules & Guidelines Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Gavel, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                        Text(
                            text = AppStrings.matchRules(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = tournament.rules ?: "Default Free Fire competitive guidelines apply.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "• Emulators and PC players are strictly prohibited.\n• Room ID & Password will be released 15 minutes before the match.\n• Players must join with the exact Free Fire UID submitted during registration.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            // Register Action Button
            Button(
                onClick = onOpenRegisterDialog,
                enabled = !isRegistered && !isFull && tournament.status.equals("upcoming", ignoreCase = true),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("detail_register_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRegistered) SuccessGreen else NeonPurpleBright,
                    disabledContainerColor = if (isRegistered) SuccessGreen.copy(alpha = 0.5f) else Color(0xFF2C2A48)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isRegistered) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TextWhite)
                        Text(
                            text = AppStrings.registeredBadge(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else if (isFull) {
                    Text(
                        text = AppStrings.fullSlots(language),
                        style = MaterialTheme.typography.titleMedium.copy(color = TextMuted)
                    )
                } else {
                    Text(
                        text = AppStrings.registerNow(language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PrizeTier(rank: String, amount: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF131224), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(
            text = rank,
            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.labelLarge.copy(
                color = color,
                fontWeight = FontWeight.Black
            )
        )
    }
}
