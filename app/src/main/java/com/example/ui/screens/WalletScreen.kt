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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CoinPackage
import com.example.data.model.Wallet
import com.example.data.model.WalletTransaction
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ErrorRed
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
fun WalletScreen(
    wallet: Wallet?,
    transactions: List<WalletTransaction>,
    language: AppLanguage,
    isLoading: Boolean,
    onAddCoins: (coins: Int, packageTitle: String, priceInr: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showTopUpDialog by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTransactions = remember(transactions, selectedFilter) {
        when (selectedFilter) {
            "CREDIT" -> transactions.filter { it.isCredit }
            "DEBIT" -> transactions.filter { !it.isCredit }
            "ENTRY" -> transactions.filter { it.type == "MATCH_ENTRY" }
            else -> transactions
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EsportsBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Balance Card
        item {
            WalletBalanceCard(
                wallet = wallet,
                language = language,
                onOpenTopUp = { showTopUpDialog = true }
            )
        }

        // Regulatory & Ledger Assurance Note
        item {
            LedgerAssuranceBanner(language = language)
        }

        // Transactions Header & Filters
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppStrings.transactionHistory(language),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${filteredTransactions.size} Records",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                    )
                }

                // Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        label = { Text(AppStrings.allTransactions(language)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonPurpleBright,
                            selectedLabelColor = TextWhite,
                            containerColor = EsportsCardBg,
                            labelColor = TextMuted
                        ),
                        modifier = Modifier.testTag("filter_wallet_all")
                    )
                    FilterChip(
                        selected = selectedFilter == "CREDIT",
                        onClick = { selectedFilter = "CREDIT" },
                        label = { Text(AppStrings.creditsFilter(language)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuccessGreen.copy(alpha = 0.8f),
                            selectedLabelColor = TextWhite,
                            containerColor = EsportsCardBg,
                            labelColor = TextMuted
                        ),
                        modifier = Modifier.testTag("filter_wallet_credit")
                    )
                    FilterChip(
                        selected = selectedFilter == "DEBIT",
                        onClick = { selectedFilter = "DEBIT" },
                        label = { Text(AppStrings.debitsFilter(language)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FieryOrange,
                            selectedLabelColor = TextWhite,
                            containerColor = EsportsCardBg,
                            labelColor = TextMuted
                        ),
                        modifier = Modifier.testTag("filter_wallet_debit")
                    )
                }
            }
        }

        // Transaction List
        if (filteredTransactions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = AppStrings.noTransactions(language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = TextMuted,
                                textAlign = TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showTopUpDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = AppStrings.addCoins(language), color = TextWhite)
                        }
                    }
                }
            }
        } else {
            items(filteredTransactions, key = { it.id }) { tx ->
                TransactionItemCard(tx = tx, language = language)
            }
        }
    }

    // Top-Up Package Selector Dialog
    if (showTopUpDialog) {
        TopUpPackagesDialog(
            language = language,
            isLoading = isLoading,
            onDismiss = { showTopUpDialog = false },
            onSelectPackage = { pkg ->
                onAddCoins(pkg.totalCoins, pkg.label ?: "${pkg.coins} Coins Package", pkg.priceInr)
                showTopUpDialog = false
            }
        )
    }
}

@Composable
fun WalletBalanceCard(
    wallet: Wallet?,
    language: AppLanguage,
    onOpenTopUp: () -> Unit
) {
    val coins = wallet?.coins ?: 100
    val totalAdded = wallet?.totalAdded ?: 100
    val totalSpent = wallet?.totalSpent ?: 0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(listOf(NeonPurpleBright, ElectricBlue)),
                shape = RoundedCornerShape(20.dp)
            )
            .testTag("wallet_balance_card"),
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonPurple.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = NeonPurpleBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = AppStrings.availableCoins(language),
                        style = MaterialTheme.typography.titleSmall.copy(color = TextMuted)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(SuccessGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Ledger Protected",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SuccessGreen,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Coin Balance
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BooyahGold.copy(alpha = 0.2f))
                        .border(1.dp, BooyahGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🪙", fontSize = 24.sp)
                }

                Column {
                    Text(
                        text = "$coins Coins",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextWhite,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "≈ ₹$coins Reference Value (1 Coin = ₹1 INR)",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = ElectricBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = AppStrings.referenceValueSubtitle(language),
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 11.sp)
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = EsportsBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // Ledger Summary Row & Add Coins CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column {
                            Text(
                                text = "Total Credited",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                            )
                            Text(
                                text = "+$totalAdded",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Column {
                            Text(
                                text = "Total Spent",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, fontSize = 10.sp)
                            )
                            Text(
                                text = "-$totalSpent",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = FieryOrange,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                Button(
                    onClick = onOpenTopUp,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("open_add_coins_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = AppStrings.addCoins(language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun LedgerAssuranceBanner(language: AppLanguage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131227)),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF262352))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = CyanGlow,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = AppStrings.devModeNotice(language),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFD4D3F0),
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TransactionItemCard(
    tx: WalletTransaction,
    language: AppLanguage
) {
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = tx.createdAt?.toDate()?.let { dateFormat.format(it) } ?: "Just now"

    val isCredit = tx.isCredit
    val icon = when (tx.type) {
        "WELCOME_BONUS" -> Icons.Default.CardGiftcard
        "TEST_TOPUP" -> Icons.Default.ArrowDownward
        "REFUND" -> Icons.Default.CheckCircle
        "MATCH_ENTRY" -> Icons.Default.SportsEsports
        else -> if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward
    }

    val iconColor = when {
        tx.type == "WELCOME_BONUS" -> BooyahGold
        tx.type == "REFUND" -> CyanGlow
        isCredit -> SuccessGreen
        else -> FieryOrange
    }

    val displayTitle = when (tx.type) {
        "WELCOME_BONUS" -> AppStrings.welcomeBonus(language)
        "TEST_TOPUP" -> tx.referenceTitle ?: AppStrings.testTopUpCredit(language)
        "MATCH_ENTRY" -> tx.referenceTitle ?: AppStrings.matchEntryDeduction(language)
        "REFUND" -> tx.referenceTitle ?: AppStrings.refundRecord(language)
        else -> tx.description.ifBlank { "Ledger Adjustment" }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, EsportsBorder, RoundedCornerShape(12.dp))
            .testTag("tx_card_${tx.id}"),
        colors = CardDefaults.cardColors(containerColor = EsportsCardBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f))
                        .border(1.dp, iconColor.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        ),
                        maxLines = 1
                    )
                    Text(
                        text = "$dateStr • ID: ${tx.id.takeLast(8)}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isCredit) "+${tx.amount} Coins" else "${tx.amount} Coins",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isCredit) SuccessGreen else FieryOrange,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Ref: ₹${tx.referenceValueInr}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

@Composable
fun TopUpPackagesDialog(
    language: AppLanguage,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSelectPackage: (CoinPackage) -> Unit
) {
    val packages = CoinPackage.DEFAULT_PACKAGES

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EsportsCardBg,
        shape = RoundedCornerShape(20.dp),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppStrings.topUpModalTitle(language),
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = TextWhite,
                        fontWeight = FontWeight.Bold
                    )
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Dev mode sandbox banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF141328), RoundedCornerShape(8.dp))
                        .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "🎮 " + AppStrings.devModeNotice(language),
                        style = MaterialTheme.typography.labelSmall.copy(color = CyanGlow, fontSize = 11.sp)
                    )
                }

                // Packages List
                packages.forEach { pkg ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(
                                width = 1.dp,
                                color = if (pkg.isPopular) BooyahGold else EsportsBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable(enabled = !isLoading) { onSelectPackage(pkg) }
                            .testTag("package_${pkg.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (pkg.isPopular) Color(0xFF1E1A38) else Color(0xFF121124)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(text = "🪙", fontSize = 22.sp)
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${pkg.totalCoins} Coins",
                                            style = MaterialTheme.typography.titleSmall.copy(
                                                color = TextWhite,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                        if (pkg.bonusCoins > 0) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(SuccessGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "+${pkg.bonusCoins} Bonus",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = SuccessGreen,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = pkg.label ?: "Standard pack",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }
                            }

                            Button(
                                onClick = { onSelectPackage(pkg) },
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (pkg.isPopular) BooyahGold else NeonPurpleBright
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "₹${pkg.priceInr}",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = if (pkg.isPopular) Color.Black else TextWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
