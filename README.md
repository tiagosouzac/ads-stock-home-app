# StockHome 📦

Aplicativo Android para controle de estoque doméstico — trabalho da disciplina de Desenvolvimento de Aplicativos Android.

## 👥 Integrantes

| Nome | GitHub | Responsabilidade |
|------|--------|-----------------|
| Pedro Lopes | @PedroHLSs | Telas Jetpack Compose, componentes UI, tema e navegação |
| Tiago Souza de Castro | @tiagosouzac | API REST (Node.js + TypeScript + Prisma + PostgreSQL) |
| [Seu Nome] | @[seu-github] | Arquitetura MVVM, integração app ↔ API, persistência (JWT) |

## 🎨 Protótipo no Figma

https://www.figma.com/design/V6X8tUznvfmXAsEPREYu1o/Untitled?node-id=0-1&t=5Eyu5Zlg01YHEZHI-1

## 🎥 Vídeo demonstrativo

link youtube

---

## 📱 Sobre o app

StockHome é um aplicativo de controle de estoque para uso doméstico. Permite cadastrar produtos, acompanhar validade e quantidade mínima, e receber alertas quando algo precisa de atenção.

### Funcionalidades
- Login e cadastro de conta
- Dashboard com resumo do estoque
- Lista de itens com filtros por categoria e status
- Tela de detalhes de cada produto
- Formulário para adicionar e editar produtos
- Tela de alertas (estoque baixo e vencimento próximo)
- Perfil do usuário com configuração de alerta de validade

### Arquitetura

O projeto segue o padrão **MVVM (Model-View-ViewModel)**:

```
app/
├── data/           # Modelos de dados (ApiModels.kt)
├── network/        # Retrofit, StockHomeApi, ApiResult
├── viewmodel/      # ViewModels com StateFlow
└── ui/
    ├── screens/    # Telas Composable (consomem o ViewModel)
    ├── components/ # Componentes reutilizáveis
    ├── icons/      # Ícones vetoriais
    └── theme/      # Cores, tipografia, tema
```

Cada tela observa um `UiState` via `collectAsState()` — toda lógica de negócio fica no ViewModel, as telas são puramente declarativas.

---

## ⚙️ Como rodar

### API (backend)

Pré-requisitos: [Docker](https://docker.com) e [Node.js 20+](https://nodejs.org)

```bash
cd api
cp .env.example .env        # configure as variáveis se necessário
docker compose up -d        # sobe o PostgreSQL
npm install
npm run db:migrate          # roda as migrations
npm run db:seed             # popula com dados de exemplo
npm run dev                 # API rodando em http://localhost:3000
```

### App Android

1. Abra a pasta `app/` no **Android Studio**
2. Em `RetrofitClient.kt`, confirme que `BASE_URL = "http://10.0.2.2:3000/"` (emulador) ou ajuste para o IP da sua máquina (celular físico)
3. Clique em **Run** com um emulador ou celular conectado

Credenciais de teste (seed):
- **E-mail:** `marina@stockhome.app`
- **Senha:** `senha123`

---

## 🛠️ Tecnologias

### App Android
- Kotlin + Jetpack Compose
- Arquitetura MVVM com `ViewModel` e `StateFlow`
- Retrofit 2 + OkHttp (comunicação com a API)
- Gson (parsing JSON)
- SharedPreferences (persistência do token JWT)
- Material Design 3

### API REST
- Node.js + TypeScript + Express
- Prisma ORM + PostgreSQL (Docker)
- JWT para autenticação
- Zod para validação de schemas
