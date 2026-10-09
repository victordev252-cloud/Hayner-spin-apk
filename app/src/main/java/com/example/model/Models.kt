package com.example.model

fun parseCategoryFromLabel(label: String): RewardCategory {
    val lower = label.lowercase()
    return when {
        lower.contains("try again") || lower.contains("isku day") -> RewardCategory.TRY_AGAIN
        lower.contains("bonus") || lower.contains("dheeraad") -> RewardCategory.BONUS
        lower.contains("small") || lower.contains("yar") -> RewardCategory.SMALL
        lower.contains("mystery") || lower.contains("qarsoodi") -> RewardCategory.MYSTERY
        lower.contains("lucky") || lower.contains("nasiib") -> RewardCategory.LUCKY
        lower.contains("special") || lower.contains("gaar") -> RewardCategory.SPECIAL
        lower.contains("surprise") || lower.contains("lama filaan") -> RewardCategory.SURPRISE
        lower.contains("no prize") || lower.contains("waxba") -> RewardCategory.NO_PRIZE
        else -> RewardCategory.CUSTOM
    }
}

data class WheelSegment(
    val id: String,
    val label: String,
    val colorHex: String,
    val category: RewardCategory = parseCategoryFromLabel(label),
    val iconName: String = "star",
    val weight: Int = 1
)

data class SpinWheel(
    val id: String,
    val name: String,
    val description: String = "",
    val segments: List<WheelSegment>,
    val isCustom: Boolean = false,
    val themeId: String = "galaxy",
    val createdAt: Long = System.currentTimeMillis()
)

data class SpinHistoryItem(
    val id: String,
    val wheelId: String,
    val wheelName: String,
    val segmentLabel: String,
    val category: RewardCategory,
    val colorHex: String,
    val timestampMillis: Long,
    val spinNumber: Int,
    val isPractice: Boolean = false
)

data class SpinResult(
    val segment: WheelSegment,
    val spinNumber: Int,
    val timestampMillis: Long = System.currentTimeMillis(),
    val wheelName: String,
    val isPractice: Boolean = false
)

data class DailyChallenge(
    val id: String,
    val titleKey: String,
    val descKey: String,
    val targetCount: Int,
    val currentCount: Int,
    val completed: Boolean = false,
    val claimed: Boolean = false,
    val rewardPoints: Int = 50
)

data class UserProfile(
    val displayName: String = "Hayner Champion",
    val avatarIndex: Int = 0,
    val totalSpins: Int = 0,
    val currentStreak: Int = 1,
    val bestStreak: Int = 1,
    val lastSpinDateString: String = "",
    val totalRewardPoints: Int = 100
)
