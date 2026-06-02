package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.ResetPasswordRequest
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EsqueciSenhaUiState(
    val loading: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false,
)

/**
 * ViewModel da tela "Esqueci minha senha" (fluxo simplificado, sem e-mail).
 * Chama POST /auth/reset-password informando e-mail e a nova senha.
 */
class EsqueciSenhaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EsqueciSenhaUiState())
    val uiState: StateFlow<EsqueciSenhaUiState> = _uiState.asStateFlow()

    fun redefinir(email: String, novaSenha: String, confirmacao: String) {
        when {
            email.isBlank() || novaSenha.isBlank() ->
                return _uiState.update { it.copy(erro = "Preencha todos os campos.") }
            novaSenha.length < 6 ->
                return _uiState.update { it.copy(erro = "Use ao menos 6 caracteres na nova senha.") }
            novaSenha != confirmacao ->
                return _uiState.update { it.copy(erro = "As senhas não coincidem.") }
        }
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = safeApiCall {
                RetrofitClient.api.resetPassword(ResetPasswordRequest(email.trim(), novaSenha))
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
