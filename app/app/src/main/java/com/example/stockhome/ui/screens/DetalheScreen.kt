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
import androidx.compose.foundation.layout.width
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
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Card
import com.example.stockhome.ui.components.StatusChip
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.DetalheViewModel

@Composable
fun DetalheScreen(
    go: (String, Any?) -> Unit,
    id: Int,
    vm: DetalheViewModel = viewModel(factory = DetalheViewModel.Factory(id)),
) {
    val state by vm.uiState.collectAsState()
    val p = state.produto ?: return
    val s = state.status ?: return

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(title = "Detalhe do item", onBack = { go("itens", null) })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 24.dp),
        ) {
            // Cabeçalho
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp))
                        .background(Sh.surface).border(1.dp, Sh.border, RoundedCornerShape(18.dp)),
                ) { Icon("box", 32.dp, stroke = 1.6f, color = Sh.brand) }
                Spacer(Modifier.width(16.dp))
                Column {
                    T(p.nome, 21f, FontWeight.ExtraBold, Sh.ink, letterSpacing = -0.4f, lineHeight = 25f)
                    Spacer(Modifier.height(8.dp))
                    StatusChip(s.tipo, s.label)
                }
            }
            Spacer(Modifier.height(18.dp))

            // Quantidades
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatBlock(
                    valor = p.qtd.toString(), sub = p.un,
                    label = "Quantidade atual", alerta = state.estoqueBaixo,
                    modifier = Modifier.weight(1f),
                )
                StatBlock(
                    valor = p.min.toString(), sub = p.un,
                    label = "Quantidade mínima",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(18.dp))

            // Informações
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    InfoRow("tag", "Categoria", state.nomeCategoria)
                    InfoRow("calendar", "Validade", state.validadeFmt)
                    InfoRow("clock", "Última atualização", state.atualizadoFmt, last = true)
                }
            }
            Spacer(Modifier.height(22.dp))

            // Ações
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button("Editar item", { go("form", p.id) }, icon = "edit")
                Button("Excluir item", { go("itens", null) }, variant = "danger", icon = "trash")
            }
        }
    }
}

// ── Composables locais ─────────────────────────────────────────

@Composable
private fun StatBlock(
    valor: String,
    sub: String,
    label: String,
    alerta: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier
            .clip(RoundedCornerShape(14.dp)).background(Sh.surface)
            .border(1.dp, Sh.border, RoundedCornerShape(14.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        T(label, 12.5f, FontWeight.Bold, Sh.ink3)
        Spacer(Modifier.height(6.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            T(valor, 26f, FontWeight.ExtraBold, if (alerta) Sh.danger.fg else Sh.ink, lineHeight = 26f)
            T(sub, 13f, FontWeight.SemiBold, Sh.ink3)
        }
    }
}

@Composable
private fun InfoRow(icon: String, label: String, value: String, last: Boolean = false) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        ) {
            Icon(icon, 20.dp, color = Sh.ink3)
            Spacer(Modifier.width(13.dp))
            T(label, 14.5f, FontWeight.SemiBold, Sh.ink2, modifier = Modifier.weight(1f))
            T(value, 14.5f, FontWeight.Bold, Sh.ink)
        }
        if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
    }
}
