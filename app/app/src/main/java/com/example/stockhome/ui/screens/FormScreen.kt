package com.example.stockhome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Field
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.FormViewModel

@Composable
fun FormScreen(
    go: (String, Any?) -> Unit,
    id: Any?,
    vm: FormViewModel = viewModel(key = "form_$id", factory = FormViewModel.Factory(id)),
) {
    val state by vm.uiState.collectAsState()
    val voltar = { if (state.editando) go("detalhe", id) else go("itens", null) }

    // Volta para a lista assim que o produto for salvo na API.
    LaunchedEffect(state.salvo) { if (state.salvo) go("itens", null) }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(
            title = if (state.editando) "Editar item" else "Adicionar item",
            onBack = { voltar() },
        )
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
        ) {
            Field(
                label = "Nome do produto",
                value = state.nomeProduto,
                onValueChange = vm::onNomeChange,
                placeholder = "Ex.: Arroz branco 5kg",
            )
            Field(
                label = "Categoria",
                value = state.nomeCategoria,
                icon = "tag",
                trailing = { Icon("chevD", 20.dp, color = Sh.ink3) },
                onClick = vm::proximaCategoria,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stepper(
                    label = "Quantidade atual",
                    value = state.qtdAtual,
                    onIncrement = vm::incrementarQtdAtual,
                    onDecrement = vm::decrementarQtdAtual,
                    modifier = Modifier.weight(1f),
                )
                Stepper(
                    label = "Quantidade mínima",
                    value = state.qtdMinima,
                    onIncrement = vm::incrementarQtdMinima,
                    onDecrement = vm::decrementarQtdMinima,
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(10.dp))
            T(
                "Você será avisado quando o estoque ficar abaixo do mínimo.",
                12f, FontWeight.SemiBold, Sh.ink3,
                modifier = Modifier.padding(horizontal = 4.dp),
            )
            Spacer(Modifier.height(18.dp))
            Field(
                label = "Data de validade (opcional)",
                value = state.validade,
                onValueChange = vm::onValidadeChange,
                placeholder = "aaaa-mm-dd",
                icon = "calendar",
            )
            if (state.erro != null) {
                Spacer(Modifier.height(2.dp))
                T(state.erro!!, 13f, FontWeight.SemiBold, Sh.danger.fg)
            }
        }

        // Barra de ações
        Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
        Row(
            Modifier.fillMaxWidth().background(Sh.bg).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button("Cancelar", { voltar() }, variant = "secondary", full = false)
            Button(
                if (state.loading) "Salvando…" else "Salvar",
                { vm.salvar() },
                icon = "check",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// ── Composables locais ─────────────────────────────────────────

@Composable
private fun Stepper(
    label: String,
    value: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        T(label, 13f, FontWeight.Bold, Sh.ink2)
        Spacer(Modifier.height(7.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().height(52.dp)
                .clip(RoundedCornerShape(14.dp)).background(Sh.surface)
                .border(1.dp, Sh.border, RoundedCornerShape(14.dp))
                .padding(horizontal = 8.dp),
        ) {
            StepBtn("minus", Sh.borderStrong, Sh.ink, onClick = onDecrement)
            T(value.toString(), 17f, FontWeight.ExtraBold, Sh.ink)
            StepBtn("plus", Sh.brand, Sh.brand, onClick = onIncrement)
        }
    }
}

@Composable
private fun StepBtn(
    icon: String,
    borderColor: androidx.compose.ui.graphics.Color,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(11.dp)).background(Sh.surface)
            .border(1.dp, borderColor, RoundedCornerShape(11.dp))
            .clickable { onClick() },
    ) { Icon(icon, 18.dp, stroke = 2.2f, color = iconColor) }
}
