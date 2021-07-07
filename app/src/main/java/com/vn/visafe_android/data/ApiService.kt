package com.vn.visafe_android.data

import com.vn.visafe_android.model.GroupData
import com.vn.visafe_android.model.UserInfo
import com.vn.visafe_android.model.WorkspaceGroupData
import com.vn.visafe_android.model.request.*
import com.vn.visafe_android.model.response.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    @POST("register")
    fun doRegister(@Body registerRequest: RegisterRequest): Call<BaseResponse>

    @POST("login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("forgot-password")
    fun doRequestEmailForgotPassword(@Query("username") username: String?): Call<ResponseBody>

    @POST("reset-password")
    fun doResetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<BaseResponse>

    @POST("activate-account")
    fun doActiveAccount(@Body activeAccountRequest: ActiveAccountRequest): Call<BaseResponse>

    @GET("user/profile")
    fun doGetUserInfo(): Call<UserInfo>

    @PATCH("user/change-password")
    fun doChangePassword(@Body changePasswordRequest: ChangePasswordRequest): Call<ResponseBody>

    @POST("re-activation")
    fun doReActiveAccount(@Body loginRequest: LoginRequest): Call<BaseResponse>

    @GET("workspaces")
    fun doGetWorkSpacesOfCurrentUser(): Call<List<WorkspaceGroupData>>

    @GET("group")
    fun doGetAGroupWithId(@Query("groupid") groupid: String?): Call<List<GroupData>>

    @GET("group")
    fun doGetGroupWithId(@Query("wsId") wsId: String?): Call<List<GroupData>>

    @POST("group/add")
    fun doCreateGroup(@Body createGroupRequest: CreateGroupRequest): Call<ResponseBody>

    @POST("workspace/add")
    fun doCreateWorkspace(@Body createWorkSpaceRequest: CreateWorkSpaceRequest): Call<WorkspaceGroupData>

    @HTTP(
        method = "DELETE",
        path = "workspace/delete",
        hasBody = true
    )
    fun doDeleteWorkspace(@Body deleteWorkSpaceRequest: DeleteWorkSpaceRequest): Call<ResponseBody>

    @PATCH("workspace/update/rename")
    fun doUpdateNameWorkSpace(@Body updateNameWorkspaceRequest: UpdateNameWorkspaceRequest): Call<ResponseBody>

    @PATCH("workspace/update")
    fun doUpdateWorkspace(@Body updateWorkspaceRequest: WorkspaceGroupData): Call<ResponseBody>

}