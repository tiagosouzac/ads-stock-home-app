package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.LoginRequest
import com.example.stockhome.data.RegisterRequest
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val loading: Boolean = false,
    val erro: String? = null,
    val sucesso: Boolean = false,
)

/**
 * ViewModel das telas de Login e Cadastro.
 * Chama a API do Tiago (/auth/login e /auth/register) e salva o JWT.
 */
class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(erro = "Preencha e-mail e senha.") }
            return
        }
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = safeApiCall {
                RetrofitClient.api.login(LoginRequest(email.trim(), password))
            }
            when (result) {
                is ApiResult.Success -> {
                    RetrofitClient.saveToken(result.data.token)
                    _uiState.update { it.copy(loading = false, sucesso = true) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(loading = false, erro = result.message) }
                }
            }
        }
    }

    fun cadastrar(nome: String, email: String, senha: String) {
        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
            _uiState.update { it.copy(erro = "Preencha todos os campos.") }
            return
        }
        if (senha.length < 6) {
            _uiState.update { it.copy(erro = "Use ao menos 6 caracteres na senha.") }
            return
        }
        _uiState.update { it.copy(loading = true, erro = null) }
        viewModelScope.launch {
            val result = safeApiCall {
                RetrofitClient.api.register(RegisterRequest(nome.trim(), email.trim(), senha))
            }
            when (result) {
                is ApiResult.Success -> {
                    RetrofitClient.saveToken(result.data.token)
                    _uiState.update { it.copy(loading = false, sucesso = true) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(loading = false, erro = result.message) }
                }
            }
        }
    }

    fun limparErro() = _uiState.update { it.copy(erro = null) }

    /**
     * Reseta o sinal de sucesso depois que a navegação já o consumiu.
     * Necessário porque o ViewModel tem escopo de Activity e é reutilizado
     * entre login/cadastro (sem isso, voltar ao login redirecionaria sozinho).
     */
    fun limparSucesso() = _uiState.update { it.copy(sucesso = false) }
}
