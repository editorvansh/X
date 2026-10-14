package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.UserProfile
import com.example.data.repository.UserRepository
import com.example.util.AppLanguage
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

sealed interface AuthUiState {
    data object Initial : AuthUiState
    data object Loading : AuthUiState
    data class Authenticated(val user: FirebaseUser, val profile: UserProfile?, val isAdmin: Boolean) : AuthUiState
    data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth = Firebase.auth
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin.asStateFlow()

    // Forgot Password & OTP verification state
    private val _generatedOtp = MutableStateFlow<String?>(null)
    val generatedOtp: StateFlow<String?> = _generatedOtp.asStateFlow()

    private val _otpSentEmail = MutableStateFlow<String?>(null)
    val otpSentEmail: StateFlow<String?> = _otpSentEmail.asStateFlow()

    private val _otpVerified = MutableStateFlow(false)
    val otpVerified: StateFlow<Boolean> = _otpVerified.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                observeUser(user)
            } else {
                _uiState.value = AuthUiState.Initial
                _isAdmin.value = false
            }
        }
    }

    fun toggleLanguage() {
        _language.value = if (_language.value == AppLanguage.ENGLISH) AppLanguage.HINDI else AppLanguage.ENGLISH
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    private fun observeUser(user: FirebaseUser) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            userRepository.createOrUpdateProfileFromAuth(user)
            
            // Check admin status
            val adminCheck = userRepository.checkAdmin(user.uid, user.email)
            _isAdmin.value = adminCheck

            userRepository.observeUserProfile(user.uid).collect { profile ->
                _uiState.value = AuthUiState.Authenticated(user, profile, _isAdmin.value)
            }
        }
    }

    fun signInWithGoogle(context: Context, activity: Activity) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val serverClientId = context.getString(R.string.default_web_client_id)
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetSignInWithGoogleOption.Builder(serverClientId)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential

                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                    auth.signInWithCredential(authCredential).await()
                } else {
                    _uiState.value = AuthUiState.Error("Unexpected credential format received.")
                }
            } catch (e: GetCredentialCancellationException) {
                _uiState.value = AuthUiState.Initial
            } catch (e: GetCredentialException) {
                _uiState.value = AuthUiState.Error("Google Sign-In failed: ${e.message ?: "Could not complete sign in."}")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Authentication error occurred.")
            }
        }
    }

    fun signInWithEmail(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email and password.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                auth.signInWithEmailAndPassword(email.trim(), pass.trim()).await()
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Invalid email or password.")
            }
        }
    }

    fun signUpWithEmail(
        displayName: String,
        email: String,
        pass: String,
        inGameId: String,
        inGameName: String
    ) {
        if (email.isBlank() || pass.length < 6) {
            _uiState.value = AuthUiState.Error("Please enter a valid email and password (minimum 6 characters).")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val authResult = auth.createUserWithEmailAndPassword(email.trim(), pass.trim()).await()
                val user = authResult.user
                if (user != null) {
                    // Update user profile display name
                    user.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName.trim())
                            .build()
                    ).await()

                    // Update in Firestore
                    userRepository.createOrUpdateProfileFromAuth(user)
                    if (inGameId.isNotBlank() && inGameName.isNotBlank()) {
                        userRepository.updateGamerProfile(user.uid, inGameName.trim(), inGameId.trim())
                    }
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Registration failed.")
            }
        }
    }

    fun sendForgotPasswordReset(email: String, onResult: (Boolean, String?, String?) -> Unit) {
        if (email.isBlank() || !email.contains("@")) {
            onResult(false, null, "Please enter a valid email address.")
            return
        }

        viewModelScope.launch {
            try {
                // Generate a 6-digit OTP code
                val otp = (100000 + Random.nextInt(900000)).toString()
                _generatedOtp.value = otp
                _otpSentEmail.value = email.trim()
                _otpVerified.value = false

                // Send official Firebase password reset email
                auth.sendPasswordResetEmail(email.trim()).await()

                onResult(true, otp, "Password reset instructions & OTP sent to ${email.trim()}!")
            } catch (e: Exception) {
                onResult(false, null, e.message ?: "Failed to send reset email.")
            }
        }
    }

    fun verifyOtp(enteredOtp: String): Boolean {
        val currentOtp = _generatedOtp.value
        val isMatch = currentOtp != null && enteredOtp.trim() == currentOtp
        if (isMatch) {
            _otpVerified.value = true
        }
        return isMatch
    }

    fun resetOtpState() {
        _generatedOtp.value = null
        _otpSentEmail.value = null
        _otpVerified.value = false
    }

    fun updateGamerDetails(inGameName: String, freeFireUid: String, onDone: (Boolean, String?) -> Unit) {
        val user = auth.currentUser ?: return
        viewModelScope.launch {
            val result = userRepository.updateGamerProfile(user.uid, inGameName, freeFireUid)
            if (result.isSuccess) {
                onDone(true, null)
            } else {
                onDone(false, result.exceptionOrNull()?.message ?: "Failed to update profile")
            }
        }
    }

    fun signOut() {
        auth.signOut()
        _uiState.value = AuthUiState.Initial
    }
}

class AuthViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(UserRepository(context)) as T
    }
}
