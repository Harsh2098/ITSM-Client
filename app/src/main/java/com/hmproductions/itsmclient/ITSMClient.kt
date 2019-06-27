package com.hmproductions.itsmclient

import com.hmproductions.itsmclient.data.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ITSMClient {

    @Headers("Content-Type:application/json")
    @POST("user/login")
    fun login(@Body loginDetails: LoginDetails): Call<GenericResponse>

    @Headers("Content-Type:application/json")
    @POST("user/signup")
    fun signUp(@Body genericAuthenticationDetails: GenericAuthenticationDetails): Call<GenericResponse>

    @GET("config")
    fun getConfigurations(@Header("Authorization") authorization: String): Call<ConfigurationResponse>

    @GET("config/available")
    fun getAvailableFields(@Header("Authorization") authorization: String): Call<FieldResponse>

    @POST("config")
    fun setConfiguration(@Header("Authorization") authorization: String, @Body configurationRequest: ConfigurationRequest): Call<GenericResponse>

    @GET("core/data")
    fun getCoreData(@Header("Authorization") authorization: String): Call<ResponseBody>

    @POST("user/reset_password")
    fun forgotPassword(@Body genericAuthenticationDetails: GenericAuthenticationDetails): Call<GenericResponse>

    @POST("user/change_password")
    fun changePassword(@Header("Authorization") authorization: String, @Body changePasswordDetails: ChangePasswordDetails): Call<GenericResponse>

    @POST("alter")
    fun alterConfiguration(@Header("Authorization") authorization: String, @Body configurationRequest: ConfigurationRequest): Call<GenericResponse>

}