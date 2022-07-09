package com.example.tex.retrofit

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface MainInterface {

    @GET("/new.json?")
    fun getNewPosts(
        @Query("after") after: String,
        @Query("limit") limit: String,
        @Query("sr_detail") sr_detail: Int
    ): Call<ResponseBody>

    @GET("/hot.json?")
    fun getHotPosts(
        @Query("after") after: String,
        @Query("limit") limit: String,
        @Query("sr_detail") sr_detail: Int
    ): Call<ResponseBody>

    @GET("/api/v1/me")
    fun getProfileInfo(): Call<ResponseBody>

    @GET("/api/v1/me/friends")
    fun getFriends(): Call<ResponseBody>

    @GET("/user/{name}/comments.json?")
    fun getUsersComments(
        @Path("name") name: String,
        @Query("limit") limit: Int,
        @Query("after") after: String,
    ): Call<ResponseBody>

    @GET("/u/{name}/about.json")
    fun getSubredditsInfo(
        @Path("name") name: String,
    ): Call<ResponseBody>

    @PUT("/api/v1/me/friends/{name}")
    fun makeUserFriend(
        @Path("name") name: String,
        @Body params: RequestBody?,
    ): Call<Unit>

    @HTTP(method = "DELETE", path = "/api/v1/me/friends/{name}", hasBody = true)
    fun unfriendUser(
        @Path("name") name: String,
        @Body params: RequestBody?,
    ): Call<Unit>

    @POST("/api/subscribe?action=sub")
    suspend fun follow(@Query("sr_name") name: String): Response<Unit>

    @POST("/api/subscribe?action=unsub")
    suspend fun unFollow(@Query("sr_name") name: String): Response<Unit>

    @GET("{link}{id}.json?limit=20")
    fun getComments(
        @Path("link", encoded = true) link: String,
        @Path("id", encoded = true) id: String,
    ): Call<ResponseBody>

    @GET("/user/{username}/saved.json?")
    fun getSaved(
        @Path("username") name: String,
        @Query("type") type: String,
        @Query("after") after: String,
        @Query("limit") limit: String,
    ): Call<ResponseBody>

    @GET("/search?")
    fun searchPosts(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("after") after: String,
        @Query("sr_detail") sr_detail: Int
    ): Call<ResponseBody>

    @GET("/api/morechildren?&children=")
    fun getMoreComments(
        @Query("api_type") type: String = "json",
        @Query("link_id") linkId: String,
        @Query("showmore") showMore: Boolean = true,
        @Query("children") listOfIds: String,
    ): Call<ResponseBody>

    @POST("/api/vote?")
    fun vote(
        @Query("id") id: String,
        @Query("dir") vote: Int,
    ): Call<Unit>

    @POST("/api/save?")
    fun saveById(
        @Query("id") id: String,
        @Query("category") category: String,
    ): Call<Unit>

    @POST("/api/unsave?")
    fun unsaveById(
        @Query("id") id: String
    ): Call<Unit>

    @POST("/api/comment?")
    fun addComment(
        @Query("api_type") apiType: String,
        @Query("thing_id") id: String,
        @Query("text") text: String,
    ): Call<Unit>
}