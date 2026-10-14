package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.Announcement
import com.example.data.model.Registration
import com.example.data.model.Tournament
import com.example.data.model.UserProfile
import com.example.data.model.Wallet
import com.example.ui.components.TournamentCard
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
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

@Composable
fun HomeScreen(
    userProfile: UserProfile?,
    wallet: Wallet? = null,
    tournaments: List<Tournament>,
    registrations: List<Registration>,
    announcements: List<Announcement>,
    language: AppLanguage,
    onViewTournament: (Tournament) -> Unit,
    onNavigateToMyMatches: () -> Unit,
    onNavigateToTournaments: () -> Unit,
    onNavigateToWallet: (() -> Unit)? = null,
    onNavigateToLeaderboard: (() -> Unit)? = null,
    onSeedData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val registeredIds = registrations.map { it.tournamentId }.toSet()
    val featuredTournament = tournaments.maxByOrNull { it.prizePool }
    val liveTournaments = tournaments.filter { it.status.equals("live", ignoreCase = true) }
    val closingSoonTournaments = tournaments.filter {
        it.status.equals("upcoming", ignoreCase = true) && (it.maxSlots - it.filledSlots) in 1..10
    }
    val upcomingTournaments = tournaments.filter { !it.status.equals("completed", ignoreCase = true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Player Greeting & Stats Banner
        item {
            PlayerWelcomeCard(
                userProfile = userProfile,
                wallet = wallet,
                registeredCount = registrations.size,
                language = language,
                onQuickMyMatches = onNavigateToMyMatches,
                onQuickWallet = onNavigateToWallet
            )
        }

        // Announcements Banner
        if (announcements.isNotEmpty()) {
            item {
                AnnouncementsSection(
                    announcements = announcements,
                    language = language
                )
            }
        }

        // Hero Featured Championship Card
        item {
            HeroBannerCard(
                featuredTournament = featuredTournament,
                language = language,
                onViewTournament = onViewTournament
            )
        }

        // Live Matches Ticker (if any)
        if (liveTournaments.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(LiveRed, CircleShape)
                    )
                    Text(
                        text = AppStrings.liveMatches(language).uppercase(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = LiveRed,
                            fontWeight = FontWeight.Black
                        )
                    )
                }
            }

            items(liveTournaments, key = { "live_${it.id}" }) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    isRegistered = registeredIds.contains(tournament.id),
                    language = language,
                    onViewDetails = onViewTournament
                )
            }
        }

        // Registration Closing Soon Carousel (if any)
        if (closingSoonTournaments.isNotEmpty()) {
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.closingSoon(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = FieryOrange,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(closingSoonTournaments, key = { "closing_${it.id}" }) { t ->
                            ClosingSoonCard(
                                tournament = t,
                                language = language,
                                onClick = { onViewTournament(t) }
                            )
                        }
                    }
                }
            }
        }

        // Upcoming Tournaments Header & List
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.upcomingTournaments(language),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = "View All",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToTournaments() }
                        .padding(4.dp)
                )
            }
        }

        if (upcomingTournaments.isEmpty()) {
            item {
                EmptyTournamentsCard(
                    onSeedData = onSeedData,
                    language = language
                )
            }
        } else {
            items(upcomingTournaments.take(5), key = { "upcoming_${it.id}" }) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    isRegistered = registeredIds.contains(tournament.id),
                    language = language,
                    onViewDetails = onViewTournament
                )
            }
        }
    }
}

@Composable
private fun PlayerWelcomeCard(
    userProfile: UserProfile?,
    wallet: Wallet? = null,
    registeredCount: Int,
    language: AppLanguage,
    onQuickMyMatches: () -> Unit,
    onQuickWallet: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, EsportsBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!userProfile?.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = userProfile?.photoUrl,
                            contentDescription = "Avatar",
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, NeonPurpleBright, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NeonPurple.copy(alpha = 0.3f))
                                .border(1.5.dp, NeonPurpleBright, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userProfile?.displayName?.take(1)?.uppercase() ?: "P",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    Column {
                        Text(
                            text = AppStrings.welcomePlayer(userProfile?.displayName ?: "Champion", language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = if (!userProfile?.inGameName.isNullOrBlank()) "IGN: ${userProfile?.inGameName}" else "Setup Free Fire IGN in Profile",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (!userProfile?.inGameName.isNullOrBlank()) ElectricBlue else FieryOrange
                            )
                        )
                    }
                }

                // Quick access actions: Coins & Matches
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (wallet != null && onQuickWallet != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF242244))
                                .border(1.dp, BooyahGold.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { onQuickWallet() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🪙", fontSize = 11.sp)
                                Text(
                                    text = "${wallet.coins}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = BooyahGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Button(
                        onClick = onQuickMyMatches,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF242244)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Matches ($registeredCount)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = NeonPurpleBright,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Player Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF131224), RoundedCornerShape(10.dp))
                    .padding(vertical = 10.dp, horizontal = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(label = "Matches", value = "${userProfile?.matchesPlayed ?: 0}", color = ElectricBlue)
                Text(text = "•", color = Color(0xFF2E2B52))
                StatItem(label = "Booyah", value = "${userProfile?.wins ?: 0}", color = BooyahGold)
                Text(text = "•", color = Color(0xFF2E2B52))
                StatItem(label = "Kills", value = "${userProfile?.kills ?: 0}", color = FieryOrange)
                Text(text = "•", color = Color(0xFF2E2B52))
                StatItem(label = "Points", value = "${userProfile?.totalPoints ?: 0}", color = SuccessGreen)
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                color = color,
                fontWeight = FontWeight.Black
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextMuted,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun HeroBannerCard(
    featuredTournament: Tournament?,
    language: AppLanguage,
    onViewTournament: (Tournament) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, NeonPurpleBright.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .clickable {
                if (featuredTournament != null) onViewTournament(featuredTournament)
            }
            .testTag("hero_banner_card"),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Championship Hero Banner",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xCC090815),
                                Color(0xFA090815)
                            )
                        )
                    )
            )

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(FieryOrange, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = AppStrings.featuredTournament(language).uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        )
                    }

                    if (featuredTournament != null) {
                        Text(
                            text = "${featuredTournament.format} • ${featuredTournament.map}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = featuredTournament?.title ?: "Akshar Championship Series",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Black
                    ),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Prize Pool: ₹${featuredTournament?.prizePool ?: 10000}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = BooyahGold,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppStrings.viewDetails(language),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = NeonPurpleBright,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = NeonPurpleBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementsSection(
    announcements: List<Announcement>,
    language: AppLanguage
) {
    val latest = announcements.firstOrNull() ?: return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1A1635))
            .border(1.dp, NeonPurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = "Announcement",
            tint = FieryOrange,
            modifier = Modifier.size(22.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = latest.title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Text(
                text = latest.content,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    fontSize = 11.sp
                ),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ClosingSoonCard(
    tournament: Tournament,
    language: AppLanguage,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, FieryOrange.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .background(FieryOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${tournament.maxSlots - tournament.filledSlots} slots remaining",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = FieryOrange,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = tournament.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1
            )
            Text(
                text = "${tournament.format} • ₹${tournament.prizePool}",
                style = MaterialTheme.typography.bodySmall.copy(color = BooyahGold)
            )
        }
    }
}

@Composable
private fun EmptyTournamentsCard(
    onSeedData: () -> Unit,
    language: AppLanguage
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, EsportsBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SportsEsports,
                contentDescription = null,
                tint = NeonPurpleBright,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Welcome to Akshar Tournament!",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "No tournaments are listed currently. Click below to load official Free Fire scrims, announcements, and leaderboards.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = TextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )
            Button(
                onClick = onSeedData,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("seed_tournaments_button")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Load Sample Tournaments", color = TextWhite)
            }
        }
    }
}
