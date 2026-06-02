package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.CATEGORIAS
import com.example.stockhome.data.Produto
import com.example.stockhome.data.Status
import com.example.stockhome.data.fmtData
import com.example.stockhome.data.fmtDataLonga
import com.example.stockhome.data.parseIsoDate
import com.example.stockhome.data.statusTipoFromApi
import com.example.stockhome.data.toProduto
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
    val produto: Produto? = null,
    val status: Status? = null,
    val estoqueBaixo: Boolean = false,
    val nomeCategoria: String = "",
    val validadeFmt: String = "—",
    val atualizadoFmt: String = "—",
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
                is ApiResult.Success -> {
                    val api = result.data
                    val nomeCat = api.category?.name
                        ?: CATEGORIAS[api.categoryId]?.nome
                        ?: api.categoryId
                    _uiState.update {
                        it.copy(
                            loading = false,
                            produto = api.toProduto(),
                            status = Status(statusTipoFromApi(api.status.type), api.status.label),
                            estoqueBaixo = api.status.type == "low",
                            nomeCategoria = nomeCat,
                            validadeFmt = fmtData(parseIsoDate(api.expiresAt)),
                            atualizadoFmt = fmtDataLonga(parseIsoDate(api.lastUpdated)),
                        )
                    }
                }
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
