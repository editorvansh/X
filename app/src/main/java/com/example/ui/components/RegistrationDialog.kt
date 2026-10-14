package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Tournament
import com.example.data.model.UserProfile
import com.example.data.model.Wallet
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun RegistrationDialog(
    tournament: Tournament,
    userProfile: UserProfile?,
    wallet: Wallet?,
    language: AppLanguage,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (inGameId: String, inGameName: String, teamName: String?, teammates: String?) -> Unit
) {
    val isTeamFormat = tournament.format.equals("Duo", ignoreCase = true) || tournament.format.equals("Squad", ignoreCase = true)
    val userCoins = wallet?.coins ?: 100
    val entryFee = tournament.entryFee
    val hasSufficientBalance = entryFee == 0 || userCoins >= entryFee

    var inGameId by remember { mutableStateOf(userProfile?.inGameId ?: "") }
    var inGameName by remember { mutableStateOf(userProfile?.inGameName ?: "") }
    var teamName by remember { mutableStateOf("") }
    var teammates by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsCardBg,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column {
                Text(
                    text = AppStrings.confirmRegistration(language),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "${tournament.title} • ${tournament.format}",
                    style = MaterialTheme.typography.bodySmall.copy(color = ElectricBlue)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Wallet & Entry Fee Breakdown Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(androidx.compose.ui.graphics.Color(0xFF131227), RoundedCornerShape(12.dp))
                        .border(1.dp, if (!hasSufficientBalance) ErrorRed else EsportsBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MonetizationOn,
                                    contentDescription = null,
                                    tint = if (entryFee == 0) SuccessGreen else CyanGlow,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppStrings.entryFee(language),
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                                )
                            }
                            Text(
                                text = if (entryFee == 0) AppStrings.freeEntry(language) else "$entryFee Coins (₹$entryFee ref value)",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (entryFee == 0) SuccessGreen else BooyahGold,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppStrings.availableCoins(language),
                                    style = MaterialTheme.typography.labelMedium.copy(color = TextMuted)
                                )
                            }
                            Text(
                                text = "$userCoins Coins",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (hasSufficientBalance) ElectricBlue else ErrorRed,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        if (!hasSufficientBalance) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "Insufficient Coins! Need ${entryFee - userCoins} more Coins. Please top up in Wallet tab.",
                                    style = MaterialTheme.typography.labelSmall.copy(color = ErrorRed, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }

                // Free Fire UID
                OutlinedTextField(
                    value = inGameId,
                    onValueChange = {
                        inGameId = it
                        errorMsg = null
                    },
                    label = { Text(AppStrings.inGameIdLabel(language)) },
                    placeholder = { Text("e.g. 294819204") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = NeonPurpleBright)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_free_fire_uid"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = NeonPurpleBright,
                        unfocusedBorderColor = EsportsBorder,
                        focusedLabelColor = NeonPurpleBright,
                        unfocusedLabelColor = TextMuted
                    )
                )

                // Free Fire IGN
                OutlinedTextField(
                    value = inGameName,
                    onValueChange = {
                        inGameName = it
                        errorMsg = null
                    },
                    label = { Text(AppStrings.inGameNameLabel(language)) },
                    placeholder = { Text("e.g. HeadHunter_99") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = ElectricBlue)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_in_game_name"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = EsportsBorder,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = TextMuted
                    )
                )

                if (isTeamFormat) {
                    // Team Name
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text(AppStrings.teamNameLabel(language)) },
                        placeholder = { Text("e.g. Phoenix Warriors") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = FieryOrange)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_team_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = FieryOrange,
                            unfocusedBorderColor = EsportsBorder,
                            focusedLabelColor = FieryOrange,
                            unfocusedLabelColor = TextMuted
                        )
                    )

                    // Teammates UID/Names
                    OutlinedTextField(
                        value = teammates,
                        onValueChange = { teammates = it },
                        label = { Text(AppStrings.teammatesLabel(language)) },
                        placeholder = { Text("e.g. Player2 (UID: 123), Player3 (UID: 456)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_teammates"),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = FieryOrange,
                            unfocusedBorderColor = EsportsBorder,
                            focusedLabelColor = FieryOrange,
                            unfocusedLabelColor = TextMuted
                        )
                    )
                }

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                    )
                }

                // Important guidelines
                Text(
                    text = "• Room ID & Password will be released in My Matches 15 min before match.\n• Emulators & modified APKs are prohibited.\n• 1 Coin = ₹1 INR reference value.",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (!hasSufficientBalance) {
                        errorMsg = "Insufficient coin balance. Please top up your wallet."
                        return@Button
                    }
                    if (inGameId.isBlank()) {
                        errorMsg = "Please enter your Free Fire UID."
                        return@Button
                    }
                    if (inGameName.isBlank()) {
                        errorMsg = "Please enter your In-Game Name (IGN)."
                        return@Button
                    }
                    if (isTeamFormat && teamName.isBlank()) {
                        errorMsg = "Please provide your team name."
                        return@Button
                    }
                    onConfirm(
                        inGameId.trim(),
                        inGameName.trim(),
                        if (isTeamFormat) teamName.trim() else null,
                        if (isTeamFormat) teammates.trim() else null
                    )
                },
                enabled = !isLoading && hasSufficientBalance,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasSufficientBalance) NeonPurpleBright else androidx.compose.ui.graphics.Color.Gray
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_registration_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(18.dp),
                        strokeWidth = 2.dp,
                        color = TextWhite
                    )
                } else {
                    Text(
                        text = if (hasSufficientBalance) AppStrings.confirmRegistration(language) else "Insufficient Coins",
                        style = MaterialTheme.typography.labelLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                    )
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EsportsBorder)
            ) {
                Text(text = "Cancel", color = TextMuted)
            }
        }
    )
}
