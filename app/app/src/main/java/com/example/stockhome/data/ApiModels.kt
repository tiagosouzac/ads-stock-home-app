package com.example.stockhome.data

/**
 * Modelos de dados que espelham as respostas JSON da API REST.
 * Cada data class corresponde a um objeto retornado pelos endpoints do Tiago.
 */

// ── Auth ──────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String,
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val alertDays: Int? = null,
)

data class AuthResponse(
    val token: String,
    val user: ApiUser,
)

// ── Usuário ───────────────────────────────────────────────────

data class ApiUser(
    val id: String,
    val name: String,
    val email: String,
    val initials: String,
    val alertDays: Int,
)

data class UpdateMeRequest(
    val name: String? = null,
    val alertDays: Int? = null,
)

// ── Categorias ────────────────────────────────────────────────

data class ApiCategory(
    val id: String,
    val name: String,
)

// ── Produtos ──────────────────────────────────────────────────

data class ApiProduct(
    val id: Int,
    val name: String,
    val categoryId: String,
    val category: ApiCategory?,
    val quantity: Int,
    val minQuantity: Int,
    val unit: String,
    val expiresAt: String?,        // "YYYY-MM-DD" ou null
    val lastUpdated: String,       // "YYYY-MM-DD"
    val daysUntilExpiry: Int?,
    val status: ApiStatus,
)

data class ApiStatus(
    val type: String,              // "ok" | "low" | "expiring" | "expired"
    val label: String,
)

data class CreateProductRequest(
    val name: String,
    val categoryId: String,
    val quantity: Int,
    val minQuantity: Int,
    val unit: String,
    val expiresAt: String? = null, // "YYYY-MM-DD"
)

data class UpdateProductRequest(
    val name: String? = null,
    val categoryId: String? = null,
    val quantity: Int? = null,
    val minQuantity: Int? = null,
    val unit: String? = null,
    val expiresAt: String? = null,
)

data class AdjustQuantityRequest(
    val delta: Int? = null,
    val quantity: Int? = null,
)

// ── Dashboard ─────────────────────────────────────────────────

data class DashboardSummary(
    val total: Int,
    val counters: DashboardCounters,
    val low: List<ApiProduct>,
    val expiring: List<ApiProduct>,
)

data class DashboardCounters(
    val low: Int,
    val expiring: Int,
    val expired: Int,
)

// ── Alertas ───────────────────────────────────────────────────

data class AlertsResponse(
    val expired: List<ApiProduct>,
    val low: List<ApiProduct>,
    val expiring: List<ApiProduct>,
    val total: Int,
)
