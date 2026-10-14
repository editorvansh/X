package com.example.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class AppLanguage {
    ENGLISH,
    HINDI
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ENGLISH }

object AppStrings {
    fun appTitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अक्षर टूर्नामेंट" else "Akshar Tournament"
    fun esportsSlogan(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "फ्री फायर ईस्पोर्ट्स प्लेटफॉर्म" else "Free Fire Esports Platform"
    
    // Navigation
    fun navHome(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "होम" else "Home"
    fun navTournaments(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "टूर्नामेंट्स" else "Tournaments"
    fun navMyMatches(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "मेरे मैच" else "My Matches"
    fun navLeaderboard(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लीडरबोर्ड" else "Leaderboard"
    fun navProfile(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "प्रोफाइल" else "Profile"
    fun navAdmin(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "एडमिन पैनल" else "Admin Panel"

    // Auth
    fun welcomeHeading(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अक्षर टूर्नामेंट में आपका स्वागत है" else "Welcome to Akshar Tournament"
    fun welcomeSub(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "भारत का प्रीमियर फ्री फायर ईस्पोर्ट्स एरीना" else "India's Premier Free Fire Esports Arena"
    fun signInWithGoogle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "गूगल से साइन इन करें" else "Continue with Google"
    fun loginSubtitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "टूर्नामेंट में भाग लेने के लिए सुरक्षित लॉगिन" else "Secure authentication to register & compete in live tournaments"
    fun emailLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "ईमेल आईडी" else "Email Address"
    fun passwordLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पासवर्ड" else "Password"
    fun confirmPasswordLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पासवर्ड की पुष्टि करें" else "Confirm Password"
    fun forgotPassword(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पासवर्ड भूल गए?" else "Forgot Password?"
    fun signInTab(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लॉगिन करें" else "Sign In"
    fun signUpTab(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रजिस्टर करें" else "Register"
    fun sendOtpReset(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "ओटीपी व रीसेट लिंक भेजें" else "Send Reset Link & OTP"
    fun otpLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "6-अंकों का वेरिफिकेशन ओटीपी" else "6-Digit Verification OTP"
    fun verifyOtp(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "ओटीपी सत्यापित करें" else "Verify OTP Code"
    fun resetSentNotice(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पासवर्ड रीसेट लिंक आपके ईमेल पर भेजा गया है।" else "Password reset link and OTP sent to your registered email."
    fun backToSignIn(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लॉगिन पर वापस जाएं" else "Back to Sign In"
    fun fullNameLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पूरा नाम" else "Full Name"
    fun orDivider(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "या" else "OR"

    // Home
    fun welcomePlayer(name: String, lang: AppLanguage) = if (lang == AppLanguage.HINDI) "स्वागत है, $name!" else "Welcome, $name!"
    fun featuredTournament(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "फीचर्ड टूर्नामेंट" else "Featured Championship"
    fun upcomingTournaments(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "आगामी टूर्नामेंट्स" else "Upcoming Tournaments"
    fun liveMatches(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लाइव मैच" else "Live Matches"
    fun closingSoon(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रजिस्ट्रेशन जल्द समाप्त" else "Registration Closing Soon"
    fun announcements(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "महत्वपूर्ण सूचनाएं" else "Announcements"
    fun quickMyMatches(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "मेरे मैच देखें" else "View My Matches"
    fun viewDetails(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "विवरण देखें" else "View Details"
    fun registerNow(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अभी रजिस्टर करें" else "Register Now"
    fun registeredBadge(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रजिस्टर्ड" else "Registered"
    fun fullSlots(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "फुल स्लॉट" else "Slots Full"

    // Details & Registration
    fun tournamentDetails(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "टूर्नामेंट विवरण" else "Tournament Details"
    fun matchRules(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "मैच नियम व शर्तें" else "Match Rules & Terms"
    fun prizeDistribution(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "पुरस्कार वितरण" else "Prize Distribution"
    fun map(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नक्शा (मैप)" else "Map"
    fun format(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "प्रारूप" else "Format"
    fun entryFee(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "प्रवेश शुल्क" else "Entry Fee"
    fun freeEntry(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "निःशुल्क (FREE)" else "FREE"
    fun prizePool(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कुल पुरस्कार" else "Prize Pool"
    fun slots(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "स्लॉट्स" else "Slots"
    fun inGameIdLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "फ्री फायर प्लेयर यूआईडी (UID)" else "Free Fire Player UID"
    fun inGameNameLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "इन-गेम नाम (IGN)" else "In-Game Name (IGN)"
    fun teamNameLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "टीम का नाम" else "Team Name"
    fun teammatesLabel(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "साथी खिलाड़ियों के नाम/UID" else "Teammates (Name/UID)"
    fun confirmRegistration(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रजिस्ट्रेशन कन्फर्म करें" else "Confirm Registration"
    fun registrationSuccess(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रजिस्ट्रेशन सफल रहा!" else "Registration Successful!"

    // My Matches
    fun roomDetails(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कस्टम रूम विवरण" else "Custom Room Details"
    fun roomId(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रूम आईडी" else "Room ID"
    fun roomPassword(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रूम पासवर्ड" else "Room Password"
    fun roomNotReleased(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रूम विवरण मैच से 15 मिनट पहले जारी किया जाएगा।" else "Room details will be released 15 minutes before match time."
    fun noRegisteredMatches(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "आपने अभी तक किसी टूर्नामेंट में भाग नहीं लिया है।" else "You haven't registered for any tournament yet."

    // Leaderboard
    fun rank(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रैंक" else "Rank"
    fun kills(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "किल्स" else "Kills"
    fun wins(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "जीत" else "Wins"
    fun points(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अंक" else "Points"

    // Profile
    fun myProfile(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "मेरी प्रोफाइल" else "My Profile"
    fun saveProfile(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "प्रोफाइल सेव करें" else "Save Profile"
    fun logout(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लॉगआउट" else "Logout"
    fun deleteAccount(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "अकाउंट डिलीट करें" else "Delete Account"
    fun termsPolicy(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नियम एवं गोपनीयता नीति" else "Terms & Fair Play Policy"
    fun language(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "भाषा बदलें" else "Language"
    fun freeTournamentNotice(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "नोट: यह प्लेटफॉर्म वर्तमान में फ्री-टू-प्ले ईस्पोर्ट्स प्रैक्टिस और कम्युनिटी टूर्नामेंट्स के लिए है।" else "Note: This platform currently hosts free-to-play esports practice and community tournaments."

    // Wallet
    fun navWallet(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "वॉलेट" else "Wallet"
    fun availableCoins(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "उपलब्ध कॉइन्स" else "Available Coins"
    fun addCoins(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कॉइन्स जोड़ें" else "Add Coins"
    fun coinBalance(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कॉइन बैलेंस" else "Coin Balance"
    fun referenceValueNotice(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "1 कॉइन = ₹1 संदर्भ मूल्य (INR Reference Value)" else "1 Coin = ₹1 INR Reference Value"
    fun referenceValueSubtitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "यह केवल संदर्भ मूल्य है और इसे नकद में नहीं बदला जा सकता।" else "Reference conversion only. Coins are not cash-redeemable."
    fun transactionHistory(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "लेन-देन इतिहास" else "Transaction History"
    fun noTransactions(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कोई लेन-देन नहीं मिला।" else "No transactions recorded yet."
    fun allTransactions(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "सभी" else "All"
    fun creditsFilter(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "क्रेडिट (+)" else "Credits (+)"
    fun debitsFilter(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "डेबिट (-)" else "Debits (-)"
    fun topUpModalTitle(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "कॉइन्स टॉप-अप पैक चुनें" else "Select Coin Package"
    fun devModeNotice(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "डेवलपमेंट मोड: टेस्ट लेन-देन सक्रिय है। कोई वास्तविक शुल्क नहीं कटेगा।" else "Development Mode: Test transactions active. No real money charged."
    fun matchEntryDeduction(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "मैच एंट्री शुल्क" else "Match Entry Fee"
    fun refundRecord(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "रिफंड क्रेडिट" else "Refund Credit"
    fun testTopUpCredit(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "वॉलेट टॉप-अप" else "Wallet Top-Up"
    fun welcomeBonus(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "वेलकम बोनस कॉइन्स" else "Welcome Bonus Coins"
    fun instantAdd(lang: AppLanguage) = if (lang == AppLanguage.HINDI) "तुरंत जोड़ें" else "Add Now"
}
