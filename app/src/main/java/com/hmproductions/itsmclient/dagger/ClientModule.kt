package com.hmproductions.itsmclient.dagger

import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ClientModule {

    @Provides
    @ITSMApplicationScope
    fun getLiveRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient).baseUrl(BASE_URL).build()

    @Provides
    @ITSMApplicationScope
    fun getRetrofitClient(retrofit: Retrofit): ITSMClient = retrofit.create(ITSMClient::class.java)

    @Provides
    @ITSMApplicationScope
    fun getOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Provides
    @ITSMApplicationScope
    fun getInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return interceptor
    }
}
