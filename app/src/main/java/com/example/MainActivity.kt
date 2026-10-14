package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Tournament
import com.example.ui.components.EsportsTopBar
import com.example.ui.components.RegistrationDialog
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LeaderboardScreen
import com.example.ui.screens.MyMatchesScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.TournamentDetailScreen
import com.example.ui.screens.TournamentsScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.AksharTournamentTheme
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AuthUiState
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AuthViewModelFactory
import com.example.ui.viewmodel.TournamentViewModel
import com.example.ui.viewmodel.TournamentViewModelFactory
import com.example.ui.viewmodel.UiMessage
import com.example.util.AppLanguage
import com.example.util.AppStrings
import com.example.util.LocalAppLanguage
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(applicationContext)
    }

    private val tournamentViewModel: TournamentViewModel by viewModels {
        TournamentViewModelFactory(applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val authState by authViewModel.uiState.collectAsStateWithLifecycle()
            val language by authViewModel.language.collectAsStateWithLifecycle()
            val isAdmin by authViewModel.isAdmin.collectAsStateWithLifecycle()

            CompositionLocalProvider(LocalAppLanguage provides language) {
                AksharTournamentTheme {
                    when (val state = authState) {
                        is AuthUiState.Authenticated -> {
                            AksharTournamentApp(
                                authViewModel = authViewModel,
                                tournamentViewModel = tournamentViewModel,
                                authState = state,
                                language = language,
                                isAdmin = isAdmin
                            )
                        }
                        else -> {
                            AuthScreen(
                                uiState = state,
                                language = language,
                                onToggleLanguage = { authViewModel.toggleLanguage() },
                                onSignInClick = { activity ->
                                    authViewModel.signInWithGoogle(this@MainActivity, activity)
                                },
                                onEmailSignIn = { email, pass ->
                                    authViewModel.signInWithEmail(email, pass)
                                },
                                onEmailSignUp = { name, email, pass, uid, ign ->
                                    authViewModel.signUpWithEmail(name, email, pass, uid, ign)
                                },
                                onForgotPassword = { email, callback ->
                                    authViewModel.sendForgotPasswordReset(email, callback)
                                },
                                onVerifyOtp = { otp ->
                                    authViewModel.verifyOtp(otp)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AksharTournamentApp(
    authViewModel: AuthViewModel,
    tournamentViewModel: TournamentViewModel,
    authState: AuthUiState.Authenticated,
    language: AppLanguage,
    isAdmin: Boolean
) {
    var selectedNavIndex by remember { mutableIntStateOf(0) }
    var showAdminScreen by remember { mutableStateOf(false) }
    var showLeaderboardScreen by remember { mutableStateOf(false) }
    var registeringTournament by remember { mutableStateOf<Tournament?>(null) }

    val tournaments by tournamentViewModel.tournaments.collectAsStateWithLifecycle()
    val filteredTournaments by tournamentViewModel.filteredTournaments.collectAsStateWithLifecycle()
    val registrations by tournamentViewModel.userRegistrations.collectAsStateWithLifecycle()
    val announcements by tournamentViewModel.announcements.collectAsStateWithLifecycle()
    val leaderboard by tournamentViewModel.leaderboard.collectAsStateWithLifecycle()
    val selectedTournament by tournamentViewModel.selectedTournament.collectAsStateWithLifecycle()
    val roomDetails by tournamentViewModel.roomDetails.collectAsStateWithLifecycle()
    val wallet by tournamentViewModel.wallet.collectAsStateWithLifecycle()
    val walletTransactions by tournamentViewModel.walletTransactions.collectAsStateWithLifecycle()
    val formatFilter by tournamentViewModel.formatFilter.collectAsStateWithLifecycle()
    val statusFilter by tournamentViewModel.statusFilter.collectAsStateWithLifecycle()
    val searchQuery by tournamentViewModel.searchQuery.collectAsStateWithLifecycle()
    val isLoading by tournamentViewModel.isLoading.collectAsStateWithLifecycle()
    val uiMessage by tournamentViewModel.uiMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiMessage) {
        uiMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg.text)
            tournamentViewModel.clearUiMessage()
        }
    }

    // Handle Hardware Back Button
    BackHandler(enabled = selectedTournament != null || showAdminScreen || showLeaderboardScreen || selectedNavIndex != 0) {
        if (selectedTournament != null) {
            tournamentViewModel.selectTournament(null)
        } else if (showAdminScreen) {
            showAdminScreen = false
        } else if (showLeaderboardScreen) {
            showLeaderboardScreen = false
        } else if (selectedNavIndex != 0) {
            selectedNavIndex = 0
        }
    }

    val isRegisteredForSelected = remember(selectedTournament, registrations) {
        selectedTournament?.let { t -> registrations.any { it.tournamentId == t.id } } ?: false
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EsportsBackground),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (selectedTournament == null && !showAdminScreen && !showLeaderboardScreen) {
                EsportsTopBar(
                    language = language,
                    isAdmin = isAdmin,
                    coinBalance = wallet?.coins,
                    onOpenWallet = { selectedNavIndex = 3 },
                    onOpenLeaderboard = { showLeaderboardScreen = true },
                    onToggleLanguage = { authViewModel.toggleLanguage() },
                    onOpenAdmin = { showAdminScreen = true }
                )
            }
        },
        bottomBar = {
            if (selectedTournament == null && !showAdminScreen && !showLeaderboardScreen) {
                EsportsBottomBar(
                    selectedIndex = selectedNavIndex,
                    language = language,
                    onSelectTab = { selectedNavIndex = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(EsportsBackground)
        ) {
            when {
                // Sub-screen: Admin Dashboard
                showAdminScreen -> {
                    AdminScreen(
                        tournaments = tournaments,
                        language = language,
                        isLoading = isLoading,
                        onBack = { showAdminScreen = false },
                        onCreateTournament = { tourney ->
                            tournamentViewModel.adminCreateTournament(tourney) {
                                scope.launch { snackbarHostState.showSnackbar("Tournament Created!") }
                            }
                        },
                        onReleaseRoom = { tourneyId, roomId, pass, notes ->
                            tournamentViewModel.adminReleaseRoom(tourneyId, roomId, pass, notes) {
                                scope.launch { snackbarHostState.showSnackbar("Room details published!") }
                            }
                        },
                        onUpdateStatus = { tourneyId, status, winner ->
                            tournamentViewModel.adminUpdateStatus(tourneyId, status, winner)
                        },
                        onCreateAnnouncement = { title, content, tag ->
                            tournamentViewModel.adminCreateAnnouncement(title, content, tag) {
                                scope.launch { snackbarHostState.showSnackbar("Announcement posted!") }
                            }
                        },
                        onPublishLeaderboard = { entry ->
                            tournamentViewModel.adminPublishLeaderboard(entry) {
                                scope.launch { snackbarHostState.showSnackbar("Leaderboard score saved!") }
                            }
                        },
                        onSeedSampleData = {
                            tournamentViewModel.seedDefaultTournaments { success ->
                                if (success) scope.launch { snackbarHostState.showSnackbar("Sample tournaments loaded!") }
                            }
                        }
                    )
                }

                // Sub-screen: Leaderboard
                showLeaderboardScreen -> {
                    LeaderboardScreen(
                        leaderboard = leaderboard,
                        language = language,
                        onBack = { showLeaderboardScreen = false }
                    )
                }

                // Sub-screen: Tournament Details
                selectedTournament != null -> {
                    TournamentDetailScreen(
                        tournament = selectedTournament!!,
                        isRegistered = isRegisteredForSelected,
                        roomDetails = roomDetails,
                        language = language,
                        onBack = { tournamentViewModel.selectTournament(null) },
                        onOpenRegisterDialog = {
                            registeringTournament = selectedTournament
                        }
                    )
                }

                // Main Navigation Tabs
                else -> {
                    when (selectedNavIndex) {
                        0 -> HomeScreen(
                            userProfile = authState.profile,
                            wallet = wallet,
                            tournaments = tournaments,
                            registrations = registrations,
                            announcements = announcements,
                            language = language,
                            onViewTournament = { tournamentViewModel.selectTournament(it) },
                            onNavigateToMyMatches = { selectedNavIndex = 2 },
                            onNavigateToTournaments = { selectedNavIndex = 1 },
                            onNavigateToWallet = { selectedNavIndex = 3 },
                            onNavigateToLeaderboard = { showLeaderboardScreen = true },
                            onSeedData = {
                                tournamentViewModel.seedDefaultTournaments { success ->
                                    if (success) scope.launch { snackbarHostState.showSnackbar("Sample tournaments loaded!") }
                                }
                            }
                        )

                        1 -> TournamentsScreen(
                            tournaments = filteredTournaments,
                            registrations = registrations,
                            selectedFormat = formatFilter,
                            selectedStatus = statusFilter,
                            searchQuery = searchQuery,
                            language = language,
                            onFormatChange = { tournamentViewModel.setFormatFilter(it) },
                            onStatusChange = { tournamentViewModel.setStatusFilter(it) },
                            onSearchChange = { tournamentViewModel.setSearchQuery(it) },
                            onViewDetails = { tournamentViewModel.selectTournament(it) }
                        )

                        2 -> MyMatchesScreen(
                            tournaments = tournaments,
                            registrations = registrations,
                            roomDetails = roomDetails,
                            language = language,
                            onViewTournament = { tournamentViewModel.selectTournament(it) },
                            onExploreTournaments = { selectedNavIndex = 1 }
                        )

                        3 -> WalletScreen(
                            wallet = wallet,
                            transactions = walletTransactions,
                            language = language,
                            isLoading = isLoading,
                            onAddCoins = { coins, title, priceInr ->
                                tournamentViewModel.addCoins(coins, title, priceInr)
                            }
                        )

                        4 -> ProfileScreen(
                            userProfile = authState.profile,
                            wallet = wallet,
                            language = language,
                            isAdmin = isAdmin,
                            onOpenWallet = { selectedNavIndex = 3 },
                            onToggleLanguage = { authViewModel.toggleLanguage() },
                            onUpdateGamerDetails = { ign, uid ->
                                authViewModel.updateGamerDetails(ign, uid) { success, err ->
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (success) "Free Fire profile updated!" else (err ?: "Update failed")
                                        )
                                    }
                                }
                            },
                            onOpenAdmin = { showAdminScreen = true },
                            onSeedData = {
                                tournamentViewModel.seedDefaultTournaments { success ->
                                    if (success) scope.launch { snackbarHostState.showSnackbar("Sample tournaments loaded!") }
                                }
                            },
                            onSignOut = { authViewModel.signOut() }
                        )
                    }
                }
            }
        }
    }

    // Modal Registration Dialog
    if (registeringTournament != null) {
        RegistrationDialog(
            tournament = registeringTournament!!,
            userProfile = authState.profile,
            wallet = wallet,
            language = language,
            isLoading = isLoading,
            onDismiss = { registeringTournament = null },
            onConfirm = { inGameId, inGameName, teamName, teammates ->
                tournamentViewModel.register(
                    tournament = registeringTournament!!,
                    playerDisplayName = authState.profile?.displayName ?: "Player",
                    inGameId = inGameId,
                    inGameName = inGameName,
                    teamName = teamName,
                    teammates = teammates,
                    onSuccess = {
                        registeringTournament = null
                    }
                )
            }
        )
    }
}

@Composable
fun EsportsBottomBar(
    selectedIndex: Int,
    language: AppLanguage,
    onSelectTab: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .navigationBarsPadding()
            .height(68.dp)
            .border(1.dp, EsportsBorder),
        containerColor = Color(0xFF100F21),
        tonalElevation = 8.dp
    ) {
        val navItems = listOf(
            Triple(AppStrings.navHome(language), Icons.Default.Home, 0),
            Triple(AppStrings.navTournaments(language), Icons.Default.SportsEsports, 1),
            Triple(AppStrings.navMyMatches(language), Icons.Default.SportsScore, 2),
            Triple(AppStrings.navWallet(language), Icons.Default.AccountBalanceWallet, 3),
            Triple(AppStrings.navProfile(language), Icons.Default.Person, 4)
        )

        navItems.forEach { (label, icon, index) ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectTab(index) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (isSelected) NeonPurpleBright else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonPurpleBright else TextMuted,
                            fontSize = 10.sp
                        )
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = NeonPurple.copy(alpha = 0.2f),
                    selectedIconColor = NeonPurpleBright,
                    unselectedIconColor = TextMuted,
                    selectedTextColor = NeonPurpleBright,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_item_$index")
            )
        }
    }
}
