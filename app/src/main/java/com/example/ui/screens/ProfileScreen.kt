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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import coil.compose.AsyncImage
import com.example.data.model.UserProfile
import com.example.data.model.Wallet
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
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    wallet: Wallet? = null,
    language: AppLanguage,
    isAdmin: Boolean,
    onOpenWallet: (() -> Unit)? = null,
    onToggleLanguage: () -> Unit,
    onUpdateGamerDetails: (String, String) -> Unit,
    onOpenAdmin: () -> Unit,
    onSeedData: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var editIgn by remember { mutableStateOf(userProfile?.inGameName ?: "") }
    var editUid by remember { mutableStateOf(userProfile?.inGameId ?: "") }
    var showPolicyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Profile Avatar & Name Card
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!userProfile?.photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = userProfile?.photoUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, NeonPurpleBright, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.3f))
                            .border(2.dp, NeonPurpleBright, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile?.displayName?.take(1)?.uppercase() ?: "P",
                            style = MaterialTheme.typography.headlineLarge.copy(color = TextWhite, fontWeight = FontWeight.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = userProfile?.displayName ?: "Player",
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )

                Text(
                    text = userProfile?.email ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                )

                if (isAdmin) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .background(BooyahGold.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "OFFICIAL ADMIN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BooyahGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Coin Wallet Balance Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(BooyahGold.copy(alpha = 0.15f))
                            .border(1.dp, BooyahGold.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🪙", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = AppStrings.navWallet(language),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "${wallet?.coins ?: 100} Coins (≈ ₹${wallet?.coins ?: 100} Ref Value)",
                            style = MaterialTheme.typography.bodySmall.copy(color = ElectricBlue)
                        )
                    }
                }

                if (onOpenWallet != null) {
                    Button(
                        onClick = onOpenWallet,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("profile_manage_wallet_button")
                    ) {
                        Text(
                            text = "Manage",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = TextWhite,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Free Fire In-Game Credentials Card
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Free Fire Player Credentials",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    IconButton(
                        onClick = {
                            editIgn = userProfile?.inGameName ?: ""
                            editUid = userProfile?.inGameId ?: ""
                            showEditDialog = true
                        },
                        modifier = Modifier.testTag("edit_gamer_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = ElectricBlue)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Player UID", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        Text(
                            text = if (!userProfile?.inGameId.isNullOrBlank()) userProfile?.inGameId!! else "Not Set",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (!userProfile?.inGameId.isNullOrBlank()) TextWhite else FieryOrange,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "In-Game Name (IGN)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        Text(
                            text = if (!userProfile?.inGameName.isNullOrBlank()) userProfile?.inGameName!! else "Not Set",
                            style = MaterialTheme.typography.titleSmall.copy(
                                color = if (!userProfile?.inGameName.isNullOrBlank()) ElectricBlue else FieryOrange,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Career Statistics
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
                Text(
                    text = "Career Statistics",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CareerStatBox(label = "Matches", value = "${userProfile?.matchesPlayed ?: 0}", color = ElectricBlue)
                    CareerStatBox(label = "Booyah Wins", value = "${userProfile?.wins ?: 0}", color = BooyahGold)
                    CareerStatBox(label = "Eliminations", value = "${userProfile?.kills ?: 0}", color = FieryOrange)
                    CareerStatBox(label = "Total Points", value = "${userProfile?.totalPoints ?: 0}", color = SuccessGreen)
                }
            }
        }

        // Settings & Actions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, EsportsBorder, RoundedCornerShape(14.dp)),
            colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Language toggle row
                ActionRow(
                    icon = Icons.Default.Translate,
                    title = AppStrings.language(language),
                    subtitle = if (language == AppLanguage.HINDI) "हिंदी (Switch to English)" else "English (हिंदी चुनें)",
                    onClick = onToggleLanguage
                )

                // Fair Play Policy
                ActionRow(
                    icon = Icons.Default.Security,
                    title = AppStrings.termsPolicy(language),
                    subtitle = "Anti-cheat rules & fair play pledge",
                    onClick = { showPolicyDialog = true }
                )

                // Admin Dashboard (if admin)
                if (isAdmin) {
                    ActionRow(
                        icon = Icons.Default.AdminPanelSettings,
                        title = AppStrings.navAdmin(language),
                        subtitle = "Create tournaments & release room credentials",
                        onClick = onOpenAdmin
                    )
                }

                // Seed sample tournaments row
                ActionRow(
                    icon = Icons.Default.Refresh,
                    title = "Load Sample Tournaments",
                    subtitle = "Populate demo Free Fire scrims & leaderboards",
                    onClick = onSeedData
                )
            }
        }

        // Notice
        Text(
            text = AppStrings.freeTournamentNotice(language),
            style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Logout Button
        Button(
            onClick = onSignOut,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF261828)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("logout_button")
        ) {
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = AppStrings.logout(language),
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(80.dp))
    }

    // Edit Gamer Profile Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor = EsportsCardBg,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(
                    text = "Update Free Fire Profile",
                    style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editUid,
                        onValueChange = { editUid = it },
                        label = { Text("Free Fire UID") },
                        placeholder = { Text("e.g. 294810294") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = NeonPurpleBright,
                            unfocusedBorderColor = EsportsBorder
                        )
                    )

                    OutlinedTextField(
                        value = editIgn,
                        onValueChange = { editIgn = it },
                        label = { Text("In-Game Name (IGN)") },
                        placeholder = { Text("e.g. Viper_God") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite,
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = EsportsBorder
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateGamerDetails(editIgn.trim(), editUid.trim())
                        showEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright)
                ) {
                    Text(text = "Save", color = TextWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text(text = "Cancel", color = TextMuted)
                }
            }
        )
    }

    // Policy Dialog
    if (showPolicyDialog) {
        AlertDialog(
            onDismissRequest = { showPolicyDialog = false },
            containerColor = EsportsCardBg,
            shape = RoundedCornerShape(18.dp),
            title = {
                Text(text = "Fair Play & Anti-Cheat Policy", style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold))
            },
            text = {
                Text(
                    text = "1. Prohibited Tools: Emulators, APK mods, auto-aim scripts, and macro triggers are strictly forbidden.\n\n" +
                            "2. Teaming: Unfair collaboration with enemy squads in solo or team matches leads to immediate disqualification.\n\n" +
                            "3. Room Credentials: Room ID and Password are for registered players only.\n\n" +
                            "4. Proof & Disputes: Record your final screen/kill feed for placement verifications.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextWhite)
                )
            },
            confirmButton = {
                Button(
                    onClick = { showPolicyDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright)
                ) {
                    Text(text = "I Agree", color = TextWhite)
                }
            }
        )
    }
}

@Composable
private fun CareerStatBox(label: String, value: String, color: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color(0xFF131224), RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
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
                fontSize = 10.sp
            )
        )
    }
}

@Composable
private fun ActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = NeonPurpleBright, modifier = Modifier.size(22.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleSmall.copy(color = TextWhite, fontWeight = FontWeight.Bold))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = TextMuted, fontSize = 11.sp))
        }
    }
}
