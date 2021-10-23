package vn.ncsc.visafe.data

import vn.ncsc.visafe.model.GroupData
import vn.ncsc.visafe.model.UserInfo
import vn.ncsc.visafe.model.WorkspaceGroupData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import vn.ncsc.visafe.model.RoutingResponse
import vn.ncsc.visafe.model.request.*
import vn.ncsc.visafe.model.response.*


interface ApiService {

    /*========Authen=============*/
    @POST("register")
    fun doRegister(@Body registerRequest: RegisterRequest): Call<BaseResponse>

    @POST("login")
    fun doLogin(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("login/google")
    fun doLoginWithGoogle(@Body loginSocialRequest: LoginSocialRequest): Call<LoginResponse>

    @POST("login/facebook")
    fun doLoginWithFacebook(@Body loginSocialRequest: LoginSocialRequest): Call<LoginResponse>

    @POST("forgot-password")
    fun doRequestEmailForgotPassword(@Body forgotPassword: LoginRequest): Call<ResponseBody>

    @POST("reset-password")
    fun doResetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<ResponseBody>

    @POST("activate-account")
    fun doActiveAccount(@Body activeAccountRequest: ActiveAccountRequest): Call<BaseResponse>

    @POST("re-activation")
    fun doReActiveAccount(@Body loginRequest: LoginRequest): Call<BaseResponse>

    /*===========User=============*/
    @GET("user/profile")
    fun doGetUserInfo(): Call<UserInfo>

    @PATCH("user/change-password")
    fun doChangePassword(@Body changePasswordRequest: ChangePasswordRequest): Call<ResponseBody>

    @PATCH("user/change-profile")
    fun doChangeProfile(@Body changeProfiledRequest: RegisterRequest): Call<ResponseBody>

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
    fun doUpdateNameWorkSpace(@Body updateNameWorkspaceRequest: UpdateNameWorkspaceRequest): Call<WorkspaceGroupData>

    @PATCH("workspace/update")
    fun doUpdateWorkspace(@Body updateWorkspaceRequest: WorkspaceGroupData): Call<ResponseBody>

    /*=========Group=================*/
    @GET("group")
    fun doGetAGroupWithId(@Query("groupid") groupid: String?): Call<GroupData>

    @GET("groups")
    fun doGetGroupsWithId(@Query("wsId") wsId: String?): Call<GroupsDataResponse>

    @POST("group/add")
    fun doCreateGroup(@Body createGroupRequest: CreateGroupRequest): Call<ResponseBody>

    @PATCH("group/update")
    fun doUpdateGroup(@Body groupData: GroupData): Call<ResponseBody>

    @PATCH("group/update/rename")
    fun doUpdateNameGroup(@Body updateGroupNameRequest: UpdateGroupNameRequest): Call<ResponseBody>

    @HTTP(
        method = "DELETE",
        path = "group/delete",
        hasBody = true
    )
    fun doDeleteGroup(@Body deleteGroupRequest: DeleteGroupRequest): Call<ResponseBody>

    /*=========User in Group=================*/
    @PATCH("group/update/user-to-manager")
    fun doUpgradeUserToManager(@Body userInGroupRequest: UserInGroupRequest): Call<ResponseBody>

    @PATCH("group/update/whitelist")
    fun doUpdateWhiteList(@Body updateWhiteListRequest: UpdateWhiteListRequest): Call<ResponseBody>

    @HTTP(
        method = "DELETE",
        path = "group/delete/member",
        hasBody = true
    )
    fun doRemoveUserFromGroup(@Body removeUserRequest: UserInGroupRequest): Call<ResponseBody>

    @POST("group/invite/members")
    fun doInviteUserIntoGroup(@Body inviteUserRequest: UserInGroupRequest): Call<AddMemberInGroupResponse>

    @POST("user/out-group")
    fun doUserLeaveGroup(@Body leaveGroupRequest: UserInGroupRequest): Call<ResponseBody>

    @PATCH("group/update/user-to-viewer")
    fun doUpgradeUserToViewer(@Body userToViewerRequest: UserInGroupRequest): Call<ResponseBody>

    /*=========Device in Group=================*/
    @HTTP(
        method = "DELETE",
        path = "group/delete/device",
        hasBody = true
    )
    fun doRemoveDeviceFromGroup(@Body removeDeviceRequest: RemoveDeviceRequest): Call<ResponseBody>

    @POST("group/invite/device")
    fun doAddDeviceToGroup(@Body addDeviceRequest: AddDeviceRequest): Call<BaseResponse>

    @PATCH("group/update/device")
    fun doUpgradeDevice(@Body updateDevice: UpdateDeviceRequest): Call<ResponseBody>

    @POST("device/request-out-group")
    fun doRequestOutGroup(@Body outGroupRequest: OutGroupRequest): Call<BaseResponse>

    @POST("device/active-vip")
    fun doActiveVip(@Body activeVipRequest: ActiveVipRequest): Call<ResponseBody>

    /*=========Notification=================*/
    @GET("user/notifications")
    fun doGetNotification(@Query("page") page: Int?): Call<NotificationResponse>

    @POST("user/see-notifications")
    fun doSeeNotification(@Body notificationRequest: NotificationRequest): Call<ResponseBody>

    @POST("user/read-notification")
    fun doReadANotification(@Body notificationRequest: NotificationRequest): Call<ResponseBody>

    @POST("user/read-all-notification")
    fun doReadAllNotification(): Call<ResponseBody>

    /*=========Stats==================================*/
    @GET("stats/workspace")
    fun doGetStatisticalOneWorkspace(
        @Query("workspace_id") workspace_id: String?,
        @Query("time_limit") time_limit: String?
    ): Call<StatsWorkspaceResponse>

    @GET("stats/group")
    fun doGetStatisticalOneGroup(
        @Query("group_id") group_id: String?,
        @Query("time_limit") time_limit: String?
    ): Call<StatsWorkspaceResponse>

    @GET("stats/client")
    fun doGetStatisticalOneDeviceInGroup(
        @Query("workspace_id") workspace_id: String?,
        @Query("client_id") client_id: String?,
        @Query("time_limit") time_limit: String?
    ): Call<StatsWorkspaceResponse>

    @POST("ipma")
    fun doCheckBotnet(): Call<BotnetResponse>

    @GET("querylog_group")
    fun doGetQueryLogGroup(
        @Query("group_id") group_id: String?,
        @Query("response_status") response_status: String?,
        @Query("limit") limit: String?,
        @Query("older_than") older_than: String?,
    ): Call<QueryLogResponse>

    @POST("stats/delete_log")
    fun doDeleteLog(@Body deleteLogRequest: DeleteLogRequest): Call<ResponseBody>

    @POST("report_phishing")
    fun doReportWebsitePhishing(@Body reportWebRequest: ReportWebRequest): Call<ResponseBody>

    /*=========Another==================================*/
    @GET("control/gen-device-id")
    fun doGetDeviceId(): Call<DeviceIdResponse>

    @POST("device/register")
    fun doSendToken(@Body sendTokenRequest: SendTokenRequest): Call<ResponseBody>

    @GET("packages")
    fun doGetAllPackage(): Call<MutableList<AllPackageResponse>>

    @POST("order")
    fun doOderPayPackage(@Body oderPayRequest: OderPayRequest): Call<PayPackageResponse>

    @POST("device/check")
    fun doCheckDeviceInGroup(@Body checkDevice: SendTokenRequest): Call<CheckDeviceInGroupResponse>

    @GET("routing")
    fun doGetDnsUrl(): Call<RoutingResponse>
}