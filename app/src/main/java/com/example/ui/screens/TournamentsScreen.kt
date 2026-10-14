package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Registration
import com.example.data.model.Tournament
import com.example.ui.components.TournamentCard
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun TournamentsScreen(
    tournaments: List<Tournament>,
    registrations: List<Registration>,
    selectedFormat: String,
    selectedStatus: String,
    searchQuery: String,
    language: AppLanguage,
    onFormatChange: (String) -> Unit,
    onStatusChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onViewDetails: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    val registeredIds = registrations.map { it.tournamentId }.toSet()
    val formats = listOf("All", "Solo", "Duo", "Squad")
    val statuses = listOf("All", "Upcoming", "Live", "Completed")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tournament_search_input"),
                placeholder = {
                    Text(
                        text = "Search by title, map (Bermuda...), or mode",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ElectricBlue)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedBorderColor = NeonPurpleBright,
                    unfocusedBorderColor = EsportsBorder,
                    focusedContainerColor = EsportsCardBg,
                    unfocusedContainerColor = EsportsCardBg
                )
            )
        }

        // Format Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Game Mode Format",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(formats) { format ->
                        val isSelected = selectedFormat.equals(format, ignoreCase = true)
                        FilterChipBadge(
                            label = format,
                            isSelected = isSelected,
                            selectedColor = NeonPurpleBright,
                            onClick = { onFormatChange(format) }
                        )
                    }
                }
            }
        }

        // Status Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(statuses) { status ->
                    val isSelected = selectedStatus.equals(status, ignoreCase = true)
                    FilterChipBadge(
                        label = status,
                        isSelected = isSelected,
                        selectedColor = ElectricBlue,
                        onClick = { onStatusChange(status) }
                    )
                }
            }
        }

        // Results count
        item {
            Text(
                text = "${tournaments.size} Tournaments Available",
                style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
            )
        }

        if (tournaments.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "No tournaments match your filter",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Try clearing your search query or selecting 'All' formats.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        } else {
            items(tournaments, key = { it.id }) { tournament ->
                TournamentCard(
                    tournament = tournament,
                    isRegistered = registeredIds.contains(tournament.id),
                    language = language,
                    onViewDetails = onViewDetails
                )
            }
        }
    }
}

@Composable
private fun FilterChipBadge(
    label: String,
    isSelected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) selectedColor.copy(alpha = 0.2f) else EsportsCardBg
            )
            .border(
                width = 1.dp,
                color = if (isSelected) selectedColor else EsportsBorder,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = if (isSelected) selectedColor else TextMuted,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        )
    }
}
