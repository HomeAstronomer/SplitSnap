package com.example.aisplitwise

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.aisplitwise.feature.dashboard.DashBoard
import com.example.aisplitwise.feature.dashboard.DashboardViewModel
import com.example.aisplitwise.feature.feature_login.LoginScreen
import com.example.aisplitwise.feature.feature_signup.SignUpScreen
import com.google.firebase.auth.FirebaseUser
import kotlinx.serialization.Serializable


@Serializable
data class DashBoardRoute(
    val uid:String="",
    val displayName: String?,
    val email: String?,
    val phoneNumber: String?,
    val photoUrl: String?
)

@Serializable
object LoginScreenRoute
@Serializable
object SignUpScreenRoute

@Composable
fun NavHostInitializer(navController: NavHostController, currentUser: FirebaseUser?) {
    var initScreen:Any=LoginScreenRoute
    if (currentUser != null) {
       initScreen=DashBoardRoute(currentUser.uid,currentUser.displayName,currentUser.email,currentUser.phoneNumber,currentUser.photoUrl?.path)
    }
    NavHost(navController = navController, startDestination = initScreen) {
        composable<DashBoardRoute> {
            val args = it.toRoute<DashBoardRoute>()
            val dashBoardViewModel = hiltViewModel<DashboardViewModel>()
            DashBoard(args,
                dashBoardViewModel)
        }


        composable<LoginScreenRoute> {
            LoginScreen( loginViewModel = hiltViewModel(),
                navController)
        }
        composable<SignUpScreenRoute> {
            SignUpScreen( signUpViewModel = hiltViewModel(),
                navController = navController)
        }

    }
}

