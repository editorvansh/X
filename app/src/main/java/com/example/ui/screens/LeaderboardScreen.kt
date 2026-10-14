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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.example.data.model.LeaderboardEntry
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.IconButton
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun LeaderboardScreen(
    leaderboard: List<LeaderboardEntry>,
    language: AppLanguage,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val sorted = leaderboard.sortedBy { it.rank }
    val first = sorted.find { it.rank == 1 }
    val second = sorted.find { it.rank == 2 }
    val third = sorted.find { it.rank == 3 }
    val rest = sorted.filter { it.rank > 3 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (onBack != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextWhite
                        )
                    }
                    Text(
                        text = AppStrings.navLeaderboard(language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Hero Trophy Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, BooyahGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Black)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.img_trophy_cup),
                        contentDescription = "Championship Trophy",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xE6090815), Color(0x99090815), Color.Transparent)
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "SEASON 1 CHAMPIONS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = BooyahGold,
                                fontWeight = FontWeight.Black
                            )
                        )
                        Text(
                            text = "Hall of Fame",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Black
                            )
                        )
                        Text(
                            text = "Rankings updated after each official tournament.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        }

        // Top 3 Podium
        if (first != null || second != null || third != null) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place (Silver)
                    second?.let {
                        PodiumColumn(
                            entry = it,
                            rankText = "2nd",
                            podiumHeight = 90.dp,
                            color = Color(0xFFC0C0C0)
                        )
                    }

                    // 1st Place (Gold)
                    first?.let {
                        PodiumColumn(
                            entry = it,
                            rankText = "1st",
                            podiumHeight = 120.dp,
                            color = BooyahGold
                        )
                    }

                    // 3rd Place (Bronze)
                    third?.let {
                        PodiumColumn(
                            entry = it,
                            rankText = "3rd",
                            podiumHeight = 75.dp,
                            color = Color(0xFFCD7F32)
                        )
                    }
                }
            }
        }

        // Scoring rules note
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, EsportsBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                    Text(
                        text = "Scoring Rule: #1 = 12 Pts, #2 = 9 Pts, #3 = 8 Pts, #4 = 7 Pts, +1 Pt per Kill",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }
        }

        // Rankings table header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "RANK & PLAYER", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    Text(text = "KILLS", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Text(text = "WINS", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                    Text(text = "PTS", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                }
            }
        }

        // Rest of leaderboard
        items(rest, key = { it.id }) { entry ->
            LeaderboardRow(entry = entry)
        }
    }
}

@Composable
private fun PodiumColumn(
    entry: LeaderboardEntry,
    rankText: String,
    podiumHeight: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        // Player Avatar
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E1A3C))
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = entry.playerName.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium.copy(color = color, fontWeight = FontWeight.Bold)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = entry.playerName,
            style = MaterialTheme.typography.labelMedium.copy(
                color = TextWhite,
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1
        )

        Text(
            text = "${entry.totalPoints} pts",
            style = MaterialTheme.typography.labelSmall.copy(color = color)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Podium Block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.1f))
                    )
                )
                .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = rankText,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = color,
                    fontWeight = FontWeight.Black
                )
            )
        }
    }
}

@Composable
private fun LeaderboardRow(entry: LeaderboardEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(EsportsCardBg)
            .border(1.dp, EsportsBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "#${entry.rank}",
                style = MaterialTheme.typography.titleSmall.copy(
                    color = ElectricBlue,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.width(30.dp)
            )

            Column {
                Text(
                    text = entry.playerName,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "UID: ${entry.inGameId}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            Text(
                text = "${entry.kills}",
                style = MaterialTheme.typography.bodyMedium.copy(color = FieryOrange, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${entry.wins}",
                style = MaterialTheme.typography.bodyMedium.copy(color = BooyahGold, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "${entry.totalPoints}",
                style = MaterialTheme.typography.titleSmall.copy(color = SuccessGreen, fontWeight = FontWeight.Black)
            )
        }
    }
}
