package com.hmproductions.itsmclient

import com.hmproductions.itsmclient.data.LoginDetails
import com.hmproductions.itsmclient.data.LoginResponse
import com.hmproductions.itsmclient.data.SignUpDetails
import com.hmproductions.itsmclient.data.TicketResponse
import retrofit2.Call
import retrofit2.http.*

interface ITSMClient {

    @Headers("Content-Type:application/json")
    @POST("user/login")
    fun login(@Body loginDetails: LoginDetails): Call<LoginResponse>

    @Headers("Content-Type:application/json")
    @POST("user/signup")
    fun signUp(@Body signUpDetails: SignUpDetails): Call<LoginResponse>

    @GET("core/data")
    fun getTickets(@Header("Authorization") authorization: String): Call<TicketResponse>
}