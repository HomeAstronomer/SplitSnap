package com.local.data.repository.member

import com.local.data.local.Member

/**
 * Defines the contract for a remote data source that handles member data.
 * This allows for multiple implementations (e.g., Firebase, custom backend).
 */
interface MemberDataSource {
    suspend fun getMember(uid: String): Member?
    suspend fun createMember(member: Member)
}