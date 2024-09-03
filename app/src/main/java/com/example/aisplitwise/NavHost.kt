package com.example.aisplitwise

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aisplitwise.feature.dashboard.DashBoard
import com.example.aisplitwise.feature.dashboard.DashboardViewModel
import com.example.aisplitwise.feature.feature_login.LoginScreen
import com.example.aisplitwise.feature.feature_signup.SignUpScreen
import com.example.aisplitwise.feature.feature_splash.SplashScreen
import com.google.firebase.auth.FirebaseUser
import kotlinx.serialization.Serializable


@Serializable
object SplashRoute
@Serializable
object DashBoardRoute

@Serializable
object LoginScreenRoute
@Serializable
object SignUpScreenRoute

@Composable
fun NavHostInitializer(navController: NavHostController) {
    NavHost(navController = navController, startDestination = SplashRoute) {
        composable<SplashRoute> {
            SplashScreen(hiltViewModel(),navController)
        }

        composable<DashBoardRoute> {
            val dashBoardViewModel = hiltViewModel<DashboardViewModel>()
            DashBoard(dashBoardViewModel)
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

