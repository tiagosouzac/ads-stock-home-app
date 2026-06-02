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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.ui.components.Avatar
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Card
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Manrope
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.PerfilViewModel

@Composable
fun PerfilScreen(
    go: (String, Any?) -> Unit,
    vm: PerfilViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    LaunchedEffect(Unit) { vm.carregarPerfil() }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(title = "Perfil", onBack = { go("home", null) })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 24.dp),
        ) {
            // Identidade
            Column(
                Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Avatar(state.iniciais, 76)
                Spacer(Modifier.height(14.dp))
                T(state.nome, 20f, FontWeight.ExtraBold, Sh.ink)
                Spacer(Modifier.height(2.dp))
                T(state.email, 14f, FontWeight.SemiBold, Sh.ink3)
            }

            // Conta
            SectionLabel("CONTA")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    MenuRow("user", "Editar perfil", onClick = { go("editarPerfil", null) })
                    MenuRow("key", "Alterar senha", last = true, onClick = { go("alterarSenha", null) })
                }
            }
            Spacer(Modifier.height(22.dp))

            // Alertas de validade
            SectionLabel("ALERTAS DE VALIDADE")
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    T("Avisar com antecedência de", 14.5f, FontWeight.Bold, Sh.ink)
                    Spacer(Modifier.height(3.dp))
                    T("Dias antes da data de validade.", 13f, FontWeight.SemiBold, Sh.ink3)
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        state.opcoesAlerta.forEach { d ->
                            val on = d == state.diasAlerta
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.weight(1f).height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (on) Sh.brandTint else Sh.surface)
                                    .border(1.5.dp, if (on) Sh.brand else Sh.border, RoundedCornerShape(12.dp))
                                    .clickable { vm.onDiasAlertaChange(d) },
                            ) {
                                T("$d dias", 14.5f, FontWeight.ExtraBold, if (on) Sh.brandDark else Sh.ink2)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(22.dp))

            Button("Sair da conta", { vm.sair(); go("login", null) }, variant = "danger", icon = "logout")
            Spacer(Modifier.height(18.dp))

            Text(
                "StockHome · versão 1.0.0",
                color = Sh.ink3, fontFamily = Manrope,
                fontSize = 12.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// ── Composables locais ─────────────────────────────────────────

@Composable
private fun MenuRow(
    icon: String,
    label: String,
    value: String? = null,
    last: Boolean = false,
    onClick: () -> Unit = {},
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 15.dp),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Sh.bg),
            ) { Icon(icon, 19.dp, color = Sh.ink2) }
            Spacer(Modifier.width(13.dp))
            T(label, 15f, FontWeight.Bold, Sh.ink, modifier = Modifier.weight(1f))
            if (value != null) {
                T(value, 13.5f, FontWeight.SemiBold, Sh.ink3)
                Spacer(Modifier.width(8.dp))
            }
            Icon("chevR", 18.dp, color = Sh.ink3)
        }
        if (!last) Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
    }
}

@Composable
private fun SectionLabel(text: String) {
    T(
        text, 12.5f, FontWeight.ExtraBold, Sh.ink3, letterSpacing = 0.6f,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )
}
