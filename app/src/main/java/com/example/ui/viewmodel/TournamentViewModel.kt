package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.Announcement
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Registration
import com.example.data.model.RoomDetails
import com.example.data.model.Tournament
import com.example.data.model.Wallet
import com.example.data.model.WalletTransaction
import com.example.data.repository.TournamentRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface UiMessage {
    val text: String
    data class Success(override val text: String) : UiMessage
    data class Error(override val text: String) : UiMessage
}

class TournamentViewModel(
    private val repository: TournamentRepository,
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _tournaments = MutableStateFlow<List<Tournament>>(emptyList())
    val tournaments: StateFlow<List<Tournament>> = _tournaments.asStateFlow()

    private val _userRegistrations = MutableStateFlow<List<Registration>>(emptyList())
    val userRegistrations: StateFlow<List<Registration>> = _userRegistrations.asStateFlow()

    private val _wallet = MutableStateFlow<Wallet?>(null)
    val wallet: StateFlow<Wallet?> = _wallet.asStateFlow()

    private val _walletTransactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val walletTransactions: StateFlow<List<WalletTransaction>> = _walletTransactions.asStateFlow()

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _leaderboard = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _selectedTournament = MutableStateFlow<Tournament?>(null)
    val selectedTournament: StateFlow<Tournament?> = _selectedTournament.asStateFlow()

    private val _roomDetails = MutableStateFlow<RoomDetails?>(null)
    val roomDetails: StateFlow<RoomDetails?> = _roomDetails.asStateFlow()

    private val _formatFilter = MutableStateFlow("All")
    val formatFilter: StateFlow<String> = _formatFilter.asStateFlow()

    private val _statusFilter = MutableStateFlow("All")
    val statusFilter: StateFlow<String> = _statusFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _uiMessage = MutableStateFlow<UiMessage?>(null)
    val uiMessage: StateFlow<UiMessage?> = _uiMessage.asStateFlow()

    // Filtered tournaments
    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        _tournaments,
        _formatFilter,
        _statusFilter,
        _searchQuery
    ) { list, format, status, query ->
        list.filter { tournament ->
            val matchesFormat = format == "All" || tournament.format.equals(format, ignoreCase = true)
            val matchesStatus = status == "All" || tournament.status.equals(status, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    tournament.title.contains(query, ignoreCase = true) ||
                    tournament.map.contains(query, ignoreCase = true) ||
                    tournament.format.contains(query, ignoreCase = true)
            matchesFormat && matchesStatus && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.observeTournaments().collect { list ->
                _tournaments.value = list
                // If selected tournament is in list, update it
                val currentSelected = _selectedTournament.value
                if (currentSelected != null) {
                    _selectedTournament.value = list.find { it.id == currentSelected.id } ?: currentSelected
                }
            }
        }

        viewModelScope.launch {
            repository.observeAnnouncements().collect { list ->
                _announcements.value = list
            }
        }

        viewModelScope.launch {
            repository.observeLeaderboard().collect { list ->
                _leaderboard.value = list
            }
        }

        // Listen for user registrations and wallet when user is logged in
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                viewModelScope.launch {
                    repository.ensureWalletExists(uid)
                }
                viewModelScope.launch {
                    repository.observeUserRegistrations(uid).collect { list ->
                        _userRegistrations.value = list
                    }
                }
                viewModelScope.launch {
                    repository.observeWallet(uid).collect { w ->
                        _wallet.value = w
                    }
                }
                viewModelScope.launch {
                    repository.observeWalletTransactions(uid).collect { txList ->
                        _walletTransactions.value = txList
                    }
                }
            } else {
                _userRegistrations.value = emptyList()
                _wallet.value = null
                _walletTransactions.value = emptyList()
            }
        }
    }

    fun setFormatFilter(format: String) {
        _formatFilter.value = format
    }

    fun setStatusFilter(status: String) {
        _statusFilter.value = status
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectTournament(tournament: Tournament?) {
        _selectedTournament.value = tournament
        if (tournament != null) {
            loadRoomDetails(tournament.id)
        } else {
            _roomDetails.value = null
        }
    }

    fun loadRoomDetails(tournamentId: String) {
        viewModelScope.launch {
            repository.observeRoomDetails(tournamentId).collect { details ->
                _roomDetails.value = details
            }
        }
    }

    fun clearUiMessage() {
        _uiMessage.value = null
    }

    fun register(
        tournament: Tournament,
        playerDisplayName: String,
        inGameId: String,
        inGameName: String,
        teamName: String? = null,
        teammates: String? = null,
        onSuccess: () -> Unit
    ) {
        if (inGameId.isBlank() || inGameName.isBlank()) {
            _uiMessage.value = UiMessage.Error("Please enter your Free Fire UID and In-Game Name.")
            return
        }

        if (tournament.filledSlots >= tournament.maxSlots) {
            _uiMessage.value = UiMessage.Error("Tournament slots are fully booked!")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.registerForTournament(
                tournament = tournament,
                playerDisplayName = playerDisplayName,
                inGameId = inGameId,
                inGameName = inGameName,
                teamName = teamName,
                teammates = teammates
            )
            _isLoading.value = false
            if (result.isSuccess) {
                _uiMessage.value = UiMessage.Success("Registration confirmed for ${tournament.title}!")
                onSuccess()
            } else {
                val err = result.exceptionOrNull()?.message ?: "Registration failed."
                _uiMessage.value = UiMessage.Error(err)
            }
        }
    }

    fun addCoins(coins: Int, packageTitle: String, priceInr: Int, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addCoins(coins, packageTitle, priceInr)
            _isLoading.value = false
            if (result.isSuccess) {
                _uiMessage.value = UiMessage.Success("Successfully added $coins Coins to your wallet!")
                onComplete()
            } else {
                val err = result.exceptionOrNull()?.message ?: "Failed to add coins."
                _uiMessage.value = UiMessage.Error(err)
            }
        }
    }

    fun seedDefaultTournaments(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.seedInitialData()
            _isLoading.value = false
            if (result.isSuccess) {
                _uiMessage.value = UiMessage.Success("Sample tournaments, leaderboard & announcements loaded!")
                onComplete(true)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Failed to seed sample data."
                _uiMessage.value = UiMessage.Error(err)
                onComplete(false)
            }
        }
    }

    // Admin operations
    fun adminCreateTournament(tournament: Tournament, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.createTournament(tournament)
            _isLoading.value = false
            if (res.isSuccess) {
                _uiMessage.value = UiMessage.Success("Tournament created successfully!")
                onSuccess()
            } else {
                _uiMessage.value = UiMessage.Error(res.exceptionOrNull()?.message ?: "Failed to create tournament")
            }
        }
    }

    fun adminReleaseRoom(tournamentId: String, roomId: String, pass: String, notes: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.releaseRoomDetails(tournamentId, roomId, pass, notes)
            _isLoading.value = false
            if (res.isSuccess) {
                _uiMessage.value = UiMessage.Success("Room credentials released to registered players!")
                onSuccess()
            } else {
                _uiMessage.value = UiMessage.Error(res.exceptionOrNull()?.message ?: "Failed to release room")
            }
        }
    }

    fun adminUpdateStatus(tournamentId: String, status: String, winnerName: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.updateTournamentStatus(tournamentId, status, winnerName)
            _isLoading.value = false
            if (res.isSuccess) {
                _uiMessage.value = UiMessage.Success("Tournament status updated to ${status.uppercase()}!")
            } else {
                _uiMessage.value = UiMessage.Error(res.exceptionOrNull()?.message ?: "Failed to update status")
            }
        }
    }

    fun adminCreateAnnouncement(title: String, content: String, tag: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.createAnnouncement(title, content, tag)
            _isLoading.value = false
            if (res.isSuccess) {
                _uiMessage.value = UiMessage.Success("Announcement published!")
                onSuccess()
            } else {
                _uiMessage.value = UiMessage.Error(res.exceptionOrNull()?.message ?: "Failed to post announcement")
            }
        }
    }

    fun adminPublishLeaderboard(entry: LeaderboardEntry, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repository.publishLeaderboardEntry(entry)
            _isLoading.value = false
            if (res.isSuccess) {
                _uiMessage.value = UiMessage.Success("Leaderboard entry updated!")
                onSuccess()
            } else {
                _uiMessage.value = UiMessage.Error(res.exceptionOrNull()?.message ?: "Failed to update leaderboard")
            }
        }
    }
}

class TournamentViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TournamentViewModel(TournamentRepository(context)) as T
    }
}
