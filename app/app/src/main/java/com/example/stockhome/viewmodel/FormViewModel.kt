package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.ApiCategory
import com.example.stockhome.data.CreateProductRequest
import com.example.stockhome.data.UpdateProductRequest
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormUiState(
    val loading: Boolean = false,
    val erro: String? = null,
    val erroNome: String? = null,
    val salvo: Boolean = false,
    val editando: Boolean = false,
    val nomeProduto: String = "",
    val categoriaSelecionada: ApiCategory? = null,
    val categorias: List<ApiCategory> = emptyList(),
    val qtdAtual: Int = 1,
    val qtdMinima: Int = 1,
    val unidade: String = "un",
    val validade: String = "",   // formato "YYYY-MM-DD" internamente
) {
    /** Nome da categoria selecionada, para exibição no campo. */
    val nomeCategoria: String get() = categoriaSelecionada?.name ?: "Selecione…"
}

/**
 * ViewModel da FormScreen (adicionar e editar produto).
 * Chama POST /products ou PATCH /products/{id}.
 */
class FormViewModel(private val id: Any?) : ViewModel() {

    private val _uiState = MutableStateFlow(FormUiState())
    val uiState: StateFlow<FormUiState> = _uiState.asStateFlow()
    private val produtoId: Int? = if (id is Int) id else null

    init {
        viewModelScope.launch {
            val catsResult = safeApiCall { RetrofitClient.api.listCategories() }
            val cats = (catsResult as? ApiResult.Success)?.data ?: emptyList()

            if (produtoId != null) {
                // Modo edição: carrega dados do produto
                val result = safeApiCall { RetrofitClient.api.getProduct(produtoId) }
                val p = (result as? ApiResult.Success)?.data
                val cat = cats.find { it.id == p?.categoryId }
                _uiState.update {
                    it.copy(
                        editando = true,
                        categorias = cats,
                        nomeProduto = p?.name ?: "",
                        categoriaSelecionada = cat,
                        qtdAtual = p?.quantity ?: 1,
                        qtdMinima = p?.minQuantity ?: 1,
                        unidade = p?.unit ?: "un",
                        validade = p?.expiresAt ?: "",
                    )
                }
            } else {
                _uiState.update { it.copy(categorias = cats, categoriaSelecionada = cats.firstOrNull()) }
            }
        }
    }

    fun onNomeChange(nome: String) = _uiState.update { it.copy(nomeProduto = nome, erroNome = null) }
    fun onCategoriaChange(cat: ApiCategory) = _uiState.update { it.copy(categoriaSelecionada = cat) }
    fun onValidadeChange(v: String) = _uiState.update { it.copy(validade = v) }
    fun onUnidadeChange(u: String) = _uiState.update { it.copy(unidade = u) }

    fun incrementarQtdAtual() = _uiState.update { it.copy(qtdAtual = it.qtdAtual + 1) }
    fun decrementarQtdAtual() = _uiState.update { it.copy(qtdAtual = maxOf(0, it.qtdAtual - 1)) }
    fun incrementarQtdMinima() = _uiState.update { it.copy(qtdMinima = it.qtdMinima + 1) }
    fun decrementarQtdMinima() = _uiState.update { it.copy(qtdMinima = maxOf(0, it.qtdMinima - 1)) }

    fun salvar() {
        val state = _uiState.value
        if (state.nomeProduto.isBlank()) {
            _uiState.update { it.copy(erroNome = "Informe o nome do produto.", erro = null) }
            return
        }
        val catId = state.categoriaSelecionada?.id
        if (catId == null) {
            _uiState.update { it.copy(erro = "Selecione uma categoria.") }
            return
        }
        val validadeApi = state.validade.takeIf { it.isNotBlank() }

        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = if (produtoId != null) {
                safeApiCall {
                    RetrofitClient.api.updateProduct(
                        produtoId,
                        UpdateProductRequest(
                            name = state.nomeProduto.trim(),
                            categoryId = catId,
                            quantity = state.qtdAtual,
                            minQuantity = state.qtdMinima,
                            unit = state.unidade,
                            expiresAt = validadeApi,
                        )
                    )
                }
            } else {
                safeApiCall {
                    RetrofitClient.api.createProduct(
                        CreateProductRequest(
                            name = state.nomeProduto.trim(),
                            categoryId = catId,
                            quantity = state.qtdAtual,
                            minQuantity = state.qtdMinima,
                            unit = state.unidade,
                            expiresAt = validadeApi,
                        )
                    )
                }
            }
            when (result) {
                is ApiResult.Success -> _uiState.update { it.copy(loading = false, salvo = true) }
                is ApiResult.Error -> _uiState.update { it.copy(loading = false, erro = result.message) }
            }
        }
    }

    class Factory(private val id: Any?) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = FormViewModel(id) as T
    }
}
