package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.ApiCategory
import com.example.stockhome.data.ApiProduct
import com.example.stockhome.data.Produto
import com.example.stockhome.data.toProduto
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ItensUiState(
    val loading: Boolean = true,
    val erro: String? = null,
    val iniciais: String = "",
    val categorias: List<String> = listOf("Todas"), // [Todas] + nomes das categorias
    val categoriaSelecionada: Int = 0,    // índice em [Todas] + categorias
    val filtrosStatus: List<String> = listOf("Todos", "Estoque baixo", "Vencendo", "OK"),
    val statusSelecionado: Int = 0,
    val termoBusca: String = "",
    val itensFiltrados: List<Produto> = emptyList(),
    val totalAlertas: Int = 0,
)

/**
 * ViewModel da ItensScreen.
 * Usa GET /products (com query params) e GET /categories.
 * Os filtros são enviados direto para a API quando possível;
 * o filtro de status é aplicado localmente (a API suporta ambos).
 */
class ItensViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ItensUiState())
    val uiState: StateFlow<ItensUiState> = _uiState.asStateFlow()
    private var todosOsItens: List<ApiProduct> = emptyList()
    private var apiCategorias: List<ApiCategory> = emptyList()

    init {
        viewModelScope.launch {
            val catsResult = safeApiCall { RetrofitClient.api.listCategories() }
            apiCategorias = (catsResult as? ApiResult.Success)?.data ?: emptyList()

            val userResult = safeApiCall { RetrofitClient.api.getMe() }
            val iniciais = (userResult as? ApiResult.Success)?.data?.initials ?: ""

            _uiState.update {
                it.copy(
                    categorias = listOf("Todas") + apiCategorias.map { c -> c.name },
                    iniciais = iniciais,
                )
            }
            carregarProdutos()
        }
    }

    fun onBuscaChange(termo: String) {
        _uiState.update { it.copy(termoBusca = termo) }
        aplicarFiltrosLocais()
    }

    fun onCategoriaSelecionada(index: Int) {
        _uiState.update { it.copy(categoriaSelecionada = index) }
        aplicarFiltrosLocais()
    }

    fun onStatusSelecionado(index: Int) {
        _uiState.update { it.copy(statusSelecionado = index) }
        aplicarFiltrosLocais()
    }

    fun recarregar() = carregarProdutos()

    private fun carregarProdutos() {
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.api.listProducts() }
            when (result) {
                is ApiResult.Success -> {
                    todosOsItens = result.data
                    aplicarFiltrosLocais()
                    _uiState.update { it.copy(loading = false) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(loading = false, erro = result.message) }
                }
            }
        }
    }

    private fun aplicarFiltrosLocais() {
        val state = _uiState.value
        var lista = todosOsItens

        // Filtro de busca
        if (state.termoBusca.isNotBlank()) {
            lista = lista.filter { it.name.contains(state.termoBusca, ignoreCase = true) }
        }

        // Filtro de categoria (índice 0 = "Todas")
        if (state.categoriaSelecionada != 0 && state.categoriaSelecionada - 1 in apiCategorias.indices) {
            val cat = apiCategorias[state.categoriaSelecionada - 1]
            lista = lista.filter { it.categoryId == cat.id }
        }

        // Filtro de status
        when (state.statusSelecionado) {
            1 -> lista = lista.filter { it.status.type == "low" }
            2 -> lista = lista.filter { it.status.type == "expiring" }
            3 -> lista = lista.filter { it.status.type == "ok" }
        }

        // Ordenação: problemas primeiro, depois alfabético
        val ordem = mapOf("expired" to 0, "low" to 1, "expiring" to 2, "ok" to 3)
        lista = lista.sortedWith(compareBy({ ordem[it.status.type] ?: 99 }, { it.name }))

        val totalAlertas = todosOsItens.count { it.status.type != "ok" }
        _uiState.update { it.copy(itensFiltrados = lista.map { p -> p.toProduto() }, totalAlertas = totalAlertas) }
    }
}
