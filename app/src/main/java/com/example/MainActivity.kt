package com.example.MainActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ScreenDestination
import com.example.ui.components.HaynerBottomNav
import com.example.ui.components.SpinResultDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyWheelsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SpinScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WheelEditorScreen
import com.example.ui.theme.HaynerSpinTheme
import com.example.viewmodel.HaynerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HaynerApp()
        }
    }
}

@Composable
fun HaynerApp(viewModel: HaynerViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val appTheme by viewModel.appTheme.collectAsStateWithLifecycle()
    val language by viewModel.appLanguage.collectAsStateWithLifecycle()

    val activeWheel by viewModel.activeWheel.collectAsStateWithLifecycle()
    val allWheels by viewModel.wheels.collectAsStateWithLifecycle()
    val editingWheel by viewModel.editingWheel.collectAsStateWithLifecycle()

    val spinMode by viewModel.spinMode.collectAsStateWithLifecycle()
    val isSpinning by viewModel.isSpinning.collectAsStateWithLifecycle()
    val wheelRotation by viewModel.wheelRotation.collectAsStateWithLifecycle()
    val needleDeflection by viewModel.needleDeflection.collectAsStateWithLifecycle()

    val lastResult by viewModel.lastResult.collectAsStateWithLifecycle()
    val showResultDialog by viewModel.showResultDialog.collectAsStateWithLifecycle()

    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val challenges by viewModel.challenges.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()

    val isSoundEnabled by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val isVibrationEnabled by viewModel.isVibrationEnabled.collectAsStateWithLifecycle()
    val isAnimationsEnabled by viewModel.isAnimationsEnabled.collectAsStateWithLifecycle()

    // Android Hardware Back Navigation handling
    BackHandler(enabled = currentScreen != ScreenDestination.HOME && currentScreen != ScreenDestination.SPLASH && currentScreen != ScreenDestination.ONBOARDING) {
        when (currentScreen) {
            ScreenDestination.WHEEL_EDITOR -> viewModel.navigateTo(ScreenDestination.MY_WHEELS)
            ScreenDestination.SETTINGS -> viewModel.navigateTo(ScreenDestination.HOME)
            else -> viewModel.navigateTo(ScreenDestination.HOME)
        }
    }

    HaynerSpinTheme(themeConfig = appTheme) {
        val showBottomNav = currentScreen in listOf(
            ScreenDestination.HOME,
            ScreenDestination.MY_WHEELS,
            ScreenDestination.SPIN,
            ScreenDestination.HISTORY,
            ScreenDestination.PROFILE
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomNav) {
                    HaynerBottomNav(
                        currentScreen = currentScreen,
                        language = language,
                        onNavigate = { dest -> viewModel.navigateTo(dest) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp(0f))
            ) {
                when (currentScreen) {
                    ScreenDestination.SPLASH -> {
                        SplashScreen(
                            language = language,
                            onFinish = {
                                viewModel.completeOnboarding() // checks if was onboarded or goes to Home/Onboarding
                            }
                        )
                    }

                    ScreenDestination.ONBOARDING -> {
                        OnboardingScreen(
                            language = language,
                            onComplete = { viewModel.completeOnboarding() }
                        )
                    }

                    ScreenDestination.HOME -> {
                        HomeScreen(
                            activeWheel = activeWheel,
                            userProfile = userProfile,
                            lastResult = lastResult,
                            recentHistory = history,
                            challenges = challenges,
                            language = language,
                            onSpinNow = {
                                viewModel.navigateTo(ScreenDestination.SPIN)
                                viewModel.startSpin()
                            },
                            onClaimChallenge = { id -> viewModel.claimChallenge(id) },
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }

                    ScreenDestination.SPIN -> {
                        SpinScreen(
                            activeWheel = activeWheel,
                            allWheels = allWheels,
                            spinMode = spinMode,
                            isSpinning = isSpinning,
                            wheelRotation = wheelRotation,
                            needleDeflection = needleDeflection,
                            language = language,
                            onSelectWheel = { w -> viewModel.selectActiveWheel(w) },
                            onSetSpinMode = { m -> viewModel.setSpinMode(m) },
                            onSpin = { viewModel.startSpin() }
                        )
                    }

                    ScreenDestination.MY_WHEELS -> {
                        MyWheelsScreen(
                            wheels = allWheels,
                            activeWheelId = activeWheel.id,
                            language = language,
                            onCreateNew = { viewModel.startCreateWheel() },
                            onEditWheel = { w -> viewModel.startEditWheel(w) },
                            onDuplicateWheel = { w -> viewModel.duplicateWheel(w) },
                            onDeleteWheel = { id -> viewModel.deleteWheel(id) },
                            onResetDefaults = { viewModel.resetWheelsToDefault() },
                            onSelectActive = { w -> viewModel.selectActiveWheel(w) }
                        )
                    }

                    ScreenDestination.WHEEL_EDITOR -> {
                        editingWheel?.let { wheel ->
                            WheelEditorScreen(
                                wheel = wheel,
                                language = language,
                                onWheelChange = { updated -> viewModel.updateEditingWheel(updated) },
                                onSave = { viewModel.saveEditingWheel() },
                                onBack = { viewModel.navigateTo(ScreenDestination.MY_WHEELS) }
                            )
                        }
                    }

                    ScreenDestination.HISTORY -> {
                        HistoryScreen(
                            history = history,
                            language = language,
                            onClearHistory = { viewModel.clearHistory() }
                        )
                    }

                    ScreenDestination.PROFILE -> {
                        ProfileScreen(
                            userProfile = userProfile,
                            wheelsCount = allWheels.size,
                            language = language,
                            onUpdateName = { name -> viewModel.updateDisplayName(name) },
                            onUpdateAvatar = { idx -> viewModel.updateAvatar(idx) },
                            onResetStats = { viewModel.resetStatistics() }
                        )
                    }

                    ScreenDestination.SETTINGS -> {
                        SettingsScreen(
                            currentTheme = appTheme,
                            language = language,
                            isSoundEnabled = isSoundEnabled,
                            isVibrationEnabled = isVibrationEnabled,
                            isAnimationsEnabled = isAnimationsEnabled,
                            onSetTheme = { t -> viewModel.setTheme(t) },
                            onSetLanguage = { l -> viewModel.setLanguage(l) },
                            onToggleSound = { s -> viewModel.toggleSound(s) },
                            onToggleVibration = { v -> viewModel.toggleVibration(v) },
                            onToggleAnimations = { a -> viewModel.toggleAnimations(a) },
                            onClearHistory = { viewModel.clearHistory() },
                            onResetStats = { viewModel.resetStatistics() },
                            onResetAllData = { viewModel.resetAllData() },
                            onBack = { viewModel.navigateTo(ScreenDestination.HOME) }
                        )
                    }
                }
            }

            // Global Result Dialog when a spin finishes
            if (showResultDialog && lastResult != null) {
                SpinResultDialog(
                    result = lastResult!!,
                    language = language,
                    onSpinAgain = {
                        viewModel.dismissResultDialog()
                        viewModel.startSpin()
                    },
                    onBackHome = {
                        viewModel.dismissResultDialog()
                        viewModel.navigateTo(ScreenDestination.HOME)
                    },
                    onDismiss = { viewModel.dismissResultDialog() }
                )
            }
        }
    }
}
