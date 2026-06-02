package com.example.stockhome.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Field
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.AlterarSenhaViewModel

@Composable
fun AlterarSenhaScreen(
    go: (String, Any?) -> Unit,
    vm: AlterarSenhaViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    var atual by remember { mutableStateOf("") }
    var nova by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verSenha by remember { mutableStateOf(false) }

    LaunchedEffect(state.sucesso) {
        if (state.sucesso) {
            vm.limparSucesso()
            Toast.makeText(context, "Senha alterada com sucesso.", Toast.LENGTH_SHORT).show()
            go("perfil", null)
        }
    }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(title = "Alterar senha", onBack = { go("perfil", null) })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp),
        ) {
            T(
                "Use ao menos 6 caracteres. Você precisará informar sua senha atual.",
                15f, FontWeight.SemiBold, Sh.ink3,
            )
            Spacer(Modifier.height(24.dp))

            Field(
                label = "Senha atual",
                value = atual,
                onValueChange = { atual = it; vm.limparErro() },
                placeholder = "Sua senha atual",
                icon = "lock",
                isPassword = !verSenha,
                keyboardType = KeyboardType.Password,
            )
            Field(
                label = "Nova senha",
                value = nova,
                onValueChange = { nova = it; vm.limparErro() },
                placeholder = "Ao menos 6 caracteres",
                icon = "key",
                isPassword = !verSenha,
                keyboardType = KeyboardType.Password,
                trailing = {
                    Box(Modifier.clickable { verSenha = !verSenha }) {
                        Icon("eye", 20.dp, color = if (verSenha) Sh.brand else Sh.ink3)
                    }
                },
            )
            Field(
                label = "Confirmar nova senha",
                value = confirmar,
                onValueChange = { confirmar = it; vm.limparErro() },
                placeholder = "Repita a nova senha",
                icon = "key",
                isPassword = !verSenha,
                keyboardType = KeyboardType.Password,
                error = state.erro,
            )
        }

        // Barra de ações
        Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
        Column(Modifier.fillMaxWidth().background(Sh.bg).padding(20.dp)) {
            Button(
                if (state.loading) "Salvando…" else "Salvar nova senha",
                { vm.alterar(atual, nova, confirmar) },
                icon = "check",
            )
        }
    }
}
