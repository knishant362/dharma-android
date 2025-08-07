package com.aurora.app.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.aurora.app.BuildConfig
import com.aurora.app.data.local.database.AppDatabase
import com.aurora.app.data.local.database.dao.AppDao
import com.aurora.app.data.local.storage.StorageManagerImpl
import com.aurora.app.data.remote.api.ApiService
import com.aurora.app.data.repo.MainRepositoryImpl
import com.aurora.app.data.repo.MediaRepositoryImpl
import com.aurora.app.domain.repo.MainRepository
import com.aurora.app.domain.repo.MediaRepository
import com.chuckerteam.chucker.api.ChuckerInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG){
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttpInterceptor(
        loggingInterceptor: HttpLoggingInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val CACHE_SIZE = 100 * 1024 * 1024L // 100 MB
        val cacheDir = context.cacheDir
        val cache = Cache(cacheDir, CACHE_SIZE)
        return OkHttpClient
            .Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("appId", "aurora").build()
                chain.proceed(request)
            }
            .cache(cache)
            .addInterceptor(ChuckerInterceptor(context))
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {
        return Retrofit
            .Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideMainRepository(@ApplicationContext context: Context, apiService: ApiService, storageManagerImpl: StorageManagerImpl, appDao: AppDao): MainRepository {
        return MainRepositoryImpl(context, apiService, storageManagerImpl, appDao)
    }

    @Provides
    @Singleton
    fun provideMediaRepository(@ApplicationContext context: Context): MediaRepository {
        return MediaRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideSpreadStorageManager(dataStore: DataStore<Preferences>): StorageManagerImpl {
        return StorageManagerImpl(dataStore)
    }

    private const val DB_NAME = "dharma_database.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {

//        val dbPath = context.getDatabasePath("dharma_database.db")
//        dbPath.parentFile?.mkdirs() // Ensure directory exists
//        Timber.d("AppDatabase Database path: ${dbPath.absolutePath}")


        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dharma.db"
        ).createFromAsset("dharma.db").build()
//
//        return Room.databaseBuilder(
//            context.applicationContext,
//            AppDatabase::class.java,
//            "dharma_database.db"
//        )
//            .createFromAsset("dharma_database.db") // Adjust if in subfolder, e.g., "databases/dharma_database.db"
//            .fallbackToDestructiveMigration()
//            .build()
    }

    @Provides
    @Singleton
    fun provideAppDao(database: AppDatabase): AppDao {
        return database.appDao()
    }

    private fun copyDatabaseFromAssets(context: Context, dbPath: File) {
        try {
            dbPath.parentFile?.mkdirs()

            context.assets.list("")?.forEach {
                Timber.e("Nishant Asset: $it")
            }


            context.assets.open(DB_NAME).use { input ->
                FileOutputStream(dbPath).use { output ->
                    input.copyTo(output)
                }
            }

            Log.d("DB_COPY", "Copied $DB_NAME to ${dbPath.absolutePath}")
        } catch (e: IOException) {
            Log.e("DB_COPY", "Error copying DB from assets: ${e.localizedMessage}", e)
            throw RuntimeException("Failed to copy database from assets", e)
        }
    }



}