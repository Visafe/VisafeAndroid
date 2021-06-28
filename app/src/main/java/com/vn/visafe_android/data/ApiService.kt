package com.vn.visafe_android.data

import com.vn.visafe_android.model.LoginRequest
import com.vn.visafe_android.model.RegisterRequest
import com.vn.visafe_android.model.ResetPasswordRequest
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("/control/register")
    fun doRegister(@Body registerRequest: RegisterRequest): Call<BaseResponse>

    @POST("/control/login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<BaseResponse>

    @GET("/control/forgot-password")
    fun doRequestEmailForgotPassword(@Query("email") email: String?): Call<BaseResponse>

    @POST("/control/reset-password")
    fun doResetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<BaseResponse>

}