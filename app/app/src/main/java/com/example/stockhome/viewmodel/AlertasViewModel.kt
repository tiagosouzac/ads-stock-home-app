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

data class AlertasUiState(
    val loading: Boolean = true,
    val erro: String? = null,
    val iniciais: String = "",
    val vencendo: List<Produto> = emptyList(),
    val baixos: List<Produto> = emptyList(),
    val totalAlertas: Int = 0,
)

/**
 * ViewModel da AlertasScreen.
 * Chama GET /alerts da API do Tiago.
 */
class AlertasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlertasUiState())
    val uiState: StateFlow<AlertasUiState> = _uiState.asStateFlow()

    init {
        carregarAlertas()
    }

    fun carregarAlertas() {
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val userResult = safeApiCall { RetrofitClient.api.getMe() }
            val iniciais = (userResult as? ApiResult.Success)?.data?.initials ?: ""

            val result = safeApiCall { RetrofitClient.api.getAlerts() }
            when (result) {
                is ApiResult.Success -> {
                    val data = result.data
                    val vencendo = (data.expiring + data.expired)
                        .sortedBy { it.daysUntilExpiry ?: Int.MAX_VALUE }
                        .map { it.toProduto() }
                    _uiState.update {
                        it.copy(
                            loading = false,
                            iniciais = iniciais,
                            vencendo = vencendo,
                            baixos = data.low.map { p -> p.toProduto() },
                            totalAlertas = data.total,
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(loading = false, erro = result.message) }
                }
            }
        }
    }
}
