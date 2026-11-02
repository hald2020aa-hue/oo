package eu.kanade.presentation.theme.colorscheme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Neon Reader: vivid accents on low-glare surfaces.
 * Keep elevated surfaces dark to avoid distracting from reading content.
 */
internal object NeonCyberColorScheme : BaseColorScheme() {

    override val darkScheme = darkColorScheme(
        primary = Color(0xFF00E5FF),
        onPrimary = Color(0xFF001D26),
        primaryContainer = Color(0xFF004C59),
        onPrimaryContainer = Color(0xFFB5F6FF),
        inversePrimary = Color(0xFF006775),
        secondary = Color(0xFFB388FF),
        onSecondary = Color(0xFF211336),
        secondaryContainer = Color(0xFF443063),
        onSecondaryContainer = Color(0xFFE9D6FF),
        tertiary = Color(0xFFFF64CC),
        onTertiary = Color(0xFF3B0028),
        tertiaryContainer = Color(0xFF66204F),
        onTertiaryContainer = Color(0xFFFFD7F0),
        error = Color(0xFFFF8490),
        onError = Color(0xFF3D0009),
        errorContainer = Color(0xFF7B2631),
        onErrorContainer = Color(0xFFFFDADF),
        background = Color(0xFF080D1A),
        onBackground = Color(0xFFE7ECFF),
        surface = Color(0xFF0B1020),
        onSurface = Color(0xFFE7ECFF),
        surfaceVariant = Color(0xFF202842),
        onSurfaceVariant = Color(0xFFBAC6E5),
        outline = Color(0xFF8692B2),
        outlineVariant = Color(0xFF38425E),
        inverseSurface = Color(0xFFE4EAFF),
        inverseOnSurface = Color(0xFF151B2B),
        surfaceDim = Color(0xFF080D1A),
        surfaceBright = Color(0xFF303A55),
        surfaceContainerLowest = Color(0xFF080B15),
        surfaceContainerLow = Color(0xFF10162A),
        surfaceContainer = Color(0xFF151C32),
        surfaceContainerHigh = Color(0xFF1D2740),
        surfaceContainerHighest = Color(0xFF28324F),
    )

    // A usable light alternative remains available for accessibility.
    override val lightScheme = lightColorScheme(
        primary = Color(0xFF006E7C),
        onPrimary = Color(0xFFFFFFFF),
        primaryContainer = Color(0xFFB5F4FF),
        onPrimaryContainer = Color(0xFF00363D),
        inversePrimary = Color(0xFF57D7EC),
        secondary = Color(0xFF664B91),
        onSecondary = Color(0xFFFFFFFF),
        secondaryContainer = Color(0xFFE9DDFF),
        onSecondaryContainer = Color(0xFF2A164D),
        tertiary = Color(0xFF9D246D),
        onTertiary = Color(0xFFFFFFFF),
        tertiaryContainer = Color(0xFFFFD9EB),
        onTertiaryContainer = Color(0xFF3D0029),
        error = Color(0xFFB3261E),
        onError = Color(0xFFFFFFFF),
        background = Color(0xFFF6F8FF),
        onBackground = Color(0xFF192235),
        surface = Color(0xFFFBFAFF),
        onSurface = Color(0xFF192235),
        surfaceVariant = Color(0xFFE3E8F4),
        onSurfaceVariant = Color(0xFF47546A),
        outline = Color(0xFF68768E),
        outlineVariant = Color(0xFFC2CADB),
        inverseSurface = Color(0xFF202B42),
        inverseOnSurface = Color(0xFFF6F8FF),
        surfaceDim = Color(0xFFD5DCEC),
        surfaceBright = Color(0xFFFBFAFF),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF0F3FD),
        surfaceContainer = Color(0xFFE8EDF9),
        surfaceContainerHigh = Color(0xFFE1E8F5),
        surfaceContainerHighest = Color(0xFFD8E0F0),
    )
}
