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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Tournament
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
import com.example.ui.theme.TextDark
import com.example.util.AppLanguage
import com.google.firebase.Timestamp
import java.util.Date

@Composable
fun AdminScreen(
    tournaments: List<Tournament>,
    language: AppLanguage,
    isLoading: Boolean,
    onBack: () -> Unit,
    onCreateTournament: (Tournament) -> Unit,
    onReleaseRoom: (tournamentId: String, roomId: String, pass: String, notes: String) -> Unit,
    onUpdateStatus: (tournamentId: String, status: String, winnerName: String?) -> Unit,
    onCreateAnnouncement: (title: String, content: String, tag: String) -> Unit,
    onPublishLeaderboard: (LeaderboardEntry) -> Unit,
    onSeedSampleData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Create", "Manage Rooms", "Announcements", "Results")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack, modifier = Modifier.testTag("admin_back_button")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextWhite
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = null,
                    tint = BooyahGold,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Admin Tournament Control Panel",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

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
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTab == index) NeonPurpleBright else TextMuted
                            )
                        )
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (selectedTab) {
                0 -> CreateTournamentTab(isLoading = isLoading, onCreateTournament = onCreateTournament, onSeedSampleData = onSeedSampleData)
                1 -> ManageRoomsTab(tournaments = tournaments, isLoading = isLoading, onReleaseRoom = onReleaseRoom, onUpdateStatus = onUpdateStatus)
                2 -> AnnouncementsTab(isLoading = isLoading, onCreateAnnouncement = onCreateAnnouncement)
                3 -> ResultsTab(tournaments = tournaments, isLoading = isLoading, onPublishLeaderboard = onPublishLeaderboard)
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun CreateTournamentTab(
    isLoading: Boolean,
    onCreateTournament: (Tournament) -> Unit,
    onSeedSampleData: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("Squad") }
    var mapName by remember { mutableStateOf("Bermuda") }
    var maxSlots by remember { mutableStateOf("12") }
    var prizePool by remember { mutableStateOf("5000") }
    var firstPrize by remember { mutableStateOf("2500") }
    var secondPrize by remember { mutableStateOf("1500") }
    var thirdPrize by remember { mutableStateOf("700") }
    var perKill by remember { mutableStateOf("30") }
    var entryFee by remember { mutableStateOf("0") }
    var rules by remember { mutableStateOf("Mobile only. No emulators or hacking.") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Create New Tournament",
                style = MaterialTheme.typography.titleMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold)
            )

            AdminTextField(value = title, onValueChange = { title = it }, label = "Tournament Title", placeholder = "e.g. Free Fire Squad Rush")
            AdminTextField(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Brief match summary")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = format, onValueChange = { format = it }, label = "Format (Solo/Duo/Squad)", placeholder = "Squad")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = mapName, onValueChange = { mapName = it }, label = "Map", placeholder = "Bermuda")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = maxSlots, onValueChange = { maxSlots = it }, label = "Max Slots", placeholder = "12")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = entryFee, onValueChange = { entryFee = it }, label = "Entry Fee (₹)", placeholder = "0")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = prizePool, onValueChange = { prizePool = it }, label = "Total Prize (₹)", placeholder = "5000")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = perKill, onValueChange = { perKill = it }, label = "Per Kill Bonus (₹)", placeholder = "30")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = firstPrize, onValueChange = { firstPrize = it }, label = "1st Place Prize (₹)", placeholder = "2500")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = secondPrize, onValueChange = { secondPrize = it }, label = "2nd Place Prize (₹)", placeholder = "1500")
                }
            }

            AdminTextField(value = rules, onValueChange = { rules = it }, label = "Rules & Policies", placeholder = "Rules text")

            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val t = Tournament(
                            id = "t_${System.currentTimeMillis()}",
                            title = title.trim(),
                            description = description.trim(),
                            rules = rules.trim(),
                            gameTitle = "Free Fire MAX",
                            format = format.trim(),
                            map = mapName.trim(),
                            maxSlots = maxSlots.toIntOrNull() ?: 12,
                            filledSlots = 0,
                            entryFee = entryFee.toIntOrNull() ?: 0,
                            prizePool = prizePool.toIntOrNull() ?: 5000,
                            firstPrize = firstPrize.toIntOrNull() ?: 2500,
                            secondPrize = secondPrize.toIntOrNull() ?: 1500,
                            thirdPrize = thirdPrize.toIntOrNull() ?: 700,
                            perKillPrize = perKill.toIntOrNull() ?: 30,
                            status = "upcoming",
                            roomReleased = false,
                            scheduledAt = Timestamp(Date(System.currentTimeMillis() + 86400000L)),
                            registrationEnd = Timestamp(Date(System.currentTimeMillis() + 82800000L))
                        )
                        onCreateTournament(t)
                        title = ""
                        description = ""
                    }
                },
                enabled = !isLoading && title.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_create_tournament_btn")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextWhite)
                } else {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Publish Tournament", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onSeedSampleData,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1D38)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Reset & Populate Sample Free Fire Scrims", color = ElectricBlue)
            }
        }
    }
}

@Composable
private fun ManageRoomsTab(
    tournaments: List<Tournament>,
    isLoading: Boolean,
    onReleaseRoom: (tournamentId: String, roomId: String, pass: String, notes: String) -> Unit,
    onUpdateStatus: (tournamentId: String, status: String, winnerName: String?) -> Unit
) {
    if (tournaments.isEmpty()) {
        Text(text = "No tournaments to manage.", color = TextMuted)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        tournaments.forEach { t ->
            var roomId by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }
            var notes by remember { mutableStateOf("") }

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
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = t.title, style = MaterialTheme.typography.titleMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold))
                            Text(text = "${t.format} • ${t.map} • Status: ${t.status.uppercase()}", style = MaterialTheme.typography.bodySmall.copy(color = ElectricBlue))
                        }

                        Box(
                            modifier = Modifier
                                .background(if (t.roomReleased) SuccessGreen.copy(alpha = 0.2f) else FieryOrange.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (t.roomReleased) "ROOM RELEASED" else "ROOM PENDING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (t.roomReleased) SuccessGreen else FieryOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                    // Release room inputs
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(value = roomId, onValueChange = { roomId = it }, label = "Room ID", placeholder = "e.g. 782910")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            AdminTextField(value = password, onValueChange = { password = it }, label = "Password", placeholder = "e.g. 1234")
                        }
                    }

                    AdminTextField(value = notes, onValueChange = { notes = it }, label = "Instructions", placeholder = "e.g. Slot numbers assigned by team")

                    Button(
                        onClick = {
                            if (roomId.isNotBlank() && password.isNotBlank()) {
                                onReleaseRoom(t.id, roomId, password, notes)
                            }
                        },
                        enabled = !isLoading && roomId.isNotBlank() && password.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Release Room Credentials to Players", color = TextDark, fontWeight = FontWeight.Bold)
                    }

                    // Status change quick buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onUpdateStatus(t.id, "live", null) },
                            colors = ButtonDefaults.buttonColors(containerColor = LiveRed),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Set LIVE", color = TextWhite)
                        }

                        Button(
                            onClick = { onUpdateStatus(t.id, "completed", null) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C2A48)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Mark COMPLETED", color = TextWhite)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnnouncementsTab(
    isLoading: Boolean,
    onCreateAnnouncement: (title: String, content: String, tag: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("Notice") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Publish In-App Announcement", style = MaterialTheme.typography.titleMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold))

            AdminTextField(value = title, onValueChange = { title = it }, label = "Title", placeholder = "e.g. Server Maintenance Notice")
            AdminTextField(value = tag, onValueChange = { tag = it }, label = "Category Tag", placeholder = "e.g. Fair Play / Notice / Update")
            AdminTextField(value = content, onValueChange = { content = it }, label = "Announcement Content", placeholder = "Full text")

            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onCreateAnnouncement(title, content, tag)
                        title = ""
                        content = ""
                    }
                },
                enabled = !isLoading && title.isNotBlank() && content.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = FieryOrange),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = TextWhite, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Publish Announcement", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ResultsTab(
    tournaments: List<Tournament>,
    isLoading: Boolean,
    onPublishLeaderboard: (LeaderboardEntry) -> Unit
) {
    var playerName by remember { mutableStateOf("") }
    var inGameId by remember { mutableStateOf("") }
    var kills by remember { mutableStateOf("0") }
    var wins by remember { mutableStateOf("0") }
    var points by remember { mutableStateOf("0") }
    var rank by remember { mutableStateOf("1") }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Add / Update Leaderboard Ranking", style = MaterialTheme.typography.titleMedium.copy(color = TextWhite, fontWeight = FontWeight.Bold))

            AdminTextField(value = playerName, onValueChange = { playerName = it }, label = "Player IGN", placeholder = "e.g. BooyahKing")
            AdminTextField(value = inGameId, onValueChange = { inGameId = it }, label = "Free Fire UID", placeholder = "e.g. 192839102")

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = rank, onValueChange = { rank = it }, label = "Rank (#)", placeholder = "1")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = points, onValueChange = { points = it }, label = "Total Points", placeholder = "150")
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = kills, onValueChange = { kills = it }, label = "Kills", placeholder = "25")
                }
                Box(modifier = Modifier.weight(1f)) {
                    AdminTextField(value = wins, onValueChange = { wins = it }, label = "Wins", placeholder = "3")
                }
            }

            Button(
                onClick = {
                    if (playerName.isNotBlank()) {
                        val entry = LeaderboardEntry(
                            id = "rank_${rank.trim()}_${System.currentTimeMillis()}",
                            playerName = playerName.trim(),
                            inGameId = inGameId.trim(),
                            matchesPlayed = 5,
                            kills = kills.toIntOrNull() ?: 0,
                            wins = wins.toIntOrNull() ?: 0,
                            totalPoints = points.toIntOrNull() ?: 0,
                            rank = rank.toIntOrNull() ?: 1
                        )
                        onPublishLeaderboard(entry)
                        playerName = ""
                        inGameId = ""
                    }
                },
                enabled = !isLoading && playerName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = BooyahGold),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = TextDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Save Leaderboard Score", color = TextDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AdminTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite,
            focusedBorderColor = NeonPurpleBright,
            unfocusedBorderColor = EsportsBorder,
            focusedLabelColor = NeonPurpleBright,
            unfocusedLabelColor = TextMuted
        )
    )
}
