package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.ChangePasswordRequest
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AlterarSenhaUiState(
    val loading: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false,
)

/**
 * ViewModel da tela "Alterar senha" (usuário logado).
 * Chama PATCH /me/password da API, validando a senha atual no servidor.
 */
class AlterarSenhaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AlterarSenhaUiState())
    val uiState: StateFlow<AlterarSenhaUiState> = _uiState.asStateFlow()

    fun alterar(senhaAtual: String, novaSenha: String, confirmacao: String) {
        when {
            senhaAtual.isBlank() || novaSenha.isBlank() ->
                return _uiState.update { it.copy(erro = "Preencha todos os campos.") }
            novaSenha.length < 6 ->
                return _uiState.update { it.copy(erro = "Use ao menos 6 caracteres na nova senha.") }
            novaSenha != confirmacao ->
                return _uiState.update { it.copy(erro = "As senhas não coincidem.") }
        }
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = safeApiCall {
                RetrofitClient.api.changePassword(ChangePasswordRequest(senhaAtual, novaSenha))
            }
            when (result) {
                is ApiResult.Success ->
                    _uiState.update { it.copy(loading = false, sucesso = true) }
                is ApiResult.Error ->
                    _uiState.update { it.copy(loading = false, erro = result.message) }
            }
        }
    }

    fun limparErro() = _uiState.update { it.copy(erro = null) }

    /** Reseta o sinal de sucesso após a navegação consumi-lo (VM é reutilizado). */
    fun limparSucesso() = _uiState.update { it.copy(sucesso = false) }
}
