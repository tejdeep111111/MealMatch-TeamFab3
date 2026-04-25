package com.teamfab.meallmatch.person.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.teamfab.meallmatch.R

// ── Google Fonts Provider ────────────────────────────
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

private val Newsreader = FontFamily(
    Font(googleFont = GoogleFont("Newsreader"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Newsreader"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Newsreader"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Newsreader"), fontProvider = provider, weight = FontWeight.Bold),
)

private val PlusJakartaSans = FontFamily(
    Font(googleFont = GoogleFont("Plus Jakarta Sans"), fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = GoogleFont("Plus Jakarta Sans"), fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("Plus Jakarta Sans"), fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("Plus Jakarta Sans"), fontProvider = provider, weight = FontWeight.Bold),
)

// ── Nourish Color Palette ────────────────────────────
object NourishColors {
    // Primary
    val Primary = Color(0xFF7E5700)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFE8A930)
    val OnPrimaryContainer = Color(0xFF5F4000)
    val InversePrimary = Color(0xFFFDBB41)

    // Secondary
    val Secondary = Color(0xFF296A42)
    val OnSecondary = Color(0xFFFFFFFF)
    val SecondaryContainer = Color(0xFFABEFBC)
    val OnSecondaryContainer = Color(0xFF2E6F46)

    // Tertiary
    val Tertiary = Color(0xFF0060AC)
    val OnTertiary = Color(0xFFFFFFFF)
    val TertiaryContainer = Color(0xFF81B7FF)
    val OnTertiaryContainer = Color(0xFF004782)

    // Error
    val Error = Color(0xFFBA1A1A)
    val OnError = Color(0xFFFFFFFF)
    val ErrorContainer = Color(0xFFFFDAD6)
    val OnErrorContainer = Color(0xFF93000A)

    // Surface / Background
    val Surface = Color(0xFFFFF8F1)
    val SurfaceDim = Color(0xFFDFD9D2)
    val SurfaceBright = Color(0xFFFFF8F1)
    val SurfaceContainerLowest = Color(0xFFFFFFFF)
    val SurfaceContainerLow = Color(0xFFF9F3EB)
    val SurfaceContainer = Color(0xFFF3EDE6)
    val SurfaceContainerHigh = Color(0xFFEDE7E0)
    val SurfaceContainerHighest = Color(0xFFE8E1DB)
    val OnSurface = Color(0xFF1D1B17)
    val OnSurfaceVariant = Color(0xFF504535)
    val SurfaceVariant = Color(0xFFE8E1DB)
    val InverseSurface = Color(0xFF33302C)
    val InverseOnSurface = Color(0xFFF6F0E9)

    // Outline
    val Outline = Color(0xFF827562)
    val OutlineVariant = Color(0xFFD5C4AF)

    // Background
    val Background = Color(0xFFFFF8F1)
    val OnBackground = Color(0xFF1D1B17)

    // Extra from design
    val CardBorder = Color(0xFFEEEAE1)
    val AmbientShadow = Color(0x14E8A930) // rgba(232,169,48,0.08)
}

// ── Light Color Scheme ───────────────────────────────
private val NourishLightColorScheme = lightColorScheme(
    primary = NourishColors.Primary,
    onPrimary = NourishColors.OnPrimary,
    primaryContainer = NourishColors.PrimaryContainer,
    onPrimaryContainer = NourishColors.OnPrimaryContainer,
    inversePrimary = NourishColors.InversePrimary,

    secondary = NourishColors.Secondary,
    onSecondary = NourishColors.OnSecondary,
    secondaryContainer = NourishColors.SecondaryContainer,
    onSecondaryContainer = NourishColors.OnSecondaryContainer,

    tertiary = NourishColors.Tertiary,
    onTertiary = NourishColors.OnTertiary,
    tertiaryContainer = NourishColors.TertiaryContainer,
    onTertiaryContainer = NourishColors.OnTertiaryContainer,

    error = NourishColors.Error,
    onError = NourishColors.OnError,
    errorContainer = NourishColors.ErrorContainer,
    onErrorContainer = NourishColors.OnErrorContainer,

    surface = NourishColors.Surface,
    onSurface = NourishColors.OnSurface,
    surfaceVariant = NourishColors.SurfaceVariant,
    onSurfaceVariant = NourishColors.OnSurfaceVariant,
    surfaceDim = NourishColors.SurfaceDim,
    surfaceBright = NourishColors.SurfaceBright,
    surfaceContainerLowest = NourishColors.SurfaceContainerLowest,
    surfaceContainerLow = NourishColors.SurfaceContainerLow,
    surfaceContainer = NourishColors.SurfaceContainer,
    surfaceContainerHigh = NourishColors.SurfaceContainerHigh,
    surfaceContainerHighest = NourishColors.SurfaceContainerHighest,
    inverseSurface = NourishColors.InverseSurface,
    inverseOnSurface = NourishColors.InverseOnSurface,

    outline = NourishColors.Outline,
    outlineVariant = NourishColors.OutlineVariant,

    background = NourishColors.Background,
    onBackground = NourishColors.OnBackground,
)

// ── Typography ───────────────────────────────────────
val NourishTypography = Typography(
    // Display Large → Newsreader 40sp semibold
    displayLarge = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 48.sp,
        letterSpacing = (-0.02).sp
    ),
    // Headline Large → Newsreader 32sp semibold
    headlineLarge = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
    ),
    // Headline Medium → Newsreader 24sp medium
    headlineMedium = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp,
        lineHeight = 32.sp,
    ),
    // Headline Small → Newsreader 20sp medium
    headlineSmall = TextStyle(
        fontFamily = Newsreader,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    // Title Large → Plus Jakarta Sans 20sp semibold
    titleLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
    ),
    // Title Medium → Plus Jakarta Sans 16sp semibold
    titleMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // Title Small → Plus Jakarta Sans 14sp semibold
    titleSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Body Large → Plus Jakarta Sans 16sp
    bodyLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    // Body Medium → Plus Jakarta Sans 14sp
    bodyMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    // Body Small → Plus Jakarta Sans 12sp
    bodySmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    // Label Large → Plus Jakarta Sans 12sp bold
    labelLarge = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Label Medium
    labelMedium = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    // Label Small
    labelSmall = TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.5.sp
    ),
)

// ── Theme Composable ─────────────────────────────────
@Composable
fun NourishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // For now, always use light (matching the stitch design)
    val colorScheme = NourishLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = NourishTypography,
        shapes = Shapes(
            small = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
            medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
            large = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
            extraLarge = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        ),
        content = content
    )
}

