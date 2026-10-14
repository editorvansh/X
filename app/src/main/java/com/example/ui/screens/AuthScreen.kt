package com.example.ui.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BooyahGold
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EsportsBackground
import com.example.ui.theme.EsportsBorder
import com.example.ui.theme.EsportsCardBg
import com.example.ui.theme.FieryOrange
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBright
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.AuthUiState
import com.example.util.AppLanguage
import com.example.util.AppStrings

@Composable
fun AuthScreen(
    uiState: AuthUiState,
    language: AppLanguage,
    onToggleLanguage: () -> Unit,
    onSignInClick: (Activity) -> Unit,
    onEmailSignIn: (email: String, pass: String) -> Unit,
    onEmailSignUp: (name: String, email: String, pass: String, inGameId: String, inGameName: String) -> Unit,
    onForgotPassword: (email: String, onResult: (Boolean, String?, String?) -> Unit) -> Unit,
    onVerifyOtp: (enteredOtp: String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as? Activity

    var authTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Sign Up

    // Sign In inputs
    var loginEmail by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var loginPasswordVisible by remember { mutableStateOf(false) }

    // Sign Up inputs
    var registerName by remember { mutableStateOf("") }
    var registerEmail by remember { mutableStateOf("") }
    var registerPassword by remember { mutableStateOf("") }
    var registerConfirmPassword by remember { mutableStateOf("") }
    var registerUid by remember { mutableStateOf("") }
    var registerIgn by remember { mutableStateOf("") }
    var registerPasswordVisible by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    // Forgot Password & OTP Dialog State
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var otpSentCode by remember { mutableStateOf<String?>(null) }
    var enteredOtp by remember { mutableStateOf("") }
    var otpVerifiedSuccess by remember { mutableStateOf(false) }
    var forgotStatusMessage by remember { mutableStateOf<String?>(null) }
    var forgotLoading by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0F0E22),
                        EsportsBackground,
                        Color(0xFF06060C)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Language Toggle Top Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onToggleLanguage,
                    modifier = Modifier.testTag("auth_language_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "Language",
                        tint = NeonPurpleBright,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.HINDI) "English" else "हिंदी",
                        color = TextWhite,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // Esports Crest Logo
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        2.dp,
                        Brush.sweepGradient(listOf(NeonPurpleBright, FieryOrange, ElectricBlue, NeonPurpleBright)),
                        RoundedCornerShape(20.dp)
                    )
                    .padding(3.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon),
                    contentDescription = "Akshar Tournament Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(17.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // App Heading
            Text(
                text = AppStrings.appTitle(language).uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Black,
                    color = TextWhite,
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = AppStrings.welcomeSub(language),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = ElectricBlue,
                    fontWeight = FontWeight.SemiBold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, EsportsBorder, RoundedCornerShape(18.dp)),
                colors = CardDefaults.cardColors(containerColor = EsportsCardBg)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Sign In / Register Tabs
                    TabRow(
                        selectedTabIndex = authTab,
                        containerColor = Color(0xFF131226),
                        contentColor = NeonPurpleBright,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[authTab]),
                                color = NeonPurpleBright
                            )
                        }
                    ) {
                        Tab(
                            selected = authTab == 0,
                            onClick = {
                                focusManager.clearFocus()
                                authTab = 0
                                localError = null
                            },
                            text = {
                                Text(
                                    text = AppStrings.signInTab(language),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (authTab == 0) NeonPurpleBright else TextMuted
                                    )
                                )
                            },
                            modifier = Modifier.testTag("tab_sign_in")
                        )
                        Tab(
                            selected = authTab == 1,
                            onClick = {
                                focusManager.clearFocus()
                                authTab = 1
                                localError = null
                            },
                            text = {
                                Text(
                                    text = AppStrings.signUpTab(language),
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (authTab == 1) NeonPurpleBright else TextMuted
                                    )
                                )
                            },
                            modifier = Modifier.testTag("tab_register")
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Error banners
                        if (localError != null) {
                            AuthErrorBanner(message = localError!!)
                        } else if (uiState is AuthUiState.Error) {
                            AuthErrorBanner(message = uiState.message)
                        }

                        if (authTab == 0) {
                            // --- SIGN IN FORM ---
                            OutlinedTextField(
                                value = loginEmail,
                                onValueChange = {
                                    loginEmail = it
                                    localError = null
                                },
                                label = { Text(AppStrings.emailLabel(language)) },
                                placeholder = { Text("player@esports.com") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ElectricBlue)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_email"),
                                colors = authTextFieldColors()
                            )

                            OutlinedTextField(
                                value = loginPassword,
                                onValueChange = {
                                    loginPassword = it
                                    localError = null
                                },
                                label = { Text(AppStrings.passwordLabel(language)) },
                                placeholder = { Text("••••••••") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonPurpleBright)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { loginPasswordVisible = !loginPasswordVisible }) {
                                        Icon(
                                            imageVector = if (loginPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password",
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (loginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_login_password"),
                                colors = authTextFieldColors()
                            )

                            // Forgot Password Link
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = AppStrings.forgotPassword(language),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ElectricBlue,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .clickable {
                                            forgotEmail = loginEmail.trim()
                                            otpSentCode = null
                                            enteredOtp = ""
                                            otpVerifiedSuccess = false
                                            forgotStatusMessage = null
                                            showForgotDialog = true
                                        }
                                        .padding(vertical = 4.dp)
                                        .testTag("forgot_password_link")
                                )
                            }

                            // Sign In Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    if (loginEmail.isBlank() || loginPassword.isBlank()) {
                                        localError = "Please enter your email and password."
                                        return@Button
                                    }
                                    onEmailSignIn(loginEmail.trim(), loginPassword.trim())
                                },
                                enabled = uiState !is AuthUiState.Loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("email_sign_in_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (uiState is AuthUiState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextWhite)
                                } else {
                                    Text(
                                        text = AppStrings.signInTab(language),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }

                        } else {
                            // --- SIGN UP / REGISTER FORM ---
                            OutlinedTextField(
                                value = registerName,
                                onValueChange = {
                                    registerName = it
                                    localError = null
                                },
                                label = { Text(AppStrings.fullNameLabel(language)) },
                                placeholder = { Text("Rahul Sharma") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ElectricBlue)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_register_name"),
                                colors = authTextFieldColors()
                            )

                            OutlinedTextField(
                                value = registerEmail,
                                onValueChange = {
                                    registerEmail = it
                                    localError = null
                                },
                                label = { Text(AppStrings.emailLabel(language)) },
                                placeholder = { Text("player@esports.com") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ElectricBlue)
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_register_email"),
                                colors = authTextFieldColors()
                            )

                            OutlinedTextField(
                                value = registerPassword,
                                onValueChange = {
                                    registerPassword = it
                                    localError = null
                                },
                                label = { Text(AppStrings.passwordLabel(language)) },
                                placeholder = { Text("Minimum 6 characters") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonPurpleBright)
                                },
                                trailingIcon = {
                                    IconButton(onClick = { registerPasswordVisible = !registerPasswordVisible }) {
                                        Icon(
                                            imageVector = if (registerPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle password",
                                            tint = TextMuted
                                        )
                                    }
                                },
                                visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_register_password"),
                                colors = authTextFieldColors()
                            )

                            OutlinedTextField(
                                value = registerConfirmPassword,
                                onValueChange = {
                                    registerConfirmPassword = it
                                    localError = null
                                },
                                label = { Text(AppStrings.confirmPasswordLabel(language)) },
                                placeholder = { Text("Confirm password") },
                                leadingIcon = {
                                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = NeonPurpleBright)
                                },
                                visualTransformation = if (registerPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_register_confirm_password"),
                                colors = authTextFieldColors()
                            )

                            // Optional Free Fire Details
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = registerUid,
                                        onValueChange = { registerUid = it },
                                        label = { Text("Free Fire UID") },
                                        placeholder = { Text("e.g. 2948102") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = FieryOrange, modifier = Modifier.size(16.dp))
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = authTextFieldColors()
                                    )
                                }
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = registerIgn,
                                        onValueChange = { registerIgn = it },
                                        label = { Text("In-Game Name") },
                                        placeholder = { Text("e.g. BooyahKing") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = BooyahGold, modifier = Modifier.size(16.dp))
                                        },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = authTextFieldColors()
                                    )
                                }
                            }

                            // Register Button
                            Button(
                                onClick = {
                                    focusManager.clearFocus()
                                    if (registerName.isBlank() || registerEmail.isBlank() || registerPassword.isBlank()) {
                                        localError = "Please fill in your name, email, and password."
                                        return@Button
                                    }
                                    if (registerPassword.length < 6) {
                                        localError = "Password must be at least 6 characters long."
                                        return@Button
                                    }
                                    if (registerPassword != registerConfirmPassword) {
                                        localError = "Passwords do not match."
                                        return@Button
                                    }
                                    onEmailSignUp(
                                        registerName.trim(),
                                        registerEmail.trim(),
                                        registerPassword.trim(),
                                        registerUid.trim(),
                                        registerIgn.trim()
                                    )
                                },
                                enabled = uiState !is AuthUiState.Loading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("email_sign_up_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonPurpleBright),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                if (uiState is AuthUiState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = TextWhite)
                                } else {
                                    Text(
                                        text = AppStrings.signUpTab(language),
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        // Divider OR
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f), color = EsportsBorder)
                            Text(
                                text = "  ${AppStrings.orDivider(language)}  ",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f), color = EsportsBorder)
                        }

                        // Google Sign-In Button
                        Button(
                            onClick = {
                                if (activity != null) {
                                    onSignInClick(activity)
                                }
                            },
                            enabled = uiState !is AuthUiState.Loading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("google_sign_in_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F1D38)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EsportsBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Google Sign In",
                                    tint = BooyahGold,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = AppStrings.signInWithGoogle(language),
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = AppStrings.loginSubtitle(language),
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // --- FORGOT PASSWORD & OTP VERIFICATION DIALOG ---
    if (showForgotDialog) {
        AlertDialog(
            onDismissRequest = { showForgotDialog = false },
            containerColor = EsportsCardBg,
            shape = RoundedCornerShape(18.dp),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.LockReset, contentDescription = null, tint = ElectricBlue)
                    Text(
                        text = AppStrings.forgotPassword(language),
                        style = MaterialTheme.typography.titleLarge.copy(color = TextWhite, fontWeight = FontWeight.Bold)
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
                    Text(
                        text = "Enter your registered email address to receive password reset instructions and a 6-digit OTP verification code.",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )

                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text(AppStrings.emailLabel(language)) },
                        placeholder = { Text("player@esports.com") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ElectricBlue)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_forgot_email"),
                        colors = authTextFieldColors()
                    )

                    // Send Button
                    Button(
                        onClick = {
                            forgotLoading = true
                            forgotStatusMessage = null
                            onForgotPassword(forgotEmail.trim()) { success, otp, msg ->
                                forgotLoading = false
                                forgotStatusMessage = msg
                                if (success && otp != null) {
                                    otpSentCode = otp
                                }
                            }
                        },
                        enabled = !forgotLoading && forgotEmail.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("send_otp_button")
                    ) {
                        if (forgotLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = TextDark)
                        } else {
                            Text(
                                text = AppStrings.sendOtpReset(language),
                                color = TextDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // OTP Code Section when sent
                    if (otpSentCode != null) {
                        Spacer(modifier = Modifier.height(4.dp))

                        // Simulation / active OTP badge for user convenience
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(NeonPurple.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                .border(1.dp, NeonPurpleBright, RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Verification OTP Dispatched",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeonPurpleBright, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Your OTP code is: $otpSentCode",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = TextWhite,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                )
                            }
                        }

                        // OTP Input
                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = {
                                if (it.length <= 6) enteredOtp = it
                            },
                            label = { Text(AppStrings.otpLabel(language)) },
                            placeholder = { Text("e.g. $otpSentCode") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = BooyahGold)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_otp_code"),
                            colors = authTextFieldColors()
                        )

                        Button(
                            onClick = {
                                val verified = onVerifyOtp(enteredOtp)
                                if (verified) {
                                    otpVerifiedSuccess = true
                                    forgotStatusMessage = "OTP verified successfully! Check your inbox for the reset link to choose your new password."
                                } else {
                                    forgotStatusMessage = "Incorrect OTP code. Please enter the valid 6-digit code."
                                }
                            },
                            enabled = enteredOtp.length == 6,
                            colors = ButtonDefaults.buttonColors(containerColor = if (otpVerifiedSuccess) SuccessGreen else NeonPurpleBright),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verify_otp_button")
                        ) {
                            if (otpVerifiedSuccess) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = TextWhite)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "OTP Verified", color = TextWhite, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text(text = AppStrings.verifyOtp(language), color = TextWhite, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (forgotStatusMessage != null) {
                        Text(
                            text = forgotStatusMessage!!,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = if (otpVerifiedSuccess) SuccessGreen else FieryOrange
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showForgotDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262444))
                ) {
                    Text(text = AppStrings.backToSignIn(language), color = TextWhite)
                }
            }
        )
    }
}

@Composable
private fun AuthErrorBanner(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f), RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.error, RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextWhite,
    unfocusedTextColor = TextWhite,
    focusedBorderColor = NeonPurpleBright,
    unfocusedBorderColor = EsportsBorder,
    focusedLabelColor = NeonPurpleBright,
    unfocusedLabelColor = TextMuted,
    focusedContainerColor = Color(0xFF131224),
    unfocusedContainerColor = Color(0xFF131224)
)
