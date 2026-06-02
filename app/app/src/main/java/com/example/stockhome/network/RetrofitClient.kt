package com.example.stockhome.network

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton que constrói e fornece a instância do Retrofit.
 *
 * O token JWT é salvo em SharedPreferences e automaticamente
 * adicionado no header "Authorization: Bearer <token>" de cada requisição.
 *
 * Como inicializar (chamar 1x na Application ou na MainActivity):
 *   RetrofitClient.init(context)
 *
 * Depois usar:
 *   RetrofitClient.api.login(...)
 *   RetrofitClient.saveToken(token)
 *   RetrofitClient.clearToken()
 */
object RetrofitClient {

    /**
     * URL base da API. Ajuste conforme o ambiente:
     *   - Emulador Android acessando localhost do PC → "http://10.0.2.2:3000/"
     *   - Celular físico na mesma rede → "http://<IP-DO-PC>:3000/"
     *   - Produção → URL real do servidor
     */
    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private const val PREFS_NAME = "stockhome_prefs"
    private const val KEY_TOKEN = "jwt_token"

    private lateinit var prefs: SharedPreferences

    val api: StockHomeApi by lazy { buildApi() }

    fun init(context: Context) {
        prefs = context.applicationContext
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) =
        prefs.edit().putString(KEY_TOKEN, token).apply()

    fun clearToken() =
        prefs.edit().remove(KEY_TOKEN).apply()

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isLoggedIn(): Boolean = getToken() != null

    private fun buildApi(): StockHomeApi {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val original = chain.request()
                val token = getToken()
                val request = if (token != null) {
                    original.newBuilder()
                        .header("Authorization", "Bearer $token")
                        .build()
                } else original
                chain.proceed(request)
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(StockHomeApi::class.java)
    }
}
