package com.example.aisplitwise.data.repository

import com.example.aisplitwise.data.local.Member
import com.example.aisplitwise.data.local.MemberDao
import com.example.aisplitwise.data.local.toMap
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val memberDao: MemberDao,
    private val fireStoreDb: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
){

    fun firebaseAuthSignIn(email: String, password: String): Flow<DataState<AuthResult>> = flow {
        try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val currentUser = authResult.user

            if (currentUser != null) {
                val member = getMemberFromFirestore(currentUser.uid)

                if (member != null) {
                    memberDao.insertMember(member)
                }
                emit(DataState.Success(authResult))
            } else {
                emit(DataState.Error("No authenticated user found"))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Something Went Wrong"))
        }
    }

    private suspend fun getMemberFromFirestore(uid: String): Member? {
        return try {
            val documentSnapshot = fireStoreDb.collection("Members").document(uid).get().await()
            if (documentSnapshot.exists()) {
                documentSnapshot.toObject(Member::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            // Optionally, log the error
            null
        }


    }

    fun signup(
        email: String,
        password: String,
        displayName: String,
        phoneNumber: String
    ): Flow<DataState<FirebaseUser?>> = flow {
        try {
            // Sign up using Firebase Auth
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val currentUser = authResult.user

            if (currentUser != null) {
                // Add user to Members collection
                val memberAdded = addUserToMembers(currentUser, displayName, phoneNumber)

                if (memberAdded!=null) {
                    memberDao.deleteAllMembers()
                    memberDao.insertMember(memberAdded)
                    emit(DataState.Success(currentUser))
                } else {
                    emit(DataState.Error("Failed to add user to members collection"))
                }
            } else {
                emit(DataState.Error("No authenticated user found"))
            }
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Something Went Wrong"))
        }
    }

    private suspend fun addUserToMembers(
        currentUser: FirebaseUser,
        displayName: String,
        phoneNumber: String
    ): Member? {
        return try {
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

            // Update Firebase Auth profile
            currentUser.updateProfile(profileChangeRequest).await()

            // Convert the Member object to a Map
            val memberMap = member.toMap()

            // Add the Member to the "Members" collection in Firestore
            fireStoreDb.collection("Members")
                .document(currentUser.uid)
                .set(memberMap)
                .await()

            member // Indicate that the member was successfully added
        } catch (e: Exception) {
            // Handle error (log or print as needed)
            println("Error adding user to members collection: ${e.message}")
            null // Indicate that there was an error
        }
    }

    fun getMemberDb(): Flow<List<Member>> {
        return memberDao.getAllFlow()
    }






}