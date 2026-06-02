package com.example.stockhome

import android.app.Application
import com.example.stockhome.network.RetrofitClient

/**
 * Application class do StockHome.
 *
 * Inicializa o RetrofitClient com o Context da aplicação,
 * permitindo que o SharedPreferences (onde o JWT é guardado)
 * funcione antes de qualquer tela ser criada.
 *
 * Precisa ser declarada no AndroidManifest.xml com:
 *   android:name=".StockHomeApp"
 */
class StockHomeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
