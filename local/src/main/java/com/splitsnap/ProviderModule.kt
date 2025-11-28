package com.splitsnap

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.firebase.vertexai.GenerativeModel
import com.google.firebase.vertexai.type.HarmBlockThreshold
import com.google.firebase.vertexai.type.HarmCategory
import com.google.firebase.vertexai.type.SafetySetting
import com.google.firebase.vertexai.type.Schema
import com.google.firebase.vertexai.type.generationConfig
import com.google.firebase.vertexai.vertexAI
import com.local.data.AppDatabase
import com.local.data.local.ExpenseDao
import com.local.data.local.GroupDao
import com.local.data.local.MemberDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "Spliwise.db"
        ).build()
    }

    @Singleton
    @Provides
    fun provideFireStore(): FirebaseFirestore {
        return  Firebase.firestore
    }

    @Singleton
    @Provides
    fun provideFireBaseAuth(): FirebaseAuth {
        return   Firebase.auth
    }

    @Singleton
    @Provides
    fun provideFireBaseStorage(): FirebaseStorage {
        return   Firebase.storage
    }

    @Singleton
    @Provides
    fun provideFireBaseVertexAI(): GenerativeModel {
        val transactionSchema = Schema.Companion.obj(
            mapOf(


                "receiver" to Schema.Companion.obj(
                    mapOf(
                        "name" to Schema.Companion.string(),
                        "upiId" to Schema.Companion.string()
                    )
                ),
                "amount" to Schema.Companion.double(),
                "time" to Schema.Companion.string("Iso Time"), // ISO 8601 format
                "transactionId" to Schema.Companion.string(),
                "platform" to Schema.Companion.enumeration(listOf("GooglePay", "PhonePe", "Paytm", "Other"))
            )
        )

        val dangerousContent = SafetySetting(
            HarmCategory.Companion.DANGEROUS_CONTENT,
            HarmBlockThreshold.Companion.NONE
        )
        val sexuallyExplicit = SafetySetting(
            HarmCategory.Companion.SEXUALLY_EXPLICIT,
            HarmBlockThreshold.Companion.NONE
        )
        val hateSpeech =
            SafetySetting(HarmCategory.Companion.HATE_SPEECH, HarmBlockThreshold.Companion.NONE)
        val harassment =
            SafetySetting(HarmCategory.Companion.HARASSMENT, HarmBlockThreshold.Companion.NONE)
        return   com.google.firebase.Firebase.vertexAI.generativeModel("gemini-1.5-flash",generationConfig = generationConfig {
            responseMimeType = "application/json"
            responseSchema = transactionSchema
        },
            safetySettings = listOf(dangerousContent, sexuallyExplicit, hateSpeech, harassment)
        )
    }


    @Provides
    fun provideMemberDao(database: AppDatabase): MemberDao = database.memberDao()

    @Provides
    fun provideGroupDao(database: AppDatabase): GroupDao = database.groupDao()

    @Provides
    fun provideExpenseDao(database: AppDatabase): ExpenseDao = database.expenseDao()
}