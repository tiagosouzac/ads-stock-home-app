package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class HomeUiState(
    val loading: Boolean = true,
    val erro: String? = null,
    val nomeUsuario: String = "",
    val iniciais: String = "",
    val totalItens: Int = 0,
    val itensEstoqueBaixo: Int = 0,
    val itensVencendo: Int = 0,
    val totalAlertas: Int = 0,
    val itensAtencao: List<Produto> = emptyList(),
)

/**
 * ViewModel da HomeScreen.
 * Chama GET /dashboard/summary e GET /me da API do Tiago.
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        carregarDados()
    }

    fun carregarDados() {
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            // Busca usuário e resumo em paralelo (simplificado: sequencial)
            val meResult = safeApiCall { RetrofitClient.api.getMe() }
            val resumoResult = safeApiCall { RetrofitClient.api.getSummary() }

            val user = (meResult as? ApiResult.Success)?.data
            val resumo = (resumoResult as? ApiResult.Success)?.data

            if (resumo == null) {
                val msg = (resumoResult as? ApiResult.Error)?.message ?: "Erro ao carregar dados."
                _uiState.update { it.copy(loading = false, erro = msg) }
                return@launch
            }

            val atencao = (resumo.expiring + resumo.low)
                .distinctBy { it.id }
                .take(3)
                .map { it.toProduto() }
            val totalAlertas = resumo.counters.low + resumo.counters.expiring + resumo.counters.expired

            _uiState.update {
                it.copy(
                    loading = false,
                    nomeUsuario = user?.name?.split(" ")?.first() ?: "",
                    iniciais = user?.initials ?: "",
                    totalItens = resumo.total,
                    itensEstoqueBaixo = resumo.counters.low,
                    itensVencendo = resumo.counters.expiring,
                    totalAlertas = totalAlertas,
                    itensAtencao = atencao,
                )
            }
        }
    }
}
