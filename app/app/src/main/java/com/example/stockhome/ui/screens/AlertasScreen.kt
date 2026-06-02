package com.example.stockhome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.data.Produto
import com.example.stockhome.ui.components.Avatar
import com.example.stockhome.ui.components.BottomNav
import com.example.stockhome.ui.components.Card
import com.example.stockhome.ui.components.ItemRow
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.AlertasViewModel

@Composable
fun AlertasScreen(
    go: (String, Any?) -> Unit,
    vm: AlertasViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(
            title = "Alertas",
            subtitle = "${state.totalAlertas} itens precisam de atenção",
            right = { Avatar(state.iniciais, 38) { go("perfil", null) } },
        )
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
        ) {
            AlertSection("Vencendo em breve", state.vencendo, go, baixo = false)
            AlertSection("Estoque baixo", state.baixos, go, baixo = true)
        }
        BottomNav("alertas", go, state.totalAlertas)
    }
}

// ── Composables locais ─────────────────────────────────────────

@Composable
private fun AlertSection(
    titulo: String,
    itens: List<Produto>,
    go: (String, Any?) -> Unit,
    baixo: Boolean,
) {
    val t = if (baixo) Sh.danger else Sh.warn
    Column(Modifier.padding(bottom = 22.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(9.dp)).background(t.bg)
                    .border(1.dp, t.bd, RoundedCornerShape(9.dp)),
            ) { Icon(if (baixo) "box" else "clock", 16.dp, stroke = 2f, color = t.fg) }
            T(titulo, 15.5f, FontWeight.ExtraBold, Sh.ink, modifier = Modifier.weight(1f))
            T(itens.size.toString(), 13f, FontWeight.ExtraBold, t.fg)
        }
        Spacer(Modifier.height(10.dp))
        if (itens.isNotEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
                    itens.forEachIndexed { i, p -> ItemRow(p, go, i == itens.lastIndex) }
                }
            }
        } else {
            Card(Modifier.fillMaxWidth()) {
                Box(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    T("Nada por aqui. Tudo certo! 🎉", 13.5f, FontWeight.SemiBold, Sh.ink3)
                }
            }
        }
    }
}
