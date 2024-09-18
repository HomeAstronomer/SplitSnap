package com.example.aisplitwise

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aisplitwise.feature.dashboard.DashBoard
import com.example.aisplitwise.feature.dashboard.DashboardViewModel
import com.example.aisplitwise.feature.feature_create_group.CreateGroupScreen
import com.example.aisplitwise.feature.feature_ledger.LedgerScreen
import com.example.aisplitwise.feature.feature_login.LoginScreen
import com.example.aisplitwise.feature.feature_signup.SignUpScreen
import com.example.aisplitwise.feature.feature_splash.SplashScreen
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

@Composable
fun NavHostInitializer(navController: NavHostController) {
    NavHost(navController = navController, startDestination = SplashRoute) {
        composable<SplashRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            SplashScreen(hiltViewModel(),navController)
        }

        composable<DashBoardRoute> (
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            val dashBoardViewModel = hiltViewModel<DashboardViewModel>()
            DashBoard(dashBoardViewModel,navController)
        }


        composable<LoginScreenRoute> (
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            LoginScreen( loginViewModel = hiltViewModel(),
                navController)
        }

        composable<SignUpScreenRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            SignUpScreen( signUpViewModel = hiltViewModel(),
                navController = navController)
        }

        composable<CreateGroupRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            CreateGroupScreen(
                navController = navController)
        }

        composable<LedgerRoute>(
            enterTransition = { slideInOutTransition(slideDirection = SlideDirection.LeftToRight).first() },
            exitTransition = {slideInOutTransition(slideDirection = SlideDirection.LeftToRight).second() }
        ) {
            it.arguments
            LedgerScreen(
                navHostController = navController,
                ledgerViewModel = hiltViewModel())
        }

    }
}


fun slideInOutTransition(
    duration: Int = 1000,
    slideDirection: SlideDirection
): Pair<() -> EnterTransition, () -> ExitTransition> {
    return when (slideDirection) {
        SlideDirection.LeftToRight -> {
            {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(duration,easing= LinearEasing))
            } to {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(duration,easing= LinearEasing))
            }
        }
        SlideDirection.RightToLeft -> {
            {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(duration,easing= LinearEasing))
            } to {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(duration,easing= LinearEasing))
            }
        }
    }
}

enum class SlideDirection {
    LeftToRight,
    RightToLeft
}

