package com.example

import com.example.data.DefaultWheels
import com.example.localization.Localization
import com.example.model.AppLanguage
import com.example.model.RewardCategory
import com.example.model.parseCategoryFromLabel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HaynerSpinUnitTest {

    @Test
    fun defaultWheel_hasExactRequiredSegments() {
        val wheel = DefaultWheels.haynerDefaultWheel
        assertEquals(8, wheel.segments.size)

        val labels = wheel.segments.map { it.label }
        assertTrue(labels.contains("Try Again"))
        assertTrue(labels.contains("Bonus Spin"))
        assertTrue(labels.contains("Small Reward"))
        assertTrue(labels.contains("Mystery Reward"))
        assertTrue(labels.contains("Lucky Spin"))
        assertTrue(labels.contains("Special Prize"))
        assertTrue(labels.contains("No Prize"))
        assertTrue(labels.contains("Surprise Reward"))
    }

    @Test
    fun wheelAngleMath_correctlyCalculatesTargetSegment() {
        val segmentCount = 8
        val arcAngle = 360f / segmentCount

        for (targetIndex in 0 until segmentCount) {
            val segmentCenter = (targetIndex + 0.5f) * arcAngle
            val targetRemainder = ((270f - segmentCenter) % 360f + 360f) % 360f

            // When wheel is rotated by targetRemainder, pointer at 270 degrees
            val unrotatedPointer = ((270f - targetRemainder) % 360f + 360f) % 360f
            val calculatedIndex = (unrotatedPointer / arcAngle).toInt() % segmentCount

            assertEquals(targetIndex, calculatedIndex)
        }
    }

    @Test
    fun categoryDetection_matchesDemonstrationPrizes() {
        assertEquals(RewardCategory.TRY_AGAIN, parseCategoryFromLabel("Try Again"))
        assertEquals(RewardCategory.BONUS, parseCategoryFromLabel("Bonus Spin"))
        assertEquals(RewardCategory.NO_PRIZE, parseCategoryFromLabel("No Prize"))
        assertEquals(RewardCategory.CUSTOM, parseCategoryFromLabel("Pizza Night"))
    }

    @Test
    fun localization_providesEnglishAndSomali() {
        val spinEn = Localization.get("spin_now", AppLanguage.ENGLISH)
        val spinSo = Localization.get("spin_now", AppLanguage.SOMALI)

        assertEquals("SPIN NOW", spinEn)
        assertEquals("WAREEJI HADDA", spinSo)

        val homeEn = Localization.get("nav_home", AppLanguage.ENGLISH)
        val homeSo = Localization.get("nav_home", AppLanguage.SOMALI)

        assertEquals("Home", homeEn)
        assertEquals("Guriga", homeSo)
    }
}
