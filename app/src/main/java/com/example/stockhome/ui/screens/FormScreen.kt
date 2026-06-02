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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.stockhome.data.CATEGORIAS
import com.example.stockhome.data.PRODUTOS
import com.example.stockhome.data.fmtData
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Field
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Sh

@Composable
private fun Stepper(label: String, value: Int, modifier: Modifier = Modifier) {
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
            StepBtn("minus", Sh.borderStrong, Sh.ink)
            T(value.toString(), 17f, FontWeight.ExtraBold, Sh.ink)
            StepBtn("plus", Sh.brand, Sh.brand)
        }
    }
}

@Composable
private fun StepBtn(icon: String, borderColor: androidx.compose.ui.graphics.Color, iconColor: androidx.compose.ui.graphics.Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(11.dp)).background(Sh.surface)
            .border(1.dp, borderColor, RoundedCornerShape(11.dp)),
    ) { Icon(icon, 18.dp, stroke = 2.2f, color = iconColor) }
}

@Composable
fun FormScreen(go: (String, Any?) -> Unit, id: Any?) {
    val editando = id != "novo" && id is Int
    val p = if (editando) (PRODUTOS.find { it.id == id } ?: PRODUTOS[2]) else null
    val voltar = { if (editando) go("detalhe", id) else go("itens", null) }

    Column(Modifier.fillMaxSize().background(Sh.bg)) {
        TopBar(title = if (editando) "Editar item" else "Adicionar item", onBack = { voltar() })
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp),
        ) {
            Field(label = "Nome do produto", value = p?.nome, placeholder = "Ex.: Arroz branco 5kg")
            Field(
                label = "Categoria",
                value = if (p != null) CATEGORIAS[p.cat]!!.nome else "Alimentos",
                icon = "tag",
                trailing = { Icon("chevD", 20.dp, color = Sh.ink3) },
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Stepper("Quantidade atual", p?.qtd ?: 1, Modifier.weight(1f))
                Stepper("Quantidade mínima", p?.min ?: 1, Modifier.weight(1f))
            }
            Spacer(Modifier.height(10.dp))
            T("Você será avisada quando o estoque ficar abaixo do mínimo.", 12f, FontWeight.SemiBold, Sh.ink3,
                modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(Modifier.height(18.dp))
            Field(
                label = "Data de validade (opcional)",
                value = if (p?.validade != null) fmtData(p.validade) else null,
                placeholder = "dd/mm/aaaa",
                icon = "calendar",
                trailing = { Icon("chevD", 20.dp, color = Sh.ink3) },
            )
        }
        // Barra de ações
        Box(Modifier.fillMaxWidth().height(1.dp).background(Sh.border))
        Row(
            Modifier.fillMaxWidth().background(Sh.bg).padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button("Cancelar", { voltar() }, variant = "secondary", full = false)
            Button("Salvar", { voltar() }, icon = "check", modifier = Modifier.weight(1f))
        }
    }
}
