package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.Announcement
import com.example.data.model.LeaderboardEntry
import com.example.data.model.Registration
import com.example.data.model.RoomDetails
import com.example.data.model.Tournament
import com.example.data.model.Wallet
import com.example.data.model.WalletTransaction
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.util.Date

class TournamentRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    // Observe all tournaments
    fun observeTournaments(): Flow<List<Tournament>> = flow {
        val path = "tournaments"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Tournament::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // Observe user registrations
    fun observeUserRegistrations(userId: String): Flow<List<Registration>> = flow {
        val path = "registrations"
        emitAll(
            db.collection(path)
                .whereEqualTo("userId", userId)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Registration::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // Check if user is registered for a specific tournament
    suspend fun isUserRegistered(tournamentId: String, userId: String): Boolean = try {
        val regId = "${tournamentId}_$userId"
        val doc = db.collection("registrations").document(regId).get().await()
        doc.exists()
    } catch (e: Exception) {
        false
    }

    // Observe User Wallet
    fun observeWallet(userId: String): Flow<Wallet?> = flow {
        val path = "wallets/$userId"
        emitAll(
            db.collection("wallets").document(userId)
                .snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(Wallet::class.java)
                    } else {
                        null
                    }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    emit(null)
                }
        )
    }

    // Observe User Wallet Transactions
    fun observeWalletTransactions(userId: String): Flow<List<WalletTransaction>> = flow {
        val path = "wallets/$userId/transactions"
        emitAll(
            db.collection("wallets").document(userId)
                .collection("transactions")
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(WalletTransaction::class.java)
                        .sortedByDescending { it.createdAt?.toDate()?.time ?: 0L }
                }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    emit(emptyList())
                }
        )
    }

    // Ensure wallet exists, creating 100 free welcome coins on first access
    suspend fun ensureWalletExists(userId: String): Result<Wallet> = try {
        val walletRef = db.collection("wallets").document(userId)
        val snap = walletRef.get().await()
        if (snap.exists()) {
            Result.success(snap.toObject(Wallet::class.java) ?: Wallet(userId = userId))
        } else {
            val initialCoins = 100
            val wallet = Wallet(
                userId = userId,
                coins = initialCoins,
                totalAdded = initialCoins,
                totalSpent = 0
            )
            val walletPayload = mapOf(
                "userId" to userId,
                "coins" to initialCoins,
                "totalAdded" to initialCoins,
                "totalSpent" to 0,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            walletRef.set(walletPayload).await()

            val txId = "tx_welcome_${System.currentTimeMillis()}"
            val txPayload = mapOf(
                "id" to txId,
                "userId" to userId,
                "type" to "WELCOME_BONUS",
                "amount" to initialCoins,
                "referenceId" to "welcome_bonus",
                "referenceTitle" to "Akshar Tournament Welcome Bonus",
                "status" to "COMPLETED",
                "idempotencyKey" to "welcome_${userId}",
                "description" to "Free initial esports coins to test tournament registration",
                "createdAt" to FieldValue.serverTimestamp()
            )
            walletRef.collection("transactions").document(txId).set(txPayload).await()

            Result.success(wallet)
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "wallets/$userId")
        Result.failure(e)
    }

    // Add Coins (Test Development Sandbox Mode as required by Prompt)
    suspend fun addCoins(coinsToAdd: Int, packageTitle: String, priceInr: Int): Result<Unit> = try {
        val uid = requireUserId()
        ensureWalletExists(uid)
        val walletRef = db.collection("wallets").document(uid)
        val txId = "tx_topup_${System.currentTimeMillis()}_${(1000..9999).random()}"
        val idempotencyKey = "topup_${uid}_${System.currentTimeMillis()}"

        val txPayload = mapOf(
            "id" to txId,
            "userId" to uid,
            "type" to "TEST_TOPUP",
            "amount" to coinsToAdd,
            "referenceId" to "pack_${priceInr}",
            "referenceTitle" to packageTitle,
            "status" to "COMPLETED",
            "idempotencyKey" to idempotencyKey,
            "description" to "Test wallet top-up: $coinsToAdd Coins (₹$priceInr ref value)",
            "createdAt" to FieldValue.serverTimestamp()
        )

        db.runBatch { batch ->
            batch.update(
                walletRef,
                mapOf(
                    "coins" to FieldValue.increment(coinsToAdd.toLong()),
                    "totalAdded" to FieldValue.increment(coinsToAdd.toLong()),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            batch.set(walletRef.collection("transactions").document(txId), txPayload)
        }.await()

        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "wallets")
        Result.failure(e)
    }

    // Register user for tournament with Atomic Wallet & Slot verification
    suspend fun registerForTournament(
        tournament: Tournament,
        playerDisplayName: String,
        inGameId: String,
        inGameName: String,
        teamName: String? = null,
        teammates: String? = null
    ): Result<Unit> {
        return try {
            val uid = requireUserId()
        ensureWalletExists(uid)
        val regId = "${tournament.id}_$uid"
        val regRef = db.collection("registrations").document(regId)
        val tourneyRef = db.collection("tournaments").document(tournament.id)
        val walletRef = db.collection("wallets").document(uid)

        // Check if already registered
        val existingReg = regRef.get().await()
        if (existingReg.exists()) {
            return Result.failure(IllegalStateException("You are already registered for this tournament!"))
        }

        // Check slot availability
        val currentTourneySnap = tourneyRef.get().await()
        val currentFilled = currentTourneySnap.getLong("filledSlots")?.toInt() ?: tournament.filledSlots
        val maxSlots = currentTourneySnap.getLong("maxSlots")?.toInt() ?: tournament.maxSlots
        if (currentFilled >= maxSlots) {
            return Result.failure(IllegalStateException("Tournament slots are completely full!"))
        }

        // Check wallet balance if entry fee > 0
        val entryFee = tournament.entryFee
        if (entryFee > 0) {
            val walletSnap = walletRef.get().await()
            val currentCoins = walletSnap.getLong("coins")?.toInt() ?: 0
            if (currentCoins < entryFee) {
                return Result.failure(
                    IllegalStateException(
                        "Insufficient Coins in Wallet. Balance: $currentCoins Coins, Required: $entryFee Coins. Please top up your wallet."
                    )
                )
            }
        }

        val regPayload = mutableMapOf<String, Any>(
            "id" to regId,
            "tournamentId" to tournament.id,
            "userId" to uid,
            "playerDisplayName" to playerDisplayName,
            "inGameId" to inGameId.trim(),
            "inGameName" to inGameName.trim(),
            "status" to "confirmed",
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        if (!teamName.isNullOrBlank()) regPayload["teamName"] = teamName.trim()
        if (!teammates.isNullOrBlank()) regPayload["teammates"] = teammates.trim()

        val txId = "tx_entry_${System.currentTimeMillis()}"
        val txPayload = if (entryFee > 0) {
            mapOf(
                "id" to txId,
                "userId" to uid,
                "type" to "MATCH_ENTRY",
                "amount" to -entryFee,
                "referenceId" to tournament.id,
                "referenceTitle" to tournament.title,
                "status" to "COMPLETED",
                "idempotencyKey" to "entry_${tournament.id}_${uid}",
                "description" to "Entry deduction for ${tournament.title} (${tournament.format})",
                "createdAt" to FieldValue.serverTimestamp()
            )
        } else null

        // Execute batch write atomically
        db.runBatch { batch ->
            batch.set(regRef, regPayload)
            batch.update(
                tourneyRef,
                mapOf(
                    "filledSlots" to FieldValue.increment(1),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            )
            if (entryFee > 0 && txPayload != null) {
                batch.update(
                    walletRef,
                    mapOf(
                        "coins" to FieldValue.increment(-entryFee.toLong()),
                        "totalSpent" to FieldValue.increment(entryFee.toLong()),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                )
                batch.set(walletRef.collection("transactions").document(txId), txPayload)
            }
        }.await()

        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "registrations")
        Result.failure(e)
    }
}

    // Observe Room Details (Only available when roomReleased == true and user is registered or admin)
    fun observeRoomDetails(tournamentId: String): Flow<RoomDetails?> = flow {
        val path = "tournaments/$tournamentId/room/details"
        emitAll(
            db.collection("tournaments").document(tournamentId)
                .collection("room").document("details")
                .snapshots()
                .map { snapshot -> snapshot.toObject(RoomDetails::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.GET, path)
                    emit(null)
                }
        )
    }

    // Observe announcements
    fun observeAnnouncements(): Flow<List<Announcement>> = flow {
        val path = "announcements"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(Announcement::class.java) }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // Observe Leaderboard
    fun observeLeaderboard(): Flow<List<LeaderboardEntry>> = flow {
        val path = "leaderboards"
        emitAll(
            db.collection(path)
                .snapshots()
                .map { snapshot -> snapshot.toObjects(LeaderboardEntry::class.java).sortedBy { it.rank } }
                .catch { error ->
                    if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                    throw error
                }
        )
    }

    // Admin Operations
    suspend fun createTournament(tournament: Tournament): Result<String> = try {
        val tourneyId = if (tournament.id.isNotBlank()) tournament.id else "t_${System.currentTimeMillis()}"
        val payload = mapOf(
            "id" to tourneyId,
            "title" to tournament.title,
            "description" to (tournament.description ?: "Official Akshar Tournament match"),
            "rules" to (tournament.rules ?: "Default Free Fire competitive guidelines apply. Emulators prohibited. No teaming or hacks."),
            "gameTitle" to tournament.gameTitle,
            "format" to tournament.format,
            "map" to tournament.map,
            "maxSlots" to tournament.maxSlots,
            "filledSlots" to 0,
            "entryFee" to tournament.entryFee,
            "prizePool" to tournament.prizePool,
            "firstPrize" to tournament.firstPrize,
            "secondPrize" to tournament.secondPrize,
            "thirdPrize" to tournament.thirdPrize,
            "perKillPrize" to tournament.perKillPrize,
            "status" to tournament.status,
            "roomReleased" to false,
            "scheduledAt" to (tournament.scheduledAt ?: Timestamp(Date(System.currentTimeMillis() + 86400000L))),
            "registrationEnd" to (tournament.registrationEnd ?: Timestamp(Date(System.currentTimeMillis() + 82800000L))),
            "createdAt" to FieldValue.serverTimestamp(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("tournaments").document(tourneyId).set(payload).await()
        Result.success(tourneyId)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "tournaments")
        Result.failure(e)
    }

    suspend fun releaseRoomDetails(
        tournamentId: String,
        roomId: String,
        roomPassword: String,
        instructions: String
    ): Result<Unit> = try {
        val roomRef = db.collection("tournaments").document(tournamentId)
            .collection("room").document("details")
        
        val payload = mapOf(
            "roomId" to roomId.trim(),
            "roomPassword" to roomPassword.trim(),
            "instructions" to instructions.trim(),
            "updatedAt" to FieldValue.serverTimestamp()
        )
        roomRef.set(payload).await()

        db.collection("tournaments").document(tournamentId).update(
            mapOf(
                "roomReleased" to true,
                "status" to "live",
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()

        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "tournaments/$tournamentId/room/details")
        Result.failure(e)
    }

    suspend fun updateTournamentStatus(tournamentId: String, status: String, winnerName: String? = null): Result<Unit> = try {
        val payload = mutableMapOf<String, Any>(
            "status" to status,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        winnerName?.let { payload["winnerName"] = it }
        db.collection("tournaments").document(tournamentId).update(payload).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.UPDATE, "tournaments/$tournamentId")
        Result.failure(e)
    }

    suspend fun createAnnouncement(title: String, content: String, tag: String): Result<Unit> = try {
        val id = "ann_${System.currentTimeMillis()}"
        val payload = mapOf(
            "id" to id,
            "title" to title.trim(),
            "content" to content.trim(),
            "tag" to tag.trim(),
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("announcements").document(id).set(payload).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.CREATE, "announcements")
        Result.failure(e)
    }

    suspend fun publishLeaderboardEntry(entry: LeaderboardEntry): Result<Unit> = try {
        val id = if (entry.id.isNotBlank()) entry.id else "rank_${entry.rank}"
        val payload = mapOf(
            "id" to id,
            "playerName" to entry.playerName,
            "inGameId" to entry.inGameId,
            "matchesPlayed" to entry.matchesPlayed,
            "kills" to entry.kills,
            "wins" to entry.wins,
            "totalPoints" to entry.totalPoints,
            "rank" to entry.rank,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        db.collection("leaderboards").document(id).set(payload).await()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        handleFirestoreError(e, OperationType.WRITE, "leaderboards")
        Result.failure(e)
    }

    // Seed Demo Data for Testing
    suspend fun seedInitialData(): Result<Unit> = seedDemoData()

    suspend fun seedDemoData(): Result<Unit> = try {
        val now = System.currentTimeMillis()
        val demoTournaments = listOf(
            Tournament(
                id = "tourney_grand_clash",
                title = "Akshar Grand Clash: Bermuda Squad",
                description = "High octane 4v4 squad battle royale on Bermuda map. Free entry, ₹10,000 prize pool celebration cup!",
                rules = "Strictly mobile players only. No hacks, no teaming. Room ID shared 15 mins prior.",
                gameTitle = "Free Fire MAX",
                format = "Squad",
                map = "Bermuda",
                maxSlots = 12,
                filledSlots = 4,
                entryFee = 0,
                prizePool = 10000,
                firstPrize = 5000,
                secondPrize = 3000,
                thirdPrize = 1500,
                perKillPrize = 50,
                status = "upcoming",
                roomReleased = false,
                scheduledAt = Timestamp(Date(now + 3600000L * 4)),
                registrationEnd = Timestamp(Date(now + 3600000L * 3))
            ),
            Tournament(
                id = "tourney_solo_headhunters",
                title = "Solo Headhunters: Purgatory",
                description = "Every player for themselves! 48 players clash for glory on Purgatory.",
                rules = "Solo survival. Points awarded per kill + top 10 placement bonus.",
                gameTitle = "Free Fire MAX",
                format = "Solo",
                map = "Purgatory",
                maxSlots = 48,
                filledSlots = 32,
                entryFee = 0,
                prizePool = 5000,
                firstPrize = 2500,
                secondPrize = 1500,
                thirdPrize = 700,
                perKillPrize = 30,
                status = "upcoming",
                roomReleased = false,
                scheduledAt = Timestamp(Date(now + 3600000L * 8)),
                registrationEnd = Timestamp(Date(now + 3600000L * 7))
            ),
            Tournament(
                id = "tourney_duo_kalahari",
                title = "Desert Duo Showdown: Kalahari",
                description = "Partner up and conquer the desert terrain of Kalahari.",
                rules = "Duo teams. Both players must register with valid Free Fire UIDs.",
                gameTitle = "Free Fire MAX",
                format = "Duo",
                map = "Kalahari",
                maxSlots = 24,
                filledSlots = 18,
                entryFee = 0,
                prizePool = 6000,
                firstPrize = 3000,
                secondPrize = 2000,
                thirdPrize = 1000,
                perKillPrize = 40,
                status = "upcoming",
                roomReleased = false,
                scheduledAt = Timestamp(Date(now + 3600000L * 24)),
                registrationEnd = Timestamp(Date(now + 3600000L * 22))
            )
        )

        for (t in demoTournaments) {
            createTournament(t)
        }

        // Demo Announcements
        createAnnouncement(
            title = "Welcome to Akshar Tournament Season 1!",
            content = "Registration is now live for all Bermuda and Purgatory daily tournaments. Ensure your Free Fire UID is updated in Profile.",
            tag = "Notice"
        )
        createAnnouncement(
            title = "Fair Play & Anti-Cheat Policy",
            content = "Use of third party scripts, emulators, or macro triggers will result in permanent ban and forfeiture of points.",
            tag = "Fair Play"
        )

        // Demo Leaderboard
        val demoLeaderboard = listOf(
            LeaderboardEntry(id = "rank_1", playerName = "ViperX_FF", inGameId = "284910291", matchesPlayed = 15, kills = 68, wins = 6, totalPoints = 340, rank = 1),
            LeaderboardEntry(id = "rank_2", playerName = "ShadowKiller99", inGameId = "481029103", matchesPlayed = 14, kills = 54, wins = 5, totalPoints = 295, rank = 2),
            LeaderboardEntry(id = "rank_3", playerName = "Aura_Sniper", inGameId = "192830192", matchesPlayed = 12, kills = 49, wins = 4, totalPoints = 260, rank = 3),
            LeaderboardEntry(id = "rank_4", playerName = "Toxic_Hunter", inGameId = "918273645", matchesPlayed = 11, kills = 41, wins = 3, totalPoints = 210, rank = 4),
            LeaderboardEntry(id = "rank_5", playerName = "PhoenixGod", inGameId = "381920192", matchesPlayed = 10, kills = 38, wins = 3, totalPoints = 195, rank = 5)
        )

        for (entry in demoLeaderboard) {
            publishLeaderboardEntry(entry)
        }

        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
