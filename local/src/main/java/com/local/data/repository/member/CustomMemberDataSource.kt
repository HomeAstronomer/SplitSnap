package com.local.data.repository.member

import com.local.data.local.Member
import javax.inject.Inject

/**
 * An implementation of [MemberDataSource] that uses a custom backend for member data.
 */
class CustomMemberDataSource @Inject constructor(
    private val memberApiService: MemberApiService
) : MemberDataSource {

    override suspend fun getMember(uid: String): Member? {
        return try {
            memberApiService.getMember(uid)
        } catch (e: Exception) {
            // You might want to add more sophisticated error handling here
            null
        }
    }

    override suspend fun createMember(member: Member) {
        try {
            memberApiService.createMember(member)
        } catch (e: Exception) {
            // You might want to add more sophisticated error handling here
        }
    }
}
