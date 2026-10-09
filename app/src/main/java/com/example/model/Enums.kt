package com.example.model

enum class AppThemeConfig(val title: String) {
    PURPLE_GALAXY("Purple Galaxy"),
    NEON_BLUE("Neon Blue"),
    ROYAL_GOLD("Royal Gold"),
    EMERALD_NIGHT("Emerald Night"),
    CRIMSON_NIGHT("Crimson Night"),
    MINIMAL_DARK("Minimal Dark")
}

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    SOMALI("so", "Af-Soomaali")
}

enum class SpinMode {
    STANDARD,
    PRACTICE,
    QUICK
}

enum class RewardCategory {
    TRY_AGAIN,
    BONUS,
    SMALL,
    MYSTERY,
    LUCKY,
    SPECIAL,
    SURPRISE,
    NO_PRIZE,
    CUSTOM
}

enum class ScreenDestination {
    SPLASH,
    ONBOARDING,
    HOME,
    MY_WHEELS,
    SPIN,
    HISTORY,
    PROFILE,
    WHEEL_EDITOR,
    SETTINGS
}
