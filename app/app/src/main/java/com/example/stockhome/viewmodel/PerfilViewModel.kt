package com.example.stockhome.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockhome.data.UpdateMeRequest
import com.example.stockhome.network.ApiResult
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.network.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val loading: Boolean = true,
    val salvando: Boolean = false,
    val salvo: Boolean = false,
    val erro: String? = null,
    val erroNome: String? = null,
    val nome: String = "",
    val email: String = "",
    val iniciais: String = "",
    val diasAlerta: Int = 7,
    val opcoesAlerta: List<Int> = listOf(3, 7, 15),
)

/**
 * ViewModel da PerfilScreen.
 * Chama GET /me e PATCH /me da API do Tiago.
 */
class PerfilViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        carregarPerfil()
    }

    fun carregarPerfil() {
        _uiState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.api.getMe() }
            when (result) {
                is ApiResult.Success -> {
                    val u = result.data
                    _uiState.update {
                        it.copy(
                            loading = false,
                            nome = u.name,
                            email = u.email,
                            iniciais = u.initials,
                            diasAlerta = u.alertDays,
                        )
                    }
                }
                is ApiResult.Error ->
                    _uiState.update { it.copy(loading = false, erro = result.message) }
            }
        }
    }

    /** Atualiza o nome localmente enquanto o usuário digita na tela de edição. */
    fun onNomeChange(nome: String) {
        _uiState.update { it.copy(nome = nome, erroNome = null) }
    }

    /** Salva o nome na API (PATCH /me) e sinaliza [PerfilUiState.salvo] no sucesso. */
    fun salvarNome() {
        val nome = _uiState.value.nome.trim()
        if (nome.length < 2) {
            _uiState.update { it.copy(erroNome = "Informe o nome completo.") }
            return
        }
        _uiState.update { it.copy(salvando = true, erroNome = null) }
        viewModelScope.launch {
            val result = safeApiCall { RetrofitClient.api.updateMe(UpdateMeRequest(name = nome)) }
            when (result) {
                is ApiResult.Success -> {
                    val u = result.data
                    _uiState.update {
                        it.copy(salvando = false, salvo = true, nome = u.name, iniciais = u.initials)
                    }
                }
                is ApiResult.Error ->
                    _uiState.update { it.copy(salvando = false, erroNome = result.message) }
            }
        }
    }

    /** Reseta o sinal de salvo após a navegação consumi-lo. */
    fun limparSalvo() = _uiState.update { it.copy(salvo = false) }

    fun onDiasAlertaChange(dias: Int) {
        _uiState.update { it.copy(diasAlerta = dias) }
        // Salva automaticamente na API ao mudar
        viewModelScope.launch {
            safeApiCall { RetrofitClient.api.updateMe(UpdateMeRequest(alertDays = dias)) }
        }
    }

    fun sair() {
        RetrofitClient.clearToken()
    }
}
