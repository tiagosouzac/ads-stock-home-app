package com.example.stockhome.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.example.stockhome.R

/**
 * Manrope carregada via Google Downloadable Fonts (mesma fonte do protótipo).
 * Requer permissão de INTERNET; em caso de falha o sistema usa a sans-serif padrão.
 */
private val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)

private val manropeFont = GoogleFont("Manrope")

val Manrope = FontFamily(
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.SemiBold),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.Bold),
    Font(googleFont = manropeFont, fontProvider = provider, weight = FontWeight.ExtraBold),
)

// Typography base com Manrope aplicada a todos os estilos (fallback p/ Text() padrão).
val ShTypography = Typography().let { t ->
    Typography(
        displayLarge = t.displayLarge.copy(fontFamily = Manrope),
        displayMedium = t.displayMedium.copy(fontFamily = Manrope),
        displaySmall = t.displaySmall.copy(fontFamily = Manrope),
        headlineLarge = t.headlineLarge.copy(fontFamily = Manrope),
        headlineMedium = t.headlineMedium.copy(fontFamily = Manrope),
        headlineSmall = t.headlineSmall.copy(fontFamily = Manrope),
        titleLarge = t.titleLarge.copy(fontFamily = Manrope),
        titleMedium = t.titleMedium.copy(fontFamily = Manrope),
        titleSmall = t.titleSmall.copy(fontFamily = Manrope),
        bodyLarge = t.bodyLarge.copy(fontFamily = Manrope),
        bodyMedium = t.bodyMedium.copy(fontFamily = Manrope),
        bodySmall = t.bodySmall.copy(fontFamily = Manrope),
        labelLarge = t.labelLarge.copy(fontFamily = Manrope),
        labelMedium = t.labelMedium.copy(fontFamily = Manrope),
        labelSmall = t.labelSmall.copy(fontFamily = Manrope),
    )
}
