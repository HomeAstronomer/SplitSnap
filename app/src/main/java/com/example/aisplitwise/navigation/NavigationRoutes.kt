package com.example.aisplitwise.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute
@Serializable
object DashBoardRoute

@Serializable
object LoginScreenRoute

@Serializable
object SignUpScreenRoute

@Serializable
object CreateGroupRoute

@Serializable
data class LedgerRoute(val groupId:String="")

@Serializable
object ExpenseDialogRoute

@Serializable
data class AddMemberDialogRoute(val groupId:String="")

@Serializable
data class JoinGroupDialogRoute(val groupId:String="")