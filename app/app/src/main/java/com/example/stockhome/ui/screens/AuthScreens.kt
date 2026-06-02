package com.example.stockhome.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stockhome.network.RetrofitClient
import com.example.stockhome.ui.components.Button
import com.example.stockhome.ui.components.Field
import com.example.stockhome.ui.components.Logo
import com.example.stockhome.ui.components.T
import com.example.stockhome.ui.components.TopBar
import com.example.stockhome.ui.components.Wordmark
import com.example.stockhome.ui.icons.Icon
import com.example.stockhome.ui.theme.Manrope
import com.example.stockhome.ui.theme.Sh
import com.example.stockhome.viewmodel.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(go: (String, Any?) -> Unit) {
    // Verifica a sessão salva: com token válido vai direto para a Home.
    LaunchedEffect(Unit) {
        delay(700)
        go(if (RetrofitClient.isLoggedIn()) "home" else "login", null)
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Sh.bg)
            .clickable { go("login", null) },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Logo(88)
            Spacer(Modifier.height(22.dp))
            Wordmark(32f)
            Spacer(Modifier.height(8.dp))
            T("Sua despensa sob controle", 14.5f, FontWeight.SemiBold, Sh.ink3)
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp),
        ) {
            Spinner()
            T("Verificando sessão…", 12f, FontWeight.SemiBold, Sh.ink3)
        }
    }
}

@Composable
private fun Spinner() {
    val transition = rememberInfiniteTransition(label = "spin")
    val angle by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing), RepeatMode.Restart),
        label = "angle",
    )
    androidx.compose.foundation.Canvas(Modifier.size(30.dp).rotate(angle)) {
        val w = 3.dp.toPx()
        drawCircle(color = Sh.brandTint, radius = (size.minDimension - w) / 2, style = Stroke(w))
        drawArc(
            color = Sh.brand, startAngle = -90f, sweepAngle = 90f, useCenter = false,
            style = Stroke(w, cap = StrokeCap.Round),
            topLeft = androidx.compose.ui.geometry.Offset(w / 2, w / 2),
            size = androidx.compose.ui.geometry.Size(size.width - w, size.height - w),
        )
    }
}

@Composable
fun LoginScreen(
    go: (String, Any?) -> Unit,
    vm: AuthViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var verSenha by remember { mutableStateOf(false) }

    LaunchedEffect(state.sucesso) {
        if (state.sucesso) {
            vm.limparSucesso()
            go("home", null)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Sh.bg)
            .verticalScroll(rememberScrollState()),
    ) {
        // Cabeçalho de marca com um leve degradê verde.
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Sh.brandTint, Sh.bg)))
                .padding(top = 52.dp, bottom = 30.dp),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Logo(72)
                Spacer(Modifier.height(16.dp))
                Wordmark(28f)
                Spacer(Modifier.height(6.dp))
                T("Sua despensa sob controle", 13.5f, FontWeight.SemiBold, Sh.ink3)
            }
        }

        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 6.dp, bottom = 28.dp)) {
            T("Seja Bem-Vindo!", 27f, FontWeight.ExtraBold, Sh.ink, letterSpacing = -0.5f)
            Spacer(Modifier.height(6.dp))
            T("Entre para acompanhar seu estoque.", 15f, FontWeight.SemiBold, Sh.ink3)
            Spacer(Modifier.height(22.dp))

            // Formulário dentro de um cartão, destacando-o do fundo.
            com.example.stockhome.ui.components.Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(start = 20.dp, end = 20.dp, top = 22.dp, bottom = 22.dp)) {
                    Field(
                        label = "E-mail",
                        value = email,
                        onValueChange = { email = it; vm.limparErro() },
                        placeholder = "voce@email.com",
                        icon = "mail",
                        keyboardType = KeyboardType.Email,
                    )
                    Field(
                        label = "Senha",
                        value = senha,
                        onValueChange = { senha = it; vm.limparErro() },
                        placeholder = "Sua senha",
                        icon = "lock",
                        isPassword = !verSenha,
                        keyboardType = KeyboardType.Password,
                        trailing = {
                            Box(Modifier.clickable { verSenha = !verSenha }) {
                                Icon("eye", 20.dp, color = if (verSenha) Sh.brand else Sh.ink3)
                            }
                        },
                    )
                    if (state.erro != null) {
                        T(state.erro!!, 13f, FontWeight.SemiBold, Sh.danger.fg)
                        Spacer(Modifier.height(10.dp))
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Box(Modifier.clickable { }) {
                            T("Esqueci minha senha", 13.5f, FontWeight.Bold, Sh.brandDark)
                        }
                    }
                    Spacer(Modifier.height(18.dp))
                    Button(
                        if (state.loading) "Entrando…" else "Entrar",
                        { vm.login(email, senha) },
                        icon = "logout",
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                T("Não tem conta? ", 14f, FontWeight.SemiBold, Sh.ink2)
                Box(Modifier.clickable { go("cadastro", null) }) {
                    T("Cadastre-se", 14f, FontWeight.ExtraBold, Sh.brandDark)
                }
            }
        }
    }
}

@Composable
fun CadastroScreen(
    go: (String, Any?) -> Unit,
    vm: AuthViewModel = viewModel(),
) {
    val state by vm.uiState.collectAsState()
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var verSenha by remember { mutableStateOf(false) }

    LaunchedEffect(state.sucesso) {
        if (state.sucesso) {
            vm.limparSucesso()
            go("home", null)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Sh.bg)
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(title = "Criar conta", onBack = { go("login", null) })
        Column(Modifier.padding(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 24.dp)) {
            T("Leva menos de um minuto para começar a organizar sua casa.", 15f, FontWeight.SemiBold, Sh.ink3)
            Spacer(Modifier.height(26.dp))
            Field(
                label = "Nome completo",
                value = nome,
                onValueChange = { nome = it; vm.limparErro() },
                placeholder = "Seu nome",
                icon = "user",
            )
            Field(
                label = "E-mail",
                value = email,
                onValueChange = { email = it; vm.limparErro() },
                placeholder = "voce@email.com",
                icon = "mail",
                keyboardType = KeyboardType.Email,
            )
            Field(
                label = "Senha",
                value = senha,
                onValueChange = { senha = it; vm.limparErro() },
                placeholder = "Ao menos 6 caracteres",
                icon = "lock",
                isPassword = !verSenha,
                keyboardType = KeyboardType.Password,
                error = state.erro,
                trailing = {
                    Box(Modifier.clickable { verSenha = !verSenha }) {
                        Icon("eye", 20.dp, color = if (verSenha) Sh.brand else Sh.ink3)
                    }
                },
            )
            Spacer(Modifier.height(24.dp))
            Button(
                if (state.loading) "Criando…" else "Criar conta",
                { vm.cadastrar(nome, email, senha) },
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Ao continuar você concorda com os Termos\nde Uso e a Política de Privacidade.",
                color = Sh.ink3, fontFamily = Manrope, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center, lineHeight = 18.sp, modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
