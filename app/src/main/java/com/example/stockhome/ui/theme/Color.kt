package com.example.stockhome.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.stockhome.data.StatusTipo

/**
 * Tokens de cor — porta fiel do objeto `SH` de sh-ui.jsx.
 */
object Sh {
    val bg = Color(0xFFF4F7F2)
    val surface = Color(0xFFFFFFFF)
    val border = Color(0xFFE7EBE2)
    val borderStrong = Color(0xFFD8DECF)
    val ink = Color(0xFF18211B)
    val ink2 = Color(0xFF5A645B)
    val ink3 = Color(0xFF949E94)
    val brand = Color(0xFF1B8E57)
    val brandDark = Color(0xFF13693F)
    val brandTint = Color(0xFFE7F3EB)

    val radius = 16

    // tokens de status (chips contornados)
    data class StatusToken(val fg: Color, val bg: Color, val bd: Color)

    val ok = StatusToken(Color(0xFF1A8A55), Color(0xFFE9F4ED), Color(0xFFBFE0CC))
    val warn = StatusToken(Color(0xFFA2710D), Color(0xFFFBF0D6), Color(0xFFEBD095))
    val danger = StatusToken(Color(0xFFBE3A2B), Color(0xFFFBE9E5), Color(0xFFF0C4BB))

    fun statusToken(tipo: StatusTipo): StatusToken = when (tipo) {
        StatusTipo.baixo, StatusTipo.vencido -> danger
        StatusTipo.vencendo -> warn
        StatusTipo.ok -> ok
    }
}
