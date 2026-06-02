package com.example.stockhome

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.stockhome.ui.screens.AlertasScreen
import com.example.stockhome.ui.screens.CadastroScreen
import com.example.stockhome.ui.screens.DetalheScreen
import com.example.stockhome.ui.screens.FormScreen
import com.example.stockhome.ui.screens.HomeScreen
import com.example.stockhome.ui.screens.ItensScreen
import com.example.stockhome.ui.screens.LoginScreen
import com.example.stockhome.ui.screens.PerfilScreen
import com.example.stockhome.ui.screens.SplashScreen
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.ui.theme.StockHomeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StockHomeTheme {
                AppRoot()
            }
        }
    }
}

/** Uma entrada da pilha de navegação: tela + parâmetro. */
private data class Entry(val screen: String, val param: Any?)

@Composable
private fun AppRoot() {
    // Pilha de navegação simples (equivalente ao `go` do protótipo React).
    val stack = remember { androidx.compose.runtime.mutableStateListOf(Entry("splash", null)) }
    val current = stack.last()

    val go: (String, Any?) -> Unit = { screen, param ->
        // Abas e fluxos de "voltar lógico" não empilham infinitamente:
        // troca a tela atual mantendo a pilha enxuta para telas principais.
        when (screen) {
            "splash", "login" -> {
                stack.clear(); stack.add(Entry(screen, param))
            }
            "home", "itens", "alertas" -> {
                // navegação entre abas: mantém home como base
                if (current.screen in listOf("home", "itens", "alertas")) {
                    stack[stack.lastIndex] = Entry(screen, param)
                } else {
                    stack.add(Entry(screen, param))
                }
            }
            else -> stack.add(Entry(screen, param))
        }
    }

    // Botão físico/gesto de voltar
    BackHandler(enabled = stack.size > 1) { stack.removeAt(stack.lastIndex) }

    androidx.compose.foundation.layout.Box(
        Modifier
            .fillMaxSize()
            .background(Sh.bg)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        when (current.screen) {
            "splash" -> SplashScreen(go)
            "login" -> LoginScreen(go)
            "cadastro" -> CadastroScreen(go)
            "home" -> HomeScreen(go)
            "itens" -> ItensScreen(go)
            "detalhe" -> DetalheScreen(go, (current.param as? Int) ?: 3)
            "form" -> FormScreen(go, current.param ?: "novo")
            "alertas" -> AlertasScreen(go)
            "perfil" -> PerfilScreen(go)
        }
    }
}
