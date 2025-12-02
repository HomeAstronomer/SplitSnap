package com.local.data.repository.member

import com.google.firebase.firestore.FirebaseFirestore
import com.local.data.MEMBER_COLLECTION
import com.local.data.local.Member
import com.local.data.local.toMap
import com.local.data.repository.member.MemberDataSource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * A concrete implementation of [MemberDataSource] that uses Firebase Firestore
 * to store and retrieve member data.
 */
class FirebaseMemberDataSource @Inject constructor(
    private val fireStoreDb: FirebaseFirestore
) : MemberDataSource {

    override suspend fun getMember(uid: String): Member? {
        val documentSnapshot = fireStoreDb.collection(MEMBER_COLLECTION).document(uid).get().await()
        return documentSnapshot.toObject(Member::class.java)
    }

    override suspend fun createMember(member: Member) {
        fireStoreDb.collection(MEMBER_COLLECTION).document(member.uid).set(member.toMap()).await()
    }
}