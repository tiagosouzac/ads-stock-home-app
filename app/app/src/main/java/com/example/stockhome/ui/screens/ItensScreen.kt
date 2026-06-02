package com.example.stockhome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.ui.theme.Manrope
import com.example.stockhome.ui.components.Avatar
import com.example.stockhome.ui.components.BottomNav
import com.example.stockhome.ui.components.Card
import com.example.stockhome.ui.components.Chip
import com.example.stockhome.ui.components.Fab
import com.example.stockhome.ui.components.ItemRow
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.ItensViewModel

@Composable
fun ItensScreen(
    go: (String, Any?) -> Unit,
    vm: ItensViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    LaunchedEffect(Unit) { vm.recarregar() }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(
            title = "Itens",
            right = { Avatar(state.iniciais, 38) { go("perfil", null) } },
        )

        Box(Modifier.weight(1f)) {
            Column(
                Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 96.dp),
            ) {
                // Busca
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
                    BasicTextField(
                        value = state.termoBusca,
                        onValueChange = { vm.onBuscaChange(it) },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = Sh.ink, fontFamily = Manrope,
                            fontSize = 15.sp, fontWeight = FontWeight.Medium,
                        ),
                        cursorBrush = SolidColor(Sh.brand),
                        modifier = Modifier.weight(1f),
                        decorationBox = { inner ->
                            if (state.termoBusca.isBlank()) {
                                T("Buscar por nome…", 15f, FontWeight.Medium, Sh.ink3, maxLines = 1)
                            }
                            inner()
                        },
                    )
                }
                Spacer(Modifier.height(14.dp))

                // Filtro de categoria
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    state.categorias.forEachIndexed { i, c ->
                        Chip(
                            text = c,
                            active = i == state.categoriaSelecionada,
                            onClick = { vm.onCategoriaSelecionada(i) },
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))

                // Filtro de status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon("filter", 18.dp, color = Sh.ink3)
                    Row(
                        Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        state.filtrosStatus.forEachIndexed { i, s ->
                            Chip(
                                text = s,
                                active = i == state.statusSelecionado,
                                onClick = { vm.onStatusSelecionado(i) },
                            )
                        }
                    }
                }
                Spacer(Modifier.height(14.dp))

                T(
                    "${state.itensFiltrados.size} itens",
                    12.5f, FontWeight.Bold, Sh.ink3,
                    modifier = Modifier.padding(start = 4.dp),
                )
                Spacer(Modifier.height(4.dp))

                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
                        state.itensFiltrados.forEachIndexed { i, p ->
                            ItemRow(p, go, i == state.itensFiltrados.lastIndex)
                        }
                    }
                }
            }

            Box(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 20.dp)) {
                Fab(onClick = { go("form", "novo") })
            }
        }

        BottomNav("itens", go, state.totalAlertas)
    }
}
