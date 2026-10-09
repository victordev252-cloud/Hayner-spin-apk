package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundEffects
import com.example.data.DefaultWheels
import com.example.data.HaynerPreferences
import com.example.model.AppLanguage
import com.example.model.AppThemeConfig
import com.example.model.DailyChallenge
import com.example.model.RewardCategory
import com.example.model.ScreenDestination
import com.example.model.SpinHistoryItem
import com.example.model.SpinMode
import com.example.model.SpinResult
import com.example.model.SpinWheel
import com.example.model.UserProfile
import com.example.model.WheelSegment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID
import kotlin.random.Random

class HaynerViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = HaynerPreferences(application)
    val soundEffects = SoundEffects(application).apply {
        isSoundEnabled = prefs.isSoundEnabled
        isVibrationEnabled = prefs.isVibrationEnabled
    }

    // Screen State
    private val _currentScreen = MutableStateFlow(
        if (prefs.isOnboardingCompleted) ScreenDestination.HOME else ScreenDestination.SPLASH
    )
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Wheels
    private val _wheels = MutableStateFlow(prefs.loadWheels())
    val wheels: StateFlow<List<SpinWheel>> = _wheels.asStateFlow()

    private val _activeWheel = MutableStateFlow(
        _wheels.value.find { it.id == prefs.activeWheelId } ?: _wheels.value.firstOrNull() ?: DefaultWheels.haynerDefaultWheel
    )
    val activeWheel: StateFlow<SpinWheel> = _activeWheel.asStateFlow()

    // Wheel Editor State
    private val _editingWheel = MutableStateFlow<SpinWheel?>(null)
    val editingWheel: StateFlow<SpinWheel?> = _editingWheel.asStateFlow()

    // Spin Engine State
    private val _spinMode = MutableStateFlow(SpinMode.STANDARD)
    val spinMode: StateFlow<SpinMode> = _spinMode.asStateFlow()

    private val _isSpinning = MutableStateFlow(false)
    val isSpinning: StateFlow<Boolean> = _isSpinning.asStateFlow()

    private val _wheelRotation = MutableStateFlow(0f)
    val wheelRotation: StateFlow<Float> = _wheelRotation.asStateFlow()

    private val _needleDeflection = MutableStateFlow(0f)
    val needleDeflection: StateFlow<Float> = _needleDeflection.asStateFlow()

    private val _lastResult = MutableStateFlow<SpinResult?>(null)
    val lastResult: StateFlow<SpinResult?> = _lastResult.asStateFlow()

    private val _showResultDialog = MutableStateFlow(false)
    val showResultDialog: StateFlow<Boolean> = _showResultDialog.asStateFlow()

    // History
    private val _history = MutableStateFlow(prefs.loadSpinHistory())
    val history: StateFlow<List<SpinHistoryItem>> = _history.asStateFlow()

    // Profile & Stats
    private val _userProfile = MutableStateFlow(prefs.loadUserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Daily Challenges
    private val _challenges = MutableStateFlow(prefs.loadDailyChallenges())
    val challenges: StateFlow<List<DailyChallenge>> = _challenges.asStateFlow()

    // Settings
    private val _appTheme = MutableStateFlow(prefs.appTheme)
    val appTheme: StateFlow<AppThemeConfig> = _appTheme.asStateFlow()

    private val _appLanguage = MutableStateFlow(prefs.appLanguage)
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isSoundEnabled = MutableStateFlow(prefs.isSoundEnabled)
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(prefs.isVibrationEnabled)
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _isAnimationsEnabled = MutableStateFlow(prefs.isAnimationsEnabled)
    val isAnimationsEnabled: StateFlow<Boolean> = _isAnimationsEnabled.asStateFlow()

    private var spinJob: Job? = null

    init {
        // Check streak on start
        checkAndUpdateStreak()
    }

    fun navigateTo(screen: ScreenDestination) {
        soundEffects.playButtonTap()
        _currentScreen.value = screen
    }

    fun completeOnboarding() {
        prefs.isOnboardingCompleted = true
        _currentScreen.value = ScreenDestination.HOME
    }

    fun setSpinMode(mode: SpinMode) {
        _spinMode.value = mode
    }

    fun selectActiveWheel(wheel: SpinWheel) {
        _activeWheel.value = wheel
        prefs.activeWheelId = wheel.id
    }

    /**
     * Executes the realistic spinning wheel animation and mathematically synchronizes
     * the visually stopped segment with the winning result.
     */
    fun startSpin(onFinished: ((SpinResult) -> Unit)? = null) {
        if (_isSpinning.value) return
        val wheel = _activeWheel.value
        val segments = wheel.segments
        if (segments.isEmpty()) return

        _isSpinning.value = true
        _showResultDialog.value = false

        spinJob?.cancel()
        spinJob = viewModelScope.launch {
            val segmentCount = segments.size
            val arcAngle = 360f / segmentCount

            // Pick winning segment based on weights
            val targetIndex = Random.nextInt(segmentCount)
            val winningSegment = segments[targetIndex]

            // Top pointer is at 270 degrees in Android coordinate system (12 o'clock).
            // Segment i is drawn from [i * arcAngle, (i+1) * arcAngle].
            // To center segment targetIndex under the needle at 270 degrees:
            // (targetIndex + 0.5) * arcAngle + finalAngle ≡ 270 (mod 360)
            // Therefore: finalAngle ≡ 270 - (targetIndex + 0.5) * arcAngle (mod 360)
            val jitter = (Random.nextFloat() - 0.5f) * (arcAngle * 0.4f)
            val segmentCenter = (targetIndex + 0.5f) * arcAngle + jitter
            val targetRemainder = ((270f - segmentCenter) % 360f + 360f) % 360f

            val currentRot = _wheelRotation.value
            val currentRem = ((currentRot % 360f) + 360f) % 360f
            var delta = targetRemainder - currentRem
            if (delta < 0) delta += 360f

            // Add full rotations depending on mode
            val fullRotations = when (_spinMode.value) {
                SpinMode.QUICK -> 3
                else -> Random.nextInt(5, 8)
            }
            val totalAngleToSpin = (fullRotations * 360f) + delta
            val targetRotation = currentRot + totalAngleToSpin

            val durationMs = when (_spinMode.value) {
                SpinMode.QUICK -> 1500L
                else -> 4000L
            }

            val startTime = System.currentTimeMillis()
            var lastPegIndex = -1

            while (true) {
                val elapsed = System.currentTimeMillis() - startTime
                val progress = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)

                // Quintic ease out for super-realistic deceleration
                val easeProgress = 1f - Math.pow((1f - progress).toDouble(), 4.5).toFloat()
                val currentAngle = currentRot + (totalAngleToSpin * easeProgress)
                _wheelRotation.value = currentAngle

                // Calculate which peg is passing under the top needle (270 degrees)
                val unrotatedPointer = ((270f - currentAngle) % 360f + 360f) % 360f
                val currentPeg = (unrotatedPointer / arcAngle).toInt() % segmentCount

                if (currentPeg != lastPegIndex) {
                    lastPegIndex = currentPeg
                    // Flick needle
                    _needleDeflection.value = -18f
                    soundEffects.playTick()
                    soundEffects.vibrateTick()
                } else {
                    // Needle springs back
                    _needleDeflection.value *= 0.7f
                }

                if (progress >= 1f) break
                delay(16) // ~60fps smooth loop
            }

            _wheelRotation.value = targetRotation
            _needleDeflection.value = 0f
            _isSpinning.value = false

            // Update stats & history
            val isPractice = _spinMode.value == SpinMode.PRACTICE
            val newSpinNumber = _userProfile.value.totalSpins + 1
            val result = SpinResult(
                segment = winningSegment,
                spinNumber = newSpinNumber,
                timestampMillis = System.currentTimeMillis(),
                wheelName = wheel.name,
                isPractice = isPractice
            )

            _lastResult.value = result
            _showResultDialog.value = true

            // Audio & Vibration for result
            if (winningSegment.category == RewardCategory.TRY_AGAIN || winningSegment.category == RewardCategory.NO_PRIZE) {
                soundEffects.playTryAgainChime()
            } else {
                soundEffects.playWinFanfare()
                soundEffects.vibrateWin()
            }

            // Persist to history
            recordSpinResult(result)
            updateChallengesAfterSpin(isPractice)

            onFinished?.invoke(result)
        }
    }

    private fun recordSpinResult(result: SpinResult) {
        val historyItem = SpinHistoryItem(
            id = UUID.randomUUID().toString(),
            wheelId = _activeWheel.value.id,
            wheelName = result.wheelName,
            segmentLabel = result.segment.label,
            category = result.segment.category,
            colorHex = result.segment.colorHex,
            timestampMillis = result.timestampMillis,
            spinNumber = result.spinNumber,
            isPractice = result.isPractice
        )

        val updatedHistory = listOf(historyItem) + _history.value
        _history.value = updatedHistory
        prefs.saveSpinHistory(updatedHistory)

        // Update profile
        val profile = _userProfile.value
        val updatedProfile = profile.copy(
            totalSpins = profile.totalSpins + 1,
            lastSpinDateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        )
        _userProfile.value = updatedProfile
        prefs.saveUserProfile(updatedProfile)
    }

    private fun updateChallengesAfterSpin(isPractice: Boolean) {
        val list = _challenges.value.map { ch ->
            when (ch.id) {
                "ch_1" -> { // Complete 3 spins
                    val newCount = (ch.currentCount + 1).coerceAtMost(ch.targetCount)
                    ch.copy(currentCount = newCount, completed = newCount >= ch.targetCount)
                }
                "ch_4" -> { // Practice spins
                    if (isPractice) {
                        val newCount = (ch.currentCount + 1).coerceAtMost(ch.targetCount)
                        ch.copy(currentCount = newCount, completed = newCount >= ch.targetCount)
                    } else ch
                }
                else -> ch
            }
        }
        _challenges.value = list
        prefs.saveDailyChallenges(list)
    }

    fun claimChallenge(challengeId: String) {
        soundEffects.playWinFanfare()
        val list = _challenges.value.map {
            if (it.id == challengeId && it.completed && !it.claimed) {
                it.copy(claimed = true)
            } else it
        }
        _challenges.value = list
        prefs.saveDailyChallenges(list)

        // Add reward points
        val currentProfile = _userProfile.value
        val updatedProfile = currentProfile.copy(
            totalRewardPoints = currentProfile.totalRewardPoints + 50
        )
        _userProfile.value = updatedProfile
        prefs.saveUserProfile(updatedProfile)
    }

    fun dismissResultDialog() {
        _showResultDialog.value = false
    }

    // Wheel Editor methods
    fun startCreateWheel() {
        val newWheel = SpinWheel(
            id = "wheel_" + UUID.randomUUID().toString().take(8),
            name = "My Lucky Wheel",
            description = "Custom spinning wheel",
            segments = listOf(
                WheelSegment(UUID.randomUUID().toString(), "Prize A", "#E11D48"),
                WheelSegment(UUID.randomUUID().toString(), "Prize B", "#0284C7"),
                WheelSegment(UUID.randomUUID().toString(), "Prize C", "#059669"),
                WheelSegment(UUID.randomUUID().toString(), "Prize D", "#D97706")
            ),
            isCustom = true
        )
        _editingWheel.value = newWheel
        navigateTo(ScreenDestination.WHEEL_EDITOR)
    }

    fun startEditWheel(wheel: SpinWheel) {
        _editingWheel.value = wheel
        navigateTo(ScreenDestination.WHEEL_EDITOR)
    }

    fun updateEditingWheel(updated: SpinWheel) {
        _editingWheel.value = updated
    }

    fun saveEditingWheel() {
        val wheel = _editingWheel.value ?: return
        if (wheel.segments.size < 2) return

        val existingIndex = _wheels.value.indexOfFirst { it.id == wheel.id }
        val updatedList = if (existingIndex >= 0) {
            _wheels.value.toMutableList().apply { set(existingIndex, wheel) }
        } else {
            _wheels.value + wheel
        }

        _wheels.value = updatedList
        prefs.saveWheels(updatedList)

        if (_activeWheel.value.id == wheel.id) {
            _activeWheel.value = wheel
        }

        // Challenge progress
        val chList = _challenges.value.map {
            if (it.id == "ch_3") it.copy(currentCount = 1, completed = true) else it
        }
        _challenges.value = chList
        prefs.saveDailyChallenges(chList)

        soundEffects.playButtonTap()
        _editingWheel.value = null
        navigateTo(ScreenDestination.MY_WHEELS)
    }

    fun duplicateWheel(wheel: SpinWheel) {
        val copy = wheel.copy(
            id = "wheel_" + UUID.randomUUID().toString().take(8),
            name = "${wheel.name} (Copy)",
            isCustom = true,
            createdAt = System.currentTimeMillis()
        )
        val list = _wheels.value + copy
        _wheels.value = list
        prefs.saveWheels(list)
        soundEffects.playButtonTap()
    }

    fun deleteWheel(wheelId: String) {
        val list = _wheels.value.filter { it.id != wheelId }
        _wheels.value = if (list.isEmpty()) DefaultWheels.allDefaults else list
        prefs.saveWheels(_wheels.value)

        if (_activeWheel.value.id == wheelId) {
            _activeWheel.value = _wheels.value.first()
            prefs.activeWheelId = _activeWheel.value.id
        }
        soundEffects.playButtonTap()
    }

    fun resetWheelsToDefault() {
        _wheels.value = DefaultWheels.allDefaults
        _activeWheel.value = DefaultWheels.haynerDefaultWheel
        prefs.saveWheels(DefaultWheels.allDefaults)
        prefs.activeWheelId = DefaultWheels.HAYNER_DEFAULT_ID
        soundEffects.playButtonTap()
    }

    // History methods
    fun clearHistory() {
        _history.value = emptyList()
        prefs.saveSpinHistory(emptyList())
        soundEffects.playButtonTap()
    }

    // Profile methods
    fun updateDisplayName(name: String) {
        val updated = _userProfile.value.copy(displayName = name.trim().ifEmpty { "Hayner Spinner" })
        _userProfile.value = updated
        prefs.saveUserProfile(updated)
    }

    fun updateAvatar(avatarIdx: Int) {
        val updated = _userProfile.value.copy(avatarIndex = avatarIdx)
        _userProfile.value = updated
        prefs.saveUserProfile(updated)
        soundEffects.playButtonTap()
    }

    fun resetStatistics() {
        val updated = _userProfile.value.copy(
            totalSpins = 0,
            currentStreak = 1,
            bestStreak = 1
        )
        _userProfile.value = updated
        prefs.saveUserProfile(updated)
        soundEffects.playButtonTap()
    }

    // Settings methods
    fun setTheme(theme: AppThemeConfig) {
        _appTheme.value = theme
        prefs.appTheme = theme
        soundEffects.playButtonTap()

        // Challenge: try 2 different themes
        val chList = _challenges.value.map {
            if (it.id == "ch_2") {
                val newCount = (it.currentCount + 1).coerceAtMost(2)
                it.copy(currentCount = newCount, completed = newCount >= 2)
            } else it
        }
        _challenges.value = chList
        prefs.saveDailyChallenges(chList)
    }

    fun setLanguage(lang: AppLanguage) {
        _appLanguage.value = lang
        prefs.appLanguage = lang
        soundEffects.playButtonTap()
    }

    fun toggleSound(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        prefs.isSoundEnabled = enabled
        soundEffects.isSoundEnabled = enabled
    }

    fun toggleVibration(enabled: Boolean) {
        _isVibrationEnabled.value = enabled
        prefs.isVibrationEnabled = enabled
        soundEffects.isVibrationEnabled = enabled
    }

    fun toggleAnimations(enabled: Boolean) {
        _isAnimationsEnabled.value = enabled
        prefs.isAnimationsEnabled = enabled
    }

    fun resetAllData() {
        prefs.clearAllData()
        _wheels.value = DefaultWheels.allDefaults
        _activeWheel.value = DefaultWheels.haynerDefaultWheel
        _history.value = emptyList()
        _userProfile.value = UserProfile()
        _challenges.value = prefs.loadDailyChallenges()
        _appTheme.value = AppThemeConfig.PURPLE_GALAXY
        _appLanguage.value = AppLanguage.ENGLISH
        _isSoundEnabled.value = true
        _isVibrationEnabled.value = true
        _isAnimationsEnabled.value = true
        soundEffects.isSoundEnabled = true
        soundEffects.isVibrationEnabled = true
        soundEffects.playButtonTap()
        _currentScreen.value = ScreenDestination.HOME
    }

    private fun checkAndUpdateStreak() {
        val profile = _userProfile.value
        val lastDateStr = profile.lastSpinDateString
        if (lastDateStr.isEmpty()) return

        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        try {
            val lastDate = sdf.parse(lastDateStr) ?: return
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val diffMillis = today.time - lastDate.time
            val diffDays = diffMillis / (1000 * 60 * 60 * 24)

            if (diffDays > 1) {
                // Streak broken
                val updated = profile.copy(currentStreak = 1)
                _userProfile.value = updated
                prefs.saveUserProfile(updated)
            } else if (diffDays == 1L) {
                // Streak active
            }
        } catch (_: Exception) {}
    }
}
