package com.example.data

import com.example.model.RewardCategory
import com.example.model.SpinWheel
import com.example.model.WheelSegment
import java.util.UUID

object DefaultWheels {
    val HAYNER_DEFAULT_ID = "wheel_hayner_default"

    val haynerDefaultWheel = SpinWheel(
        id = HAYNER_DEFAULT_ID,
        name = "Hayner Spin Classic",
        description = "The signature demonstration prize wheel with 8 balanced lucky segments.",
        isCustom = false,
        themeId = "galaxy",
        segments = listOf(
            WheelSegment(
                id = "seg_1",
                label = "Try Again",
                colorHex = "#E11D48", // Ruby
                category = RewardCategory.TRY_AGAIN,
                iconName = "replay"
            ),
            WheelSegment(
                id = "seg_2",
                label = "Bonus Spin",
                colorHex = "#7C3AED", // Violet
                category = RewardCategory.BONUS,
                iconName = "auto_awesome"
            ),
            WheelSegment(
                id = "seg_3",
                label = "Small Reward",
                colorHex = "#0284C7", // Sky Blue
                category = RewardCategory.SMALL,
                iconName = "card_giftcard"
            ),
            WheelSegment(
                id = "seg_4",
                label = "Mystery Reward",
                colorHex = "#9333EA", // Purple
                category = RewardCategory.MYSTERY,
                iconName = "help"
            ),
            WheelSegment(
                id = "seg_5",
                label = "Lucky Spin",
                colorHex = "#059669", // Emerald
                category = RewardCategory.LUCKY,
                iconName = "stars"
            ),
            WheelSegment(
                id = "seg_6",
                label = "Special Prize",
                colorHex = "#D97706", // Amber
                category = RewardCategory.SPECIAL,
                iconName = "military_tech"
            ),
            WheelSegment(
                id = "seg_7",
                label = "No Prize",
                colorHex = "#64748B", // Slate
                category = RewardCategory.NO_PRIZE,
                iconName = "sentiment_dissatisfied"
            ),
            WheelSegment(
                id = "seg_8",
                label = "Surprise Reward",
                colorHex = "#DB2777", // Pink
                category = RewardCategory.SURPRISE,
                iconName = "celebration"
            )
        )
    )

    val decisionMakerWheel = SpinWheel(
        id = "wheel_decision_maker",
        name = "Quick Decision",
        description = "Let fate guide your daily choices.",
        isCustom = false,
        themeId = "neon",
        segments = listOf(
            WheelSegment(UUID.randomUUID().toString(), "Yes!", "#059669", RewardCategory.CUSTOM, "check"),
            WheelSegment(UUID.randomUUID().toString(), "No Way", "#E11D48", RewardCategory.CUSTOM, "close"),
            WheelSegment(UUID.randomUUID().toString(), "Maybe Later", "#D97706", RewardCategory.CUSTOM, "schedule"),
            WheelSegment(UUID.randomUUID().toString(), "Go For It", "#7C3AED", RewardCategory.CUSTOM, "flash_on"),
            WheelSegment(UUID.randomUUID().toString(), "Ask a Friend", "#0284C7", RewardCategory.CUSTOM, "group"),
            WheelSegment(UUID.randomUUID().toString(), "Sleep on It", "#64748B", RewardCategory.CUSTOM, "bed")
        )
    )

    val allDefaults = listOf(haynerDefaultWheel, decisionMakerWheel)
}
