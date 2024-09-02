package com.example.aisplitwise

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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





    @Provides
    fun provideTaskDao(database: AppDatabase): UserDao = database.userDao()
}
