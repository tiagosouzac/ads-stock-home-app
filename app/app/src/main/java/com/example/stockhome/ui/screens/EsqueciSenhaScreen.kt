package com.example.stockhome.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.stockhome.viewmodel.EsqueciSenhaViewModel

@Composable
fun EsqueciSenhaScreen(
    go: (String, Any?) -> Unit,
    vm: EsqueciSenhaViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var nova by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verSenha by remember { mutableStateOf(false) }

    LaunchedEffect(state.sucesso) {
        if (state.sucesso) {
            vm.limparSucesso()
            Toast.makeText(context, "Senha redefinida. Faça login com a nova senha.", Toast.LENGTH_LONG).show()
            go("login", null)
        }
    }

    Column(
        Modifier.fillMaxSize().background(Sh.bg).verticalScroll(rememberScrollState()),
    ) {
        TopBar(title = "Redefinir senha", onBack = { go("login", null) })
        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            T(
                "Informe seu e-mail e escolha uma nova senha para sua conta.",
                15f, FontWeight.SemiBold, Sh.ink3,
            )
            Spacer(Modifier.height(26.dp))

            Field(
                label = "E-mail",
                value = email,
                onValueChange = { email = it; vm.limparErro() },
                placeholder = "voce@email.com",
                icon = "mail",
                keyboardType = KeyboardType.Email,
            )
            Field(
                label = "Nova senha",
                value = nova,
                onValueChange = { nova = it; vm.limparErro() },
                placeholder = "Ao menos 6 caracteres",
                icon = "lock",
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
                icon = "lock",
                isPassword = !verSenha,
                keyboardType = KeyboardType.Password,
                error = state.erro,
            )
            Spacer(Modifier.height(8.dp))
            Button(
                if (state.loading) "Redefinindo…" else "Redefinir senha",
                { vm.redefinir(email, nova, confirmar) },
                icon = "check",
            )
        }
    }
}
