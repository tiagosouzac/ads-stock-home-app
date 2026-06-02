package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.ApiProduct
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalheUiState(
    val loading: Boolean = true,
    val erro: String? = null,
    val produto: ApiProduct? = null,
    val deletando: Boolean = false,
    val deletado: Boolean = false,
)

/**
 * ViewModel da DetalheScreen.
 * Chama GET /products/{id} e DELETE /products/{id}.
 */
class DetalheViewModel(private val id: Int) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalheUiState())
    val uiState: StateFlow<DetalheUiState> = _uiState.asStateFlow()

    init {
        carregarProduto()
    }

    fun carregarProduto() {
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.api.getProduct(id) }
            when (result) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(loading = false, produto = result.data) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(loading = false, erro = result.message) }
            }
        }
    }

    fun excluir() {
        _uiState.update { it.copy(deletando = true) }
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.api.deleteProduct(id) }
            when (result) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(deletando = false, deletado = true) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(deletando = false, erro = result.message) }
            }
        }
    }

    class Factory(private val id: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalheViewModel(id) as T
    }
}
