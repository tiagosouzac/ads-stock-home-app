package com.example.stockhome.data

import java.time.LocalDate

/**
 * Conversões entre os modelos da API (data/ApiModels.kt) e o modelo de
 * exibição usado pelas telas Compose (Produto / StatusTipo).
 *
 * As telas e os componentes compartilhados (ItemRow, StatusChip, …) foram
 * escritos sobre o modelo `Produto`; aqui adaptamos o que vem da API para
 * esse formato, mantendo uma única fonte de UI.
 */

/** Converte o status textual da API ("ok"/"low"/"expiring"/"expired") no enum local. */
fun statusTipoFromApi(type: String): StatusTipo = when (type) {
    "low" -> StatusTipo.baixo
    "expiring" -> StatusTipo.vencendo
    "expired" -> StatusTipo.vencido
    else -> StatusTipo.ok
}

/** Faz o parse de uma data "YYYY-MM-DD" da API; null/invalida vira null. */
fun parseIsoDate(s: String?): LocalDate? =
    s?.takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }

/** Mapeia o produto vindo da API para o modelo de exibição usado pelas telas. */
fun ApiProduct.toProduto(): Produto = Produto(
    id = id,
    nome = name,
    cat = categoryId,
    qtd = quantity,
    min = minQuantity,
    un = unit,
    validade = parseIsoDate(expiresAt),
    atualizado = parseIsoDate(lastUpdated) ?: LocalDate.now(),
)

fun List<ApiProduct>.toProdutos(): List<Produto> = map { it.toProduto() }
