package com.example.stockhome.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stockhome.data.CATEGORIAS
import com.example.stockhome.data.Produto
import com.example.stockhome.data.StatusTipo
import com.example.stockhome.data.diasAte
import com.example.stockhome.data.statusItem
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Manrope
import com.example.stockhome.ui.theme.Sh
import kotlinx.coroutines.launch

@Composable
fun Logo(size: Int = 64) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size * 0.28f).dp))
            .background(Sh.brand),
    ) {
        Icon("box", (size * 0.52f).dp, stroke = 1.7f, color = androidx.compose.ui.graphics.Color.White)
    }
}

@Composable
fun Wordmark(fs: Float = 26f) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = Sh.ink)) { append("Stock") }
            withStyle(SpanStyle(color = Sh.brand)) { append("Home") }
        },
        fontFamily = Manrope,
        fontSize = fs.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.5).sp,
    )
}

fun shortStatus(p: Produto): Pair<StatusTipo, String> {
    val s = statusItem(p)
    return when (s.tipo) {
        StatusTipo.vencido -> StatusTipo.vencido to "Vencido"
        StatusTipo.baixo -> StatusTipo.baixo to "Baixo"
        StatusTipo.vencendo -> {
            val d = diasAte(p.validade)
            StatusTipo.vencendo to (if (d == 0) "Vence hoje" else "Vence ${d}d")
        }
        StatusTipo.ok -> StatusTipo.ok to "OK"
    }
}

/** Linha de item (reutilizada em Itens, Alertas, Home) — agora interativa. */
@Composable
fun ItemRow(p: Produto, go: (String, Any?) -> Unit, last: Boolean) {
    val (tipo, label) = shortStatus(p)
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    // Animações de toque: um leve "afundar" + um brilho que se apaga.
    val scale = remember { Animatable(1f) }
    val flash = remember { Animatable(0f) }

    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale.value)
                .clip(RoundedCornerShape(12.dp))
                .background(Sh.brandTint.copy(alpha = flash.value))
                .clickable {
                    // Efeito sonoro + tátil imediato a cada clique.
                    ClickFeedback.play(view)
                    scope.launch {
                        // Pulso de destaque que se desfaz.
                        launch {
                            flash.snapTo(0.55f)
                            flash.animateTo(0f, tween(360))
                        }
                        // Pequena animação de pressionar e soltar…
                        scale.animateTo(0.95f, tween(70))
                        scale.animateTo(1f, tween(120))
                        // …e só então navega, para que a animação seja percebida.
                        go("detalhe", p.id)
                    }
                }
                .padding(horizontal = 4.dp, vertical = 13.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Sh.bg),
            ) { Icon("box", 22.dp, stroke = 1.7f, color = Sh.ink2) }
            Spacer(Modifier.width(13.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    p.nome, color = Sh.ink, fontFamily = Manrope, fontSize = 15.sp,
                    fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                T("${p.qtd} ${p.un} · ${CATEGORIAS[p.cat]!!.nome}", 13f, FontWeight.SemiBold, Sh.ink3, maxLines = 1)
            }
            Spacer(Modifier.width(13.dp))
            StatusChip(tipo, label, small = true)
        }
        if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
    }
}
