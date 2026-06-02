package com.example.stockhome.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.ui.components.Avatar
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Field
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.PerfilViewModel

@Composable
fun EditarPerfilScreen(
    go: (String, Any?) -> Unit,
    vm: PerfilViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()

    // Volta para o perfil assim que o nome for salvo na API.
    LaunchedEffect(state.salvo) {
        if (state.salvo) {
            vm.limparSalvo()
            go("perfil", null)
        }
    }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(title = "Editar perfil", onBack = { go("perfil", null) })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
        ) {
            // Avatar com as iniciais atuais.
            Column(
                Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Avatar(state.iniciais, 76)
            }

            Field(
                label = "Nome completo",
                value = state.nome,
                onValueChange = vm::onNomeChange,
                placeholder = "Seu nome",
                icon = "user",
                error = state.erroNome,
            )
            // E-mail não é editável pela API.
            Field(
                label = "E-mail",
                value = state.email,
                icon = "mail",
                helper = "O e-mail não pode ser alterado.",
            )
        }

        // Barra de ações
        Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
        Column(Modifier.fillMaxWidth().background(Sh.bg).padding(20.dp)) {
            Button(
                if (state.salvando) "Salvando…" else "Salvar alterações",
                { vm.salvarNome() },
                icon = "check",
            )
        }
    }
}
