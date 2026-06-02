package com.example.stockhome.data

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Porta fiel de sh-data.jsx — dados mock do StockHome (produtos BR),
 * categorias, usuário e helpers de status/datas.
 *
 * "Hoje" = data atual do dispositivo, para que o status calculado localmente
 * (ItemRow) coincida com o status calculado pela API.
 */
val HOJE: LocalDate get() = LocalDate.now()

data class Categoria(val id: String, val nome: String)

val CATEGORIAS: Map<String, Categoria> = linkedMapOf(
    "alimentos" to Categoria("alimentos", "Alimentos"),
    "bebidas" to Categoria("bebidas", "Bebidas"),
    "limpeza" to Categoria("limpeza", "Limpeza"),
    "higiene" to Categoria("higiene", "Higiene"),
)

data class Usuario(
    val nome: String,
    val email: String,
    val iniciais: String,
    val diasAlerta: Int,
)

val USUARIO = Usuario(
    nome = "Marina Alves",
    email = "marina.alves@email.com",
    iniciais = "MA",
    diasAlerta = 7,
)

data class Produto(
    val id: Int,
    val nome: String,
    val cat: String,
    val qtd: Int,
    val min: Int,
    val un: String,
    val validade: LocalDate?,
    val atualizado: LocalDate,
)

// mês 1-indexado no LocalDate (diferente do JS que era 0-indexado)
val PRODUTOS: List<Produto> = listOf(
    Produto(1, "Arroz branco 5kg", "alimentos", 2, 1, "pacotes", LocalDate.of(2026, 11, 10), LocalDate.of(2026, 5, 28)),
    Produto(2, "Feijão carioca 1kg", "alimentos", 1, 2, "pacotes", LocalDate.of(2026, 9, 1), LocalDate.of(2026, 5, 30)),
    Produto(3, "Café torrado 500g", "alimentos", 1, 1, "pacote", LocalDate.of(2026, 6, 5), LocalDate.of(2026, 5, 20)),
    Produto(4, "Leite integral 1L", "bebidas", 6, 4, "caixas", LocalDate.of(2026, 6, 4), LocalDate.of(2026, 6, 1)),
    Produto(5, "Açúcar refinado 1kg", "alimentos", 0, 1, "pacotes", LocalDate.of(2026, 12, 2), LocalDate.of(2026, 5, 25)),
    Produto(6, "Óleo de soja 900ml", "alimentos", 3, 1, "frascos", LocalDate.of(2026, 10, 15), LocalDate.of(2026, 5, 18)),
    Produto(7, "Macarrão espaguete 500g", "alimentos", 4, 2, "pacotes", LocalDate.of(2027, 2, 10), LocalDate.of(2026, 5, 12)),
    Produto(8, "Iogurte natural 170g", "bebidas", 4, 2, "potes", LocalDate.of(2026, 6, 6), LocalDate.of(2026, 5, 31)),
    Produto(9, "Detergente neutro 500ml", "limpeza", 1, 2, "frascos", null, LocalDate.of(2026, 5, 22)),
    Produto(10, "Sabão em pó 1kg", "limpeza", 2, 1, "caixas", null, LocalDate.of(2026, 5, 10)),
    Produto(11, "Amaciante 2L", "limpeza", 0, 1, "frascos", null, LocalDate.of(2026, 5, 27)),
    Produto(12, "Papel higiênico 12un", "higiene", 1, 1, "pacotes", null, LocalDate.of(2026, 5, 16)),
    Produto(13, "Creme dental 90g", "higiene", 3, 1, "tubos", LocalDate.of(2027, 3, 1), LocalDate.of(2026, 5, 8)),
    Produto(14, "Sabonete 85g", "higiene", 5, 2, "barras", null, LocalDate.of(2026, 4, 30)),
    Produto(15, "Farinha de trigo 1kg", "alimentos", 1, 1, "pacotes", LocalDate.of(2026, 8, 20), LocalDate.of(2026, 5, 5)),
)

// ── Helpers de status ─────────────────────────────────────────
enum class StatusTipo { ok, baixo, vencendo, vencido }

data class Status(val tipo: StatusTipo, val label: String)

fun diasAte(data: LocalDate?): Int? {
    if (data == null) return null
    return ChronoUnit.DAYS.between(HOJE, data).toInt()
}

fun statusItem(p: Produto, diasAlerta: Int = USUARIO.diasAlerta): Status {
    val d = diasAte(p.validade)
    val baixo = p.qtd < p.min || p.qtd == 0
    if (d != null && d < 0) return Status(StatusTipo.vencido, "Vencido")
    if (baixo) return Status(StatusTipo.baixo, "Estoque baixo")
    if (d != null && d <= diasAlerta) return Status(StatusTipo.vencendo, "Vence em breve")
    return Status(StatusTipo.ok, "OK")
}

private val MESES = listOf("jan", "fev", "mar", "abr", "mai", "jun", "jul", "ago", "set", "out", "nov", "dez")

fun fmtData(d: LocalDate?): String {
    if (d == null) return "—"
    val dd = d.dayOfMonth.toString().padStart(2, '0')
    val mm = d.monthValue.toString().padStart(2, '0')
    return "$dd/$mm/${d.year}"
}

fun fmtDataLonga(d: LocalDate?): String {
    if (d == null) return "—"
    return "${d.dayOfMonth} de ${MESES[d.monthValue - 1]}. de ${d.year}"
}

// ── Resumos para o dashboard ──────────────────────────────────
data class Resumo(val total: Int, val baixos: List<Produto>, val vencendo: List<Produto>)

fun resumo(): Resumo {
    val total = PRODUTOS.size
    val baixos = PRODUTOS.filter { statusItem(it).tipo == StatusTipo.baixo }
    val vencendo = PRODUTOS.filter {
        val d = diasAte(it.validade); d != null && d >= 0 && d <= USUARIO.diasAlerta
    }
    return Resumo(total, baixos, vencendo)
}
