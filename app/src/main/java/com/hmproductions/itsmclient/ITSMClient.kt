package com.hmproductions.itsmclient

import com.hmproductions.itsmclient.data.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ITSMClient {

    @POST("user/login")
    fun login(@Body accountDetails: AccountDetails): Call<GenericResponse>

    @POST("user/signup")
    fun signUp(@Body genericAuthenticationDetails: GenericAuthenticationDetails): Call<GenericResponse>

    @POST("user/reset_password")
    fun forgotPassword(@Body genericAuthenticationDetails: GenericAuthenticationDetails): Call<GenericResponse>

    @POST("user/change_password")
    fun changePassword(@Header("Authorization") authorization: String, @Body changePasswordDetails: ChangePasswordDetails): Call<GenericResponse>

    @HTTP(method = "DELETE", path = "user/delete", hasBody = true)
    fun deleteAccount(@Header ("Authorization") authorization: String, @Body accountDetails: AccountDetails): Call<GenericResponse>

    @GET("config")
    fun getConfigurations(@Header("Authorization") authorization: String): Call<ConfigurationResponse>

    @GET("config/available")
    fun getAvailableFields(@Header("Authorization") authorization: String): Call<FieldResponse>

    @POST("config")
    fun setConfiguration(@Header("Authorization") authorization: String, @Body configurationRequest: ConfigurationRequest): Call<GenericResponse>

    @GET("core/data")
    fun getCoreData(@Header("Authorization") authorization: String): Call<ResponseBody>

    @POST("alter")
    fun alterConfiguration(@Header("Authorization") authorization: String, @Body configurationRequest: ConfigurationRequest): Call<GenericResponse>

    @GET("alter")
    fun getRequestedConfigurationsForAdmin(@Header("Authorization") authorization: String): Call<AlterAdminResponse>

    @GET("alter")
    fun getRequestedConfigurationsForUser(@Header("Authorization") authorization: String): Call<AlterUserResponse>

    @HTTP(method = "DELETE", path = "alter", hasBody = true)
    fun deleteConfigurationRequest(@Header("Authorization") authorization: String, @Body deleteConfigurationRequest: DeleteConfigurationRequest): Call<GenericResponse>
}