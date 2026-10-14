package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Tournament
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class TournamentRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun observeTournaments_authenticated_emitsList() = runBlocking {
        signInTestUser(USER_EMAIL)
        val repository = TournamentRepository(firestore, auth)

        val tournaments = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeTournaments().first()
        }
        assertNotNull(tournaments)
    }

    @Test
    fun registerForTournament_authenticated_createsRegistration() = runBlocking {
        // First sign in admin to create a tournament
        signInTestUser(ADMIN_EMAIL)
        val adminRepo = TournamentRepository(firestore, auth)
        val tourneyId = "t_" + UUID.randomUUID().toString().replace("-", "").take(8)
        val tournament = Tournament(
            id = tourneyId,
            title = "Rules Test Clash",
            format = "Solo",
            map = "Bermuda",
            maxSlots = 48,
            filledSlots = 0,
            status = "upcoming",
            roomReleased = false
        )
        val createResult = adminRepo.createTournament(tournament)
        if (createResult.isFailure) {
            val err = createResult.exceptionOrNull()
            err?.printStackTrace()
            fail("Admin create tournament failed: ${err?.message}")
        }
        assertTrue("Admin create tournament should succeed", createResult.isSuccess)

        // Now sign in normal player and register
        signInTestUser(USER_EMAIL)
        auth.currentUser?.getIdToken(true)?.await()
        kotlinx.coroutines.delay(600)
        val playerRepo = TournamentRepository(firestore, auth)
        val regResult = playerRepo.registerForTournament(
            tournament = tournament,
            playerDisplayName = "TestPlayer",
            inGameId = "FF123456",
            inGameName = "ProGamer"
        )
        if (regResult.isFailure) {
            val err = regResult.exceptionOrNull()
            err?.printStackTrace()
            fail("Player registration failed: ${err?.message}")
        }
        assertTrue("Player registration should succeed", regResult.isSuccess)
    }

    @Test
    fun observeTournaments_unauthenticated_failsWithPermissionDenied() = runBlocking {
        auth.signOut()
        val repository = TournamentRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeTournaments().first()
            }
            fail("Expected FirebaseFirestoreException PERMISSION_DENIED")
        } catch (e: Exception) {
            val isPermissionDenied = e is FirebaseFirestoreException && 
                e.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
            val hasPermissionMessage = e.message?.contains("PERMISSION_DENIED", ignoreCase = true) == true
            assertTrue("Expected permission denied error: ${e.message}", isPermissionDenied || hasPermissionMessage)
        }
    }

    private companion object {
        const val ADMIN_EMAIL = "saritakumari9818422028@gmail.com"
        const val USER_EMAIL = "player_test@test.com"
        const val FLOW_TIMEOUT_MS = 5000L
    }
}
