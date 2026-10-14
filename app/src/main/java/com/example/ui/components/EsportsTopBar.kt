package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun EsportsTopBar(
    language: AppLanguage,
    isAdmin: Boolean,
    coinBalance: Int? = null,
    onOpenWallet: (() -> Unit)? = null,
    onOpenLeaderboard: (() -> Unit)? = null,
    onToggleLanguage: () -> Unit,
    onOpenAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Logo & Branding
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_icon),
                contentDescription = "Akshar Tournament Crest",
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, NeonPurpleBright, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column {
                Text(
                    text = AppStrings.appTitle(language),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = AppStrings.esportsSlogan(language),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = ElectricBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }

        // Actions: Coin Balance Chip, Leaderboard, Admin, Language
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Quick Coin Chip
            if (coinBalance != null && onOpenWallet != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF1B1934))
                        .border(1.dp, BooyahGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clickable { onOpenWallet() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("topbar_wallet_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "🪙", fontSize = 12.sp)
                        Text(
                            text = "$coinBalance",
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = BooyahGold,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Leaderboard Trophy Icon
            if (onOpenLeaderboard != null) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1B1934))
                        .border(1.dp, EsportsBorder, CircleShape)
                        .clickable { onOpenLeaderboard() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Leaderboard",
                        tint = CyanGlow,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (isAdmin) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BooyahGold.copy(alpha = 0.15f))
                        .border(1.dp, BooyahGold, RoundedCornerShape(8.dp))
                        .clickable { onOpenAdmin() }
                        .padding(horizontal = 7.dp, vertical = 5.dp)
                        .testTag("admin_panel_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = BooyahGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "ADMIN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = BooyahGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            // Language switcher button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1B1934))
                    .border(1.dp, EsportsBorder, RoundedCornerShape(16.dp))
                    .clickable { onToggleLanguage() }
                    .padding(horizontal = 8.dp, vertical = 5.dp)
                    .testTag("language_toggle_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = NeonPurpleBright,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (language == AppLanguage.HINDI) "HI" else "EN",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}
