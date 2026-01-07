package com.example.coffepath.di

import android.content.Context
import com.example.coffepath.BuildConfig
import com.example.coffepath.api.OverpassApi
import com.example.coffepath.database.CafeDao
import com.example.coffepath.database.CafeDatabase
import com.example.coffepath.database.ReviewDao
import com.example.coffepath.database.UserProfileDao
import com.example.coffepath.mlkit.DrinkClassifier
import com.example.coffepath.mlkit.MenuOCRReader
import com.example.coffepath.repository.CafeRepositoryImpl
import com.example.coffepath.repository.ICafeRepository
import com.example.coffepath.repository.PreferencesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.SERVER_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    @Provides
    @Singleton
    fun provideOverpassApi(retrofit: Retrofit): OverpassApi {
        return retrofit.create(OverpassApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CafeDatabase {
        return CafeDatabase.getDatabase(context)
    }

    @Provides
    @Singleton
    fun provideCafeDao(database: CafeDatabase): CafeDao {
        return database.cafeDao()
    }

    @Provides
    @Singleton
    fun provideReviewDao(database: CafeDatabase): ReviewDao {
        return database.reviewDao()
    }

    @Provides
    @Singleton
    fun provideUserProfileDao(database: CafeDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    @Singleton
    fun provideCafeRepository(dao: CafeDao, api: OverpassApi): ICafeRepository {
        return CafeRepositoryImpl(dao, api)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(@ApplicationContext context: Context): PreferencesRepository {
        return PreferencesRepository(context)
    }

    @Provides
    @Singleton
    fun provideDrinkClassifier(@ApplicationContext context: Context): DrinkClassifier {
        return DrinkClassifier(context)
    }

    @Provides
    @Singleton
    fun provideMenuOCRReader(@ApplicationContext context: Context): MenuOCRReader {
        return MenuOCRReader(context)
    }
}