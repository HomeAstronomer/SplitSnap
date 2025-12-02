package com.local.data.repository.member

import com.local.data.local.Member
import com.local.data.local.MemberDao
import com.local.data.repository.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepository @Inject constructor(
    private val memberDao: MemberDao,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val memberDataSource: MemberDataSource
) {

    fun getLoggedInMember(): Flow<Member?> {
        return memberDao.getAllFlow().map { it.firstOrNull() }
    }

    fun firebaseAuthSignIn(email: String, password: String): Flow<DataState<Member>> = flow {
        emit(DataState.Loading)
        try {
            val user = authRemoteDataSource.signInWithEmail(email, password)
            val member = memberDataSource.getMember(user.uid)
                ?: throw IllegalStateException("Authenticated user not found in database.")

            memberDao.deleteAllMembers()
            memberDao.insertMember(member)
            emit(DataState.Success(member))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Sign-in failed"))
        }
    }

    fun signup(
        email: String, password: String, displayName: String, phoneNumber: String
    ): Flow<DataState<Member>> = flow {
        emit(DataState.Loading)
        try {
            val user = authRemoteDataSource.signUp(email, password, displayName)
            val member = Member(
                uid = user.uid,
                displayName = displayName,
                email = user.email,
                phoneNumber = phoneNumber,
                photoUrl = user.photoUrl?.toString()
            )
            memberDataSource.createMember(member)

            memberDao.deleteAllMembers()
            memberDao.insertMember(member)
            emit(DataState.Success(member))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Sign-up failed"))
        }
    }

    fun loginUsingGoogle(idToken: String): Flow<DataState<Member>> = flow {
        emit(DataState.Loading)
        try {
            val user = authRemoteDataSource.signInWithGoogle(idToken)
            var member = memberDataSource.getMember(user.uid)

            if (member == null) {
                member = Member(
                    uid = user.uid,
                    displayName = user.displayName ?: "",
                    email = user.email,
                    phoneNumber = user.phoneNumber ?: "",
                    photoUrl = user.photoUrl?.toString()
                )
                memberDataSource.createMember(member)
            }

            memberDao.deleteAllMembers()
            memberDao.insertMember(member)
            emit(DataState.Success(member))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Google sign-in failed"))
        }
    }

    fun signOut(): Flow<DataState<Unit>> = flow {
        emit(DataState.Loading)
        try {
            authRemoteDataSource.signOut()
            memberDao.deleteAllMembers()
            emit(DataState.Success(Unit))
        } catch (e: Exception) {
            emit(DataState.Error(e.message ?: "Sign-out failed"))
        }
    }
}