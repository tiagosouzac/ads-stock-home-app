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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.stockhome.data.USUARIO
import com.example.stockhome.data.resumo
import com.example.stockhome.ui.components.Avatar
import com.example.stockhome.ui.components.BottomNav
import com.example.stockhome.ui.components.Card
import com.example.stockhome.ui.components.ItemRow
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh

@Composable
private fun StatTile(valor: String, label: String, tom: String? = null, modifier: Modifier = Modifier) {
    val cor = when (tom) {
        "baixo" -> Sh.danger.fg
        "vencendo" -> Sh.warn.fg
        else -> Sh.brand
    }
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Sh.surface)
            .border(1.dp, Sh.border, RoundedCornerShape(16.dp))
            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 13.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(Modifier.size(9.dp).clip(CircleShape).background(cor))
            T(valor, 27f, FontWeight.ExtraBold, Sh.ink, lineHeight = 27f)
        }
        Spacer(Modifier.height(9.dp))
        T(label, 12.5f, FontWeight.Bold, Sh.ink2, lineHeight = 16f)
    }
}

@Composable
private fun QuickCard(icon: String, titulo: String, sub: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Sh.surface)
            .border(1.dp, Sh.border, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(Sh.brandTint),
        ) { Icon(icon, 21.dp, stroke = 1.9f, color = Sh.brandDark) }
        Column {
            T(titulo, 14.5f, FontWeight.ExtraBold, Sh.ink)
            Spacer(Modifier.height(2.dp))
            T(sub, 12.5f, FontWeight.SemiBold, Sh.ink3)
        }
    }
}

@Composable
private fun SectionHead(titulo: String, acao: String? = null, onAcao: () -> Unit = {}) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom,
    ) {
        T(titulo, 16f, FontWeight.ExtraBold, Sh.ink)
        if (acao != null) Box(Modifier.clickable { onAcao() }) { T(acao, 13.5f, FontWeight.Bold, Sh.brandDark) }
    }
}

@Composable
fun HomeScreen(go: (String, Any?) -> Unit) {
    val r = resumo()
    val atencao = (r.vencendo + r.baixos).take(3)
    val totalAlertas = (r.vencendo + r.baixos).map { it.id }.toSet().size
    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 24.dp),
        ) {
            // Saudação
            Row(
                Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    T("Olá, Marina 👋", 14f, FontWeight.Bold, Sh.ink3)
                    Spacer(Modifier.height(2.dp))
                    T("Sua despensa hoje", 23f, FontWeight.ExtraBold, Sh.ink, letterSpacing = -0.5f)
                }
                Avatar(USUARIO.iniciais, 42) { go("perfil", null) }
            }
            // Resumo
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatTile(r.total.toString(), "Itens no estoque", modifier = Modifier.weight(1f))
                StatTile(r.baixos.size.toString(), "Com estoque baixo", "baixo", Modifier.weight(1f))
                StatTile(r.vencendo.size.toString(), "Vencendo em breve", "vencendo", Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))
            // Precisa de atenção
            SectionHead("Precisa de atenção", "Ver alertas") { go("alertas", null) }
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(horizontal = 14.dp, vertical = 2.dp)) {
                    atencao.forEachIndexed { i, p -> ItemRow(p, go, i == atencao.lastIndex) }
                }
            }
            Spacer(Modifier.height(24.dp))
            // Acesso rápido
            SectionHead("Acesso rápido")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickCard("list", "Todos os itens", "${r.total} cadastrados", { go("itens", null) }, Modifier.weight(1f))
                QuickCard("plus", "Adicionar", "Novo produto", { go("form", "novo") }, Modifier.weight(1f))
            }
        }
        BottomNav("home", go, totalAlertas)
    }
}
