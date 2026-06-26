package com.example.data

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

interface SmmApiService {

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun getBalance(
        @Field("key") key: String,
        @Field("action") action: String = "balance"
    ): Response<SmmBalanceResponse>

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun getServices(
        @Field("key") key: String,
        @Field("action") action: String = "services"
    ): Response<List<SmmServiceResponse>>

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun addOrder(
        @Field("key") key: String,
        @Field("action") action: String = "add",
        @Field("service") service: String,
        @Field("link") link: String,
        @Field("quantity") quantity: String
    ): Response<SmmOrderResponse>

    @FormUrlEncoded
    @POST("api/v2")
    suspend fun getOrderStatus(
        @Field("key") key: String,
        @Field("action") action: String = "status",
        @Field("order") orderId: String
    ): Response<SmmOrderStatusResponse>
}

// Response models
data class SmmBalanceResponse(
    val balance: String? = null,
    val currency: String? = null,
    val error: String? = null
)

data class SmmServiceResponse(
    val service: String,
    val name: String,
    val category: String,
    val rate: String, // Base rate per 1000 from SMM Provider
    val min: String? = null,
    val max: String? = null,
    val type: String? = null
)

data class SmmOrderResponse(
    val order: String? = null,
    val error: String? = null
)

data class SmmOrderStatusResponse(
    val status: String? = null,
    val charge: String? = null,
    val start_count: String? = null,
    val remains: String? = null,
    val currency: String? = null,
    val error: String? = null
)

object RetrofitClient {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val instance: SmmApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Config.API_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(SmmApiService::class.java)
    }
}
