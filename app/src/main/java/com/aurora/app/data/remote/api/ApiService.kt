package com.aurora.app.data.remote.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @GET("/api/homepage")
    suspend fun fetchHomepageData(): Response<String>

    @GET("api/wallpaper")
    suspend fun fetchWallpapers(
        @Query("page") page: Int,
        @Query("perPage") perPage: Int = 20,
    ): Response<String>

    @Multipart
    @POST("api/wallpaper")
    suspend fun uploadWallpaper(
        @Part("title") title: RequestBody,
        @Part("resolution") resolution: RequestBody,
        @Part("album_id") albumId: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Response<String>

}