package com.hmproductions.itsmclient.dagger

import com.hmproductions.itsmclient.ITSMClient
import com.hmproductions.itsmclient.dagger.ITSMApplicationScope
import com.hmproductions.itsmclient.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ClientModule {

    @Provides
    @ITSMApplicationScope
    fun getLiveRetrofit(): Retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
        .client(getOkHttpClient()).baseUrl(BASE_URL).build()

    @Provides
    @ITSMApplicationScope
    fun getRetrofitClient(retrofit: Retrofit): ITSMClient = retrofit.create(ITSMClient::class.java)

    private fun getOkHttpClient() = OkHttpClient.Builder().build()
}
