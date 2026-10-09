package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.model.AppThemeConfig

data class HaynerThemeColors(
    val background: Color,
    val surface: Color,
    val card: Color,
    val cardBorder: Color,
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val textPrimary: Color = TextPrimary,
    val textSecondary: Color = TextSecondary,
    val textMuted: Color = TextMuted,
    val glowBrush: Brush = Brush.linearGradient(listOf(primary, secondary)),
    val bgBrush: Brush = Brush.verticalGradient(listOf(surface, background))
)

val LocalHaynerColors = staticCompositionLocalOf {
    HaynerThemeColors(
        background = GalaxyBg,
        surface = GalaxySurface,
        card = GalaxyCard,
        cardBorder = GalaxyCardBorder,
        primary = GalaxyPrimary,
        secondary = GalaxySecondary,
        accent = GalaxyAccent
    )
}

fun getHaynerColors(theme: AppThemeConfig): HaynerThemeColors {
    return when (theme) {
        AppThemeConfig.PURPLE_GALAXY -> HaynerThemeColors(
            background = GalaxyBg,
            surface = GalaxySurface,
            card = GalaxyCard,
            cardBorder = GalaxyCardBorder,
            primary = GalaxyPrimary,
            secondary = GalaxySecondary,
            accent = GalaxyAccent
        )
        AppThemeConfig.NEON_BLUE -> HaynerThemeColors(
            background = NeonBlueBg,
            surface = NeonBlueSurface,
            card = NeonBlueCard,
            cardBorder = NeonBlueCardBorder,
            primary = NeonBluePrimary,
            secondary = NeonBlueSecondary,
            accent = NeonBlueAccent
        )
        AppThemeConfig.ROYAL_GOLD -> HaynerThemeColors(
            background = RoyalGoldBg,
            surface = RoyalGoldSurface,
            card = RoyalGoldCard,
            cardBorder = RoyalGoldCardBorder,
            primary = RoyalGoldPrimary,
            secondary = RoyalGoldSecondary,
            accent = RoyalGoldAccent
        )
        AppThemeConfig.EMERALD_NIGHT -> HaynerThemeColors(
            background = EmeraldNightBg,
            surface = EmeraldNightSurface,
            card = EmeraldNightCard,
            cardBorder = EmeraldNightCardBorder,
            primary = EmeraldNightPrimary,
            secondary = EmeraldNightSecondary,
            accent = EmeraldNightAccent
        )
        AppThemeConfig.CRIMSON_NIGHT -> HaynerThemeColors(
            background = CrimsonNightBg,
            surface = CrimsonNightSurface,
            card = CrimsonNightCard,
            cardBorder = CrimsonNightCardBorder,
            primary = CrimsonNightPrimary,
            secondary = CrimsonNightSecondary,
            accent = CrimsonNightAccent
        )
        AppThemeConfig.MINIMAL_DARK -> HaynerThemeColors(
            background = MinimalDarkBg,
            surface = MinimalDarkSurface,
            card = MinimalDarkCard,
            cardBorder = MinimalDarkCardBorder,
            primary = MinimalDarkPrimary,
            secondary = MinimalDarkSecondary,
            accent = MinimalDarkAccent
        )
    }
}

fun createColorScheme(colors: HaynerThemeColors): ColorScheme {
    return darkColorScheme(
        primary = colors.primary,
        onPrimary = Color.White,
        secondary = colors.secondary,
        onSecondary = Color.White,
        tertiary = colors.accent,
        background = colors.background,
        onBackground = colors.textPrimary,
        surface = colors.surface,
        onSurface = colors.textPrimary,
        surfaceVariant = colors.card,
        onSurfaceVariant = colors.textSecondary,
        outline = colors.cardBorder
    )
}

@Composable
fun HaynerSpinTheme(
    themeConfig: AppThemeConfig = AppThemeConfig.PURPLE_GALAXY,
    content: @Composable () -> Unit
) {
    val haynerColors = getHaynerColors(themeConfig)
    val colorScheme = createColorScheme(haynerColors)

    CompositionLocalProvider(LocalHaynerColors provides haynerColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
