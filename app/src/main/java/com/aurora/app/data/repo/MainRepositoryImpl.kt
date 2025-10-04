package com.aurora.app.data.repo

import android.content.Context
import com.aurora.app.BuildConfig
import com.aurora.app.core.analytics.firebase.Analytics
import com.aurora.app.data.local.database.AppDatabase
import com.aurora.app.data.local.database.dao.AppDao
import com.aurora.app.data.local.database.entity.PostEntity
import com.aurora.app.data.local.storage.StorageManager
import com.aurora.app.data.model.SpreadResult
import com.aurora.app.data.model.User
import com.aurora.app.data.model.WorkDto
import com.aurora.app.data.model.toReaderStyleModel
import com.aurora.app.data.model.toWorkDto
import com.aurora.app.data.model.wallpaper.WallpaperExtra
import com.aurora.app.data.model.work.WorkModel
import com.aurora.app.data.remote.api.ApiService
import com.aurora.app.data.remote.request.ImageUploadRequest
import com.aurora.app.di.AppModule.DB_NAME
import com.aurora.app.domain.model.ReaderStyle
import com.aurora.app.domain.model.toReaderStyle
import com.aurora.app.domain.model.wallpaper.WallpaperExtraDto
import com.aurora.app.domain.model.wallpaper.toDto
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.utils.Constants
import com.aurora.app.utils.ResponseState
import com.aurora.app.utils.safeApiCall
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val storageManager: StorageManager,
    private val appDao: AppDao,
    private val database: AppDatabase
) : MainRepository {

    val gson = Gson()

    override suspend fun fetchReaderStyle(): ReaderStyle {
        return storageManager.getReaderStyle()?.toReaderStyle() ?: ReaderStyle.Default
    }

    override suspend fun setReaderStyle(readerStyle: ReaderStyle) {
        storageManager.setReaderStyle(readerStyle.toReaderStyleModel())
    }

    override suspend fun getUserProfile(): User {
        return User(
            name = storageManager.getName(),
            gender = storageManager.getGender(),
            dateOfBirth = storageManager.getDateOfBirth(),
            relationshipStatus = storageManager.getRelationshipStatus(),
            occupation = storageManager.getOccupation()
        )
    }

    override suspend fun saveUserProfile(
        name: String,
        gender: String,
        dateOfBirth: String,
        relationshipStatus: String,
        occupation: String
    ) {
        storageManager.setName(name)
        storageManager.setGender(gender)
        storageManager.setDateOfBirth(dateOfBirth)
        storageManager.setRelationshipStatus(relationshipStatus)
        storageManager.setOccupation(occupation)
    }

    override suspend fun getWorkDetails(workDto: WorkDto): ResponseState<WorkModel?> {
        val folder = File(context.filesDir, Constants.WORK_DIRECTORY)
        val filename = "${workDto.id}.json"
        val workFile = File(folder, filename)
        if (workFile.exists()) {
            val workModel = gson.fromJson(workFile.readText(), WorkModel::class.java)
            return ResponseState.Success(workModel)
        } else {
            val fileUrl = "${BuildConfig.BASE_URL}/${Constants.FILE_ENDPOINT}/${workDto.jsonFile}"
            val response = apiService.fetchWorkFile(fileUrl)
            if (response.isSuccessful) {
                val json = response.body()
                if (json != null) {
                    folder.mkdirs() // Create directory if it doesn't exist
                    workFile.writeText(gson.toJson(json))
                    val data = gson.fromJson(json, WorkModel::class.java)
                    return ResponseState.Success(message = "File download successfully", data = data)
                } else {
                    return ResponseState.Error(message = "Work model is null")
                }
            } else {
                return ResponseState.Error(message = "Failed to fetch work file: ${response.message()}")
            }
        }
    }

    override suspend fun getAllPosts(): List<PostEntity> {
        return appDao.getAllPosts()
    }

    override suspend fun getPosts(id: String): List<PostEntity> {
        return appDao.getPostsById(id)
    }

    override suspend fun getPostsByType(mType: String): List<PostEntity> {
        return appDao.getPostsByType(mType)
    }

    override suspend fun getWallpapersData(id: String): ResponseState<WallpaperExtraDto> {
        return try {
            val response = appDao.getPostsById(id).firstOrNull()?.extra ?: throw Exception("No data found")
            val data = gson.fromJson(response, WallpaperExtra::class.java)
            ResponseState.Success(data.toDto())
        } catch (e: Exception) {
            ResponseState.Error(message = e.message ?: "Unknown error")
        }
    }

    override suspend fun getWallpapers(
        mType: String,
        data: String
    ): List<PostEntity> {
        return appDao.getPosts(mType = mType, topics = data)
    }

    override suspend fun fetchWorks(): ResponseState<List<WorkDto>> =
        safeApiCall(
            call = { apiService.fetchWorks(filter = "", perPage = 200) },
            success = { it.items.map { work -> work.toWorkDto() } }
        )

    override suspend fun uploadWallpaper(request: ImageUploadRequest): ResponseState<String> =
        safeApiCall(
            call = {
                apiService.uploadWallpaper(
                    imageFile = request.imageFilePart,
                    resolution = request.resolution,
                    title = request.title,
                    albumId = request.albumId
                )
            },
            success = { it }
        )


    override suspend fun saveSpread(spreadDetailId: String, selectedCardIds: List<String>) {
        storageManager.saveSpread(spreadDetailId, selectedCardIds)
    }

    override suspend fun getSavedSpreads(): List<SpreadResult> {
        return storageManager.getSavedSpreads()
    }

    override suspend fun getSpreadResultBySpreadId(spreadId: String): List<SpreadResult> {
        return storageManager.getSpreadsBySpreadId(spreadId)
    }

    override suspend fun deleteResult(result: SpreadResult): Boolean {
        return storageManager.deleteResult(result)
    }

    override suspend fun getDbVersion(): Int? {
        return storageManager.getVersion()
    }

    override suspend fun verifyDatabase(): Flow<ResponseState<Int>> = flow {
        try {
            val localVersion = getDbVersion()
            val response = apiService.getDbVersion("${BuildConfig.HOST_BASE_URL}/version.json")

            if (!response.isSuccessful) {
                Analytics.logEvent("db_update_failure", "reason" to "version_check_failed", "local_version" to (localVersion?.toString() ?: "null"))
                emit(ResponseState.Error(message = "Failed to check database version"))
                return@flow
            }

            val data = response.body()
            if (data == null) {
                Analytics.logEvent("db_update_failure", "reason" to "invalid_version_data", "local_version" to (localVersion?.toString() ?: "null"))
                emit(ResponseState.Error(message = "Invalid version data"))
                return@flow
            }

            val remoteVersion = data.version
            val dbUrl = data.url
            val shouldUpgrade = (localVersion == null) || remoteVersion > localVersion

            Analytics.logEvent("db_update_check", "local_version" to (localVersion?.toString() ?: "null"), "remote_version" to remoteVersion, "should_upgrade" to shouldUpgrade)

            if (shouldUpgrade) {
                emit(ResponseState.Loading())
                val status = updateDatabase("${BuildConfig.HOST_BASE_URL}/$dbUrl")
                if (status) {
                    storageManager.saveVersion(remoteVersion)
                    Analytics.logEvent("db_update_success", "new_version" to remoteVersion)
                    emit(ResponseState.Success(data = remoteVersion, message = "Database updated to version $remoteVersion"))
                } else {
                    Analytics.logEvent("db_update_failure", "reason" to "db_update_failed", "local_version" to (localVersion?.toString() ?: "null"), "remote_version" to remoteVersion)
                    emit(ResponseState.Error(message = "Database update failed"))
                }
            } else {
                emit(ResponseState.Success(data = null, message = "No upgrade needed"))
            }
        } catch (e: Exception) {
            Analytics.logEvent("db_update_failure", "reason" to "exception_${e.javaClass.simpleName}")
            emit(ResponseState.Error(message = e.message ?: "Error verifying database"))
        }
    }

    private fun updateDatabase(dbUrl: String): Boolean {
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(dbUrl).build()
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                Timber.e("updateDatabase: Failed response: ${response.code}")
                return false
            }

            val tempFile = File(context.cacheDir, "new_$DB_NAME")
            response.body?.byteStream()?.use { input ->
                FileOutputStream(tempFile, false).use { output ->
                    input.copyTo(output)
                }
            }

            database.close()

            val dbPath = context.getDatabasePath(DB_NAME)
            if (dbPath.exists()) {
                dbPath.delete()
            }

            tempFile.copyTo(dbPath, overwrite = true)
            tempFile.delete()

            Timber.d("updateDatabase: New DB copied to: ${dbPath.absolutePath}")
            return true
        } catch (e: Exception) {
            Timber.e(e, "updateDatabase: Error replacing DB: ${e.message}")
            return false
        }
    }
}