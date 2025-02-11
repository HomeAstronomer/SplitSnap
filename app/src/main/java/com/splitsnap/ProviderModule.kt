package com.splitsnap

import android.content.Context
import androidx.room.Room
import com.splitsnap.data.AppDatabase
import com.splitsnap.data.local.ExpenseDao
import com.splitsnap.data.local.GroupDao
import com.splitsnap.data.local.MemberDao
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
    fun provideFireBaseVertexAI(): GenerativeModel{
        val transactionSchema = Schema.obj(
            mapOf(


                "receiver" to Schema.obj(
                    mapOf(
                        "name" to Schema.string(),
                        "upiId" to Schema.string()
                    )
                ),
                "amount" to Schema.double(),
                "time" to Schema.string("Iso Time"), // ISO 8601 format
                "transactionId" to Schema.string(),
                "platform" to Schema.enumeration(listOf("GooglePay", "PhonePe", "Paytm", "Other"))
            )
        )

        val dangerousContent = SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.NONE)
        val sexuallyExplicit = SafetySetting(HarmCategory.SEXUALLY_EXPLICIT, HarmBlockThreshold.NONE)
        val hateSpeech = SafetySetting(HarmCategory.HATE_SPEECH, HarmBlockThreshold.NONE)
        val harassment = SafetySetting(HarmCategory.HARASSMENT, HarmBlockThreshold.NONE)
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
