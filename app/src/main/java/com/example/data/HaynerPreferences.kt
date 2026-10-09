package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppLanguage
import com.example.model.AppThemeConfig
import com.example.model.DailyChallenge
import com.example.model.RewardCategory
import com.example.model.SpinHistoryItem
import com.example.model.SpinWheel
import com.example.model.UserProfile
import com.example.model.WheelSegment
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HaynerPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("hayner_spin_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_ACTIVE_WHEEL_ID = "active_wheel_id"
        private const val KEY_CUSTOM_WHEELS = "custom_wheels_json"
        private const val KEY_SPIN_HISTORY = "spin_history_json"
        private const val KEY_USER_PROFILE = "user_profile_json"
        private const val KEY_CHALLENGES = "challenges_json"
        private const val KEY_CHALLENGES_DATE = "challenges_date"
        private const val KEY_APP_THEME = "app_theme"
        private const val KEY_APP_LANGUAGE = "app_language"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_ANIMATIONS_ENABLED = "animations_enabled"
    }

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var activeWheelId: String
        get() = prefs.getString(KEY_ACTIVE_WHEEL_ID, DefaultWheels.HAYNER_DEFAULT_ID) ?: DefaultWheels.HAYNER_DEFAULT_ID
        set(value) = prefs.edit().putString(KEY_ACTIVE_WHEEL_ID, value).apply()

    var appTheme: AppThemeConfig
        get() {
            val name = prefs.getString(KEY_APP_THEME, AppThemeConfig.PURPLE_GALAXY.name)
            return try {
                AppThemeConfig.valueOf(name ?: AppThemeConfig.PURPLE_GALAXY.name)
            } catch (_: Exception) {
                AppThemeConfig.PURPLE_GALAXY
            }
        }
        set(value) = prefs.edit().putString(KEY_APP_THEME, value.name).apply()

    var appLanguage: AppLanguage
        get() {
            val code = prefs.getString(KEY_APP_LANGUAGE, AppLanguage.ENGLISH.code)
            return if (code == "so") AppLanguage.SOMALI else AppLanguage.ENGLISH
        }
        set(value) = prefs.edit().putString(KEY_APP_LANGUAGE, value.code).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var isAnimationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ANIMATIONS_ENABLED, value).apply()

    // Wheels
    fun loadWheels(): List<SpinWheel> {
        val jsonStr = prefs.getString(KEY_CUSTOM_WHEELS, null)
        if (jsonStr.isNullOrEmpty()) {
            return DefaultWheels.allDefaults
        }
        return try {
            val list = mutableListOf<SpinWheel>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val segs = mutableListOf<WheelSegment>()
                val segArray = obj.getJSONArray("segments")
                for (j in 0 until segArray.length()) {
                    val sObj = segArray.getJSONObject(j)
                    val catName = sObj.optString("category", "CUSTOM")
                    val cat = try { RewardCategory.valueOf(catName) } catch (_: Exception) { RewardCategory.CUSTOM }
                    segs.add(
                        WheelSegment(
                            id = sObj.getString("id"),
                            label = sObj.getString("label"),
                            colorHex = sObj.getString("colorHex"),
                            category = cat,
                            iconName = sObj.optString("iconName", "star"),
                            weight = sObj.optInt("weight", 1)
                        )
                    )
                }
                list.add(
                    SpinWheel(
                        id = obj.getString("id"),
                        name = obj.getString("name"),
                        description = obj.optString("description", ""),
                        segments = segs,
                        isCustom = obj.optBoolean("isCustom", false),
                        themeId = obj.optString("themeId", "galaxy"),
                        createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                    )
                )
            }
            if (list.isEmpty()) DefaultWheels.allDefaults else list
        } catch (_: Exception) {
            DefaultWheels.allDefaults
        }
    }

    fun saveWheels(wheels: List<SpinWheel>) {
        try {
            val array = JSONArray()
            for (w in wheels) {
                val obj = JSONObject()
                obj.put("id", w.id)
                obj.put("name", w.name)
                obj.put("description", w.description)
                obj.put("isCustom", w.isCustom)
                obj.put("themeId", w.themeId)
                obj.put("createdAt", w.createdAt)
                val segArray = JSONArray()
                for (s in w.segments) {
                    val sObj = JSONObject()
                    sObj.put("id", s.id)
                    sObj.put("label", s.label)
                    sObj.put("colorHex", s.colorHex)
                    sObj.put("category", s.category.name)
                    sObj.put("iconName", s.iconName)
                    sObj.put("weight", s.weight)
                    segArray.put(sObj)
                }
                obj.put("segments", segArray)
                array.put(obj)
            }
            prefs.edit().putString(KEY_CUSTOM_WHEELS, array.toString()).apply()
        } catch (_: Exception) {
            // Safely handle errors
        }
    }

    // Spin History
    fun loadSpinHistory(): List<SpinHistoryItem> {
        val jsonStr = prefs.getString(KEY_SPIN_HISTORY, null) ?: return emptyList()
        return try {
            val list = mutableListOf<SpinHistoryItem>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val catName = obj.optString("category", "CUSTOM")
                val cat = try { RewardCategory.valueOf(catName) } catch (_: Exception) { RewardCategory.CUSTOM }
                list.add(
                    SpinHistoryItem(
                        id = obj.getString("id"),
                        wheelId = obj.getString("wheelId"),
                        wheelName = obj.getString("wheelName"),
                        segmentLabel = obj.getString("segmentLabel"),
                        category = cat,
                        colorHex = obj.getString("colorHex"),
                        timestampMillis = obj.getLong("timestampMillis"),
                        spinNumber = obj.getInt("spinNumber"),
                        isPractice = obj.optBoolean("isPractice", false)
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveSpinHistory(history: List<SpinHistoryItem>) {
        try {
            val array = JSONArray()
            // Keep up to 200 history items
            val truncated = history.take(200)
            for (item in truncated) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("wheelId", item.wheelId)
                obj.put("wheelName", item.wheelName)
                obj.put("segmentLabel", item.segmentLabel)
                obj.put("category", item.category.name)
                obj.put("colorHex", item.colorHex)
                obj.put("timestampMillis", item.timestampMillis)
                obj.put("spinNumber", item.spinNumber)
                obj.put("isPractice", item.isPractice)
                array.put(obj)
            }
            prefs.edit().putString(KEY_SPIN_HISTORY, array.toString()).apply()
        } catch (_: Exception) {
            // Safely handle
        }
    }

    // User Profile
    fun loadUserProfile(): UserProfile {
        val jsonStr = prefs.getString(KEY_USER_PROFILE, null) ?: return UserProfile()
        return try {
            val obj = JSONObject(jsonStr)
            UserProfile(
                displayName = obj.optString("displayName", "Hayner Champion"),
                avatarIndex = obj.optInt("avatarIndex", 0),
                totalSpins = obj.optInt("totalSpins", 0),
                currentStreak = obj.optInt("currentStreak", 1),
                bestStreak = obj.optInt("bestStreak", 1),
                lastSpinDateString = obj.optString("lastSpinDateString", ""),
                totalRewardPoints = obj.optInt("totalRewardPoints", 100)
            )
        } catch (_: Exception) {
            UserProfile()
        }
    }

    fun saveUserProfile(profile: UserProfile) {
        try {
            val obj = JSONObject()
            obj.put("displayName", profile.displayName)
            obj.put("avatarIndex", profile.avatarIndex)
            obj.put("totalSpins", profile.totalSpins)
            obj.put("currentStreak", profile.currentStreak)
            obj.put("bestStreak", profile.bestStreak)
            obj.put("lastSpinDateString", profile.lastSpinDateString)
            obj.put("totalRewardPoints", profile.totalRewardPoints)
            prefs.edit().putString(KEY_USER_PROFILE, obj.toString()).apply()
        } catch (_: Exception) {
            // Safely handle
        }
    }

    // Daily Challenges
    fun loadDailyChallenges(): List<DailyChallenge> {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val savedDate = prefs.getString(KEY_CHALLENGES_DATE, "")

        if (savedDate != todayStr) {
            // New day: reset challenges
            val fresh = listOf(
                DailyChallenge("ch_1", "challenge_spin_3", "challenge_spin_3", 3, 0, false, false, 50),
                DailyChallenge("ch_2", "challenge_themes", "challenge_themes", 2, 0, false, false, 75),
                DailyChallenge("ch_3", "challenge_custom", "challenge_custom", 1, 0, false, false, 100),
                DailyChallenge("ch_4", "challenge_practice", "challenge_practice", 5, 0, false, false, 50)
            )
            saveDailyChallenges(fresh, todayStr)
            return fresh
        }

        val jsonStr = prefs.getString(KEY_CHALLENGES, null) ?: return emptyList()
        return try {
            val list = mutableListOf<DailyChallenge>()
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DailyChallenge(
                        id = obj.getString("id"),
                        titleKey = obj.getString("titleKey"),
                        descKey = obj.getString("descKey"),
                        targetCount = obj.getInt("targetCount"),
                        currentCount = obj.getInt("currentCount"),
                        completed = obj.optBoolean("completed", false),
                        claimed = obj.optBoolean("claimed", false),
                        rewardPoints = obj.optInt("rewardPoints", 50)
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveDailyChallenges(challenges: List<DailyChallenge>, todayStr: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) {
        try {
            val array = JSONArray()
            for (c in challenges) {
                val obj = JSONObject()
                obj.put("id", c.id)
                obj.put("titleKey", c.titleKey)
                obj.put("descKey", c.descKey)
                obj.put("targetCount", c.targetCount)
                obj.put("currentCount", c.currentCount)
                obj.put("completed", c.completed)
                obj.put("claimed", c.claimed)
                obj.put("rewardPoints", c.rewardPoints)
                array.put(obj)
            }
            prefs.edit()
                .putString(KEY_CHALLENGES, array.toString())
                .putString(KEY_CHALLENGES_DATE, todayStr)
                .apply()
        } catch (_: Exception) {
            // Safely handle
        }
    }

    fun clearAllData() {
        prefs.edit().clear().apply()
    }
}
