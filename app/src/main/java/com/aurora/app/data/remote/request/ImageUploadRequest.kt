package com.aurora.app.data.remote.request

import com.aurora.app.domain.model.ImageUploadData
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.FileNotFoundException

data class ImageUploadRequest(
    val title: RequestBody,
    val resolution: RequestBody,
    val albumId: RequestBody,
    val imageFilePart: MultipartBody.Part
)

fun ImageUploadData.toImageUploadRequest(): ImageUploadRequest {
    val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
    val resolutionBody = resolution.toRequestBody("text/plain".toMediaTypeOrNull())
    val albumIdBody = album.toRequestBody("text/plain".toMediaTypeOrNull())

    // Handle image URI to file conversion
    val imageFilePart = imageFile.let {
        try {
            if (!imageFile.exists()) throw FileNotFoundException("File not found")
            val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image_file", imageFile.name, requestBody)
        } catch (e: FileNotFoundException) {
            throw IllegalArgumentException("Invalid image file: ${e.message}")
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to prepare image: ${e.message}")
        }
    }

    return ImageUploadRequest(
        title = titleBody,
        resolution = resolutionBody,
        imageFilePart = imageFilePart,
        albumId = albumIdBody
    )
}
