package com.example.stockhome.network

import com.example.stockhome.data.AdjustQuantityRequest
import com.example.stockhome.data.AlertsResponse
import com.example.stockhome.data.ApiCategory
import com.example.stockhome.data.ApiProduct
import com.example.stockhome.data.ApiUser
import com.example.stockhome.data.AuthResponse
import com.example.stockhome.data.ChangePasswordRequest
import com.example.stockhome.data.CreateProductRequest
import com.example.stockhome.data.DashboardSummary
import com.example.stockhome.data.LoginRequest
import com.example.stockhome.data.OkResponse
import com.example.stockhome.data.RegisterRequest
import com.example.stockhome.data.ResetPasswordRequest
import com.example.stockhome.data.UpdateMeRequest
import com.example.stockhome.data.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato Retrofit da API REST criada pelo Tiago.
 * Cada método corresponde a uma rota em api/src/routes/index.ts.
 */
interface StockHomeApi {

    // ── Auth ──────────────────────────────────────────────────

    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): Response<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetPasswordRequest): Response<OkResponse>

    // ── Profile ───────────────────────────────────────────────

    @GET("me")
    suspend fun getMe(): Response<ApiUser>

    @PATCH("me")
    suspend fun updateMe(@Body body: UpdateMeRequest): Response<ApiUser>

    @PATCH("me/password")
    suspend fun changePassword(@Body body: ChangePasswordRequest): Response<OkResponse>

    // ── Categories ────────────────────────────────────────────

    @GET("categories")
    suspend fun listCategories(): Response<List<ApiCategory>>

    // ── Dashboard / Alerts ────────────────────────────────────

    @GET("dashboard/summary")
    suspend fun getSummary(): Response<DashboardSummary>

    @GET("alerts")
    suspend fun getAlerts(): Response<AlertsResponse>

    // ── Products ──────────────────────────────────────────────

    @GET("products")
    suspend fun listProducts(
        @Query("search") search: String? = null,
        @Query("category") category: String? = null,
        @Query("status") status: String? = null,
        @Query("sort") sort: String? = null,
    ): Response<List<ApiProduct>>

    @POST("products")
    suspend fun createProduct(@Body body: CreateProductRequest): Response<ApiProduct>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<ApiProduct>

    @PATCH("products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body body: UpdateProductRequest,
    ): Response<ApiProduct>

    @PATCH("products/{id}/quantity")
    suspend fun adjustQuantity(
        @Path("id") id: Int,
        @Body body: AdjustQuantityRequest,
    ): Response<ApiProduct>

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Unit>
}
