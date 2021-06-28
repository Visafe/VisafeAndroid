package com.vn.visafe_android.data

import com.vn.visafe_android.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("/control/register")
    fun doRegister(@Body registerRequest: RegisterRequest): Call<BaseResponse>

}