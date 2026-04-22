package com.mohanlv.network.model

import com.google.gson.annotations.SerializedName

/**
 * WanAndroid 统一响应体
 * errorCode: 0=成功, -1001=需要登录, 其他=错误
 */
data class WanResponse<T>(
    @SerializedName("errorCode") val errorCode: Int = 0,
    @SerializedName("errorMsg") val errorMsg: String = "",
    @SerializedName("data") val data: T? = null
) {
    fun isSuccess(): Boolean = errorCode == 0
    fun needLogin(): Boolean = errorCode == -1001
}

/**
 * 登录请求体
 */
data class LoginRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)

/**
 * 注册请求体
 */
data class RegisterRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("repassword") val repassword: String
)

/**
 * 登录响应数据
 */
data class LoginData(
    @SerializedName("chapterTops") val chapterTops: List<String>? = null,
    @SerializedName("collectIds") val collectIds: List<Int>? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("icon") val icon: String? = null,
    @SerializedName("id") val id: Int = 0,
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("password") val password: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("type") val type: Int = 0,
    @SerializedName("username") val username: String? = null
)

/**
 * 用户信息（个人中心）
 */
data class UserInfo(
    @SerializedName("coinCount") val coinCount: Int = 0,         // 积分
    @SerializedName("rank") val rank: String = "",               // 排名
    @SerializedName("userId") val userId: Int = 0,              // 用户ID
    @SerializedName("username") val username: String = ""        // 用户名
)

/**
 * 首页文章
 */
data class Article(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("desc") val desc: String = "",
    @SerializedName("author") val author: String = "",
    @SerializedName("shareUser") val shareUser: String = "",
    @SerializedName("link") val link: String = "",
    @SerializedName("publishTime") val publishTime: Long = 0,
    @SerializedName("superChapterName") val superChapterName: String = "",
    @SerializedName("chapterName") val chapterName: String = "",
    @SerializedName("zap") val zap: Boolean = false,
    @SerializedName("niceShareDate") val niceShareDate: String = "",
    @SerializedName("niceDate") val niceDate: String = "",
    @SerializedName("collect") val collect: Boolean = false
)

/**
 * Banner
 */
data class Banner(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("desc") val desc: String = "",
    @SerializedName("imagePath") val imagePath: String = "",
    @SerializedName("url") val url: String = "",
    @SerializedName("type") val type: Int = 0
)

/**
 * 分页数据
 */
data class PageData<T>(
    @SerializedName("curPage") val curPage: Int = 1,
    @SerializedName("datas") val datas: List<T> = emptyList(),
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("over") val over: Boolean = false,
    @SerializedName("pageCount") val pageCount: Int = 0,
    @SerializedName("size") val size: Int = 0,
    @SerializedName("total") val total: Int = 0
)
