package com.aurora.app.data.remote.api

import com.aurora.app.data.remote.response.WorkResponse
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    @GET("/api/collections/work/records")
    suspend fun fetchWorks(
        @Query("expand") expand: String = "jsonFile"
    ): Response<WorkResponse>

    @Multipart
    @POST("api/wallpaper")
    suspend fun uploadWallpaper(
        @Part("title") title: RequestBody,
        @Part("resolution") resolution: RequestBody,
        @Part("album_id") albumId: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Response<String>

    @GET
    suspend fun fetchWorkFile(
        @Url fileUrl: String
    ): Response<JsonObject>

}