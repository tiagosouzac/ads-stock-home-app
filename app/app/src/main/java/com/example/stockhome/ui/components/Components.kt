package com.example.stockhome.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockhome.data.StatusTipo
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Manrope
import com.example.stockhome.ui.theme.Sh

/** Texto curto com a fonte Manrope (atalho). */
@Composable
fun T(
    text: String,
    size: Float,
    weight: FontWeight,
    color: Color,
    modifier: Modifier = Modifier,
    letterSpacing: Float = 0f,
    maxLines: Int = Int.MAX_VALUE,
    lineHeight: Float = 0f,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontFamily = Manrope,
        fontSize = size.sp,
        fontWeight = weight,
        letterSpacing = letterSpacing.sp,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        lineHeight = if (lineHeight > 0) lineHeight.sp else androidx.compose.ui.unit.TextUnit.Unspecified,
    )
}

/** Chip de status contornado. */
@Composable
fun StatusChip(tipo: StatusTipo, label: String, small: Boolean = false) {
    val t = Sh.statusToken(tipo)
    val padH = if (small) 8 else 10
    val padV = if (small) 2 else 4
    val fs = if (small) 11.5f else 12.5f
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(t.bg)
            .border(1.dp, t.bd, RoundedCornerShape(8.dp))
            .padding(horizontal = padH.dp, vertical = padV.dp),
    ) {
        Box(
            Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(t.fg)
        )
        T(label, fs, FontWeight.Bold, t.fg, letterSpacing = 0.1f, maxLines = 1)
    }
}

/** Chip neutro de categoria/filtro. */
@Composable
fun Chip(text: String, active: Boolean, onClick: () -> Unit = {}) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (active) Sh.brandTint else Sh.surface)
            .border(1.dp, if (active) Sh.brand else Sh.borderStrong, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        T(text, 13.5f, FontWeight.SemiBold, if (active) Sh.brandDark else Sh.ink2, maxLines = 1)
    }
}

/** Card branco padrão. */
@Composable
fun Card(
    modifier: Modifier = Modifier,
    radius: Int = Sh.radius,
    content: @Composable () -> Unit,
) {
    Box(
        modifier
            .clip(RoundedCornerShape(radius.dp))
            .background(Sh.surface)
            .border(1.dp, Sh.border, RoundedCornerShape(radius.dp))
    ) { content() }
}

/** Avatar circular com iniciais. */
@Composable
fun Avatar(iniciais: String, size: Int = 40, onClick: (() -> Unit)? = null) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Sh.brandTint)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
    ) {
        T(iniciais, size * 0.36f, FontWeight.ExtraBold, Sh.brandDark)
    }
}

/** Top app bar genérica. */
@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    right: (@Composable () -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(Sh.bg)
            .height(56.dp)
            .padding(horizontal = 8.dp),
    ) {
        if (onBack != null) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable { onBack() },
            ) { Icon("back", 22.dp, color = Sh.ink) }
        }
        Box(Modifier.weight(1f).padding(start = if (onBack != null) 0.dp else 8.dp)) {
            androidx.compose.foundation.layout.Column {
                T(title, 19f, FontWeight.ExtraBold, Sh.ink, lineHeight = 23f)
                if (subtitle != null) {
                    Spacer(Modifier.height(2.dp))
                    T(subtitle, 12.5f, FontWeight.Normal, Sh.ink3)
                }
            }
        }
        if (right != null) right()
    }
}

/** Botões: primary, secondary, danger. */
@Composable
fun Button(
    text: String,
    onClick: () -> Unit,
    variant: String = "primary",
    icon: String? = null,
    full: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val bg: Color; val fg: Color; val bd: Color?
    when (variant) {
        "secondary" -> { bg = Sh.surface; fg = Sh.ink; bd = Sh.borderStrong }
        "danger" -> { bg = Sh.danger.bg; fg = Sh.danger.fg; bd = Sh.danger.bd }
        else -> { bg = Sh.brand; fg = Sh.surface; bd = null }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .then(if (full) Modifier.fillMaxWidth() else Modifier)
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .then(if (bd != null) Modifier.border(1.dp, bd, RoundedCornerShape(14.dp)) else Modifier)
            .clickable { onClick() }
            .padding(horizontal = 18.dp),
    ) {
        if (icon != null) {
            Icon(icon, 20.dp, stroke = 2f, color = fg)
            Spacer(Modifier.width(8.dp))
        }
        T(text, 15.5f, FontWeight.ExtraBold, fg, maxLines = 1)
    }
}

/**
 * Campo de formulário. Se [onValueChange] for fornecido, vira um campo de texto
 * editável (BasicTextField); caso contrário, exibe [value] de forma estática.
 */
@Composable
fun Field(
    label: String,
    value: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    placeholder: String? = null,
    icon: String? = null,
    helper: String? = null,
    error: String? = null,
    focus: Boolean = false,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val filled = !value.isNullOrEmpty()
    val borderColor = when {
        error != null -> Sh.danger.fg
        focus -> Sh.brand
        else -> Sh.border
    }
    androidx.compose.foundation.layout.Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        T(label, 13f, FontWeight.Bold, Sh.ink2)
        Spacer(Modifier.height(7.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Sh.surface)
                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp),
        ) {
            if (icon != null) {
                Icon(icon, 20.dp, color = if (error != null) Sh.danger.fg else if (focus) Sh.brand else Sh.ink3)
            }
            if (onValueChange != null) {
                BasicTextField(
                    value = value ?: "",
                    onValueChange = onValueChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Sh.ink, fontFamily = Manrope,
                        fontSize = 15.5.sp, fontWeight = FontWeight.Medium,
                    ),
                    cursorBrush = SolidColor(Sh.brand),
                    visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    modifier = Modifier.weight(1f),
                    decorationBox = { inner ->
                        if (!filled && placeholder != null) {
                            T(placeholder, 15.5f, FontWeight.Medium, Sh.ink3, maxLines = 1)
                        }
                        inner()
                    },
                )
            } else {
                T(
                    if (filled) value!! else (placeholder ?: ""),
                    15.5f, FontWeight.Medium,
                    if (filled) Sh.ink else Sh.ink3,
                    modifier = Modifier.weight(1f), maxLines = 1,
                )
            }
            if (trailing != null) trailing()
        }
        if (error != null || helper != null) {
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                if (error != null) Icon("alert", 14.dp, stroke = 2.2f, color = Sh.danger.fg)
                T(error ?: helper!!, 12.5f, FontWeight.SemiBold, if (error != null) Sh.danger.fg else Sh.ink3)
            }
        }
    }
}

/** Selo de notificação com o número perfeitamente centralizado. */
@Composable
fun NotificationBadge(count: Int, modifier: Modifier = Modifier) {
    val texto = if (count > 9) "9+" else count.toString()
    val fs = if (count > 9) 8.5f else 10f
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(17.dp)
            .clip(CircleShape)
            .background(Sh.danger.fg)
            .border(1.5.dp, Sh.surface, CircleShape),
    ) {
        Text(
            text = texto,
            color = Color.White,
            fontFamily = Manrope,
            fontSize = fs.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            lineHeight = fs.sp,
            maxLines = 1,
            // Remove o padding/altura-de-linha extra da fonte para centralizar o dígito.
            style = TextStyle(
                platformStyle = PlatformTextStyle(includeFontPadding = false),
                lineHeightStyle = LineHeightStyle(
                    alignment = LineHeightStyle.Alignment.Center,
                    trim = LineHeightStyle.Trim.Both,
                ),
            ),
        )
    }
}

/** Bottom navigation (3 abas). */
@Composable
fun BottomNav(current: String, go: (String, Any?) -> Unit, alertas: Int = 0) {
    data class Aba(val id: String, val icon: String, val label: String, val badge: Int = 0)
    val abas = listOf(
        Aba("home", "home", "Início"),
        Aba("itens", "list", "Itens"),
        Aba("alertas", "bell", "Alertas", alertas),
    )
    androidx.compose.foundation.layout.Column(Modifier.fillMaxWidth().background(Sh.surface)) {
        Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 8.dp, top = 8.dp, bottom = 10.dp),
        ) {
        abas.forEach { a ->
            val on = current == a.id
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { go(a.id, null) }
                    .padding(vertical = 4.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (on) Sh.brandTint else Color.Transparent)
                            .padding(horizontal = 18.dp, vertical = 4.dp),
                    ) {
                        Icon(a.icon, 23.dp, stroke = if (on) 2.1f else 1.8f, color = if (on) Sh.brandDark else Sh.ink2)
                    }
                    if (a.badge > 0) {
                        NotificationBadge(
                            count = a.badge,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 9.dp),
                        )
                    }
                }
                T(a.label, 11.5f, if (on) FontWeight.ExtraBold else FontWeight.SemiBold, if (on) Sh.brandDark else Sh.ink2)
            }
        }
        }
    }
}

/** FAB do StockHome. */
@Composable
fun Fab(onClick: () -> Unit, label: String? = null) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .height(56.dp)
            .then(if (label == null) Modifier.width(56.dp) else Modifier)
            .clip(RoundedCornerShape(18.dp))
            .background(Sh.brand)
            .clickable { onClick() }
            .padding(horizontal = if (label != null) 16.dp else 0.dp),
    ) {
        if (label == null) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Icon("plus", 24.dp, stroke = 2.2f, color = Color.White)
            }
        } else {
            Icon("plus", 24.dp, stroke = 2.2f, color = Color.White)
            T(label, 15f, FontWeight.ExtraBold, Color.White)
        }
    }
}
