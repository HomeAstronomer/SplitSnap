package com.local.data.repository.member

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.local.data.MEMBER_COLLECTION
import com.local.data.local.Member
import com.local.data.local.toMap
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MemberRemoteDataSource @Inject constructor(
    private val fireStoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun getMember(uid: String): Member? {
        val documentSnapshot = fireStoreDb.collection(MEMBER_COLLECTION).document(uid).get().await()
        return if (documentSnapshot.exists()) {
            documentSnapshot.toObject(Member::class.java)
        } else {
            null
        }
    }

    suspend fun signUp(email: String, password: String, displayName: String, phoneNumber: String): FirebaseUser {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val currentUser = authResult.user ?: throw Exception("User not created")

        val member = Member(
            uid = currentUser.uid,
            displayName = displayName,
            email = currentUser.email,
            phoneNumber = phoneNumber,
            photoUrl = currentUser.photoUrl?.toString()
        )

        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        currentUser.updateProfile(profileChangeRequest).await()

        fireStoreDb.collection(MEMBER_COLLECTION).document(currentUser.uid).set(member.toMap()).await()
        return currentUser
    }

    suspend fun signInWithGoogle(idToken: String): Member {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val authResult = firebaseAuth.signInWithCredential(credential).await()
        val user = authResult.user ?: throw Exception("Google Sign in failed")

        var member = getMember(user.uid)
        if (member == null) {
            member = Member(
                uid = user.uid,
                displayName = user.displayName ?: "",
                email = user.email,
                phoneNumber = user.phoneNumber ?: "",
                photoUrl = user.photoUrl?.toString()
            )
            fireStoreDb.collection(MEMBER_COLLECTION).document(user.uid).set(member.toMap()).await()
        }
        return member
    }
}