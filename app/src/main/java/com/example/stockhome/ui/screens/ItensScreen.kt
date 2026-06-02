package com.example.stockhome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.stockhome.data.CATEGORIAS
import com.example.stockhome.data.PRODUTOS
import com.example.stockhome.data.StatusTipo
import com.example.stockhome.data.USUARIO
import com.example.stockhome.data.statusItem
import com.example.stockhome.ui.components.Avatar
import com.example.stockhome.ui.components.BottomNav
import com.example.stockhome.ui.components.Card
import com.example.stockhome.ui.components.Chip
import com.example.stockhome.ui.components.Fab
import com.example.stockhome.ui.components.ItemRow
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh

private val ordemStatus = mapOf(
    StatusTipo.vencido to 0, StatusTipo.baixo to 1, StatusTipo.vencendo to 2, StatusTipo.ok to 3,
)

@Composable
fun ItensScreen(go: (String, Any?) -> Unit) {
    val cats = listOf("Todos") + CATEGORIAS.values.map { it.nome }
    val status = listOf("Todos", "Estoque baixo", "Vencendo", "OK")
    val lista = PRODUTOS.sortedWith(
        compareBy({ ordemStatus[statusItem(it).tipo] ?: 99 }, { it.nome })
    )
    val totalAlertas = PRODUTOS.count { statusItem(it).tipo != StatusTipo.ok }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBarItens(go)
        // O FAB vive dentro da área de conteúdo (acima da barra), então nunca a sobrepõe.
        Box(Modifier.weight(1f)) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 96.dp),
            ) {
                // Busca (estática)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth().height(48.dp)
                        .clip(RoundedCornerShape(14.dp)).background(Sh.surface)
                        .border(1.dp, Sh.border, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp),
                ) {
                    Icon("search", 20.dp, color = Sh.ink3)
                    T("Buscar por nome…", 15f, FontWeight.Medium, Sh.ink3)
                }
                Spacer(Modifier.height(14.dp))
                // Filtro de categoria
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    cats.forEachIndexed { i, c -> Chip(c, active = i == 0) }
                }
                Spacer(Modifier.height(12.dp))
                // Filtro de status
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon("filter", 18.dp, color = Sh.ink3)
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) { status.forEachIndexed { i, s -> Chip(s, active = i == 0) } }
                }
                Spacer(Modifier.height(14.dp))
                T("${lista.size} itens", 12.5f, FontWeight.Bold, Sh.ink3, modifier = Modifier.padding(start = 4.dp))
                Spacer(Modifier.height(4.dp))
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
                        lista.forEachIndexed { i, p -> ItemRow(p, go, i == lista.lastIndex) }
                    }
                }
            }
            Box(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 20.dp)) {
                Fab(onClick = { go("form", "novo") })
            }
        }
        BottomNav("itens", go, totalAlertas)
    }
}

@Composable
private fun TopBarItens(go: (String, Any?) -> Unit) {
    com.example.stockhome.ui.components.TopBar(
        title = "Itens",
        right = { Avatar(USUARIO.iniciais, 38) { go("perfil", null) } },
    )
}
