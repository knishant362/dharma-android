package com.aurora.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import com.aurora.app.BuildConfig
import com.aurora.app.data.local.database.AppDatabase
import com.aurora.app.data.local.database.dao.AppDao
import com.aurora.app.data.local.storage.StorageManagerImpl
import com.aurora.app.data.remote.api.ApiService
import com.aurora.app.data.remote.api.horoscope.HoroscopeService
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
    fun provideMainRepository(@ApplicationContext context: Context, apiService: ApiService, storageManagerImpl: StorageManagerImpl, appDao: AppDao, database: AppDatabase): MainRepository {
        return MainRepositoryImpl(context, apiService, storageManagerImpl, appDao, database)
    }

    @Provides
    @Singleton
    fun provideHoroscopeService(): HoroscopeService {
        return HoroscopeService()
    }

    @Provides
    @Singleton
    fun provideMediaRepository(@ApplicationContext context: Context, horoscopeService: HoroscopeService): MediaRepository {
        return MediaRepositoryImpl(context, horoscopeService)
    }

    @Provides
    @Singleton
    fun provideSpreadStorageManager(dataStore: DataStore<Preferences>): StorageManagerImpl {
        return StorageManagerImpl(dataStore)
    }

    const val DB_NAME = "dharma.db"

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            DB_NAME
        ).createFromAsset(DB_NAME).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideAppDao(database: AppDatabase): AppDao {
        return database.appDao()
    }

}