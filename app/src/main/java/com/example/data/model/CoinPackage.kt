package com.example.data.model

data class CoinPackage(
    val id: String,
    val coins: Int,
    val bonusCoins: Int = 0,
    val priceInr: Int,
    val label: String? = null,
    val isPopular: Boolean = false
) {
    val totalCoins: Int get() = coins + bonusCoins

    companion object {
        val DEFAULT_PACKAGES = listOf(
            CoinPackage(
                id = "pack_starter_20",
                coins = 20,
                bonusCoins = 0,
                priceInr = 20,
                label = "Starter Pack"
            ),
            CoinPackage(
                id = "pack_duo_50",
                coins = 50,
                bonusCoins = 5,
                priceInr = 50,
                label = "Duo Match Special",
                isPopular = true
            ),
            CoinPackage(
                id = "pack_squad_100",
                coins = 100,
                bonusCoins = 15,
                priceInr = 100,
                label = "+15% Extra Coins",
                isPopular = false
            ),
            CoinPackage(
                id = "pack_pro_250",
                coins = 250,
                bonusCoins = 50,
                priceInr = 250,
                label = "+20% Pro Booster"
            ),
            CoinPackage(
                id = "pack_champion_500",
                coins = 500,
                bonusCoins = 120,
                priceInr = 500,
                label = "Best Value Champion Pack"
            )
        )
    }
}
