package com.local.data.repository.member

import com.local.data.local.Member
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MemberApiService {

    @GET("members/{uid}")
    suspend fun getMember(@Path("uid") uid: String): Member?

    @POST("members")
    suspend fun createMember(@Body member: Member)
}
