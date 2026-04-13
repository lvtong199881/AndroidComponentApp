package com.mohanlv.network.api

import com.mohanlv.network.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * WanAndroid API 服务接口
 * 文档：https://www.wanandroid.com/blog/show/2
 *
 * 注意：
 * - POST 请求建议使用 postman 模拟
 * - 登录错误码：-1001，其他错误码：-1，成功：0
 */
interface ApiService {

    // ==================== 用户相关 ====================

    /**
     * 用户登录
     * POST /user/login
     * 参数：username, password (form-urlencoded)
     */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<WanResponse<LoginData>>

    /**
     * 用户注册
     * POST /user/register
     * 参数：username, password, repassword (form-urlencoded)
     */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): Response<WanResponse<LoginData>>

    /**
     * 退出登录
     * GET /user/logout
     * 需要登录
     */
    @GET("user/logout")
    suspend fun logout(): Response<WanResponse<Any>>

    // ==================== 用户信息 ====================

    /**
     * 获取个人积分信息
     * GET /lg/coin/userinfo/json
     * 需要登录
     */
    @GET("lg/coin/userinfo/json")
    suspend fun getCoinInfo(): Response<WanResponse<UserInfo>>

    // ==================== 首页文章 ====================

    /**
     * 首页文章列表
     * GET /article/list/{page}/json
     * 页码从 0 开始
     */
    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page: Int): Response<WanResponse<PageData<Article> > >

    /**
     * 首页 Banner
     * GET /banner/json
     */
    @GET("banner/json")
    suspend fun getBanner(): Response<WanResponse<List<Banner>>>

    // ==================== 收藏相关 ====================

    /**
     * 收藏文章
     * POST /lg/collect/add/json
     * 参数：title, author, link
     */
    @POST("lg/collect/add/json")
    suspend fun collectArticle(@Body params: Map<String, String>): Response<WanResponse<Any>>

    /**
     * 获取收藏列表
     * GET /lg/collect/page/{page}/json
     */
    @GET("lg/collect/list/{page}/json")
    suspend fun getCollectList(@Path("page") page: Int): Response<WanResponse<PageData<Article>>>
}
