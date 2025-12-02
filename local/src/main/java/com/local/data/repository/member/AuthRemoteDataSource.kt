package com.local.data.repository.member

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * A dedicated data source for handling all authentication-related tasks with Firebase.
 * This class is responsible for signing in, signing up, and signing out users.
 */
class AuthRemoteDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return authResult.user ?: throw Exception("Authentication failed")
    }

    suspend fun signUp(email: String, password: String, displayName: String): FirebaseUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val currentUser = authResult.user ?: throw Exception("User not created")

        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        currentUser.updateProfile(profileChangeRequest).await()
        return currentUser
    }

    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        return authResult.user ?: throw Exception("Google Sign in failed")
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }
}