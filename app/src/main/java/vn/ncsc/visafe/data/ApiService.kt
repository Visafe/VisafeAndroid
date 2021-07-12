package vn.ncsc.visafe.data

import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.model.WorkspaceGroupData
import vn.ncsc.visafe.model.response.GroupsDataResponse
import vn.ncsc.visafe.model.response.LoginResponse
import vn.ncsc.visafe.model.response.NotificationResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import vn.ncsc.visafe.model.request.*


interface ApiService {

    /*========Authen=============*/
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

    @POST("re-activation")
    fun doReActiveAccount(@Body loginRequest: LoginRequest): Call<BaseResponse>

    /*===========User=============*/
    @GET("user/profile")
    fun doGetUserInfo(): Call<UserInfo>

    @PATCH("user/change-password")
    fun doChangePassword(@Body changePasswordRequest: ChangePasswordRequest): Call<ResponseBody>

    /*===========Workspace=============*/
    @GET("workspaces")
    fun doGetWorkSpacesOfCurrentUser(): Call<List<WorkspaceGroupData>>

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

    /*=========Group=================*/
    @GET("group")
    fun doGetAGroupWithId(@Query("groupid") groupid: String?): Call<GroupData>

    @GET("groups")
    fun doGetGroupsWithId(@Query("wsId") wsId: String?): Call<GroupsDataResponse>

    @POST("group/add")
    fun doCreateGroup(@Body createGroupRequest: CreateGroupRequest): Call<ResponseBody>

    @HTTP(
        method = "DELETE",
        path = "group/delete",
        hasBody = true
    )
    fun doDeleteGroup(@Body deleteGroupRequest: DeleteGroupRequest): Call<ResponseBody>

    /*=========Notification=================*/
    @GET("user/notifications")
    fun doGetNotification(@Query("page") page: Int?): Call<NotificationResponse>

    @POST("user/see-notifications")
    fun doSeeNotification(@Body notificationRequest: NotificationRequest): Call<ResponseBody>

    @POST("user/read-notification")
    fun doReadANotification(@Body notificationRequest: NotificationRequest): Call<ResponseBody>

    @POST("user/read-all-notification")
    fun doReadAllNotification(): Call<ResponseBody>
}