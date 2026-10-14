package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.Registration
import com.example.data.model.RoomDetails
import com.example.data.model.Tournament
import com.example.ui.components.RoomDetailsCard
import com.example.ui.components.TournamentCard
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun MyMatchesScreen(
    tournaments: List<Tournament>,
    registrations: List<Registration>,
    roomDetails: RoomDetails?,
    language: AppLanguage,
    onViewTournament: (Tournament) -> Unit,
    onExploreTournaments: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("All", "Upcoming", "Live", "Completed")

    val registeredTournamentMap = remember(tournaments, registrations) {
        val regMap = registrations.associateBy { it.tournamentId }
        tournaments.filter { regMap.containsKey(it.id) }
    }

    val filteredMatches = remember(registeredTournamentMap, selectedTab) {
        when (selectedTab) {
            1 -> registeredTournamentMap.filter { it.status.equals("upcoming", ignoreCase = true) }
            2 -> registeredTournamentMap.filter { it.status.equals("live", ignoreCase = true) }
            3 -> registeredTournamentMap.filter { it.status.equals("completed", ignoreCase = true) }
            else -> registeredTournamentMap
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = EsportsCardBg,
            contentColor = NeonPurpleBright,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = NeonPurpleBright
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) NeonPurpleBright else TextMuted
                            )
                        )
                    }
                )
            }
        }

        if (filteredMatches.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, EsportsBorder, RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = AppStrings.noRegisteredMatches(language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMuted,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )

                        Button(
                            onClick = onExploreTournaments,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("explore_tournaments_button")
                        ) {
                            Text(
                                text = "Explore & Join Tournaments",
                                style = MaterialTheme.typography.labelLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredMatches, key = { it.id }) { match ->
                    val userReg = registrations.find { it.tournamentId == match.id }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(EsportsCardBg)
                            .border(1.dp, EsportsBorder, RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TournamentCard(
                            tournament = match,
                            isRegistered = true,
                            language = language,
                            onViewDetails = onViewTournament
                        )

                        // Registration Credentials info
                        if (userReg != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFF141328), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Your UID: ${userReg.inGameId} (${userReg.inGameName})",
                                    style = MaterialTheme.typography.bodySmall.copy(color = ElectricBlue)
                                )
                                if (!userReg.teamName.isNullOrBlank()) {
                                    Text(
                                        text = "Team: ${userReg.teamName}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = FieryOrange)
                                    )
                                }
                            }
                        }

                        // Room details reveal if room released
                        RoomDetailsCard(
                            roomDetails = roomDetails,
                            isRoomReleased = match.roomReleased,
                            language = language
                        )
                    }
                }
            }
        }
    }
}
