package com.example.stockhome.network

import retrofit2.Response

/**
 * Resultado de uma chamada à API: sucesso com dado T ou falha com mensagem de erro.
 * Usado em todos os ViewModels para tornar o tratamento de erros uniforme.
 */
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val message: String) : ApiResult<Nothing>()
}

/**
 * Extensão que converte uma Response do Retrofit em ApiResult,
 * capturando erros de rede, HTTP e de parsing de forma centralizada.
 */
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): ApiResult<T> {
    return try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                ApiResult.Success(body)
            } else {
                ApiResult.Error("Resposta vazia do servidor.")
            }
        } else {
            // Sessão expirada/ inválida: descarta o token para forçar novo login.
            if (response.code() == 401) RetrofitClient.clearToken()

            val errorMsg = response.errorBody()?.string()
            val msg = errorMsg
                ?.substringAfter("\"error\":\"", "")
                ?.substringBefore("\"")
                ?.takeIf { it.isNotBlank() }
                ?: "Erro ${response.code()}"
            ApiResult.Error(msg)
        }
    } catch (e: Exception) {
        ApiResult.Error(e.message ?: "Erro de conexão com o servidor.")
    }
}
